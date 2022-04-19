package notification.listener.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION_CODES;

import androidx.annotation.RequiresApi;

import io.flutter.plugin.common.EventChannel.EventSink;

import java.util.HashMap;

public class NotificationReceiver extends BroadcastReceiver {

    private EventSink eventSink;

    public NotificationReceiver(EventSink eventSink) {
        this.eventSink = eventSink;
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getStringExtra(NotificationConstants.PACKAGE_NAME);
        String title = intent.getStringExtra(NotificationConstants.NOTIFICATION_TITLE);
        String content = intent.getStringExtra(NotificationConstants.NOTIFICATION_CONTENT);
        String notificationIcon = intent.getStringExtra(NotificationConstants.NOTIFICATIONS_ICON);
        String notificationExtrasPicture = intent.getStringExtra(NotificationConstants.EXTRAS_PICTURE);
        String hasExtrasPicture = intent.getStringExtra(NotificationConstants.HAS_EXTRAS_PICTURE);
        boolean hasRemoved = intent.getBooleanExtra(NotificationConstants.IS_REMOVED , false);

        HashMap<String, Object> data = new HashMap<>();
        data.put("packageName", packageName);
        data.put("title", title);
        data.put("content", content);
        data.put("notificationIcon", notificationIcon);
        data.put("notificationExtrasPicture", notificationExtrasPicture);
        data.put("hasExtrasPicture", hasExtrasPicture);
        data.put("hasRemoved", hasRemoved);

        eventSink.success(data);
    }
}
