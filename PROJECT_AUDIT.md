# Project Audit: HyperCapsule (Phase 1)

**Date:** 2026-11-11  
**Project Directory:** `D:\HyperCapsule`  
**Current Application ID / Namespace:** `com.d4viddf.hyperbridge`  
**Target Application ID / Namespace:** `com.hypercapsule.app`  
**Target Application Name:** HyperCapsule  
**Upstream Codebase:** HyperBridge (v0.5.7, versionCode 33) by D4vidDf  

---

## 1. Executive Summary

This comprehensive audit details the architecture, code organization, components, dependencies, security and privacy posture, and brand references of the inherited HyperBridge Android Kotlin application. This audit establishes the baseline for Phase 1: Rebranding, Package Migration, Documentation, Privacy Policy Preparation, and Engineering Refactoring without UI redesign or functional regression.

---

## 2. Directory & File Inventory

The repository is structured as a standard single-module modern Android Gradle project:

```
D:\HyperCapsule\
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.yml              # Upstream bug report template with HyperBridge references
│   │   └── feature_request.yml         # Upstream feature request template
│   ├── workflows/
│   │   ├── build-debug-apk.yml         # CI workflow building debug APK
│   │   └── tests.yml                   # CI workflow running unit tests
│   └── FUNDING.yml                     # Upstream sponsor config (D4vidDf)
├── app/
│   ├── build.gradle.kts                # Application build config (compileSdk 36, minSdk 35, targetSdk 37)
│   ├── proguard-rules.pro              # Keep rules for serialization, room, shizuku
│   └── src/
│       ├── androidTest/java/com/d4viddf/hyperbridge/ExampleInstrumentedTest.kt
│       ├── test/java/com/d4viddf/hyperbridge/ExampleUnitTest.kt
│       └── main/
│           ├── AndroidManifest.xml     # Core component and permission manifest
│           ├── aidl/com/d4viddf/hyperbridge/
│           │   ├── IPrivilegedService.aidl
│           │   └── IPrivilegedLogCallback.aidl
│           ├── java/com/d4viddf/hyperbridge/
│           │   ├── MainActivity.kt
│           │   ├── data/
│           │   │   ├── AppCacheManager.kt
│           │   │   ├── AppPreferences.kt
│           │   │   ├── ConfigResolver.kt
│           │   │   ├── db/
│           │   │   │   ├── AppDatabase.kt
│           │   │   │   ├── AppSettings.kt
│           │   │   │   └── SettingsDAO.kt
│           │   │   ├── model/
│           │   │   │   └── BackupModels.kt
│           │   │   ├── theme/
│           │   │   │   ├── RulesEngine.kt
│           │   │   │   └── ThemeRepository.kt
│           │   │   └── widget/
│           │   │       ├── HyperAppWidgetHost.kt
│           │   │       └── WidgetManager.kt
│           │   ├── integration/shizuku/
│           │   │   ├── PrivilegedServiceImpl.kt
│           │   │   ├── ShizukuHook.kt
│           │   │   ├── ShizukuUserServiceRecycler.kt
│           │   │   ├── ShizukuUtil.kt
│           │   │   └── XmsfNetworkHelper.kt
│           │   ├── models/
│           │   │   ├── ActiveIslands.kt
│           │   │   ├── AppGroup.kt
│           │   │   ├── BridgeAction.kt
│           │   │   ├── HyperIslandData.kt
│           │   │   ├── IslandConfig.kt
│           │   │   ├── IslandLimitMode.kt
│           │   │   ├── NavContent.kt
│           │   │   ├── NotificationType.kt
│           │   │   ├── WidgetConfig.kt
│           │   │   ├── WidgetRenderMode.kt
│           │   │   ├── WidgetSize.kt
│           │   │   └── theme/
│           │   │       └── ThemeModels.kt
│           │   ├── receiver/
│           │   │   ├── BootReceiver.kt
│           │   │   └── InlineReplyReceiver.kt
│           │   ├── service/
│           │   │   ├── InlineReplyService.kt
│           │   │   ├── NotificationReaderService.kt
│           │   │   ├── PermanentIslandManager.kt
│           │   │   ├── WidgetOverlayService.kt
│           │   │   └── translators/
│           │   │       ├── BaseTranslator.kt
│           │   │       ├── CallTranslator.kt
│           │   │       ├── DownloadTranslator.kt
│           │   │       ├── LiveUpdateTranslator.kt
│           │   │       ├── MediaTranslator.kt
│           │   │       ├── MessageTranslator.kt
│           │   │       ├── NavTranslator.kt
│           │   │       ├── ProgressTranslator.kt
│           │   │       ├── StandardTranslator.kt
│           │   │       ├── TimerTranslator.kt
│           │   │       └── WidgetTranslator.kt
│           │   ├── ui/
│           │   │   ├── AppListViewModel.kt
│           │   │   ├── components/
│           │   │   ├── navigation/
│           │   │   ├── screens/
│           │   │   └── theme/
│           │   └── util/
│           │       ├── BackupManager.kt
│           │       ├── DeviceUtils.kt
│           │       ├── ImageExtensions.kt
│           │       ├── ShizukuManager.kt
│           │       ├── StringExt.kt
│           │       ├── SystemIntents.kt
│           │       └── XiaomiNotificationHelper.kt
│           └── res/
│               ├── drawable/
│               ├── layout/
│               ├── mipmap-*/
│               ├── values/ (and 20 localized values-* directories)
│               └── xml/
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── .gitignore
├── build.gradle.kts
├── CONTRIBUTING.md
├── crowdin.yml
├── gradle.properties
├── gradlew / gradlew.bat
├── LICENSE
├── Prompt.txt
├── README.md
└── settings.gradle.kts
```

---

## 3. Architecture & Key Android Components

### 3.1 Activities
1. **`MainActivity`** (`android.intent.action.MAIN`, `android.intent.category.LAUNCHER`):
   - Entry point using Jetpack Compose and `androidx.navigation3`.
   - Directs between Onboarding (if setup not complete) and the 3-tab Home interface (`Design`, `Active Apps`, `Library`).
2. **`ThemeInstallerActivity`** (`com.d4viddf.hyperbridge.APPLY_THEME`, `ACTION_VIEW` for ZIP/.hbr theme packages):
   - Handles theme imports from external intents or downloads, presents dialog to inspect and apply themes.

### 3.2 Services
1. **`NotificationReaderService`** (`android.service.notification.NotificationListenerService`):
   - Core background service intercepting status bar notifications (`StatusBarNotification`).
   - Resolves effective engine (Native Live Update vs Custom HyperIsland via `hyperisland_kit`).
   - Delegates to specialized translators.
   - Dispatches translated focus notifications using either `ShizukuManager` or `NotificationManagerCompat`.
2. **`WidgetOverlayService`** (`Service`, `foregroundServiceType="shortService"`):
   - Hosts and snapshots pinned Android AppWidgets for overlay rendering.
3. **`InlineReplyService`** (`Service`):
   - Draws a floating Compose WindowManager view for in-place quick replies over other applications.
4. **`PrivilegedServiceImpl`** (`IPrivilegedService.aidl`):
   - Runs as a privileged Shizuku user process to invoke hidden system firewall APIs for XMSF network toggle.

### 3.3 Broadcast Receivers
1. **`BootReceiver`**:
   - Listens for `BOOT_COMPLETED`, `LOCKED_BOOT_COMPLETED`, `MY_PACKAGE_REPLACED`, `USER_UNLOCKED`, etc., to initialize `WidgetManager` and re-synchronize notifications.
2. **`InlineReplyReceiver`**:
   - Receives remote input replies and forwards them to `InlineReplyService`.

### 3.4 Content Providers
1. **`androidx.core.content.FileProvider`**:
   - Authority: `${applicationId}.provider`
   - Shares exported backups (`.hbr`) and themes with external apps.
2. **`rikka.shizuku.ShizukuProvider`**:
   - Authority: `${applicationId}.shizuku`
   - Configured for IPC with Shizuku / Sui.

---

## 4. Dependencies & Third-Party Libraries

From `gradle/libs.versions.toml`:
- **Android Gradle Plugin (AGP):** `9.3.2`
- **Kotlin:** `2.3.21` (Compose compiler plugin enabled)
- **KSP:** `2.3.2`
- **Compose Material 3:** `1.5.0-alpha15`
- **Navigation 3:** `androidx.navigation3:navigation3-runtime:1.1.2`, `ui`, `lifecycle-viewmodel-navigation3:2.10.0`
- **Room Database:** `2.8.4` (Room compiler, runtime, ktx)
- **DataStore:** `androidx.datastore:datastore-preferences:1.2.1`
- **Visual Blur:** `dev.chrisbanes.haze:haze:1.1.1`, `haze-materials`
- **Privileged API:** `dev.rikka.shizuku:api:13.1.5`, `provider:13.1.5`
- **HyperIsland Kit:** `io.github.d4viddf:hyperisland_kit:0.4.4` (3rd party dependency published on Maven Central by upstream author D4vidDf)
- **Serialization:** `kotlinx.serialization:1.11.0`, `com.google.code.gson:gson:2.14.0`

---

## 5. Security & Privacy Audit

1. **Network Permissions:**
   - **`android.permission.INTERNET` is NOT requested in `AndroidManifest.xml`.**
   - The app has no network access capabilities.
   - All processing, notification listening, SQLite storage, and image parsing run **100% locally and offline on-device**.
2. **Notification Listener Service (`BIND_NOTIFICATION_LISTENER_SERVICE`):**
   - Reads incoming notification metadata (title, text, small icon, large icon, actions, progress, media session).
   - Filters out spoiler words and junk. Does NOT transmit notification content.
3. **Query All Packages (`android.permission.QUERY_ALL_PACKAGES`):**
   - Required so the user can browse their installed applications in the Library tab and choose which ones to bridge to the island.
4. **Shizuku Integration (`moe.shizuku.manager.permission.API_V23`):**
   - Used for privileged notification dispatch and temporary firewall rule toggling on Chinese ROMs (`com.xiaomi.xmsf`).
5. **System Alert Window (`android.permission.SYSTEM_ALERT_WINDOW`):**
   - Used exclusively for the floating inline reply overlay window.

---

## 6. Upstream Attribution & License Obligations

- The upstream codebase is licensed under the **Apache License, Version 2.0**.
- Original Copyright: `D4vidDf`.
- Upstream special thanks: `Stardawn (星间晞)` for XMSF notification workaround research.
- All original Apache 2.0 notices and third-party library licenses (`THIRD_PARTY_NOTICES.md`) must be meticulously preserved.
