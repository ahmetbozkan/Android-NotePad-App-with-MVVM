package com.example.notepadpro.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notepadpro.dao.NoteDao;
import com.example.notepadpro.model.Note;

@Database(entities = Note.class, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;

    public abstract NoteDao getNoteDao();

    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

    /** Whit this callback we can do any operation we want when Database is created for first time.*/

    /*private static Callback roomCallBack = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            new PopulateDbAsyncTask(instance).execute();
        }

    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;

        public PopulateDbAsyncTask(NoteDatabase db) {
            noteDao = db.getNoteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title 1", "Subtitle 1", "Note Text 1", "#ff234f", null, "null", false));
            noteDao.insert(new Note("Title 2", "Subtitle 2", "Note Text 2", "#ffffff", null, "null", false));
            noteDao.insert(new Note("Title 3", "Subtitle 3", "Note Text 3", "#ffffff", null, "null", false));

            return null;
        }
    }*/

}
