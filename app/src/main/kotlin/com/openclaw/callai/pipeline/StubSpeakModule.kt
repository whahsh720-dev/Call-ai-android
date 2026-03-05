package com.openclaw.callai.pipeline

import android.util.Log
import kotlinx.coroutines.delay

/**
 * Stub: logs the reply instead of speaking it.
 * Replace with Android TTS or a real TTS API.
 */
class StubSpeakModule : SpeakModule {

    private val tag = "StubSpeak"

    override suspend fun speak(reply: String) {
        delay(100)
        Log.i(tag, "SPEAK: $reply")
    }
}
