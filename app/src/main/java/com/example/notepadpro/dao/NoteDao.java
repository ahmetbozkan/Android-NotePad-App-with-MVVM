package com.example.notepadpro.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notepadpro.model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM note_table WHERE title LIKE :queryString")
    LiveData<List<Note>> getFilteredNotes(String queryString);

    @Query("SELECT * FROM note_table WHERE is_favorite = 1")
    LiveData<List<Note>> getAllFavNotes();

    @Query("SELECT * FROM note_table WHERE title LIKE :queryString AND is_favorite = 1")
    LiveData<List<Note>> getFilteredFavNotes(String queryString);
}
