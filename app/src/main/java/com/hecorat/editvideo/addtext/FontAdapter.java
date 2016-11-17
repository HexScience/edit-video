package com.hecorat.editvideo.addtext;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hecorat.editvideo.R;

import java.io.File;
import java.util.List;

/**
 * Created by TienDam on 11/17/2016.
 */

public class FontAdapter extends ArrayAdapter<String>{
    int selected;
    public FontAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        setSelectedItem(0);
    }

    public void setSelectedItem(int position) {
        selected = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, null);
        TextView textView = (TextView) view.findViewById(R.id.spinner_textview);
        if (position == selected) {
            textView.setBackgroundColor(Color.RED);
        } else {
            textView.setBackgroundColor(Color.DKGRAY);
        }

        textView.setText(getItem(position));
        return view;
    }

}
