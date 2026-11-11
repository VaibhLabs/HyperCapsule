# 🤝 Contributing to HyperCapsule

Thank you for considering a contribution to **HyperCapsule**! 🎉 Contributions of code, bug reports, testing, documentation, accessibility feedback and localization are warmly welcome.

This guide explains how to prepare a development environment, report a useful issue, propose a change and submit a pull request.

> 💡 **Before starting:** For large features, permission changes, architectural changes or changes that affect privacy/security, open a [GitHub Discussion](https://github.com/VaibhLabs/HyperCapsule/discussions) first. This helps avoid duplicated work and makes it possible to agree on scope before code is written.

---

## 📑 Contents

- [📜 Code of Conduct](#-code-of-conduct)
- [💡 Ways to Contribute](#-ways-to-contribute)
- [🛠️ Development Setup](#️-development-setup)
- [🏗️ Project Architecture](#️-project-architecture)
- [🎨 Code Style and Engineering Principles](#-code-style-and-engineering-principles)
- [🌿 Branch and Commit Conventions](#-branch-and-commit-conventions)
- [🚀 Pull Request Process](#-pull-request-process)
- [🧪 Testing Requirements](#-testing-requirements)
- [🐛 Bug Reporting](#-bug-reporting)
- [🔒 Security and Privacy-Sensitive Changes](#-security-and-privacy-sensitive-changes)
- [🌐 Translations and Documentation](#-translations-and-documentation)
- [📦 Release Process](#-release-process)
- [⚖️ Licence and Contributions](#️-licence-and-contributions)
- [❓ Questions?](#-questions)

---

## 📜 Code of Conduct

Be respectful, constructive and patient. Critique ideas and code rather than people. Harassment, discrimination, personal attacks, spam and disclosure of another person's private information are not acceptable.

Maintainers may edit, close or remove contributions that do not meet these expectations.

---

## 💡 Ways to Contribute

You can help by:

- 🐛 Fixing reproducible bugs.
- 📱 Improving device and ROM compatibility.
- 🔄 Improving notification translators and supported interaction flows.
- ♿ Improving UI accessibility, layout and usability.
- 🧪 Adding unit and regression tests for existing or new behaviour.
- 📖 Improving documentation and build instructions.
- 🔍 Reviewing open issues and pull requests.
- 🌐 Improving translations and localization.

For feature proposals, describe the user problem, proposed behaviour, affected Android versions/devices and any permission or privacy implications before implementation.

---

## 🛠️ Development Setup

### 📋 Requirements

Use versions compatible with the checked-out Gradle wrapper and Android Gradle Plugin:

- 💻 **Android Studio** (Ladybug / Meerkat or compatible newer release).
- ☕ **JDK 17 or JDK 21**, as supported by the current project toolchain.
- 📦 **Android SDK platforms and build-tools** required by `app/build.gradle.kts`.
- 🌿 **Git**.
- 📱 A physical Android device or emulator for functional testing.

*OEM-dependent features should be tested on the relevant manufacturer firmware where possible. An emulator alone cannot validate every HyperOS, MIUI, Shizuku/Sui or device-specific behaviour.*

### 📥 Clone the Repository

```bash
git clone https://github.com/VaibhLabs/HyperCapsule.git
cd HyperCapsule
```

### 💻 Open the Project

Open the repository root in Android Studio, select the project's supported JDK, allow Gradle sync to finish and resolve any SDK components requested by the build.

### 🧪 Run Unit Tests

Linux / macOS:

```bash
./gradlew :app:testDebugUnitTest
```

Windows PowerShell:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

### 🔨 Build the Debug APK

Linux / macOS:

```bash
./gradlew :app:assembleDebug
```

Windows PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
```

The debug APK is normally generated under `app/build/outputs/apk/debug/`.

---

## 🏗️ Project Architecture

The project is a Kotlin-based Android application using Jetpack Compose. The main areas currently include:

| 📂 Area | 🎯 Responsibility |
|---|---|
| `ui/` | Compose screens, reusable components, navigation and presentation |
| `service/` | Notification listener, overlay/widget services and notification translators |
| `data/` | Preferences, persistence, repositories and data models |
| `integration/` | Shizuku/Sui and privileged system integrations |
| `models/` | Domain models, configuration contracts and serializable data |
| `receiver/` | Broadcast receivers and system event handling |
| `util/` | Shared utility and helper functionality |
| `aidl/` | IPC interfaces used by privileged integrations |

Keep responsibilities separated. Avoid placing persistence, Android system calls or complex business logic directly inside composable UI functions.

---

## 🎨 Code Style and Engineering Principles

### ☕ Kotlin & Compose

- Use Kotlin for application code unless an existing integration requires another language.
- Use Jetpack Compose and the project's established Material 3/design-system components for UI.
- Follow existing naming, formatting and package conventions.
- Prefer small, cohesive functions and clear state ownership.
- Use lifecycle-aware state collection and structured coroutines.
- Avoid blocking the main thread.
- Handle nullable, missing, malformed and manufacturer-specific data safely.
- Keep accessibility in mind: content descriptions, touch target sizes, contrast and dynamic text sizing.

### 🏛️ Architecture

- Reuse existing repositories, ViewModels, services and utility abstractions where appropriate.
- Do not create duplicate managers or parallel implementations without a clear reason.
- Keep notification translation logic isolated from visual rendering.
- Keep Android framework and privileged operations behind suitable integration boundaries.
- Treat Android version and OEM-specific behaviour as variable, not universal.

### 👁️ UI and Visual Consistency

- Preserve HyperCapsule's visual identity and existing design language.
- Reuse established spacing, typography, shapes, colours, motion and glass components.
- A UI pull request should include before/after screenshots or a short recording where practical.
- Test both compact and large displays and relevant orientation changes when the change affects layout.

### 📦 Dependencies

- Prefer AndroidX, Kotlin and existing project dependencies when they solve the problem.
- Explain why a new dependency is necessary.
- Review its licence, maintenance status, permissions and transitive dependencies.
- Do not add advertising, analytics, telemetry or proprietary tracking SDKs without prior maintainer discussion and an explicit privacy review.
- Do not add network access or remote data transmission as an incidental part of an unrelated change.

---

## 🌿 Branch and Commit Conventions

Do not push directly to `main`.

Create a focused branch from the latest `main`:

```bash
git checkout main
git pull origin main
git checkout -b type/short-description
```

Suggested branch prefixes:

- `feat/` — a new feature.
- `fix/` — a bug fix.
- `perf/` — performance improvement.
- `ui/` — user-interface change.
- `docs/` — documentation change.
- `test/` — tests or test infrastructure.
- `refactor/` — code restructuring without intended behaviour change.

Use clear, semantic commit messages:

```text
feat: add configurable capsule spacing
fix: handle missing notification icons
ui: improve theme creator layout
test: cover notification timeout conversion
docs: clarify Windows build steps
```

Keep commits focused. Avoid including generated build output, local IDE state, APK signing keys, credentials or unrelated formatting changes.

---

## 🚀 Pull Request Process

1. 🔍 Check existing issues, discussions and pull requests to avoid duplicate work.
2. 💬 For non-trivial changes, discuss the approach before implementation.
3. 🍴 Fork the repository if you do not have collaborator write access.
4. 🌿 Create a focused branch.
5. 🛠️ Implement the change with appropriate tests and documentation.
6. 🧪 Run the relevant test and build commands (`:app:testDebugUnitTest`).
7. 🔍 Review your own diff and remove debug code, temporary files and accidental generated artifacts.
8. 🚀 Push your branch to your fork.
9. 📝 Open a pull request against `main`.
10. 🤝 Respond constructively to review feedback and update the branch as needed.

### 📝 Pull Request Description

Use this template in your PR:

```markdown
## Summary
Describe what changed and why.

## Related issue
Closes #

## Change type
- [ ] 🐛 Bug fix
- [ ] ✨ Feature
- [ ] 🎨 UI/UX
- [ ] ⚡ Performance
- [ ] 📖 Documentation
- [ ] 🧪 Tests
- [ ] ♻️ Refactor

## Implementation
Describe the approach and important files/components affected.

## Testing
- [ ] `:app:testDebugUnitTest` passes
- [ ] `:app:assembleDebug` passes
- [ ] Tested on a physical device/emulator (describe below)
- [ ] No unrelated behaviour was changed

Device:
Android version:
Manufacturer / ROM:

## UI evidence
Attach before/after screenshots or a recording if the change affects UI.

## Privacy, permissions and dependencies
Describe any new permission, data access, network behaviour, SDK or dependency.
Write "None" if not applicable.

## Checklist
- [ ] My changes are focused and documented.
- [ ] I have reviewed my diff.
- [ ] I have not committed secrets, keystores or generated build output.
- [ ] Required licence and attribution notices are preserved.
```

### 👀 Review Expectations

A pull request may be asked to change or may be declined if it:

- Does not explain its purpose or testing.
- Breaks compilation or existing behaviour.
- Adds unrelated refactoring or generated files.
- Introduces unreviewed network, telemetry, permission or data-handling changes.
- Removes applicable copyright, licence or attribution notices.
- Lacks screenshots for a meaningful UI change.
- Cannot be reproduced or maintained safely.

---

## 🧪 Testing Requirements

At minimum, run these checks for code changes:

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
```

On Windows PowerShell:

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
```

Also:

- 📱 Test the changed feature on a relevant Android version.
- 🔄 Check existing behaviour that could be affected by the change.
- 🛡️ Test permission denial and missing-data paths when relevant.
- 📐 Test orientation, screen size and OEM differences when relevant.
- 📊 Include exact test results; do not report tests as passed if they were not run.

---

## 🐛 Bug Reporting

Before opening a new issue, search existing issues and discussions.

Use the repository's bug report template if available. A useful report includes:

- 🏷️ HyperCapsule version and build.
- 📱 Device manufacturer and exact model.
- 🤖 Android version.
- ⚙️ ROM/firmware name and version (e.g., HyperOS version and Global/CN variant).
- 🔓 Whether the device is rooted and whether Shizuku/Sui is used, if relevant.
- 🛡️ Permissions enabled for HyperCapsule.
- 🔁 Steps to reproduce.
- ✅ Expected behaviour.
- ❌ Actual behaviour.
- ⏱️ Frequency: always, intermittent or once.
- 📸 Screenshots or screen recording, if useful.
- 📋 Relevant `adb logcat` output for crashes or service issues.

### 📋 Collecting Logs

```bash
adb logcat -d -v threadtime > hypercapsule-logcat.txt
```

Only share the relevant time window and relevant entries. Review the file before uploading it. Redact message contents, notification text, phone numbers, account names, tokens, device identifiers and other personal information.

---

## 🔒 Security and Privacy-Sensitive Changes

Notification access and overlay permissions are sensitive Android capabilities. Changes involving them require extra care.

- 🛡️ Do not collect or persist notification content unless the change has been discussed, justified and reviewed.
- 🚫 Do not transmit notification content or other personal data to a remote service as part of an unrelated feature.
- 🔒 Do not add Internet permission, analytics, crash reporting or tracking without prior maintainer review and corresponding privacy documentation.
- 📋 Explain every new permission and its user-facing purpose.
- ⚖️ Follow least-privilege principles.
- 🧱 Do not weaken Android permission checks or bypass platform security restrictions.
- 🔐 Report suspected security vulnerabilities privately to the repository maintainer rather than posting exploit details in a public issue.

---

## 🌐 Translations and Documentation

Documentation improvements are welcome. Keep instructions reproducible and use the actual task names and paths from the current project.

For translations:

- 🔤 Preserve meaning rather than translating technical identifiers.
- 🧩 Keep placeholders and resource keys unchanged.
- ✍️ Avoid machine-generated translations without review.
- 🌍 Follow the existing localization structure and workflow.

---

## 📦 Release Process

Releases are managed by the project maintainer. Community contributors should not publish official APKs or use the maintainer's signing credentials.

A release candidate should be checked for:

- ✅ Successful unit tests and release build.
- 🏷️ Correct application ID, version name and version code.
- 🎯 Correct minimum/target/compile SDK configuration.
- 🛡️ Required permissions and privacy disclosures.
- ⚖️ Licence and attribution files.
- 📱 Device/ROM compatibility notes.
- 🔐 APK signature verification.
- 🔒 SHA-256 checksum.
- 📝 Release notes that match the actual artifact.

The maintainer publishes signed APKs and release notes through GitHub Releases. Never commit the release keystore, signing passwords, tokens or private credentials.

---

## ⚖️ Licence and Contributions

HyperCapsule includes code and components governed by their applicable licences. Review the repository's `LICENSE`, `NOTICE`, `COPYRIGHT.md` and `THIRD_PARTY_NOTICES.md`.

By submitting a contribution, you confirm that:

- 📜 You have the right to submit the work.
- 🛡️ The contribution does not knowingly infringe another party's rights.
- 🧩 You have identified any third-party code, assets or dependencies you introduced.
- ⚖️ You agree that accepted contributions may be distributed under the repository's applicable project licence, subject to existing third-party licences and notices.

Do not remove or rewrite existing copyright notices, licence headers or required attribution.

---

## ❓ Questions?

- 💬 **Discussions:** [GitHub Discussions](https://github.com/VaibhLabs/HyperCapsule/discussions)
- 🐛 **Issues:** [GitHub Issues](https://github.com/VaibhLabs/HyperCapsule/issues)
- 🌐 **Repository:** [HyperCapsule on GitHub](https://github.com/VaibhLabs/HyperCapsule)

Thank you for helping improve HyperCapsule! 🚀
