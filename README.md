# 🚀 urovo_flutter

A Flutter plugin to integrate **Urovo device hardware** features including **Beeper**, **Printer**, **Scanner**, and **Magnetic Card Reader (SearchMagCard)**. This library enables easy access to Urovo-specific services for printing receipts, scanning barcodes, reading magnetic cards, and playing beep sounds directly from your Flutter app.

---

## ✨ Features

- 🔔 **Beeper Service**: Play beep sounds for user feedback.  
- 🖨️ **Print Service**: Print text, images, barcodes, and QR codes with customizable formatting.  
- 📷 **Scanner Service**: Scan barcodes using device cameras with configurable options.  
- 💳 **SearchMagCard Service**: Listen to magnetic card swipe events and handle card data.  
- ⚡ Supports asynchronous event listeners with error handling.  
- 🧩 Easy integration with Flutter apps and minimal setup.  

---

## 📦 Installation

Add this to your `pubspec.yaml`:

```yaml
dependencies:
  urovo_flutter:
    git:
      url: https://github.com/GradleBuildTech/urovo_flutter.git
```

### Run:
```
flutter pub get
```
---
## 🚀 Usage
### 1. Import package

```dart
import 'package:urovo_flutter/urovo.dart';
import 'package:flutter/services.dart' show rootBundle;
```
### 2. Initialize UrovoService

```dart
late UrovoService _urovoService;

@override
void initState() {
  super.initState();
  _urovoService = UrovoService(device: UrovoDevice.urovo);
}
```

### 3. Using Services

💳 Magnetic Card Reader (SearchMagCard)
Listen to magnetic card swipe events:
```dart
_urovoService.startListening(
  onMagCardEvent: (magCardEvent) {
    print('Magnetic Card Event: ${magCardEvent.toJson()}');
  },
  onError: (error) {
    print('Error: $error');
  },
);
```
📷 Scanner Service
Start scanning barcodes with the camera:
```dart
_urovoService.scannerService.startScan(
  onScanResult: (result) {
    print(result);
  },
  scannerObject: ScannerObject(timeOut: 100, cameraView: CameraView.back),
);
```

🖨️ Printer Service
Print text, images, and QR codes:
```dart
final imageData = await rootBundle.load("assets/ic_app_dark.png");

_urovoService.onPrint(
  printModel: PrintModel(
    spacing: 0,
    items: [
      PrintItemModel(isSpacing: true, size: 14, bold: true),
      PrintItemModel(
        textLeft: "SAMPLE RECEIPT",
        size: 2,
        bold: true,
        align: PrintItemAlign.center,
      ),
      PrintItemModel(
        image: PrintImage(imageData: imageData, width: 40, height: 20),
        align: PrintItemAlign.center,
      ),
      PrintItemModel(
        qrCode: "1234567890",
        align: PrintItemAlign.center,
      ),
    ],
  ),
);
```

🔔 Beeper Service
Play a beep sound:
```dart
_urovoService.onBeeper();
```
---
## 📱 Example: Using UrovoService in a Flutter Widget

```dart
FloatingActionButton(
  onPressed: () async {
    _urovoService.startListening(
      onMagCardEvent: (event) => print('MagCard: ${event.toJson()}'),
      onError: (error) => print('Error: $error'),
    );

    final imageData = await rootBundle.load("assets/ic_app_dark.png");
    _urovoService.onPrint(
      printModel: PrintModel(
        spacing: 0,
        items: [
          PrintItemModel(textLeft: "Demo Print", size: 2, bold: true),
          PrintItemModel(image: PrintImage(imageData: imageData, width: 40, height: 20)),
          PrintItemModel(qrCode: "1234567890", align: PrintItemAlign.center),
        ],
      ),
    );

    _urovoService.beep();
  },
  tooltip: 'Start Urovo Services',
  child: const Icon(Icons.play_arrow),
);

```

---

## 🔧 Supported Features

- Real-time barcode scanning  
- Printing text and images  
- Playing alert beep sounds  
- Reading data from magnetic cards  
- Easy integration with Flutter  
- Support for Urovo devices running Android  

---

## 📌 Note

- This library only supports the Android platform.  
- Please ensure your device is a Urovo device with the necessary hardware support.  
- Make sure to check and grant the required permissions for the app to access hardware functions.  
