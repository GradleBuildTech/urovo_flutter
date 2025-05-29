import 'package:flutter/material.dart';
import 'package:urovo_flutter/urovo.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  late UrovoService _urovoService;

  @override
  void initState() {
    // Initialize the UrovoService
    _urovoService = UrovoService(device: UrovoDevice.urovo);
    super.initState();
  }

  void _incrementCounter() {
    // _urovoService.scannerService.startScan(
    //     scannerObject:
    //         ScannerObject(timeOut: 100, cameraView: CameraView.back));
    // setState(() {
    _urovoService.onPrint(
        printModel: PrintModel(spacing: 0, items: [
      PrintItemModel(
          textLeft: "PHIẾU TẠM TÍNH",
          size: 2,
          bold: true,
          align: PrintItemAlign.center),
      PrintItemModel(
          textLeft: "Số HD",
          textRight: "POS1213121311313",
          size: 2,
          bold: false),
      PrintItemModel(
          textLeft: "Ngày",
          textRight: "20:33:23 21-05-2002",
          size: 2,
          bold: false),
      PrintItemModel(
          textLeft: "Máy",
          textRight: "Điểm bán trung tâm",
          size: 2,
          bold: false),
      PrintItemModel(
          textLeft: "Thu ngân:",
          textRight: "Nguyễn Quốc",
          size: 2,
          bold: false),
      PrintItemModel(
          textLeft: "Khách:", textRight: "Dan Thy", size: 2, bold: false),
      PrintItemModel(
          textLeft: "SDT:", textRight: "09813818212", size: 2, bold: false),
      PrintItemModel(isSpacing: true, size: 14, bold: true),
      PrintItemModel(isSpacing: true, size: 14, bold: true),
      PrintItemModel(
          textLeft: "Tên món",
          textRight: "Thành tiền",
          textCenter: "SL",
          size: 1,
          bold: true),
      PrintItemModel(
          textLeft: "------------------------------",
          size: 1,
          bold: true,
          align: PrintItemAlign.center),
      PrintItemModel(
          textLeft: "Đậu hũ sốt Cocktail(VAT5)", size: 1, bold: false),
      PrintItemModel(
          textLeft: "152đ",
          textRight: "152đ",
          textCenter: "1",
          size: 1,
          bold: false),
      PrintItemModel(isSpacing: true, size: 1, bold: true),
      PrintItemModel(
          textLeft: "------------------------------",
          size: 1,
          bold: true,
          align: PrintItemAlign.center),
      PrintItemModel(textLeft: "Tổng cộng: 3", size: 1, bold: false),
      PrintItemModel(isSpacing: true, size: 1, bold: true),
      PrintItemModel(textLeft: "VAT5", textRight: "27đ", size: 1, bold: false),
      PrintItemModel(isSpacing: true, size: 1, bold: true),
      PrintItemModel(
          textLeft: "Tiền chiết khấu:", textRight: "59đ", size: 1, bold: true),
      PrintItemModel(
          textLeft: "Thành tiền:", textRight: "524đ", size: 1, bold: true),
      PrintItemModel(
          textLeft: "Phí dịch vụ:", textRight: "0đ", size: 1, bold: true),
      PrintItemModel(
          textLeft: "Tổng tiền phải thu: 551đ",
          textRight: "551đ",
          size: 1,
          bold: true),
      PrintItemModel(isSpacing: true, size: 1, bold: true),
      PrintItemModel(
          textLeft: "------------------------------",
          size: 1,
          bold: true,
          align: PrintItemAlign.center),
      PrintItemModel(
          qrCode: "222222222222222222222", align: PrintItemAlign.center),
    ]));
    // });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'You have pushed the button this many times:',
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
