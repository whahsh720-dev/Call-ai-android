package com.openclaw.callai

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.openclaw.callai.pipeline.StubCaptureModule
import com.openclaw.callai.pipeline.StubGenerateReplyModule
import com.openclaw.callai.pipeline.StubSpeakModule
import com.openclaw.callai.pipeline.StubTranscribeModule

/**
 * Foreground service that owns the [Pipeline] and [CallStateMonitor].
 *
 * Start via:  startForegroundService(Intent(context, CallAiService::class.java))
 * Stop via:   stopService(Intent(context, CallAiService::class.java))
 */
class CallAiService : LifecycleService() {

    private val tag = "CallAiService"
    private val channelId = "call_ai_channel"
    private val notificationId = 1

    private lateinit var pipeline: Pipeline
    private lateinit var callStateMonitor: CallStateMonitor

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "onCreate")

        pipeline = Pipeline(
            capture = StubCaptureModule(),
            transcribe = StubTranscribeModule(),
            generate = StubGenerateReplyModule(),
            speak = StubSpeakModule(),
        )

        callStateMonitor = CallStateMonitor(this).apply {
            listener = { state ->
                when (state) {
                    CallStateMonitor.State.OFFHOOK -> pipeline.start(lifecycleScope)
                    CallStateMonitor.State.IDLE -> pipeline.stop()
                    CallStateMonitor.State.RINGING -> Unit // wait for answer
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(notificationId, buildNotification())
        callStateMonitor.start()
        Log.d(tag, "Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        pipeline.stop()
        callStateMonitor.stop()
        Log.d(tag, "Service destroyed")
        super.onDestroy()
    }

    // ── notification ──────────────────────────────────────────────────────────

    private fun buildNotification(): Notification {
        ensureChannel()
        val tapIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Call AI")
            .setContentText("Listening for calls…")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentIntent(tapIntent)
            .setOngoing(true)
            .build()
    }

    private fun ensureChannel() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(channelId) == null) {
            nm.createNotificationChannel(
                NotificationChannel(channelId, "Call AI", NotificationManager.IMPORTANCE_LOW)
            )
        }
    }
}
