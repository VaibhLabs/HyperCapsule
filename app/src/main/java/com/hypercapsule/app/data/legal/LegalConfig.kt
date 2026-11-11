package com.hypercapsule.app.data.legal

/**
 * Central configuration object for HyperCapsule Legal, Privacy, Copyright and Support.
 * Governs contact details, website integration, effective dates, and upstream attributions.
 */
object LegalConfig {
    const val APP_NAME = "HyperCapsule"
    const val DEVELOPER_NAME = "VaibhLabs"
    const val SUPPORT_EMAIL = "riftarc.official@gmail.com"
    const val EFFECTIVE_DATE = "November 11, 2026"
    const val VERSION_NAME = "1.00 Stable"
    const val VERSION_CODE = 34

    // Configurable website URL (kept null / blank until officially launched).
    // The Privacy Policy page checks this central property: if null or blank,
    // it displays "Website coming soon" in a disabled/informational state.
    // When a valid HTTPS URL is configured, it opens via a secure browser intent.
    val WEBSITE_URL: String? = null

    // Upstream project references (Apache License 2.0 attribution)
    const val UPSTREAM_PROJECT_NAME = "HyperBridge"
    const val UPSTREAM_AUTHOR = "D4vidDf"
    const val UPSTREAM_REPOSITORY_URL = "https://github.com/D4vidDf/HyperBridge"
    const val TOOLKIT_NAME = "HyperIsland-ToolKit"
    const val TOOLKIT_REPOSITORY_URL = "https://github.com/D4vidDf/HyperIsland-ToolKit"
}
