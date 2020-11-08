package com.example.notepadpro.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.notepadpro.model.Note;
import com.example.notepadpro.repository.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private NoteRepository noteRepository;

    private MutableLiveData<String> allNotesQuery = new MutableLiveData<>();
    private LiveData<List<Note>> allNotes;


   // private MutableLiveData<String> favNotesQuery = new MutableLiveData<>();
    private LiveData<List<Note>> allFavNotes;


    public NoteViewModel(Application application) {
        super(application);

        noteRepository = new NoteRepository(application);
        allNotes = noteRepository.getAllNotes();

        allFavNotes = noteRepository.getAllFavNotes();
    }

    public void initAllNotes() {
        allNotes = Transformations.switchMap(allNotesQuery, input -> {
           if(input == null || input.equals("") || input.equals("%%")) {
               return noteRepository.getAllNotes();
           }
           else {
               return noteRepository.getFilteredNotes(input);
           }
        });
    }

    /*public void initAllFavNotes() {
        allFavNotes = Transformations.switchMap(favNotesQuery, input -> {
            if(input == null || input.equals("") || input.equals("%%")) {
                return noteRepository.getAllFavNotes();
            }
            else {
                return noteRepository.getFilteredFavNotes(input);
            }
        });
    }*/

    public void insertNote(Note note) {
        noteRepository.insertNote(note);
    }

    public void updateNote(Note note) {
        noteRepository.updateNote(note);
    }

    public void deleteNote(Note note) {
        noteRepository.deleteNote(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<List<Note>> getAllFavNotes() {
        return allFavNotes;
    }

    public MutableLiveData<String> getAllNotesQuery() {
        return allNotesQuery;
    }

   /* public MutableLiveData<String> getFavNotesQuery() {
        return favNotesQuery;
    }*/
}
