package com.example.urovo_flutter.module.urovo.service

import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.example.urovo_flutter.model.toPrintModel
import com.example.urovo_flutter.module.BaseService
import com.example.urovo_flutter.utils.getBitmapBytes
import com.example.urovo_flutter.utils.listToBitmap
import com.urovo.sdk.print.PrinterProviderImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Service responsible for printing using the Urovo SDK.
 * It converts structured print models into printable content.
 * @param printerProvider The provider for printer operations.
 */
internal class UrovoPrintingService(
    private val printerProvider: PrinterProviderImpl
) : BaseService() {

    override fun onStart(arg: Any?, errorCallBack: ((String) -> Unit)?) {
        CoroutineScope(Dispatchers.IO).launch {
            // Prevent running multiple print jobs simultaneously
            if (isRunning) return@launch

            // Convert input argument to a structured print model
            val printModel = (arg as? Map<*, *>)?.toPrintModel()
            if (printModel == null) {
                errorCallBack?.invoke("Invalid print model")
                return@launch
            }

            isRunning = true
            try {
                // Initialize printer and set default gray level
                printerProvider.initPrint()
                printerProvider.setGray(0)

                // Custom font path used for text printing
                val fontPath = "${Environment.getExternalStorageDirectory().path}/CALIBRI.ttf"

                // Iterate through each item in the print model
                for (item in printModel.items) {

                    // Handle image printing if image data is available
                    if (!item.image?.imageData.isNullOrEmpty()) {
                        Log.d("UrovoPrintingService", "Printing image: ${item.image?.imageData} bytes")
                        val image = item.image!!
                        val bitmap = listToBitmap(image.imageData, image.width, image.height)
                        val imageData = getBitmapBytes(bitmap)

                        printerProvider.addImage(Bundle().apply {
                            putInt("align", item.align?.value ?: 1)
                            putInt("offset", 0)
                            putInt("width", image.width)
                            putInt("height", image.height)
                        }, imageData)

                    } else {
                        // Create formatting bundle for text
                        val format = Bundle().apply {
                            putInt("lineHeight", 10)
                            putString("fontName", fontPath)
                            putInt("font", item.size ?: 1)
                            item.bold?.let { putBoolean("fontBold", it) }
                        }

                        // Print content based on the presence of QR code or text alignment
                        when {
                            item.qrCode != null -> {
                                // Print QR code
                                printerProvider.addQrCode(Bundle().apply {
                                    putInt("align", item.align?.value ?: 1)
                                    putInt("offset", -1)
                                    putInt("expectedHeight", 300)
                                }, item.qrCode)
                            }

                            item.textCenter == null && item.textRight == null -> {
                                // Print single left-aligned text
                                format.putInt("align", item.align?.value ?: 1)
                                printerProvider.addText(format, item.textLeft)
                            }

                            item.textCenter == null -> {
                                // Print left and right-aligned text
                                printerProvider.addTextLeft_Right(format, item.textLeft, item.textRight.orEmpty())
                            }

                            else -> {
                                // Print left, center, and right-aligned text
                                printerProvider.addTextLeft_Center_Right(
                                    format,
                                    item.textLeft,
                                    item.textCenter,
                                    item.textRight.orEmpty()
                                )
                            }
                        }
                    }

                    // Feed lines between items (custom spacing or default to 1 line)
                    printerProvider.feedLine(printModel.spacing ?: 1)
                }

                // Final line feed and execute print
                printerProvider.feedLine(4)
                printerProvider.feedLine(-1)
                printerProvider.startPrint()

            } catch (e: Exception) {
                // Handle errors and notify caller
                e.printStackTrace()
                errorCallBack?.invoke(e.message ?: "Print error")
            } finally {
                // Reset state and close the printer connection
                isRunning = false
                printerProvider.close()
            }
        }
    }

    override fun onStop() {
        // Optional cleanup if needed
    }
}
