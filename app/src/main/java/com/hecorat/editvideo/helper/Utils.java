package com.hecorat.editvideo.helper;

import android.content.Context;

/**
 * Created by bkmsx on 11/11/2016.
 */
public class Utils {

    public static final int dpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp*density);
    }
}
