# Building HyperCapsule

This document outlines the prerequisites, environment setup, and Gradle build instructions for compiling HyperCapsule from source.

---

## 1. Prerequisites

- **Operating System:** Windows 10/11, macOS, or Linux.
- **Java Development Kit (JDK):** **JDK 17** or **JDK 21** (JDK 21 LTS recommended).
- **Android SDK:**
  - `compileSdk`: 36 (Android 16 preview)
  - `minSdk`: 35 (Android 15)
  - `targetSdk`: 37
  - Build Tools: 35.0.0+
- **Gradle:** Version 9.5.0 (managed automatically via `gradlew`).

---

## 2. Environment Setup

### 2.1 Set JAVA_HOME
Ensure that your `JAVA_HOME` environment variable points to your JDK 17/21 installation:

**PowerShell (Windows):**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

**Bash (Linux / macOS):**
```bash
export JAVA_HOME=/path/to/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
```

### 2.2 Configure Android SDK
Create or update `local.properties` in the root project directory:
```properties
sdk.dir=C:\\Users\\<your-username>\\AppData\\Local\\Android\\Sdk
```

---

## 3. Build Commands

### 3.1 Verify Configuration
Dry-run the tasks to verify plugins and toolchain configuration:
```powershell
.\gradlew tasks --dry-run
```

### 3.2 Assemble Debug APK
Compile the debug build:
```powershell
.\gradlew assembleDebug
```
The output APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### 3.3 Assemble Release APK
Compile the minified and optimized release build:
```powershell
.\gradlew assembleRelease
```
The output APK is generated at:
`app/build/outputs/apk/release/app-release.apk`

### 3.4 Run Unit Tests
Execute local JUnit unit tests:
```powershell
.\gradlew test
```

---

## 4. ProGuard / R8 Optimization

HyperCapsule uses R8 code shrinking and optimization in release builds. ProGuard keep rules for serialization models, Room entities, AIDL interfaces, and Shizuku IPC are configured in `app/proguard-rules.pro`.
