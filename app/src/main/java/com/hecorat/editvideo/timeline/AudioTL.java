package com.hecorat.editvideo.timeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.database.AudioObject;
import com.hecorat.editvideo.export.AudioHolder;
import com.hecorat.editvideo.main.Constants;
import com.hecorat.editvideo.main.MainActivity;
import com.semantive.waveformandroid.waveform.soundfile.CheapSoundFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bkmsx on 05/11/2016.
 */
public class AudioTL extends ImageView {
    public int min, max;
    public int left, right;
    public int width, height;
    public int startTimeMs, endTimeMs;
    public int start;
    public int duration;
    public int startInTimelineMs, endInTimelineMs;
    public int leftMargin;
    public float volume, volumePreview;
    public int orderInList;
    public int projectId;
    public boolean isExists;
    public int background, nameColor;
    private float range=0;
    private float minGain=0;
    private boolean soundWaveReady;
    private float step;
    private int[] frameGains;

    public String name, audioPath, audioPreview;
    public Rect bacgroundRect, rectTop,rectBottom, rectLeft, rectRight;
    public Paint paint;
    public RelativeLayout.LayoutParams params;
    public MediaMetadataRetriever retriever;

    public AudioHolder audioHolder;
    public MainActivity mActivity;
    private CheapSoundFile soundFile;

    public static final int SPACE = 1;
    public static final float MIN_LIMIT = 0.85f;

    public AudioTL(Context context, String audioPath, int height, int leftMargin) {
        super(context);
        mActivity = (MainActivity) context;
        projectId = mActivity.mProjectId;
        this.audioPath = audioPath;
        audioPreview = audioPath;//spare
        audioHolder = new AudioHolder();
        paint = new Paint();
        this.height = height;
        left = leftMargin;
        this.leftMargin = mActivity.mLeftMarginTimeLine;
        background = ContextCompat.getColor(mActivity, R.color.background_timeline);

        isExists = new File(audioPath).exists();
        if (isExists) {
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(audioPath);
            duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            name = new File(audioPath).getName();
            nameColor = Color.MAGENTA;
        } else {
            duration = Constants.DEFAULT_DURATION;
            name = "??????";
            background = Color.RED;
            nameColor = Color.WHITE;
        }

        width = duration / Constants.SCALE_VALUE;
        right = leftMargin + width;
        min = leftMargin;
        max = leftMargin + width;
        volume = 1f;
        volumePreview = 1f;

        params = new RelativeLayout.LayoutParams(width, height);
        seekTimeLine(left, right);

        updateTimeLineStatus();

        soundWaveReady = false;
        if (isExists) {
            new LoadWaveSoundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void restoreAudioObject(AudioObject audio) {
        projectId = audio.projectId;
        startTimeMs = Integer.parseInt(audio.startTime);
        endTimeMs = Integer.parseInt(audio.endTime);
        left = Integer.parseInt(audio.left);
        volume = Float.parseFloat(audio.volume);
        volumePreview = Float.parseFloat(audio.volumePreview);

        width = (endTimeMs - startTimeMs) / Constants.SCALE_VALUE;
        right = left + width;
        start = startTimeMs / Constants.SCALE_VALUE;
        min = left - start;
        max = min + duration / Constants.SCALE_VALUE;

        drawTimeLine();
    }

    public AudioObject getAudioObject() {
        AudioObject audioObject = new AudioObject();
        audioObject.projectId = projectId;
        audioObject.path = audioPath;
        audioObject.startTime = startTimeMs + "";
        audioObject.endTime = endTimeMs + "";
        audioObject.left = left + "";
        audioObject.orderInList = orderInList + "";
        audioObject.volume = volume + "";
        audioObject.volumePreview = volumePreview + "";
        return audioObject;
    }

    public void updateAudioHolder(){
        audioHolder.audioPath = audioPath;
        audioHolder.startTimeMs = startTimeMs /1000f;
        audioHolder.startInTimeLineMs = startInTimelineMs /1000f;
        audioHolder.duration = (endInTimelineMs - startInTimelineMs)/1000f;
        audioHolder.volume = volume;
    }

    public void seekTimeLine(int left, int right){
        this.left = left;
        this.right = right;
        width = right - left;
        start = left - min;

        drawTimeLine();
    }

    private void drawTimeLine(){
        bacgroundRect = new Rect(0, 0, width, height);
        rectTop = new Rect(0, 0, width, Constants.BORDER_WIDTH);
        rectBottom = new Rect(0, height- Constants.BORDER_WIDTH, width, height);
        rectLeft = new Rect(0, 0, Constants.BORDER_WIDTH, height);
        rectRight = new Rect(width- Constants.BORDER_WIDTH, 0, width, height);
        params.width = width;
        params.leftMargin = left;
        setLayoutParams(params);
        invalidate();
        updateTimeLineStatus();
    }

    public void moveTimeLine(int leftMargin) {
        int moveX = leftMargin - left;
        left += moveX;
        right += moveX;
        min += moveX;
        max += moveX;
        params.leftMargin = left;
        invalidate();
        updateTimeLineStatus();
    }

    public void updateTimeLineStatus(){
        startTimeMs = start* Constants.SCALE_VALUE;
        endTimeMs = (start + width)* Constants.SCALE_VALUE;
        startInTimelineMs = (left-leftMargin)* Constants.SCALE_VALUE;
        endInTimelineMs = (right-leftMargin)* Constants.SCALE_VALUE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw background
        paint.setColor(background);
        canvas.drawRect(bacgroundRect, paint);

        //draw Sound Wave
        if (soundWaveReady) {
            paint.setColor(ContextCompat.getColor(mActivity, R.color.wave_sound_color));

            int end = start + width;
            for (int i = start; i < end; i++) {
                drawWaveform(canvas, i * SPACE, step / SPACE, height / 2, paint);
            }
        }

        //draw Audio name
        paint.setColor(nameColor);
        paint.setTextSize(35);
        canvas.drawText(name, 20, 50, paint);

        //draw borders
        paint.setColor(ContextCompat.getColor(mActivity, R.color.border_timeline_color));
        canvas.drawRect(rectTop, paint);
        canvas.drawRect(rectBottom, paint);
        canvas.drawRect(rectLeft, paint);
        canvas.drawRect(rectRight, paint);
    }

    protected void drawWaveform(Canvas canvas, int x, float step, int centerVertical, Paint paint) {
        int heightWave = (int) (getScaledHeight((int)(x * step)) * centerVertical);
        drawVerticalLine( canvas, x - start, centerVertical - heightWave, centerVertical + 1 + heightWave, paint);
    }

    protected void drawVerticalLine(Canvas canvas, int x, int y0, int y1, Paint paint) {
        canvas.drawLine(x, y0, x, y1, paint);
    }

    protected float getScaledHeight(int x) {
        float value = (frameGains[x] - minGain) / range;
        if (value < 0.0)
            value = 0.0f;
        if (value > 1.0)
            value = 1.0f;
        return value;
    }

    public void initSoundWave() {
        frameGains = soundFile.getFrameGains();
        step = (float) soundFile.getNumFrames() / width;

        ArrayList<Float> listFrameGain = new ArrayList<>();
        for (float gain : frameGains) {
            listFrameGain.add(gain);
        }
        float average = getAverage(listFrameGain);
        listFrameGain.clear();
        for (float gain : frameGains) {
            if (gain > average/2) {
                listFrameGain.add(gain);
            }
        }
        average = getAverage(listFrameGain);

        ArrayList<Float> listFrameMins = new ArrayList<>();
        ArrayList<Float> listFrameMaxs = new ArrayList<>();
        for (float gain : listFrameGain) {
            if (gain < average && gain > average*MIN_LIMIT) {
                listFrameMins.add(gain);
            } else if (gain > average) {
                listFrameMaxs.add(gain);
            }
        }

        minGain = average;
        for (float gain : listFrameMins) {
            minGain = Math.min(minGain, gain);
        }

        float maxGain = average;
        for (float gain : listFrameMaxs) {
            maxGain = Math.max(maxGain, gain);
        }

        range = maxGain - minGain;
    }

    private float getAverage(ArrayList<Float> list) {
        float sum = 0;
        for (float gain : list) {
            sum += gain;
        }
        return sum/list.size();
    }

    private class LoadWaveSoundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                soundFile = CheapSoundFile.create(audioPath, listener);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initSoundWave();
            soundWaveReady = true;
            invalidate();
        }
    }

    CheapSoundFile.ProgressListener listener = new CheapSoundFile.ProgressListener() {
        @Override
        public boolean reportProgress(double v) {
            return true;
        }
    };



    private void log(String msg) {
        Log.e("Log for audio", msg);
    }
}
