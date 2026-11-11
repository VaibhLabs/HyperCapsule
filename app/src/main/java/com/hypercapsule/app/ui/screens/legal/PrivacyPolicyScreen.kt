package com.hypercapsule.app.ui.screens.legal

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.hypercapsule.app.data.legal.LegalConfig
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val colors = LocalHyperCapsuleColors.current
    val typography = LocalHyperCapsuleTypography.current
    val spacing = LocalHyperCapsuleSpacing.current
    val shapes = LocalHyperCapsuleShapes.current

    val document = remember {
        LegalDocument(
            title = "Privacy Policy",
            lastUpdated = LegalConfig.EFFECTIVE_DATE,
            summary = "HyperCapsule is engineered as a strictly offline, privacy-first Android utility developed by ${LegalConfig.DEVELOPER_NAME}. This Privacy Policy provides full transparency regarding how the application handles permissions, notification processing, and device data.",
            sections = listOf(
                LegalSection(
                    title = "1. Introduction & Core Privacy Principles",
                    paragraphs = listOf(
                        "${LegalConfig.APP_NAME} (\"the Application\", \"we\", \"us\", or \"our\"), developed by ${LegalConfig.DEVELOPER_NAME}, is designed to enhance notification usability through dynamic capsule animations on Android devices.",
                        "We believe privacy is a fundamental right. HyperCapsule is architected so that your personal data, communications, and app usage patterns remain exclusively on your physical device. We do not operate external tracking servers, telemetry pipelines, or advertising networks."
                    )
                ),
                LegalSection(
                    title = "2. Information the Application Accesses",
                    paragraphs = listOf(
                        "HyperCapsule accesses only the minimal set of system information necessary to display capsule animations and support dynamic notification overlays:",
                        "All accessed data is transiently held in system memory (RAM) solely during the active display cycle of a capsule and is never permanently logged to disk or exported."
                    ),
                    bulletPoints = listOf(
                        "Installed Application Metadata: Package names, application labels, and app launcher icons to match notifications with their originating application.",
                        "Status Bar Notifications: Incoming alert titles, short text snippets, subtext, and notification icons.",
                        "System State: Battery level, charging status, and audio playback session state when active.",
                        "Device Hardware Profile: Screen cutout geometry and display metrics used solely to align the dynamic capsule."
                    )
                ),
                LegalSection(
                    title = "3. Notification Access Permissions",
                    paragraphs = listOf(
                        "To display notifications in the floating capsule overlay, HyperCapsule requires the Android Notification Listener Service permission (android.permission.BIND_NOTIFICATION_LISTENER_SERVICE).",
                        "Android classifies this as a sensitive permission because it allows an authorized service to inspect incoming system notifications. HyperCapsule uses this permission exclusively for real-time visual presentation."
                    ),
                    bulletPoints = listOf(
                        "No Background Scraping: The service does not scrape or read notifications when the app is disabled or when an app is on your blocklist.",
                        "No Background Interception: The application does not intercept or suppress system notifications unless explicitly configured via Do Not Disturb / Island rules.",
                        "Immediate Lifecycle Release: Notification objects are referenced only for the duration of the notification's visibility."
                    )
                ),
                LegalSection(
                    title = "4. Notification Content Processing",
                    paragraphs = listOf(
                        "When an authorized application posts an alert, HyperCapsule receives a NotificationStatusBarObject containing notification title, body text, and small icon.",
                        "Notification text (such as personal messages, alert bodies, or verification codes) is processed in-memory solely to render the preview text on the dynamic capsule.",
                        "Notification contents are NEVER written to a permanent database, never cached to persistent disk files, and never transmitted outside your device."
                    )
                ),
                LegalSection(
                    title = "5. Shizuku Permissions and Functionality",
                    paragraphs = listOf(
                        "HyperCapsule provides optional integration with the Shizuku framework for elevated system capabilities.",
                        "Shizuku access is entirely optional. The primary notification capsule functions properly without granting Shizuku access.",
                        "When granted, Shizuku is used strictly for elevated local system commands (e.g., toggling Xiaomi HyperOS focus notification firewall rules or managing system UI overlay flags). Shizuku permissions are executed purely on-device and cannot exfiltrate data."
                    )
                ),
                LegalSection(
                    title = "6. Local Device Processing",
                    paragraphs = listOf(
                        "100% of HyperCapsule's computation occurs locally on your physical device hardware.",
                        "Text parsing, keyword blocklist filtering, priority evaluation, island layout calculation, and blur rendering (via Haze) happen directly within your device's CPU and GPU memory space.",
                        "No cloud computing, remote server processing, or edge proxying is utilized at any time."
                    )
                ),
                LegalSection(
                    title = "7. Local Storage & Cached Information",
                    paragraphs = listOf(
                        "HyperCapsule stores only non-sensitive configuration settings inside the private, sandboxed app storage directory managed by the Android operating system:",
                        "Users can manually export an encrypted JSON backup file of their preferences using the Backup & Restore tool. This backup file remains under user control in the device's storage directory."
                    ),
                    bulletPoints = listOf(
                        "User Preferences: Island positioning, capsule width/height offsets, color customizations, and theme toggles (stored in AndroidX DataStore).",
                        "App Priority & Blocklist Rules: User-defined package priorities, muted terms, and blacklisted app identifiers (stored in local Room database).",
                        "No Notification History: Historical notification texts and communication logs are NOT stored in the database."
                    )
                ),
                LegalSection(
                    title = "8. Network Communication",
                    paragraphs = listOf(
                        "HyperCapsule does NOT request the android.permission.INTERNET permission in its AndroidManifest.xml.",
                        "Because the application does not declare the Internet permission, the Android operating system strictly prohibits HyperCapsule from opening network sockets, making HTTP/HTTPS web requests, transmitting packets, or communicating with external web servers.",
                        "You can verify this directly in Android System Settings > Apps > HyperCapsule > Permissions, where Internet access is not present."
                    )
                ),
                LegalSection(
                    title = "9. Third-Party Libraries & SDKs",
                    paragraphs = listOf(
                        "HyperCapsule relies only on reputable, open-source development libraries that operate entirely offline:",
                        "None of the bundled third-party libraries include tracking code, advertising SDKs, or background telemetric beacons."
                    ),
                    bulletPoints = listOf(
                        "AndroidX & Jetpack Compose — Native UI, architecture components, Room database, and DataStore.",
                        "Haze — Local GPU-accelerated glassmorphism blur and noise rendering.",
                        "Shizuku API — Local IPC bridge for elevated Android system management.",
                        "Kotlinx Coroutines & Serialization — Asynchronous task scheduling and JSON backup parser.",
                        "Google Gson — Local JSON serialization helper."
                    )
                ),
                LegalSection(
                    title = "10. Analytics & Crash Reporting",
                    paragraphs = listOf(
                        "HyperCapsule contains ZERO analytics frameworks, ZERO advertising libraries, and ZERO crash reporting beacons.",
                        "We do not use Firebase Analytics, Google Analytics, AppCenter, Sentry, Bugsnag, Flurry, or any proprietary telemetry SDKs.",
                        "If a crash occurs, crash logs are maintained strictly by your Android operating system's local logcat buffer and are not automatically transmitted anywhere."
                    )
                ),
                LegalSection(
                    title = "11. Information Sharing & Third Parties",
                    paragraphs = listOf(
                        "Because HyperCapsule does not collect, receive, or transmit personal data over the internet, we do NOT sell, rent, trade, lease, or share any personal information with third parties, data brokers, advertisers, or government agencies.",
                        "Your notification content, app usage habits, and configuration settings never leave your physical device."
                    )
                ),
                LegalSection(
                    title = "12. Data Security",
                    paragraphs = listOf(
                        "All local configuration preferences are secured by Android's application sandboxing model (UID isolation). Other installed applications cannot access HyperCapsule's internal data directory unless your device is rooted or compromised.",
                        "Notification payloads are kept in volatile RAM and dereferenced promptly when the capsule dismisses, minimizing exposure window."
                    )
                ),
                LegalSection(
                    title = "13. Data Retention",
                    paragraphs = listOf(
                        "Transient notification data: Retained in memory only for the seconds the capsule is active on screen, then garbage collected.",
                        "User configuration data: Retained in your device's local private storage until you clear app data or uninstall the application."
                    )
                ),
                LegalSection(
                    title = "14. Data Deletion",
                    paragraphs = listOf(
                        "You have full autonomy over your data at all times. You can delete all stored preferences and database records by:",
                        "1. Navigating to Android Settings > Apps > HyperCapsule > Storage & cache > Clear Data.",
                        "2. Uninstalling HyperCapsule from your device, which instructs the Android OS to permanently remove all private application directories and files."
                    )
                ),
                LegalSection(
                    title = "15. User Controls & Permission Revocation",
                    paragraphs = listOf(
                        "You may grant or revoke permissions at any time through standard Android system settings:",
                        "Revoking Notification Access disables capsule display for incoming alerts but leaves your saved layout configurations intact."
                    ),
                    bulletPoints = listOf(
                        "Notification Access: Settings > Apps > Special app access > Notification access > HyperCapsule (Toggle On/Off).",
                        "Shizuku Authorization: Open the Shizuku Manager app > Authorized Applications > HyperCapsule (Revoke).",
                        "System Alert Window: Settings > Apps > Special app access > Display over other apps > HyperCapsule (Toggle On/Off)."
                    )
                ),
                LegalSection(
                    title = "16. Children's Privacy",
                    paragraphs = listOf(
                        "HyperCapsule does not address anyone under the age of 13, nor does it knowingly solicit or collect personal information from children.",
                        "Because the application operates entirely offline and collects zero personal data, it poses no data collection risks to children."
                    )
                ),
                LegalSection(
                    title = "17. Changes to this Privacy Policy",
                    paragraphs = listOf(
                        "We may update our Privacy Policy periodically to reflect enhancements or changes in Android platform permissions.",
                        "Any revisions will be published within updated releases of the application with an updated effective date. We encourage users to review this page occasionally."
                    )
                ),
                LegalSection(
                    title = "18. Contact Information",
                    paragraphs = listOf(
                        "If you have questions, concerns, or feedback regarding this Privacy Policy or HyperCapsule's privacy practices, please contact ${LegalConfig.DEVELOPER_NAME} directly at:",
                        "Email: ${LegalConfig.SUPPORT_EMAIL}"
                    )
                )
            )
        )
    }

    LegalDocumentScreen(
        document = document,
        onBack = onBack,
        bottomContent = {
            // Website Launch Option (Section 8: Future Website Support)
            HyperCapsuleGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = shapes.large,
                contentPadding = spacing.default
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Official Web Portal",
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                    Spacer(modifier = Modifier.height(spacing.extraSmall))

                    if (!LegalConfig.WEBSITE_URL.isNullOrBlank()) {
                        Text(
                            text = "View the latest online documentation, announcements, and policy updates on our official website.",
                            style = typography.bodyMedium,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(spacing.medium))
                        HyperCapsuleActionButton(
                            text = "Open Website",
                            icon = Icons.Rounded.Language,
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, LegalConfig.WEBSITE_URL.toUri())
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            isPrimary = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "Website coming soon",
                            style = typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "An official web portal for ${LegalConfig.APP_NAME} is currently in development by ${LegalConfig.DEVELOPER_NAME}. Online policy versions, release notes, and community links will be accessible here once the portal is launched.",
                            style = typography.bodySmall,
                            color = colors.textSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    )
}
