package com.example.notepadpro.fragments;

import android.content.Context;
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

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";

    public static final String EXTRA_EXISTING_NOTE = "Existing_Note";
    public static final int EDIT_NOTE_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private NoteViewModel noteViewModel;

    ActivityCommunication activityCommunication;

    //This interface stands for reaching to NoteViewModel instance from MainActivity for adding new notes.
    public interface ActivityCommunication {
        void setViewModel(NoteViewModel noteViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main, container, false);
        buildRecyclerView(v);

        noteViewModel = new ViewModelProvider(getActivity(), ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getActivity().getApplication()))
                .get(NoteViewModel.class);

        noteViewModel.initAllNotes();

        noteViewModel.getAllNotes().observe(getActivity(), new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteAdapter.submitList(notes);
            }
        });

        noteViewModel.getAllNotesQuery().setValue("");

        EditText et_searchNote = v.findViewById(R.id.et_search_all_notes);
        et_searchNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                noteViewModel.getAllNotesQuery().setValue("%" + s.toString() + "%");
            }
        });


        activityCommunication.setViewModel(noteViewModel);
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
                    Toast.makeText(getActivity(), getResources().getString(R.string.note_added_favorite), Toast.LENGTH_SHORT).show();
                    note.setIs_favorite(true);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.note_removed_favorite), Toast.LENGTH_SHORT).show();
                    note.setIs_favorite(false);
                }

                noteViewModel.updateNote(note);
                noteAdapter.notifyItemChanged(position);
            }
        });

        return v;
    }


    private void buildRecyclerView(View v) {
        recyclerView = v.findViewById(R.id.recyclerview_all_notes);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof ActivityCommunication) {
            activityCommunication = (ActivityCommunication) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ActivityCommunication interface.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activityCommunication = null;
    }
}
