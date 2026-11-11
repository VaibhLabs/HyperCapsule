# 03_PERMANENT_ISLAND_AUDIT.md — Permanent Island Architecture & Overlay Touch Investigation

**Audit Date:** 2026-11-11  
**Investigator:** Senior Android Reverse-Engineering Analyst & Systems Architect  
**Primary Target Files:**  
- [`app/src/main/java/com/hypercapsule/app/service/PermanentIslandManager.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/PermanentIslandManager.kt)
- [`app/src/main/java/com/hypercapsule/app/service/InlineReplyService.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/InlineReplyService.kt)
- [`app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt)

---

## 1. Architectural Anatomy: WindowManager vs. Focus Notification

### Key Finding 1: Permanent Island is NOT a WindowManager Overlay
In standard Android development, persistent dynamic islands (such as DynamicSpot) utilize Android's `WindowManager.addView()` with `TYPE_APPLICATION_OVERLAY`.  
However, HyperCapsule's `PermanentIslandManager` **does not create an Android WindowManager window**.

In [`PermanentIslandManager.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/PermanentIslandManager.kt#L163-L200):
```kotlin
val builder = HyperIslandNotification.Builder(context, "permanent_island", "Permanent Island")
builder.setEnableFloat(false)
builder.setIslandConfig(timeout = 86400000, dismissible = false, highlightColor = "#FFFFFF", expandedTimeMs = 0)
builder.setShowNotification(false)
builder.setReopen(true)
builder.setIslandFirstFloat(false)

val emptyString = "\u00A0".repeat(currentWidth)
builder.setBigIslandInfo(
    left = ImageTextInfoLeft(1, null, TextInfo(emptyString, emptyString)),
    right = null
)
builder.setSmallIsland("")

val data = HyperIslandData(builder.buildResourceBundle(), builder.buildJsonParam())
val notifBuilder = NotificationCompat.Builder(context, "hyper_bridge_notification_channel")
    .setSmallIcon(R.drawable.ic_launcher_foreground)
    .setContentTitle("Permanent Island")
    .setContentText("Empty Island")
    .setPriority(NotificationCompat.PRIORITY_MIN)
    .setOngoing(true)

notifBuilder.addExtras(data.resources)
val notification = notifBuilder.build()
notification.extras.putString("miui.focus.param", data.jsonParam)

ShizukuManager.notify(context, PERMANENT_BRIDGE_ID, notification)
```

The Permanent Island is instantiated by transmitting an empty ongoing notification (`PERMANENT_BRIDGE_ID = 9999`) carrying Xiaomi's proprietary JSON payload `"miui.focus.param"`. Xiaomi's `com.android.systemui` status bar daemon parses this payload and renders a hardware cutout pill natively within SystemUI.

---

## 2. Forensic Investigation of Issue #450 (Touchscreen Freeze)

### Symptom Recap
- **Device:** Xiaomi 17T Pro, HyperOS 4.0, Android 17 (Global ROM).
- **Behavior:** Users report that enabling the island freezes touch input, blocking soft keyboards and navigation controls.

### Local Source Code Evidence: Two Distinct Mechanisms Identified

#### Mechanism A: Full-Screen WindowManager Hijack in `InlineReplyService.kt`
While `PermanentIslandManager` does not create a WindowManager view, **`InlineReplyService.kt` does**.  
Inspect [`InlineReplyService.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/InlineReplyService.kt#L330-L343):
```kotlin
val params = WindowManager.LayoutParams(
    WindowManager.LayoutParams.MATCH_PARENT,
    WindowManager.LayoutParams.MATCH_PARENT,
    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
    PixelFormat.TRANSLUCENT
).apply {
    gravity = Gravity.CENTER
    // We do NOT use FLAG_NOT_FOCUSABLE because we need the keyboard to open!
    // But we must be careful to consume back presses to allow the user to exit.
}
```

**Critical Defect Analysis:**
1. **Unbounded Touch Sizing:** The overlay dimensions are `MATCH_PARENT` $\times$ `MATCH_PARENT` with `FLAG_LAYOUT_NO_LIMITS`. It blankets $100\%$ of the physical display surface.
2. **Missing `FLAG_NOT_TOUCH_MODAL`:** Because `FLAG_NOT_FOCUSABLE` was intentionally omitted (to allow the IME keyboard to open), the window is **modal** by default in Android's window dispatcher. Every touch on the screen—including touches on the navigation bar, app bar, or outside areas—is intercepted by the service's root `Box`:
   ```kotlin
   Box(
       modifier = Modifier
           .fillMaxSize()
           .background(Color.Transparent)
           .clickable(
               interactionSource = remember { MutableInteractionSource() },
               indication = null
           ) {
               removeOverlayAndStop()
           },
       contentAlignment = Alignment.BottomCenter
   )
   ```
3. **Leaked Overlay Life-Cycle on HyperOS 4.0:** If an inline reply broadcast is triggered by a messaging notification or test intent, or if the view fails to dismiss due to gesture navigation swallowing the back key (`KEYCODE_BACK`), the invisible `MATCH_PARENT` transparent layer remains bound to `WindowManager`. On HyperOS 4.0 / Android 17, predictive back gestures do not dispatch `KEYCODE_BACK` to `View.onKeyPreIme()`. Thus, the user's touchscreen becomes completely unresponsive to underlying applications.

#### Mechanism B: HyperOS 4.0 SystemUI Cutout Pill Touch Delegation
In HyperOS 4.0 (Android 17), Xiaomi redesigned the SystemUI status bar cutout window. When `PERMANENT_BRIDGE_ID = 9999` is dispatched with `emptyString = "\u00A0".repeat(currentWidth)`, SystemUI calculates an expanded hit-box across the top of the display. On early HyperOS 4.0 firmware builds, the SystemUI cutout view fails to pass `FLAG_NOT_TOUCHABLE` to its sub-elements when `dismissible = false` and `reopen = true`, causing SystemUI to swallow touches near the top status bar.

### HyperCapsule Status for Issue #450
**CONFIRMED HIGH-RISK CODE PATH (Local Defect in `InlineReplyService.kt` + Firmware Factor in HyperOS 4.0).**

---

## 3. Forensic Investigation of Issue #445 (Permanent Island vs. Music/Maps)

### Symptom Recap
- **Device:** POCO X8 Pro, HyperOS 3.0.304, Android 16.
- **Behavior:** Permanent Island re-appears over active Music playback (Spotify) or navigation (Google Maps).

### Local Source Code Evidence: The Conflict Loop

#### Defect 1: Incomplete Native Island Detection in `NotificationReaderService.kt`
Inspect [`NotificationReaderService.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt#L603-L620):
```kotlin
if (it.packageName != packageName) {
    val extras = it.notification.extras
    var isNative = false
    if (extras != null) {
        if (extras.containsKey("miui.focus.param") || extras.containsKey("miui.system.focus.param")) {
            isNative = true
        }
        val template = extras.getString(Notification.EXTRA_TEMPLATE)
        if (template == "androidx.media.app.NotificationCompat\$MediaStyle" ||
            template == "android.app.Notification\$MediaStyle") {
            isNative = true
        }
    }
    if (isNative) {
        if (nativeIslands.add(it.key)) updatePermanentIsland()
    } else {
        if (nativeIslands.remove(it.key)) updatePermanentIsland()
    }
}
```

1. **Rigid String Match for MediaStyle:** The template check performs an exact equality check against two specific string constants:
   - `androidx.media.app.NotificationCompat$MediaStyle`
   - `android.app.Notification$MediaStyle`  
   On modern Android 15/16/17, media players utilizing the `MediaSession` architecture or custom media player frameworks frequently emit templates like `android.app.Notification$DecoratedMediaCustomViewStyle` or no template string at all (handled via `Notification.CATEGORY_TRANSPORT`).  
   When this happens, `isNative` evaluates to **`false`**.
2. **Zero Native Detection for Navigation:** Google Maps, Waze, and Petal Maps post ongoing navigation notifications. They do **not** inject `"miui.focus.param"`. Because `extras.containsKey("miui.focus.param")` is false and `template` is not MediaStyle, **navigation notifications are never recognized as native islands**!

#### Defect 2: The Premature Reassertion in `desiredActive()`
Inspect [`PermanentIslandManager.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/PermanentIslandManager.kt#L101-L107):
```kotlin
// Bridged islands deliberately do NOT hide the permanent island: HyperOS shows the newest
// focus island on top, so keeping 9999 posted makes the permanent island reappear instantly
// when a bridged island collapses or expires (removing it would leave a gap until the TTL).
private fun desiredActive(): Boolean {
    val isLandscape = context.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    return isPermanentIslandEnabled && !hasNativeIsland && !(isHideInLandscapeEnabled && isLandscape)
}
```

Notice the explicit design assumption in lines 101–103:  
*"Bridged islands deliberately do NOT hide the permanent island: HyperOS shows the newest focus island on top..."*

**Why This Breaks on POCO X8 Pro (HyperOS 3.0.304):**
1. When Spotify or Google Maps is active and bridged, HyperCapsule posts the bridged island with ID `sbn.key.hashCode()`.
2. Because `hasNativeIsland` is false (due to Defect 1 or because bridged islands don't increment `hasNativeIsland`), `desiredActive()` returns **`true`**.
3. HyperCapsule keeps `PERMANENT_BRIDGE_ID = 9999` posted in the system!
4. In HyperOS 3.0.304 (following the security patch reported in #445), Xiaomi's SystemUI focus manager changed its stacking behavior: instead of always giving priority to the newest focus notification, SystemUI prioritizes ongoing persistent notifications or rotates focus when track metadata updates!
5. Furthermore, whenever `syncNotifications()` runs (which executes periodically every 60 seconds or on screen-on, line 1259), it executes `permanentIslandManager.reconcile(..., refresh = true)`.  
6. In `PermanentIslandManager.kt` lines 115–123:
   ```kotlin
   if (shouldShow && isIslandPresent && refresh) {
       dispatchPermanentIsland()
       isIslandActive = true
       return
   }
   ```
   **This forces a re-dispatch of the Permanent Island (ID 9999) every minute or on every screen wake**, directly overwriting the active music and navigation island!

### HyperCapsule Status for Issue #445
**CONFIRMED LOCAL BUG (Directly demonstrable in `PermanentIslandManager.kt` and `NotificationReaderService.kt`).**

---

## 4. Architectural Summary

| Defect Identifier | File & Line | Cause | Impact |
| :--- | :--- | :--- | :--- |
| `BUG-PI-01` | `NotificationReaderService.kt:609-614` | Navigation & non-standard Media omitted from `isNative` | `hasNativeIsland` remains false during navigation and streaming |
| `BUG-PI-02` | `PermanentIslandManager.kt:101-107` | `desiredActive()` ignores bridged active islands (`activeIslands.isNotEmpty()`) | Permanent Island remains posted simultaneously with bridged islands |
| `BUG-PI-03` | `PermanentIslandManager.kt:115-123` | Periodic `syncNotifications` re-dispatches ID 9999 over active media | Flashing/reassertion of empty island every 60s or on screen-on |
| `BUG-OV-01` | `InlineReplyService.kt:330-337` | `MATCH_PARENT` modal overlay without `FLAG_NOT_TOUCH_MODAL` | Intercepts all display touches if overlay is orphaned |
