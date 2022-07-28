package com.example.collectable;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class AddItemFieldAdapter extends ArrayAdapter<AddItemField> {
    private Context mContext;
    private int mResource;

    public AddItemFieldAdapter(@NonNull Context context, int resource, @NonNull ArrayList<AddItemField> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView fieldName = convertView.findViewById(R.id.aiFieldLbl);
        EditText fieldInput = convertView.findViewById(R.id.aiFieldET);

        fieldName.setText(getItem(position).getFieldName());
        String etHint = "Enter the " + getItem(position).getFieldName();
        fieldInput.setHint(etHint);

        //Set expected datatype for edittext
        int datatypeNum;
        switch(getItem(position).getDatatype())
        {
            case "Text": datatypeNum = 1; break;
            case "Number": datatypeNum = 2; break;
            case "Date": datatypeNum =4; break;
            default: datatypeNum = 1; break;
        }
        fieldInput.setInputType(datatypeNum);

        return convertView;
    }
}
