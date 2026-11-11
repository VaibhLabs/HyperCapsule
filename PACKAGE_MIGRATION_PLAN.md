# Package Migration Plan: HyperCapsule

**Source Package:** `com.d4viddf.hyperbridge`  
**Target Package:** `com.hypercapsule.app`  
**Target Application ID:** `com.hypercapsule.app`  

---

## 1. Scope & Strategy

Migrating the application identifier and root package namespace touches multiple Android layers:
1. **Build System & Compiler**: `app/build.gradle.kts` namespace, applicationId, KSP generated files, AIDL stubs.
2. **Directory Structure**: Physical relocation of Kotlin source files and AIDL files.
3. **Source Code**: Package headers and imports in 45+ Kotlin files and 2 AIDL files.
4. **Android Manifest & IPC**: Activity, Service, Receiver, Provider declarations, Intent Actions, and authorities.
5. **Shizuku Privileged User Service**: Component name used when registering with Shizuku daemon.
6. **Room Database & Preferences**: Storage file names, database migrations, and cached setting keys.
7. **External Compatibility**: Backward-compatible handling of theme intents (`APPLY_THEME`).

---

## 2. Relocation Map

### 2.1 File System Paths
| Component | Old Path | New Path |
|---|---|---|
| **Main Kotlin Source** | `app/src/main/java/com/d4viddf/hyperbridge/` | `app/src/main/java/com/hypercapsule/app/` |
| **Main AIDL Source** | `app/src/main/aidl/com/d4viddf/hyperbridge/` | `app/src/main/aidl/com/hypercapsule/app/` |
| **Unit Tests** | `app/src/test/java/com/d4viddf/hyperbridge/` | `app/src/test/java/com/hypercapsule/app/` |
| **Android Tests** | `app/src/androidTest/java/com/d4viddf/hyperbridge/` | `app/src/androidTest/java/com/hypercapsule/app/` |

---

## 3. Traced References & Migration Details

### 3.1 Build Configuration (`app/build.gradle.kts`)
- `namespace`: Change `"com.d4viddf.hyperbridge"` to `"com.hypercapsule.app"`.
- `applicationId`: Change `"com.d4viddf.hyperbridge"` to `"com.hypercapsule.app"`.

### 3.2 AIDL Interfaces
- `IPrivilegedService.aidl`:
  - Change `package com.d4viddf.hyperbridge;` to `package com.hypercapsule.app;`
  - Import `com.hypercapsule.app.IPrivilegedLogCallback;`
- `IPrivilegedLogCallback.aidl`:
  - Change `package com.d4viddf.hyperbridge;` to `package com.hypercapsule.app;`

### 3.3 Android Manifest (`app/src/main/AndroidManifest.xml`)
- Activities:
  - `.MainActivity` -> resolves to `com.hypercapsule.app.MainActivity`
  - `.ui.screens.theme.ThemeInstallerActivity` -> resolves to `com.hypercapsule.app.ui.screens.theme.ThemeInstallerActivity`
- Services:
  - `.service.NotificationReaderService`
  - `.service.WidgetOverlayService`
  - `.service.InlineReplyService`
- Receivers:
  - `.receiver.BootReceiver`
  - `.receiver.InlineReplyReceiver`
- Providers:
  - `androidx.core.content.FileProvider`: Authority `${applicationId}.provider` (automatically uses `com.hypercapsule.app.provider`)
  - `rikka.shizuku.ShizukuProvider`: Authority `${applicationId}.shizuku` (automatically uses `com.hypercapsule.app.shizuku`)
- Intent Actions:
  - `com.d4viddf.hyperbridge.APPLY_THEME` -> Keep accepting this legacy action AND add `com.hypercapsule.app.APPLY_THEME` so existing theme packages continue to work!
  - `com.d4viddf.hyperbridge.THEME_PROVIDER` -> Keep in `<queries>` alongside `com.hypercapsule.app.THEME_PROVIDER`.
  - Internal broadcast actions in `BootReceiver` and `NotificationReaderService`:
    - `com.hypercapsule.app.ACTION_TEST_MIGRATION`
    - `com.hypercapsule.app.ISLAND_CLICKED`
    - `com.hypercapsule.app.ACTION_RELOAD_THEME`
    - `com.hypercapsule.app.ACTION_PERFORM_MIGRATION`

### 3.4 Shizuku Privileged Service Registration (`ShizukuUserServiceRecycler.kt`)
- `Shizuku.UserServiceArgs`:
  - ComponentName: `context.packageName` (`com.hypercapsule.app`) and `PrivilegedServiceImpl::class.java.name` (`com.hypercapsule.app.integration.shizuku.PrivilegedServiceImpl`).

### 3.5 ProGuard Rules (`app/proguard-rules.pro`)
- Replace:
  ```proguard
  -keep class com.d4viddf.hyperbridge.models.** { *; }
  -keep class com.d4viddf.hyperbridge.data.db.** { *; }
  -keep class com.d4viddf.hyperbridge.integration.shizuku.** { *; }
  -keep class com.d4viddf.hyperbridge.IPrivilegedService { *; }
  -keep class com.d4viddf.hyperbridge.IPrivilegedService$Stub { *; }
  ```
  With:
  ```proguard
  -keep class com.hypercapsule.app.models.** { *; }
  -keep class com.hypercapsule.app.data.db.** { *; }
  -keep class com.hypercapsule.app.integration.shizuku.** { *; }
  -keep class com.hypercapsule.app.IPrivilegedService { *; }
  -keep class com.hypercapsule.app.IPrivilegedService$Stub { *; }
  ```

### 3.6 Room Database Compatibility (`AppDatabase.kt`)
- Existing database filename: `"hyperbridge_database"`.
- Migration strategy:
  - Check if legacy database `"hyperbridge_database"` exists on device. If so, copy/migrate to `"hypercapsule_database"` or safely open `"hypercapsule_database"` with fallback to ensure zero data loss for upgraded installations.

---

## 4. Verification Checkpoints
- [ ] Directory tree relocated to `com/hypercapsule/app/`.
- [ ] All `.kt` files contain updated package header `package com.hypercapsule.app...`.
- [ ] All AIDL interfaces compiled into stubs without missing class references.
- [ ] R-class references (`com.hypercapsule.app.R`) resolved cleanly.
- [ ] Build variant `assembleDebug` succeeds with new namespace.
