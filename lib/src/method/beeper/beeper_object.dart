class BeeperObject {
  final int? cnts;
  final int? msecTime;

  BeeperObject({
    this.cnts,
    this.msecTime,
  });

  Map<String, dynamic> get toJson {
    return {'cnts': cnts, 'msecTime': msecTime};
  }
}
