package com.example.collectable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collectable.databinding.ItemListRowBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddCollection extends AppCompatActivity {

    //Variables
    EditText titleET, itemGoalET, fieldNameET;
    Spinner template, iconSpinner, fieldDatatypeSpinner;
    Button addFieldBtn, createCollectionBtn;
    ListView fieldList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userUid;

    private static final String TAG = "AddCollectionActivity";

    ArrayList<CollectionFieldItem> colFieldArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collection);

        //instantiation
        titleET = findViewById(R.id.ccTitleET);
        itemGoalET = findViewById(R.id.ccItemGoalET);
        fieldNameET = findViewById(R.id.ccFieldNameET);
        template = findViewById(R.id.ccTemplateDropdown);
        iconSpinner = findViewById(R.id.ccIconSpinner);
        fieldDatatypeSpinner = findViewById(R.id.ccDatatypeSpinner);
        addFieldBtn = findViewById(R.id.ccAddFieldBtn);
        createCollectionBtn = findViewById(R.id.ccCreateCollectionBtn);
        fieldList = findViewById(R.id.ccFieldItemList);

        //Firebase instantiations
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userUid = mAuth.getCurrentUser().getUid();

        colFieldArrayList = new ArrayList<>();

        colFieldArrayList.add(new CollectionFieldItem(false, "Name", "text"));
        colFieldArrayList.add(new CollectionFieldItem(false, "Description", "text"));
        colFieldArrayList.add(new CollectionFieldItem(false, "Date Acquired", "date"));

        CollectionFieldAdapter fieldAdapter = new CollectionFieldAdapter(this, R.layout.collection_field_list_row, colFieldArrayList);

        fieldList.setAdapter(fieldAdapter);
    }

    public void addItemOnClick(View view) {
        String name = fieldNameET.getText().toString().trim();
        String datatype = fieldDatatypeSpinner.getSelectedItem().toString().trim();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Select a field name.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(datatype.equals("Select a datatype"))
        {
            Toast.makeText(this, "Select a field datatype.", Toast.LENGTH_SHORT).show();
            return;
        }

        colFieldArrayList.add(new CollectionFieldItem(true, name, datatype));

        CollectionFieldAdapter fieldAdapter = new CollectionFieldAdapter(this, R.layout.collection_field_list_row, colFieldArrayList);

        fieldList.setAdapter(fieldAdapter);
    }

    public void createNewCollectionOnClick(View view) {
        String title;
        String icon;
        int itemGoal;

        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<String> fieldDatatypes = new ArrayList<>();

        try{
            title = titleET.getText().toString().trim();
            itemGoal = Integer.parseInt(itemGoalET.getText().toString().trim());
            icon = iconSpinner.getSelectedItem().toString().trim();

            if(TextUtils.isEmpty(title))
            {
                Toast.makeText(this, "Select a title.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(icon.equals("Select an icon"))
            {
                Toast.makeText(this, "Select an icon.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(itemGoal==0)
            {
                Toast.makeText(this, "Choose an item goal.", Toast.LENGTH_SHORT).show();
                return;
            }

            for(int i = 0; i < colFieldArrayList.size(); i++)
            {
                //Get field name
                fieldNames.add(colFieldArrayList.get(i).getFieldName());

                //get field datatype
                fieldDatatypes.add(colFieldArrayList.get(i).getDatatype());
            }

            addCollectionToFirebase(title, itemGoal, icon, fieldNames, fieldDatatypes);
        } catch (Exception ex)
        {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    private void addCollectionToFirebase(String title, int itemGoal, String icon, ArrayList<String> fieldNames, ArrayList<String> fieldDatatypes)
    {
        try {           //https://firebase.google.com/docs/firestore/manage-data/add-data
            Map<String, Object> collection = new HashMap<>();
            collection.put("title", title);
            collection.put("itemGoal", itemGoal);
            collection.put("numItems", 0);
            collection.put("icon", icon);

            for (int i = 0; i < fieldNames.size(); i++)
            {
                collection.put(fieldNames.get(i), fieldDatatypes.get(i));
            }

            db.collection("users")
                    .document(userUid)
                    .collection("collections")
                    .document(UUID.randomUUID() + "_" + title)
                    .set(collection)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddCollection.this, "Collection added to Database", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Collection added to Firestore!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddCollection.this, "Failed to add Collection", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error adding collection to Firestore! " + e.getMessage());
                            return;
                        }
                    });

            Intent intent = new Intent(AddCollection.this, Home.class);
            startActivity(intent);
            finish();
        } catch(Exception ex)
        {
            Log.w(TAG, "Failed to add collection to FireStore!" + ex.getMessage());
        }
    }
}