package com.openclaw.callai.pipeline

import android.util.Log
import kotlinx.coroutines.delay

/**
 * Stub: echoes a canned reply.
 * Replace with a real LLM call (e.g. Claude API via Anthropic SDK).
 */
class StubGenerateReplyModule : GenerateReplyModule {

    private val tag = "StubGenerate"

    override suspend fun generate(transcript: String): String? {
        delay(300) // simulate LLM latency
        val reply = "I heard: \"$transcript\". (AI reply placeholder)"
        Log.d(tag, "generate() -> $reply")
        return reply
    }
}
