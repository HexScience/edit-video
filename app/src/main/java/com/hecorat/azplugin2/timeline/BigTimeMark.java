package com.hecorat.azplugin2.timeline;

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by TienDam on 11/28/2016.
 */

public class BigTimeMark extends FrameLayout {
    RelativeLayout.LayoutParams params;

    public BigTimeMark(Context context) {
        super(context);
        params = new RelativeLayout.LayoutParams(2, 10);
        setBackgroundColor(Color.WHITE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        setLayoutParams(params);
    }

    public RelativeLayout.LayoutParams getParams(){
        return params;
    }
}
