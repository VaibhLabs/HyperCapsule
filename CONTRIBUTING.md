# Contributing to HyperCapsule

Thank you for your interest in contributing to **HyperCapsule**!

---

## 1. Getting Started
1. Clone the repository.
2. Open the project in **Android Studio** (Ladybug / Meerkat or newer recommended) or compile using Gradle directly.
3. Ensure JDK 17 or JDK 21 is selected in Gradle Settings.
4. Sync Gradle and build the debug APK using `./gradlew assembleDebug`.

---

## 2. Architecture & Code Style
* **Language:** Kotlin exclusively.
* **UI Framework:** Jetpack Compose with Material 3 Expressive.
* **Architecture:** MVVM with reactive Kotlin Flows and Coroutines.
* **Clean Layering:**
  * `ui/`: Compose Screens, ViewModels, and reusable components.
  * `service/`: `NotificationReaderService`, background services, and specialized translators.
  * `data/`: Room Database (`AppDatabase`), DataStore, and Theme Repository.
  * `integration/`: Privileged system calls and Shizuku IPC.
  * `models/`: Data contracts, configurations, and serializable models.

---

## 3. Pull Request Guidelines
* Keep PRs focused on specific bug fixes, optimizations, or translations.
* Ensure code compiles cleanly without warnings or broken dependencies (`./gradlew test`).
* Do not introduce telemetry, remote network calls, or third-party proprietary tracking.
* Retain all required licensing and attribution headers.

---

## 4. Reporting Issues
When opening an issue:
* Specify your exact device model (e.g., Xiaomi 14, Redmi Note 13 Pro+, POCO F6).
* Specify your exact HyperOS and Android versions (e.g., HyperOS 2.0 / Android 15, Global vs. CN ROM).
* Attach `adb logcat` output with tags `HyperCapsule` or `HyperCapsuleDebug` if reporting a crash or notification processing issue.
