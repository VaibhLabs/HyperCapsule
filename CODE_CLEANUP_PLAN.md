# Code Cleanup & Engineering Refactor Plan: HyperCapsule

**Goal:** Elevate codebase quality, maintainability, and runtime stability without altering UI layout or breaking existing contracts.

---

## 1. Targeted Code Quality Improvements

### 1.1 Coroutines, Lifecycle & Memory Leak Prevention
- **`NotificationReaderService` & `WidgetOverlayService`**:
  - Replace unmanaged `CoroutineScope(Dispatchers.Default + Job())` with properly managed scopes tied to `onDestroy()` cancellation (`serviceScope.cancel()`).
  - Ensure all timeout jobs (`timeoutJobs`), debouncers, and removal jobs cleanly cancel before launching replacement jobs to avoid coroutine leaks.
- **`ShizukuManager`**:
  - Replace unbounded `CoroutineScope(Dispatchers.IO).launch` calls with a structured, managed scope.
  - Retain `Mutex` locks to guarantee atomic XMSF firewall switching without race conditions.
- **Broadcast Receivers**:
  - Ensure unregistration of dynamically registered receivers (`systemReceiver`, `islandClickReceiver`) in `NotificationReaderService.onDestroy()`.

### 1.2 Room Database & Settings Architecture
- **Room Migration**:
  - Maintain the migration from legacy DataStore to Room (`AppSettings`, `SettingsDAO`).
  - Ensure thread-safe caching via `memoryCache` with defensive copy operations.
- **Database Identity**:
  - Ensure compatibility with both `"hyperbridge_database"` and `"hypercapsule_database"` so existing installations upgrade seamlessly.

### 1.3 Translators & Translation Pipeline
- **`BaseTranslator`**:
  - Centralize color parsing with fallbacks (`toSafeColorHex`).
  - Cache Palette swatch lookups to prevent redundant image processing on frequent notification updates.
- **Deduplication in Notification Pipeline**:
  - Normalize text extraction between title, text, subText, and bigText.
  - Maintain anti-flicker content hashing (`lastContentHash`) across all translators.

### 1.4 Shizuku Integration & Privileged Services
- **`ShizukuUserServiceRecycler`**:
  - Clean up dead binder handling and retry delays.
  - Ensure proper component naming for `PrivilegedServiceImpl`.
- **`XmsfNetworkHelper` & `ShizukuHook`**:
  - Retain dual-path execution (Privileged AIDL service first, with direct hooked binder fallback).
  - Protect against `DeadObjectException` and `SecurityException`.

### 1.5 Dead Code & Unused Assets Removal
- Remove obsolete debugging `println`/raw `Log.e` traces and replace with consistent logging (`TAG`).
- Remove unused imports across all Kotlin source files.
- Remove stale `TODO` comments while preserving technical documentation.

---

## 2. Refactoring Boundaries (Strict Guarantees)
1. **DO NOT** rewrite UI screens or alter Jetpack Compose layouts.
2. **DO NOT** remove HyperOS-specific features (such as `miui.focus.param` or `hyperisland_kit` logic).
3. **DO NOT** alter the open theming specification (`.hbr` JSON schema).
4. **DO NOT** replace functioning implementations with stubs or mocks.
