# UI Component Inventory: HyperCapsule

**Phase:** 2 — Premium UI/UX Redesign  
**Goal:** Catalog existing reusable components, evaluate their current state, and define their evolution into the **HyperCapsule Design System**.

---

## 1. Existing UI Components

| Component | Source File | Current Functionality | Planned Design System Upgrade |
|---|---|---|---|
| `AppListFilterSection` | `ui/components/AppListFilterSection.kt` | Search bar, category filter tabs (Music, Nav, Productivity, Other), sort order dropdown. | Frosted glass search field, glowing active pill tabs, tactile spring animated sort chips. |
| `AppListItem` | `ui/components/AppListItem.kt` | Card item displaying app icon, title, package, active badge, switch toggle, and configuration gear. | `HyperCapsuleGlassCard` container, smooth tactile switch, micro-press scaling feedback. |
| `BehaviorVisualizer` | `ui/components/BehaviorVisualizer.kt` | Graphic visualizer showing 9-island queue behaviors (FCFS, Most Recent, Priority). | Sleek animated pill queue representation with glowing active indicator. |
| `BlockListEditor` | `ui/components/BlockListEditor.kt` | Add/delete blocked keywords with Chip wrap layout. | Modern rounded glass chips with haptic delete interactions and smooth enter/exit animations. |
| `ChangelogSheet` | `ui/components/ChangelogSheet.kt` | Version update modal bottom sheet. | Frosted glass sheet (`Haze`), expressive typography, smooth spring dismiss. |
| `CommonCards` | `ui/components/CommonCards.kt` | Diagnostic cards, warning alerts, and summary tiles. | `HyperCapsuleGlassCard` variants: Subtle, Elevated, Warning Amber, Accent Cyan/Violet. |
| `CustomColorBottomSheet` | `ui/components/CustomColorBottomSheet.kt` | Hue/saturation/lightness color picker sheet. | HyperOS-styled radial or segmented color palette with live sample circle. |
| `Dialogs` & `AppConfigBottomSheet` | `ui/components/Dialogs.kt` | Multi-category trigger sheet for per-app configs; confirmation dialogs. | Standardized `HyperCapsuleBottomSheet` with layered glass card options and tactile switches. |
| `EmptyState` | `ui/components/EmptyState.kt` | Centered icon, title, description, and optional action button. | Refined minimalist vector iconography with subtle breathing ambient glow. |
| `EnginePreview` | `ui/components/EnginePreview.kt` | Interactive live preview comparing Xiaomi Focus vs Native Live Update notifications. | Ultra-crisp mockup with dark cutout simulation and fluid toggle animations. |
| `ExpressiveComponents` | `ui/components/ExpressiveComponents.kt` | Pill buttons, tonal icon buttons, segmented switches. | Integrated into `ui/designsystem/components/HyperCapsuleButtons.kt`. |
| `HyperOSColorPicker` | `ui/components/HyperOSColorPicker.kt` | Slider-based color selection. | HyperOS native color disc / slider with hex input. |
| `IslandSettingsControl` | `ui/components/IslandSettingsControl.kt` | Sliders for timeouts and switches for floating heads-up behavior. | Smooth continuous slider with value badge, tactile switch rows with icons. |
| `NavPreviewContent` | `ui/components/NavPreviewContent.kt` | Interactive simulation of Left/Right navigation layout. | Realistic camera cutout mockup with dynamic island expansion. |
| `PermanentIslandPreview` | `ui/components/PermanentIslandPreview.kt` | Interactive preview of ambient cutout pill with width slider. | True-to-life bezel & front camera lens visualization. |

---

## 2. New Design System Components to Introduce (`ui/designsystem/components/`)

1. **`HyperCapsuleScaffold`**: Edge-to-edge root scaffold supporting optional ambient mesh gradient backgrounds, top bar pinning, and navigation bar insets.
2. **`HyperCapsuleTopBar`**: Flexible, glassy top bar with blurred background, large expressive title, back navigation button, and trailing action slots.
3. **`HyperCapsuleGlassCard`**: Core visual primitive featuring:
   - Subtle semi-transparent surface.
   - Fine border highlight (subtle specular edge).
   - Glassmorphism backdrop blur using `Haze`.
   - Micro-interaction press scale animation.
4. **`HyperCapsuleSettingsRow`**: Reusable settings item with leading icon container, title, subtitle, and trailing widget slot (Switch, Arrow, Chip, Slider).
5. **`HyperCapsuleSectionHeader`**: Clean categorized typography header with optional trailing action or item count badge.
6. **`HyperCapsuleSwitch`**: Tactile, expressive switch matching HyperOS toggle physics.
7. **`HyperCapsuleSlider`**: Custom slider with tactile haptics, rounded thumb, and floating value tag.
8. **`HyperCapsuleBottomNavigation`**: Floating pill-shaped navigation bar with glowing indicator pills and smooth icon animations.
9. **`HyperCapsuleSearchField`**: Modern search input with clear icon, voice/filter button, and glassy background.
10. **`HyperCapsuleStatusIndicator`**: Pulsing dot and pill badge for active services, Shizuku connection, and battery optimization states.
