package com.openclaw.callai

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log

/**
 * Monitors phone call state changes and notifies via [listener].
 * Requires READ_PHONE_STATE permission.
 */
class CallStateMonitor(private val context: Context) {

    private val tag = "CallStateMonitor"

    enum class State { IDLE, RINGING, OFFHOOK }

    var listener: ((State) -> Unit)? = null

    private val telephonyManager by lazy {
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    @Suppress("DEPRECATION")
    private val phoneStateListener = object : PhoneStateListener() {
        @Deprecated("Deprecated in Java")
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            val mapped = when (state) {
                TelephonyManager.CALL_STATE_RINGING -> State.RINGING
                TelephonyManager.CALL_STATE_OFFHOOK -> State.OFFHOOK
                else -> State.IDLE
            }
            Log.d(tag, "call state -> $mapped")
            listener?.invoke(mapped)
        }
    }

    @Suppress("DEPRECATION")
    fun start() {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        Log.d(tag, "started")
    }

    @Suppress("DEPRECATION")
    fun stop() {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        Log.d(tag, "stopped")
    }
}
