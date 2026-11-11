# HyperCapsule Architecture Documentation

## 1. Architectural Philosophy

HyperCapsule is engineered as a modern, reactive, offline-first Android application targeting **Xiaomi HyperOS** (API 35–37 / Android 15–16+). It adheres to Clean Architecture and MVVM principles using Jetpack Compose, Kotlin Coroutines, Kotlin Flows, and Android Jetpack libraries.

```
┌────────────────────────────────────────────────────────┐
│                   UI Layer (Compose)                   │
│  - Navigation 3 (NavDisplay, Screen)                   │
│  - Screens: Home (Active/Library), Design, Settings    │
│  - ViewModels: AppListViewModel, ThemeViewModel        │
└──────────────────────────┬─────────────────────────────┘
                           │ StateFlow / Actions
┌──────────────────────────▼─────────────────────────────┐
│                      Data Layer                        │
│  - AppPreferences (In-Memory Hot Cache + Flow)         │
│  - Room Database (AppDatabase, AppSettings, DAO)       │
│  - ThemeRepository (.hbr ZIP extraction & disk cache)  │
│  - WidgetManager (AppWidgetHost & RemoteViews)         │
└──────────────────────────▲─────────────────────────────┘
                           │
┌──────────────────────────┴─────────────────────────────┐
│                 Background Services                    │
│  - NotificationReaderService (NotificationListener)    │
│  - PermanentIslandManager (Persistent pill)            │
│  - WidgetOverlayService (AppWidget hosting)            │
│  - InlineReplyService (WindowManager overlay)          │
│  - Shizuku Integration (Privileged IPC & XMSF bypass)  │
└────────────────────────────────────────────────────────┘
```

---

## 2. Notification Pipeline & Engine Resolution

When an external notification is posted to the status bar:
1. **Interception**: `NotificationReaderService.onNotificationPosted(StatusBarNotification)` receives the callback.
2. **Filtering & Pre-Processing**:
   - Skips self-generated notifications and internal notifications.
   - Evaluates system DND state and in-app DND toggles.
   - Evaluates global and per-app blocked terms (spoiler protection).
3. **Classification**:
   - `RulesEngine.match()` evaluates active theme rules (regex matches on package, title, and text).
   - If no custom rule matches, `detectNotificationType()` classifies into `CALL`, `NAVIGATION`, `DOWNLOAD`, `PROGRESS`, `MEDIA`, `MESSAGE`, `TIMER`, or `STANDARD`.
4. **Engine Decision**:
   - **Native Live Updates Engine (`LiveUpdateTranslator`)**: Creates a standard `NotificationCompat.Builder` with HyperOS focus extras (`miui.focus.param` / category) for native OS rendering.
   - **Custom Island Engine (`hyperisland_kit`)**: Uses `HyperIslandNotification.Builder` to assemble custom resource bundles and parameters for custom cutout rendering.
5. **Dispatch & Caching**:
   - If Shizuku workaround is enabled on Chinese ROMs, `ShizukuManager.notify()` temporarily restricts `com.xiaomi.xmsf` networking before posting to bypass vendor focus notification suppression.
   - Tracks active island instances in `activeIslands`, `activeTranslations`, and `reverseTranslations`.

---

## 3. Theming & Rules Engine

* **Package Format (`.hbr`)**: Portable ZIP archive containing:
  - `theme_config.json`: Serialized `HyperTheme` containing global styles, call modules, action button configurations, and regex rules.
  - `icons/`: Folder with custom PNG/WebP assets for navigation, call icons, and progress states.
* **Rules Engine (`RulesEngine.kt`)**: Prioritized list of regex conditions matched against incoming notifications at runtime to rewrite layouts or inject theme colors dynamically.

---

## 4. Pinned Widgets System

* **`HyperAppWidgetHost`**: Custom subclass of Android's `AppWidgetHost` allocating widget IDs and creating host views.
* **`WidgetOverlayService`**: Runs a lightweight service to maintain active widget lifecycles.
* **Rendering Modes**:
  - `INTERACTIVE`: Live views with clickable remote actions.
  - `SNAPSHOT`: Periodically rendered into bitmaps to save battery and avoid empty remote collections.

---

## 5. Security & Isolation

* **Offline Boundary**: The app declares zero network permissions.
* **Storage Isolation**: User configurations reside in SQLite (`AppDatabase`) in device-protected / credential-protected app storage.
* **Privileged Isolation**: The Shizuku user service runs in an isolated process via AIDL IPC.
