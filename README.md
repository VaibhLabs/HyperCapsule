<h1 align="center">HyperCapsule</h1>

<p align="center">
  <strong>Transforming Android Notifications into Native HyperOS Dynamic Islands &amp; Overlays</strong>
</p>

<p align="center">
  HyperCapsule is a clean, modern, and open-source Android utility that bridges standard system notifications into Xiaomi HyperOS's pill-shaped dynamic island UI around the front camera cutout. Featuring rich theme customization, live updates, lockscreen widgets, and an offline-first architecture.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/version-0.5.7-blue?style=for-the-badge" alt="Version 0.5.7" />
  <img src="https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/HyperOS-Compatible-black?style=for-the-badge" alt="HyperOS" />
  <img src="https://img.shields.io/badge/License-Apache%202.0-green.svg?style=for-the-badge" alt="License" />
</p>

---

## 🚀 Key Features

* **Dynamic HyperIslands:** Converts standard status bar notifications into native-feeling pill islands around your camera cutout.
* **Dual Engine Architecture:**
  * **Native Live Updates Engine:** Generates native HyperOS focus notifications (`miui.focus.param`) and Android 16+ Promoted Live Updates.
  * **Custom Island Engine:** Powered by custom layouts via `hyperisland_kit` for rich styling and custom controls.
* **Specialized Notification Translators:**
  * 📞 **Calls:** Incoming and ongoing call cards with custom answer/decline shapes and call timers.
  * 🧭 **Turn-by-Turn Navigation:** Distance, ETA, and turn arrows for Google Maps, Waze, and navigation apps.
  * ⬇️ **Downloads:** Circular progress indicators with a "Green Tick" completion animation and text percentage parsing.
  * 🎵 **Media Playback:** Visualizers, album art, and now-playing status.
  * 💬 **Messages & Chats:** Sender avatars, grouped chat summaries, and deduplication.
  * ⏱️ **Timers & Alarms:** Active countdown and stopwatch displays.
* **Interactive Inline Reply:** Floating Compose overlay (`WindowManager`) to reply to messages directly from the island without leaving your current app.
* **Permanent Island:** Keep an elegant ambient cutout pill visible when no notifications are active; automatically hides when active notifications or widgets appear, with optional landscape auto-hide.
* **Lockscreen & Island Widgets:** Pin standard Android AppWidgets directly into the island overlay with Interactive and Snapshot rendering modes.
* **Theme Engine:** Open `.hbr` theme package standard. Create themes with real-time previews, custom icon shapes (*Squircle, Clover, Arch, Cookie*), and Smart Brand Colors via Palette extraction.
* **Spoiler Protection:** Block terms globally or per-app to prevent sensitive notifications or spoilers from showing on the island.
* **Shizuku & Sui Integration:** Elevated IPC support and XMSF firewall toggle for full focus notification support on Chinese ROMs.
* **100% Offline & Private:** Zero Internet permissions requested (`android.permission.INTERNET` is omitted). All processing happens in-memory on your device.

---

## 🛠️ Technology Stack

| Layer | Component |
|---|---|
| **Language** | Kotlin 2.3.21 |
| **Android Target** | `minSdk = 35` (Android 15), `targetSdk = 37`, `compileSdk = 36` |
| **UI Toolkit** | Jetpack Compose + Compose BOM 2026.05.01 + Material 3 Expressive (`1.5.0-alpha15`) |
| **Navigation** | AndroidX Navigation 3 (`androidx.navigation3:navigation3-runtime:1.1.2`) |
| **Blur & Visuals** | Haze (`dev.chrisbanes.haze:haze:1.1.1`) |
| **Persistence** | Room SQLite 2.8.4 (KSP) + Jetpack DataStore Preferences 1.2.1 |
| **Background Services** | `NotificationListenerService`, `AppWidgetHost`, `Service` (shortService) |
| **Privileged System** | Rikka Shizuku API 13.1.5 + Sui IPC (`IPrivilegedService.aidl`) |
| **Serialization** | `kotlinx.serialization` + Google Gson |

---

## 📁 Project Structure

```
D:\HyperCapsule\
├── app/
│   ├── src/main/
│   │   ├── aidl/com/hypercapsule/app/       # Privileged IPC AIDL interfaces
│   │   ├── java/com/hypercapsule/app/
│   │   │   ├── MainActivity.kt              # App entry point & navigation host
│   │   │   ├── data/                        # Room DB, AppPreferences, Theme Repository
│   │   │   ├── integration/shizuku/         # Shizuku service, hook, and XMSF helper
│   │   │   ├── models/                      # Domain entities, configs, theme data
│   │   │   ├── receiver/                    # Boot and inline reply broadcast receivers
│   │   │   ├── service/                     # NotificationReaderService & translators
│   │   │   ├── ui/                          # Jetpack Compose screens & components
│   │   │   └── util/                        # DeviceUtils, ShizukuManager, BackupManager
│   │   └── res/                             # Layouts, drawables, and 21 localized string tables
│   ├── assets/privacy_policy.html           # Offline privacy policy document
│   └── build.gradle.kts                     # Module build configuration
├── ARCHITECTURE.md                          # Comprehensive architecture documentation
├── BUILDING.md                              # Detailed build & toolchain guide
├── CHANGELOG.md                             # Release notes & version history
├── CONTRIBUTING.md                          # Contribution guidelines
├── PRIVACY_POLICY.md                        # Verified offline-first privacy policy
├── PROJECT_AUDIT.md                         # Detailed codebase audit
└── THIRD_PARTY_NOTICES.md                   # Open source licenses & legal attributions
```

---

## ⚙️ Build Requirements & Setup

1. **JDK 17 or JDK 21 LTS** installed.
2. Android SDK with Build Tools 35.0.0+ and SDK platform 36.
3. Clone or open the project directory in PowerShell / Terminal:

```powershell
# Set JAVA_HOME if not already configured in your system environment
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"

# Build debug APK
.\gradlew assembleDebug

# Build release APK
.\gradlew assembleRelease
```
See [BUILDING.md](BUILDING.md) for full instructions.

---

## 🔒 Permissions & Privacy

HyperCapsule operates under a strict **offline-first security model**.
* **Zero Internet Access:** The app does not include `android.permission.INTERNET`. It cannot send data outside your device.
* **Notification Listener:** Required solely to format notifications into dynamic islands; processed in-memory and discarded upon dismissal.
* **Query All Packages:** Required to allow users to select which applications to bridge.
* **System Alert Window:** Required exclusively to present the inline reply overlay.
* **Shizuku (Optional):** Used for elevated notification dispatch on Chinese ROMs.

Read our complete, verified [Privacy Policy](PRIVACY_POLICY.md) or open `privacy_policy.html` locally.

---

## 📜 Origin & Upstream Attribution

HyperCapsule is derived from the open-source **HyperBridge** project originally created by **D4vidDf**, licensed under the **Apache License, Version 2.0**.
* Upstream Repository: [HyperBridge on GitHub](https://github.com/D4vidDf/HyperBridge)
* Special thanks to **Stardawn (星间晞)** for research on the Xiaomi XMSF notification workaround enabling dynamic islands on Chinese HyperOS ROMs.
* All upstream Apache 2.0 license headers, notices, and third-party library obligations are preserved in accordance with the Apache License 2.0. See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

---

## 🤝 Contributing

Contributions, bug reports, and optimizations are welcome! Please check our [Contributing Guidelines](CONTRIBUTING.md) before submitting pull requests.

---

## 📄 License

HyperCapsule is distributed under the **Apache License, Version 2.0**. See the [LICENSE](LICENSE) file for complete terms.
