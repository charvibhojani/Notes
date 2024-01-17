package nemosofts.notes.app.Activity.Note;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nemosofts.notes.app.R;
import nemosofts.notes.app.database.NotificationDatabase;

public class NotificationClick extends AppCompatActivity {

    TextView item_textTime, notification_title, notification_subtitle, created_note;
    ImageView dismiss, snooze;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_click);

        item_textTime = findViewById(R.id.item_textTime);
        notification_title = findViewById(R.id.notification_title);
        notification_subtitle = findViewById(R.id.notification_subtitle);
        created_note = findViewById(R.id.CreatedNote);
        dismiss = findViewById(R.id.dismiss);
        snooze = findViewById(R.id.snooze);

        long createdTime = getIntent().getLongExtra("createdTime", 0);

        int noteId = (int) createdTime;

        NotificationDatabase notificationDatabase = new NotificationDatabase(this);

        try {

            String title = getIntent().getStringExtra("title");
            String subtitle = getIntent().getStringExtra("subtitle");
            String creatednote = getIntent().getStringExtra("created_note");
            String formattedTime = getIntent().getStringExtra("formattedTime");

            notification_title.setText(title);
            notification_subtitle.setText(subtitle);
            created_note.setText(creatednote);

            if (formattedTime != null) {
                Log.d(TAG, "Formatted Time: " + formattedTime);
            }

            setCurrentTime(formattedTime);

            item_textTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCurrentTime(formattedTime);
                }
            });

            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissReminder(noteId);
                    finish();
                }
            });

            snooze.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snoozeReminder(noteId);
                    finish();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in onCreate", e);
        } finally {
            notificationDatabase.close();
        }
    }

    private void setCurrentTime(String formattedTime) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = dateFormat.parse(formattedTime);

            if (date != null) {
                long currentTimeMillis = date.getTime();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(currentTimeMillis);

                String newFormattedTime = dateFormat.format(calendar.getTime());
                item_textTime.setText(newFormattedTime);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in setCurrentTime", e);
        }
    }

    private void dismissReminder(int noteId) {
        try {
            Context context = getApplicationContext();
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, noteId, alarmIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.cancel(dismissPendingIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in dismissReminder", e);
        }
    }

    private void snoozeReminder(int noteId) {
        try {
            Context context = getApplicationContext();
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, noteId, alarmIntent, 0);

            long snoozeTime = SystemClock.elapsedRealtime() + 5 * 60 * 1000;

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, snoozeTime, snoozePendingIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in snoozeReminder", e);
        }
    }
}