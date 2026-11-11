# UI Interaction Matrix: HyperCapsule

This matrix traces every interactive control, state mutator, callback, and navigation transition across HyperCapsule to guarantee zero functional regression during the Phase 2 redesign.

---

## 1. Top-Level Navigation & Scaffold

| Surface | UI Element / Control | Trigger / Event | Action & State Flow |
|---|---|---|---|
| **`HomeScreen`** | Bottom Navigation: Design | Click Tab 0 | Switches active view to Design Hub / Sub-routes. |
| **`HomeScreen`** | Bottom Navigation: Active | Click Tab 1 | Displays enabled applications with quick toggle & config access. |
| **`HomeScreen`** | Bottom Navigation: Library | Click Tab 2 | Displays all device packages with category filter & search. |
| **`HomeScreen`** | Top Bar: Settings Icon | Click | Navigates to `Screen.Info` (Settings Hub). |
| **`HomeScreen`** | Back Press | System Back / Gesture | If on Design sub-route ➔ Navigates to `DesignRoute.DASHBOARD`; if modal open ➔ dismisses modal; else ➔ exits app. |

---

## 2. Active Apps & Library Interactions (`ui/screens/home/`)

| Composable | Control | Event | Functional Contract |
|---|---|---|---|
| `ActiveAppsPage` | App Row Card | Tap | Opens `AppConfigBottomSheet` for tapped package. |
| `ActiveAppsPage` | App Enable Switch | Toggle | Updates `preferences.setPackageAllowed(pkg, boolean)`. |
| `LibraryPage` | Search Field | Text Input | Filters applications in real time by label or package name via `viewModel.searchQuery`. |
| `LibraryPage` | Category Chips | Tap Chip (All, Music, Nav, etc.) | Updates `viewModel.selectedCategory`. |
| `LibraryPage` | Sort Order Dropdown | Select Sort (Name, Recent, Package) | Updates `viewModel.sortOrder`. |
| `LibraryPage` | Enable App Switch | Toggle | Updates allowed package set. If enabled, app appears in Active tab immediately. |
| `AppConfigBottomSheet` | Notification Type Chips | Toggle (Call, Nav, Download, etc.) | Updates per-app enabled types in `AppPreferences`. |
| `AppConfigBottomSheet` | Heads-Up / Shade Switches | Toggle | Updates per-app floating banner & shade preferences. |
| `AppConfigBottomSheet` | Timeout Sliders | Drag Slider | Updates per-app dismissal timeouts. |
| `AppConfigBottomSheet` | Edit Nav Layout | Tap | Navigates to `Screen.NavCustomization(packageName)`. |

---

## 3. Design Hub & Theme Studio Interactions (`ui/screens/design/`, `ui/screens/theme/`)

| Screen / Sheet | Control | Trigger | Functional Contract |
|---|---|---|---|
| `DesignScreen` | Hero Carousel | Swipe / Tap | Displays theme engine features and documentation cards. |
| `DesignScreen` | Theme Card | Tap "Apply" | Invokes `themeRepository.activateTheme(themeId)` & updates preferences. |
| `DesignScreen` | "Create Theme" Button | Tap | Sets `designRoute = DesignRoute.THEME_CREATOR` (new theme). |
| `DesignScreen` | "My Themes" Button | Tap | Sets `designRoute = DesignRoute.THEME_MANAGER`. |
| `DesignScreen` | "Add Widget" Button | Tap | Sets `showWidgetPicker = true`. |
| `ThemeManagerScreen` | Theme Item: Edit | Tap | Sets `designRoute = DesignRoute.THEME_CREATOR` with `editingThemeId`. |
| `ThemeManagerScreen` | Theme Item: Export | Tap | Serializes `.hbr` ZIP package and invokes system share sheet. |
| `ThemeManagerScreen` | Theme Item: Delete | Tap | Displays confirmation dialog; deletes theme directory from internal storage. |
| `ThemeCreatorScreen` | Sub-Navigation Tabs | Tap (Colors, Icons, Calls, Apps, Actions, Engine) | Switches active editor detail content. |
| `ColorsDetailContent` | Preset Swatches / Custom Picker | Select Color | Updates active theme `highlightColor` / `backgroundColor`. |
| `IconsDetailContent` | Shape Radio Cards | Select (Circle, Squircle, Arch, Cookie, Clover) | Updates theme `iconShapeId` and triggers real-time preview shape morphing. |
| `CallStyleSheetContent` | Answer / Decline Shapes & Colors | Select Shape / Pick Color | Updates theme `CallModule` properties. |
| `ActionsDetailContent` | "Add Keyword Rule" Button | Tap | Opens `ActionConfigSheet` to map button text to custom style. |
| `ThemeCreatorScreen` | "Save" / "Apply" Buttons | Tap | Writes `theme_config.json` to disk, packages assets, and hot-reloads theme. |
| `WidgetPickerScreen` | Widget Provider Tile | Tap | Allocates widget ID via `WidgetManager.allocateId()` and opens `WidgetConfigScreen`. |
| `WidgetConfigScreen` | Render Mode Selector | Tap (Interactive / Snapshot) | Updates `WidgetConfig.renderMode`. |
| `WidgetConfigScreen` | Container Size Selector | Tap (Original, S, M, L, XL) | Updates `WidgetConfig.size`. |
| `WidgetConfigScreen` | "Save Widget" Button | Tap | Binds widget and registers in `savedWidgetIdsFlow`. |

---

## 4. Settings & Diagnostic Interactions (`ui/screens/settings/`)

| Screen | Control | Event | Functional Contract |
|---|---|---|---|
| `InfoScreen` | Configuration Rows | Tap Row | Navigates to corresponding setting sub-screen (`Setup`, `Behavior`, `GlobalSettings`, `Blocklist`, `Backup`). |
| `InfoScreen` | Language Selector Row | Tap | Opens in-app `LanguageSelectorDialog` to change app locale. |
| `GlobalSettingsScreen` | Setting Rows | Tap Row | Navigates to sub-screens (`IslandSettings`, `EngineSettings`, `DndSettings`, `PermanentIsland`, `ReplyCustomization`). |
| `SetupHealthScreen` | Check Item Action Buttons | Tap | Dispatches Android system intents for Notification Access, Autostart settings, or Battery Optimization. |
| `SetupHealthScreen` | Shizuku Override Action | Tap | Prompts Shizuku permission request via `ShizukuManager.requestPermission()`. |
| `PrioritySettingsScreen`| Strategy Selector | Tap Radio (FCFS, Recent, Custom) | Updates `preferences.setLimitMode(mode)`. |
| `AppPriorityScreen` | Drag Handle | Reorder List | Updates `preferences.setAppPriorityList(list)`. |
| `GlobalBlockListScreen`| "Add Word" Button | Tap | Inserts new keyword into global spoiler filter set. |
| `GlobalBlockListScreen`| Word Chip Remove | Tap 'X' | Deletes keyword from filter set. |
| `PermanentIslandConfigScreen` | Enable Permanent Island | Toggle | Updates `preferences.setPermanentIslandEnabled(boolean)`. |
| `PermanentIslandConfigScreen` | Width Slider | Drag | Updates `preferences.setPermanentIslandWidth(width)` and re-dispatches pill. |
| `PermanentIslandConfigScreen` | Hide in Landscape | Toggle | Updates `preferences.setHidePermanentIslandLandscape(boolean)`. |
| `BackupSettingsScreen` | "Create Backup" | Tap | Selects data categories and creates export file via document picker. |
| `BackupSettingsScreen` | "Restore Backup" | Tap | Launches document picker to read `.hbr` file and navigates to `Screen.ImportPreview`. |
| `ImportPreviewScreen` | Restore Categories | Checkboxes | Selects which data (Settings, Blocklist, Priorities) to restore. |
| `ImportPreviewScreen` | "Restore Selected" | Tap | Calls `backupManager.restoreBackup()` and returns to Home. |

---

## 5. Floating Overlays & External Modals

| Surface | Control | Functional Contract |
|---|---|---|
| `InlineReplyService` | Floating Textfield | Auto-requests focus, accepts soft keyboard input, handles IME action Send. |
| `InlineReplyService` | Send Button | Dispatches reply text through source notification's `RemoteInput` pending intent and dismisses overlay. |
| `InlineReplyService` | Outside Touch | Dismisses floating WindowManager overlay view. |
| `ThemeInstallerActivity`| "Apply Theme" Button | Unzips `.hbr` theme package, installs to `filesDir/themes/`, activates theme, and notifies service. |
| `ThemeInstallerActivity`| "Save for Later" Button | Unzips and installs theme without immediately activating it. |
| `ThemeInstallerActivity`| "Close" / Cancel | Discards temporary theme files and finishes activity. |
