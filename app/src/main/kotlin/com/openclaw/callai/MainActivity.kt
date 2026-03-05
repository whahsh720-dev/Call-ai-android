package com.openclaw.callai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Single-screen UI with Start / Stop buttons that control [CallAiService].
 *
 * Required permissions are requested at runtime on first launch.
 */
class MainActivity : AppCompatActivity() {

    private val requiredPermissions = buildList {
        add(Manifest.permission.READ_PHONE_STATE)
        add(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    private val permissionRequestCode = 42

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        tvStatus = findViewById(R.id.tvStatus)

        btnStart.setOnClickListener { onStartClicked() }
        btnStop.setOnClickListener { onStopClicked() }

        requestMissingPermissions()
    }

    private fun onStartClicked() {
        if (!allPermissionsGranted()) {
            requestMissingPermissions()
            return
        }
        val intent = Intent(this, CallAiService::class.java)
        ContextCompat.startForegroundService(this, intent)
        setStatus("Running — waiting for a call…", running = true)
    }

    private fun onStopClicked() {
        stopService(Intent(this, CallAiService::class.java))
        setStatus("Stopped", running = false)
    }

    private fun setStatus(text: String, running: Boolean) {
        tvStatus.text = text
        btnStart.isEnabled = !running
        btnStop.isEnabled = running
    }

    // ── permissions ───────────────────────────────────────────────────────────

    private fun allPermissionsGranted() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestMissingPermissions() {
        val missing = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing, permissionRequestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (!allGranted) {
                tvStatus.text = "Some permissions denied — features may not work"
            }
        }
    }
}
