package com.example.notesapp.ui.fragments;

import android.os.Bundle;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class enterNumberFragment extends Fragment {
    EditText enterNumber;
    Button getOTP;
    private View initialView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialView = view;
        enterNumber = initialView.findViewById(R.id.input_mobileNumber);
        getOTP = initialView.findViewById(R.id.getOtp);

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!enterNumber.getText().toString().trim().isEmpty()) {
                    if ((enterNumber.getText().toString().trim()).length() == 10) {
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + enterNumber.getText().toString(), 60,
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
                                    public void onCodeSent(@NonNull String verificationId,
                                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("mobileNumber", enterNumber.getText().toString());
                                        bundle.putString("verificationId", verificationId);
                                        Navigation.findNavController(initialView).navigate(R.id.action_enterNumberFragment_to_verificationOtpFragment, bundle);

                                    }
                                }

                        );
                        // Navigation.findNavController(view).navigate(R.id.action_Enter);
                    } else {
                        Toast.makeText(getContext(), "Enter correct number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Enter number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter_number, container, false);
    }
}