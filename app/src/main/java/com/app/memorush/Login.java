package com.app.memorush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText emailET, passwordET;
    Button loginBTN;
    ProgressBar progressBar;
    TextView registerTVBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = findViewById(R.id.etlgemail);
        passwordET = findViewById(R.id.etlgpw);
        progressBar = findViewById(R.id.caprogressbar);
        loginBTN = findViewById(R.id.loginbtn);
        registerTVBTN = findViewById(R.id.registertv);

        loginBTN.setOnClickListener((v) -> loginUser());
        registerTVBTN.setOnClickListener((v) -> startActivity(new Intent(Login.this, CreateAcc.class)));
    }

    void loginUser() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        boolean isValidated = validateData(email, password);
        if (!isValidated) {
            return;
        }

        loginAccountInFirebase(email, password);
    }

    void loginAccountInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()) {
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                } else {
                    Utility.showToast(Login.this, task.getException().getLocalizedMessage());
                }
            }
        });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            loginBTN.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginBTN.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Email is invalid");
            return false;
        }

        return true;
    }
}
