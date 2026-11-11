# 04_FILES_MODIFIED.md — Complete Record of Source Code Modifications

**Application:** HyperCapsule v1.00 Stable  
**Application ID:** `com.hypercapsule.app`  
**Git Checkpoint Commit:** `15afda8`  

---

## 1. Inventory of Modified Files

A total of 19 existing source files were modified, and 1 new comprehensive verification test suite was added:

### Models & Configurations (2 Files)
1. `app/src/main/java/com/hypercapsule/app/models/IslandConfig.kt`
   - Added `timeoutMs` and `floatTimeoutMs` computed properties. Converts seconds to milliseconds with `Long` overflow protection and an 86,400,000ms (24-hour) upper bound.
2. `app/src/main/java/com/hypercapsule/app/models/WidgetConfig.kt`
   - Added `timeoutMs` computed property with safe `Long` multiplication and boundary checks.

### Notification Translators (8 Files)
3. `app/src/main/java/com/hypercapsule/app/service/translators/MessageTranslator.kt`
   - Updated `builder.setIslandConfig` to pass `timeout = config.timeoutMs` and `expandedTimeMs = config.floatTimeoutMs`.
4. `app/src/main/java/com/hypercapsule/app/service/translators/StandardTranslator.kt`
   - Updated `builder.setIslandConfig` with `timeoutMs` and `floatTimeoutMs`.
5. `app/src/main/java/com/hypercapsule/app/service/translators/DownloadTranslator.kt`
   - Updated completed and active download configurations with `timeoutMs` and `floatTimeoutMs`.
6. `app/src/main/java/com/hypercapsule/app/service/translators/CallTranslator.kt`
   - Updated incoming/active call expanded time to `config.floatTimeoutMs`.
7. `app/src/main/java/com/hypercapsule/app/service/translators/NavTranslator.kt`
   - Updated navigation expanded time to `config.floatTimeoutMs`.
8. `app/src/main/java/com/hypercapsule/app/service/translators/TimerTranslator.kt`
   - Updated timer island configuration with `timeoutMs` and `floatTimeoutMs`.
9. `app/src/main/java/com/hypercapsule/app/service/translators/WidgetTranslator.kt`
   - Updated widget island timeout with `config.timeoutMs`.
10. `app/src/main/java/com/hypercapsule/app/service/translators/LiveUpdateTranslator.kt`
    - Injected shade visibility check: when `config?.isShowShade == false`, sets `builder.setPriority(NotificationCompat.PRIORITY_MIN)` and `builder.setSilent(true)`.

### Core Services & Background Engines (4 Files)
11. `app/src/main/java/com/hypercapsule/app/service/translators/BaseTranslator.kt`
    - Cached immutable transparent (96x96 ARGB_8888) and fallback (1x1) bitmaps in a companion object to eliminate memory allocations during notification updates.
12. `app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt`
    - Created `hyper_bridge_silent_channel` (`IMPORTANCE_MIN`).
    - Extracted unified `isNativeIslandNotification(sbn)` recognizing HyperOS focus params, MediaStyle templates, `EXTRA_MEDIA_SESSION`, `CATEGORY_TRANSPORT`, and ongoing `CATEGORY_NAVIGATION`.
    - Exempted ongoing and special notifications from the 2,000ms removal debounce.
    - Cancelled pending delayed removal jobs upon notification repost.
    - Added `isShowShade` support in `postStandardNotification`.
    - Hardened orphan notification reaper against false-positive cancellations.
    - Reconciled `activeWidgets` with active status bar notifications during sync.
13. `app/src/main/java/com/hypercapsule/app/service/PermanentIslandManager.kt`
    - Updated `desiredActive()` to require `currentRealNotifications == 0`, preventing Permanent Island from conflicting with active bridged notifications.
    - Exempted tablets from landscape island suppression.
14. `app/src/main/java/com/hypercapsule/app/service/InlineReplyService.kt`
    - Resized WindowManager window to `WRAP_CONTENT` anchored to `Gravity.BOTTOM`.
    - Added `FLAG_NOT_TOUCH_MODAL` and `FLAG_WATCH_OUTSIDE_TOUCH`.
    - Configured Compose root container to `fillMaxWidth().wrapContentHeight()`.
    - Added `MotionEvent.ACTION_OUTSIDE` listener.
    - Added 60-second inactivity watchdog and `ACTION_SCREEN_OFF` broadcast receiver to prevent orphaned overlays.

### System Utilities & Integrations (4 Files)
15. `app/src/main/java/com/hypercapsule/app/util/DeviceUtils.kt`
    - Added `isTablet(context: Context): Boolean` checking `smallestScreenWidthDp >= 600`.
16. `app/src/main/java/com/hypercapsule/app/util/ShizukuManager.kt`
    - Replaced ad-hoc `CoroutineScope(Dispatchers.IO)` calls with a structured `SupervisorJob() + Dispatchers.IO` scope.
    - Bypassed the XMSF network workaround on Global ROMs (`!DeviceUtils.isCNRom`).
17. `app/src/main/java/com/hypercapsule/app/integration/shizuku/PrivilegedServiceImpl.kt`
    - Reduced `OP_TIMEOUT_MS` from 3000ms to 500ms.
    - Added guard against reflection failure on API 35+ (Android 15+).
18. `app/src/main/java/com/hypercapsule/app/receiver/BootReceiver.kt`
    - Eliminated package manager component disable/enable toggle.
    - Implemented official `NotificationListenerService.requestRebind(component)` with cooldown.
19. `app/src/main/java/com/hypercapsule/app/data/theme/ThemeRepository.kt`
    - Normalized exported ZIP entry paths using `.replace('\\', '/')`.

### Test Suites (1 New File)
20. `app/src/test/java/com/hypercapsule/app/RemediationVerificationTest.kt`
    - Unit tests covering timeout conversion, Discord rapid repost simulation, Permanent Island desired active logic, and ZIP path normalization.
