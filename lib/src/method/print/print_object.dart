enum PrintItemAlign {
  left("left"),
  center("center"),
  right("right");

  final String value;

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
  final String textLeft;
  final String? textCenter;
  final String? textRight;
  final String? qrCode;
  final int? size;
  final bool? bold;
  final bool? underline;
  final PrintItemAlign? align;

  PrintItemModel({
    this.textLeft = '',
    this.textCenter,
    this.textRight,
    this.qrCode,
    this.size,
    this.bold,
    this.underline,
    this.align = PrintItemAlign.left,
  })  : assert(
          ((textLeft.trim().isNotEmpty ||
                  (textCenter?.trim().isNotEmpty ?? false) ||
                  (textRight?.trim().isNotEmpty ?? false)) ^
              (qrCode?.trim().isNotEmpty ?? false)),
          'Exactly one of (textLeft/textCenter/textRight) or qrCode must be non-null and non-blank.',
        ),
        assert(
          size == null || size > 0,
          'Size must be greater than 0 if provided.',
        );

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = {
      'textLeft': textLeft,
    };

    if (textCenter != null) data['textCenter'] = textCenter;
    if (textRight != null) data['textRight'] = textRight;
    if (qrCode != null) data['qrCode'] = qrCode;
    if (size != null) data['size'] = size;
    if (bold != null) data['bold'] = bold;
    if (underline != null) data['underline'] = underline;
    if (align != null) data['align'] = align!.value;

    return data;
  }
}
