package notification.listener.service;

import static notification.listener.service.NotificationUtils.encodeToBase64;
import static notification.listener.service.NotificationUtils.getBitmapFromDrawable;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;


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
        int iconId = extras.getInt(Notification.EXTRA_SMALL_ICON);
        Drawable drawable = getSmallIcon(iconId, packageName);
        Bitmap smallIcon = getBitmapFromDrawable(drawable);

        Intent intent = new Intent(NotificationConstants.INTENT);
        intent.putExtra(NotificationConstants.PACKAGE_NAME, packageName);

        if (smallIcon != null) {
            intent.putExtra(NotificationConstants.NOTIFICATION_TITLE, encodeToBase64(getBitmapFromDrawable(drawable), Bitmap.CompressFormat.PNG, 100));
        }

        if (extras != null) {
            CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);

            intent.putExtra(NotificationConstants.NOTIFICATION_TITLE, title.toString());
            intent.putExtra(NotificationConstants.NOTIFICATION_CONTENT, text.toString());
            intent.putExtra(NotificationConstants.IS_REMOVED, isRemoved);

            if (extras.containsKey(Notification.EXTRA_PICTURE)) {
                Bitmap bmp = (Bitmap) extras.get(Notification.EXTRA_PICTURE);
                intent.putExtra(NotificationConstants.HAS_EXTRAS_PICTURE, true);
                intent.putExtra(NotificationConstants.EXTRAS_PICTURE, encodeToBase64(bmp, Bitmap.CompressFormat.PNG, 100));
            }
        }
        sendBroadcast(intent);
    }


    public Drawable getSmallIcon(int iconId, String packageName) {
        try {
            PackageManager manager = getPackageManager();
            Resources resources = manager.getResourcesForApplication(packageName);
            Drawable icon = resources.getDrawable(iconId);
            return icon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
