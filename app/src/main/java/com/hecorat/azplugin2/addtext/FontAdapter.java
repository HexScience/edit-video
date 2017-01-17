package com.hecorat.azplugin2.addtext;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hecorat.azplugin2.R;

import java.util.List;

/**
 * Created by TienDam on 11/17/2016.
 */

public class FontAdapter extends ArrayAdapter<String> {
    private int selected;

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
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getContext().getString(R.string.spinner_text));

        Typeface typeface = Typeface.createFromFile(getItem(position));
        textView.setTypeface(typeface);
        return view;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, null);
        TextView textView = (TextView) view.findViewById(R.id.spinner_textview);
        if (position == selected) {
            textView.setBackgroundColor(Color.RED);
        } else {
            textView.setBackgroundColor(Color.DKGRAY);
        }

        Typeface typeface = Typeface.createFromFile(getItem(position));
        textView.setTypeface(typeface);
        return view;
    }

}
