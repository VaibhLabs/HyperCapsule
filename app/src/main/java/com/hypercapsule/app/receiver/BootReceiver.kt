package com.hypercapsule.app.receiver

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.util.Log
import com.hypercapsule.app.service.NotificationReaderService

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.hypercapsule.app.data.db.AppDatabase

class BootReceiver : BroadcastReceiver() {

    companion object {
        private var lastToggleTime = 0L
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        
        // 1. Determine Importance
        val isMajor = action == Intent.ACTION_BOOT_COMPLETED ||
                action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
                action == Intent.ACTION_MY_PACKAGE_REPLACED ||
                action == "android.intent.action.QUICKBOOT_POWERON"

        val isMinor = action == Intent.ACTION_USER_UNLOCKED ||
                action == Intent.ACTION_USER_PRESENT ||
                action == "android.intent.action.USER_SWITCHED"

        val isTest = action == "com.hypercapsule.app.ACTION_TEST_MIGRATION" ||
                action == "com.d4viddf.hyperbridge.ACTION_TEST_MIGRATION"

        if (!isMajor && !isMinor && !isTest) return

        Log.d("HyperCapsule", "Trigger event detected: $action")

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 2. Migration Logic (Major or Test only)
                if (isMajor || isTest) {
                    Log.d("HyperCapsule", "Major trigger: Performing database migration.")
                    AppDatabase.performMigration(context) { progress ->
                        Log.d("HyperCapsule", "Migration progress: $progress%")
                    }
                    // Trigger AppPreferences initialization to run SharedPreferences migrations
                    com.hypercapsule.app.data.AppPreferences(context)
                }
            } catch (e: Exception) {
                Log.e("HyperCapsule", "Error during migration in BootReceiver", e)
            } finally {
                // 3. Re-bind Logic
                withContext(Dispatchers.Main) {
                    try {
                        val now = System.currentTimeMillis()
                        if (now - lastToggleTime > 5000) {
                            lastToggleTime = now
                            Log.d("HyperCapsule", "Trigger: Requesting official notification listener re-bind.")
                            requestRebind(context)
                        } else {
                            Log.d("HyperCapsule", "Trigger: Cooldown active, skipping re-bind request.")
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }

    private fun requestRebind(context: Context) {
        try {
            val component = ComponentName(context, NotificationReaderService::class.java)
            NotificationListenerService.requestRebind(component)
        } catch (e: Exception) {
            Log.e("HyperCapsule", "Failed to request official re-bind", e)
        }
    }
}