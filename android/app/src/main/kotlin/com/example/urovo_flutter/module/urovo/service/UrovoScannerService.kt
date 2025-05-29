package com.example.urovo_flutter.module.urovo.service

import android.content.Context
import android.os.Bundle
import com.urovo.sdk.scanner.InnerScannerImpl
import com.urovo.sdk.scanner.listener.ScannerListener
import com.urovo.sdk.scanner.utils.Constant
import io.flutter.plugin.common.EventChannel

///[ScannerService] is a service that handles QR code scanning using the Urovo SDK.
/// It implements the [EventChannel.StreamHandler] interface to manage the event stream for scanning events.
/// It starts the scanner when a listen event is received and handles success, error, timeout, and cancel events.
/// It uses the [InnerScannerImpl] class from the Urovo SDK to perform the actual scanning.
/// The scanner can be configured with a camera type (front or back) and a timeout duration.
class ScannerService(
    private val innerScannerImpl: InnerScannerImpl,
    private val context: Context
) : EventChannel.StreamHandler {

    private var eventSink: EventChannel.EventSink? = null

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
        val map = arguments as? Map<*, *> ?: return
        val camera = map["camera"] as? String ?: "front"
        val timeout = (map["timeout"] as? Int) ?: 30

        val bundle = Bundle().apply {
            putString(Constant.Scankey.upPromptString, "Scan now")
            putString(Constant.Scankey.downPromptString, "")
            putString(Constant.Scankey.title, "QR Scan")
        }
        val cameraId = if (camera == "front") {
            Constant.CameraID.FRONT
        } else {
            Constant.CameraID.BACK
        }

        try {
            innerScannerImpl.startScan(
                context,
                bundle,
                cameraId,
                timeout,
                object : ScannerListener {
                    override fun onSuccess(p0: String?, p1: ByteArray?) {
                        eventSink?.success(p0)
                    }

                    override fun onError(p0: Int, p1: String?) {
                        eventSink?.error("Scanner error", p1, null)
                    }

                    override fun onTimeout() {
                        eventSink?.error("Scanner timeout", "Scanning timed out", null)
                    }

                    override fun onCancel() {
                        eventSink?.endOfStream()
                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            eventSink?.error("Scanner error", e.message, null)
        }

    }

    override fun onCancel(arguments: Any?) {
        eventSink = null
        try {
            innerScannerImpl.stopScan()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}