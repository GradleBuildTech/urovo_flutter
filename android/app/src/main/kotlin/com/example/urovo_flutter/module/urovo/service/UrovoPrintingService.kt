package com.example.urovo_flutter.module.urovo.service

import android.os.Bundle
import android.os.Environment
import com.example.urovo_flutter.model.toPrintModel
import com.urovo.sdk.print.PrinterProviderImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * UrovoPrintingService is responsible for handling printing operations using the Urovo SDK.
 * It initializes the printer, processes the print model, and manages the printing workflow.
 * It extends the UrovoBaseService to provide a structured service implementation.
 * @property printerProvider An instance of PrinterProviderImpl to interact with the printer.
 * @constructor Creates an instance of UrovoPrintingService with the specified printer provider.
 * @param printerProvider An instance of PrinterProviderImpl to interact with the printer.
 * This service runs in a coroutine to handle asynchronous printing operations.
 */
internal class UrovoPrintingService(
    private val printerProvider: PrinterProviderImpl
) : UrovoBaseService() {

    /// Flag to indicate if the printing process is currently running.
    override fun onStart(arg: Any?, errorCallBack: ((String) -> Unit)?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isRunning) return@launch

                val printModel = if (arg is Map<*, *>) arg.toPrintModel() else null
                if (printModel == null) {
                    errorCallBack?.invoke("Invalid print model")
                    return@launch
                }


                isRunning = true
                printerProvider.initPrint()
                val status = printerProvider.status

                printerProvider.setGray(0)

                val fontPath =
                    Environment.getExternalStorageDirectory().path + "/CALIBRI.ttf"

                for (item in printModel.items) {
                    val format = Bundle().apply {
                        putInt("lineHeight", 10)
                        putString("fontName", fontPath)
                        putInt("font", item.size ?: 1)
                        item.bold?.let { putBoolean("fontBold", it) }
                    }


                    when {
                        item.qrCode != null -> {
                            printerProvider.addQrCode(Bundle().apply {
                                putInt("align", item.align?.value ?: 1)
                                putInt("offset", -1)
                                putInt("expectedHeight", 300)
                            }, item.qrCode)
                        }
                        item.textCenter == null && item.textRight == null -> {
                            format.putInt("align", item.align?.value ?: 1)
                            printerProvider.addText(format, item.textLeft)
                        }
                        item.textCenter == null -> {
                            printerProvider.addTextLeft_Right(
                                format,
                                item.textLeft,
                                item.textRight.orEmpty()
                            )
                        }
                        else -> {
                            printerProvider.addTextLeft_Center_Right(
                                format,
                                item.textLeft,
                                item.textCenter,
                                item.textRight.orEmpty()
                            )
                        }
                    }
                    // Additional spacing from print model or default 1 line
                    printModel.spacing?.let {
                        printerProvider.feedLine(it)
                    } ?: printerProvider.feedLine(1)
                }

                printerProvider.feedLine(4)
                printerProvider.feedLine(-1)
                printerProvider.startPrint()
                printerProvider.close()


                // Consider logging or handling the print result here if needed

            } catch (e: Exception) {
                e.printStackTrace()
                errorCallBack?.invoke(e.message ?: "Print error")
            } finally {
                // Always reset running state and close printer connection
                isRunning = false
                printerProvider.close()
            }
        }
    }

    override fun onStop() {
        // Implement cleanup if needed
    }
}
