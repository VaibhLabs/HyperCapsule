# HyperCapsule 1.00 Stable — Official Changelog

**Release:** HyperCapsule 1.00 Stable  
**Version Code:** 34  
**Developer:** VaibhLabs  
**Release Date:** 2026-11-11  

---

## 🌟 Highlights of Version 1.00 Stable

HyperCapsule 1.00 Stable represents the definitive realization of a modern, responsive, and aesthetically stunning dynamic island experience for Android. Featuring our brand new **Red AMOLED × Liquid Glass** design system, full bidirectional application state reliability, an overhauled Island Behaviour studio, and official VaibhLabs identity.

---

## 🎨 New Visual Identity: Red AMOLED × Liquid Glass

- **Semantic Color Architecture:**
  - True AMOLED Obsidian Canvas (`#080809`) for maximum contrast and battery efficiency.
  - Matte Graphite Primary Surface (`#141419`) and Elevated Cards (`#1B1B22`).
  - Specular Liquid Glass 1dp Borders (`#34343D`).
  - Electric Red Brand Accent (`#FF454F`) paired with Soft Crimson Highlights (`#E85D68`) and Ambient Red Tint (`#35191E`).
  - Retained Ice Cyan (`#35C8F5`) for secondary functional accents and informational cues.
  - High Legibility Text System: Primary (`#F5F5F7`, 14.2:1 contrast) and Secondary (`#A4A4B0`).
- **Liquid Glass Elevation & Specular Depth:** Refined cards with subtle depth without expensive full-screen GPU blur penalties.

---

## 🐛 Critical Bug Fixes

- **App Library Toggle Defect (`BUG-LIB-TOGGLE-01`):**
  - Resolved high-priority issue where toggling off an application in the App Library failed to disable it.
  - Fixed hardcoded boolean parameters in `LibraryPage.kt` and `ActiveAppsPage.kt`.
  - Added direct synchronization between `AppPreferences` and `memoryCache` for immediate zero-lag state propagation across coroutines.
  - Backed by automated bidirectional unit test suite (`PackageToggleTest.kt`).

---

## 🏝️ Island Behaviour UI Overhaul

- **Top Live Queue Simulator:** Interactive Dynamic Notch preview visualizing notification stacking in real time.
- **Enhanced Strategy Cards:** Generous 88dp+ touch cards with clear descriptions and custom vector iconography.
- **Fluid Selection Indicators:** Smooth spring animated borders and glowing red accents on selected strategies.
- **Strategy Preservation:** 100% preservation of underlying algorithms: *First Come First Served*, *Most Recent*, and *Custom Priority*.

---

## 🏛️ Design Hub Polish

- **Smooth Horizontal Pager:** Completely eradicated page clipping, glitches, and side black bars in the Design tab carousel.
- **Functional Customization Card:** Directly launches the integrated Theme Creator.
- **Official Community Link:** Cleanly links to the official repository at `https://github.com/VaibhLabs/HyperCapsule`.
- **Pro Features Card Excised:** Removed mock paywalls and non-functional purchase cards.

---

## 🏢 Official Developer Branding (VaibhLabs)

- **Studio Identity:** Fully unified under **VaibhLabs**.
- **Action Button:** "Visit VaibhLabs.com" safely launches `https://github.com/VaibhLabs/HyperCapsule` via standard `ACTION_VIEW`.
- **Footer:** Proudly marked with "Made with ❤️ in India".
- **Legal Compliance:** Comprehensive upstream open-source attributions retained in `LicensesScreen.kt` and `THIRD_PARTY_NOTICES.md`.

---

## ⚡ Performance & Animation System

- **Motion Specifications:** Lightweight spring physics (`stiffness = 500f`, `dampingRatio = 0.8f`) for all switches, navigation items, and expandable cards.
- **Battery & 120Hz Efficiency:** Eliminated continuous background rendering loops, optimized Compose recomposition scopes, and attached stable keys across all Lazy components.
- **Release Welcome Experience:** One-time verified welcome dialog introducing users to HyperCapsule 1.00 Stable without interrupting onboarding.
