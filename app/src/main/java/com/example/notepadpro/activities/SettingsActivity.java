package com.example.notepadpro.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notepadpro.R;
import com.example.notepadpro.SettingsSharedPref;
import com.example.notepadpro.adapters.VersionNoteAdapter;
import com.example.notepadpro.model.VersionNote;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    private static final String OTHER_APPS_URL = "https://play.google.com/store/apps/developer?id=Ahmet+Bozkan&hl=en";

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int SELECT_IMAGE_REQUEST_CODE = 2;

    private ImageView img_profilePic;
    private TextView tv_rateUs;
    private TextView tv_otherApps;

    private RecyclerView recyclerView_versionNotes;
    private ArrayList<VersionNote> itemList;

    private LinearLayout layout_versionNotes;
    private AlertDialog dialog_versionNotes;

    private LinearLayout layout_languages;
    private AlertDialog dialog_languages;

    private SwitchCompat switch_darkMode;

    private SettingsSharedPref settingsSharedPref;

    @Override
    public void onBackPressed() {
        goPreviousAc(SettingsActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();

        if(settingsSharedPref.getAppTheme()) switch_darkMode.setChecked(true);
        else switch_darkMode.setChecked(false);

        img_profilePic.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        SettingsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST_CODE
                );
            } else {
                selectImage();
            }
        });

    }

    private void showSetLanguageDialog() {
        if (dialog_languages == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View v = LayoutInflater.from(this).inflate(
                    R.layout.layout_languages_dialog,
                    (ViewGroup) findViewById(R.id.languages_container)
            );

            builder.setView(v);

            dialog_languages = builder.create();

            if (dialog_languages.getWindow() != null) {
                dialog_languages.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            v.findViewById(R.id.layout_language_english).setOnClickListener(v1 -> {
                settingsSharedPref.setLocale("en");

                dialog_languages.dismiss();

                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(getIntent());
            });

            v.findViewById(R.id.layout_language_turkish).setOnClickListener(v12 -> {
                settingsSharedPref.setLocale("tr");

                dialog_languages.dismiss();

                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(getIntent());
            });

            v.findViewById(R.id.layout_dismiss_languages_dialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_languages.dismiss();
                }
            });
        }

        dialog_languages.show();
    }

    ////////// Selecting Image ////////////////
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();

                if (imageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        img_profilePic.setImageBitmap(bitmap);
                        Toast.makeText(this, "Image saved succesfully.", Toast.LENGTH_SHORT).show();

                        String imagePath = getPathFromUri(imageUri);
                        saveImage(imagePath);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, "File not found.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void saveImage(String imagePath) {
        settingsSharedPref.saveImagePath(imagePath);
    }

    private String getPathFromUri(Uri imageUri) {
        String filePath;

        Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);

        if (cursor == null) {
            filePath = imageUri.getPath();
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
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    ////////////////////////////////////////

    private void openAppPage(String url) {
        try {
            Intent appPage = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + url));
            startActivity(appPage);
        } catch (ActivityNotFoundException e) {
            Intent appPage = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + url));
            startActivity(appPage);
        }
    }

    private void openOtherAppsPage() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(OTHER_APPS_URL));
        startActivity(intent);
    }

    private void goPreviousAc(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }

    ///////// Version Notes //////////////
    private void showVersionNotes() {
        if (dialog_versionNotes == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View v = LayoutInflater.from(this).inflate(
                    R.layout.layout_version_notes_dialog,
                    (ViewGroup) findViewById(R.id.version_notes_container)
            );

            builder.setView(v);

            dialog_versionNotes = builder.create();

            if (dialog_versionNotes.getWindow() != null) {
                dialog_versionNotes.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            buildVersionRecyclerView(v);

            v.findViewById(R.id.btn_dismiss_version_notes_dialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_versionNotes.dismiss();
                }
            });
        }

        dialog_versionNotes.show();
    }

    private void buildVersionRecyclerView(View v) {
        recyclerView_versionNotes = v.findViewById(R.id.recyclerview_version_notes);
        recyclerView_versionNotes.setHasFixedSize(true);

        recyclerView_versionNotes.setLayoutManager(new LinearLayoutManager(this));
        VersionNoteAdapter versionNoteAdapter = new VersionNoteAdapter(itemList);

        setVersionNoteItems(versionNoteAdapter);
    }

    private void setVersionNoteItems(VersionNoteAdapter versionNoteAdapter) {
        itemList.add(new VersionNote("Version 1.0", "- Deneme 1\n- Deneme 2"));

        recyclerView_versionNotes.setAdapter(versionNoteAdapter);
    }
    ///////////////////////////////////

    private void loadLanguage() {
        settingsSharedPref = new SettingsSharedPref(this);

        String lang = settingsSharedPref.getLanguage();

        if (!lang.isEmpty())
            settingsSharedPref.setLocale(lang);
    }

    private void init() {
        img_profilePic = findViewById(R.id.img_profile_pic);
        switch_darkMode = findViewById(R.id.switch_dark_mode);
        switch_darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    settingsSharedPref.saveAppTheme(isChecked);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else {
                    settingsSharedPref.saveAppTheme(isChecked);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        String imagePath = settingsSharedPref.getImagePath();

        if (!imagePath.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            img_profilePic.setImageBitmap(bitmap);
        } else {
            img_profilePic.setImageResource(R.drawable.ic_person);
        }

        layout_languages = findViewById(R.id.layout_change_language);

        itemList = new ArrayList<>();

        tv_rateUs = findViewById(R.id.tv_rate_us);
        tv_rateUs.setOnClickListener(v ->
                openAppPage(getPackageName())

        );

        tv_otherApps = findViewById(R.id.tv_other_apps);
        tv_otherApps.setOnClickListener(v ->
                openOtherAppsPage()
        );

        layout_versionNotes = findViewById(R.id.layout_show_version_notes);
        layout_versionNotes.setOnClickListener(v -> {
            showVersionNotes();
        });

        layout_languages = findViewById(R.id.layout_change_language);
        layout_languages.setOnClickListener(v -> {
            showSetLanguageDialog();
        });

        findViewById(R.id.img_back).setOnClickListener(v ->
                goPreviousAc(SettingsActivity.this)
        );

        findViewById(R.id.btn_notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
            }
        });
    }

}