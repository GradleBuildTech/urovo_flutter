package com.example.urovo_flutter.module.urovo

import android.content.Context
import com.example.urovo_flutter.module.BaseModule
import com.example.urovo_flutter.module.urovo.service.UrovoBeeperService
import com.example.urovo_flutter.module.urovo.service.UrovoPrintingService
import com.example.urovo_flutter.module.urovo.service.UrovoScannerService
import com.example.urovo_flutter.utils.ChannelTag
import com.urovo.sdk.beeper.BeeperImpl
import com.urovo.sdk.print.PrinterProviderImpl
import com.urovo.sdk.scanner.InnerScannerImpl
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class UrovoModule: BaseModule() {
    override fun doAction(
        context: Context,
        method: String,
        argument: Any,
        result: MethodChannel.Result
    ) {
        when (method) {
            ChannelTag.PRINT_METHOD -> {
                val printService = UrovoPrintingService(PrinterProviderImpl.getInstance(context))
                printService.onStart(
                    errorCallBack = ({
                        result.error("print error", it, null)
                    })
                )
            }

            ChannelTag.BEEP_METHOD -> {
                val beeperService = UrovoBeeperService(BeeperImpl.getInstance())
                beeperService.onStart(argument, errorCallBack = ({
                    result.error("beep error", it, null)
                }))
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    override fun scannerStream(context: Context): EventChannel.StreamHandler {
        return UrovoScannerService(
            innerScannerImpl = InnerScannerImpl.getInstance(context),
            context = context // Context should be passed from the activity or application context
        )
    }
}