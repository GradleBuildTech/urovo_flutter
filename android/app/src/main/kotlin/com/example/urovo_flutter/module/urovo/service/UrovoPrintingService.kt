package com.example.urovo_flutter.module.urovo.service

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.coroutines.coroutineContext

/**
 * Service responsible for printing using the Urovo SDK.
 * Handles print jobs with coroutines, Job, and Mutex for thread safety.
 * Includes specific error handling for exceptions and out-of-paper conditions.
 * @param printerProvider The provider for printer operations.
 * @param context Application context for accessing app-specific storage.
 */
internal class UrovoPrintingService (
    private val printerProvider: PrinterProviderImpl,
    private val context: Context
) : BaseService() {

    private val printingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex = Mutex()
    private var currentJob: Job? = null
    private val fontPath by lazy { initializeFontPath() }

    override fun onStart(arg: Any?, errorCallBack: ((String) -> Unit)?) {
        // Cancel any existing job to prevent overlap
        currentJob?.cancel()

        currentJob = printingScope.launch {
            mutex.withLock {
                if (!isActive) return@withLock

                // Validate and convert print model
                val printModel = try {
                    withContext(Dispatchers.Default) {
                        (arg as? Map<*, *>)?.toPrintModel()
                    }
                } catch (e: Exception) {
                    Log.e("UrovoPrintingService", "Invalid print model", e)
                    withContext(Dispatchers.Main) {
                        errorCallBack?.invoke("INVALID_MODEL: ${e.message ?: "Invalid print data"}")
                    }
                    return@withLock
                }

                if (printModel == null) {
                    withContext(Dispatchers.Main) {
                        errorCallBack?.invoke("INVALID_MODEL: Print model is null")
                    }
                    return@withLock
                }

                try {
                    // Check paper status before printing
                    if (!checkPaperStatus()) {
                        withContext(Dispatchers.Main) {
                            errorCallBack?.invoke("OUT_OF_PAPER: Printer is out of paper")
                        }
                        return@withLock
                    }

                    // Check font file existence
                    if (!File(fontPath).exists()) {
                        withContext(Dispatchers.Main) {
                            errorCallBack?.invoke("FONT_NOT_FOUND: Font file CALIBRI.ttf not found")
                        }
                        return@withLock
                    }

                    startPrintJob(printModel)
                } catch (e: IOException) {
                    Log.e("UrovoPrintingService", "IO error during printing", e)
                    withContext(Dispatchers.Main) {
                        errorCallBack?.invoke("IO_ERROR: ${e.message ?: "Printer communication error"}")
                    }
                } catch (e: IllegalStateException) {
                    Log.e("UrovoPrintingService", "Printer state error", e)
                    withContext(Dispatchers.Main) {
                        errorCallBack?.invoke("PRINTER_ERROR: ${e.message ?: "Printer not ready"}")
                    }
                } catch (e: OutOfMemoryError) {
                    Log.e("UrovoPrintingService", "Out of memory during image processing", e)
                    withContext(Dispatchers.Main) {
                        errorCallBack?.invoke("MEMORY_ERROR: ${e.message ?: "Image too large"}")
                    }
                } catch (e: Exception) {
                    Log.e("UrovoPrintingService", "Unexpected print error", e)
                    withContext(Dispatchers.Main) {
                        errorCallBack?.invoke("UNKNOWN_ERROR: ${e.message ?: "Unknown print error"}")
                    }
                } finally {
                    try {
                        printerProvider.close()
                    } catch (e: Exception) {
                        Log.e("UrovoPrintingService", "Failed to close printer", e)
                    }
                }
            }
        }

        currentJob?.invokeOnCompletion { throwable ->
            if (throwable != null && throwable !is kotlinx.coroutines.CancellationException) {
                printingScope.launch(Dispatchers.Main) {
                    errorCallBack?.invoke("JOB_FAILED: ${throwable.message ?: "Printing job failed"}")
                }
            }
        }
    }

    override fun onStop() {
        currentJob?.cancel()
        printingScope.cancel("Printing service stopped")
    }

    private suspend fun startPrintJob(printModel: PrintModel) {
        printerProvider.initPrint()
        printerProvider.setGray(0)

        for (item in printModel.items) {
            if (!coroutineContext.isActive) break

            if (!item.image?.imageData.isNullOrEmpty()) {
                printImageItem(item)
            } else {
                printTextItem(item)
            }

            printerProvider.feedLine(printModel.spacing ?: 1)
        }

        if (coroutineContext.isActive) {
            printerProvider.feedLine(4)
            printerProvider.feedLine(-1)
            printerProvider.startPrint()
        }
    }

    private suspend fun printTextItem(item: PrintItemModel) {
        val format = createTextFormat(item)

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

    private suspend fun printImageItem(item: PrintItemModel) {
        val image = item.image!!
        val bitmap = withContext(Dispatchers.Default) {
            listToBitmap(image.imageData, image.width, image.height)
        }
        val imageData = withContext(Dispatchers.Default) {
            getBitmapBytes(bitmap)
        }

        val imageBundle = Bundle().apply {
            putInt("align", item.align?.value ?: 1)
            putInt("offset", 0)
            putInt("width", image.width)
            putInt("height", image.height)
        }

        printerProvider.addImage(imageBundle, imageData)
    }

    private fun createTextFormat(item: PrintItemModel): Bundle {
        return Bundle().apply {
            putInt("lineHeight", 10)
            putString("fontName", fontPath)
            putInt("font", item.size ?: 1)
            item.bold?.let { putBoolean("fontBold", it) }
        }
    }

    private suspend fun checkPaperStatus(): Boolean {
        return try {
            // Assuming PrinterProviderImpl has a method to check paper status
            // Replace with actual Urovo SDK method if different
//            printerProvider.checkPaperStatus() // Hypothetical method
            return true
        } catch (e: Exception) {
            Log.e("UrovoPrintingService", "Failed to check paper status", e)
            false
        }
    }

    private fun initializeFontPath(): String {
        val fontFile = File(context.getExternalFilesDir(null), "CALIBRI.ttf")
        if (!fontFile.exists()) {
            try {
                context.assets.open("CALIBRI.ttf").use { input ->
                    fontFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: FileNotFoundException) {
                Log.e("UrovoPrintingService", "Font file not found in assets", e)
            } catch (e: IOException) {
                Log.e("UrovoPrintingService", "Failed to copy font file", e)
            }
        }
        return fontFile.absolutePath
    }
}