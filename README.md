# Call AI — Android Skeleton

Minimal Android app that monitors phone calls and runs audio through a
four-stage AI pipeline: **Capture → Transcribe → GenerateReply → Speak**.

## Project structure

```
app/src/main/
├── AndroidManifest.xml
├── kotlin/com/openclaw/callai/
│   ├── MainActivity.kt          # Start / Stop buttons
│   ├── CallAiService.kt         # Foreground service
│   ├── CallStateMonitor.kt      # TelephonyManager wrapper
│   ├── Pipeline.kt              # Wires the four modules in a loop
│   └── pipeline/
│       ├── CaptureModule.kt     # interface — audio capture
│       ├── TranscribeModule.kt  # interface — speech-to-text
│       ├── GenerateReplyModule.kt # interface — LLM reply
│       ├── SpeakModule.kt       # interface — TTS playback
│       ├── StubCaptureModule.kt
│       ├── StubTranscribeModule.kt
│       ├── StubGenerateReplyModule.kt
│       └── StubSpeakModule.kt
└── res/
    ├── layout/activity_main.xml
    └── values/{strings,themes,colors}.xml
```

## Prerequisites

| Tool | Version |
|------|---------|
| Android Studio | Hedgehog 2023.1.1 + |
| JDK | 17 |
| Android SDK | API 35 (compileSdk) |
| Gradle | 8.x (wrapper included) |

## Build

```bash
# debug APK
./gradlew assembleDebug

# install on connected device / emulator
./gradlew installDebug
```

The APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

## Run

1. Connect a device (API 26+) or start an emulator.
2. `./gradlew installDebug` — installs the app.
3. Open **Call AI** on the device.
4. Grant the requested permissions (phone state, microphone, notifications).
5. Tap **Start** — the foreground service starts and the status bar shows a
   persistent notification.
6. Place or receive a phone call — the pipeline activates automatically when
   the call goes off-hook and stops when the call ends.
7. Tap **Stop** to shut the service down manually.

## Replacing stubs

| Stub | Swap with |
|------|-----------|
| `StubCaptureModule` | `AudioRecord` targeting `VOICE_COMMUNICATION` source |
| `StubTranscribeModule` | OpenAI Whisper API, Google Cloud STT, or on-device Whisper |
| `StubGenerateReplyModule` | [Anthropic Claude API](https://docs.anthropic.com/en/api/) via the Kotlin/JVM SDK |
| `StubSpeakModule` | Android `TextToSpeech`, ElevenLabs, or Google Cloud TTS |

Inject your real implementations into `CallAiService.onCreate()` where the
`Pipeline` is constructed.

## Permissions

| Permission | Why |
|------------|-----|
| `READ_PHONE_STATE` | Detect call state changes |
| `RECORD_AUDIO` | Capture microphone audio |
| `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_MICROPHONE` | Run as foreground service with mic |
| `POST_NOTIFICATIONS` | Show the persistent notification (Android 13+) |

## Notes

- `PhoneStateListener` is deprecated on API 31+; replace with
  `TelephonyCallback` when targeting API 31+ exclusively.
- Real call audio capture requires `AudioRecord` with source
  `MediaRecorder.AudioSource.VOICE_COMMUNICATION` and appropriate audio
  routing — the stub returns silent buffers as a placeholder.
- The pipeline loop runs on `Dispatchers.IO`; all module methods must be
  either non-blocking (CaptureModule) or suspending (the rest).
