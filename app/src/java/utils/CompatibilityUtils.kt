package com.example.aichatassistant.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat

object CompatibilityUtils {
    
    // Handle storage permissions for Android 9/10
    fun getStorageDir(context: Context, type: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Environment.DIRECTORY_DOCUMENTS
        } else {
            ContextCompat.getExternalFilesDirs(context, null).firstOrNull()?.path 
                ?: context.filesDir.path
        }
    }

    // Check if accessibility service is enabled (Android 9+)
    fun isAccessibilityEnabled(context: Context): Boolean {
        val service = "${context.packageName}/.accessibility.WhatsAppMonitorService"
        val setting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return setting?.contains(service) == true
    }

    // Workaround for Android 9 notification channels
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "whatsapp_monitor",
                "WhatsApp Monitor",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors WhatsApp messages"
            }
            
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}