package nemosofts.notes.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nemosofts.notes.app.entities.Note;

public class NotificationDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "notificationdb";
    public static final String TABLE_NAME = "notification";
    public static final String COL_TITLE = "title";
    public static final String COL_SUBTITLE = "subtitle";
    public static final String COL_CREATED_TIME = "created_time";
    public static final String COL_CREATED_NOTE = "created_note";

    public NotificationDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" + "ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TITLE + " TEXT, " + COL_SUBTITLE + " TEXT, " + COL_CREATED_TIME + " TEXT, " + COL_CREATED_NOTE + " TEXT)";
            db.execSQL(createTableQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addData(String title, String subtitle, String note, int backgroundcolor) {
        long result = -1;

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_TITLE, title);
            contentValues.put(COL_SUBTITLE, subtitle);
            contentValues.put(COL_CREATED_NOTE, note);

            String formattedTime = getFormattedTime(System.currentTimeMillis());
            contentValues.put(COL_CREATED_TIME, formattedTime);

            result = db.insert(TABLE_NAME, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Note> readAllData(String createTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT * FROM " + TABLE_NAME;
        List<Note> notes = new ArrayList<>();

        try (Cursor cursor = db.rawQuery(qry, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Note note = new Note();
                    note.setTitle(cursor.getString(cursor.getColumnIndex(COL_TITLE)));
                    note.setSubtitle(cursor.getString(cursor.getColumnIndex(COL_SUBTITLE)));
                    note.setCreatedTime(cursor.getLong(cursor.getColumnIndex(COL_CREATED_TIME)));
                    note.setCreatednotes(cursor.getString(cursor.getColumnIndex(COL_CREATED_NOTE)));

                    notes.add(note);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return notes;
    }

    public Cursor readdataWithCurrentTime() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Get the current time in the system format
        String formattedTime = getFormattedTime(System.currentTimeMillis());

        // Extract the time part from the formatted time
        String currentTime = formattedTime.substring(0, 5);  // Assuming "HH:mm" format

        String qry = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_CREATED_TIME + " = ?";
        Cursor cursor = db.rawQuery(qry, new String[]{currentTime});

        return cursor;
    }

    private String getFormattedTime(long timeMillis) {
        // Format the time in the system format
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date(timeMillis));
    }
}