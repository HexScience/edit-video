package com.hecorat.azplugin2.helper.picktime;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.helper.Utils;

/**
 * Created by bkmsx on 1/11/2017.
 */

public class PickTimePanel extends LinearLayout implements View.OnClickListener,
        DialogTimePicker.OnDialogTimePickerListener {
    TextView textMin, textMax;
    OnPickTimeListener callback;
    int minSecond, maxSecond;
    TimeData timeDataMin, timeDataMax, timeDataDuration;
    AppCompatActivity activity;

    public PickTimePanel(Context context) {
        super(context);
    }

    public PickTimePanel(AppCompatActivity activity, OnPickTimeListener listener, int durationMs) {
        super(activity);
        this.activity = activity;
        callback = listener;
        LinearLayout layout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.pick_time_layout, null);
        textMax = (TextView) layout.findViewById(R.id.text_selected_max_value_trim);
        textMin = (TextView) layout.findViewById(R.id.text_selected_min_value_trim);
        textMin.setPaintFlags(textMin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textMax.setPaintFlags(textMax.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textMin.setOnClickListener(this);
        textMax.setOnClickListener(this);
        timeDataMin = new TimeData(0);
        timeDataMax = new TimeData(durationMs);
        timeDataDuration = new TimeData(durationMs);
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(layout, params);
    }

    @Override
    public void onTimePickerChanged() {
        callback.onPickTimeCompleted(timeDataMin.milisecs, timeDataMax.milisecs);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(textMin)) {
            DialogTimePicker.newInstance(getContext(), timeDataMin, timeDataMax, this)
                    .show(activity.getSupportFragmentManager(), "pick time min");
        }
        else if (v.equals(textMax)) {
            DialogTimePicker.newInstance(getContext(), timeDataMax, timeDataDuration, this)
                    .show(activity.getSupportFragmentManager(), "pick time min");
        }
    }

    public void setTextValues(int minMs, int maxMs) {
        timeDataMin.setMsec(minMs);
        timeDataMax.setMsec(maxMs);
        minSecond = minMs / 1000;
        maxSecond = maxMs / 1000;
        textMin.setText(Utils.timeToText(minSecond));
        textMax.setText(Utils.timeToText(maxSecond));
    }

    public interface OnPickTimeListener {
        void onPickTimeCompleted(int minMs, int maxMs);
    }
}
