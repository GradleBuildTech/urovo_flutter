package com.example.urovo_flutter.module.urovo.service.stream

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.urovo_flutter.model.toJson
import com.example.urovo_flutter.model.toMagCardInformation
import com.urovo.sdk.magcard.MagCardReaderImpl
import com.urovo.sdk.magcard.listener.MagCardListener
import com.urovo.sdk.utils.DateUtil
import io.flutter.plugin.common.EventChannel
import java.util.Date

/* * UrovoMagCardService.kt
 * This service handles the reading of magnetic cards using the Urovo SDK.
 * It listens for card events and sends the results back through an EventChannel.
 */
class UrovoMagCardService(
    private val magCardReaderImpl: MagCardReaderImpl,
) : EventChannel.StreamHandler {

    @Volatile
    private var eventSink: EventChannel.EventSink? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
        val timeout = (arguments as? Int) ?: 30

        try {
            magCardReaderImpl.searchCard(timeout, object : MagCardListener {

                override fun onSuccess(track: Bundle) {
                    mainHandler.post {
                        eventSink?.let { sink ->
                            val currentTime = DateUtil.formatDate(Date())
                            val magCardInfo = track.toMagCardInformation()
                            val json = magCardInfo.toJson(currentTime)
                            sink.success(json)
                        }
                    }
                }

                override fun onError(error: Int, message: String) {
                    mainHandler.post {
                        eventSink?.let {
                            val errorMsg = "Error code: $error, Message: $message"
                            Log.e("UrovoMagCardService", errorMsg)
                            it.error("Card reading error", errorMsg, null)
                        }
                    }
                }

                override fun onTimeout() {
                    mainHandler.post {
                        eventSink?.error("Card reading timeout", "The card reading operation timed out.", null)
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            mainHandler.post {
                eventSink?.let {
                    Log.e("UrovoMagCardService", "Failed to start MagCardReader: ${e.message}")
                    it.error("Card reading error", "Failed to start MagCardReader: ${e.message}", null)
                }
            }
        }
    }

    override fun onCancel(arguments: Any?) {
        try {
            magCardReaderImpl.stopSearch()
        } catch (e: Exception) {
            e.printStackTrace()
            eventSink?.error("Stop error", "Failed to stop MagCardReader: ${e.message}", null)
        } finally {
            eventSink?.endOfStream()
            eventSink = null
        }
    }
}
