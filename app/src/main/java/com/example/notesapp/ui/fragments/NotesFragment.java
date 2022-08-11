package com.example.notesapp.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notesapp.R;
import com.example.notesapp.data.database.NotesDatabase;
import com.example.notesapp.data.entities.Note;
import com.example.notesapp.listeners.NotesListeners;
import com.example.notesapp.ui.adapters.NotesAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotesFragment extends Fragment implements NotesListeners {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        return view;
    }

    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    private int noteOnClickedPosition = -1;
    private View initialView;
    private FirebaseAuth mAuth;
    private AlertDialog dialogLogout;
    private EditText inputSearch;
    private FirebaseUser firebaseUser;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initialView = view;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        notesRecyclerView = view.findViewById(R.id.notesRecyclerView);
        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this::onNoteClicked);
        setRecyclerView();
        view.findViewById(R.id.imageAddNoteMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_notesFragment_to_createNotesFragment);
            }
        });
        getNotes();
        inputSearch = view.findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (noteList.size() != 0) {
                    notesAdapter.searchNotes(editable.toString());
                }
            }
        });


        if (getArguments() != null && getArguments().getBoolean("alreadyLoggedIn", false)) {
            //nothing to do
        } else {
            clearDatabase();
            getDataFromFireBase();
        }

        view.findViewById(R.id.imageArchive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_notesFragment_to_archiveFragment);
            }
        });

        view.findViewById(R.id.imageTheme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getAppTheme()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    setAppTheme("Dark");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    setAppTheme("Light");
                }
            }
        });

        view.findViewById(R.id.imageLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutNoteDialog();
            }
        });

    }

    private boolean getAppTheme() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Theme", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Mode", "Light");
        return theme.equals("Light");

    }

    private void setAppTheme(String theme) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Theme", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Mode", theme);
        editor.apply();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int source = viewHolder.getAdapterPosition();
            int destination = target.getAdapterPosition();
            Collections.swap(noteList, source, destination);
            notesAdapter.notifyItemMoved(source, destination);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    private void showLogoutNoteDialog() {
        if (dialogLogout == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_logout_user, this.initialView.findViewById(R.id.layoutAddUrlContainer));
            builder.setView(view);
            dialogLogout = builder.create();
            if (dialogLogout.getWindow() != null) {
                dialogLogout.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textYes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearDatabase();
                    mAuth.signOut();
                    dialogLogout.dismiss();
                    Navigation.findNavController(initialView).navigate(R.id.action_notesFragment_to_loginFragment);
                }
            });

            view.findViewById(R.id.textNo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogLogout.dismiss();
                }
            });
        }
        dialogLogout.show();
    }

    private void clearDatabase() {
        @SuppressLint("StaticFieldLeak")
        class clearDatabase extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getNotesDatabase(getActivity().getApplicationContext()).clearAllTables();
                return null;
            }

        }
        new clearDatabase().execute();
    }

    private void getNotes() {
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase.getNotesDatabase(getActivity().getApplicationContext()).noteDao().getAllNotes();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                //app is started so add all the data
                if (noteList.size() == 0) {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                } else {
                    //adding latest note just created
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                }
                notesRecyclerView.smoothScrollToPosition(0);
            }
        }
        new GetNotesTask().execute();
    }

    private void setRecyclerView() {
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notesRecyclerView.setAdapter(notesAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(notesRecyclerView);
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteOnClickedPosition = position;
        Bundle bundle = new Bundle();
        bundle.putBoolean("ViewOrEdit", true);
        bundle.putSerializable("note", note);
        Navigation.findNavController(initialView).navigate(R.id.action_notesFragment_to_createNotesFragment, bundle);
    }

    private void getDataFromFireBase() {
        CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("notes")
                .document(firebaseUser.getUid()).collection("myNotes");
        notebookRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                noteList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Note note = documentSnapshot.toObject(Note.class);
                    save(note);
                    noteList.add(note);
                }
                notesAdapter.notifyDataSetChanged();

            }
        });
    }

    private void save(Note note) {
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getNotesDatabase(getActivity().getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

        }
        new SaveNoteTask().execute();

    }
}