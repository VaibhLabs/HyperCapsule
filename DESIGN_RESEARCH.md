# HyperCapsule Design Research & Design Direction Document

**Project:** HyperCapsule — Dynamic Island for Xiaomi HyperOS & Android  
**Version:** 1.00 Stable  
**Studio / Developer:** VaibhLabs  
**Design Lead:** Senior Android Product & Motion Design Architecture  

---

## 1. Executive Summary & Problem Analysis

HyperCapsule provides an ambient, hardware-notch integrated notification pill (Dynamic Island) for Android devices, especially Xiaomi HyperOS. Prior revisions suffered from AI-typical design tropes:
1. **Excessive & Indiscriminate Glow:** Heavy neon gradients (`#00C7FF`, `#9D4EDD`) splashed on nearly every card container and button, reducing visual contrast and creating visual fatigue.
2. **Generic AI-Generated Layouts:** Endless homogeneous cards without clear primary/secondary hierarchical anchoring.
3. **Rough Interaction Feedback:** Abrupt toggle transitions and jerky carousel snapping with an annoying horizontal black border artifact on the Design tab.
4. **Scattered Settings:** Deeply nested, inconsistent controls in Island Behavior where users had trouble understanding how Heads-up popups and Notification shade islands interact.

Phase 2.5 establishes a **Human-Crafted, Commercial-Grade Visual Identity** anchored in Xiaomi HyperOS native aesthetics, Material 3 Expressive motion physics, and AMOLED depth.

---

## 2. Core Design Philosophy: "Fluid Precision & Tactile Depth"

Instead of superficial glow overlays, HyperCapsule 1.00 Stable uses:
- **Layered Spatial Elevation:** True tonal layering (`#07080A` canvas -> `#10131A` base surface -> `#161A24` glass container -> `#1E2330` elevated highlight) with precise 1dp specular borders (`#24FFFFFF`).
- **Tactile Physics (Haptics + Motion):** Interactive elements deliver subtle haptic ticks alongside critically damped spring physics (`dampingRatio = 0.65f`, `stiffness = MediumLow`), giving toggles and cards a responsive, physical sensation.
- **Intentional Whitespace & Rhythm:** Strict adherence to a 4dp / 8dp grid system with 16dp horizontal gutter margins and 24dp section spacing.
- **Human-Directed Information Architecture:** Highlighting actionable statuses (e.g., Active Notification Engine, Island Preview) at the top, grouping operational parameters logically, and relegating edge-case overrides into clean expandable disclosures.

---

## 3. Visual System Specifications

### 3.1 Color System
- **Canvas / Amoled Base:** `#06080C` (Absolute black battery saver with deep blue undertone).
- **Surface Elevation 1 (Card Base):** `#0E121B` (8% Luminance) with `#16FFFFFF` border.
- **Surface Elevation 2 (Interactive Glass):** `#151B27` (12% Luminance) with `#22FFFFFF` specular edge.
- **Brand Primary Accent (Electric Cyan):** `#00C7FF` — Used exclusively for active states, key toggles, and primary calls-to-action.
- **Secondary Accent (Hyper Violet):** `#8A4FFF` — Reserved for auxiliary badges, pro tags, and theme highlights.
- **Functional Semantics:**
  - Success / Live: `#30D158` (iOS / HyperOS compliant emerald)
  - Warning / Caution: `#FFD60A` (Amber)
  - Critical / Destructive: `#FF453A` (Coral crimson)

### 3.2 Typography Scale
- **Display / Top Titles:** `Inter` / `Manrope` / `Roboto Sans`, Headline Medium (24sp, FontWeight.Bold, tracking -0.5sp).
- **Section Headers:** Title Medium (15sp, FontWeight.SemiBold, tracking +0.1sp, uppercase subtle tint).
- **Card Titles:** Body Large (16sp, FontWeight.SemiBold).
- **Explanatory Subtitles:** Body Small (13sp, FontWeight.Normal, line-height 18sp, color `#A0A5B5`).
- **Interactive Badges / Counters:** Label Small (11sp, FontWeight.Bold, tracking +0.5sp).

### 3.3 Motion Specifications (Jetpack Compose)
- **Spring Toggle Physics:**
  - Stiffness: `Spring.StiffnessMediumLow` (400f)
  - Damping Ratio: `Spring.DampingRatioMediumBouncy` (0.65f)
  - Thumb Morph: Width dynamically expands from 20dp to 24dp during state transitions.
- **Card Micro-Interactions:**
  - Press Scale: `0.98f` with `tween(100)` enter and `spring(dampingRatio = 0.6f)` release.
- **Expandable Disclosures:**
  - Enter: `expandVertically(spring(stiffness = Spring.StiffnessLow)) + fadeIn(tween(180))`
  - Exit: `shrinkVertically(spring(stiffness = Spring.StiffnessLow)) + fadeOut(tween(140))`

---

## 4. Mobile Information Architecture & Screen Restructuring

### 4.1 Island Behavior Redesign
The previous Island Behavior UI was a flat list of disparate cards. The new layout is organized into 3 clear conceptual modules:
1. **Module 1: Presence & Auto-Dismissal**
   - Live Island interactive preview showing real-time pill expansion.
   - Master toggle: "Auto-hide Notification Island".
   - Contextual fluid slider: Step-snapped duration from 1 second to persistent.
2. **Module 2: Pop-up / Heads-Up Presentation (Floating Pill)**
   - Float pill toggle with real-time screen overlay simulator.
   - Separate floating timeout control with live seconds indicator.
   - Lock screen & notification shade presentation toggles.
3. **Module 3: Interception & Action Hygiene**
   - Native notification suppression ("Remove original notification").
   - Inline reply enabling toggle with input preview.
   - Synchronized dismissal with original notification.

### 4.2 Design Hub Carousel & Root Cause Fix
The side black bar on the Design Tab was caused by `HorizontalMultiBrowseCarousel` measuring item widths outside the parent column's clipped boundaries and conflicting with window insets under `enableEdgeToEdge()`.  
**Architecture Resolution:**
- Implement a stable, edge-to-edge aware `HorizontalPager` / bounded `LazyRow` with `rememberLazyListState()` or `rememberPagerState()`, explicit page snapping, and `PaddingValues(horizontal = 16.dp)`.
- Enforce strict clipping on item boundaries with zero horizontal layout leakage.
- Stabilize touch target velocity to prevent erratic swipe overscroll.

---

## 5. Accessibility & Inclusivity (WCAG 2.1 AA Compliance)
- Contrast ratio for all text elements against `#0E121B` exceeds 4.5:1 (Primary text `#F5F6F8` achieves 14.8:1; Secondary `#A0A5B5` achieves 6.5:1).
- Touch target sizes for all switches, buttons, and settings rows strictly meet or exceed `48dp x 48dp`.
- `contentDescription` attributes supplied for all informative iconography.
- Full support for system font scaling up to 1.35x without container clipping or ellipsis collapse.
