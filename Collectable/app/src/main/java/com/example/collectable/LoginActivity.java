package com.example.collectable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    //vars
    TextView title, emailLabel, passwordLabel, forgotPassword;
    EditText emailInput, passwordInput;
    Button login;

    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        title = findViewById(R.id.title);
        emailLabel = findViewById(R.id.loginEmailLabelTextView);
        passwordLabel = findViewById(R.id.loginPasswordLabelTextView);
        emailInput = findViewById(R.id.loginEmailEditText);
        passwordInput = findViewById(R.id.loginPasswordEditText);
        login = findViewById(R.id.loginBtn);
        forgotPassword = findViewById(R.id.forgotPasswordTextView);

        mAuth = FirebaseAuth.getInstance();
    }

    public void loginOnClick(View view)
    {
        try {
            //get the email and password
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            //housekeeping
            if (TextUtils.isEmpty(email))
            {
                Toast.makeText(this,"Enter your email to login",Toast.LENGTH_SHORT).show();
                emailInput.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password))
            {
                Toast.makeText(this,"Enter your password to login",Toast.LENGTH_SHORT).show();
                passwordInput.requestFocus();
                return;
            }

            //attempt sign-in
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Login Successful");

                        //take to next screen
                        Intent intent = new Intent(LoginActivity.this, Home.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Error logging in!", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error logging in");

                        //clear boxes
                        passwordInput.setText("");
                        passwordInput.requestFocus();
                    }
                }
            });
        } catch (Exception ex)
        {
            Toast.makeText(this, "Unable to login!", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Catch log in: " + ex.getMessage());
        }
    }
}