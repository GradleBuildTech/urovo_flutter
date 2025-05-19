package com.example.urovo_flutter;

import android.os.Bundle
import android.os.PersistableBundle
import com.example.urovo_flutter.service.PrintingService
import com.example.urovo_flutter.utils.ChannelTag
import com.urovo.sdk.print.PrinterProviderImpl
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity: FlutterActivity() {

    private var printService: PrintingService? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        printService = PrintingService(
            printerProvider = PrinterProviderImpl.getInstance(this)
        )
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, ChannelTag.CHANNEL).setMethodCallHandler {
                call, result ->
            if(call.method == PrintingService.METHOD_PRINT) {
                printService?.onStartPrint()
                result.success("Print started")
            }
        }
    }


    private fun startPrint() {
        CoroutineScope(Dispatchers.IO).launch {

        }
    }
}
