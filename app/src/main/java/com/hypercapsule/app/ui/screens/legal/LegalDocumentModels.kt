package com.hypercapsule.app.ui.screens.legal

data class LegalSection(
    val title: String,
    val paragraphs: List<String>,
    val bulletPoints: List<String> = emptyList()
)

data class LegalDocument(
    val title: String,
    val lastUpdated: String,
    val summary: String,
    val sections: List<LegalSection>
)
