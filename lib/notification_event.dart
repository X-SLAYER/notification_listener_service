import 'dart:typed_data';

import 'notification_listener_service.dart';

class ServiceNotificationEvent {
  /// the notification id
  int id;

  /// check if we can reply the Notification
  bool canReply;

  /// if the notification has an extras image
  bool haveExtraPicture;

  /// if the notification has been removed
  bool hasRemoved;

  /// notification extras image
  /// To display an image simply use the [Image.memory] widget.
  /// Example:
  ///
  /// ```
  /// Image.memory(notif.extrasPicture)
  /// ```
  Uint8List? extrasPicture;

  /// notification package name
  String packageName;

  /// notification title
  String title;

  /// the notification app icon
  /// To display an image simply use the [Image.memory] widget.
  /// Example:
  ///
  /// ```
  /// Image.memory(notif.appIcon)
  /// ```
  Uint8List? appIcon;

  /// the notification large icon (ex: album covers)
  /// To display an image simply use the [Image.memory] widget.
  /// Example:
  ///
  /// ```
  /// Image.memory(notif.largeIcon)
  /// ```
  Uint8List? largeIcon;

  /// the content of the notification
  String content;

  /// if the notification is ongoing (cannot be dismissed and is in progress)
  bool onGoing;

  /// the time at which the notification was posted, in milliseconds since epoch
  int timestamp;

  /// returns the post time as a [DateTime]
  DateTime get humanTime => DateTime.fromMillisecondsSinceEpoch(timestamp);

  ServiceNotificationEvent({
    required this.id,
    required this.title,
    required this.canReply,
    required this.haveExtraPicture,
    required this.hasRemoved,
    required this.packageName,
    required this.content,
    required this.onGoing,
    required this.timestamp,
    required this.appIcon,
    required this.extrasPicture,
    required this.largeIcon,
  });

  factory ServiceNotificationEvent.fromMap(Map<dynamic, dynamic> map) =>
      ServiceNotificationEvent(
        id: map['id'] ?? 0,
        canReply: map['canReply'] ?? false,
        haveExtraPicture: map['haveExtraPicture'],
        hasRemoved: map['hasRemoved'] ?? false,
        packageName: map['packageName'] ?? '',
        title: map['title'] ?? '',
        content: map['content'] ?? '',
        onGoing: map['onGoing'] ?? false,
        timestamp: map['postTime'] ?? 0,
        extrasPicture: map['notificationExtrasPicture'],
        largeIcon: map['largeIcon'],
        appIcon: map['appIcon'],
      );

  /// send a direct message reply to the incoming notification
  Future<bool> sendReply(String message) async {
    if (!canReply) throw Exception("The notification is not replyable");
    try {
      return await methodeChannel.invokeMethod<bool>("sendReply", {
            'message': message,
            'notificationId': id,
          }) ??
          false;
    } catch (e) {
      rethrow;
    }
  }

  @override
  String toString() {
    return '''ServiceNotificationEvent(
      id: $id
      can reply: $canReply
      packageName: $packageName
      title: $title
      content: $content
      hasRemoved: $hasRemoved
      haveExtraPicture: $haveExtraPicture
      onGoing: $onGoing
      timestamp: $timestamp
      humanTime: $humanTime
      ''';
  }
}
