package com.openclaw.callai.pipeline

/**
 * Captures a chunk of audio from the call and returns raw PCM bytes.
 * Implementations should be non-blocking; return null when no audio is ready.
 */
interface CaptureModule {
    fun start()
    fun stop()
    /** Returns a PCM audio chunk, or null if nothing is available yet. */
    fun captureChunk(): ByteArray?
}
