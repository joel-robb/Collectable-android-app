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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    //variables
    TextView title, emailLabel, nameLabel, surnameLabel, passwordLabel, confirmPasswordLabel;
    EditText emailInput, nameInput, surnameInput, passwordInput, confirmPasswordInput;
    Button registerBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //logging
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //typecast variables
        title = findViewById(R.id.title);
        emailLabel = findViewById(R.id.registerEmailLabelTextView);
        nameLabel = findViewById(R.id.registerNameLabelTextView);
        surnameLabel = findViewById(R.id.registerSurnameLabelTextView);
        passwordLabel = findViewById(R.id.registerPasswordLabelTextView);
        confirmPasswordLabel = findViewById(R.id.registerConfirmPasswordLabelTextView);

        emailInput = findViewById(R.id.registerEmailEditText);
        nameInput = findViewById(R.id.registerNameEditText);
        surnameInput = findViewById(R.id.registerSurnameEditText);
        passwordInput = findViewById(R.id.registerPasswordEditText);
        confirmPasswordInput = findViewById(R.id.registerConfirmPasswordEditText);

        registerBtn = findViewById(R.id.registerBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void registerOnClick(View view) {
        try {
            if (view.getId() == R.id.registerBtn) {
                String email = emailInput.getText().toString().trim();
                String firstname = nameInput.getText().toString().trim();
                String lastname = surnameInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String confirmPassword = confirmPasswordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(this, "Enter an Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(firstname)) {
                    Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(lastname)) {
                    Toast.makeText(this, "Enter a last name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Enter a Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(this, "Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //range checks --> dont allow less than 6
                if (password.length() < 6 || confirmPassword.length() < 6) {
                    Toast.makeText(this, "Password must be 6 characters or more", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirmPassword.equals(password)) {
                    //create this user
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser fbUser = mAuth.getCurrentUser();

                                //stores users name in firebaseAuth
                                String fullname = firstname + " " + lastname;
                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(fullname).build();
                                fbUser.updateProfile(profileChangeRequest);

                                //Pushing user details into a hashmap
                                Map<String, Object> userDetails = new HashMap<>();
                                userDetails.put("firstname", firstname);
                                userDetails.put("lastname", lastname);
                                userDetails.put("email", email);
                                userDetails.put("streakEnabled", 1);
                                userDetails.put("streakFrequency", "Once a month");
                                userDetails.put("streakCurrent", 0);
                                userDetails.put("streakLastUpdate", "29/06/2022");

                                //Add user info to Firestore DB - https://firebase.google.com/docs/firestore/manage-data/add-data
                                db.collection("users")
                                        .document(mAuth.getCurrentUser().getUid())
                                        .set(userDetails)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "User Successfully added to Firestore!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding user to Firestore!");
                                            }
                                        });

                                //Confirm Registration success
                                Toast.makeText(Register.this, "Registration Successful.", Toast.LENGTH_SHORT).show();

                                //redirect to login
                                Intent intent = new Intent(Register.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Register.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Password don't match!", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception ex)
        {
            Toast.makeText(this, "Error Registering - Try again later.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Unable to Register - " + ex.getMessage());
        }
    }
}