package com.example.urovo_flutter;

import com.example.urovo_flutter.utils.ChannelTag
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    private var appService: AppService = AppService.getInstance()

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            ChannelTag.CHANNEL
        ).setMethodCallHandler { call, result ->
            appService.doAction(
                context = this,
                method = call.method,
                argument = call.arguments,
                result = result
            )
        }

        EventChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            ChannelTag.SCANNER_CHANNEL
        ).setStreamHandler(
           appService.listenScannerStream(context = this)
        )
    }

}
