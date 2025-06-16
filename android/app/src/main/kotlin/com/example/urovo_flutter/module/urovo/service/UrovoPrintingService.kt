package com.example.urovo_flutter.module.urovo.service

import android.os.Bundle
import android.os.Environment
import com.example.urovo_flutter.model.PrintItemModel
import com.example.urovo_flutter.model.PrintModel
import com.example.urovo_flutter.model.toPrintModel
import com.example.urovo_flutter.module.BaseService
import com.example.urovo_flutter.utils.getBitmapBytes
import com.example.urovo_flutter.utils.listToBitmap
import com.urovo.sdk.print.PrinterProviderImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.coroutineContext

/**
 * Service responsible for printing using the Urovo SDK.
 * It converts structured print models into printable content.
 * @param printerProvider The provider for printer operations.
 */
internal class UrovoPrintingService(
    private val printerProvider: PrinterProviderImpl
) : BaseService() {

    private val printingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex = Mutex()
    private var currentJob: Job? = null

    override fun onStart(arg: Any?, errorCallBack: ((String) -> Unit)?) {
        currentJob = printingScope.launch {
            mutex.withLock {
                if (!isActive) return@withLock

                val printModel = (arg as? Map<*, *>)?.toPrintModel()
                if (printModel == null) {
                    errorCallBack?.invoke("Invalid print model")
                    return@withLock
                }

                try {
                    startPrintJob(printModel)
                } catch (e: Exception) {
                    e.printStackTrace()
                    errorCallBack?.invoke(e.message ?: "Print error")
                } finally {
                    printerProvider.close()
                }
            }
        }

        currentJob?.invokeOnCompletion { throwable ->
            if (throwable != null) {
                errorCallBack?.invoke(throwable.message ?: "Printing job failed")
            }
        }
    }

    override fun onStop() {
        currentJob?.cancel()
        printingScope.cancel("Printing service stopped")
    }

    // -------------------------------
    // Main print logic entry point
    // -------------------------------
    private suspend fun startPrintJob(printModel: PrintModel) {
        printerProvider.initPrint()
        printerProvider.setGray(0)

        val fontPath = getFontPath()

        for (item in printModel.items) {
            if (!coroutineContext.isActive) break

            if (!item.image?.imageData.isNullOrEmpty()) {
                printImageItem(item)
            } else {
                printTextItem(item, fontPath)
            }

            printerProvider.feedLine(printModel.spacing ?: 1)
        }

        if (coroutineContext.isActive) {
            printerProvider.feedLine(4)
            printerProvider.feedLine(-1)
            printerProvider.startPrint()
        }
    }

    // -------------------------------
    // Text item printer
    // -------------------------------
    private fun printTextItem(item: PrintItemModel, fontPath: String) {
        val format = createTextFormat(item, fontPath)

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
    }

    // -------------------------------
    // Image item printer
    // -------------------------------
    private fun printImageItem(item: PrintItemModel) {
        val image = item.image!!
        val bitmap = listToBitmap(image.imageData, image.width, image.height)
        val imageData = getBitmapBytes(bitmap)

        val imageBundle = Bundle().apply {
            putInt("align", item.align?.value ?: 1)
            putInt("offset", 0)
            putInt("width", image.width)
            putInt("height", image.height)
        }

        printerProvider.addImage(imageBundle, imageData)
    }

    // -------------------------------
    // Helper: Create text formatting bundle
    // -------------------------------
    private fun createTextFormat(item: PrintItemModel, fontPath: String): Bundle {
        return Bundle().apply {
            putInt("lineHeight", 10)
            putString("fontName", fontPath)
            putInt("font", item.size ?: 1)
            item.bold?.let { putBoolean("fontBold", it) }
        }
    }

    // -------------------------------
    // Helper: Font path
    // -------------------------------
    private fun getFontPath(): String {
        return "${Environment.getExternalStorageDirectory().path}/CALIBRI.ttf"
    }
}
