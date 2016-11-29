package com.hecorat.editvideo.timeline;

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by TienDam on 11/28/2016.
 */

public class SmallTimeMark extends FrameLayout {
    RelativeLayout.LayoutParams params;

    public SmallTimeMark(Context context) {
        super(context);
        params = new RelativeLayout.LayoutParams(2, 5);
        setBackgroundColor(Color.GRAY);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        setLayoutParams(params);
    }

    public RelativeLayout.LayoutParams getParams(){
        return params;
    }
}
