package com.example.notesapp.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.notesapp.R;
import com.example.notesapp.data.database.NotesDatabase;
import com.example.notesapp.data.entities.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CreateNotesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_notes, container, false);
        return view;
    }

    ImageView imageBack, imageSave;
    View view;
    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);
        this.view = view;
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_createNotesFragment_to_notesFragment);
            }
        });

        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        textDateTime.setText(new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date()));
    }

    private void saveNote() {
        if (inputNoteTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Note title can't be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (inputNoteSubtitle.getText().toString().trim().isEmpty() &&
                inputNoteText.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Note can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());

        class SaveNoteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getNotesDatabase(getActivity().getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                Intent intent = new Intent();
                getActivity().setResult(Activity.RESULT_OK, intent);
                Navigation.findNavController(view).navigate(R.id.action_createNotesFragment_to_notesFragment);
            }
        }
        new SaveNoteTask().execute();
    }

    private void initialize(View view) {
        inputNoteTitle = view.findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = view.findViewById(R.id.inputNoteSubTitle);
        inputNoteText = view.findViewById(R.id.inputNote);
        imageBack = view.findViewById(R.id.imageBack);
        imageSave = view.findViewById(R.id.imageSave);
        textDateTime = view.findViewById(R.id.textDateTime);
    }
}