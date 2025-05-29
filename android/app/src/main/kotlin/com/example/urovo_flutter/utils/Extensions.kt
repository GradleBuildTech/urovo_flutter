package com.example.urovo_flutter.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun listToBitmap(pixels: List<Int>, width: Int, height: Int): Bitmap {
    require(pixels.size == width * height) {
        "The size of the pixel list must be width * height"
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels.toIntArray(), 0, width, 0, 0, width, height)
    return bitmap
}


fun getBitmapBytes(bitmap: Bitmap): ByteArray? {
    var imageData: ByteArray? = null
    try {
        val bass = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bass)
        imageData = bass.toByteArray()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return imageData
}
