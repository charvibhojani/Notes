package nemosofts;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nemosofts.notes.app.R;
import nemosofts.notes.app.database.NotificationDatabase;

public class NotificationService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "TaskTrackerChannel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification() {
        NotificationDatabase notificationDatabase = new NotificationDatabase(this);
        Cursor cursor = notificationDatabase.readdataWithCurrentTime();

        if (cursor != null && cursor.moveToFirst()) {
            long currentTimeMillis = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedTime = sdf.format(new Date(currentTimeMillis));

            String currentTaskName = null;
            String currentCategory = null;

            do {
                String taskTime = cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabase.COL_CREATED_TIME));
                if (isCurrentTime(taskTime, formattedTime)) {
                    currentTaskName = cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabase.COL_TITLE));
                    currentCategory = cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabase.COL_SUBTITLE));

                    if (isTaskTimePast(taskTime, formattedTime)) {
                        stopSelf();
                    } else {
                        showNotification(currentTaskName, currentCategory);
                        break;
                    }
                }
            } while (cursor.moveToNext());

            cursor.close();
        }
        notificationDatabase.close();
    }


    private boolean isCurrentTime(String taskTime, String currentTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            Date taskDateTime = sdf.parse(taskTime);
            Date currentDateTime = sdf.parse(currentTime);

            long timeDifferenceMillis = Math.abs(taskDateTime.getTime() - currentDateTime.getTime());
            long timeRangeMillis = 5 * 60 * 1000;

            return timeDifferenceMillis <= timeRangeMillis;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isTaskTimePast(String taskTime, String currentTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            Date taskDateTime = sdf.parse(taskTime);
            Date currentDateTime = sdf.parse(currentTime);

            return taskDateTime.getTime() < currentDateTime.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showNotification(String taskName, String category) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.bell)
                .setContentTitle(taskName)
                .setContentText(category)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Tracker Channel";
            String description = "Channel for Task Tracker Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
