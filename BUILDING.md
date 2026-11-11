# 🛠️ Building HyperCapsule

This document outlines the prerequisites, toolchain specifications, environment setup, and Gradle build instructions for compiling **HyperCapsule** from source.

---

## 📋 1. Project Toolchain Specifications

The project configuration defines the following specifications:

| Component | Specification |
|---|---|
| **Application ID** | `com.hypercapsule.app` |
| **Version Name** | `1.00 Stable` |
| **Version Code** | `34` |
| **compileSdk** | `36` (Android 16 preview) |
| **minSdk** | `35` (Android 15) |
| **targetSdk** | `37` |
| **Android Gradle Plugin (AGP)** | `9.3.2` |
| **Kotlin** | `2.3.21` |
| **Gradle Wrapper** | `9.5.0` |
| **Required JDK** | **JDK 17** or **JDK 21** (JDK 21 LTS recommended) |

---

## 💻 2. Prerequisites & Environment Setup

### 2.1 Install Java Development Kit (JDK)
AGP 9.3+ and Gradle 9.5+ require **JDK 17 or later** (JDK 21 LTS is strongly recommended).

Configure your `JAVA_HOME` environment variable to point to your JDK installation:

**PowerShell (Windows):**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"  # Adjust to your JDK path
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

**Bash / Zsh (Linux / macOS):**
```bash
export JAVA_HOME=/path/to/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
```

### 2.2 Configure Android SDK
Create a `local.properties` file in the repository root (this file is excluded by `.gitignore`):

**Windows:**
```properties
sdk.dir=C:\\Users\\<username>\\AppData\\Local\\Android\\Sdk
```

**Linux / macOS:**
```properties
sdk.dir=/Users/<username>/Library/Android/sdk
```

---

## 🔐 3. Release Signing Configuration

HyperCapsule strictly avoids storing signing credentials in source control.

To compile a signed release APK, configure credentials via **environment variables** or a local, untracked **`keystore.properties`** file in the root directory.

### Option A: Environment Variables
```powershell
# Windows PowerShell
$env:HYPERCAPSULE_STORE_FILE = "path/to/hypercapsule-release.jks"
$env:HYPERCAPSULE_STORE_PASSWORD = "your-store-password"
$env:HYPERCAPSULE_KEY_ALIAS = "your-key-alias"
$env:HYPERCAPSULE_KEY_PASSWORD = "your-key-password"
```

```bash
# Linux / macOS
export HYPERCAPSULE_STORE_FILE="/path/to/hypercapsule-release.jks"
export HYPERCAPSULE_STORE_PASSWORD="your-store-password"
export HYPERCAPSULE_KEY_ALIAS="your-key-alias"
export HYPERCAPSULE_KEY_PASSWORD="your-key-password"
```

### Option B: Local `keystore.properties` (Untracked)
Create a file named `keystore.properties` in the project root:
```properties
storeFile=hypercapsule-release.jks
storePassword=your-store-password
keyAlias=your-key-alias
keyPassword=your-key-password
```
*(This file is explicitly ignored in `.gitignore` and must never be committed).*

---

## 🚀 4. Build Commands

### 4.1 Verify Configuration
Dry-run Gradle tasks to verify plugins and toolchain configuration:

**Windows PowerShell:**
```powershell
.\gradlew.bat tasks --dry-run
```

**Linux / macOS:**
```bash
./gradlew tasks --dry-run
```

### 4.2 Run Local Unit Tests
Execute the local JUnit test suite:

**Windows PowerShell:**
```powershell
.\gradlew.bat :app:testDebugUnitTest
```

**Linux / macOS:**
```bash
./gradlew :app:testDebugUnitTest
```

Test reports are generated at:
```text
app/build/reports/tests/testDebugUnitTest/index.html
```

### 4.3 Build Debug APK
Build a debug APK (no release signing required):

**Windows PowerShell:**
```powershell
.\gradlew.bat :app:assembleDebug
```

**Linux / macOS:**
```bash
./gradlew :app:assembleDebug
```

The output APK is generated at:
```text
app/build/outputs/apk/debug/app-debug.apk
```

### 4.4 Build Signed Release APK
Build the minified, ProGuard-optimized production release APK:

**Windows PowerShell:**
```powershell
.\gradlew.bat :app:assembleRelease
```

**Linux / macOS:**
```bash
./gradlew :app:assembleRelease
```

The output release APK is generated at:
```text
app/build/outputs/apk/release/app-release.apk
```

---

## ⚠️ 5. Common Build Issues & Troubleshooting

### Issue 1: `Gradle requires JVM 17 or later to run. Your build is currently configured to use JVM 8.`
* **Cause:** Gradle detected an older Java installation in your system `PATH`.
* **Fix:** Explicitly set `JAVA_HOME` to your JDK 17 or JDK 21 installation before running Gradle:
  ```powershell
  $env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
  .\gradlew.bat :app:assembleDebug
  ```

### Issue 2: `Release build requested but signing credentials are not configured!`
* **Cause:** Running `:app:assembleRelease` without configuring signing environment variables or `keystore.properties`.
* **Fix:** Provide the 4 required signing values as described in [Section 3](#-3-release-signing-configuration).

### Issue 3: `SDK location not found`
* **Cause:** `local.properties` is missing or the `sdk.dir` path is incorrect.
* **Fix:** Create `local.properties` in the project root with the correct path to your Android SDK.

---

## 🛡️ 6. Code Optimization & ProGuard Rules

Release builds enable R8 code shrinking and resource optimization (`isMinifyEnabled = true`, `isShrinkResources = true`).
Custom keep rules for Kotlin serialization, Room entities, Shizuku IPC, and AIDL interfaces are defined in [`app/proguard-rules.pro`](app/proguard-rules.pro).
