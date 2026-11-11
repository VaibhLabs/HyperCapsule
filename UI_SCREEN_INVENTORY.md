# UI Screen Inventory: HyperCapsule

**Project:** HyperCapsule  
**Directory:** `D:\HyperCapsule`  
**Phase:** 2 — Premium UI/UX Redesign  
**Aesthetic:** Liquid Glass × Material 3 Expressive × HyperOS Native × AMOLED Premium  

---

## 1. Top-Level Navigation Destinations (`mainNavGraph`)

| Destination Key | Composable / Screen | File Location | Description & Sub-flows |
|---|---|---|---|
| `Screen.Onboarding` | `OnboardingScreen` | `ui/screens/onboarding/OnboardingScreen.kt` | Interactive multi-step setup guide: Welcome, How it Works, Permissions (Post Notification, Notification Access, System Alert Window), Battery Unrestricted, Autostart, and Diagnostics. |
| `Screen.Home` | `HomeScreen` | `ui/screens/home/HomeScreen.kt` | Main container with 3-tab navigation bar (`Design`, `Active Apps`, `Library`) hosting sub-routes. |
| `Screen.Info` | `InfoScreen` | `ui/screens/settings/InfoScreen.kt` | Primary Settings & About hub with application identity, version info, quick navigation to Configuration & About categories. |
| `Screen.GlobalSettings` | `GlobalSettingsScreen` | `ui/screens/settings/GlobalSettingsScreen.kt` | Central behavior controls (Heads-up popup, Notification shade, Timeouts, Inline Reply, Island Settings, Engine Settings, DND, Permanent Island). |
| `Screen.Setup` | `SetupHealthScreen` | `ui/screens/settings/SetupHealthScreen.kt` | System diagnostic checklist for Autostart, Battery Optimization, Notification Access, Overlay Permission, and Featured Focus Whitelist. |
| `Screen.Behavior` | `PrioritySettingsScreen` | `ui/screens/settings/PrioritySettingsScreen.kt` | Strategy selector for 9-island overflow: Most Recent, First-Come-First-Served, Custom App Priority. |
| `Screen.AppPriority` | `AppPriorityScreen` | `ui/screens/settings/AppPriorityScreen.kt` | Reorderable drag-and-drop list of applications to define strict notification priority. |
| `Screen.GlobalBlocklist` | `GlobalBlocklistScreen` | `ui/screens/settings/GlobalBlocklistScreen.kt` | Spoiler protection editor for global keyword filtering and access to per-app blocklists. |
| `Screen.BlocklistApps` | `BlocklistAppListScreen` | `ui/screens/settings/GlobalBlockListScreen.kt` | Application selector for per-app spoiler term filtering. |
| `Screen.Backup` | `BackupSettingsScreen` | `ui/screens/settings/BackupSettingsScreen.kt` | Export and Import `.hbr` settings, blocklists, and priority backup archives. |
| `Screen.ImportPreview` | `ImportPreviewScreen` | `ui/screens/settings/ImportPreviewScreen.kt` | Inspection sheet displaying backup metadata, creation date, device model, and selective restore checkboxes. |
| `Screen.History` | `ChangelogHistoryScreen` | `ui/screens/settings/ChangelogHistoryScreen.kt` | Expandable version history with detailed release notes. |
| `Screen.Licenses` | `LicensesScreen` | `ui/screens/settings/LicensesScreen.kt` | Clean list of open-source libraries, authors, licenses, and repositories. |
| `Screen.NavCustomization` | `NavCustomizationScreen` | `ui/screens/settings/NavCustomizationScreen.kt` | Left/Right navigation info layout selector (Distance, ETA, Turn Instruction, Empty) with live island preview. |
| `Screen.EngineSettings` | `EngineSettingsScreen` | `ui/screens/settings/EngineSettingsScreen.kt` | Switch between Xiaomi Featured Design and Native Live Update engine, plus Shizuku workaround toggle. |
| `Screen.IslandSettings` | `IslandSettingsScreen` | `ui/screens/settings/IslandSettingsScreen.kt` | Island timeout durations, heads-up banner float durations, and smart swipe behaviors. |
| `Screen.DndSettings` | `DndSettingsScreen` | `ui/screens/settings/DndSettingsScreen.kt` | Do Not Disturb rules: manual pause island toggle, system DND sync detection. |
| `Screen.PermanentIslandConfig` | `PermanentIslandConfigScreen` | `ui/screens/settings/PermanentIslandConfigScreen.kt` | Ambient camera cutout pill toggle, width slider, and landscape auto-hide switch with live preview. |
| `Screen.ReplyCustomization` | `GlobalReplyCustomizationScreen` | `ui/screens/theme/GlobalReplyCustomizationScreen.kt` | Styling editor for the floating inline reply overlay (textfield colors, blur glass effect, corner radii). |

---

## 2. Nested Sub-Screens & Routes (`HomeScreen` Design Tab)

| Sub-Route | Composable / Screen | File Location | Purpose & Controls |
|---|---|---|---|
| `DesignRoute.DASHBOARD` | `DesignScreen` | `ui/screens/design/DesignScreen.kt` | Design hub featuring Hero banner carousel, quick Theme picker, active Widget cards, and creation shortcuts. |
| `DesignRoute.THEME_MANAGER` | `ThemeManagerScreen` | `ui/screens/theme/ThemeManagerScreen.kt` | Theme library displaying custom and installed themes with activate, export, edit, and delete actions. |
| `DesignRoute.THEME_CREATOR` | `ThemeCreatorScreen` | `ui/screens/theme/ThemeCreatorScreen.kt` | Comprehensive theme editor studio with sub-navigation for Colors, Icons, Calls, Apps, Actions, and Engine rules. |
| `DesignRoute.WIDGET_LIST` | `SavedAppWidgetsScreen` | `ui/screens/design/SavedAppWidgetsScreen.kt` | Manager for active and saved Android AppWidgets pinned to the Island. |
| Widget Picker Modal | `WidgetPickerScreen` | `ui/screens/design/WidgetPickerScreen.kt` | System widget explorer grouped by installed application with search and preview generator. |
| Widget Config Modal | `WidgetConfigScreen` | `ui/screens/design/WidgetConfigScreen.kt` | Fine-tune widget container size (Original, S, M, L, XL), render mode (Interactive vs Snapshot), auto-refresh, and timeout. |

---

## 3. Theme Studio Sub-Modules (`ui/screens/theme/content/`)

1. **`ColorsDetailContent`**: Presets palette, custom hex/HSV color picker (`HyperOSColorPicker`), wallpaper Monet color mode, and smart app brand color extraction.
2. **`IconsDetailContent`**: Shape selector (*Circle, Square, Squircle, Arch, Cookie, Clover, Flower*), padding slider, and custom icon asset pickers for Nav & Progress.
3. **`CallStyleSheetContent`**: Answer and Decline button shapes, accent colors, and custom icon assets.
4. **`AppsDetailContent`**: Per-app custom theme overrides list and configuration launcher.
5. **`AppThemeEditorScreen`**: Granular override editor for individual applications.
6. **`ActionsDetailContent` & `ActionConfigSheet`**: Keyword-based notification button styling (*Text, Icon, Both*, background color, tint).
7. **`ReplyStyleSheetContent`**: Embedded inline reply styling sheet.
8. **`EngineThemeContent`**: Per-theme engine preference overrides.
9. **`ThemeSettingsContent`**: Theme metadata editor (Name, Author, Description, Custom Icon).

---

## 4. Modal Overlays, Bottom Sheets & Floating Views

1. **`AppConfigBottomSheet`** (`ui/components/Dialogs.kt`): Per-app island trigger overrides, notification types toggle (Calls, Nav, Media, Downloads, Messages, Timers), timeout overrides.
2. **`ThemeInstallerActivity`** (`ui/screens/theme/ThemeInstallerActivity.kt`): Floating dialog activity prompting user to inspect and apply downloaded/imported `.hbr` theme packages.
3. **`InlineReplyService`** (`service/InlineReplyService.kt`): Floating Compose `WindowManager` window with frosted glass backdrop blur, auto-focused text field, and remote input send action.
4. **`ChangelogSheet`** (`ui/components/ChangelogSheet.kt`): Modal bottom sheet displaying "What's New" upon application updates.
5. **`CustomColorBottomSheet`** (`ui/components/CustomColorBottomSheet.kt`): Bottom sheet color selector.
