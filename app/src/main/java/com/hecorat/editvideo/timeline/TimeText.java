package com.hecorat.editvideo.timeline;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by TienDam on 11/28/2016.
 */

public class TimeText extends TextView {
    RelativeLayout.LayoutParams params;
    public TimeText(Context context, int second) {
        super(context);
        Date date = new Date(second*1000);
        String time = new SimpleDateFormat("mm:ss", Locale.getDefault()).format(date);
        setText(time);
        setTextSize(13);
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
