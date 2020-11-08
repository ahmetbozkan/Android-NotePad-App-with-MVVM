package com.example.notepadpro.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.notepadpro.R;
import com.example.notepadpro.SettingsSharedPref;
import com.example.notepadpro.adapters.TabLayoutAdapter;
import com.example.notepadpro.model.Note;
import com.example.notepadpro.fragments.FavouritesFragment;
import com.example.notepadpro.fragments.MainFragment;
import com.example.notepadpro.viewmodel.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import static com.example.notepadpro.activities.CreateNoteActivity.EXTRA_COLOR;
import static com.example.notepadpro.activities.CreateNoteActivity.EXTRA_DATE_TIME;
import static com.example.notepadpro.activities.CreateNoteActivity.EXTRA_IMAGE_PATH;
import static com.example.notepadpro.activities.CreateNoteActivity.EXTRA_IS_FAVORITE;
import static com.example.notepadpro.activities.CreateNoteActivity.EXTRA_NOTE_TEXT;
import static com.example.notepadpro.activities.CreateNoteActivity.EXTRA_SUBTITLE;
import static com.example.notepadpro.activities.CreateNoteActivity.EXTRA_TITLE;

public class MainActivity extends AppCompatActivity implements MainFragment.ActivityCommunication {
    private static final String TAG = "MainActivity";

    private static final int ADD_NOTE_REQUEST_CODE = 1;

    private TabLayoutAdapter tabLayoutAdapter;
    private ViewPager viewPager;
    private NoteViewModel noteViewModel;

    private ImageView img_profilePic;

    private SettingsSharedPref settingsSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLanguage();
        if(settingsSharedPref.getAppTheme()) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setProfilePicture();

        viewPager = findViewById(R.id.fragment_container);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabRippleColor(null);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton floatingActionButton = findViewById(R.id.btn_add_note);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
            startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);
        });

        ImageView btn_settings = findViewById(R.id.btn_settings);
        btn_settings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void loadLanguage() {
        settingsSharedPref = new SettingsSharedPref(this);

        String lang = settingsSharedPref.getLanguage();

        if (!lang.isEmpty())
            settingsSharedPref.setLocale(lang);
    }

    @Override
    public void setViewModel(NoteViewModel noteViewModel) {
        this.noteViewModel = noteViewModel;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {
            String title = data.getStringExtra(EXTRA_TITLE);
            String subtitle = data.getStringExtra(EXTRA_SUBTITLE);
            String noteText = data.getStringExtra(EXTRA_NOTE_TEXT);
            String imagePath = data.getStringExtra(EXTRA_IMAGE_PATH);
            String noteColor = data.getStringExtra(EXTRA_COLOR);
            String dateTime = data.getStringExtra(EXTRA_DATE_TIME);
            boolean isFavorite = data.getBooleanExtra(EXTRA_IS_FAVORITE, false);

            //Log.d(TAG, "onActivityResult: Image Path: " + imagePath);

            Note note = new Note(title, subtitle, noteText, noteColor, imagePath, dateTime, isFavorite);
            noteViewModel.insertNote(note);

            Toast.makeText(this, getResources().getString(R.string.note_saved), Toast.LENGTH_SHORT).show();
        }
    }

    private void setProfilePicture() {
        settingsSharedPref = new SettingsSharedPref(this);
        String imagePath = settingsSharedPref.getImagePath();

        img_profilePic = findViewById(R.id.img_profile_pic);

        if(!imagePath.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            img_profilePic.setImageBitmap(bitmap);
        }
        else {
            img_profilePic.setImageResource(R.drawable.ic_person);
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        tabLayoutAdapter = new TabLayoutAdapter(getSupportFragmentManager());
        tabLayoutAdapter.addFragment(new MainFragment(), getResources().getString(R.string.all_notes));
        tabLayoutAdapter.addFragment(new FavouritesFragment(), getResources().getString(R.string.favorite_notes));

        viewPager.setAdapter(tabLayoutAdapter);
    }

}