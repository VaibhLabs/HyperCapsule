# HyperCapsule App Library Toggle Bug Report

**Bug ID:** BUG-LIB-TOGGLE-01  
**Severity:** High (Functional Defect)  
**Status:** FIXED & VERIFIED  
**Component:** Home Dashboard (`LibraryPage.kt` & `ActiveAppsPage.kt`)  
**Resolution Date:** 2026-11-11  

---

## 1. Bug Description & Reproduction Steps

### Reported Symptom
1. Open the HyperCapsule application and navigate to the **Library** tab.
2. Toggle an unbridged application ON using its switch.
3. The application correctly turns ON, and immediately appears in the **Active** tab.
4. Attempting to toggle the same application OFF in the Library tab fails completely — the switch stays ON or reverts immediately back to ON.

---

## 2. Root Cause Analysis

Inspection of `LibraryPage.kt` revealed the exact defect at the `AppListItem` invocation:

```kotlin
// Defective code in LibraryPage.kt (line 141):
AppListItem(
    app = app,
    onToggle = { viewModel.toggleApp(app.packageName, true) }, // <-- HARDCODED TRUE!
    onSettingsClick = { onConfig(app) },
)
```

### Analysis
1. `AppListItem` hosts `HyperCapsuleSwitch(checked = app.isBridged, onCheckedChange = onToggle)`.
2. When the user taps the switch, `HyperCapsuleSwitch` computes `!checked` and delivers the target `Boolean` (`false` if currently bridged, `true` if unbridged) to the `onToggle: (Boolean) -> Unit` lambda.
3. However, `LibraryPage.kt` completely discarded the boolean parameter and **hardcoded `true`** into `viewModel.toggleApp(app.packageName, true)`.
4. Therefore, whenever the user attempted to disable an application from the Library tab, `toggleApp` was invoked with `true`, re-inserting the package into `ALLOWED_PACKAGES` and making it impossible to disable an app from the Library page!
5. Similarly, `ActiveAppsPage.kt` had hardcoded `false`, preventing symmetric re-enabling from that page if clicked again.

---

## 3. Implemented Fix

### 3.1 Parameterized Callbacks in `LibraryPage.kt` and `ActiveAppsPage.kt`
Both pages were updated to correctly forward the target boolean parameter emitted by `HyperCapsuleSwitch`:

```kotlin
// Corrected code in LibraryPage.kt & ActiveAppsPage.kt:
AppListItem(
    app = app,
    onToggle = { isEnabled -> viewModel.toggleApp(app.packageName, isEnabled) },
    onSettingsClick = { onConfig(app) },
)
```

### 3.2 Immediate Memory Cache Reconciliation in `AppPreferences.kt`
To guarantee zero-lag synchronous reads across coroutines, `AppPreferences.save` and `AppPreferences.remove` were enhanced to immediately synchronize with `memoryCache`:

```kotlin
private suspend fun save(key: String, value: String) {
    memoryCache[key] = value
    dao.insert(AppSetting(key, value))
}

private suspend fun remove(key: String) {
    memoryCache.remove(key)
    dao.delete(key)
}
```

---

## 4. Verification & Testing

### Automated Unit Testing
Created `app/src/test/java/com/hypercapsule/app/PackageToggleTest.kt` verifying:
1. `testEnablePackageFromEmpty`: Enabling an app from empty state produces `{package}`.
2. `testDisablePackageAfterEnabled`: Disabling an enabled app successfully removes it from the set.
3. `testRepeatedToggleCycles`: 6 consecutive bidirectional toggle cycles across multiple packages (`com.whatsapp`, `com.spotify.music`) with 100% accuracy and zero stale state.

### Results
- OFF -> ON: Successfully enables and bridges the app.
- ON -> OFF: Successfully disables and unbridges the app.
- Instant UI reflection: Active apps list and Library switch update immediately.
- Persistence: DataStore / Room retains state across app restarts and navigation.
