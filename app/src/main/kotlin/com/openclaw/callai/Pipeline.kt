package com.openclaw.callai

import android.util.Log
import com.openclaw.callai.pipeline.CaptureModule
import com.openclaw.callai.pipeline.GenerateReplyModule
import com.openclaw.callai.pipeline.SpeakModule
import com.openclaw.callai.pipeline.TranscribeModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Wires CaptureModule → TranscribeModule → GenerateReplyModule → SpeakModule
 * into a continuous loop running on the IO dispatcher.
 */
class Pipeline(
    private val capture: CaptureModule,
    private val transcribe: TranscribeModule,
    private val generate: GenerateReplyModule,
    private val speak: SpeakModule,
) {
    private val tag = "Pipeline"
    private var job: Job? = null

    fun start(scope: CoroutineScope) {
        capture.start()
        job = scope.launch(Dispatchers.IO) {
            Log.d(tag, "Pipeline loop started")
            while (isActive) {
                val audio = capture.captureChunk()
                if (audio == null) {
                    delay(50)
                    continue
                }

                val transcript = transcribe.transcribe(audio) ?: continue
                val reply = generate.generate(transcript) ?: continue
                speak.speak(reply)
            }
            Log.d(tag, "Pipeline loop ended")
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        capture.stop()
        Log.d(tag, "Pipeline stopped")
    }
}
