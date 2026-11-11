# 02_UPSTREAM_BUG_REFERENCE.md — Upstream Bug Intelligence Reference Database

**Audit Date:** 2026-11-11  
**Target Repository Lineage:** Upstream HyperBridge Public Bug Tracker  
**Audit Scope:** Public issue reports mapped to local HyperCapsule v1.00 Stable source tree  

---

## 1. Issue Overview Matrix

| Issue ID | Primary Symptom | Reported Device & OS | Upstream App Version | Local Investigation Target |
| :---: | :--- | :--- | :---: | :--- |
| **#450** | Permanent Island / Overlay causes touchscreen unresponsiveness | Xiaomi 17T Pro (HyperOS 4.0, Android 17) | v0.6.0-rc1 | `PermanentIslandManager.kt`, `InlineReplyService.kt` |
| **#445** | Permanent Island incorrectly reappears during Music / Maps | POCO X8 Pro (HyperOS 3.0.304, Android 16) | v0.6.0-beta5 | `PermanentIslandManager.kt`, `NotificationReaderService.kt` |
| **#444** | Third-party app notifications do not appear on tablet | Xiaomi Pad 6S Pro (HyperOS 3.0.303, Android 16) | v0.6.0-beta5 | `XiaomiNotificationHelper.kt`, `DeviceUtils.kt`, `LiveUpdateTranslator.kt` |
| **#441** | Duplicate notifications in panel despite disable setting | POCO X7 Pro (HyperOS 3.0.301, Android 16) | v0.6.0-beta5 | `NotificationReaderService.kt`, `IslandConfig.kt`, Translators |
| **#410** | Notifications appear and immediately auto-dismiss | POCO X6 Pro (HyperOS 3.0, Android 16) | v0.5.7 / v0.6.0 | `NotificationReaderService.kt`, Translators (`setIslandConfig`) |
| **#405** | Random notification delivery delays with unrestricted battery | Xiaomi 17 Ultra (HyperOS 4.0, Android 17) | v0.6.0-beta4 | `ShizukuManager.kt`, `XmsfNetworkHelper.kt`, Coroutine Dispatchers |
| **#438** | Discord voice chat island disappears after mute/unmute | Any device running Discord | v0.6.0-beta5 | `NotificationReaderService.kt` (`recentlyRemovedKeys`, `forceDismiss`) |
| **#442** | Google Messages SMS code does not appear on right side of island | Android device receiving SMS OTP | v0.6.0-beta5 | `MessageTranslator.kt` (Feature Request / Enhancement) |

---

## 2. Detailed Upstream Target Specifications

### Target 1: Issue #450 — Touchscreen Freeze / Unresponsive Overlay Controls
- **Upstream Reference:** [Issue #450](https://github.com/D4vidDf/HyperBridge/issues/450)
- **Environment Reported:** Xiaomi 17T Pro, HyperOS 4.0, Android 17, Global ROM.
- **Reported Defect:** Enabling Permanent Island or receiving notifications causes soft keyboard touches, bottom navigation gestures, and app controls to become completely unresponsive. The user must force-close or lock/unlock the screen to regain touch input.
- **Hypothesis:** WindowManager `TYPE_APPLICATION_OVERLAY` flag misconfiguration (`MATCH_PARENT` sizing without `FLAG_NOT_TOUCH_MODAL` or `FLAG_NOT_FOCUSABLE`), or HyperOS 4.0 SystemUI focus cutout window consuming global touch bounds.

### Target 2: Issue #445 — Permanent Island Reassertion During Active Media / Maps
- **Upstream Reference:** [Issue #445](https://github.com/D4vidDf/HyperBridge/issues/445)
- **Environment Reported:** POCO X8 Pro, HyperOS 3.0.304, Android 16.
- **Reported Defect:** The user enables Permanent Island. While playing music (Spotify) or running navigation (Google Maps), the system island or bridged media island displays, but after a short duration (or security patch update), the black empty Permanent Island re-asserts itself on top of the media island, hiding playback controls and track progress.
- **Hypothesis:** State desynchronization between `nativeIslands` tracking and `desiredActive()` evaluation, lack of navigation awareness in native detector, or periodic `syncNotifications` reasserting notification ID 9999.

### Target 3: Issue #444 — Tablet Notification Invisibility
- **Upstream Reference:** [Issue #444](https://github.com/D4vidDf/HyperBridge/issues/444)
- **Environment Reported:** Xiaomi Pad 6S Pro, HyperOS 3.0.303, Android 16.
- **Reported Defect:** Native HyperOS islands (e.g., charging, stylus connection) render correctly on the tablet status bar, but notifications from third-party applications (WhatsApp, Telegram, etc.) never appear as dynamic islands.
- **Hypothesis:** Firmware-level `content://miui.statusbar.notification.public` permission gating (`canShowFocus` returns false for non-system apps on Pad builds), missing horizontal cutout coordinates in tablet landscape mode, or `persist.sys.feature.island` returning false on tablets.

### Target 4: Issue #441 — Duplicate Notification Posting
- **Upstream Reference:** [Issue #441](https://github.com/D4vidDf/HyperBridge/issues/441)
- **Environment Reported:** POCO X7 Pro, HyperOS 3.0.301, Android 16.
- **Reported Defect:** Users disable "Show in Notification Shade" (`isShowShade = false`). However, both the original application notification and HyperCapsule's bridged notification appear in the notification drawer simultaneously.
- **Hypothesis:** Android 16 `LiveUpdateTranslator` ignores `isShowShade`, and `NOTIFICATION_CHANNEL_ID` is defined as `IMPORTANCE_HIGH`, forcing SystemUI to display a visible shade item regardless of `miui.focus.param`.

### Target 5: Issue #410 — Notification Flash and Auto-Dismissal
- **Upstream Reference:** [Issue #410](https://github.com/D4vidDf/HyperBridge/issues/410)
- **Environment Reported:** POCO X6 Pro, HyperOS 3.0, Android 16.
- **Reported Defect:** Notifications pop up in the Dynamic Island for a fraction of a second and immediately vanish without user interaction.
- **Hypothesis:** Unit mismatch in `setIslandConfig(timeout = config.timeout)` where `timeout` is passed as raw seconds (`10`) into an API expecting milliseconds (`10,000`), or `REASON_APP_CANCEL` from summary updates triggering immediate `cancel(hyperId)`.

### Target 6: Issue #405 — Random Notification Processing Latency
- **Upstream Reference:** [Issue #405](https://github.com/D4vidDf/HyperBridge/issues/405)
- **Environment Reported:** Xiaomi 17 Ultra, HyperOS 4.0, Android 17.
- **Reported Defect:** Notifications are delayed by 2 to 10 seconds before appearing on the Dynamic Island, despite having all battery optimizations set to unrestricted.
- **Hypothesis:** Artificial serialization in `ShizukuManager` using `notifyMutex.withLock` combined with `XmsfNetworkHelper.setXmsfNetworkingEnabled` 1-second delay loops.

### Target 7: Issue #438 — Discord Voice Chat Drop on Mute/Unmute
- **Upstream Reference:** [Issue #438](https://github.com/D4vidDf/HyperBridge/issues/438)
- **Environment Reported:** Discord voice chat on any Android 15/16/17 device.
- **Reported Defect:** When active in Discord voice chat, the Dynamic Island shows the active call. Pressing mute or unmute causes the island to permanently disappear.
- **Hypothesis:** `onNotificationRemoved` tags the SBN key in `recentlyRemovedKeys`, and `onNotificationPosted` drops any repost with the same key arriving within 2,000ms.

### Target 8: Issue #442 — SMS OTP Extraction Request
- **Upstream Reference:** [Issue #442](https://github.com/D4vidDf/HyperBridge/issues/442)
- **Environment Reported:** Google Messages receiving verification codes.
- **Reported Defect (Enhancement):** The sender name appears on the left pill, but the numerical verification code is buried in full message text on the right or truncated. User requested dedicated OTP extraction and right-pill code presentation.
- **Classification:** Enhancement Request.
