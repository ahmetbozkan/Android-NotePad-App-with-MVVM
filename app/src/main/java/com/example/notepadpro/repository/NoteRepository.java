package com.example.notepadpro.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.notepadpro.dao.NoteDao;
import com.example.notepadpro.database.NoteDatabase;
import com.example.notepadpro.model.Note;

import java.util.List;

public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;
    private LiveData<List<Note>> favNotes;

    public NoteRepository(Application application) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);

        noteDao = noteDatabase.getNoteDao();
        allNotes = noteDao.getAllNotes();
        favNotes = noteDao.getAllFavNotes();
    }

    public void insertNote(Note note) {
        new InsertNoteAsyncTask(noteDao).execute(note);
    }

    public void updateNote(Note note) {
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteNote(Note note) {
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<List<Note>> getAllFavNotes() {
        return favNotes;
    }

    public LiveData<List<Note>> getFilteredNotes(String query) {
        return noteDao.getFilteredNotes(query);
    }

    public LiveData<List<Note>> getFilteredFavNotes(String query) {
        return noteDao.getFilteredFavNotes(query);
    }


    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        public InsertNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);

            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        public UpdateNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);

            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        public DeleteNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);

            return null;
        }
    }


}
