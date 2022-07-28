package com.example.collectable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Home extends AppCompatActivity {

    //variables
    FloatingActionButton addCollectionFAB;
    TextView pageTitle, welcomeLbl, userNameLbl;
    ImageView headerImg;
    ImageView userImg;
    Button streaksBtn;
    ListView collectionList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userUid;

    private String streakFrequency;
    private int currentStreak = 0;
    private boolean stEnabled;
    private Date lastUpdate;


    private static final String TAG = "HomeActivity";
    ArrayList<CollectionItem> collectionItemArrayList;
    //int numItems;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addCollectionFAB = findViewById(R.id.btnAddModule);

        //instantiation
        pageTitle = findViewById(R.id.pageTitle);
        headerImg = findViewById(R.id.headerImg);
        welcomeLbl = findViewById(R.id.welcomeMsg);
        userNameLbl = findViewById(R.id.userNameMsg);
        userImg = findViewById(R.id.userProfileBtn);
        streaksBtn = findViewById(R.id.streaksBtn);
        collectionList = findViewById(R.id.collectionListView);

        //Firebase instantiations
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userUid = mAuth.getCurrentUser().getUid();

        //initialiseStreaks();
        //streaksBtn.setText("currentStreak");



        userNameLbl.setText(mAuth.getCurrentUser().getDisplayName());

        //populate the listview from the db
        populateCollectionListView();

        addCollectionFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, AddCollection.class);
                startActivity(intent);
            }
        });

        collectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CollectionItem collection = (CollectionItem)collectionList.getItemAtPosition(i);

                Intent intent = new Intent(Home.this, ViewCollection.class);
                intent.putExtra("collectionId", collection.getUid());        //https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application
                intent.putExtra("collectionName", collection.getName());
                startActivity(intent);
                finish();
            }
        });
    }

    private void initialiseStreaks() {
        try {
            db.collection("users")
                    .document(userUid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful())
                            {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()){
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    int stEnabledInt = Integer.parseInt(document.get("streakEnabled").toString());
                                    stEnabled = false;
                                    if (stEnabledInt == 1)
                                    {
                                        stEnabled = true;
                                    }
                                    streakFrequency = document.get("streakFrequency").toString();
                                    currentStreak = Integer.parseInt(document.get("streakCurrent").toString());
                                    try {
                                        lastUpdate = new SimpleDateFormat("dd/mm/yyyy").parse(document.get("streakLastUpdate").toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else
                                {
                                    Log.w(TAG, "Unable to retrieve data!");
                                }
                            } else {
                                Log.w(TAG, "Error getting documents! " + task.getException());
                                Toast.makeText(Home.this, "Failed to get Data!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } catch(Exception ex)
        {
            Log.w(TAG, "Unable to initialise streak manager: " + ex.getMessage());
        }
    }

    private void populateCollectionListView() {
        ArrayList<CollectionItem> collectionItemArrayList = new ArrayList<>();

        try
        {
            db.collection("users")                          //https://firebase.google.com/docs/firestore/query-data/get-data#java_12
                    .document(userUid)
                    .collection("collections")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                            {
                                for(QueryDocumentSnapshot document : task.getResult())
                                {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    String title = document.get("title").toString();
                                    String icon = document.get("icon").toString();
                                    int itemGoal = Integer.parseInt(document.get("itemGoal").toString());
                                    int currentItems = Integer.parseInt(document.get("numItems").toString());

                                    //countCollectionItems(document.getId());
                                    //currentItems = numItems;

                                    collectionItemArrayList.add(new CollectionItem(icon, title, itemGoal, currentItems, document.getId()));
                                }

                                //Populate List View
                                try {
                                    CollectionItemAdapter collectionItemAdapter = new CollectionItemAdapter(Home.this, R.layout.collection_list_row, collectionItemArrayList);
                                    collectionList.setAdapter(collectionItemAdapter);
                                    Log.d(TAG, "List populated Successfully");
                                }catch (Exception ex)
                                {
                                    Log.w(TAG, "Failed to populate list: " + ex.getMessage());
                                }
                            } else {
                                Log.w(TAG, "Error getting documents! " + task.getException());
                                Toast.makeText(Home.this, "Failed to get Data!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception ex)
        {
            Toast.makeText(this, "Unable to read from Database", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Unable to populate listView: " + ex.getMessage());
        }
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(Home.this, Settings.class);
        startActivity(intent);
    }
}