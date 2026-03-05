package com.openclaw.callai.pipeline

/**
 * Converts a PCM audio chunk into a text transcript.
 * Returns null when there is not enough audio to produce a result.
 */
interface TranscribeModule {
    suspend fun transcribe(audio: ByteArray): String?
}
