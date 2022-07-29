package com.example.notesapp.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notesapp.data.dao.ArchiveDao;
import com.example.notesapp.data.entities.Note;

@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class ArchiveDatabase extends RoomDatabase {

    private static ArchiveDatabase archiveDatabase;

    public static synchronized ArchiveDatabase getArchiveDatabase(Context context) {
        if (archiveDatabase == null) {
            archiveDatabase = Room.databaseBuilder(context, ArchiveDatabase.class, "archive_db").build();
        }
        return archiveDatabase;
    }

    public abstract ArchiveDao archiveDao();

}
