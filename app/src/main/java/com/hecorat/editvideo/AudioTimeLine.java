package com.hecorat.editvideo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;

/**
 * Created by bkmsx on 05/11/2016.
 */
public class AudioTimeLine extends ImageView {
    int min, max;
    int left, right;
    int width, height;
    int startTime, endTime;
    int start;
    int leftMargin;
    int duration;
    String name;
    Rect bacgroundRect;
    Paint paint;
    RelativeLayout.LayoutParams params;

    public AudioTimeLine(Context context, String audioPath, int height, int leftMargin) {
        super(context);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(audioPath);
        duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        name = new File(audioPath).getName();

        startTime = 0;
        endTime = duration;
        width = endTime/Constants.SCALE_VALUE;

        this.height = height;
        this.leftMargin = leftMargin;
        left = leftMargin;
        right = leftMargin + width;
        min = leftMargin;
        max = leftMargin + width;
        drawTimeLine(left, right, leftMargin);
        params = new RelativeLayout.LayoutParams(width, height);
        params.leftMargin = leftMargin;
        paint = new Paint();
    }

    public void drawTimeLine(int left, int right, int leftMargin){
        this.left = left;
        this.right = right;
        this.leftMargin = leftMargin;
        width = right - left;
        start = left - leftMargin; // it for visualation after
        startTime = start*Constants.SCALE_VALUE;
        min = leftMargin;
        max = leftMargin + duration/Constants.SCALE_VALUE;
        endTime = (start + width)*Constants.SCALE_VALUE;
        bacgroundRect = new Rect(0, 0, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(getResources().getColor(R.color.background_timeline));
        canvas.drawRect(bacgroundRect, paint);
        paint.setColor(Color.MAGENTA);
        paint.setTextSize(35);
        canvas.drawText(name, 20, 50, paint);
    }
}
