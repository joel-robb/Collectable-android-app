package com.example.collectable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Settings extends AppCompatActivity {

    //Variables
    ImageView headerImg;
    TextView userName, userEmail, currentStreaksSetting;
    Button btnLogOut, btnStreaksInfo;
    SwitchCompat swtNotifcations, swtStreaks;
    Spinner streakFrequency;
    AlertDialog.Builder builder;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Instantiations
        headerImg = findViewById(R.id.headerImg);
        userName = findViewById(R.id.userNameLbl);
        userEmail = findViewById(R.id.userEmailLbl);
        btnLogOut = findViewById(R.id.logoutBtn);
        btnStreaksInfo = findViewById(R.id.streaksHelp);
        swtNotifcations = findViewById(R.id.notifSwitch);
        swtStreaks = findViewById(R.id.streakSwitch);
        streakFrequency = findViewById(R.id.streakFrequencyDropdown);
        currentStreaksSetting = findViewById(R.id.currentStreaksSettingLbl);

        builder = new AlertDialog.Builder(this);

        swtStreaks.setChecked(true);

        //Firebase instantiations
        mAuth = FirebaseAuth.getInstance();

        userName.setText(mAuth.getCurrentUser().getDisplayName());
        userEmail.setText(mAuth.getCurrentUser().getEmail());

        swtStreaks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                } else {

                }
            }
        });
    }

    public void logOutOnClick(View view) {
        mAuth.signOut();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    public void displayStreaksHelpOnClick(View view) {
        builder.setMessage(R.string.streakAboutBody);

        AlertDialog alert = builder.create();
        alert.setTitle("What are streaks?");
        alert.show();
    }

    public void updateStreakFrequencyOnClick(View view) {
        String selected = streakFrequency.getSelectedItem().toString();
        currentStreaksSetting.setText("Currently set to: " + selected);
    }
}