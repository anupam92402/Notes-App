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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    private FirebaseAuth mAuth;
    private TextView email, backToLogin;
    private View initialView;
    private Button recoverPassword;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialView = view;
        initialize();
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_forgotPasswordFragment_to_loginFragment);
            }
        });
        recoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recoverPassword();
            }
        });
    }

    private void recoverPassword() {
        String mailText = email.getText().toString().trim();
        if (mailText.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your email id", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.sendPasswordResetEmail(mailText).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Mail sent to your email id", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(initialView).navigate(R.id.action_forgotPasswordFragment_to_loginFragment);
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
        recoverPassword = initialView.findViewById(R.id.btnRecoverPassword);
        backToLogin = initialView.findViewById(R.id.textBackToLogin);
    }

}