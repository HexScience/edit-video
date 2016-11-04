package com.hecorat.editvideo;

import android.content.ClipData;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainTimeLineControl.OnControlTimeLineChanged, ExtraTimeLineControl.OnExtraTimeLineControlChanged{
    private LinearLayout mVideoViewLayout;
    private int mCountVideo = 2;
    private ArrayList<MainTimeLine> mVideoList;
    private RelativeLayout mLayoutVideo, mLayoutImage, mLayoutText, mLayoutAudio;
    private RelativeLayout mTimeLineVideo, mTimeLineImage, mTimeLineText, mTimeLineAudio;
    private boolean mMainControlVisiable;
    private MainTimeLine mSelectedMainTimeLine;
    private MainTimeLineControl mMainTimeLineControl;
    private RelativeLayout.LayoutParams mControlParams;
    private CustomHorizontalScrollView mScrollView;
    private int mTimeLineVideoHeight = 150;
    private int mTimeLineImageHeight = 70;
    private ExtraTimeLine mSelectedExtraTimeLine;
    private boolean mExtraControlVisiable;
    private ExtraTimeLineControl mExtraTimeLineControl;
    private ImageView mImageShadow;
    private RelativeLayout.LayoutParams mImageShadowParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        mVideoViewLayout = (LinearLayout) findViewById(R.id.video_view_layout);
        mLayoutVideo = (RelativeLayout) findViewById(R.id.layout_video);
        mTimeLineVideo = (RelativeLayout) findViewById(R.id.timeline_video);
        mScrollView = (CustomHorizontalScrollView) findViewById(R.id.scroll_view);
        mLayoutImage = (RelativeLayout) findViewById(R.id.layout_image);
        mTimeLineImage = (RelativeLayout) findViewById(R.id.timeline_image);
        mLayoutText = (RelativeLayout) findViewById(R.id.layout_text);
        mTimeLineText = (RelativeLayout) findViewById(R.id.timeline_text);
        mLayoutAudio = (RelativeLayout) findViewById(R.id.layout_audio);
        mTimeLineAudio = (RelativeLayout) findViewById(R.id.timeline_audio);
        mImageShadow = new ImageView(this);
        mImageShadow.setBackgroundResource(R.drawable.shadow);
        mImageShadowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        setVideoRatio();
        mVideoList = new ArrayList<>();
        String videoPath = Environment.getExternalStorageDirectory()+"/a.mp4";
        for (int i=0; i<mCountVideo; i++) {
            MainTimeLine mainTimeLine = new MainTimeLine(this, videoPath, mTimeLineVideoHeight);
            mainTimeLine.setOnClickListener(onMainTimeLineClick);
            mVideoList.add(mainTimeLine);
            mLayoutVideo.addView(mainTimeLine);
        }
        getLeftMargin(mCountVideo-1);
        mMainTimeLineControl = new MainTimeLineControl(this, mVideoList.get(0).width, mTimeLineVideoHeight);
        mControlParams = (RelativeLayout.LayoutParams) mMainTimeLineControl.getLayoutParams();
        mTimeLineVideo.addView(mMainTimeLineControl);
        toggleMainTimeLineControl();

        String imagePath = Environment.getExternalStorageDirectory()+"/a.png";
        ExtraTimeLine extraTimeLine = new ExtraTimeLine(this, imagePath, mTimeLineImageHeight);
        extraTimeLine.setLeftMargin(Constants.MARGIN_LEFT_TIME_LINE);
        extraTimeLine.setOnClickListener(onExtraTimeLineClick);
        extraTimeLine.setOnLongClickListener(onTimelineLongClick);
        mLayoutImage.addView(extraTimeLine);

        ExtraTimeLine extraTimeLine1 = new ExtraTimeLine(this, imagePath, mTimeLineImageHeight);
        extraTimeLine1.setLeftMargin(Constants.MARGIN_LEFT_TIME_LINE+extraTimeLine.width);
        extraTimeLine1.setOnClickListener(onExtraTimeLineClick);
        extraTimeLine1.setOnLongClickListener(onTimelineLongClick);
        mLayoutImage.addView(extraTimeLine1);

        mExtraTimeLineControl = new ExtraTimeLineControl(this, extraTimeLine.timeLineStatus.leftMargin, extraTimeLine.width, mTimeLineImageHeight);
        mTimeLineImage.addView(mExtraTimeLineControl);
        mSelectedExtraTimeLine = extraTimeLine;
        setExtraControlVisiable(false);

        mTimeLineImage.setOnDragListener(onDragListener);
        mTimeLineVideo.setOnDragListener(onDragListener);
        mTimeLineText.setOnDragListener(onDragListener);
    }

    View.OnLongClickListener onTimelineLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            mSelectedExtraTimeLine = (ExtraTimeLine) view;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(clipData, shadowBuilder, view, 0);
            } else {
                view.startDrag(clipData, shadowBuilder, view, 0);
            }
            mImageShadowParams.width = mSelectedExtraTimeLine.width;
            mImageShadowParams.height = mSelectedExtraTimeLine.height;
            mImageShadowParams.leftMargin = mSelectedExtraTimeLine.leftPosition;
            mTimeLineImage.addView(mImageShadow, mImageShadowParams);
            return false;
        }
    };

    View.OnDragListener onDragListener = new View.OnDragListener() {
        boolean inLayoutImage;
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            int x = (int) dragEvent.getX();
            log("X = "+x);
            if (view.equals(mTimeLineImage) || view.equals(mTimeLineVideo)) {
                if (x != 0) {
                    mImageShadowParams.leftMargin = x - 200;
                    mImageShadow.setLayoutParams(mImageShadowParams);

                }
                inLayoutImage = true;
            }

            if (view.equals(mTimeLineText) || view.equals(mTimeLineAudio)){
                if (x != 0) {
                    mImageShadowParams.leftMargin = x - 200;
                    mImageShadow.setLayoutParams(mImageShadowParams);
                }
                inLayoutImage = false;
            }

            if (inLayoutImage) {
//                if ()
            }

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    mTimeLineImage.removeView(mImageShadow);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:

                    break;
            }
            return true;
        }
    };

    View.OnClickListener onExtraTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mSelectedExtraTimeLine != null) {
                mExtraTimeLineControl.saveTimeLineStatus(mSelectedExtraTimeLine);
            }
            mSelectedExtraTimeLine = (ExtraTimeLine) view;
            setExtraControlVisiable(true);
            mExtraTimeLineControl.restoreTimeLineStatus(mSelectedExtraTimeLine);
        }
    };

    private void setExtraControlVisiable(boolean visiable){
        mExtraControlVisiable = visiable;
        if (visiable) {
            mExtraTimeLineControl.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
        } else {
            mExtraTimeLineControl.setVisibility(View.INVISIBLE);
            mScrollView.scroll = true;
        }
    }

    View.OnClickListener onMainTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMainControlVisiable = true;
            toggleMainTimeLineControl();
            saveTimLineStatus();
            mSelectedMainTimeLine = (MainTimeLine) view;
            showControl();
        }
    };

    private void showControl(){
        backupTimeLineStatus();
        mMainTimeLineControl.updateLayout(mMainTimeLineControl.start, mMainTimeLineControl.end, true);
    }

    private void backupTimeLineStatus(){
        MainTimeLineStatus timeLineStatus = mSelectedMainTimeLine.timeLineStatus;
        mControlParams.leftMargin = timeLineStatus.leftMargin;
        mMainTimeLineControl.start = timeLineStatus.start;
        mMainTimeLineControl.end = timeLineStatus.end;
        mMainTimeLineControl.currentMinPosition = timeLineStatus.currentMinPosition;
        mMainTimeLineControl.currentMaxPosition = timeLineStatus.currentMaxPosition;
        mMainTimeLineControl.maxPosition = timeLineStatus.maxPosition;
        log("Start Time: "+timeLineStatus.startTime+" End Time: "+timeLineStatus.endTime);
    }

    private void log(String msg) {
        Log.e("Edit video", msg);
    }

    private void saveTimLineStatus(){
        if (mSelectedMainTimeLine == null) {
            return;
        }
        MainTimeLineStatus timeLineStatus = mSelectedMainTimeLine.timeLineStatus;
        timeLineStatus.startTime = mSelectedMainTimeLine.startTime;
        timeLineStatus.endTime = mSelectedMainTimeLine.endTime;
        timeLineStatus.start = mMainTimeLineControl.start;
        timeLineStatus.end = mMainTimeLineControl.end;
        timeLineStatus.currentMinPosition = mMainTimeLineControl.currentMinPosition;
        timeLineStatus.currentMaxPosition = mMainTimeLineControl.currentMaxPosition;
        timeLineStatus.maxPosition = mMainTimeLineControl.maxPosition;
        timeLineStatus.leftMargin = mControlParams.leftMargin;
    }

    private void toggleMainTimeLineControl(){
        if (mMainControlVisiable) {
            mMainTimeLineControl.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
        } else {
            mMainTimeLineControl.setVisibility(View.INVISIBLE);
            mScrollView.scroll = true;
        }
    }

    @Override
    public void updateTimeLine(int start, int end) {
        mSelectedMainTimeLine.drawTimeLine(start, end);
        getLeftMargin(mCountVideo - 1);
    }

    @Override
    public void invisibleControl() {
        mMainControlVisiable = false;
        toggleMainTimeLineControl();
    }

    private int getLeftMargin(int position){
        MainTimeLine timeLine = mVideoList.get(position);
        int leftMargin;
        if (position == 0) {
            leftMargin = Constants.MARGIN_LEFT_TIME_LINE;
        } else {
            MainTimeLine mainTimeLine = mVideoList.get(position-1);
            leftMargin = mainTimeLine.width + getLeftMargin(position-1);
        }
        timeLine.setLeftMargin(leftMargin);
        return leftMargin;
    }

    private void setVideoRatio() {
        ViewGroup.LayoutParams params = mVideoViewLayout.getLayoutParams();
        params.width = (int) (params.height * 1.77);
        mVideoViewLayout.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
    }

    public void hideStatusBar() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(setSystemUiVisibility());
    }

    public static int setSystemUiVisibility() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }

    @Override
    public void updateExtraTimeLine(int start, int end) {
        mSelectedExtraTimeLine.drawTimeLine(start, end);
    }

    @Override
    public void invisibleExtraControl() {
        setExtraControlVisiable(false);
        mExtraTimeLineControl.saveTimeLineStatus(mSelectedExtraTimeLine);
    }
}
