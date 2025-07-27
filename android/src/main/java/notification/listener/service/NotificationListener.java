package notification.listener.service;

import static notification.listener.service.NotificationUtils.getBitmapFromDrawable;
import static notification.listener.service.models.ActionCache.cachedNotifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;

import notification.listener.service.models.Action;


@SuppressLint("OverrideAbstract")
@RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {
    private static NotificationListener instance;

    public static NotificationListener getInstance() {
        return instance;
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        instance = this;
    }

    @RequiresApi(api = VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        handleNotification(notification, false);
    }

    @RequiresApi(api = VERSION_CODES.KITKAT)
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        handleNotification(sbn, true);
    }

    @RequiresApi(api = VERSION_CODES.KITKAT)
    private void handleNotification(StatusBarNotification notification, boolean isRemoved) {
        Intent intent = new Intent(NotificationConstants.INTENT);
        Map<String, Object> data = buildNotificationDataMap(notification, isRemoved);

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof byte[]) {
                byte[] byteArray = (byte[]) value;
                if (byteArray.length > 100_000) continue;
                intent.putExtra(key, byteArray);
            } else if (value instanceof Boolean) {
                Boolean boolValue = (Boolean) value;
                intent.putExtra(key, boolValue);
            } else if (value instanceof Integer) {
                Integer intValue = (Integer) value;
                intent.putExtra(key, intValue);
            } else if (value instanceof Long) {
                Long longValue = (Long) value;
                intent.putExtra(key, longValue);
            } else if (value instanceof String) {
                String stringValue = (String) value;
                if (stringValue.getBytes(StandardCharsets.UTF_8).length > 50_000) continue;
                intent.putExtra(key, stringValue);
            }
        }

        sendBroadcast(intent);
    }


    public byte[] getAppIcon(String packageName) {
        try {
            PackageManager manager = getBaseContext().getPackageManager();
            Drawable icon = manager.getApplicationIcon(packageName);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            getBitmapFromDrawable(icon).compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = VERSION_CODES.M)
    private byte[] getNotificationLargeIcon(Context context, Notification notification) {
        try {
            Icon largeIcon = notification.getLargeIcon();
            if (largeIcon == null) {
                return null;
            }
            Drawable iconDrawable = largeIcon.loadDrawable(context);
            Bitmap iconBitmap = ((BitmapDrawable) iconDrawable).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ERROR LARGE ICON", "getNotificationLargeIcon: " + e.getMessage());
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<Map<String, Object>> getActiveNotificationData() {
        List<Map<String, Object>> notificationList = new ArrayList<>();
        StatusBarNotification[] activeNotifications = getActiveNotifications();

        for (StatusBarNotification sbn : activeNotifications) {
            Map<String, Object> notifData = buildNotificationDataMap(sbn, false);
            notificationList.add(notifData);
        }

        return notificationList;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Map<String, Object> buildNotificationDataMap(StatusBarNotification sbn, boolean isRemoved) {
        Map<String, Object> data = new HashMap<>();
        Notification notification = sbn.getNotification();
        String packageName = sbn.getPackageName();
        Bundle extras = notification.extras;

        byte[] appIcon = getAppIcon(packageName);
        byte[] largeIcon = null;
        if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            largeIcon = getNotificationLargeIcon(getApplicationContext(), notification);
        }

        Action action = NotificationUtils.getQuickReplyAction(notification, packageName);
        if (action != null) {
            cachedNotifications.put(sbn.getId(), action);
        }

        data.put(NotificationConstants.ID, sbn.getId());
        data.put(NotificationConstants.PACKAGE_NAME, packageName);
        data.put(NotificationConstants.CAN_REPLY, action != null);
        data.put(NotificationConstants.NOTIFICATIONS_ICON, appIcon);
        data.put(NotificationConstants.NOTIFICATIONS_LARGE_ICON, largeIcon);
        data.put(NotificationConstants.NOTIFICATION_TIME, sbn.getPostTime());
        data.put(NotificationConstants.IS_REMOVED, isRemoved);

        if (extras != null) {
            CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
            data.put(NotificationConstants.NOTIFICATION_TITLE, title != null ? title.toString() : null);
            data.put(NotificationConstants.NOTIFICATION_CONTENT, text != null ? text.toString() : null);
            data.put(NotificationConstants.HAVE_EXTRA_PICTURE, extras.containsKey(Notification.EXTRA_PICTURE));

            if (extras.containsKey(Notification.EXTRA_PICTURE)) {
                Bitmap bmp = (Bitmap) extras.get(Notification.EXTRA_PICTURE);
                if (bmp != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    data.put(NotificationConstants.EXTRAS_PICTURE, stream.toByteArray());
                } else {
                    Log.w("NotificationListener", "Notification.EXTRA_PICTURE exists but is null.");
                }
            }
        }

        return data;
    }
}
