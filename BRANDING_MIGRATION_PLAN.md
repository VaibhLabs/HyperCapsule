# Branding Migration Plan: HyperCapsule

**Target Brand Name:** HyperCapsule  
**Previous Brand Name:** Hyper Bridge / HyperBridge  

---

## 1. Brand Identity Overview

HyperCapsule represents the evolution of the application into an independent, cleanly maintained, open-source utility for HyperOS devices. This plan defines the migration of all user-facing strings, visual themes, logging tags, and metadata while strictly adhering to legal attribution and third-party license requirements.

---

## 2. Resource Rebranding (`app/src/main/res/values/strings.xml` and Localizations)

### 2.1 Core Application Identity
| String Resource Key | Original Value | HyperCapsule Value |
|---|---|---|
| `app_name` | Hyper Bridge | **HyperCapsule** |
| `welcome_title` | Welcome to\nHyper Bridge | **Welcome to\nHyperCapsule** |
| `how_it_works_desc` | Select your favorite apps... HyperBridge transforms... | Select your favorite apps... **HyperCapsule** transforms... |
| `developer_credit` | by D4vidDf | **HyperCapsule Project** |
| `developer` | Developer | **HyperCapsule Project** |
| `developer_subtitle` | Visit d4viddf.com | **HyperOS Dynamic Island Utility** |
| `source_code` | Source Code | **Source Code** |
| `source_code_subtitle` | GitHub Repository | **HyperCapsule Open Source** |
| `privacy_desc` | Hyper Bridge processes everything... | **HyperCapsule** processes everything directly on your phone\'s processor.\n\nIt does NOT require internet access, meaning your notifications and personal data never leave your device. |
| `theme_installer_apply_question` | Do you want to apply this theme to HyperBridge now? | Do you want to apply this theme to **HyperCapsule** now? |
| `permission_reminder_desc` | HyperBridge has been updated!... | **HyperCapsule** has been updated! Please ensure... |
| `featured_notifications_check_desc` | ...display HyperBridge islands. | ...display **HyperCapsule** islands. |
| `featured_notifications_troubleshoot_desc` | ...Enable it for HyperBridge. | ...Enable it for **HyperCapsule**. |
| `featured_notifications_shizuku_desc` | ...HyperBridge will use Shizuku... | ...**HyperCapsule** will use Shizuku... |
| `migration_title` | HyperBridge Update | **HyperCapsule Update** |

### 2.2 Localized Resources
The string `app_name` and primary brand references in the 20 localized string tables (`values-ca-rES`, `values-cs-rCZ`, `values-de-rDE`, `values-en-rUS`, `values-es-rEM`, `values-es-rES`, `values-fr-rFR`, `values-gl-rES`, `values-hu-rHU`, `values-in-rID`, `values-it-rIT`, `values-ja-rJP`, `values-ko-rKR`, `values-pl-rPL`, `values-pt-rBR`, `values-ru-rRU`, `values-sk-rSK`, `values-tr-rTR`, `values-uk-rUA`, `values-zh-rTW`) will be updated to display "HyperCapsule" consistently.

---

## 3. Themes & Style Rebranding (`themes.xml` & Compose)

1. **`app/src/main/res/values/themes.xml`**:
   - `Theme.HyperBridge` -> `Theme.HyperCapsule`
   - `Theme.HyperBridge.Transparent` -> `Theme.HyperCapsule.Transparent`
2. **`AndroidManifest.xml`**:
   - Update `android:theme` references to `@style/Theme.HyperCapsule`.
3. **Jetpack Compose Theme (`ui/theme/Theme.kt`)**:
   - Rename `HyperBridgeTheme` composable to `HyperCapsuleTheme`.
   - Update usages in `MainActivity.kt`, `ThemeInstallerActivity.kt`, and screen previews.

---

## 4. UI Actions & Developer Links

Per Section 3 of the specification:
1. **GitHub Repository Button (`InfoScreen.kt`)**:
   - Remove the external navigation callback to `https://github.com/D4vidDf/HyperBridge`.
   - The button will display a disabled state or inform the user that repository navigation is currently offline in this build.
   - No silent external redirects or hidden callbacks.
2. **Developer Profile Link**:
   - Remove the external browser launch to `https://d4viddf.com`.
   - Present local project information and documentation access.

---

## 5. Logging Tags & Debug Identifiers

Update internal logging tags:
- `private val TAG = "HyperBridgeDebug"` -> `"HyperCapsuleDebug"`
- `private val TAG = "HyperBridge"` -> `"HyperCapsule"`
- `private val TAG = "HyperWidgetService"` -> `"HyperCapsuleWidget"`
- `private val TAG = "HyperRules"` -> `"HyperCapsuleRules"`
- `private val tag = "HyperBridgeTheme"` -> `"HyperCapsuleTheme"`

---

## 6. Preserved Legal & Historical Attribution

The following items are strictly preserved:
1. **Upstream Copyright Notice** in `LICENSE`: Apache License 2.0 copyright to D4vidDf.
2. **Third-Party Attribution**: Special Thanks to `Stardawn (星间晞)` for the research enabling the XMSF notification workaround on Chinese ROMs.
3. **Open Source Licenses Screen (`LicensesScreen.kt`)**:
   - Explicit attribution for all upstream libraries, including `HyperIsland-ToolKit` by D4vidDf (Apache 2.0).
4. **Historical Changelog**:
   - Previous versions (v0.1.0 through v0.5.7) are preserved as historical release notes.
