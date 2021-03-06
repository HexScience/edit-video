package com.hecorat.azplugin2.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.widget.RelativeLayout;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.database.VideoObject;
import com.hecorat.azplugin2.export.VideoHolder;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.main.Constants;
import com.hecorat.azplugin2.main.MainActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bkmsx on 31/10/2016.
 */
public class VideoTL extends AppCompatImageView {
    public Rect rectBackground, rectLeft, rectRight, rectTop, rectBottom;
    public Paint paint;
    public MediaMetadataRetriever retriever;
    public Bitmap defaultBitmap;
    public RelativeLayout.LayoutParams params;
    public String videoPath, audioPreview, originVideoPath;
    public MainActivity mActivity;
    public ArrayList<Bitmap> listBitmap;
    public VideoHolder videoHolder;

    public int width, height;
    public float videoRatio, originVideoRatio;
    public int MARGIN_LEFT_TIME_LINE;
    public int startInTimeLineMs, endInTimeLineMs;
    public int startTimeMs, endTimeMs;
    public int startPosition;
    public int left, right;
    public int min, max;
    public int durationVideo;
    public float volume, volumePreview;
    public boolean hasAudio;
    public int mBackgroundColor;
    public int mBorderColor;
    public boolean isHighLight;
    public int orderInList;
    public boolean isExists;
    public float leftSide, rightSide, bottomSide, topSide;
    private int videoWidth, videoHeight;

    VideoTL(Context context) {
        super(context);
    }

    public VideoTL(Context context, String videoPath, int height) {
        super(context);
        mActivity = (MainActivity) context;
        MARGIN_LEFT_TIME_LINE = mActivity.mLeftMarginTimeLine;
        this.videoPath = videoPath;
        originVideoPath = videoPath;
        this.height = height;

        listBitmap = new ArrayList<>();
        paint = new Paint();
        hasAudio = true;
        videoHolder = new VideoHolder();
        mBackgroundColor = ContextCompat.getColor(mActivity, R.color.background_timeline);
        mBorderColor = ContextCompat.getColor(mActivity, R.color.border_video_timeline);
        defaultBitmap = Utils.createDefaultBitmap();

        isExists = new File(videoPath).exists();
        if (isExists) {
            retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(videoPath);
                durationVideo = Integer.parseInt(retriever.extractMetadata
                        (MediaMetadataRetriever.METADATA_KEY_DURATION));
                videoWidth = Integer.parseInt(retriever.extractMetadata
                        (MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                videoHeight = Integer.parseInt(retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                int rotation = Integer.parseInt(retriever.extractMetadata
                        (MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                if (rotation == 90 || rotation == 270) {
                    int temp = videoHeight;
                    videoHeight = videoWidth;
                    videoWidth = temp;
                }
                originVideoRatio = (float) videoWidth / videoHeight;
                log("videoWidth = " + videoWidth);
                log("videoHeight = " + videoHeight);
                log("rotation = " + rotation);
                new AsyncTaskExtractFrame().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (RuntimeException e) {
                durationVideo = Constants.DEFAULT_DURATION;
                mBackgroundColor = Color.RED;
                isExists = false;
            }
        } else {
            durationVideo = Constants.DEFAULT_DURATION;
            mBackgroundColor = Color.RED;
        }
        startTimeMs = 0;
        endTimeMs = durationVideo;
        left = 0;
        width = durationVideo/ Constants.SCALE_VALUE;
        min = left;
        max = min + width;
        right = left + width;
        volume = 1f;
        volumePreview = 1f;

        params = new RelativeLayout.LayoutParams(width, height);
        setLayoutParams(params);
        drawTimeLineWith(startTimeMs, endTimeMs);
        setVideoSides(0, 1, 0, 1);

        // spare
        this.audioPreview = videoPath;
    }

    public void setVideoSides(float left, float right, float bottom, float top) {
        leftSide = left;
        rightSide = right;
        bottomSide = bottom;
        topSide = top;
        videoRatio = (right - left) / (top - bottom) * originVideoRatio;
    }

    public void restoreVideoObject(VideoObject video) {
        startTimeMs = Integer.parseInt(video.startTime);
        endTimeMs = Integer.parseInt(video.endTime);
        left = Integer.parseInt(video.left);
        volume = Float.parseFloat(video.volume);
        volumePreview = Float.parseFloat(video.volumePreview);
        leftSide = Float.parseFloat(video.leftSide);
        rightSide = Float.parseFloat(video.rightSide);
        bottomSide = Float.parseFloat(video.bottomSide);
        topSide = Float.parseFloat(video.topSide);
        videoRatio = (rightSide - leftSide) / (topSide - bottomSide) * originVideoRatio;
        drawTimeLineWith(startTimeMs, endTimeMs);
    }

    public VideoObject getVideoObject() {
        VideoObject videoObject = new VideoObject();
        videoObject.path = originVideoPath;
        videoObject.startTime = startTimeMs + "";
        videoObject.endTime = endTimeMs + "";
        videoObject.left = left + "";
        videoObject.orderInList = orderInList + "";
        videoObject.volume = volume + "";
        videoObject.volumePreview = volumePreview + "";
        videoObject.leftSide = leftSide + "";
        videoObject.rightSide = rightSide + "";
        videoObject.bottomSide = bottomSide + "";
        videoObject.topSide = topSide + "";
        return videoObject;
    }

    public void highlightTL() {
        isHighLight = true;
        mBorderColor = ContextCompat.getColor(mActivity, R.color.video_timeline_highline);
        invalidate();
    }

    public void setNormalTL(){
        isHighLight = false;
        mBorderColor = ContextCompat.getColor(mActivity, R.color.border_video_timeline);
        invalidate();
    }

    public void setLeftMargin(int value) {
        int moveX = value-left;
        left = value;
        params.leftMargin = left;
        min += moveX;
        max += moveX;
        right = left+width;
        setLayoutParams(params);
        updateTimeLineStatus();
    }

    //spare
    public void drawTimeLine(int leftPosition, int width) {
        int moveX = left - leftPosition;
        min += moveX;
        max += moveX;
        this.width = width;
        right = left + width;
        startPosition = left - min;
        params.width = width;
        setLayoutParams(params);
        rectBackground = new Rect(0, 0, width, height);
        rectLeft = new Rect(0, 0, Constants.BORDER_WIDTH, height);
        rectRight = new Rect(width- Constants.BORDER_WIDTH, 0, width, height);
        invalidate();
        updateTimeLineStatus();
    }

    public void drawTimeLineWith(int startTime, int endTime){
        startPosition = startTime/Constants.SCALE_VALUE;
        width = (endTime-startTime)/Constants.SCALE_VALUE;
        right = left + width;
        min = left - startPosition;
        params.width = width;
        params.leftMargin = left;
        setLayoutParams(params);
        rectBackground = new Rect(0, 0, width, height);
        rectLeft = new Rect(0, 0, Constants.BORDER_WIDTH, height);
        rectRight = new Rect(width- Constants.BORDER_WIDTH, 0, width, height);
        rectTop = new Rect(0, 0 , width, Constants.BORDER_WIDTH);
        rectBottom = new Rect(0, height-Constants.BORDER_WIDTH, width, height);

        this.startTimeMs = startTime;
        this.endTimeMs = endTime;
        startInTimeLineMs = (left - MARGIN_LEFT_TIME_LINE)* Constants.SCALE_VALUE;
        endInTimeLineMs = (right - MARGIN_LEFT_TIME_LINE) * Constants.SCALE_VALUE;
        invalidate();
    }

    public void updateTimeLineStatus() {
        startTimeMs = startPosition* Constants.SCALE_VALUE;
        endTimeMs = (right - min) * Constants.SCALE_VALUE;
        startInTimeLineMs = (left - MARGIN_LEFT_TIME_LINE)* Constants.SCALE_VALUE;
        endInTimeLineMs = (right - MARGIN_LEFT_TIME_LINE) * Constants.SCALE_VALUE;
    }

    public VideoHolder updateVideoHolder(){
        videoHolder.videoPath = videoPath;
        videoHolder.startTimeSecond = startTimeMs /1000f;
        videoHolder.durationSec = (endTimeMs - startTimeMs)/1000f;
        videoHolder.volume = volume;
        videoHolder.left = (int) (videoWidth * leftSide);
        videoHolder.top = (int) (videoHeight * (1 - topSide));
        videoHolder.width = (int) (videoWidth * (rightSide - leftSide));
        videoHolder.height = (int) (videoHeight * (topSide - bottomSide));
        videoHolder.ratio = videoRatio;
        videoHolder.changeVolume = volume != 1;
        videoHolder.crop = (rightSide - leftSide) != 1 || (topSide - bottomSide) != 1;
        return videoHolder;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayoutParams(params);
        paint.setColor(mBackgroundColor);
        canvas.drawRect(rectBackground, paint);
        for (int i=0; i<listBitmap.size(); i++){
            canvas.drawBitmap(listBitmap.get(i), i*150 - startPosition, 0, paint);
        }
        paint.setColor(mBorderColor);
        canvas.drawRect(rectTop, paint);
        canvas.drawRect(rectBottom, paint);
        canvas.drawRect(rectLeft, paint);
        canvas.drawRect(rectRight, paint);
    }

    private class AsyncTaskExtractFrame extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            int microDuration = durationVideo * 1000;
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

    private void log(String msg){
        Log.e("Video TimeLine",msg);
    }
}
