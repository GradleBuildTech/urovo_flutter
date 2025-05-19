import 'package:urovo_flutter/src/method/scanner/object/camera_view.dart';

class ScannerObject {
  final int? timeOut;

  final CameraView? cameraView;

  ScannerObject({this.timeOut, this.cameraView});

  Map<String, dynamic> get toJson {
    final json = <String, dynamic>{};
    if (timeOut != null) {
      json['timeOut'] = timeOut;
    }
    if (cameraView != null) {
      json['cameraView'] = cameraView?.value;
    }
    return json;
  }
}
