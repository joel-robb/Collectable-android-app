package com.example.collectable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutScreen extends AppCompatActivity {

    TextView title, body1, body2, body3;
    ImageView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_screen);

        title = findViewById(R.id.pageTitle);
        body1 = findViewById(R.id.aboutBody1);
        body2 = findViewById(R.id.aboutBody2);
        body3 = findViewById(R.id.aboutBody3);
        header = findViewById(R.id.headerImg);
    }
}