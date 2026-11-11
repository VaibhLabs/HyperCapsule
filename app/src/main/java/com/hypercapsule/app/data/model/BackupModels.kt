package com.hypercapsule.app.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Root Container for HyperCapsule backup data (compatible with .hbr backup archives)
 */
@Serializable
data class HyperCapsuleBackup(
    @SerializedName("metadata") val metadata: BackupMetadata,
    @SerializedName("settings") val settings: List<AppSettingBackup>
)

typealias HyperBridgeBackup = HyperCapsuleBackup

@Serializable
data class BackupMetadata(
    @SerializedName("package_name") val packageName: String,
    @SerializedName("version_code") val versionCode: Int,
    @SerializedName("version_name") val versionName: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("device_model") val deviceModel: String
)

@Serializable
data class AppSettingBackup(
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String
)
