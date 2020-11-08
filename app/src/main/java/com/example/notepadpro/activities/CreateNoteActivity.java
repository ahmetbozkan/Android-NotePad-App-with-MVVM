package com.example.notepadpro.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notepadpro.R;
import com.example.notepadpro.SettingsSharedPref;
import com.example.notepadpro.model.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.notepadpro.fragments.MainFragment.EXTRA_EXISTING_NOTE;

public class CreateNoteActivity extends AppCompatActivity {

    private static final String TAG = "CreateNoteActivity";

    public static final String EXTRA_ID = "com.example.notepadpro.activities.CreateNoteActivity.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.notepadpro.activities.CreateNoteActivity.EXTRA_TITLE";
    public static final String EXTRA_SUBTITLE = "com.example.notepadpro.activities.CreateNoteActivity.EXTRA_SUBTITLE";
    public static final String EXTRA_NOTE_TEXT = "com.example.notepadpro.activities.CreateNoteActivity.EXTRA_NOTE_TEXT";
    public static final String EXTRA_COLOR = "com.example.notepadpro.activities.CreateNoteActivity.EXTRA_COLOR";
    public static final String EXTRA_IMAGE_PATH = "com.example.notepadpro.activities.CreateNoteActivity.EXTRA_IMAGE_PATH";
    public static final String EXTRA_DATE_TIME = "com.example.notepadpro.activities.CreateNoteActivity.EXTRA_DATE_TIME";
    public static final String EXTRA_IS_FAVORITE = "com.example.notepadpro.activities.CreateNoteActivity.EXTRA_IS_FAVORITE";

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int SELECT_IMAGE_REQUEST_CODE = 2;

    private EditText et_title;
    private EditText et_subtitle;
    private EditText et_noteText;

    private TextView tv_activityTitle;
    private TextView tv_dateTime;
    private View view_subtitleIndicator;

    private ImageView img_back;
    private ImageView img_save;
    private ImageView img_note;
    private ImageView img_favoriteImage;
    private ImageView img_removeImage;

    private LinearLayout layout_noteSettings;
    private LinearLayout layout_deleteNote;
    private LinearLayout layout_addFavorite;

    private String selectedNoteColor;
    private String selectedImagePath;
    private int id;
    private boolean isFavorite;

    private Intent intent;
    private Note existingNote;

    private AlertDialog deleteDialog;

    private SettingsSharedPref settingsSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        setContentView(R.layout.activity_create_note);
        init();
        buildSettingsLayout();

        img_save.setOnClickListener(v ->
                saveNote()
        );

        layout_deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(existingNote != null)
                    showDeleteDialog();
                else
                    Toast.makeText(CreateNoteActivity.this, getResources().getString(R.string.note_not_deleted), Toast.LENGTH_SHORT).show();
            }
        });

        layout_addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(existingNote != null){
                    if(!isFavorite) {
                        isFavorite = true;
                        img_favoriteImage.setImageResource(R.drawable.ic_favorite);
                        Toast.makeText(CreateNoteActivity.this, getResources().getString(R.string.note_added_favorite), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        isFavorite = false;
                        img_favoriteImage.setImageResource(R.drawable.ic_no_favorite);
                        Toast.makeText(CreateNoteActivity.this, getResources().getString(R.string.note_removed_favorite), Toast.LENGTH_SHORT).show();
                    }
                }

                else Toast.makeText(CreateNoteActivity.this, getResources().getString(R.string.note_not_added_favorite), Toast.LENGTH_SHORT).show();
            }
        });

        img_removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectedImagePath.isEmpty()) {
                    selectedImagePath = "";
                    img_note.setVisibility(View.GONE);
                    img_removeImage.setVisibility(View.GONE);
                }
            }
        });

    }

    private void loadLanguage() {
        settingsSharedPref = new SettingsSharedPref(this);

        String lang = settingsSharedPref.getLanguage();

        if (!lang.isEmpty())
            settingsSharedPref.setLocale(lang);
    }

    private void saveNote() {
        String title = et_title.getText().toString();
        String subtitle = et_subtitle.getText().toString();
        String noteText = et_noteText.getText().toString();
        String dateTime = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date());

        if (title.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.note_title_empty), Toast.LENGTH_SHORT).show();
            return;
        } else if (noteText.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.note_text_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);

        if (!subtitle.trim().isEmpty())
            data.putExtra(EXTRA_SUBTITLE, subtitle);
        else
            data.putExtra(EXTRA_SUBTITLE, getResources().getString(R.string.empty_subtitle_placeholder));

        Note note = new Note(title, subtitle, noteText, selectedNoteColor, selectedImagePath, dateTime, isFavorite);

        if (existingNote != null)
            note.setId(id);
        data.putExtra(EXTRA_EXISTING_NOTE, note);

        data.putExtra(EXTRA_NOTE_TEXT, noteText);
        data.putExtra(EXTRA_IMAGE_PATH, selectedImagePath);
        data.putExtra(EXTRA_COLOR, selectedNoteColor);
        data.putExtra(EXTRA_DATE_TIME, dateTime);
        data.putExtra(EXTRA_IS_FAVORITE, false);

        setResult(RESULT_OK, data);
        finish();
    }

    private void showDeleteDialog() {
        if (deleteDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View v = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_dialog,
                    (ViewGroup) findViewById(R.id.delete_dialog_container)
            );

            builder.setView(v);

            deleteDialog = builder.create();

            if (deleteDialog.getWindow() != null) {
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            v.findViewById(R.id.tv_delete_note).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();

                    Intent data = new Intent();
                    data.putExtra(EXTRA_EXISTING_NOTE, existingNote);
                    data.putExtra("IfDeleted", true);

                    setResult(RESULT_OK, data);
                    finish();
                }
            });

            v.findViewById(R.id.tv_cancel_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                }
            });
        }

        deleteDialog.show();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;

        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);

        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }

        return filePath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();

                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        img_note.setImageBitmap(bitmap);
                        img_note.setVisibility(View.VISIBLE);
                        img_removeImage.setVisibility(View.VISIBLE);

                        selectedImagePath = getPathFromUri(selectedImageUri);
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void setExistingNoteViews(Note note) {
        tv_activityTitle.setText(getResources().getString(R.string.edit_note));
        tv_dateTime.setText(note.getDate_time());
        et_title.setText(note.getTitle());
        et_subtitle.setText(note.getSubtitle());
        et_noteText.setText(note.getNote_text());

        selectedNoteColor = note.getColor();
        selectedImagePath = note.getImage_path();
        id = note.getId();
        isFavorite = note.isIs_favorite();

        switch (selectedNoteColor) {
            case "#ececec":
                layout_noteSettings.findViewById(R.id.view_color1).performClick();
                break;
            case "#FDE4BC":
                layout_noteSettings.findViewById(R.id.view_color2).performClick();
                break;
            case "#ffe9f4":
                layout_noteSettings.findViewById(R.id.view_color3).performClick();
                break;
            case "#e7f5fb":
                layout_noteSettings.findViewById(R.id.view_color4).performClick();
                break;
            case "#c93030":
                layout_noteSettings.findViewById(R.id.view_color5).performClick();
                break;
            case "#658CBB":
                layout_noteSettings.findViewById(R.id.view_color6).performClick();
                break;
            case "#1f1f1f":
                layout_noteSettings.findViewById(R.id.view_color7).performClick();
                break;

        }

        GradientDrawable gradientDrawable = (GradientDrawable) view_subtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));

        if (!note.getImage_path().isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(note.getImage_path());

            img_note.setImageBitmap(bitmap);
            img_note.setVisibility(View.VISIBLE);
            img_removeImage.setVisibility(View.VISIBLE);
        } else {
            selectedImagePath = "";
            img_note.setVisibility(View.GONE);
            img_removeImage.setVisibility(View.GONE);
        }

        if(note.isIs_favorite())
            img_favoriteImage.setImageResource(R.drawable.ic_favorite);
        else
            img_favoriteImage.setImageResource(R.drawable.ic_no_favorite);


    }

    private void buildSettingsLayout() {
        layout_noteSettings = findViewById(R.id.layout_create_note_settings);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layout_noteSettings);

        findViewById(R.id.tv_create_note_settings_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        final ImageView image_color1 = layout_noteSettings.findViewById(R.id.img_color1);
        final ImageView image_color2 = layout_noteSettings.findViewById(R.id.img_color2);
        final ImageView image_color3 = layout_noteSettings.findViewById(R.id.img_color3);
        final ImageView image_color4 = layout_noteSettings.findViewById(R.id.img_color4);
        final ImageView image_color5 = layout_noteSettings.findViewById(R.id.img_color5);
        final ImageView image_color6 = layout_noteSettings.findViewById(R.id.img_color6);
        final ImageView image_color7 = layout_noteSettings.findViewById(R.id.img_color7);

        layout_noteSettings.findViewById(R.id.view_color1).setOnClickListener(v -> {
            selectedNoteColor = "#ececec";
            image_color1.setImageResource(R.drawable.ic_done);

            image_color2.setImageResource(0);
            image_color3.setImageResource(0);
            image_color4.setImageResource(0);
            image_color5.setImageResource(0);
            image_color6.setImageResource(0);
            image_color7.setImageResource(0);

            GradientDrawable gradientDrawable = (GradientDrawable) view_subtitleIndicator.getBackground();
            gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
        });

        layout_noteSettings.findViewById(R.id.view_color2).setOnClickListener(v -> {
            selectedNoteColor = "#FDE4BC";
            image_color2.setImageResource(R.drawable.ic_done);

            image_color1.setImageResource(0);
            image_color3.setImageResource(0);
            image_color4.setImageResource(0);
            image_color5.setImageResource(0);
            image_color6.setImageResource(0);
            image_color7.setImageResource(0);

            GradientDrawable gradientDrawable = (GradientDrawable) view_subtitleIndicator.getBackground();
            gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
        });

        layout_noteSettings.findViewById(R.id.view_color3).setOnClickListener(v -> {
            selectedNoteColor = "#ffe9f4";
            image_color3.setImageResource(R.drawable.ic_done);

            image_color2.setImageResource(0);
            image_color1.setImageResource(0);
            image_color4.setImageResource(0);
            image_color5.setImageResource(0);
            image_color6.setImageResource(0);
            image_color7.setImageResource(0);

            GradientDrawable gradientDrawable = (GradientDrawable) view_subtitleIndicator.getBackground();
            gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
        });

        layout_noteSettings.findViewById(R.id.view_color4).setOnClickListener(v -> {
            selectedNoteColor = "#e7f5fb";
            image_color4.setImageResource(R.drawable.ic_done);

            image_color2.setImageResource(0);
            image_color3.setImageResource(0);
            image_color1.setImageResource(0);
            image_color5.setImageResource(0);
            image_color6.setImageResource(0);
            image_color7.setImageResource(0);

            GradientDrawable gradientDrawable = (GradientDrawable) view_subtitleIndicator.getBackground();
            gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
        });

        layout_noteSettings.findViewById(R.id.view_color5).setOnClickListener(v -> {
            selectedNoteColor = "#c93030";
            image_color5.setImageResource(R.drawable.ic_done);

            image_color2.setImageResource(0);
            image_color3.setImageResource(0);
            image_color4.setImageResource(0);
            image_color1.setImageResource(0);
            image_color6.setImageResource(0);
            image_color7.setImageResource(0);

            GradientDrawable gradientDrawable = (GradientDrawable) view_subtitleIndicator.getBackground();
            gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
        });

        layout_noteSettings.findViewById(R.id.view_color6).setOnClickListener(v -> {
            selectedNoteColor = "#658CBB";
            image_color6.setImageResource(R.drawable.ic_done);

            image_color2.setImageResource(0);
            image_color3.setImageResource(0);
            image_color4.setImageResource(0);
            image_color1.setImageResource(0);
            image_color5.setImageResource(0);
            image_color7.setImageResource(0);

            GradientDrawable gradientDrawable = (GradientDrawable) view_subtitleIndicator.getBackground();
            gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
        });

        layout_noteSettings.findViewById(R.id.view_color7).setOnClickListener(v -> {
            selectedNoteColor = "#1f1f1f";
            image_color7.setImageResource(R.drawable.ic_done);

            image_color2.setImageResource(0);
            image_color3.setImageResource(0);
            image_color4.setImageResource(0);
            image_color1.setImageResource(0);
            image_color5.setImageResource(0);
            image_color6.setImageResource(0);

            GradientDrawable gradientDrawable = (GradientDrawable) view_subtitleIndicator.getBackground();
            gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
        });




        LinearLayout layout_addImage = layout_noteSettings.findViewById(R.id.layout_add_image);
        layout_addImage.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        CreateNoteActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST_CODE
                );
            } else {
                selectImage();
            }
        });
    }

    private void init() {
        et_title = findViewById(R.id.et_input_title);
        et_subtitle = findViewById(R.id.et_input_subtitle);
        et_noteText = findViewById(R.id.et_input_note_text);
        tv_dateTime = findViewById(R.id.tv_date_time);
        tv_activityTitle = findViewById(R.id.tv_activity_title);
        view_subtitleIndicator = findViewById(R.id.view_subtitle_indicator);
        layout_noteSettings = findViewById(R.id.layout_create_note_settings);
        layout_deleteNote = layout_noteSettings.findViewById(R.id.layout_delete_note);
        layout_addFavorite = layout_noteSettings.findViewById(R.id.layout_add_favorite);

        img_note = findViewById(R.id.img_note_image);
        img_save = findViewById(R.id.img_save);
        img_favoriteImage = findViewById(R.id.img_add_favorite);
        img_removeImage = findViewById(R.id.img_removeImage);

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        intent = getIntent();
        existingNote = (Note) intent.getSerializableExtra(EXTRA_EXISTING_NOTE);

        if (existingNote != null) {
            setExistingNoteViews(existingNote);
        } else {
            selectedNoteColor = "#ffffff";
            selectedImagePath = "";
            tv_dateTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));
            tv_activityTitle.setText(getResources().getString(R.string.add_note));
            isFavorite = false;
        }

        GradientDrawable gradientDrawable = (GradientDrawable) view_subtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }
}