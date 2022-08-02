package com.example.notesapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.notesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private TextView email, password;
    private View initialView;
    private Button login;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialView = view;
        initialize();
        view.findViewById(R.id.forgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
            }
        });

        view.findViewById(R.id.textRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String mailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (mailText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all the Details", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(mailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Navigation.findNavController(initialView).navigate(R.id.action_loginFragment_to_notesFragment);
                    } else {
                        Toast.makeText(getActivity(), "Account does not exists", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void initialize() {
        mAuth = FirebaseAuth.getInstance();
        email = initialView.findViewById(R.id.inputEmail);
        password = initialView.findViewById(R.id.inputPassword);
        login = initialView.findViewById(R.id.btnLogin);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Navigation.findNavController(initialView).navigate(R.id.action_loginFragment_to_notesFragment);
        }
    }
}