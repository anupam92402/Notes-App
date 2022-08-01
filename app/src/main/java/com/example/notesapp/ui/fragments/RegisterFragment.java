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

public class RegisterFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    private FirebaseAuth mAuth;
    private TextView email, password;
    private View initialView;
    private Button register;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialView = view;
        initialize();
        view.findViewById(R.id.textLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String mailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (mailText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all the Details", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(mailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "User Registration Successful", Toast.LENGTH_SHORT).show();
                        sendUserEmailVerification();
                    } else {
                        Toast.makeText(getActivity(), "User Failed to Register", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserEmailVerification() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getActivity(), "Email Verification Sent", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    Navigation.findNavController(initialView).navigate(R.id.action_registerFragment_to_loginFragment);
                }
            });
        } else {
            Toast.makeText(getActivity(), "Failed to Send Verification", Toast.LENGTH_SHORT).show();
        }
    }

    public void initialize() {
        mAuth = FirebaseAuth.getInstance();
        email = initialView.findViewById(R.id.inputEmail);
        password = initialView.findViewById(R.id.inputPassword);
        register = initialView.findViewById(R.id.btnRegister);
    }

}