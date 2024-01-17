package nemosofts.notes.app.Activity.Note;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nemosofts.notes.app.R;
import nemosofts.notes.app.database.NotificationDatabase;
import nemosofts.notes.app.entities.Note;

public class AlarmReceiver extends BroadcastReceiver {

    List<Note> notes;

    @Override
    public void onReceive(Context context, Intent intent) {

        createNotificationChannel(context);

        Intent repeatingIntent = new Intent(context, NotificationClick.class);

//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        String formattedTime = formatTime(System.currentTimeMillis());

        NotificationDatabase notificationDatabase = new NotificationDatabase(context);
        notes = notificationDatabase.readAllData(formattedTime);
        notificationDatabase.close();

        if (notes != null && !notes.isEmpty()) {
            for (Note note : notes) {
                String title = note.getTitle();
                String subtitle = note.getSubtitle();
                String created_note = note.getCreatednotes();

                repeatingIntent.putExtra("title", title);
                repeatingIntent.putExtra("subtitle", subtitle);
                repeatingIntent.putExtra("created_note", created_note);
                repeatingIntent.putExtra("createdTime", note.getCreatedTime());
                repeatingIntent.putExtra("formattedTime", formattedTime);

                Log.d("AlarmReceiver", "Received alarm with Title: " + title + ", Subtitle: " + subtitle + " , Created Notes:" + created_note);

                int requestCode = note.getId();

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context,
                        requestCode,
                        repeatingIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "YourChannelId")
                        .setSmallIcon(R.drawable.bell)
                        .setContentTitle(title)
                        .setContentText(subtitle)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent); // Set the content intent

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(123, builder.build());
            }
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "YourChannelId",
                    "YourChannelName",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private String formatTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}