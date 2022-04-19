# notification_listener_service

a plugin for interacting with Notification Service in Android.

NotificationListenerService is a service that receives calls from the system when new notifications are posted or removed,

for more info check [NotificationListenerService](https://developer.android.com/reference/android/service/notification/NotificationListenerService)

### Installation and usage

Add package to your pubspec:

```yaml
dependencies:
  notification_listener_service: any # or the latest version on Pub
```

Inside AndroidManifest add this to bind notification service with your application

```
    ...
    <service android:label="notifications" android:name="notification.listener.service.NotificationListener" android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE">
        <intent-filter>
            <action android:name="android.service.notification.NotificationListenerService" />
        </intent-filter>
    </service>
    ...
</application>

```

### USAGE

```dart
 /// check if accessibility permession is enebaled
 final bool status = await NotificationListenerService.isPermissionGranted();

 /// request accessibility permission
 /// it will open the accessibility settings page and return `true` once the permission granted.
 final bool status = await NotificationListenerService.requestPermission();

 /// stream the incoming Accessibility events
  NotificationListenerService.notificationsStream.listen((event) {
    log("Current notification: $event");
  });
```

The `ServiceNotificationEvent` provides:

```dart
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
```

for each event.
