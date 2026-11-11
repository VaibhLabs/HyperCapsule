package com.hypercapsule.app.ui.screens.theme

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.data.theme.ThemeRepository
import com.hypercapsule.app.models.theme.HyperTheme
import com.hypercapsule.app.ui.designsystem.components.ButtonVariant
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.theme.HyperCapsuleTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File

class ThemeInstallerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = ThemeRepository(applicationContext)
        val prefs = AppPreferences(applicationContext)
        val dataUri: Uri? = intent?.data

        setContent {
            HyperCapsuleTheme {
                InstallerScreen(
                    uri = dataUri,
                    repo = repo,
                    prefs = prefs,
                    onFinish = { finish() }
                )
            }
        }
    }
}

@Composable
fun InstallerScreen(
    uri: Uri?,
    repo: ThemeRepository,
    prefs: AppPreferences,
    onFinish: () -> Unit
) {
    var installState by remember { mutableStateOf<InstallState>(InstallState.Idle) }
    var installedTheme by remember { mutableStateOf<HyperTheme?>(null) }
    val scope = rememberCoroutineScope()
    val shapes = LocalHyperCapsuleShapes.current

    LaunchedEffect(uri) {
        if (uri == null) {
            installState = InstallState.Error
            return@LaunchedEffect
        }

        installState = InstallState.Installing

        try {
            val themeId = repo.installThemeFromUri(uri)
            val themeFile = File(repo.getThemesDir(), "$themeId/theme_config.json")
            val json = Json { ignoreUnknownKeys = true }
            val theme = json.decodeFromString<HyperTheme>(themeFile.readText())

            installedTheme = theme
            installState = InstallState.Success(themeId)

        } catch (e: Exception) {
            e.printStackTrace()
            installState = InstallState.Error
        }
    }

    Dialog(
        onDismissRequest = onFinish,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        HyperCapsuleGlassCard(
            shape = shapes.squircleLarge,
            modifier = Modifier.width(330.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (val state = installState) {
                    is InstallState.Idle, is InstallState.Installing -> {
                        LoadingContent()
                    }
                    is InstallState.Success -> {
                        SuccessContent(
                            theme = installedTheme,
                            onApply = {
                                scope.launch {
                                    prefs.setActiveThemeId(state.themeId)
                                    repo.activateTheme(state.themeId)
                                    onFinish()
                                }
                            },
                            onCancel = onFinish
                        )
                    }
                    is InstallState.Error -> {
                        ErrorContent(onClose = onFinish)
                    }
                }
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
private fun LoadingContent() {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(shapes.squircleMedium)
            .background(colors.accentPrimary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = colors.accentPrimary,
            modifier = Modifier.size(32.dp),
            strokeWidth = 3.dp
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(R.string.theme_installer_analyzing),
        style = MaterialTheme.typography.bodyLarge,
        color = colors.textPrimary,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun SuccessContent(
    theme: HyperTheme?,
    onApply: () -> Unit,
    onCancel: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(shapes.squircleMedium)
            .background(colors.accentPrimary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Palette,
            contentDescription = null,
            tint = colors.accentPrimary,
            modifier = Modifier.size(36.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(R.string.theme_installer_success_title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = colors.textPrimary
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = theme?.meta?.name ?: "Unknown Theme",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = colors.accentPrimary
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.theme_installer_author, theme?.meta?.author ?: "Unknown"),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )
        Text(
            text = " • ",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )
        Text(
            text = stringResource(R.string.theme_installer_version, theme?.meta?.version ?: 1),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(R.string.theme_installer_apply_question),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = colors.textSecondary
    )

    Spacer(modifier = Modifier.height(24.dp))

    Column(modifier = Modifier.fillMaxWidth()) {
        HyperCapsuleActionButton(
            text = stringResource(R.string.theme_installer_action_apply),
            icon = Icons.Rounded.CheckCircle,
            onClick = onApply,
            variant = ButtonVariant.PRIMARY,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        HyperCapsuleActionButton(
            text = stringResource(R.string.theme_installer_action_later),
            onClick = onCancel,
            variant = ButtonVariant.OUTLINE,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ErrorContent(onClose: () -> Unit) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(shapes.squircleMedium)
            .background(colors.accentError.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Error,
            contentDescription = null,
            tint = colors.accentError,
            modifier = Modifier.size(36.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(R.string.theme_installer_error_title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = colors.textPrimary
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(R.string.theme_installer_error_generic),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = colors.textSecondary
    )

    Spacer(modifier = Modifier.height(24.dp))

    HyperCapsuleActionButton(
        text = stringResource(R.string.theme_installer_btn_close),
        onClick = onClose,
        variant = ButtonVariant.OUTLINE,
        modifier = Modifier.fillMaxWidth()
    )
}

sealed class InstallState {
    data object Idle : InstallState()
    data object Installing : InstallState()
    data class Success(val themeId: String) : InstallState()
    data object Error : InstallState()
}
