# 05_DEVICE_COMPATIBILITY_AUDIT.md — Device, Tablet & HyperOS Firmware Compatibility Audit

**Audit Date:** 2026-11-11  
**Investigator:** Senior Android Reverse-Engineering Analyst & Systems Architect  
**Primary Target Files:**  
- [`app/src/main/java/com/hypercapsule/app/util/XiaomiNotificationHelper.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/util/XiaomiNotificationHelper.kt)
- [`app/src/main/java/com/hypercapsule/app/util/DeviceUtils.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/util/DeviceUtils.kt)
- [`app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt)
- [`app/src/main/java/com/hypercapsule/app/service/PermanentIslandManager.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/PermanentIslandManager.kt)

---

## 1. Upstream Device Compatibility Matrix

| Device Model | Brand / Series | SoC / Architecture | Operating System | Reported Issue | Compatibility Verdict |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **Xiaomi 17T Pro** | Flagship (Phone) | Snapdragon 8 Gen 5 (arm64-v8a) | HyperOS 4.0 / Android 17 | #450 | **Compatible with Touch Bug Risk** |
| **Xiaomi 17 Ultra** | Ultra-Flagship | Snapdragon 8 Gen 5 (arm64-v8a) | HyperOS 4.0 / Android 17 | #405 | **Compatible with Mutex Delay Risk** |
| **POCO X8 Pro** | Performance (Phone) | Dimensity 9400 (arm64-v8a) | HyperOS 3.0.304 / Android 16 | #445 | **Compatible with Island Stacking Bug** |
| **POCO X7 Pro** | Performance (Phone) | Dimensity 8400 (arm64-v8a) | HyperOS 3.0.301 / Android 16 | #441 | **Compatible with Notification Drawer Duplicate Bug** |
| **POCO X6 Pro** | Mid-Range (Phone) | Dimensity 8300-Ultra | HyperOS 3.0 / Android 16 | #410 | **Compatible with Timeout Flash Bug** |
| **Xiaomi Pad 6S Pro** | Tablet (12.4" Display) | Snapdragon 8 Gen 2 | HyperOS 3.0.303 / Android 16 | #444 | **INCOMPATIBLE / FIRMWARE RESTRICTED** |
| **Redmi Note Series** | Budget / Mid-Range | Various (Dimensity / Snapdragon) | HyperOS 1.0 - 3.0 | — | **Fully Supported** |

---

## 2. In-Depth Tablet Compatibility Audit (Issue #444)

### Reported Behavior
On **Xiaomi Pad 6S Pro** running HyperOS 3.0.303 (Android 16), system islands (such as battery charging and stylus docking) appear as status bar pills, but third-party notifications translated by HyperBridge / HyperCapsule never appear.

### Forensic Investigation: Firmware vs. Application Layer

#### 1. System Feature Flag Verification
Inspect [`XiaomiNotificationHelper.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/util/XiaomiNotificationHelper.kt#L15-L17):
```kotlin
fun isSupportIsland(): Boolean {
    return getSystemPropertyBoolean("persist.sys.feature.island", false)
}
```
On Xiaomi Tablets running HyperOS for Pad (including Xiaomi Pad 6S Pro, Pad 6 Pro, and Pad 7 Pro), Xiaomi executes a customized SystemUI package: `com.android.systemui.pad`.
- In phone firmware, `persist.sys.feature.island` is hardcoded to `true` on AMOLED display builds with punch-hole cutouts.
- In tablet firmware, `persist.sys.feature.island` is often **set to `false`** or undefined by default because the tablet front camera is positioned horizontally on the long bezel rather than centered in the portrait status bar!

#### 2. The ContentProvider Permission Check
Inspect [`XiaomiNotificationHelper.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/util/XiaomiNotificationHelper.kt#L44-L59):
```kotlin
fun hasFocusPermission(context: Context): Boolean {
    var canShowFocus = false
    try {
        val uri = "content://miui.statusbar.notification.public".toUri()
        val extras = Bundle()
        extras.putString("package", context.packageName)
        val bundle = context.contentResolver.call(uri, "canShowFocus", null, extras)
        if (bundle != null) {
            canShowFocus = bundle.getBoolean("canShowFocus", false)
        }
    } catch (_: Exception) {
        canShowFocus = false
    }
    return canShowFocus
}
```
**The Firmware-Level Security Gate:**
- The ContentProvider `content://miui.statusbar.notification.public` is hosted by Xiaomi's `com.miui.notification` daemon.
- When `canShowFocus` is called for a package, the daemon inspects Xiaomi's internal white-list database: `/data/system/users/0/notification_focus_whitelist.xml`.
- On **HyperOS Pad ROMs**, Xiaomi restricts third-party app focus notifications: `canShowFocus` returns `false` for every non-system package unless explicitly modified via root/ADB!
- Furthermore, inspect `NotificationReaderService.kt` lines 791–808 and 1045–1060:
  When `hasPermission` is `false`, HyperCapsule builds a troubleshooting notification rather than posting the island directly!

#### 3. Display Cutout & Landscape Geometry Discrepancies
- In [`PermanentIslandManager.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/PermanentIslandManager.kt#L105-L106):
  ```kotlin
  val isLandscape = context.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
  return isPermanentIslandEnabled && !hasNativeIsland && !(isHideInLandscapeEnabled && isLandscape)
  ```
  Tablets operate primarily in **Landscape orientation** (e.g. keyboard dock attached). If the user or default setting has `hidePermanentIslandLandscape = true`, the permanent island is permanently suppressed on tablets!
- When Xiaomi Pad SystemUI receives `miui.focus.param` with an empty string, the cutout coordinate offset mapping in `com.android.systemui.pad` fails to map the pill to the camera cutout because the physical cutout is located at `X: Center, Y: Top` of the landscape frame, whereas the phone coordinate system assumes portrait dimensions.

### HyperCapsule Status for Issue #444
**UPSTREAM / FIRMWARE-DEPENDENT RESTRICTION.**  
The inability of third-party notifications to display on Xiaomi Pad 6S Pro is predominantly enforced by Xiaomi's HyperOS Pad firmware security policies and display cutout geometry in `com.android.systemui.pad`. However, HyperCapsule contains a contributing application flaw: failing to detect tablet form factors and suppressing islands when `isLandscape` is active.

---

## 3. HyperOS 1.0 / 2.0 / 3.0 / 4.0 Protocol Evolution

| Protocol Version | HyperOS Base | Android API | Key Characteristics | HyperCapsule Handling |
| :---: | :---: | :---: | :--- | :--- |
| **Protocol 1** | HyperOS 1.0 / MIUI 14 | Android 14 (API 34) | Initial focus notifications (`miui.focus.param` V1 JSON) | Handled via `HyperIslandNotification` |
| **Protocol 2** | HyperOS 2.0 | Android 15 (API 35) | Added rich pill animations, countdown counters | Handled via `HyperIslandNotification` |
| **Protocol 3** | HyperOS 3.0 | Android 16 (API 36) | Introduced native Android 16 Live Updates + Focus | Dual engine (Live Updates vs Kit) |
| **Protocol 4** | HyperOS 4.0 | Android 17 (API 37) | Redesigned SystemUI cutout stack, stricter IPC permissions | Susceptible to `ShizukuManager` delay and touch modal lock |

---

## 4. Chinese vs. Global ROM Divergence

### The XMSF Cloud Push Interception
In Chinese HyperOS ROMs (`isCNRom == true`):
- Xiaomi's push framework (`com.xiaomi.xmsf`) monitors incoming notifications and enforces a cloud-synced focus notification whitelist.
- If a package is not whitelisted by Xiaomi, `xmsf` intercepts and strips `"miui.focus.param"`.
- HyperBridge introduced a workaround in `XmsfNetworkHelper.kt` to disable networking on `com.xiaomi.xmsf` during dispatch.
- **The Defect:** In `ShizukuManager.kt`, this workaround can be enabled on **Global ROMs** where `xmsf` never intercepts local notifications. Enabling this workaround on Global ROMs unnecessarily penalizes every notification with sequential root-shell lock delays (Issue #405).
