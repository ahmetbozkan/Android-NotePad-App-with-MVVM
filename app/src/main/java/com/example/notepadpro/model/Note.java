package com.example.notepadpro.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "note_table")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "subtitle")
    private String subtitle;

    @ColumnInfo(name = "note_text")
    private String note_text;

    @ColumnInfo(name = "color")
    private String color;

    @ColumnInfo(name = "image_path")
    public String image_path;

    @ColumnInfo(name = "date_time")
    private String date_time;

    @ColumnInfo(name = "is_favorite")
    public boolean is_favorite;

    public Note(String title, String subtitle, String note_text, String color, String image_path, String date_time, boolean is_favorite) {
        this.title = title;
        this.subtitle = subtitle;
        this.note_text = note_text;
        this.color = color;
        this.image_path = image_path;
        this.date_time = date_time;
        this.is_favorite = is_favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getNote_text() {
        return note_text;
    }

    public void setNote_text(String note_text) {
        this.note_text = note_text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public boolean isIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", note_text='" + note_text + '\'' +
                ", color='" + color + '\'' +
                ", image_path='" + image_path + '\'' +
                ", date_time='" + date_time + '\'' +
                ", is_favorite=" + is_favorite +
                '}';
    }
}
