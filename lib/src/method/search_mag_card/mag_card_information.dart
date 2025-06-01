/// This file is part of the `flutter_esc_pos` package.
/// It is subject to the license terms in the LICENSE file found in the top-level directory of this distribution.
/// You may not use this file except in compliance with the License.
/// You can obtain a copy of the License at
class MagCardInformation {
  final String? pan;
  final String? track1;
  final String? track2;
  final String? track3;
  final String? serviceCode;
  final String? expireDate;
  final String? timeOut;

  MagCardInformation({
    this.pan,
    this.track1,
    this.track2,
    this.track3,
    this.serviceCode,
    this.expireDate,
    this.timeOut,
  });

  factory MagCardInformation.fromJson(Map<dynamic, dynamic> json) {
    return MagCardInformation(
      pan: json['pan'] as String?,
      track1: json['track1'] as String?,
      track2: json['track2'] as String?,
      track3: json['track3'] as String?,
      serviceCode: json['serviceCode'] as String?,
      expireDate: json['expireDate'] as String?,
      timeOut: json['timeOut'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'pan': pan,
      'track1': track1,
      'track2': track2,
      'track3': track3,
      'serviceCode': serviceCode,
      'expireDate': expireDate,
    };
  }
}
