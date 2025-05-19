package com.example.urovo_flutter.service

import android.os.Bundle
import com.urovo.sdk.print.PrinterProviderImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrintingService(
    val printerProvider: PrinterProviderImpl
) {
    private var isPrinting = false

    companion object {
        const val METHOD_PRINT = "print"
    }

    fun onStartPrint() {
        println("onStartPrint")
       CoroutineScope(Dispatchers.IO).launch {
           try {
               if (isPrinting) return@launch
               isPrinting = true
               printerProvider.setGray(0)

               var format = Bundle()

               format = Bundle().apply {
                   putInt("font", 1)
                   putInt("align", 1)
                   putBoolean("fontBold", true)
                   putInt("lineHeight", 10)
               }
               printerProvider.addText(format, "CENTER  CENTERCENTERCENTERCENTERCENTERCENTERCENTER")
               printerProvider.feedLine(-1)
               val result = printerProvider.startPrint()


           } catch (e: Exception) {
               e.printStackTrace()
               isPrinting = false
           }
       }
       isPrinting = false
       printerProvider.close()
    }

}