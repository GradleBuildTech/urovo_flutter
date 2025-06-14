import 'dart:typed_data';

enum PrintItemAlign {
  left(0),
  center(1),
  right(2);

  final int value;

  const PrintItemAlign(this.value);
}

class PrintModel {
  final List<PrintItemModel> items;
  final int? spacing;
  final bool? cutPaper;
  final int? printWidth;
  final int? printHeight;
  final String? printMode; // "normal", "bold", "underline"
  final String? printAlign; // "left", "center", "right"

  PrintModel({
    this.items = const [],
    this.spacing = 8,
    this.cutPaper,
    this.printWidth,
    this.printHeight,
    this.printMode,
    this.printAlign,
  });

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = {
      'items': items.map((item) => item.toJson()).toList(),
    };

    if (spacing != null) data['spacing'] = spacing;
    if (cutPaper != null) data['cutPaper'] = cutPaper;
    if (printWidth != null) data['printWidth'] = printWidth;
    if (printHeight != null) data['printHeight'] = printHeight;
    if (printMode != null) data['printMode'] = printMode;
    if (printAlign != null) data['printAlign'] = printAlign;

    return data;
  }
}

class PrintItemModel {
  final PrintImage? image;
  final String textLeft;
  final String? textCenter;
  final String? textRight;
  final String? qrCode;
  final bool isSpacing;
  final int? size;
  final bool? bold;
  final bool? underline;
  final PrintItemAlign? align;

  PrintItemModel({
    this.textLeft = '',
    this.textCenter,
    this.isSpacing = false,
    this.textRight,
    this.qrCode,
    this.size,
    this.bold,
    this.underline,
    this.image,
    this.align = PrintItemAlign.left,
  }) : assert(
          isSpacing
              ? true
              : ((textLeft.isNotEmpty ||
                      (textCenter?.trim().isNotEmpty ?? false) ||
                      (textRight?.trim().isNotEmpty ?? false)) ^
                  (qrCode?.trim().isNotEmpty ?? false) ^
                  (image != null)),
          'Exactly one of (textLeft/textCenter/textRight) or qrCode must be non-null and non-blank.',
        );

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = {
      'textLeft': textLeft,
      'isSpacing': isSpacing,
    };

    if (textCenter != null) data['textCenter'] = textCenter;
    if (textRight != null) data['textRight'] = textRight;
    if (qrCode != null) data['qrCode'] = qrCode;
    if (size != null) data['size'] = size;
    if (bold != null) data['bold'] = bold;
    if (underline != null) data['underline'] = underline;
    if (align != null) data['align'] = align!.value;
    if (image != null) {
      data['image'] = image!.toJson();
    }

    return data;
  }
}

class PrintImage {
  final ByteData imageData;
  final int? width;
  final int? height;

  PrintImage({
    required this.imageData,
    this.width,
    this.height,
  });

  Map<String, dynamic> toJson() {
    final imageString = imageData.buffer.asUint8List().join(',');
    final Map<String, dynamic> data = {'imageData': imageString};
    if (width != null) data['width'] = width;
    if (height != null) data['height'] = height;
    return data;
  }
}
