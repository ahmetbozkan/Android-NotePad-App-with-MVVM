package com.example.notepadpro.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepadpro.R;
import com.example.notepadpro.model.Note;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteViewHolder> {

    private OnItemClickListener onItemClickListener;

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getSubtitle().equals(newItem.getSubtitle()) &&
                    oldItem.getNote_text().equals(newItem.getNote_text()) &&
                    oldItem.getImage_path().equals(newItem.getImage_path()) &&
                    oldItem.getColor().equals(newItem.getColor()) &&
                    oldItem.getDate_time().equals(newItem.getDate_time()) &&
                    oldItem.isIs_favorite() == newItem.isIs_favorite();
        }
    };

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_note_item, parent, false);

        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = getItem(position);

        holder.tv_title.setText(currentNote.getTitle());
        holder.tv_subtitle.setText(currentNote.getSubtitle());
        holder.tv_dateTime.setText(currentNote.getDate_time());
        holder.layout_noteBackground.setBackgroundColor(Color.parseColor(currentNote.getColor()));

        if (!currentNote.getImage_path().isEmpty()) {
            holder.img_noteImage.setImageBitmap(decodeSampledBitmapFromFile(currentNote.getImage_path(), 100, 100));
            //holder.img_noteImage.setImageBitmap(bitmap);
            holder.img_noteImage.setVisibility(View.VISIBLE);
        } else
            holder.img_noteImage.setVisibility(View.GONE);

        if (currentNote.isIs_favorite())
            holder.img_addFavorites.setImageResource(R.drawable.ic_favorite);
        else
            holder.img_addFavorites.setImageResource(R.drawable.ic_no_favorite);

        if(currentNote.getColor().equals("#1f1f1f")) {
            holder.tv_title.setTextColor(Color.parseColor("#ffffff"));
            holder.tv_subtitle.setTextColor(Color.parseColor("#B5B3B3"));
            holder.tv_dateTime.setTextColor(Color.parseColor("#B5B3B3"));
        }
        else {
            holder.tv_title.setTextColor(Color.parseColor("#000000"));
            holder.tv_subtitle.setTextColor(Color.parseColor("#000000"));
            holder.tv_dateTime.setTextColor(Color.parseColor("#000000"));
        }
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private TextView tv_subtitle;
        private TextView tv_dateTime;
        private ImageView img_noteImage;
        private ImageView img_addFavorites;
        private LinearLayout layout_noteBackground;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subtitle = itemView.findViewById(R.id.tv_subtitle);
            tv_dateTime = itemView.findViewById(R.id.tv_date_time);
            img_noteImage = itemView.findViewById(R.id.img_single_note_image);
            img_addFavorites = itemView.findViewById(R.id.img_add_favorite);
            layout_noteBackground = itemView.findViewById(R.id.layout_single_note_background);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(getItem(position));
                    }
                }
            });

            img_addFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onAddFavoriteClick(getItem(position), position);
                    }
                }
            });

        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String imagePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public interface OnItemClickListener {
        void onItemClick(Note note);

        void onAddFavoriteClick(Note note, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
