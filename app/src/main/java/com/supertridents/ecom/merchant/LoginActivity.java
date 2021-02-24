package com.supertridents.ecom.merchant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    TextInputLayout phoneEntered,otp;
    Button btn_send_otp, btn_sign_in;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    String otpcode;
    LinearLayout layout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        btn_send_otp = findViewById(R.id.sign_in);
        btn_sign_in = findViewById(R.id.signIn);
        otp = findViewById(R.id.otp_text);
        phoneEntered = findViewById(R.id.logIn_phone);
        layout = findViewById(R.id.otpLayout);

        mAuth = FirebaseAuth.getInstance();

        btn_send_otp.setOnClickListener(v -> {

           String email = "+91"+phoneEntered.getEditText().getText().toString();
           // String email = "+918530899088";

            SharedPreferences.Editor edit = getSharedPreferences(MainActivity.INFO,MODE_PRIVATE).edit();
            edit.putString(MainActivity.EMAIL,email);
            edit.apply();
            edit.commit();

            if(email.isEmpty()) {
                phoneEntered.setError("Field cannot be empty");
                phoneEntered.requestFocus();
                return;
            }
            if(email.length()<10){
                phoneEntered.setError("Enter a valid phone");
                phoneEntered.requestFocus();
                return;
            }

            layout.setVisibility(View.VISIBLE);
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(email)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);

        });

        btn_sign_in.setOnClickListener(v -> {
            String uOtp = otp.getEditText().getText().toString().trim();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpcode, uOtp);
            signInWithPhoneAuthCredential(credential);
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                                // Start long running operation in a background thread
                                new Thread(new Runnable() {
                                    public void run() {
                                        while (progressStatus < 100) {
                                            progressStatus += 1;
                                            // Update the progress bar and display the
                                            //current value in the text view
                                            handler.post(new Runnable() {
                                                public void run() {
                                                    progressBar.setVisibility(View.VISIBLE);
                                                    progressBar.setProgress(progressStatus);
                                                }
                                            });
                                            try {
                                                // Sleep for 200 milliseconds.
                                                Thread.sleep(200);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }).start();


                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(LoginActivity.this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
             otpcode= s;
        }
    };
}