package com.openclaw.callai.pipeline

/**
 * Generates an AI reply given a user transcript.
 * Returns null when the transcript does not warrant a response.
 */
interface GenerateReplyModule {
    suspend fun generate(transcript: String): String?
}
