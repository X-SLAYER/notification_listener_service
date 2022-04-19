import 'dart:typed_data';

class ServiceNotificationEvent {
  /// if the notification has an extras image
  bool? hasExtrasPicture;

  /// if the notification has been removed
  bool? hasRemoved;

  /// notification extras image
  /// To display an image simply use the [Image.memory] widget.
  /// Example:
  ///
  /// ```
  /// Image.memory(notif.extrasPicture)
  /// ```
  Uint8List? extrasPicture;

  /// notification package name
  String? packageName;

  /// notification title
  String? title;

  /// the notification app icon
  /// To display an image simply use the [Image.memory] widget.
  /// Example:
  ///
  /// ```
  /// Image.memory(notif.notificationIcon)
  /// ```
  Uint8List? notificationIcon;

  /// the content of the notification
  String? content;

  ServiceNotificationEvent({
    this.hasExtrasPicture,
    this.hasRemoved,
    this.extrasPicture,
    this.packageName,
    this.title,
    this.notificationIcon,
    this.content,
  });

  ServiceNotificationEvent.fromMap(Map<dynamic, dynamic> json) {
    hasExtrasPicture = json['hasExtrasPicture'];
    hasRemoved = json['hasRemoved'];
    extrasPicture = json['notificationExtrasPicture'];
    packageName = json['packageName'];
    title = json['title'];
    notificationIcon = json['notificationIcon'];
    content = json['content'];
  }

  @override
  String toString() {
    return '''ServiceNotificationEvent(
      packageName: $packageName
      title: $title
      content: $content
      hasRemoved: $hasRemoved
      hasExtrasPicture: $hasExtrasPicture
      ''';
  }
}
