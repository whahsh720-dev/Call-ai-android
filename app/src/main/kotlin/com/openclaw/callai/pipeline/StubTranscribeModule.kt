package com.openclaw.callai.pipeline

import android.util.Log
import kotlinx.coroutines.delay

/**
 * Stub: pretends to transcribe after a short delay.
 * Replace with a real speech-to-text engine (e.g. Whisper, Google STT).
 */
class StubTranscribeModule : TranscribeModule {

    private val tag = "StubTranscribe"

    override suspend fun transcribe(audio: ByteArray): String? {
        delay(200) // simulate processing
        val text = if (audio.any { it != 0.toByte() }) "Hello, how can I help?" else null
        Log.d(tag, "transcribe() -> $text")
        return text
    }
}
