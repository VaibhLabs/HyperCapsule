package com.hypercapsule.app.ui.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.LocaleListCompat
import com.hypercapsule.app.R
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSettingsRow
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import com.hypercapsule.app.util.parseBold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    onBack: () -> Unit,
    onSetupClick: () -> Unit,
    onLicensesClick: () -> Unit,
    onBehaviorClick: () -> Unit,
    onGlobalSettingsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onBlocklistClick: () -> Unit,
    onBackupClick: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    var showLanguageDialog by remember { mutableStateOf(false) }

    val appVersion = remember {
        try { context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0.0" }
        catch (_: Exception) { "1.0.0" }
    }

    val appIconBitmap = remember(context) {
        try { context.packageManager.getApplicationIcon(context.packageName).toBitmap().asImageBitmap() }
        catch (_: Exception) { null }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.settings),
                onBackClick = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(spacing.medium))

                // Brand Hero Card
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(shapes.extraLarge)
                        .background(colors.surfaceElevatedGlass)
                        .border(1.dp, colors.borderHighlight, shapes.extraLarge),
                    contentAlignment = Alignment.Center
                ) {
                    if (appIconBitmap != null) {
                        Image(
                            bitmap = appIconBitmap,
                            contentDescription = stringResource(R.string.logo_desc),
                            modifier = Modifier.size(72.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = stringResource(R.string.logo_desc),
                            modifier = Modifier.size(44.dp),
                            tint = colors.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.small))

                Text(
                    text = stringResource(R.string.app_name),
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Text(
                    text = stringResource(R.string.developer_credit),
                    style = typography.labelMedium,
                    color = colors.primary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stringResource(R.string.version_template, appVersion),
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(spacing.large))

                // CONFIGURATION GROUP
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.default)) {
                    HyperCapsuleSectionHeader(title = stringResource(R.string.group_configuration))

                    HyperCapsuleGlassCard(
                        contentPadding = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            HyperCapsuleSettingsRow(
                                title = stringResource(R.string.system_setup),
                                subtitle = stringResource(R.string.system_setup_subtitle),
                                icon = Icons.Default.SettingsSuggest,
                                showChevron = true,
                                onClick = onSetupClick
                            )
                            HorizontalDivider(color = colors.borderSubtle)
                            HyperCapsuleSettingsRow(
                                title = stringResource(R.string.island_behavior),
                                subtitle = stringResource(R.string.limit_strategy),
                                icon = Icons.Default.Tune,
                                showChevron = true,
                                onClick = onBehaviorClick
                            )
                            HorizontalDivider(color = colors.borderSubtle)
                            HyperCapsuleSettingsRow(
                                title = stringResource(R.string.global_settings),
                                subtitle = stringResource(R.string.island_appearance),
                                icon = Icons.Default.Palette,
                                showChevron = true,
                                onClick = onGlobalSettingsClick
                            )
                            HorizontalDivider(color = colors.borderSubtle)
                            HyperCapsuleSettingsRow(
                                title = stringResource(R.string.blocked_terms),
                                subtitle = stringResource(R.string.spoiler_subtitle),
                                icon = Icons.Default.Block,
                                showChevron = true,
                                onClick = onBlocklistClick
                            )
                            HorizontalDivider(color = colors.borderSubtle)
                            HyperCapsuleSettingsRow(
                                title = stringResource(R.string.backup_restore_title),
                                subtitle = stringResource(R.string.backup_section_title),
                                icon = Icons.Default.Save,
                                showChevron = true,
                                onClick = onBackupClick
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.large))

                // ABOUT GROUP
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.default)) {
                    HyperCapsuleSectionHeader(title = stringResource(R.string.group_about))

                    HyperCapsuleGlassCard(
                        contentPadding = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            HyperCapsuleSettingsRow(
                                title = stringResource(R.string.language),
                                subtitle = stringResource(R.string.language_desc),
                                icon = Icons.Default.Language,
                                showChevron = true,
                                onClick = { showLanguageDialog = true }
                            )
                            HorizontalDivider(color = colors.borderSubtle)
                            HyperCapsuleSettingsRow(
                                title = stringResource(R.string.developer),
                                subtitle = stringResource(R.string.developer_subtitle),
                                icon = Icons.Default.Person,
                                showChevron = false,
                                onClick = {
                                    try {
                                        val intent = android.content.Intent(
                                            android.content.Intent.ACTION_VIEW,
                                            "https://vaibhlabs.com".toUri()
                                        )
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                }
                            )
                            HorizontalDivider(color = colors.borderSubtle)
                            HyperCapsuleSettingsRow(
                                title = stringResource(R.string.licenses),
                                subtitle = stringResource(R.string.licenses_subtitle),
                                icon = Icons.Default.Description,
                                showChevron = true,
                                onClick = onLicensesClick
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.medium))

                    // Website-Style Button: Visit VaibhLabs.com
                    HyperCapsuleActionButton(
                        text = stringResource(R.string.visit_vaibhlabs),
                        icon = Icons.Default.Language,
                        onClick = {
                            try {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    "https://vaibhlabs.com".toUri()
                                )
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        },
                        isPrimary = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(spacing.huge))

                Text(
                    text = stringResource(R.string.footer_made_with_love).parseBold(),
                    style = typography.bodySmall,
                    color = colors.textTertiary
                )

                Spacer(modifier = Modifier.height(spacing.extraSmall))

                Text(
                    text = "#ProudToBeIndian",
                    style = typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )

                Spacer(modifier = Modifier.height(spacing.huge))
            }
        }
    }

    if (showLanguageDialog) {
        LanguageSelectorDialog(onDismiss = { showLanguageDialog = false })
    }
}

@Composable
fun LanguageSelectorDialog(onDismiss: () -> Unit) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val typography = LocalHyperCapsuleTypography.current
    val spacing = LocalHyperCapsuleSpacing.current

    val languages = mapOf(
        stringResource(R.string.system_default) to "",
        "Bahasa Indonesia" to "id",
        "Čeština" to "cs",
        "Deutsch" to "de",
        "English" to "en",
        "Español" to "es",
        "Français" to "fr",
        "Italiano" to "it",
        "Magyar" to "hu",
        "日本語" to "ja",
        "Português (BR)" to "pt-BR",
        "Polski" to "pl",
        "Slovenčina" to "sk",
        "繁體中文 (TW)" to "zh-TW",
        "Korean" to "ko",
        "Русский" to "ru",
        "Türkçe" to "tr",
        "Українська" to "uk"
    )
    val currentAppLocales = AppCompatDelegate.getApplicationLocales()
    val initialTag = if (!currentAppLocales.isEmpty) currentAppLocales.toLanguageTags().split(",")[0] else ""
    val bestMatchKey = languages.entries.find { (_, tag) -> tag.isNotEmpty() && initialTag.startsWith(tag) }?.value ?: ""
    var selectedTag by remember { mutableStateOf(bestMatchKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceElevated,
        shape = shapes.large,
        icon = {
            Icon(
                Icons.Default.Language,
                null,
                tint = colors.primary,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text(
                stringResource(R.string.language),
                style = typography.titleMedium,
                color = colors.textPrimary
            )
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                HorizontalDivider(color = colors.borderSubtle)
                Column(
                    modifier = Modifier
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState())
                        .selectableGroup()
                ) {
                    languages.forEach { (name, tag) ->
                        val isSelected = (tag == selectedTag)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .selectable(
                                    selected = isSelected,
                                    onClick = { selectedTag = tag },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = spacing.small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colors.primary,
                                    unselectedColor = colors.textSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(spacing.medium))
                            Text(
                                text = name,
                                style = typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) colors.primary else colors.textPrimary
                            )
                        }
                    }
                }
                HorizontalDivider(color = colors.borderSubtle)
            }
        },
        confirmButton = {
            HyperCapsuleActionButton(
                text = stringResource(R.string.save),
                onClick = {
                    val appLocale = if (selectedTag.isEmpty()) LocaleListCompat.getEmptyLocaleList() else LocaleListCompat.forLanguageTags(selectedTag)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                    onDismiss()
                },
                isPrimary = true
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(android.R.string.cancel),
                    color = colors.textSecondary
                )
            }
        }
    )
}
