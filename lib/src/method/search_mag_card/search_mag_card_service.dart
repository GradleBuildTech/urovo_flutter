import 'dart:async';

import 'package:flutter/services.dart';
import 'package:urovo_flutter/src/method/search_mag_card/mag_card_information.dart';

import '../../../urovo.dart';

/// This mixin provides a service for listening to magnetic card events
/// using an [EventChannel].
/// It allows you to start and stop listening for magnetic card events,
/// and provides a way to handle the events through a callback function.
/// /// Usage:
/// /// ```dart
/// import 'package:urovo_flutter/urovo.dart';
/// void main() {
///  final service = SearchMagCardService();
/// service.startListening(
///    onMagCardEvent: (magCardEvent) {
///     print('Magnetic Card Event: ${magCardEvent.toJson()}');
///
///  },
///  onError: (error) {
///   print('Error: $error');
///  },
/// ///  );
/// ///  // To stop listening later
/// ///  service.stopListening();
/// ///  // Dispose the service when done
///   ///  service.dispose();
/// /// ```
mixin SearchMagCardService {
  static const _channel = EventChannel(ChannelTag.searchMagCard);

  StreamSubscription? _subscription;

  /// Starts listening for magnetic card events.
  /// /// [onMagCardEvent] is a callback function that will be called with the
  /// magnetic card event as a [MagCardEvent].

  void startListening({
    int timeout = 30,
    required Function(MagCardInformation) onMagCardEvent,
    Function(Object)? onError,
  }) {
    if (_subscription != null) {
      // If already listening, stop the previous subscription
      stopListening();
    }
    _subscription = _channel.receiveBroadcastStream(timeout).listen(
      (event) {
        if (event is! Map<dynamic, dynamic>) {
          onError?.call('Invalid event data type: ${event.runtimeType}');
          return;
        }
        final magCardEvent = MagCardInformation.fromJson(event);
        onMagCardEvent(magCardEvent);
      },
      onDone: () {
        // Handle stream completion if needed
        // For example, you can stop listening here if needed
        stopListening();
      },
      onError: (error) {
        onError?.call(error);
      },
      cancelOnError: true,
    );
  }

  /// Stops listening for magnetic card events.
  /// /// This method cancels the subscription to the magnetic card events.
  void stopListening() {
    _subscription?.cancel();
    _subscription = null;
  }

  /// Disposes the service by stopping the listening and cleaning up resources.
  void dispose() {
    stopListening();
  }
}
