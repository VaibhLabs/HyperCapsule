package com.hypercapsule.app.ui.screens.legal

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.hypercapsule.app.R
import com.hypercapsule.app.data.legal.LegalConfig
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

@Composable
fun ContactSupportScreen(
    onBack: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
    onLicensesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = LocalHyperCapsuleColors.current
    val typography = LocalHyperCapsuleTypography.current
    val spacing = LocalHyperCapsuleSpacing.current
    val shapes = LocalHyperCapsuleShapes.current

    val appVersion = remember {
        try {
            val pkg = context.packageManager.getPackageInfo(context.packageName, 0)
            "${pkg.versionName ?: LegalConfig.VERSION_NAME} (Build ${LegalConfig.VERSION_CODE})"
        } catch (_: Exception) {
            "${LegalConfig.VERSION_NAME} (Build ${LegalConfig.VERSION_CODE})"
        }
    }

    val appIconBitmap = remember(context) {
        try {
            context.packageManager.getApplicationIcon(context.packageName).toBitmap().asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    val deviceDiagnostics = remember {
        """
        Application Version: $appVersion
        Device Model: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})
        Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})
        Build Fingerprint: ${Build.FINGERPRINT}
        """.trimIndent()
    }

    fun launchEmailIntent() {
        val subject = "HyperCapsule Support Request"
        val body = """
            Hello HyperCapsule Support,

            $deviceDiagnostics

            Issue Description:
            [Please write what happened here]

            Steps to Reproduce:
            1.
            2.
            3.

            Thank you.
        """.trimIndent()

        // Construct standard mailto: URI
        val uriString = "mailto:${LegalConfig.SUPPORT_EMAIL}?subject=" +
                Uri.encode(subject) +
                "&body=" +
                Uri.encode(body)

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(uriString)
        }

        try {
            context.startActivity(emailIntent)
        } catch (_: Exception) {
            // Fallback for devices without standard mailto handler
            try {
                val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(LegalConfig.SUPPORT_EMAIL))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, body)
                }
                context.startActivity(Intent.createChooser(fallbackIntent, "Choose Email App"))
            } catch (_: Exception) {
                Toast.makeText(
                    context,
                    "No email app found. Please send an email directly to ${LegalConfig.SUPPORT_EMAIL}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun copyEmailToClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("HyperCapsule Support Email", LegalConfig.SUPPORT_EMAIL)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "Support email copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = "Contact & Support",
                onBackClick = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = spacing.default,
                    end = spacing.default,
                    top = spacing.small,
                    bottom = spacing.huge
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                // Header Hero Card
                item {
                    HyperCapsuleGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = shapes.large,
                        contentPadding = spacing.default
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(shapes.large)
                                    .background(colors.surfaceElevatedGlass)
                                    .border(1.dp, colors.borderHighlight, shapes.large),
                                contentAlignment = Alignment.Center
                            ) {
                                if (appIconBitmap != null) {
                                    Image(
                                        bitmap = appIconBitmap,
                                        contentDescription = null,
                                        modifier = Modifier.size(56.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.Bolt,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                text = LegalConfig.APP_NAME,
                                style = typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )

                            Text(
                                text = "By ${LegalConfig.DEVELOPER_NAME}",
                                style = typography.labelMedium,
                                color = colors.primary
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Version: $appVersion",
                                style = typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }

                // Support Description Card
                item {
                    HyperCapsuleGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = shapes.large,
                        contentPadding = spacing.default
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SupportAgent,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(spacing.small))
                                Text(
                                    text = "Dedicated Help & Assistance",
                                    style = typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                text = "We are dedicated to providing a premium, privacy-first dynamic capsule experience. If you encounter bugs, device cutout alignment issues, or have feature ideas, reach out to our team.",
                                style = typography.bodyMedium,
                                color = colors.textSecondary,
                                lineHeight = 20.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Email Support Action Card
                item {
                    HyperCapsuleGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = shapes.large,
                        contentPadding = spacing.default
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Email Support",
                                style = typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(spacing.extraSmall))

                            Text(
                                text = "Tapping 'Send Support Email' creates a draft in your preferred email client pre-filled with technical device diagnostics to help us diagnose your issue swiftly.",
                                style = typography.bodyMedium,
                                color = colors.textSecondary,
                                lineHeight = 20.sp,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                text = "Support Desk: ${LegalConfig.SUPPORT_EMAIL}",
                                style = typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(spacing.medium))

                            HyperCapsuleActionButton(
                                text = "Send Support Email",
                                icon = Icons.Rounded.Email,
                                onClick = { launchEmailIntent() },
                                isPrimary = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(spacing.small))

                            HyperCapsuleActionButton(
                                text = "Copy Support Email",
                                icon = Icons.Rounded.ContentCopy,
                                onClick = { copyEmailToClipboard() },
                                isPrimary = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Related Legal Shortcuts
                item {
                    HyperCapsuleGlassCard(
                        contentPadding = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                        shape = shapes.large
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPrivacyClick() }
                                    .padding(spacing.default),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Security,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(spacing.medium))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Privacy Policy",
                                        style = typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "100% offline, on-device data processing notice",
                                        style = typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = colors.textTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            HorizontalDivider(color = colors.borderSubtle)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTermsClick() }
                                    .padding(spacing.default),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Gavel,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(spacing.medium))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Terms of Use",
                                        style = typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "Application terms, warranties and liabilities",
                                        style = typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = colors.textTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            HorizontalDivider(color = colors.borderSubtle)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onLicensesClick() }
                                    .padding(spacing.default),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Description,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(spacing.medium))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Open Source Licenses",
                                        style = typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "Third-party libraries and Apache 2.0 notices",
                                        style = typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = colors.textTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
