package com.example.collectable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class ViewCollection extends AppCompatActivity {

    //variables
    TextView pageTitle;
    ImageView headerImg;
    EditText searchBar;
    Button filterBtn;
    ListView itemList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userUid;

    private String collectionUid;
    private String collectionName;
    private String searchTerm = "";

    private static final String TAG = "ViewCollectionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_collection);

        collectionUid = getIntent().getStringExtra("collectionId");     //https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application
        collectionName = getIntent().getStringExtra("collectionName");

        //instantiation
        pageTitle = findViewById(R.id.pageTitle);
        headerImg = findViewById(R.id.headerImg);
        searchBar = findViewById(R.id.itemSearch);
        filterBtn = findViewById(R.id.filterBtn);
        itemList = findViewById(R.id.itemListView);

        //Firebase instantiations
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userUid = mAuth.getCurrentUser().getUid();

        pageTitle.setText(collectionName);

        //populate the item list
        populateItemList();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchTerm = searchBar.getText().toString();

                populateItemList();
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    //Populate list to display
    private void populateItemList() {
        ArrayList<Item> itemArrayList = new ArrayList<>();
        
        try {
            db.collection("users")                  //https://firebase.google.com/docs/firestore/query-data/get-data#java_12
                    .document(userUid)
                    .collection("collections")
                    .document(collectionUid)
                    .collection("items")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                            {
                                for(QueryDocumentSnapshot document : task.getResult())
                                {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    String name = document.get("name").toString();
                                    String field = document.get("description").toString();

                                    String nameLower = name.toLowerCase();
                                    String searchLower = searchTerm.toLowerCase();

                                    if(nameLower.contains(searchLower))
                                    {
                                        itemArrayList.add(new Item(0, name, field));
                                    }
                                }
                                //Populate List View
                                try {
                                    ItemAdapater itemAdapater = new ItemAdapater(ViewCollection.this, R.layout.item_list_row, itemArrayList);
                                    itemList.setAdapter(itemAdapater);
                                    Log.d(TAG, "List populated Successfully");
                                }catch (Exception ex)
                                {
                                    Log.w(TAG, "Failed to populate list: " + ex.getMessage());
                                }
                            } else {
                                Log.w(TAG, "Error getting documents! " + task.getException());
                                Toast.makeText(ViewCollection.this, "Failed to get Data!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception ex)
        {
            Log.w(TAG, "Unable to Populate List: " + ex.getMessage());
            Toast.makeText(this, "Unable to read items from Database", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToAddItemOnClick(View view) {
        Intent intent = new Intent(ViewCollection.this, AddItem.class);
        intent.putExtra("collectionId", collectionUid);
        intent.putExtra("collectionName", collectionName);
        startActivity(intent);
    }

    public void goToCollectionStats(View view) {
        Intent intent = new Intent(ViewCollection.this, CollectionPercentageGraph.class);
        intent.putExtra("collectionName", collectionName);
        intent.putExtra("collectionUid", collectionUid);
        startActivity(intent);
    }

    public void onBackPressed() {
        startActivity(new Intent(ViewCollection.this,Home.class));
    }

    public void filterListOnClick(View view) {

    }
}