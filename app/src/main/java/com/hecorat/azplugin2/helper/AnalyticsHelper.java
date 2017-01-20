package com.hecorat.azplugin2.helper;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hecorat.azplugin2.main.AnalyticsApplication;

/**
 * Created by bkmsx on 1/18/2017.
 */

public class AnalyticsHelper {
    private static AnalyticsHelper sAnalyticsHelper;
    private String category, action;
    private int dimension;
    private String dimensionValue;
    private boolean isCustom;

    public static AnalyticsHelper getInstance() {
        if (sAnalyticsHelper == null) {
            sAnalyticsHelper = new AnalyticsHelper();
        }
        return sAnalyticsHelper;
    }

    public void send(Activity activity, String category, String action) {
        this.category = category;
        this.action = action;
        isCustom = false;
        new SendAnalyticsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, activity);
    }

    public void sendCustomDimension(Activity activity, int dimension, String dimensionValue) {
        this.dimension = dimension;
        this.dimensionValue = dimensionValue;
        isCustom = true;
        new SendAnalyticsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, activity);
    }

    private class SendAnalyticsTask extends AsyncTask<Activity, Void, Void> {
        @Override
        protected Void doInBackground(Activity... params) {
            Activity activity = params[0];
            Tracker tracker = ((AnalyticsApplication) activity.getApplication()).getDefaultTracker();
            HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder();
            if (!isCustom) {
                builder.setCategory(category)
                        .setAction(action);
            } else {
                builder.setCustomDimension(dimension, dimensionValue);
            }
            tracker.send(builder.build());
            return null;
        }
    }
}
