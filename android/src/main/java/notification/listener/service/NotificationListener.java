package notification.listener.service;

import static notification.listener.service.NotificationUtils.encodeToBase64;
import static notification.listener.service.NotificationUtils.getBitmapFromDrawable;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;


@SuppressLint("OverrideAbstract")
@RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {

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
        String packageName = notification.getPackageName();
        Bundle extras = notification.getNotification().extras;
        byte[] drawable = getSmallIcon(packageName);
        

        Intent intent = new Intent(NotificationConstants.INTENT);
        intent.putExtra(NotificationConstants.PACKAGE_NAME, packageName);

        intent.putExtra(NotificationConstants.NOTIFICATIONS_ICON, drawable);

        if (extras != null) {
            CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);

            intent.putExtra(NotificationConstants.NOTIFICATION_TITLE, title == null ? null : title.toString());
            intent.putExtra(NotificationConstants.NOTIFICATION_CONTENT, text == null ? null : text.toString());
            intent.putExtra(NotificationConstants.IS_REMOVED, isRemoved);

            if (extras.containsKey(Notification.EXTRA_PICTURE)) {
                Bitmap bmp = (Bitmap) extras.get(Notification.EXTRA_PICTURE);
                intent.putExtra(NotificationConstants.HAS_EXTRAS_PICTURE, true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                intent.putExtra(NotificationConstants.EXTRAS_PICTURE, stream.toByteArray());
            } else {
                intent.putExtra(NotificationConstants.HAS_EXTRAS_PICTURE, false);
            }
        }
        sendBroadcast(intent);
    }


    public byte[] getSmallIcon(String packageName) {
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

}
