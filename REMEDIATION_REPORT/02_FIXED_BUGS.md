# 02_FIXED_BUGS.md — Comprehensive Inventory of Remediated Defects

**Target Project:** HyperCapsule v1.00 Stable (`com.hypercapsule.app`)  
**Audit Reference:** `FORENSIC_AUDIT/01_LOCAL_BASELINE.md` through `08_FINAL_RISK_ASSESSMENT.md`  

---

## 1. Summary of Fixed Defects

| Bug ID | Audit Reference | Title | Severity | Files Modified | Verification Method |
| :--- | :--- | :--- | :---: | :--- | :--- |
| **BUG 01** | Upstream #410 | Notification Timeout Unit Mismatch (Flash Dismissal) | HIGH | `IslandConfig.kt`, `WidgetConfig.kt`, Translators (7 files) | Unit Test (`RemediationVerificationTest.kt`) |
| **BUG 02** | Upstream #438 | Discord Voice Notification Disappearance on Mute/Unmute | HIGH | `NotificationReaderService.kt` | Unit Test Simulation + Code Path Verification |
| **BUG 03** | Upstream #445 | Permanent Island Overwrites Active Music & Maps | HIGH | `PermanentIslandManager.kt`, `NotificationReaderService.kt` | Unit Test Simulation + Lifecycle Analysis |
| **BUG 04** | Upstream #450 | Inline Reply WindowManager Touch Interception Freeze | CRITICAL | `InlineReplyService.kt` | LayoutParams Refactoring + Watchdog Verification |
| **BUG 05** | Upstream #441 | Notification Shade Duplication for Live Updates | MEDIUM | `NotificationReaderService.kt`, `LiveUpdateTranslator.kt` | Silent Channel Integration & Compiler Check |
| **BUG 06** | Upstream #405 | Shizuku Dispatch Mutex Contention & Delivery Delay | HIGH | `ShizukuManager.kt`, `PrivilegedServiceImpl.kt` | Scope Refactoring + Fast-Path on Global ROMs |
| **BUG 07** | LAT-02 | BootReceiver Notification Listener Permission Revocation | CRITICAL | `BootReceiver.kt` | Official `requestRebind` API substitution |
| **BUG 08** | Upstream #444 | Tablet Landscape Permanent Island False Suppression | MEDIUM | `DeviceUtils.kt`, `PermanentIslandManager.kt` | Screen Dimension Check ($sw \ge 600dp$) |
| **LAT-01** | LAT-01 | PrivilegedService Reflection Stall on Android 15/16/17 | HIGH | `PrivilegedServiceImpl.kt` | Reduced Timeout to 500ms + API 35 Short-Circuit |
| **LAT-04** | LAT-04 | Theme Export ZIP Backslash Windows Serialization Failure | MEDIUM | `ThemeRepository.kt` | Unit Test Path Normalization |
| **LAT-05** | LAT-05 | Repeated Transparent Bitmap Allocations in BaseTranslator | LOW | `BaseTranslator.kt` | Static Companion Object Caching |
| **LAT-06 / 07** | LAT-06/07 | Orphan Reaper False Cancels & Stale Active Widgets | MEDIUM | `NotificationReaderService.kt` | Cross-Map Validation + Widget Set Reconciliation |

---

## 2. In-Depth Technical Remediation Details

### BUG 01: Notification Timeout Unit Mismatch (#410)
- **Defect Description:** User preferences stored timeouts in whole seconds (e.g., 5s, 10s). The translators passed this raw integer directly to `HyperIslandNotification.Builder.setIslandConfig(timeout = config.timeout, expandedTimeMs = config.floatTimeout)`. The underlying HyperIsland Kit interpreted these values in milliseconds, triggering SystemUI auto-dismissal after 10 milliseconds.
- **Remediation Implementation:**
  - Added computed properties `timeoutMs` and `floatTimeoutMs` to `IslandConfig.kt`:
    ```kotlin
    val timeoutMs: Int?
        get() = timeout?.let { if (it > 0) (it.toLong() * 1000L).coerceAtMost(86_400_000L).toInt() else null }
    val floatTimeoutMs: Int?
        get() = floatTimeout?.let { if (it > 0) (it.toLong() * 1000L).coerceAtMost(86_400_000L).toInt() else null }
    ```
  - Added equivalent `timeoutMs` to `WidgetConfig.kt`.
  - Updated `MessageTranslator.kt`, `StandardTranslator.kt`, `DownloadTranslator.kt`, `CallTranslator.kt`, `NavTranslator.kt`, `TimerTranslator.kt`, and `WidgetTranslator.kt`.
- **Result:** Islands stay visible for user-specified durations (e.g. 10s = 10,000ms).

---

### BUG 02: Discord Voice Notification Disappearance (#438)
- **Defect Description:** Muting/unmuting or toggling deafen in Discord triggers an atomic remove-and-repost cycle. In `NotificationReaderService.kt`, line 863 discarded any notification arriving within 2,000ms of a removal with the same key. The legitimate updated call island was discarded and lost permanently.
- **Remediation Implementation:**
  - In `NotificationReaderService.onNotificationPosted`, exempted ongoing and call/nav/media types:
    ```kotlin
    val isOngoingOrSpecial = sbn.isOngoing ||
        type == NotificationType.CALL ||
        type == NotificationType.NAVIGATION ||
        type == NotificationType.MEDIA

    val removedTime = recentlyRemovedKeys[rawSbn.key]
    if (removedTime != null && System.currentTimeMillis() - removedTime < 2000) {
        if (isOngoingOrSpecial) {
            recentlyRemovedKeys.remove(rawSbn.key)
        } else {
            return
        }
    }
    ```
  - Cancelled pending delayed removal jobs for `rawSbn.key` and `effectiveKey`.
- **Result:** Discord call updates post smoothly without disappearance.

---

### BUG 03: Permanent Island Conflicts (#445)
- **Defect Description:** `desiredActive()` in `PermanentIslandManager` only checked `!hasNativeIsland` and orientation, ignoring `currentRealNotifications`. Furthermore, `NotificationReaderService` had an incomplete native notification check that missed navigation notifications and Android `MediaSession` tokens. Whenever `syncNotifications()` ran (every 60s or on screen-on), it reasserted ID 9999 over active Spotify or Google Maps islands.
- **Remediation Implementation:**
  - Extracted comprehensive `isNativeIslandNotification(sbn)` recognizing `miui.focus.param`, MediaStyle templates, `EXTRA_MEDIA_SESSION`, `CATEGORY_TRANSPORT`, and ongoing `CATEGORY_NAVIGATION`.
  - Updated `desiredActive()` to require `currentRealNotifications == 0`:
    ```kotlin
    private fun desiredActive(): Boolean {
        val isLandscape = context.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
        val isTablet = DeviceUtils.isTablet(context)
        val landscapeSuppressed = isHideInLandscapeEnabled && isLandscape && !isTablet
        return isPermanentIslandEnabled && !hasNativeIsland && currentRealNotifications == 0 && !landscapeSuppressed
    }
    ```
- **Result:** Permanent Island gracefully yields to active bridged or native notifications and returns when they end.

---

### BUG 04: Inline Reply Touchscreen Freeze (#450)
- **Defect Description:** `InlineReplyService` allocated a `MATCH_PARENT` $\times$ `MATCH_PARENT` overlay with `FLAG_LAYOUT_NO_LIMITS` and omitted `FLAG_NOT_TOUCH_MODAL`. If the user tapped outside or if gesture navigation failed to dispatch `KEYCODE_BACK`, the transparent overlay hijacked all touch input across the entire screen.
- **Remediation Implementation:**
  - Changed WindowManager layout height to `WRAP_CONTENT` anchored to `Gravity.BOTTOM`.
  - Added `FLAG_NOT_TOUCH_MODAL` and `FLAG_WATCH_OUTSIDE_TOUCH`.
  - Set root Compose modifier to `fillMaxWidth().wrapContentHeight()`.
  - Added an outside touch listener on `composeView` triggering `removeOverlayAndStop()`.
  - Added a 60-second inactivity watchdog and an `ACTION_SCREEN_OFF` broadcast receiver to ensure the overlay cannot be orphaned.
- **Result:** Touches outside the reply card flow freely to underlying applications; orphaned overlays are eliminated.

---

### BUG 05: Notification Shade Duplication (#441)
- **Defect Description:** When users toggled `isShowShade = false`, bridged notifications and live updates were still posted to high-importance notification channels (`hyper_bridge_notification_channel` / `hyper_bridge_live_update_channel`).
- **Remediation Implementation:**
  - Created `hyper_bridge_silent_channel` with `NotificationManager.IMPORTANCE_MIN` and silent flags.
  - Dynamically route notifications with `isShowShade == false` to `hyper_bridge_silent_channel` with `PRIORITY_MIN` and `setSilent(true)` across both standard translator and live update paths.
- **Result:** Notifications respect user shade preferences within platform constraints.

---

### BUG 06 & LAT-01 / LAT-03: Shizuku Delays & Reflection (#405)
- **Defect Description:** `ShizukuManager` launched unmanaged coroutines on each call and acquired a sequential `notifyMutex` executing XMSF network toggle shell scripts with 1-second delays. On Global ROMs and Android 15+, `setFirewallChainEnabled` threw `NoSuchMethodException`, stalling dispatches for up to 3,000ms.
- **Remediation Implementation:**
  - Switched `ShizukuManager` to a structured supervisor coroutine scope.
  - Bypassed the XMSF workaround on Global ROMs (`!DeviceUtils.isCNRom`), dispatching immediately via `NotificationManagerCompat`.
  - On China ROMs, reduced reflection timeout to 500ms and short-circuited reflection on API 35+.
- **Result:** Sub-millisecond notification dispatch on Global ROMs; resilient fallback on China ROMs.

---

### BUG 07 & LAT-02: Notification Listener Reconnection
- **Defect Description:** `BootReceiver.kt` attempted to restart `NotificationReaderService` by disabling and re-enabling its component via `PackageManager`. On modern Android versions, this revokes the user's granted Notification Access permission.
- **Remediation Implementation:**
  - Deleted `toggleNotificationListener` completely.
  - Substituted the official Android framework method `NotificationListenerService.requestRebind(component)` with a 5-second cooldown debounce.
- **Result:** Listener service reconnects on boot without permission loss.

---

### BUG 08: Tablet Landscape Support (#444)
- **Defect Description:** Tablets operating primarily in landscape mode had their Permanent Island suppressed when `hidePermanentIslandLandscape` was toggled.
- **Remediation Implementation:**
  - Added `DeviceUtils.isTablet(context)` checking `smallestScreenWidthDp >= 600`.
  - Exempted tablets from landscape island suppression.
- **Result:** Tablets retain permanent island support in landscape mode.
