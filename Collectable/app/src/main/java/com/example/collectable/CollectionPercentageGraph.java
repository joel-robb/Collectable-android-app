package com.example.collectable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CollectionPercentageGraph extends AppCompatActivity {

    //variables
    private TextView pageTitle;
    private ImageView headerImg;
    private PieChart pieChart;

    int numItems;
    int itemGoal;

    private String collectionUid;
    private String collectionName;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userUid;

    private static final String TAG = "ViewCollectionStats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_percentage_graph);

        collectionUid = getIntent().getStringExtra("collectionUid");     //https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application
        collectionName = getIntent().getStringExtra("collectionName");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userUid = mAuth.getCurrentUser().getUid();

        //instantiation
        pageTitle = findViewById(R.id.pageTitle);
        headerImg = findViewById(R.id.headerImg);
        pieChart = findViewById(R.id.pieChart_view);

        pageTitle.setText(collectionName);

        try
        {
            showPieChart();
        } catch(Exception ex) {
            Toast.makeText(this, "Unable to populate chart.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Error showing chart: " + ex.getMessage());
        }
    }

    //method to handle charting
    public void showPieChart()
    {
        try
        {
            db.collection("users")                  //https://firebase.google.com/docs/firestore/query-data/get-data#java_12
                    .document(userUid)
                    .collection("collections")
                    .document(collectionUid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful())
                            {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()){
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    numItems = Integer.parseInt(document.get("numItems").toString());
                                    itemGoal = Integer.parseInt(document.get("itemGoal").toString());
                                    Log.d(TAG, "Num Items: " + numItems + "  Goal: " + itemGoal);

                                    setupPieChart();
                                    loadPieChartData();
                                } else
                                {
                                    Log.w(TAG, "Unable to retrieve data!");
                                }
                            } else {
                                Log.w(TAG, "Error getting documents! " + task.getException());
                                Toast.makeText(CollectionPercentageGraph.this, "Failed to get Data!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch(Exception ex) {
            Toast.makeText(this, "Unable to Read from db", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Error Reading: " + ex.getMessage());
        }
    }

    private void setupPieChart()
    {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(14f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Collection Progress");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
    }

    private void loadPieChartData()                 //https://www.youtube.com/watch?v=S3zqxVoIUig&ab_channel=LearntoDroid
    {
        double currentPercent = (double)numItems/(double)itemGoal;
        currentPercent = Math.round(currentPercent*100)/100.0;
        double percentRemaining = 1-currentPercent;

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry((float)currentPercent, "Current Items"));
        pieEntries.add(new PieEntry((float)percentRemaining, "Remaining Items"));

        ArrayList<Integer> colours = new ArrayList<>();

        colours.add(Color.parseColor("#FF35EC4B"));
        colours.add(Color.parseColor("#A1A1A1"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Collection Progress");
        pieDataSet.setColors(colours);

        PieData data = new PieData(pieDataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(16f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(700, Easing.EasingOption.EaseInOutQuad);
    }

    public void goBackToCollectionOnClick(View view) {
        onBackPressed();
    }
}