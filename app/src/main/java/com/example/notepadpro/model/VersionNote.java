package com.example.notepadpro.model;

public class VersionNote {

    private String version_title;
    private String version_notes;

    public VersionNote(String version_title, String version_notes) {
        this.version_title = version_title;
        this.version_notes = version_notes;
    }

    public String getVersion_title() {
        return version_title;
    }

    public void setVersion_title(String version_title) {
        this.version_title = version_title;
    }

    public String getVersion_notes() {
        return version_notes;
    }

    public void setVersion_notes(String version_notes) {
        this.version_notes = version_notes;
    }
}
