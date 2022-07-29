package com.example.notesapp.listeners;

import com.example.notesapp.data.entities.Note;

public interface NotesListeners {

    void onNoteClicked(Note note, int position);

}
