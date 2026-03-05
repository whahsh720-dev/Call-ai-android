package com.openclaw.callai.pipeline

import android.util.Log
import kotlinx.coroutines.delay

/**
 * Stub: returns a silent 16 kHz mono PCM chunk every call.
 * Replace with real AudioRecord / call-audio capture.
 */
class StubCaptureModule : CaptureModule {

    private val tag = "StubCapture"
    @Volatile private var running = false

    override fun start() {
        running = true
        Log.d(tag, "start()")
    }

    override fun stop() {
        running = false
        Log.d(tag, "stop()")
    }

    override fun captureChunk(): ByteArray? {
        if (!running) return null
        // 320 ms of silence at 16 kHz, 16-bit mono
        return ByteArray(10240)
    }
}
