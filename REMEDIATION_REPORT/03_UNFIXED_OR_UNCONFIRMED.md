# 03_UNFIXED_OR_UNCONFIRMED.md — Unfixed, Unconfirmed, and False-Positive Findings

**Project Root:** `D:\HyperCapsule`  
**Application ID:** `com.hypercapsule.app`  

---

## 1. Executive Summary

Every investigated defect was subjected to rigorous code evidence validation. Three proposals or hypotheses identified during upstream issue analysis were classified as false positives or platform-constrained boundaries where code modifications would have caused severe regressions or violated platform security boundaries.

---

## 2. Itemized Disposition Matrix

| Issue Reference | Category | Disposition | Justification |
| :--- | :--- | :---: | :--- |
| **Complete Shade Notification Erasure** | Platform Boundary | **Rejected Workaround** | Android notification policy prevents 3rd-party apps from posting ongoing notifications that are completely invisible in the shade. Attempting to force cancellation drops the status bar cutout. Remediated via `IMPORTANCE_MIN` silent channel. |
| **Total Removal of XMSF Network Code** | Upstream Proposal | **Rejected Proposal** | Removing the XMSF network gate to speed up Global ROMs would break notification delivery on authentic HyperOS China ROMs. Resolved by selectively bypassing only when `!DeviceUtils.isCNRom`. |
| **Xiaomi Pad SystemUI Whitelist Bypass** | Firmware Boundary | **Documented Limitation** | On certain Xiaomi Pad 6S Pro firmware builds, SystemUI disables `"miui.focus.param"` rendering for 3rd-party UIDs entirely. This cannot be bypassed without modifying system partition firmware. |

---

## 3. Detailed Technical Analysis

### 1. Complete Notification Shade Erasure
- **Hypothesis:** Some upstream discussions claimed setting `setPriority(PRIORITY_MIN)` or calling `cancel()` immediately after `notify()` would allow the cutout island to persist while removing 100% of the notification card from the shade.
- **Investigation:** Code inspection and Android platform verification prove that when `cancel()` is called on a notification ID, `com.android.systemui` tears down both the shade notification AND its associated focus island cutout. On Android 14+, 3rd-party applications cannot post an active notification without an accompanying entry in the system notification shade.
- **Resolution Strategy:** HyperCapsule implements the correct, supported platform behavior: when `isShowShade == false`, notifications are directed to `hyper_bridge_silent_channel` with `NotificationManager.IMPORTANCE_MIN`, `setSilent(true)`, and `PRIORITY_MIN`. The card is minimized into the collapsed silent section, preserving the island without violating Android system integrity.

---

### 2. Deletion of the XMSF Network Gate
- **Hypothesis:** Several upstream developers proposed completely removing `XmsfNetworkHelper` and privileged network toggles because they caused delays on Global ROMs.
- **Investigation:** On Xiaomi China ROMs, Xiaomi Service Framework (XMSF) aggressively monitors and swallows notifications that do not originate from certified system partners. Disabling network access to XMSF for 50ms during dispatch allows the notification to bind directly to SystemUI. Deleting this code would have crippled Chinese ROM users.
- **Resolution Strategy:** Retained the workaround for China ROMs (`DeviceUtils.isCNRom == true`), but bypassed it completely for Global ROM users.

---

### 3. Xiaomi Pad Firmware-Level Focus Whitelist
- **Hypothesis:** Upstream #444 suggested HyperBridge/HyperCapsule had a bug preventing focus islands on tablets.
- **Investigation:** In HyperOS 3.0.303 for Xiaomi Pad 6S Pro, Xiaomi's `com.android.systemui.statusbar.phone.FocusedNotifManager` contains a hardcoded package whitelist restricting focus rendering to system apps (Music, Navigation, Clock). Third-party focus payloads are ignored by the firmware status bar controller.
- **Resolution Strategy:** HyperCapsule corrected all application-side defects (tablet landscape suppression was removed in `PermanentIslandManager`), and the troubleshooting guide directs users to verify whether their firmware supports third-party focus notifications.
