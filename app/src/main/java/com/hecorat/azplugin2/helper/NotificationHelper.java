package com.hecorat.azplugin2.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.hecorat.azplugin2.R;

import java.io.File;

/**
 * Created by Bkmsx on 12/8/2016.
 */

public class NotificationHelper {
    private static int notifyID = 1;
    public static void updateNotification(Context context, int progress, String videoPath) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.exporting_msg))
                .setSmallIcon(R.drawable.ic_notification_exporting)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true);
        if (progress == -1){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File videoFile = new File(videoPath);
            intent.setDataAndType(Uri.fromFile(videoFile), "video/mp4");
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            mNotifyBuilder.setProgress(0, 0, false)
                    .setSmallIcon(R.drawable.ic_notification_exporting_6)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.export_completed))
                    .setVibrate(new long[]{0L, 500L})
                    .setOngoing(false)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        } else if (progress < 100) {
            mNotifyBuilder.setProgress(100, progress, false)
                    .setContentText(progress + " %");
        } else {
            mNotifyBuilder.setProgress(100, 100, true)
                    .setContentTitle("")
                    .setContentText("Completing..");
        }
        mNotificationManager.notify(notifyID, mNotifyBuilder.build());
    }

    public static void cancelNotification(Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notifyID);
    }
}
