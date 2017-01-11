package com.hecorat.azplugin2.timeline;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hecorat.azplugin2.helper.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by TienDam on 11/28/2016.
 */

public class TimeText extends AppCompatTextView {
    RelativeLayout.LayoutParams params;

    public TimeText(Context context) {
        super(context);
    }

    public TimeText(Context context, int second) {
        super(context);
        String time = Utils.timeToText(second);
        setText(time);
        setTextSize(11);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        setLayoutParams(params);
    }

    public RelativeLayout.LayoutParams getParams(){
        return params;
    }

    private void log(String msg){
        Log.e("Log for TimeText",msg);
    }
}
