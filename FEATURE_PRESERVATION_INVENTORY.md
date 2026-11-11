# Feature Preservation Inventory: HyperCapsule

This inventory catalogs every functional capability of the application to ensure that Phase 1 re-branding and engineering refactoring does not regress, remove, or damage existing functionality.

---

## 1. Core Notification & Island Engines

### 1.1 Dual Engine Architecture
- **Xiaomi Live Updates Engine (`LiveUpdateTranslator`):**
  - Generates notifications compatible with HyperOS native Live Update templates (`miui.focus.param` / category).
  - Supports Android 16+ Promoted Notifications (`POST_PROMOTED_NOTIFICATIONS`).
- **Custom HyperIsland Engine (`hyperisland_kit`):**
  - Uses `HyperIslandNotification.Builder` to assemble custom resource bundles and JSON parameters for custom dynamic island rendering.

### 1.2 Specialized Notification Translators
- **Call Translator (`CallTranslator`):**
  - Handles incoming and ongoing voice/video calls.
  - Dynamically extracts answer and decline actions.
  - Applies themed answer/decline icons and shapes (*Circle, Squircle, Arch, Cookie, Clover*).
- **Navigation Translator (`NavTranslator`):**
  - Parses navigation instructions, distances, and ETAs from Google Maps, Waze, etc.
  - Configurable Left/Right content positions (*Distance, ETA, Turn-by-Turn Instruction*).
- **Download Translator (`DownloadTranslator`):**
  - Dedicated circular progress display.
  - Regex percentage parsing from text (e.g. "45%").
  - "Green Tick" completion animation when download completes.
- **Progress Translator (`ProgressTranslator`):**
  - Standard progress bars for general background operations and file operations.
- **Media Translator (`MediaTranslator`):**
  - Extracts album artwork, track title, artist name, and playback controls.
  - Visualizer and "Now Playing" status display.
- **Message Translator (`MessageTranslator`):**
  - Anti-flicker deduplication for chat apps (e.g. WhatsApp, Telegram, Signal).
  - Extracts sender person avatar and message snippets.
  - Groups messages by notification group key.
- **Timer Translator (`TimerTranslator`):**
  - Countdown and stopwatch displays.
- **Standard Translator (`StandardTranslator`):**
  - Fallback translator for general alerts.

---

## 2. Permanent Island (`PermanentIslandManager`)
- Displays an empty pill around the camera cutout when no active notifications are present (`PERMANENT_BRIDGE_ID = 9999`).
- **Smart Collapsing:** Automatically hides when any real notification or widget is displayed, reappearing when clear.
- **Landscape Detection:** Dynamically hides in landscape orientation if configured.
- Configurable width slider in settings.

---

## 3. Shizuku & Chinese ROM Workaround (`integration/shizuku`)
- Direct privileged notification posting via Shizuku IPC (`IPrivilegedService.aidl`).
- **Stardawn XMSF Workaround:**
  - On Chinese ROMs, temporarily disables networking for `com.xiaomi.xmsf` via low-level firewall APIs (`OEM_DENY_CHAIN = 9`) for ~1 second during notification dispatch, bypassing MIUI/HyperOS focus notification suppression.
  - Dual implementation: AIDL user service with fallback to hooked binder (`ShizukuHook`).

---

## 4. AppWidget Integration (`data/widget` & `WidgetOverlayService`)
- Pin standard Android home screen widgets to the island or lockscreen overlay.
- Dual rendering modes:
  - **Interactive Mode:** Real-time touch interaction for controls.
  - **Snapshot Mode:** Periodic bitmap snapshots for battery efficiency.
- Container sizing (Original, Small, Medium, Large, Extra Large).

---

## 5. Inline Reply Overlay (`service/InlineReplyService`)
- Floating Jetpack Compose WindowManager overlay.
- Triggered when replying to messaging notifications with `RemoteInput`.
- Enables typing and sending replies without switching away from the active application.
- Customizable colors, corner radius, and blur effects.

---

## 6. Smart Watch Relay Notifications
- Ensures companion watch apps (Mi Fitness, WearOS, Garmin) still receive notifications when `removeOriginalNotification` is enabled.
- Posts a silent, short-lived relay copy on `WATCH_RELAY_CHANNEL_ID`.

---

## 7. Theming Engine & Rules Engine (`data/theme`)
- **Theme Creator:** Built-in theme editor with live previews.
- **Theme Manager:** Manage, export, and import portable `.hbr` ZIP theme packages.
- **Rules Engine:** Regex-based matching on package name, title, and body to redirect layout types and apply theme overrides.
- **Icon Shapes:** Circle, Square, Squircle, Arch, Cookie, Clover, Flower.
- **Smart Colors:** Palette color extraction from application icons.

---

## 8. Backup & Restore (`BackupManager`)
- Export and import application preferences, blocklists, and priority configurations to `.hbr` backup files.
- Versioned metadata schema with rollback safety.

---

## 9. Comprehensive Localization
- 21 languages supported across resource bundles.
- In-app language switcher decoupled from system locale.
