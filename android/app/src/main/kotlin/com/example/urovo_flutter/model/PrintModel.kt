package com.example.urovo_flutter.model

enum class PrintItemAlign(val value: String) {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right");

    companion object {
        fun fromValue(value: String): PrintItemAlign {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: LEFT
        }
    }
}


/**
* PrintModel.kt
* This file defines the data model for printing items in the Urovo Flutter application.
* It includes the PrintModel class which contains a list of PrintItemModel objects,
*  @param items List of PrintItemModel objects representing the items to be printed.
 * @param spacing Optional spacing between items in the print layout.
 * @param cutPaper Optional flag to indicate whether to cut the paper after printing.
 * @param printWidth Optional width of the print area.
 * @param printHeight Optional height of the print area.
 * @param printMode Optional mode for printing, such as "normal", "bold", or "underline".
 * @param printAlign Optional alignment for the printed text, such as "left", "center", or "right".
 *
* */
data class PrintModel(
    val items: List<PrintItemModel> = emptyList(),
    val spacing: Int? = 8,
    val cutPaper: Boolean? = null,
    val printWidth: Int? = null,
    val printHeight: Int? = null,
    val printMode: String? = null, // "normal", "bold", "underline"
    val printAlign: String? = null // "left", "center", "right"
)

/**
 * PrintItemModel.kt
 * This file defines the data model for individual print items in the Urovo Flutter application.
 * It includes properties for text content, QR code, size, bold, underline, and alignment.
 *
 * @param textLeft Text to be printed on the left side.
 * @param textCenter Optional text to be printed in the center.
 * @param textRight Optional text to be printed on the right side.
 * @param qrCode Optional QR code content to be printed.
 * @param size Optional font size for the text.
 * @param bold Optional flag to indicate if the text should be bold.
 * @param underline Optional flag to indicate if the text should be underlined.
 * @param align Alignment of the print item (left, center, right).
 */
data class PrintItemModel(
    val textLeft: String = "",
    val textCenter: String? = null,
    val textRight: String? = null,
    val qrCode: String? = null,
    val size: Int? = null,
    val bold: Boolean? = null,
    val underline: Boolean? = null,
    val align: PrintItemAlign? = PrintItemAlign.LEFT,
) {
    init {
        val hasText = listOf(textLeft, textCenter, textRight).any { !it.isNullOrBlank() }
        val hasQrCode = !qrCode.isNullOrBlank()

        require(hasText.xor(hasQrCode)) {
            "Exactly one of (textLeft/textCenter/textRight) or qrCode must be non-null and non-blank."
        }

        require(size == null || size > 0) {
            "Size must be greater than 0 if provided."
        }
    }
}


fun Map<*, *>.toPrintItemModel(): PrintItemModel {
    val size = this["size"] as? Int
    val bold = this["bold"] as? Boolean
    val underline = this["underline"] as? Boolean
    val alignValue = this["align"] as? String
    val align = alignValue?.let { PrintItemAlign.fromValue(it) } ?: PrintItemAlign.LEFT
    val qrCode = this["qrCode"] as? String
    val textLeft = this["textLeft"] as? String
    val textCenter = this["textCenter"] as? String
    val textRight = this["textRight"] as? String
    return PrintItemModel(
        textLeft = textLeft ?: "",
        textCenter = textCenter,
        textRight = textRight,
        qrCode = qrCode,
        size = size,
        bold = bold,
        underline = underline,
        align = align
    )

}

fun Map<*, *>.toPrintModel(): PrintModel {
    val items = this["items"] as? List<*>
    val spacing = this["spacing"] as? Int
    val cutPaper = this["cutPaper"] as? Boolean
    val printWidth = this["printWidth"] as? Int
    val printHeight = this["printHeight"] as? Int
    val printMode = this["printMode"] as? String
    val printAlign = this["printAlign"] as? String

    return PrintModel(
        items = items?.filterIsInstance<Map<*, *>>()?.map { it.toPrintItemModel() } ?: emptyList(),
        spacing = spacing,
        cutPaper = cutPaper,
        printWidth = printWidth,
        printHeight = printHeight,
        printMode = printMode,
        printAlign = printAlign
    )
}