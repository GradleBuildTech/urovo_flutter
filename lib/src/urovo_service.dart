///Singleton
class UrovoService {
  static final UrovoService _instance = UrovoService._internal();

  factory UrovoService() {
    return _instance;
  }

  UrovoService._internal();

  // Add your service methods and properties here
  void someMethod() {}
}
