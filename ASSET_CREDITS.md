# HyperCapsule Asset Credits & Licensing Catalog

**Application:** HyperCapsule (v1.00 Stable)  
**Developer:** VaibhLabs  
**Policy:** 100% locally bundled, copyright-compatible, privacy-preserving assets. No runtime external hotlinking.

---

## 1. Brand Identity & Primary Art Assets

### Official App Icon & Brand Emblem
- **Asset Files:**
  - `app/src/main/res/mipmap-*/ic_launcher.png`
  - `app/src/main/res/mipmap-*/ic_launcher_round.png`
  - `app/src/main/res/mipmap-*/ic_launcher_foreground.png`
  - `app/src/main/res/drawable/ic_logo_foreground.png`
  - `screenshots/logo.png`
  - `app/src/main/ic_launcher-playstore.png`
  - `app/src/main/res/drawable/ic_launcher_foreground.xml` (Monochrome Vector)
  - `app/src/main/res/values/ic_launcher_background.xml` (`ic_launcher_background` = `#000000`)
- **Source:** Custom original artwork designed for HyperCapsule by VaibhLabs.
- **License:** Proprietary project asset, copyright © 2026 VaibhLabs. All rights reserved.
- **Description:** 3D illuminated capsule prism with warm orange/gold glowing squircle border, central celestial star, and orbital particle ring on deep AMOLED black.

---

## 2. System Iconography

### Material Symbols & Vector Drawables
- **Asset Group:** Android Material Icons (Extended) & Custom Vectors
  - `Icons.Rounded.Widgets`, `Icons.Rounded.Palette`, `Icons.Rounded.PhoneAndroid`
  - `Icons.Default.AccessTime`, `Icons.Default.Visibility`, `Icons.Default.Layers`
  - `Icons.AutoMirrored.Filled.Reply`, `Icons.Default.DeleteSweep`
  - `Icons.Default.Tune`, `Icons.Default.SettingsSuggest`, `Icons.Default.Save`
- **Source:** Google Material Symbols (Apache License 2.0).
- **License:** Apache License 2.0.
- **Modifications:** Consistent optical sizing (20dp/24dp/28dp/40dp), customized stroke weights, and semantic tint mapping (`LocalHyperCapsuleColors`).

---

## 3. UI Illustrations & Empty States

### In-App Visuals & Hero Graphics
- **Empty States & Onboarding Highlights:**
  - `ic_empty_state_widgets` / `HyperCapsuleEmptyState.kt`
  - `ic_empty_state_apps` / `AppListFilterSection.kt`
  - `HeroSection` / Vector Pill Compositions in `DesignScreen.kt`
- **Source:** Custom algorithmic Vector / Compose Canvas drawings developed natively within Jetpack Compose code.
- **License:** Apache License 2.0 / HyperCapsule Source Code.
- **Format:** Zero external binary bloat; rendered via Compose Path / DrawScope / Canvas for infinite DPI resolution and zero disk overhead.

---

## 4. Theme Presets & Visual Mockups

### Native Island Mockups & Templates
- **Presets:**
  1. *HyperOS Dynamic Glass* (Default system style with subtle specular frosted borders)
  2. *Cyberpunk Neon* (Electric Cyan `#00C7FF` & Neon Violet `#8A4FFF`)
  3. *Monochrome Minimalist* (Subtle OLED Obsidian `#121212` with pure `#FFFFFF` typography)
  4. *Solar Sunset* (Amber & Coral `#FF7043` warm notification highlights)
- **Source:** Created directly in Compose by VaibhLabs for HyperCapsule.
- **Format:** In-memory Compose preview vector layouts and local `.hbr` JSON metadata structures.
- **License:** Apache License 2.0.
