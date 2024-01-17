package nemosofts.notes.app.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import nemosofts.notes.app.entities.Note;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import nemosofts.notes.app.R;
import nemosofts.notes.app.entities.Note;
import nemosofts.notes.app.listeners.NotesListener;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private List<Note> notes;
    private NotesListener notesListener;
    private Timer timer;
    private List<Note> noteSource;
    private OnItemClickListener itemClickListener;

    public NoteAdapter(Context context, List<Note> notes, NotesListener notesListener) {
        this.context = context;
        this.notes = notes;
        this.notesListener = notesListener;
        this.noteSource = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        final Note note = notes.get(position);
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesListener.onNoteClicked(notes.get(position), position);
            }
        });

        if (position == 0) {
            holder.item_new.setVisibility(View.VISIBLE);
        } else {
            holder.item_new.setVisibility(View.GONE);
        }

        holder.textTitle.setText(note.getTitle());
        if (note.getSubtitle().trim().isEmpty()) {
            holder.textSubtitle.setVisibility(View.GONE);
        } else {
            holder.textSubtitle.setText(note.getSubtitle());
        }

        //  Check if note.getDateTime() is not null before formatting
//        if (note.getDateTime() != null) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//            String formattedDateTime = dateFormat.format(new Date(note.getDateTime()));
//            holder.textDateTime.setText(formattedDateTime);
//        }

        holder.creatednotes.setText(note.getCreatednotes());

        int randomColor = getRandomColor();
        holder.itemView.setBackgroundColor(randomColor);

        if (note.getImagePath() != null) {
            holder.imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
            holder.imageNote.setVisibility(View.VISIBLE);
        } else {
            holder.imageNote.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnItemClickListener {
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textSubtitle, textDateTime, item_new, creatednotes;
        RelativeLayout layoutNote;
        RoundedImageView imageNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            item_new = itemView.findViewById(R.id.item_new);
            textTitle = itemView.findViewById(R.id.item_textTitlem);
            textSubtitle = itemView.findViewById(R.id.item_textSubTitle);
            textDateTime = itemView.findViewById(R.id.item_textDateTime);
            layoutNote = itemView.findViewById(R.id.item_layoutNote);
            imageNote = itemView.findViewById(R.id.item_imageNote);
            creatednotes = itemView.findViewById(R.id.created_notes);
        }
    }

    public void searchNote(final String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    notes = noteSource;
                } else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : noteSource) {
                        if (note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase()) || note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase()) || note.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(note);
                        }
                    }
                    notes = temp;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private int getRandomColor() {
        Random random = new Random();

        int min = 150;
        int max = 255;

        int red = min + random.nextInt(max - min + 1);
        int green = min + random.nextInt(max - min + 1);
        int blue = min + random.nextInt(max - min + 1);

        return Color.rgb(red, green, blue);
    }
}