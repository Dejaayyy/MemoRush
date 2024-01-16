package com.app.memorush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAcc extends AppCompatActivity {

    EditText emailET, passwordET, cpasswordET;
    Button createaccBTN;
    ProgressBar progressBar;
    TextView loginTVBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

        emailET = findViewById(R.id.etemail);
        passwordET = findViewById(R.id.etpw);
        cpasswordET = findViewById(R.id.etcpw);
        createaccBTN = findViewById(R.id.cabtn);
        progressBar = findViewById(R.id.caprogressbar);
        loginTVBTN = findViewById(R.id.logintv);

        createaccBTN.setOnClickListener(v -> createAccount());
        loginTVBTN.setOnClickListener(v -> finish());
    }

    void createAccount() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String cpassword = cpasswordET.getText().toString();

        boolean isValidated = validateData(email, password, cpassword);
        if (!isValidated) {
            return;
        }

        createAccountInFirebase(email, password);
    }

    void createAccountInFirebase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()) {
                    Utility.showToast(CreateAcc.this, "Successfully account created");
                    firebaseAuth.signOut();
                    finish();
                } else {
                    Utility.showToast(CreateAcc.this,task.getException().getLocalizedMessage());
                }
            }
        });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            createaccBTN.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            createaccBTN.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String cpassword) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Email is invalid");
            return false;
        }
        if (!password.equals(cpassword)) {
            cpasswordET.setError("Password does not match");
            return false;
        }

        return true;
    }
}
