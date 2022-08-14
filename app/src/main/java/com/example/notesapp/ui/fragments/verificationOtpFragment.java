package com.example.notesapp.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.notesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class verificationOtpFragment extends Fragment {

    EditText input1, input2, input3, input4, input5, input6;
    Button verify;
    String getOtpBackend;
    private View initialView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verification_otp, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialView = view;
        input1 = initialView.findViewById(R.id.inputOtp1);
        input2 = initialView.findViewById(R.id.inputOtp2);
        input3 = initialView.findViewById(R.id.inputOtp3);
        input4 = initialView.findViewById(R.id.inputOtp4);
        input5 = initialView.findViewById(R.id.inputOtp5);
        input6 = initialView.findViewById(R.id.inputOtp6);
        verify = initialView.findViewById(R.id.submit);

        String number = getArguments().getString("mobileNUmber");

        getOtpBackend = getArguments().getString("verificationId");

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input1.getText().toString().trim().isEmpty() && !input2.getText().toString().trim().isEmpty() && !input3.getText().toString().trim().isEmpty() && !input4.getText().toString().trim().isEmpty() && !input5.getText().toString().trim().isEmpty() && !input6.getText().toString().trim().isEmpty()) {
                    String code = input1.getText().toString() +
                            input2.getText().toString() +
                            input3.getText().toString() + input4.getText().toString() +
                            input5.getText().toString() + input6.getText().toString();
                    if (getOtpBackend != null) {
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(getOtpBackend, code);
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //Navigation Intent to home activity
                                            Navigation.findNavController(initialView).navigate(R.id.action_verificationOtpFragment_to_notesFragment);
                                        } else {
                                            Toast.makeText(getContext(), "Invalid OTP ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(getContext(), "Check your connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "enter valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        numberOtpmove();

        initialView.findViewById(R.id.resendOtp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + number, 60,
                        TimeUnit.SECONDS, getActivity(),
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {

                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newverificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                getOtpBackend = newverificationId;
                                Toast.makeText(getContext(), "OTP send successfully", Toast.LENGTH_SHORT).show();

                            }
                        }

                );

            }
        });

    }

    private void numberOtpmove() {
        input1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        input3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}