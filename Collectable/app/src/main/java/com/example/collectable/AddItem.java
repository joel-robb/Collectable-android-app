package com.example.collectable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddItem extends AppCompatActivity {

    //Variables
    TextView toCollectionLbl;
    EditText nameET;
    ImageView image1, image2, image3, image4;
    ListView fieldList;
    Button addItemBtn;
    ProgressDialog progressDialog; //pop up msg

    int picsAdded;
    ArrayList<Uri> imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String userUid;

    private String collectionUid;
    private String collectionName;

    ArrayList<AddItemField> fieldItemArray;

    private static final String TAG = "AddItemActivity";
    private static final int PICK_IMAGE_REQUEST = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        collectionUid = getIntent().getStringExtra("collectionId");     //https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application
        collectionName = getIntent().getStringExtra("collectionName");

        //instantiation
        toCollectionLbl = findViewById(R.id.aiToCollectionLbl);
        nameET = findViewById(R.id.aiNameET);
        image1 = findViewById(R.id.aiImage1);
        image2 = findViewById(R.id.aiImage2);
        image3 = findViewById(R.id.aiImage3);
        image4 = findViewById(R.id.aiImage4);
        fieldList = findViewById(R.id.aiFieldList);
        addItemBtn = findViewById(R.id.aiAddItemBtn);

        //Firebase instantiations
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userUid = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        picsAdded = 0;
        imageUri = new ArrayList<>();

        String lbl = "To: '" + collectionName + "'";
        toCollectionLbl.setText(lbl);

        //Display fields to be added
        fieldItemArray = new ArrayList<>();

        fieldItemArray.add(new AddItemField("Description", "Text"));
        fieldItemArray.add(new AddItemField("Date Acquired", "Text"));

        AddItemFieldAdapter fieldAdapter = new AddItemFieldAdapter(this, R.layout.item_field_list_row, fieldItemArray);
        fieldList.setAdapter(fieldAdapter);
    }

    /* Re-add?
    public void scanBarcodeOnClick(View view) {
    }*/

    public void addPictureOnClick(View view) {
        selectImage();
    }

    private void selectImage() {
        try{
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
        } catch(Exception ex)
        {
            Log.w(TAG, "Failed to get Image: " + ex.getMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)

        {
            imageUri.add(data.getData());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri.get(picsAdded));

                switch (picsAdded)
                {
                    case 0: image1.setImageBitmap(bitmap); break;
                    case 1: image2.setImageBitmap(bitmap); break;
                    case 2: image3.setImageBitmap(bitmap); break;
                    default: image4.setImageBitmap(bitmap); break;
                }
                picsAdded++;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        } else
        {
            Log.w(TAG, "Failed to meet reqs for image upload." + requestCode + " = " + 100 + " + " + resultCode + " = " + RESULT_OK + " + " + (data!=null) + " + " + (data.getData()!=null));
        }
    }

    private void uploadImages() {
        if(imageUri.size() > 0)     //https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/
        {
            for(int i = 0; i < imageUri.size() && i < 4; i++){
                StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

                ref.putFile(imageUri.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(AddItem.this, "Image uploaded to FB!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Image uploaded successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddItem.this, "Failed to Upload image!", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Unable to upload image: " + e.getMessage());
                    }
                });
            }
        }
    }

    public void addItemToCollectionOnClick(View view) {
        String name, description, dateAcquired;
        ArrayList<String> fieldArr = new ArrayList<>();

        try {
            name = nameET.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < fieldItemArray.size(); i++)
            {
                View view1 = fieldList.getChildAt(i);               //https://stackoverflow.com/questions/14030516/get-the-edittext-values-from-listview
                EditText et = view1.findViewById(R.id.aiFieldET);
                String field = et.getText().toString().trim();

                if (TextUtils.isEmpty(field)) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                } else
                {
                    fieldArr.add(field);
                }
            }

            description = fieldArr.get(0);
            dateAcquired = fieldArr.get(1);

            addItemToFirebase(name, description, dateAcquired);
            uploadImages();
        } catch(Exception ex){
            Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Unable to add Item: " + ex.getMessage());
        }
    }

    private void addItemToFirebase(String name, String description, String dateAcquired) {
        try{            //https://firebase.google.com/docs/firestore/manage-data/add-data
            Map<String, Object> item = new HashMap<>();
            item.put("name", name);
            item.put("description", description);
            item.put("dateAcquired", dateAcquired);

            db.collection("users")
                    .document(userUid)
                    .collection("collections")
                    .document(collectionUid)
                    .collection("items")
                    .document(UUID.randomUUID().toString())
                    .set(item)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddItem.this, "Item added to Database", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Item added to Firestore!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddItem.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error adding item to Firestore! " + e.getMessage());
                            return;
                        }
                    });

            updateNumItemsInCollection();

            Intent intent = new Intent(AddItem.this, ViewCollection.class);
            intent.putExtra("collectionId", collectionUid);        //https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application
            intent.putExtra("collectionName", collectionName);
            startActivity(intent);
            finish();
        } catch(Exception ex)
        {
            Log.w(TAG, "Unable to add Item: " + ex.getMessage());
        }
    }

    private void updateNumItemsInCollection()
    {
        try {
            db.collection("users")
                    .document(userUid)
                    .collection("collections")
                    .document(collectionUid)
                    .update("numItems", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Number of items updated successfully for collection: " + collectionUid);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "FAILED: Number of items failed to update for collection: " + collectionUid);
                        }
                    });
        } catch(Exception ex)
        {
            Log.w(TAG, "Unable to update number of items: " + ex.getMessage());
        }
    }
}