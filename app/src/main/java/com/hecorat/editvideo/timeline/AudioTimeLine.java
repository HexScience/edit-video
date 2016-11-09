package com.hecorat.editvideo.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hecorat.editvideo.main.Constants;
import com.hecorat.editvideo.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bkmsx on 05/11/2016.
 */
public class AudioTimeLine extends ImageView {
    public int min, max;
    public int left, right;
    public int width, height;
    public int startTime, endTime;
    public int start;
    public int duration;
    public String name;
    public Rect bacgroundRect;
    public Paint paint;
    public RelativeLayout.LayoutParams params;
    public MediaMetadataRetriever retriever;
    public Bitmap defaultBitmap;
    public ArrayList<Bitmap> listBitmap;

    public AudioTimeLine(Context context, String audioPath, int height, int leftMargin) {
        super(context);
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(audioPath);
        duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        name = new File(audioPath).getName();
        defaultBitmap = createDefaultBitmap();
        listBitmap = new ArrayList<>();

        startTime = 0;
        endTime = duration;
        width = endTime/ Constants.SCALE_VALUE;

        this.height = height;
        left = leftMargin;
        right = leftMargin + width;
        min = leftMargin;
        max = leftMargin + width;

        params = new RelativeLayout.LayoutParams(width, height);
        seekTimeLine(left, right);
        paint = new Paint();
//        new AsyncTaskExtractFrame().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void seekTimeLine(int left, int right){
        this.left = left;
        this.right = right;
        width = right - left;
        start = left - min; // it for visualation after
        startTime = start*Constants.SCALE_VALUE;
        endTime = (start + width)*Constants.SCALE_VALUE;
        bacgroundRect = new Rect(0, 0, width, height);
        params.width = width;
        params.leftMargin = left;
        setLayoutParams(params);
        invalidate();
    }

    public void moveTimeLine(int leftMargin) {
        int moveX = leftMargin - left;
        left += moveX;
        right += moveX;
        min += moveX;
        max += moveX;
        params.leftMargin = left;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(getResources().getColor(R.color.background_timeline));
        canvas.drawRect(bacgroundRect, paint);
        paint.setColor(Color.MAGENTA);
        paint.setTextSize(35);
        for (int i=0; i < listBitmap.size(); i++) {
            canvas.drawBitmap(listBitmap.get(i), i*150 - start, 0, paint);
        }
        canvas.drawText(name, 20, 50, paint);

    }

    private Bitmap createDefaultBitmap(){
        Paint paint = new Paint();
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        return bitmap;
    }

    private class AsyncTaskExtractFrame extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            int microDuration = duration * 1000;
            int timeStamp = 0;
            while (timeStamp < microDuration) {
                Bitmap bitmap = null;
                int currentTimeStamp = timeStamp;
                while (bitmap==null && currentTimeStamp < Math.min(timeStamp+2900000, microDuration)) {
                    bitmap = retriever.getFrameAtTime(timeStamp, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    currentTimeStamp += 60000;
                }
                if (bitmap == null) {
                    bitmap = defaultBitmap;
                }
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, 150, height, false);
                listBitmap.add(scaleBitmap);
                publishProgress();
                timeStamp += 3000000;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            invalidate();
        }
    }
}
