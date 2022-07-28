package com.example.collectable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CollectionItemAdapter extends ArrayAdapter<CollectionItem> {
    private Context mContext;
    private int mResource;


    public CollectionItemAdapter(@NonNull Context context, int resource, @NonNull ArrayList<CollectionItem> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource, parent, false);

        ImageView collectionIcon = convertView.findViewById(R.id.collectionIcon);
        TextView collectionName = convertView.findViewById(R.id.collectionName);
        TextView progressLbl = convertView.findViewById(R.id.collectionProgressLbl);
        ProgressBar progressPb = convertView.findViewById(R.id.collectionProgressPb);

        //collectionIcon.setImageResource(getItem(position).getIcon());
        collectionName.setText(getItem(position).getName());

        switch(getItem(position).getIcon()){
            case "CD":
                collectionIcon.setImageResource(R.drawable.col_icon_cd);
                break;
            case "Book":
                collectionIcon.setImageResource(R.drawable.col_icon_book);
                break;
            case "Wine Glass":
                collectionIcon.setImageResource(R.drawable.col_icon_wine);
                break;
            case "Car":
                collectionIcon.setImageResource(R.drawable.col_icon_car);
                break;
            case "Picture":
                collectionIcon.setImageResource(R.drawable.col_icon_picture);
                break;
            case "Figurine":
                collectionIcon.setImageResource(R.drawable.col_icon_figurine);
                break;
            case "Default":
                collectionIcon.setImageResource(R.drawable.col_icon_default);
                break;
        }

        String progress = "" + getItem(position).getNumItems() + "/" + getItem(position).getItemGoal();
        progressLbl.setText(progress);

        progressPb.setMax(getItem(position).getItemGoal());
        progressPb.setProgress(getItem(position).getNumItems());

        return convertView;
    }
}
