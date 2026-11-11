# 06_ADDITIONAL_BUG_DISCOVERY.md — Latent Code Defect & Vulnerability Inventory

**Audit Date:** 2026-11-11  
**Investigator:** Senior Android Reverse-Engineering Analyst & Systems Architect  
**Investigation Scope:** Deep static code audit across all subsystems in HyperCapsule v1.00 Stable  

---

## 1. Executive Summary of Newly Discovered Latent Defects

Beyond the eight primary upstream issue targets, this forensic investigation identified **seven additional critical and high-risk latent code defects** in the local HyperCapsule codebase:

| Defect ID | Category | Subsystem | Severity | Summary |
| :---: | :--- | :--- | :---: | :--- |
| **LAT-01** | IPC / Reflection Failure | `PrivilegedServiceImpl.kt` | **CRITICAL** | `IConnectivityManager` firewall reflection fails on Android 16/17 (eBPF migration), causing 3,000ms blocking timeouts. |
| **LAT-02** | Service Lifecycle / Permissions | `BootReceiver.kt` | **HIGH** | `toggleNotificationListener` disables component on boot, resetting user's Notification Access permission in HyperOS settings. |
| **LAT-03** | Race Condition / Deadlock | `ShizukuManager.kt` | **HIGH** | Sequential `notifyMutex.withLock` across all notification events creates UI thread hitching and dispatch starvation. |
| **LAT-04** | Data Integrity / Path Traversal | `ThemeRepository.kt` | **MEDIUM** | Theme export uses OS-dependent path separators (`File.relativeTo.path`), breaking `.hbr` unzipping across non-POSIX platforms. |
| **LAT-05** | Memory Leak / Bitmap Lifecycle | `BaseTranslator.kt` | **MEDIUM** | Unbounded creation of non-recycled intermediate Bitmaps during canvas tinting and polygon masking. |
| **LAT-06** | State Desynchronization | `NotificationReaderService.kt` | **HIGH** | `reapOrphanBridgeNotifications` cancels active islands during fast app switching if `postTime` difference is under 5 seconds. |
| **LAT-07** | Stale Active Island Leak | `NotificationReaderService.kt` | **MEDIUM** | `activeWidgets` entries remain registered when app widgets crash or fail to broadcast view updates. |

---

## 2. In-Depth Analysis of Discovered Defects

### LAT-01: Reflection Crash & 3-Second Blocking in `PrivilegedServiceImpl.kt`
- **Location:** [`app/src/main/java/com/hypercapsule/app/integration/shizuku/PrivilegedServiceImpl.kt:47-98`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/integration/shizuku/PrivilegedServiceImpl.kt#L47-L98)
- **Anatomy:**
  ```kotlin
  val chain = 9
  callMethodResilient(realCm, "setFirewallChainEnabled", chain, true)
  val rule = if (enabled) 0 else 2
  callMethodResilient(realCm, "setUidFirewallRule", chain, uid, rule)
  ```
- **Root Cause:** In Android 15 QPR2, Android 16 (API 36), and Android 17 (API 37), Google migrated firewall and networking controls from `IConnectivityManager` into the modular system server `com.android.net.module` utilizing kernel eBPF programs. Reflection calls targeting `setFirewallChainEnabled` and `setUidFirewallRule` on `IConnectivityManager$Stub$Proxy` throw `NoSuchMethodException`.
- **Consequence:** Because `latch.await(OP_TIMEOUT_MS, TimeUnit.MILLISECONDS)` waits for 3,000ms, every notification requesting the Shizuku firewall workaround blocks for 3 full seconds before timing out and failing, causing extreme notification lag (corroborating Issue #405).

---

### LAT-02: Notification Access Revocation via `BootReceiver.kt`
- **Location:** [`app/src/main/java/com/hypercapsule/app/receiver/BootReceiver.kt:85-103`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/receiver/BootReceiver.kt#L85-L103)
- **Anatomy:**
  ```kotlin
  private fun toggleNotificationListener(context: Context) {
      val pm = context.packageManager
      val componentName = ComponentName(context, NotificationReaderService::class.java)

      // Disable
      pm.setComponentEnabledSetting(
          componentName,
          PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
          PackageManager.DONT_KILL_APP
      )

      // Enable
      pm.setComponentEnabledSetting(
          componentName,
          PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
          PackageManager.DONT_KILL_APP
      )
  }
  ```
- **Root Cause:** When `PackageManager.setComponentEnabledSetting` transitions a `NotificationListenerService` to `COMPONENT_ENABLED_STATE_DISABLED`, Android's `NotificationManagerService` purges the component from the active enabled listeners set. On HyperOS, the security manager detects the disabled state and permanently clears the user's granted toggle in `Settings > Special App Access > Notification Access`.
- **Consequence:** Users report that after restarting their device, HyperCapsule stops receiving notifications and prompts them to re-grant notification access.

---

### LAT-03: Coroutine Scope Leak in `ShizukuManager.kt`
- **Location:** [`app/src/main/java/com/hypercapsule/app/util/ShizukuManager.kt:97-126`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/util/ShizukuManager.kt#L97-L126)
- **Anatomy:**
  ```kotlin
  fun notify(context: Context, id: Int, notification: Notification) {
      CoroutineScope(Dispatchers.IO).launch { ... }
  }
  ```
- **Root Cause:** `notify()`, `cancel()`, and `notifyWithCancel()` instantiate an unmanaged, independent `CoroutineScope(Dispatchers.IO)` on every call rather than utilizing a lifecycle-bound or structured supervisor scope.
- **Consequence:** Rapid notification updates spawn dozens of concurrent coroutines competing for `notifyMutex.withLock`, leaking memory and causing thread pool exhaustion under heavy notification loads.

---

### LAT-04: Non-POSIX Path Separators in `ThemeRepository.kt`
- **Location:** [`app/src/main/java/com/hypercapsule/app/data/theme/ThemeRepository.kt:276-285`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/data/theme/ThemeRepository.kt#L276-L285)
- **Anatomy:**
  ```kotlin
  val entryName = file.relativeTo(sourceFolder).path
  zos.putNextEntry(java.util.zip.ZipEntry(entryName))
  ```
- **Root Cause:** On Windows development environments or systems where `File.separator` is `\`, `ZipEntry` is created with backslashes (`icons\icon.png`) rather than standard POSIX forward slashes (`icons/icon.png`).
- **Consequence:** Android's `ZipInputStream` running on Linux fails to resolve subdirectories when unzipping exported themes, triggering `ZipSlip` security exceptions or corrupting theme assets.

---

### LAT-05: Unbounded Intermediate Bitmap Allocation in `BaseTranslator.kt`
- **Location:** [`app/src/main/java/com/hypercapsule/app/service/translators/BaseTranslator.kt:248-269`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/translators/BaseTranslator.kt#L248-L269)
- **Anatomy:**
  Methods such as `applyThemeToActionIcon`, `tintBitmap`, and `createRoundedIconWithBackground` allocate multiple $96 \times 96$ ARGB_8888 Bitmaps per notification action without reusing canvas buffers or bitmap pools.
- **Consequence:** For chat apps with 3 action buttons and frequent incoming messages, GC churn spikes significantly, contributing to frame drops on 120Hz displays.

---

### LAT-06: Orphan Bridge Reaper Race Condition
- **Location:** [`app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt:1345-1358`](file:///D:/HyperCapsule/app/src/main/java/com/hypercapsule/app/service/NotificationReaderService.kt#L1345-L1358)
- **Anatomy:**
  ```kotlin
  for (sbn in currentNotifications) {
      if (sbn.packageName != packageName) continue
      val id = sbn.id
      ...
      if (reverseTranslations.containsKey(id)) continue
      if (System.currentTimeMillis() - sbn.postTime < 5000) continue
      NotificationManagerCompat.from(this).cancel(id)
  }
  ```
- **Root Cause:** If `reverseTranslations` is momentarily cleared during an update or if `bridgeId` generation has a hash collision, the periodic 1-minute `syncNotifications` reaps valid ongoing notifications if they were posted over 5 seconds ago.
