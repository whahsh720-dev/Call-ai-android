package com.openclaw.callai.pipeline

/**
 * Speaks (or plays back) the given reply text to the user.
 */
interface SpeakModule {
    suspend fun speak(reply: String)
}
