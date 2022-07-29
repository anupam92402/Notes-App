package com.example.notesapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.notesapp.data.entities.Note;

import java.util.List;

@Dao
public interface ArchiveDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Note> getAllArchiveNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArchiveNote(Note note);

    @Delete
    void deleteNotePermanently(Note note);
}
