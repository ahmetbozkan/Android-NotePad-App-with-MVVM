package com.example.notepadpro.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notepadpro.R;
import com.example.notepadpro.activities.CreateNoteActivity;
import com.example.notepadpro.adapters.NoteAdapter;
import com.example.notepadpro.model.Note;
import com.example.notepadpro.viewmodel.NoteViewModel;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.notepadpro.fragments.MainFragment.EDIT_NOTE_REQUEST_CODE;
import static com.example.notepadpro.fragments.MainFragment.EXTRA_EXISTING_NOTE;

public class FavouritesFragment extends Fragment {


    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private NoteViewModel noteViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_favourites, container, false);
        buildRecyclerView(v);

        noteViewModel = new ViewModelProvider(getActivity(), ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getActivity().getApplication()))
                .get(NoteViewModel.class);

        //noteViewModel.initAllFavNotes();

        noteViewModel.getAllFavNotes().observe(getActivity(), new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteAdapter.submitList(notes);
            }
        });

        //noteViewModel.getFavNotesQuery().setValue("");

        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
                intent.putExtra(EXTRA_EXISTING_NOTE, note);
                startActivityForResult(intent, EDIT_NOTE_REQUEST_CODE);
            }

            @Override
            public void onAddFavoriteClick(Note note, int position) {
                if (!note.isIs_favorite()) {
                    note.setIs_favorite(true);
                    Toast.makeText(getActivity(), getResources().getString(R.string.note_added_favorite), Toast.LENGTH_SHORT).show();
                } else {
                    note.setIs_favorite(false);
                    Toast.makeText(getActivity(), getResources().getString(R.string.note_removed_favorite), Toast.LENGTH_SHORT).show();
                }

                noteViewModel.updateNote(note);
                noteAdapter.notifyItemChanged(position);
            }
        });

        return v;
    }

    private void buildRecyclerView(View v) {
        recyclerView = v.findViewById(R.id.recyclerview_favourite_notes);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteAdapter = new NoteAdapter();
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {

            boolean ifDeleted = data.getBooleanExtra("IfDeleted", false);
            Note note = (Note) data.getSerializableExtra(EXTRA_EXISTING_NOTE);

            if (!ifDeleted) {
                noteViewModel.updateNote(note);
                Toast.makeText(getActivity(), getResources().getString(R.string.note_updated), Toast.LENGTH_SHORT).show();
            } else {
                noteViewModel.deleteNote(note);
                Toast.makeText(getActivity(), getResources().getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
            }

        }
    }


}
