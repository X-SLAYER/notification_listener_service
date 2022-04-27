# notification_listener_service

A flutter plugin for interacting with Notification Service in Android.

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
 /// check if notification permession is enebaled
 final bool status = await NotificationListenerService.isPermissionGranted();

 /// request notification permission
 /// it will open the accessibility settings page and return `true` once the permission granted.
 final bool status = await NotificationListenerService.requestPermission();

 /// stream the incoming notification events
  NotificationListenerService.notificationsStream.listen((event) {
    log("Current notification: $event");
  });
```

The `ServiceNotificationEvent` provides:

```dart
  /// the notification id
  int? id;

  /// check if we can reply the Notification
  bool? canReply;

  /// if the notification has an extras image
  bool? hasExtrasPicture;

  /// if the notification has been removed
  bool? hasRemoved;

  /// notification extras image
  /// To display an image simply use the [Image.memory] widget.
  Uint8List? extrasPicture;

  /// notification package name
  String? packageName;

  /// notification title
  String? title;

  /// the notification app icon
  /// To display an image simply use the [Image.memory] widget.
  Uint8List? notificationIcon;

  /// the content of the notification
  String? content;

  /// send a direct message reply to the incoming notification
  Future<bool> sendReply(String message)

```

To reply to a notification provides:

```dart
  try {
    await event.sendReply("This is an auto response");
  } catch (e) {
    log(e.toString());
  }

```
## Exemple of the app on foreground

Find the exemple app [Here](https://github.com/X-SLAYER/foreground_plugins_test)

## Screenshots

<img src="https://user-images.githubusercontent.com/22800380/165560254-fc72ed1f-a31e-4498-b6de-18cea539ca11.png" width="300">






