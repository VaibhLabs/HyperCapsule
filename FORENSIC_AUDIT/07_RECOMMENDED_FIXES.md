# 07_RECOMMENDED_FIXES.md — Forensic Remediation & Architecture Blueprint

**Audit Date:** 2026-11-11  
**Investigator:** Senior Android Reverse-Engineering Analyst & Systems Architect  
**Mode:** ARCHITECTURAL SPECIFICATION ONLY (NO CODE MODIFICATIONS APPLIED)  

---

## 1. Remediation Blueprint Matrix

| Issue Target | Target Files for Modification | Core Functions & State Flows | Remediation Strategy |
| :---: | :--- | :--- | :--- |
| **#450** | `InlineReplyService.kt` | `showOverlay()` | Restrict window bounds to IME/input height; add `FLAG_NOT_TOUCH_MODAL` outside boundaries; attach timeout dismissal. |
| **#445** | `NotificationReaderService.kt`, `PermanentIslandManager.kt` | `isNative` check, `desiredActive()`, `reconcile()` | Expand `isNative` to cover navigation & modern media categories; hide permanent island when `activeIslands.isNotEmpty()`. |
| **#444** | `XiaomiNotificationHelper.kt`, `PermanentIslandManager.kt` | `isSupportIsland()`, `desiredActive()` | Add tablet form-factor detection; do not suppress islands in landscape on large screens ($sw \ge 600dp$). |
| **#441** | `LiveUpdateTranslator.kt`, `NotificationReaderService.kt` | `translateToLiveUpdate()`, `createChannels()` | Honor `config.isShowShade` in Live Updates; configure channel importance dynamically. |
| **#410** | `MessageTranslator.kt`, `StandardTranslator.kt`, `DownloadTranslator.kt` | `setIslandConfig()` | Convert `timeout` and `floatTimeout` from seconds to milliseconds ($s \times 1000L$) before passing to kit. |
| **#405** | `ShizukuManager.kt`, `PrivilegedServiceImpl.kt` | `notify()`, `setPackageNetworkingEnabled()` | Eliminate global mutex serialization on Global ROMs; detect Android 16/17 eBPF before invoking reflection. |
| **#438** | `NotificationReaderService.kt` | `onNotificationPosted()`, `onNotificationRemoved()` | Eliminate 2,000ms blocklist check for call/voice notifications; preserve call islands across atomic reposts. |
| **#442** | `MessageTranslator.kt` | `translate()` | Implement 4-8 digit OTP regex extractor; display code prominently in right pill with one-tap copy action. |
| **LAT-02** | `BootReceiver.kt` | `onReceive()` | Remove `toggleNotificationListener` component disable hack; use `requestRebind()` exclusively on all Android 14+ builds. |

---

## 2. Detailed Technical Specifications per Issue

### Remediating Issue #450 (Touchscreen Freeze / WindowManager Block)
1. **Target File:** [`app/src/main/java/com/hypercapsule/app/service/InlineReplyService.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/InlineReplyService.kt)
2. **Logical Modification:**
   - Instead of allocating `MATCH_PARENT` $\times$ `MATCH_PARENT`, set window height to `WRAP_CONTENT` with `Gravity.BOTTOM`.
   - Add `WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL` so touches outside the reply card flow directly to the underlying foreground application.
   - Attach an automatic 30-second watchdog timer to `InlineReplyService` to self-dismiss if the user leaves the device idle.
3. **Regression Risks:** Virtual keyboard layout adjustments in landscape mode; ensure `imePadding()` does not clip on custom keyboards (Gboard vs SwiftKey).
4. **Verification Requirements:**
   - Unit test verifying overlay cleanup on back press.
   - Physical device test on Android 16/17 verifying bottom navigation bar gestures respond while reply card is visible.

---

### Remediating Issue #445 (Permanent Island Stacking & Reassertion)
1. **Target Files:**
   - [`app/src/main/java/com/hypercapsule/app/service/PermanentIslandManager.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/PermanentIslandManager.kt)
   - [`app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt)
2. **Logical Modification:**
   - In `PermanentIslandManager.kt`, update `desiredActive()`:
     ```kotlin
     // Logical rule:
     val hasBridgedIslands = currentRealNotifications > 0
     return isPermanentIslandEnabled && !hasNativeIsland && !hasBridgedIslands && !(isHideInLandscapeEnabled && isLandscape)
     ```
   - In `NotificationReaderService.kt`, expand native island detection to check:
     - `template?.contains("Media", ignoreCase = true) == true`
     - `sbn.notification.category == Notification.CATEGORY_TRANSPORT`
     - `sbn.notification.category == Notification.CATEGORY_NAVIGATION`
     - `sbn.packageName.contains("maps") || sbn.packageName.contains("waze")`
   - In `reconcile()`, do not re-dispatch ID 9999 when `refresh == true` if an active bridged island is currently exhibiting.
3. **Regression Risks:** A momentary gap (up to 700ms) could occur between bridged island dismissal and permanent island restoration. Handled by `scheduleDispatchLocked()`.
4. **Verification Requirements:**
   - Unit test simulating media notification arrival and asserting `desiredActive() == false`.
   - Physical device test playing Spotify and verifying Permanent Island remains hidden until music is paused/closed.

---

### Remediating Issue #441 (Duplicate Notification Posting)
1. **Target Files:**
   - [`app/src/main/java/com/hypercapsule/app/service/translators/LiveUpdateTranslator.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/translators/LiveUpdateTranslator.kt)
   - [`app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt)
2. **Logical Modification:**
   - In `LiveUpdateTranslator.kt`, if `config?.isShowShade == false`, configure the notification builder to suppress shade presentation where supported (e.g. `setGroupSummary(false)` or `setPriority(PRIORITY_MIN)`).
   - In `NotificationReaderService.kt`, provide user setting for `removeOriginalNotification` that is enabled by default for apps where bridged notification is preferred.
3. **Regression Risks:** If original notification is removed and bridged notification is hidden from shade, user cannot review past notification history in drawer.
4. **Verification Requirements:**
   - Unit test asserting `isShowShade` propagates to notification builder.
   - Physical device test on HyperOS 3 verifying notification drawer contains only one entry.

---

### Remediating Issue #410 (Notification Flash & Auto-Dismissal)
1. **Target Files:**
   - [`app/src/main/java/com/hypercapsule/app/service/translators/MessageTranslator.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/translators/MessageTranslator.kt)
   - [`app/src/main/java/com/hypercapsule/app/service/translators/StandardTranslator.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/translators/StandardTranslator.kt)
   - [`app/src/main/java/com/hypercapsule/app/service/translators/DownloadTranslator.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/translators/DownloadTranslator.kt)
2. **Logical Modification:**
   - Convert all second-based timeout integers to milliseconds before calling `builder.setIslandConfig()`:
     ```kotlin
     val timeoutMs = (config.timeout ?: 10) * 1000
     val floatTimeoutMs = (config.floatTimeout ?: 5) * 1000
     builder.setIslandConfig(timeout = timeoutMs, dismissible = true, expandedTimeMs = floatTimeoutMs)
     ```
3. **Regression Risks:** None. Matches `PermanentIslandManager` and Xiaomi SDK expectation.
4. **Verification Requirements:**
   - Unit test validating that translated JSON payload contains `"timeout": 10000` instead of `"timeout": 10`.

---

### Remediating Issue #405 & LAT-01 (Shizuku Delay & Mutex Starvation)
1. **Target Files:**
   - [`app/src/main/java/com/hypercapsule/app/util/ShizukuManager.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/util/ShizukuManager.kt)
   - [`app/src/main/java/com/hypercapsule/app/integration/shizuku/PrivilegedServiceImpl.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/integration/shizuku/PrivilegedServiceImpl.kt)
2. **Logical Modification:**
   - In `ShizukuManager.kt`, check `DeviceUtils.isCNRom`. If running on Global ROM (`isCNRom == false`), **completely bypass the XMSF network workaround**, posting immediately via `NotificationManagerCompat`.
   - In `PrivilegedServiceImpl.kt`, guard reflection calls with `Build.VERSION.SDK_INT < 35`. On API 35+, return false immediately without blocking `CountDownLatch` for 3,000ms.
3. **Regression Risks:** None on Global ROMs.
4. **Verification Requirements:**
   - Benchmark dispatch execution time on Global ROM ensuring $< 15\text{ ms}$ latency per notification.

---

### Remediating Issue #438 (Discord Mute/Unmute Drop)
1. **Target File:** [`app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt)
2. **Logical Modification:**
   - Modify line 863 in `onNotificationPosted`:
     ```kotlin
     // Only apply recentlyRemovedKeys debounce to transient STANDARD notifications,
     // NEVER to ongoing CALL, MEDIA, or NAVIGATION notifications!
     val isOngoingSpecial = type == NotificationType.CALL || type == NotificationType.MEDIA || type == NotificationType.NAVIGATION
     if (!isOngoingSpecial) {
         val removedTime = recentlyRemovedKeys[rawSbn.key]
         if (removedTime != null && System.currentTimeMillis() - removedTime < 2000) return
     }
     ```
3. **Regression Risks:** None; ongoing communications must always accept rapid atomic updates.
4. **Verification Requirements:**
   - Unit test simulating Discord mute/unmute within 50ms and verifying notification is accepted and re-translated.
