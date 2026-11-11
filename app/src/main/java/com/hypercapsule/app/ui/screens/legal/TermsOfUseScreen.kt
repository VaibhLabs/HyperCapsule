package com.hypercapsule.app.ui.screens.legal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hypercapsule.app.data.legal.LegalConfig

@Composable
fun TermsOfUseScreen(
    onBack: () -> Unit
) {
    val document = remember {
        LegalDocument(
            title = "Terms of Use",
            lastUpdated = LegalConfig.EFFECTIVE_DATE,
            summary = "These Terms of Use (\"Terms\") govern your access to and use of the ${LegalConfig.APP_NAME} mobile application developed by ${LegalConfig.DEVELOPER_NAME}. Please read these terms carefully before utilizing the application.",
            sections = listOf(
                LegalSection(
                    title = "1. Acceptance of Terms",
                    paragraphs = listOf(
                        "By downloading, installing, launching, or using ${LegalConfig.APP_NAME} (\"the Application\"), you agree to be legally bound by these Terms of Use and our Privacy Policy.",
                        "If you do not agree with any part of these Terms, you must discontinue using the Application immediately and uninstall it from your device."
                    )
                ),
                LegalSection(
                    title = "2. Application Usage & License Grant",
                    paragraphs = listOf(
                        "${LegalConfig.DEVELOPER_NAME} grants you a personal, non-exclusive, revocable, non-transferable license to install and run ${LegalConfig.APP_NAME} on compatible Android devices owned or controlled by you.",
                        "HyperCapsule is provided to enhance the Android notification experience through floating capsule indicators, island animations, and shortcut controls. You agree to use the Application only for lawful purposes in accordance with these Terms and applicable local and international laws."
                    )
                ),
                LegalSection(
                    title = "3. User Responsibilities & Acceptable Use",
                    paragraphs = listOf(
                        "As a user of HyperCapsule, you are solely responsible for:",
                        "You agree not to attempt to disassemble, reverse engineer, or bypass the application's internal security sandbox or system permissions outside permitted open-source license rights."
                    ),
                    bulletPoints = listOf(
                        "Configuring application display settings, offsets, and blacklist rules in a manner that does not interfere with critical system accessibility or safety alerts.",
                        "Maintaining the security of your device, backups, and physical access.",
                        "Ensuring that you comply with any third-party app terms when replying to notifications via floating capsule shortcuts."
                    )
                ),
                LegalSection(
                    title = "4. Permissions & Device Compatibility",
                    paragraphs = listOf(
                        "HyperCapsule requires specific Android system permissions (such as Notification Listener Service and Display Over Other Apps) to provide floating dynamic notifications.",
                        "Compatibility may vary across Android manufacturers, hardware specifications, and operating system versions (such as custom vendor ROMs, aggressive battery managers, or custom display cutout configurations).",
                        "We strive for maximum device compatibility, but ${LegalConfig.DEVELOPER_NAME} does not guarantee that the Application will render identically across all customized vendor skins or unverified hardware."
                    )
                ),
                LegalSection(
                    title = "5. Third-Party Software & Integrations",
                    paragraphs = listOf(
                        "The Application may interact with third-party software, including messaging applications, media players, and system utilities such as Shizuku.",
                        "Your use of third-party software remains subject to the respective terms and conditions of those third-party providers. ${LegalConfig.DEVELOPER_NAME} does not control and is not liable for third-party software behavior or system updates that alter third-party APIs."
                    )
                ),
                LegalSection(
                    title = "6. Open-Source Components & Upstream Licensing",
                    paragraphs = listOf(
                        "HyperCapsule incorporates and is distributed with open-source software and components licensed under the Apache License, Version 2.0 and other open-source licenses.",
                        "Nothing in these Terms restricts, modifies, or limits your rights under the applicable open-source licenses covering those specific components. For complete open-source licenses, copyright notices, and source repository information, please consult the Open Source Licenses section within this application and the root NOTICE file."
                    )
                ),
                LegalSection(
                    title = "7. Intellectual Property & Brand Ownership",
                    paragraphs = listOf(
                        "The original name \"HyperCapsule\", the official application logo, original visual artwork, Liquid Glass styling, Red AMOLED theme adaptations, and original source contributions created by ${LegalConfig.DEVELOPER_NAME} are the intellectual property of ${LegalConfig.DEVELOPER_NAME}.",
                        "Upstream source code, third-party trademarks, and library copyrights remain the property of their respective creators and contributors."
                    )
                ),
                LegalSection(
                    title = "8. Updates, Modifications & Discontinuation",
                    paragraphs = listOf(
                        "${LegalConfig.DEVELOPER_NAME} reserves the right to modify, update, patch, or temporarily discontinue features within the Application to enhance performance, ensure compatibility with newer Android platform releases, or correct defects.",
                        "We are under no legal obligation to provide continuous maintenance, guaranteed feature additions, or perpetual backward compatibility with deprecated Android operating system versions."
                    )
                ),
                LegalSection(
                    title = "9. Service Availability & Limitations",
                    paragraphs = listOf(
                        "The Application functions 100% on-device without reliance on external cloud servers.",
                        "However, capsule display and notification bridging rely on the Android OS runtime environment. System battery optimization modes (such as Doze or vendor task killers) or permission revocations may temporarily interrupt notification delivery until re-enabled by the user."
                    )
                ),
                LegalSection(
                    title = "10. Disclaimer of Warranties",
                    paragraphs = listOf(
                        "TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW, THE APPLICATION IS PROVIDED ON AN \"AS IS\" AND \"AS AVAILABLE\" BASIS, WITH ALL FAULTS AND WITHOUT WARRANTY OF ANY KIND.",
                        "${LegalConfig.DEVELOPER_NAME} AND ITS CONTRIBUTORS EXPRESSLY DISCLAIM ALL WARRANTIES, WHETHER EXPRESS, IMPLIED, STATUTORY, OR OTHERWISE, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT.",
                        "WE DO NOT WARRANT THAT THE APPLICATION WILL OPERATE UNINTERRUPTED, SECURE, BUG-FREE, OR COMPATIBLE WITH EVERY COMBINATION OF HARDWARE AND FIRMWARE."
                    )
                ),
                LegalSection(
                    title = "11. Limitation of Liability",
                    paragraphs = listOf(
                        "TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW, IN NO EVENT SHALL ${LegalConfig.DEVELOPER_NAME}, ITS DEVELOPERS, OR UPSTREAM CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, LOSS OF DATA, LOSS OF DEVICE FUNCTIONALITY, DELAY IN NOTIFICATION DISPLAY, OR WORK STOPPAGE) ARISING OUT OF OR IN CONNECTION WITH THE USE OF OR INABILITY TO USE THE APPLICATION, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.",
                        "CERTAIN JURISDICTIONS DO NOT ALLOW THE EXCLUSION OR LIMITATION OF CERTAIN DAMAGES, SO SOME OF THE ABOVE LIMITATIONS MAY NOT APPLY TO YOU UNDER LOCAL LAW."
                    )
                ),
                LegalSection(
                    title = "12. Termination of Use",
                    paragraphs = listOf(
                        "You may terminate these Terms at any time by uninstalling and deleting all copies of the Application from your devices.",
                        "${LegalConfig.DEVELOPER_NAME} reserves the right to terminate your license to use the Application if you violate any provision of these Terms or engage in unauthorized tampering with the software."
                    )
                ),
                LegalSection(
                    title = "13. Changes to Terms",
                    paragraphs = listOf(
                        "We reserve the right to amend or replace these Terms of Use at our discretion.",
                        "Any modifications become effective immediately upon being published in an updated application release. Your continued use of the Application following such changes constitutes your acceptance of the revised Terms."
                    )
                ),
                LegalSection(
                    title = "14. Contact Information",
                    paragraphs = listOf(
                        "If you have questions, inquiries, or legal notices concerning these Terms of Use, please reach out to ${LegalConfig.DEVELOPER_NAME} at:",
                        "Email: ${LegalConfig.SUPPORT_EMAIL}"
                    )
                )
            )
        )
    }

    LegalDocumentScreen(
        document = document,
        onBack = onBack
    )
}
