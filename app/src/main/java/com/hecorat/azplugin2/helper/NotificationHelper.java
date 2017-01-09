package com.hecorat.azplugin2.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.main.MainActivity;

/**
 * Created by Bkmsx on 12/8/2016.
 */

public class NotificationHelper {

    public static void updateNotification(Context context, int progress, String videoPath) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notifyID = 1;
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.exporting_msg))
                .setSmallIcon(R.mipmap.icon_launcher)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true);
        if (progress < 100) {
            mNotifyBuilder.setProgress(100, progress, false)
                    .setContentText(progress + " %");
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(videoPath), "video/mp4");
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            mNotifyBuilder.setProgress(0, 0, false)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.export_completed))
                    .setVibrate(new long[]{0l, 500l})
                    .setOngoing(false)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        }

        mNotificationManager.notify(notifyID, mNotifyBuilder.build());
    }
}
