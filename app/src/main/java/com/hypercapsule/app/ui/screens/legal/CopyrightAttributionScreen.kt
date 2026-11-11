package com.hypercapsule.app.ui.screens.legal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hypercapsule.app.data.legal.LegalConfig

@Composable
fun CopyrightAttributionScreen(
    onBack: () -> Unit
) {
    val document = remember {
        LegalDocument(
            title = "Copyright & Attributions",
            lastUpdated = LegalConfig.EFFECTIVE_DATE,
            summary = "HyperCapsule is developed by ${LegalConfig.DEVELOPER_NAME}. This page documents our copyright notices, open-source heritage, and required legal attributions in strict compliance with the Apache License 2.0.",
            sections = listOf(
                LegalSection(
                    title = "1. Copyright Notice",
                    paragraphs = listOf(
                        "Copyright © 2026 ${LegalConfig.DEVELOPER_NAME}.",
                        "${LegalConfig.APP_NAME}'s original branding, independently created visual design, Red AMOLED aesthetic, original UI elements, liquid glass animation systems, custom configuration screens, and independently developed modifications are attributed to and owned by ${LegalConfig.DEVELOPER_NAME}.",
                        "All third-party components, inherited source files, and libraries remain subject to their respective copyright notices and licenses."
                    )
                ),
                LegalSection(
                    title = "2. Open Source Heritage & Compliance",
                    paragraphs = listOf(
                        "${LegalConfig.APP_NAME} is derived in part from and builds upon open-source software licensed under the Apache License, Version 2.0.",
                        "In compliance with Section 4 of the Apache License 2.0:"
                    ),
                    bulletPoints = listOf(
                        "Original Copyright Retention: All upstream copyright notices and attribution statements have been strictly preserved in inherited source files.",
                        "License Retention: A full copy of the Apache License 2.0 is retained and distributed within this software.",
                        "Modification Notice: Inherited files that were modified, refactored, re-themed, or expanded for HyperCapsule carry clear modification notices.",
                        "Centralized Attribution: Comprehensive third-party attributions, notices, and research references are centralized in the project's root NOTICE file and documented in the Open Source Licenses screen.",
                        "No False Endorsement: Third-party names and trademarks are used solely for attribution and identification, and do not imply endorsement of HyperCapsule."
                    )
                ),
                LegalSection(
                    title = "3. Original VaibhLabs Contributions",
                    paragraphs = listOf(
                        "The following areas represent original design, engineering, and architectural contributions created by ${LegalConfig.DEVELOPER_NAME} for HyperCapsule:"
                    ),
                    bulletPoints = listOf(
                        "Red AMOLED Visual Identity: Custom pure-black AMOLED color palettes, vibrant red accents, and high-contrast accessibility adaptations.",
                        "Liquid Glass Design System: Proprietary glassmorphism cards, blurred scrim overlays, and dynamic elevation borders.",
                        "Enhanced Settings & Navigation: Re-architected multi-screen navigation using Jetpack Navigation 3, dedicated Backup & Restore manager, and granular priority management.",
                        "Offline Legal & Diagnostic Framework: Native offline document readers, robust email intent generators with diagnostic reporting, and production QA systems."
                    )
                ),
                LegalSection(
                    title = "4. Third-Party Open Source Libraries",
                    paragraphs = listOf(
                        "HyperCapsule incorporates open-source libraries that operate strictly offline. For complete licensing terms, copyright holders, and source repository information, please consult the dedicated Open Source Licenses screen in Settings or the root NOTICE file.",
                        "Third-party components include AndroidX Jetpack libraries, Material 3, Haze, Shizuku API, Kotlin Coroutines & Serialization, and Google Gson."
                    )
                ),
                LegalSection(
                    title = "5. Apache License 2.0 Terms Summary",
                    paragraphs = listOf(
                        "Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:",
                        "http://www.apache.org/licenses/LICENSE-2.0",
                        "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License."
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
