package com.hecorat.editvideo.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.hecorat.editvideo.R;

/**
 * Created by Bkmsx on 12/8/2016.
 */

public class NotificationHelper {
    public static void notify(Context context, String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Az Video Editor");
        builder.setContentText(msg);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setVibrate(new long[]{0l, 500l});
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(123, notification);
    }
}
