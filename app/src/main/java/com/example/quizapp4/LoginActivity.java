package com.example.quizapp4;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button loginButton;
    private TextView signUpButton;
    private FirebaseAuth mAuth;
    private Dialog progressDialog;
    private TextView dialogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
//        forgotPassButton = findViewById(R.id.forgot_password);
        signUpButton = findViewById(R.id.signup);
//        dialogText = findViewById(R.id.dialog_text);
//        dialogText.setText("Signing in...");

        progressDialog = new Dialog(LoginActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(view -> {
            if (validateData()) {
                login();
            }
        });

        signUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateData() {
        if (email.getText().toString().isEmpty()) {
            email.setError("Enter email");
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            password.setError("Enter password");
            return false;
        }

        return true;
    }

    private void login() {
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email.toString().trim(), password.toString().trim())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}