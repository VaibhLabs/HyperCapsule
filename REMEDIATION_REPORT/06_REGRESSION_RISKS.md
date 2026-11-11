# 06_REGRESSION_RISKS.md — Regression Risk Assessment & Mitigations

**Application:** HyperCapsule v1.00 Stable  
**Target Root:** `D:\HyperCapsule`  
**Assessment Date:** 2026-11-11  

---

## 1. Regression Risk Analysis Matrix

| Feature Area | Potential Risk | Likelihood | Impact | Built-in Mitigation |
| :--- | :--- | :---: | :---: | :--- |
| **Inline Reply Overlay** | Virtual keyboard overlap in landscape mode on custom 3rd-party IMEs | LOW | MEDIUM | Retained `Modifier.imePadding()` and `softInputMode = SOFT_INPUT_ADJUST_RESIZE`. Added outside touch and back button dismissals. |
| **Permanent Island Reassertion** | 700ms delay before Permanent Island reappears after music closes | LOW | LOW | Deliberate engineering delay (`DISPATCH_DELAY_MS`) to allow HyperOS SystemUI to finish tearing down the previous focus session. |
| **Notification Debounce** | Duplicate islands if an app rapidly posts non-identical call notifications | VERY LOW | LOW | Content hash verification (`newContentHash == previous.lastContentHash`) prevents duplicate UI updates. |
| **Shizuku Fast-Path** | Non-standard China ROM ports not matching `isCNRom` | VERY LOW | LOW | The Shizuku workaround toggle remains available in user settings as a manual override. |
| **Silent Notification Channel** | User manually altering silent channel importance in System Settings | LOW | LOW | The service dynamically updates channel settings and verifies importance on initialization. |
| **Bitmap Caching** | Multiple threads accessing shared transparent bitmap | ZERO | NONE | Cached bitmaps are static and immutable (`Bitmap.Config.ARGB_8888`), safe for concurrent read access by Canvas. |

---

## 2. Detailed Evaluation of High-Priority Surface Areas

### 1. Inline Reply Layout Adjustments
- **Change:** Transitioned from `MATCH_PARENT` full-screen modal window to `WRAP_CONTENT` anchored at `Gravity.BOTTOM`.
- **Validation:** Ensured `composeView` properly wraps its internal content. The text field and send button are bounded to a maximum height of 200dp with scrolling enabled.
- **Risk Mitigation:** If an unexpected window manager crash occurs, the 60-second inactivity watchdog and `screenOffReceiver` will forcefully destroy the window, preventing lingering touch blocks.

### 2. Permanent Island Suppression
- **Change:** Permanent Island is now suppressed while `currentRealNotifications > 0`.
- **Validation:** When media playback ends or navigation is cancelled, `syncNotifications` and `onNotificationRemoved` update `currentRealNotifications` to `0`. `desiredActive()` immediately flips back to `true`, scheduling `scheduleDispatchLocked()`.
- **Risk Mitigation:** Even if a race condition misses an immediate event, the periodic 60-second tick in `syncNotifications` re-evaluates the state and restores the island.

### 3. Official `requestRebind` API
- **Change:** Replaced component enabled state toggling with `NotificationListenerService.requestRebind()`.
- **Validation:** `requestRebind` is the official Google Android API introduced in API 24 specifically to re-establish dropped listener connections without toggling component flags.
- **Risk Mitigation:** Prevents security permission revocation across reboots on Android 14, 15, 16, and 17.
