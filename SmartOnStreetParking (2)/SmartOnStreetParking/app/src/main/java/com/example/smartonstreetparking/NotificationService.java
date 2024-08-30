package com.example.smartonstreetparking;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationService {
    private Context context;
    private NotificationManager notificationManager;

    public NotificationService(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showNotification() {
        Intent activityIntent = new Intent(context, ParkingDurationActivity.class);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(
                context,
                1,
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Smart On-Street Parking System")
                .setContentText("This is a notice to remind you that the parking session is over in 5 minutes!")
                .setContentIntent(activityPendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(MyApp.CHANNEL_ID);
        }

        notificationManager.notify(1, builder.build());
    }

    public static final String CHANNEL_ID = "notification_channel";

}