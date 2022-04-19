import 'dart:async';
import 'dart:developer';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:notification_listener_service/notification_event.dart';

class NotificationListenerService {
  NotificationListenerService._();

  static const MethodChannel _methodeChannel =
      MethodChannel('x-slayer/notifications_channel');
  static const EventChannel _eventChannel =
      EventChannel('x-slayer/notifications_event');
  static Stream<ServiceNotificationEvent> _stream = const Stream.empty();

  /// stream the incoming Accessibility events
  static Stream<ServiceNotificationEvent> get notificationsStream {
    if (Platform.isAndroid) {
      _stream =
          _eventChannel.receiveBroadcastStream().map<ServiceNotificationEvent>(
                (event) => ServiceNotificationEvent.fromMap(event),
              );
      return _stream;
    }
    throw Exception("Notifications API exclusively available on Android!");
  }

  /// request notification permission
  /// it will open the notification settings page and return `true` once the permission granted.
  static Future<bool> requestPermission() async {
    try {
      return await _methodeChannel.invokeMethod('requestPermission');
    } on PlatformException catch (error) {
      log("$error");
      return Future.value(false);
    }
  }

  /// check if notification permession is enebaled
  static Future<bool> isPermissionGranted() async {
    try {
      return await _methodeChannel.invokeMethod('isPermissionGranted');
    } on PlatformException catch (error) {
      log("$error");
      return false;
    }
  }
}
