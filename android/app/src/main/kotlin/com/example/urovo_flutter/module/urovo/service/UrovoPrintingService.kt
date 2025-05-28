package com.example.urovo_flutter.module.urovo.service

import android.os.Bundle
import com.urovo.sdk.print.PrinterProviderImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class UrovoPrintingService(
    private val printerProvider: PrinterProviderImpl
) : UrovoBaseService() {
     override fun onStart(arg: Any?, errorCallBack: ((String) -> Unit)?) {
        println("onStartPrint")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isRunning) return@launch
                isRunning = true
                printerProvider.setGray(0)

                var format = Bundle()

                format = Bundle().apply {
                    putInt("font", 1)
                    putInt("align", 1)
                    putBoolean("fontBold", true)
                    putInt("lineHeight", 10)
                }
                printerProvider.addText(
                    format,
                    "CENTER  CENTERCENTERCENTERCENTERCENTERCENTERCENTER"
                )
                printerProvider.feedLine(-1)
                val result = printerProvider.startPrint()


            } catch (e: Exception) {
                e.printStackTrace()
                isRunning = false
                errorCallBack?.invoke(e.message ?: "Print error")
            }
        }
         isRunning = false
         printerProvider.close()
    }

    override fun onStop() {
        TODO("Not yet implemented")
    }

}