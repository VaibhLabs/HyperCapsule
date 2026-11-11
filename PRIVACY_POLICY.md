# Privacy Policy for HyperCapsule

**Effective Date:** [EFFECTIVE_DATE: e.g., November 11, 2026]  
**Last Updated:** [EFFECTIVE_DATE: e.g., November 11, 2026]  
**Application Operator / Data Controller:** [LEGAL_OPERATOR_NAME / PROJECT MAINTAINERS]  
**Contact Email:** [CONTACT_EMAIL: e.g., support@hypercapsule.org]  

---

## 1. Introduction & Core Philosophy

HyperCapsule ("we", "our", or "the app") is an open-source utility for Xiaomi, POCO, and Redmi devices running Xiaomi HyperOS. It bridges Android notifications, widgets, and live updates into the HyperIsland pill-shaped cutout UI.

**HyperCapsule is built with an offline-first architecture.** The application does **not** request or hold the Android `INTERNET` permission (`android.permission.INTERNET`). Consequently, HyperCapsule cannot send, upload, or transmit any data off your device over the Internet.

---

## 2. Sensitive Permissions & Data Usage

To provide its functionality on HyperOS, HyperCapsule requests specific Android system permissions. Below is an exhaustive and verified explanation of why each permission is required and how data is handled:

### 2.1 Notification Access (`NotificationListenerService`)
* **Purpose:** Allows HyperCapsule to detect when notifications arrive on your device (e.g., messaging apps, navigation updates, timers, media playback, and downloads) and transform them into the dynamic island layout.
* **Data Handled:** Notification title, text body, action buttons, progress indicators, and app icons.
* **Handling & Retention:** This data is processed **strictly in volatile memory (RAM)** while the notification is active. HyperCapsule does not log, track, or write your notification messages or private chat contents to persistent storage. When a notification is dismissed or times out, it is immediately cleared from memory.

### 2.2 Package Visibility (`android.permission.QUERY_ALL_PACKAGES`)
* **Purpose:** Allows the app to list installed applications on your device in the Library tab so you can choose which apps are bridged to the island and configure per-app behaviors.
* **Handling & Retention:** App package names and labels are cached locally in private app storage (`app_metadata_cache`) so that your settings and priority lists render smoothly even when offline.

### 2.3 Display Over Other Apps (`android.permission.SYSTEM_ALERT_WINDOW`)
* **Purpose:** Required to render the floating interactive Compose window when you tap "Reply" on a notification, allowing you to reply in-place without opening the full application.
* **Handling & Retention:** Text you type into the inline reply field is passed directly to the source application's `RemoteInput` intent. HyperCapsule does not record or store your replies.

### 2.4 Shizuku Integration (`moe.shizuku.manager.permission.API_V23`)
* **Purpose:** An optional, advanced integration for devices where system focus notifications are blocked (such as Chinese HyperOS ROMs). It allows the app to dispatch focus notifications with elevated privileges and briefly toggle network firewall rules for the Xiaomi Service Framework (`com.xiaomi.xmsf`) to bypass vendor interception.
* **Handling & Retention:** Operates entirely locally via inter-process communication (IPC) with the local Shizuku service. No external servers or remote telemetry are involved.

### 2.5 Boot Completed & Wake Lock (`RECEIVE_BOOT_COMPLETED`, `WAKE_LOCK`)
* **Purpose:** Ensures the notification listener and widget manager resume immediately after your device reboots.

---

## 3. Local Data Storage

All data created or modified by HyperCapsule is stored strictly in your device's private sandbox storage:
* **Room Database & Preferences:** Stores your preferences, toggle states, custom keyword blocklists (spoiler protection), app priority order, and widget layout configurations.
* **Themes & Cache:** Custom or imported `.hbr` theme packages and cached app icon bitmaps are stored in the app's internal private directory (`context.filesDir`).
* **Backup Files:** When you use the "Create Backup" feature, an export file (`.hbr`) is generated and saved to a location you select via the system document picker.

---

## 4. Third-Party Services, Analytics & Tracking

* **No Analytics SDKs:** HyperCapsule contains no telemetry, analytics SDKs (e.g., Google Analytics, Firebase Analytics), or user behavior trackers.
* **No Crash Reporting Services:** HyperCapsule contains no remote crash reporting services (e.g., Crashlytics, Sentry). System crashes are handled by Android\'s default operating system mechanisms.
* **No Advertisements:** HyperCapsule contains no advertisements, advertising SDKs, or ad identifiers.

---

## 5. Security & Protection

Because HyperCapsule operates completely offline and lacks network access permissions:
* Your data cannot be intercepted in transit over the network.
* Stored preferences reside exclusively in Android-sandboxed private app storage accessible only by the application (and root/privileged users if rooted).

---

## 6. Children's Privacy

HyperCapsule does not knowingly collect, transmit, or solicit any personal information from children or any other users.

---

## 7. Changes to This Privacy Policy

If this Privacy Policy is updated, the revised policy will be included in subsequent application releases and published in the project documentation repository with an updated revision date.

---

## 8. Contact & Legal Operator Information

For inquiries regarding this Privacy Policy, source code audit, or software licensing:

* **Operator / Project Maintainer:** [LEGAL_OPERATOR_NAME]
* **Contact Email:** [CONTACT_EMAIL]
* **Project Repository:** [PROJECT_REPOSITORY_URL]
