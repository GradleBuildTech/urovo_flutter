package com.example.urovo_flutter.module.urovo

import android.content.Context
import com.example.urovo_flutter.module.BaseModule
import com.example.urovo_flutter.module.urovo.service.UrovoBeeperService
import com.example.urovo_flutter.module.urovo.service.UrovoPrintingService
import com.example.urovo_flutter.module.urovo.service.emv.UrovoEmvService
import com.example.urovo_flutter.module.urovo.service.stream.ScannerService
import com.example.urovo_flutter.module.urovo.service.stream.UrovoMagCardService
import com.urovo.i9000s.api.emv.EmvNfcKernelApi
import com.urovo.sdk.beeper.BeeperImpl
import com.urovo.sdk.magcard.MagCardReaderImpl
import com.urovo.sdk.print.PrinterProviderImpl
import com.urovo.sdk.scanner.InnerScannerImpl
import io.flutter.plugin.common.EventChannel

class UrovoModule : BaseModule() {

    override fun scannerStream(context: Context): EventChannel.StreamHandler {
        return ScannerService(
            context = context, // Context should be passed from the activity or application context
            innerScannerImpl = InnerScannerImpl.getInstance(context),
        )
    }

    override fun searchMagCardStream(context: Context): EventChannel.StreamHandler {
        return UrovoMagCardService(magCardReaderImpl = MagCardReaderImpl.getInstance())
    }

    override fun emvStream(context: Context): EventChannel.StreamHandler {
        return UrovoEmvService(
            context = context,
            mEmvNfcKernelApi = EmvNfcKernelApi.getInstance()
        )
    }

    override fun printMethod(context: Context, argument: Any?, errorCallBack: (String) -> Unit) {
        val printService = UrovoPrintingService(PrinterProviderImpl.getInstance(context))
        printService.onStart(arg = argument, errorCallBack = errorCallBack)
    }

    override fun beepMethod(context: Context, argument: Any, errorCallBack: (String) -> Unit) {
        val beeperService = UrovoBeeperService(BeeperImpl.getInstance())
        beeperService.onStart(argument, errorCallBack)
    }
}