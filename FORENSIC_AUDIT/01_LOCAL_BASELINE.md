# 01_LOCAL_BASELINE.md — Local Project Baseline & Repository Lineage Analysis

**Audit Date:** 2026-11-11  
**Investigator:** Senior Android Reverse-Engineering Analyst & Systems Architect  
**Project Root:** `D:\HyperCapsule`  
**Application Target:** HyperCapsule v1.00 Stable  
**Mode:** READ-ONLY CODE FORENSIC INVESTIGATION  

---

## 1. Application Identity & Package Metadata

| Property | Value | Evidence Source |
| :--- | :--- | :--- |
| **Application Name** | `HyperCapsule` | `app/src/main/res/values/strings.xml`, `AndroidManifest.xml` |
| **Application ID** | `com.hypercapsule.app` | `app/build.gradle.kts` (line 15) |
| **Namespace** | `com.hypercapsule.app` | `app/build.gradle.kts` (line 9) |
| **Version Name** | `1.00 Stable` | `app/build.gradle.kts` (line 19) |
| **Version Code** | `34` | `app/build.gradle.kts` (line 18) |
| **Compile SDK** | `36` (Android 16 Vanilla Ice Cream) | `app/build.gradle.kts` (line 11) |
| **Min SDK** | `35` (Android 15) | `app/build.gradle.kts` (line 16) |
| **Target SDK** | `37` (Android 17 preview release target) | `app/build.gradle.kts` (line 17) |
| **Local Head Commit** | `7ec081ed27842034e447d21c9b36edb0a4543507` | Git Log (`master` branch) |
| **Git Remotes** | `None` (Standalone local repository) | `git remote -v` |

---

## 2. Upstream Base Version Resolution (v0.5.7 vs v0.6.0-rc1)

### The Discrepancy
Upstream HyperBridge by developer D4vidDf released tags spanning v0.1.0 through v0.5.7, and subsequently published pre-release tags including v0.6.0-beta1 through v0.6.0-rc1. Previous project documentation and issue tickets raised questions regarding whether HyperCapsule's codebase descended from HyperBridge v0.5.7 or whether features from v0.6.0-beta5 / v0.6.0-rc1 were integrated.

### Concrete Forensic Evidence
1. **Changelog Boundary:**  
   In [`D:\HyperCapsule\CHANGELOG.md`](file:///D:/HyperCapsule/CHANGELOG.md#L68-L105), the upstream historical changelog explicitly terminates at **`[0.5.7]`**:
   ```markdown
   ## Upstream HyperBridge Historical Changelog
   ### [0.5.7]
   - Upgraded to latest Android Gradle Plugin and enabled R8 code shrinking.
   - Improved notification synchronization and fixed premature notification dismissal.
   ```
   There are zero changelog entries, tag traces, or references to any `0.6.0` release.
2. **Project Audit Inventory:**  
   In [`D:\HyperCapsule\PROJECT_AUDIT.md`](file:///D:/HyperCapsule/PROJECT_AUDIT.md#L8), the upstream lineage is documented as:
   `Upstream Codebase: HyperBridge (v0.5.7, versionCode 33) by D4vidDf`.
3. **Phase 1 Final Report:**  
   In [`D:\HyperCapsule\FINAL_PHASE1_REPORT.md`](file:///D:/HyperCapsule/FINAL_PHASE1_REPORT.md#L7), the base is documented as:
   `Upstream Project Base: HyperBridge (v0.5.7, versionCode 33) by D4vidDf`.
4. **Source Code Grep:**  
   A regex search across all Kotlin sources (`app/src/main/java/**/*.kt`), resource files (`res/**`), and Gradle build scripts for `0.6.0`, `beta5`, `rc1`, and related tokens returned **zero matches**.
5. **Version Code Continuity:**  
   Upstream HyperBridge v0.5.7 had `versionCode = 33`. HyperCapsule incremented to `versionCode = 34` when releasing `1.00 Stable`.

### Definitive Conclusion
**HyperCapsule is derived strictly from HyperBridge v0.5.7 (versionCode 33).**  
No code, architectural refactorings, or bug fixes from HyperBridge v0.6.0-beta1 through v0.6.0-rc1 exist in this repository. All subsequent upstream bug reports targeting HyperBridge v0.6.0-beta/rc must therefore be analyzed on the local v0.5.7-derived architecture rather than assuming newer upstream abstractions exist.

---

## 3. Current Source-Code Architecture & Hierarchy

The application follows a modern single-module Jetpack Compose architecture:
- `com.hypercapsule.app` (Application root)
  - `data/`
    - `AppPreferences.kt` — Room-backed key-value preference store with synchronous memory cache.
    - `ConfigResolver.kt` — Merges App, Theme, and Global configurations.
    - `db/` — Room SQLite persistence (`AppDatabase`, `AppSettings`, `SettingsDAO`).
    - `theme/` — Theme engine (`RulesEngine`, `ThemeRepository`).
    - `widget/` — Dynamic island widget rendering (`HyperAppWidgetHost`, `WidgetManager`).
  - `integration/shizuku/` — Privileged IPC bridging (`PrivilegedServiceImpl`, `ShizukuHook`, `XmsfNetworkHelper`).
  - `models/` — Domain models (`ActiveIsland`, `HyperIslandData`, `IslandConfig`, `NotificationType`).
  - `receiver/` — System broadcast receivers (`BootReceiver`, `InlineReplyReceiver`).
  - `service/`
    - `NotificationReaderService.kt` — `NotificationListenerService` consuming system notifications.
    - `PermanentIslandManager.kt` — Manages the persistent notch island.
    - `WidgetOverlayService.kt` — Background service for widget overlays.
    - `InlineReplyService.kt` — Floating WindowManager overlay for interactive text replies.
    - `translators/` — Domain-specific notification parsers (`CallTranslator`, `MediaTranslator`, `NavTranslator`, `LiveUpdateTranslator`, etc.).
  - `ui/` — Complete Jetpack Compose UI (Red AMOLED theme, Design Hub, Theme Studio, Settings).
  - `util/` — System utilities (`XiaomiNotificationHelper`, `DeviceUtils`, `ShizukuManager`).

---

## 4. Git Repository & Working Tree Status

```
Branch: master
HEAD: 7ec081e "feat: complete Red AMOLED x Liquid Glass update, fix library toggle bug, and assemble signed production v1.00 APK"
Working Tree: Clean (Prompt.txt updated with audit directive)
Remotes: None configured (isolated offline development repository)
```
