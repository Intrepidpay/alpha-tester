package com.example.aichatassistant.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.aichatassistant.utils.CompatibilityUtils

class WhatsAppMonitorService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        configureForAndroidVersion()
        CompatibilityUtils.createNotificationChannel(this)
        Log.d("Accessibility", "Service connected")
    }

    private fun configureForAndroidVersion() {
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
            
            feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            notificationTimeout = 100
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            
            // Android 9+ specific config
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageNames = arrayOf("com.whatsapp")
            }
            
            // Android 10+ features
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isAccessibilityTool = true
            }
        }
        this.serviceInfo = info
    }

    @TargetApi(Build.VERSION_CODES.Q)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!CompatibilityUtils.isAccessibilityEnabled(this)) return
        
        when (event?.eventType) {
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> 
                handleNotification(event)
            
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> 
                handleContentChange(event)
            
            // Android 10 gesture handling
            AccessibilityEvent.TYPE_GESTURE_DETECTION_STARTED -> 
                Log.d("Gesture", "Detected system gesture")
        }
    }

    // Android 9 compatible notification handling
    private fun handleNotification(event: AccessibilityEvent) {
        try {
            val notification = event.parcelableData as? android.app.Notification
            val extras = notification?.extras ?: return
            
            val sender = extras.getString("android.title") ?: ""
            val message = extras.getString("android.text") ?: ""
            
            if (sender.isNotEmpty() && message.isNotEmpty()) {
                processMessage(sender, message)
            }
        } catch (e: Exception) {
            Log.e("Notification", "Android 9 compatibility error", e)
        }
    }

    override fun onInterrupt() {
        Log.w("Accessibility", "Service interrupted")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("Accessibility", "Service unbound")
        return super.onUnbind(intent)
    }
}