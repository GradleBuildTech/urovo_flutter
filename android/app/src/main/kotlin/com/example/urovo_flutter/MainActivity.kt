package com.example.urovo_flutter;

import android.os.Bundle
import android.os.PersistableBundle
import com.example.urovo_flutter.service.BeeperService
import com.example.urovo_flutter.service.PrintingService
import com.example.urovo_flutter.service.ScannerService
import com.example.urovo_flutter.utils.ChannelTag
import com.urovo.sdk.beeper.BeeperImpl
import com.urovo.sdk.print.PrinterProviderImpl
import com.urovo.sdk.scanner.InnerScannerImpl
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : FlutterActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            ChannelTag.CHANNEL
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                PrintingService.METHOD_PRINT -> {
                    val printService = PrintingService(PrinterProviderImpl.getInstance(this))
                    printService.onStart(
                        errorCallBack = ({
                            result.error("print error", it, null)
                        })
                    )
                }

                BeeperService.METHOD_BEEP -> {
                    val beeperService = BeeperService(BeeperImpl.getInstance())
                    beeperService.onStart(call.arguments, errorCallBack = ({
                        result.error("beep error", it, null)
                    }))
                }
            }
        }

        EventChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            ChannelTag.SCANNER_CHANNEL
        ).setStreamHandler(ScannerService(
            innerScannerImpl = InnerScannerImpl.getInstance(this),
            context = this
        ))
    }


    private fun startPrint() {
        CoroutineScope(Dispatchers.IO).launch {

        }
    }
}
