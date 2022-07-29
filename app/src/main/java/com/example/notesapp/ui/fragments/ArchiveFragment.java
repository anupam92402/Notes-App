package com.example.notesapp.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notesapp.R;
import com.example.notesapp.data.database.ArchiveDatabase;
import com.example.notesapp.data.database.NotesDatabase;
import com.example.notesapp.data.entities.Note;
import com.example.notesapp.listeners.NotesListeners;
import com.example.notesapp.ui.adapters.ArchiveAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArchiveFragment extends Fragment implements NotesListeners {

    View initialView;
    private RecyclerView archiveRecyclerView;
    private List<Note> noteList;
    private ArchiveAdapter archiveAdapter;
    private AlertDialog dialogDeleteNote;
    private Note alreadyAvailableNote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_archive, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialView = view;
        archiveRecyclerView = view.findViewById(R.id.archiveRecyclerView);
        noteList = new ArrayList<>();
        archiveAdapter = new ArchiveAdapter(noteList, this::onNoteClicked);
        setRecyclerView();
        getNotes();

        view.findViewById(R.id.imageBackArchive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int source = viewHolder.getAdapterPosition();
            int destination = target.getAdapterPosition();
            Collections.swap(noteList, source, destination);
            archiveAdapter.notifyItemMoved(source, destination);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    private void setRecyclerView() {
        archiveRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        archiveRecyclerView.setAdapter(archiveAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(archiveRecyclerView);
    }


    private void getNotes() {
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return ArchiveDatabase.getArchiveDatabase(getActivity().getApplicationContext()).archiveDao().getAllArchiveNotes();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                //app is started so add all the data
                if (noteList.size() == 0) {
                    noteList.addAll(notes);
                    archiveAdapter.notifyDataSetChanged();
                } else {
                    //adding latest note just created
                    noteList.add(0, notes.get(0));
                    archiveAdapter.notifyItemInserted(0);
                }
                archiveRecyclerView.smoothScrollToPosition(0);
            }
        }
        new GetNotesTask().execute();
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        alreadyAvailableNote = note;
        showDeleteNoteDialog(position);
    }

    private void showDeleteNoteDialog(int position) {
        if (dialogDeleteNote == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_restore_note, this.initialView.findViewById(R.id.layoutAddUrlContainer));
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if (dialogDeleteNote.getWindow() != null) {
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeletePermanently).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            ArchiveDatabase.getArchiveDatabase(getActivity().getApplicationContext()).archiveDao().deleteNotePermanently(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent intent = new Intent();
                            getActivity().setResult(RESULT_OK, intent);
                            noteList.remove(position);
                            archiveAdapter.notifyItemRemoved(position);
                            dialogDeleteNote.dismiss();
                        }
                    }

                    new DeleteNoteTask().execute();
                }
            });

            view.findViewById(R.id.textRestore).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            ArchiveDatabase.getArchiveDatabase(getActivity().getApplicationContext()).archiveDao().deleteNotePermanently(alreadyAvailableNote);
                            NotesDatabase.getNotesDatabase(getActivity().getApplicationContext()).noteDao().insertNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent intent = new Intent();
                            getActivity().setResult(RESULT_OK, intent);
                            noteList.remove(position);
                            archiveAdapter.notifyItemRemoved(position);
                            dialogDeleteNote.dismiss();
                        }
                    }

                    new DeleteNoteTask().execute();
                }
            });
        }
        dialogDeleteNote.show();
    }

}