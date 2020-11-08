package com.example.notepadpro.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepadpro.R;
import com.example.notepadpro.model.VersionNote;

import java.util.ArrayList;

public class VersionNoteAdapter extends RecyclerView.Adapter<VersionNoteAdapter.VersionNoteViewHolder> {

    private ArrayList<VersionNote> itemList;

    public VersionNoteAdapter(ArrayList<VersionNote> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public VersionNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_version_note_item, parent, false);

        return new VersionNoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VersionNoteViewHolder holder, int position) {
        VersionNote currentVersionNote = itemList.get(position);

        holder.tv_versionTitle.setText(currentVersionNote.getVersion_title());
        holder.tv_versionNotes.setText(currentVersionNote.getVersion_notes());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class VersionNoteViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_versionTitle;
        private TextView tv_versionNotes;

        public VersionNoteViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_versionTitle = itemView.findViewById(R.id.tv_version_title);
            tv_versionNotes = itemView.findViewById(R.id.tv_version_notes);
        }
    }
}
