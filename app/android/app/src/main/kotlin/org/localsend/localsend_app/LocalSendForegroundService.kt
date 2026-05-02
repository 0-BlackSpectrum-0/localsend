package org.localsend.localsend_app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder


class LocalSendForegroundService: Service() {
    
    companion object {
        const val CHANNEL_ID = "localsend_watchdog"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "START"
        const val ACTION_STOP = "STOP"

        fun start(context: Context) {
            val intent = Intent(context, LocalSendForegroundService::class.java)
                .apply { action = ACTION_START }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            }else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, LocalSendForegroundService::class.java)
                .apply { action = ACTION_STOP }
            context.startService(intent)
        }
    }
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                createNotificationChannel()
                startForeground(NOTIFICATION_ID, buildWatchdogNotification())
            }
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "LocalSend Watchdog",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows that localsend is running in background"
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun buildWatchdogNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        }
        val pendingOpenIntent = PendingIntent.getActivity(
            this, 0, openAppIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopServiceIntent = Intent(this, LocalSendForegroundService::class.java).apply {
            action = ACTION_STOP
        }

        val pendingStopIntent = PendingIntent.getActivity(
            this, 1, stopServiceIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Watchdog is running")
            .setContentText("Ready to receive files on your local network")
            .setSmallIcon(R.mipmap.ic_launcher_quicktile_foreground)
            .setContentIntent(pendingOpenIntent)
            .setOngoing(true)
            .setShowWhen(false)
            .addAction(
                Notification.Action.Builder(
                    null,
                    "Turn Off",
                    pendingStopIntent
                ).build()
            )
            .build()
    }
}