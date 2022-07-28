package com.example.collectable;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class CollectionFieldAdapter extends ArrayAdapter<CollectionFieldItem> {
    private Context mContext;
    private int mResource;

    public CollectionFieldAdapter(@NonNull Context context, int resource, @NonNull ArrayList<CollectionFieldItem> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView fieldName = convertView.findViewById(R.id.caFieldName);
        TextView datatype = convertView.findViewById(R.id.caFieldDataType);
        ImageView removeBtn = convertView.findViewById(R.id.caRemoveBtn);

        fieldName.setText(getItem(position).getFieldName());
        datatype.setText(getItem(position).getDatatype());

        Boolean removable = getItem(position).isRemovable();

        if (!removable)
        {
            removeBtn.setColorFilter(ContextCompat.getColor(mContext, android.R.color.darker_gray),
                    PorterDuff.Mode.MULTIPLY);
        }

        return convertView;
    }
}
