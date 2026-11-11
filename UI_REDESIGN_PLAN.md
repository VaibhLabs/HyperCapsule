# UI Redesign Plan: HyperCapsule

**Design Language:** Liquid Glass × Material 3 Expressive × HyperOS Native × AMOLED Premium  
**Project Location:** `D:\HyperCapsule`  
**Phase:** 2 — Production-Quality Implementation  

---

## 1. Visual Aesthetics & Design Tokens

### 1.1 Color Philosophy
* **AMOLED Pure Dark:** Deepest base surface (`#07080A` to `#0B0D12`), eliminating grey wash for true OLED contrast and battery savings.
* **Liquid Glass Layers:**
  - Base Card: `Color(0xFF141720).copy(alpha = 0.70f)` with a luminous 1dp border `Color.White.copy(alpha = 0.08f)`.
  - Elevated / Interactive Glass: `Color(0xFF1E2230).copy(alpha = 0.85f)` with border `Color.White.copy(alpha = 0.16f)`.
  - Active Glow / Accent: Electric Cyan (`#00C7FF`), iOS/HyperOS Dynamic Blue (`#0A84FF`), Neon Emerald (`#30D158`), Amber Flame (`#FF9F0A`), and Hyper Violet (`#AF52DE`).
* **Adaptive Light Palette:** Clean, crisp porcelain white surfaces (`#F7F8FC`) with gentle layered shadows and ice-blue accents.

### 1.2 Shape & Geometry System
* **Continuous Squircles:** Smooth squircle-like corner radii (`16.dp`, `24.dp`, `28.dp`, `32.dp`) reflecting the organic curves of the camera cutout island.
* **Layered Elevation:** Hierarchy achieved through luminance and subtle borders rather than heavy drop shadows.

### 1.3 Motion & Micro-Interactions
* **Tactile Spring Curves:** `spring(dampingRatio = 0.78f, stiffness = 400f)` for press states and screen entries.
* **Micro-Scale Feedback:** Gentle scale compression (`0.97f`) on card and button click to create tangible physical feedback.
* **Battery-Conscious Animation:** Zero unbounded canvas animations or infinite recompositions in background screens.

---

## 2. Design System Architecture (`ui/designsystem/`)

```
app/src/main/java/com/hypercapsule/app/ui/designsystem/
├── color/
│   └── HyperCapsuleColors.kt          # Semantic color scheme & token definitions
├── typography/
│   └── HyperCapsuleTypography.kt      # Expressive font scales & weights
├── shapes/
│   └── HyperCapsuleShapes.kt          # Standard squircle radii & clip shapes
├── spacing/
│   └── HyperCapsuleSpacing.kt         # Grid metrics (4dp, 8dp, 12dp, 16dp, 20dp, 24dp, 32dp)
├── motion/
│   └── HyperCapsuleMotion.kt          # Spring constants, easing specs, durations
├── components/
│   ├── HyperCapsuleScaffold.kt        # Edge-to-edge container with gradient/mesh backdrop
│   ├── HyperCapsuleTopBar.kt          # Translucent floating top bar with title & back navigation
│   ├── HyperCapsuleBottomNavigation.kt# Floating pill bottom navigation bar with active indicators
│   ├── HyperCapsuleGlassCard.kt       # Multi-variant glass card with tactile press animation
│   ├── HyperCapsuleSettingsRow.kt     # Standardized row with leading icon badge & trailing widget
│   ├── HyperCapsuleSectionHeader.kt   # Category header with optional badge/action
│   ├── HyperCapsuleControls.kt        # HyperCapsuleSwitch, HyperCapsuleSlider, ActionButton
│   ├── HyperCapsuleSearchField.kt     # Translucent search field with clear & filter buttons
│   ├── HyperCapsuleEmptyState.kt      # Stylized empty state illustration container
│   ├── HyperCapsuleStatusIndicator.kt # Pulsing status dots & health badges
│   └── HyperCapsuleDialogs.kt         # Consistent modal sheets & alert dialogs
└── theme/
    └── HyperCapsuleDesignSystem.kt    # CompositionLocal providers and unified theme hook
```

---

## 3. Implementation Order & Staging

### Stage A: Core Design System
1. Implement tokens (`HyperCapsuleColors`, `HyperCapsuleTypography`, `HyperCapsuleShapes`, `HyperCapsuleSpacing`, `HyperCapsuleMotion`).
2. Implement core components (`HyperCapsuleGlassCard`, `HyperCapsuleScaffold`, `HyperCapsuleTopBar`, `HyperCapsuleBottomNavigation`, `HyperCapsuleSettingsRow`, `HyperCapsuleControls`).

### Stage B: Home Experience & Application Explorer
1. Redesign `HomeScreen.kt` with a floating liquid bottom navigation bar and ambient background mesh.
2. Upgrade `ActiveAppsPage.kt` and `LibraryPage.kt` with modern glass tiles, reactive filter chips, and smooth search transitions.
3. Polish `AppListFilterSection.kt`, `AppListItem.kt`, and `AppConfigBottomSheet`.

### Stage C: Design Hub & Theme Studio
1. Redesign `DesignScreen.kt` with fluid hero carousel, quick theme applicator cards, and modern action shortcuts.
2. Upgrade `ThemeManagerScreen.kt` and `ThemeCreatorScreen.kt` with modern tab selectors, live previews, and shape morphing.
3. Modernize `SavedAppWidgetsScreen.kt`, `WidgetConfigScreen.kt`, and `WidgetPickerScreen.kt`.

### Stage D: Settings & Diagnostics Hub
1. Redesign `InfoScreen.kt` into an expressive, categorized command center.
2. Upgrade `GlobalSettingsScreen.kt`, `SetupHealthScreen.kt`, `PrioritySettingsScreen.kt`, `AppPriorityScreen.kt`, `GlobalBlockListScreen.kt`, `BackupSettingsScreen.kt`, `ImportPreviewScreen.kt`, `ChangelogHistoryScreen.kt`, and `LicensesScreen.kt`.
3. Modernize specialized configuration screens (`NavCustomizationScreen.kt`, `IslandSettingsScreen.kt`, `EngineSettingsScreen.kt`, `DndSettingsScreen.kt`, `PermanentIslandConfigScreen.kt`, `GlobalReplyCustomizationScreen.kt`).

### Stage E: Floating Overlays & External Sheets
1. Redesign `InlineReplyService.kt` with enhanced glassmorphic blur, modern textfield styling, and spring-animated send button.
2. Redesign `ThemeInstallerActivity.kt` dialog with sleek dark card styling and clear theme metadata inspection.

### Stage F: Verification & Reporting
1. Inspect all changed files for imports and syntactic correctness.
2. Run compilation verification.
3. Generate completion deliverables (`UI_REDESIGN_REPORT.md`, `UI_FEATURE_PRESERVATION_REPORT.md`, `UI_SCREEN_COMPLETION_MATRIX.md`).
