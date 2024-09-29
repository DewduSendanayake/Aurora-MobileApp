package com.dewdu.aurora

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TimerService : Service() {

    private val CHANNEL_ID = "timer_service_channel"
    private val NOTIFICATION_ID = 1
    private var timer: CountDownTimer? = null
    private var isTimerRunning = false
    private var focusTimeInMillis: Long = 25 * 60 * 1000 // 25 minutes

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        createNotification()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Timer Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification() {
        val notificationIntent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Timer Running")
            .setContentText("Your timer is active.")
            .setSmallIcon(R.drawable.timer)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Check if the timer is already running
        if (!isTimerRunning) {
            startFocusTimer()
        }
        return START_STICKY
    }

    private fun startFocusTimer() {
        timer = object : CountDownTimer(focusTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                focusTimeInMillis = millisUntilFinished
            }

            override fun onFinish() {
                isTimerRunning = false
                stopSelf() // Stop the service when the timer finishes
            }
        }.start()

        isTimerRunning = true
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
