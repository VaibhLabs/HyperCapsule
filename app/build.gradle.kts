import java.util.Properties
import java.io.FileInputStream
import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.21"
}

// ---------------------------------------------------------------------------
// Secure Release Signing Configuration
// Credentials are read from Environment Variables or untracked keystore.properties.
// NO passwords or keystore secrets are hardcoded in source control.
// ---------------------------------------------------------------------------
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    FileInputStream(keystorePropertiesFile).use { stream ->
        keystoreProperties.load(stream)
    }
}

val releaseStoreFilePath = System.getenv("HYPERCAPSULE_STORE_FILE")
    ?: keystoreProperties.getProperty("storeFile")
    ?: (project.findProperty("HYPERCAPSULE_STORE_FILE") as? String)

val releaseStorePassword = System.getenv("HYPERCAPSULE_STORE_PASSWORD")
    ?: keystoreProperties.getProperty("storePassword")
    ?: (project.findProperty("HYPERCAPSULE_STORE_PASSWORD") as? String)

val releaseKeyAlias = System.getenv("HYPERCAPSULE_KEY_ALIAS")
    ?: keystoreProperties.getProperty("keyAlias")
    ?: (project.findProperty("HYPERCAPSULE_KEY_ALIAS") as? String)

val releaseKeyPassword = System.getenv("HYPERCAPSULE_KEY_PASSWORD")
    ?: keystoreProperties.getProperty("keyPassword")
    ?: (project.findProperty("HYPERCAPSULE_KEY_PASSWORD") as? String)

val resolvedStoreFile = releaseStoreFilePath?.let { path ->
    val f = File(path)
    if (f.isAbsolute) {
        f
    } else {
        val rootFile = rootProject.file(path)
        if (rootFile.exists()) rootFile else file(path)
    }
}
val hasReleaseSigning = resolvedStoreFile != null &&
    resolvedStoreFile.exists() &&
    !releaseStorePassword.isNullOrBlank() &&
    !releaseKeyAlias.isNullOrBlank() &&
    !releaseKeyPassword.isNullOrBlank()

android {
    namespace = "com.hypercapsule.app"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.hypercapsule.app"
        minSdk = 35
        targetSdk = 37
        versionCode = 34
        versionName = "1.00 Stable"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (hasReleaseSigning) {
                storeFile = resolvedStoreFile
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = null
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            optimization {
                enable = true // Enables code and resource optimizations.
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
        aidl = true
    }
}

// Fail release packaging if signing is requested but unconfigured
tasks.matching { it.name == "packageRelease" }.configureEach {
    doFirst {
        if (!hasReleaseSigning) {
            throw GradleException(
                "Release build requested but signing credentials are not configured!\n" +
                "Production releases cannot be packaged without valid credentials.\n" +
                "Please configure HYPERCAPSULE_STORE_FILE, HYPERCAPSULE_STORE_PASSWORD, " +
                "HYPERCAPSULE_KEY_ALIAS, and HYPERCAPSULE_KEY_PASSWORD via environment variables " +
                "or an untracked 'keystore.properties' file."
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.palette.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.hyperisland.kit)
    implementation(libs.haze)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)

    // Navigation 3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // Shizuku
    implementation(libs.api)
    implementation(libs.provider)
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}
