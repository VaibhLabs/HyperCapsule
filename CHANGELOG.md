# Changelog — HyperCapsule

All notable changes to the HyperCapsule codebase are documented in this file.

---

## [1.00 Stable] - 2026-11-11
### Designed & Developed by VaibhLabs

### Added & Refined
- **Human-Crafted Visual Identity & UI Polish:**
  - Shifted from generic AI neon gradients to disciplined spatial elevation, intentional typography hierarchy, and subtle specular borders.
  - Native AMOLED deep black base (`#06080C`) paired with Xiaomi HyperOS glass accents.
- **Island Behavior Complete Redesign:**
  - Redesigned `IslandSettingsControl` with 3 logical modules: Display & Auto-Dismissal, Floating Popup (Heads-Up) Presentation, and Notification Interception & Inline Reply.
  - Added live interactive Island simulator demonstrating real-time pill expansion and auto-close countdown.
- **Glitch-Free Design Tab Architecture:**
  - Solved root cause of the side black bar artifact and swipe instability by replacing experimental Material 3 carousels with bounded, edge-to-edge aware `HorizontalPager` and `LazyRow` components.
  - Fluid 120Hz smooth scrolling and precise page snapping across all screen aspect ratios.
- **Tactile Motion Physics:**
  - Spring toggle switches with spring damping (`Spring.DampingRatioMediumBouncy`) and smooth thumb offset/morphing.
  - Animated expand/collapse for dependent settings with `AnimatedVisibility`.
  - Micro-haptic feedback on toggle and button clicks.
- **Theme Studio Live Island Mockups:**
  - Real notification island previews for installed themes and system default styles.
- **Developer Branding & Navigation Cleanup:**
  - Officially branded as **Developed by VaibhLabs**.
  - Added direct link button **Visit VaibhLabs.com** pointing to `https://github.com/VaibhLabs/HyperCapsule`.
  - Removed outdated Version History and Special Thanks entries from the user-facing Settings interface while preserving required legal notices.
- **One-Time v1.00 Stable Welcome Announcement:**
  - Replaced legacy startup changelog with a one-time release dialog highlighting verified capabilities.

---

## [Phase 1: Project Rebranding & Engineering Refactor] - 2026-11-11

### Added
- **Full Project Rebranding:**
  - Migrated identity from HyperBridge to **HyperCapsule**.
  - New package namespace: `com.hypercapsule.app`.
  - Application label updated across 21 localized string tables.
- **Privacy & Security Documentation:**
  - Exhaustive `PRIVACY_POLICY.md` detailing zero-internet, offline-first architecture.
  - Offline assets copy: `app/src/main/assets/privacy_policy.html`.
  - Comprehensive `THIRD_PARTY_NOTICES.md` documenting upstream and third-party software licenses.
- **Engineering & Architecture Guides:**
  - `ARCHITECTURE.md`: High-level diagram and deep dive into the dual-engine pipeline.
  - `BUILDING.md`: Toolchain, JDK 21, and compilation instructions.
  - `PROJECT_AUDIT.md`, `PACKAGE_MIGRATION_PLAN.md`, `BRANDING_MIGRATION_PLAN.md`, `CODE_CLEANUP_PLAN.md`, and `FEATURE_PRESERVATION_INVENTORY.md`.

### Changed
- **Package Relocation:**
  - Relocated all Kotlin sources from `com.d4viddf.hyperbridge` to `com.hypercapsule.app`.
  - Relocated AIDL interfaces (`IPrivilegedService.aidl`, `IPrivilegedLogCallback.aidl`) to `com.hypercapsule.app`.
  - Updated `AndroidManifest.xml` and ProGuard rules.
- **Backward Compatibility:**
  - Preserved dual-intent handling for `APPLY_THEME` (`com.hypercapsule.app.APPLY_THEME` and legacy `com.d4viddf.hyperbridge.APPLY_THEME`) to support existing `.hbr` theme packages.
  - Maintained database migration compatibility for existing user preferences.
- **Settings Screen Cleanup:**
  - Decoupled external repository link buttons without hidden redirects.
  - Updated developer and about credits to point to the local HyperCapsule project.
- **Codebase Cleanup:**
  - Cleaned up unmanaged coroutine scopes, logging tags, and unused imports.
  - Preserved all legal notices, Apache 2.0 license headers, and upstream research credits.

---

## Upstream HyperBridge Historical Changelog

### [0.5.7]
- Upgraded to latest Android Gradle Plugin and enabled R8 code shrinking.
- Improved notification synchronization and fixed premature notification dismissal.

### [0.5.6]
- Validated `highlightColor` before passing to SystemUI to prevent crashes.
- Resolved permanent island disappearance bugs on lockscreen unlock.
- Added landscape mode auto-hide toggle.

### [0.5.5]
- Fixed original app icon display on Android 16 Live Updates.
- Enabled inline replies directly from Live Update notifications.
- Messaging grouping overhaul and deduplication.

### [0.5.0]
- Sui & Shizuku workaround for Xiaomi Chinese ROMs.
- Theme Engine: per-app and global animation engines.
- Smart icon tinting for monochrome app icons.
- Dedicated download layout with circular progress and green tick animation.
