package com.hypercapsule.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AppSetting::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DB_NAME = "hypercapsule_db"
        private const val LEGACY_DB_NAME = "hyperbridge_db"

        fun getDatabase(context: Context): AppDatabase {
            val storageContext = context.createDeviceProtectedStorageContext()

            // Migration logic: Check if legacy DB exists in either DE or CE storage
            val targetPath = storageContext.getDatabasePath(DB_NAME)
            if (!targetPath.exists()) {
                val legacyDePath = storageContext.getDatabasePath(LEGACY_DB_NAME)
                val legacyCePath = context.getDatabasePath(LEGACY_DB_NAME)
                val newCePath = context.getDatabasePath(DB_NAME)

                if (legacyDePath.exists()) {
                    legacyDePath.renameTo(targetPath)
                } else if (legacyCePath.exists()) {
                    storageContext.moveDatabaseFrom(context, LEGACY_DB_NAME)
                    storageContext.getDatabasePath(LEGACY_DB_NAME).renameTo(targetPath)
                } else if (newCePath.exists()) {
                    storageContext.moveDatabaseFrom(context, DB_NAME)
                }
            }

            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    storageContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration(false)
                    .build().also { INSTANCE = it }
            }
        }

        fun performMigration(context: Context, onProgress: (Int) -> Unit) {
            val storageContext = context.createDeviceProtectedStorageContext()
            val targetPath = storageContext.getDatabasePath(DB_NAME)

            if (!targetPath.exists()) {
                val legacyDePath = storageContext.getDatabasePath(LEGACY_DB_NAME)
                val legacyCePath = context.getDatabasePath(LEGACY_DB_NAME)
                val newCePath = context.getDatabasePath(DB_NAME)

                onProgress(10)
                try {
                    if (legacyDePath.exists()) {
                        legacyDePath.renameTo(targetPath)
                        onProgress(100)
                    } else if (legacyCePath.exists()) {
                        storageContext.moveDatabaseFrom(context, LEGACY_DB_NAME)
                        storageContext.getDatabasePath(LEGACY_DB_NAME).renameTo(targetPath)
                        onProgress(100)
                    } else if (newCePath.exists()) {
                        storageContext.moveDatabaseFrom(context, DB_NAME)
                        onProgress(100)
                    } else {
                        onProgress(100) // Fresh install
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    onProgress(-1)
                }
            } else {
                onProgress(100)
            }
        }
    }
}
