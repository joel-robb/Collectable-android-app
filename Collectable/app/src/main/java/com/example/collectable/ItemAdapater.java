package com.example.collectable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ItemAdapater extends ArrayAdapter<Item> {
    private Context mContext;
    private int mResource;

    public ItemAdapater(@NonNull Context context, int resource, @NonNull ArrayList<Item> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource, parent, false);

        ImageView imageView = convertView.findViewById(R.id.itemImage);
        TextView txtName = convertView.findViewById(R.id.itemNameLbl);
        TextView txtDes = convertView.findViewById(R.id.itemFieldLbl);

        if (getItem(position).getImage() != 0)
        {
            imageView.setImageResource(getItem(position).getImage());
        }
        txtName.setText(getItem(position).getName());
        txtDes.setText(getItem(position).getField());

        return convertView;
    }
}
