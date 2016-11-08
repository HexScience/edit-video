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

public class MainActivity extends AppCompatActivity implements MainTimeLineControl.OnControlTimeLineChanged, ExtraTimeLineControl.OnExtraTimeLineControlChanged, AudioTimeLineControl.OnAudioControlTimeLineChanged {
    private LinearLayout mVideoViewLayout;

    private ArrayList<MainTimeLine> mVideoList;
    private RelativeLayout mLayoutVideo, mLayoutImage, mLayoutText, mLayoutAudio;
    private RelativeLayout mTimeLineVideo, mTimeLineImage, mTimeLineText, mTimeLineAudio;
    private MainTimeLine mSelectedMainTimeLine;
    private MainTimeLineControl mMainTimeLineControl;
    private CustomHorizontalScrollView mScrollView;
    private LinearLayout mLayoutScrollView;

    private ExtraTimeLine mSelectedExtraTimeLine;
    private ExtraTimeLineControl mExtraTimeLineControl;
    private ImageView mImageShadow;
    private RelativeLayout.LayoutParams mImageShadowParams;
    private AudioTimeLineControl mAudioTimeLineControl;
    private AudioTimeLine mSelectedAudioTimeLine;
    private ImageView mShadowIndicator;
    private RelativeLayout.LayoutParams mShadowIndicatorParams;

    public static final int DRAG_VIDEO = 0;
    public static final int DRAG_EXTRA = 1;
    public static final int DRAG_AUDIO = 2;
    private int mDragCode = DRAG_VIDEO;
    private int mCountVideo = 2;
    private int mTimeLineVideoHeight = 150;
    private int mTimeLineImageHeight = 70;

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
        mLayoutScrollView = (LinearLayout) findViewById(R.id.layout_scrollview);
        mImageShadow = new ImageView(this);
        mImageShadow.setBackgroundResource(R.drawable.shadow);
        mImageShadowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mShadowIndicator = new ImageView(this);
        mShadowIndicator.setBackgroundResource(R.drawable.shadow_indicator);
        mShadowIndicatorParams = new RelativeLayout.LayoutParams(10, mTimeLineVideoHeight);
        mShadowIndicator.setLayoutParams(mShadowIndicatorParams);

        setVideoRatio();
        mVideoList = new ArrayList<>();
        String videoPath = Environment.getExternalStorageDirectory() + "/a.mp4";
        for (int i = 0; i < mCountVideo; i++) {
            MainTimeLine mainTimeLine = new MainTimeLine(this, videoPath, mTimeLineVideoHeight);
            mainTimeLine.setOnClickListener(onMainTimeLineClick);
            mainTimeLine.setOnLongClickListener(onVideoLongClick);
            mVideoList.add(mainTimeLine);
            mLayoutVideo.addView(mainTimeLine);
        }
        getLeftMargin(mCountVideo - 1);
        mMainTimeLineControl = new MainTimeLineControl(this, mVideoList.get(0).width, mTimeLineVideoHeight, Constants.MARGIN_LEFT_TIME_LINE);
        mTimeLineVideo.addView(mMainTimeLineControl, mMainTimeLineControl.params);
        setMainControlVisible(false);

        int left = Constants.MARGIN_LEFT_TIME_LINE;
        String imagePath = Environment.getExternalStorageDirectory() + "/a.png";
        ExtraTimeLine extraTimeLine = new ExtraTimeLine(this, imagePath, mTimeLineImageHeight, left, true);
        extraTimeLine.setOnClickListener(onExtraTimeLineClick);
        extraTimeLine.setOnLongClickListener(onExtraTimelineLongClick);
        mLayoutImage.addView(extraTimeLine);

        left = 2 * Constants.MARGIN_LEFT_TIME_LINE + extraTimeLine.width;
        ExtraTimeLine extraTimeLine1 = new ExtraTimeLine(this, imagePath, mTimeLineImageHeight, left, true);
        extraTimeLine1.setOnClickListener(onExtraTimeLineClick);
        extraTimeLine1.setOnLongClickListener(onExtraTimelineLongClick);
        mLayoutImage.addView(extraTimeLine1);

        left = Constants.MARGIN_LEFT_TIME_LINE;
        ExtraTimeLine extraTimeLine2 = new ExtraTimeLine(this, "Lai Trung Tien", mTimeLineImageHeight, left, false);
        extraTimeLine2.setOnClickListener(onExtraTimeLineClick);
        extraTimeLine2.setOnLongClickListener(onExtraTimelineLongClick);
        mLayoutText.addView(extraTimeLine2);

        mExtraTimeLineControl = new ExtraTimeLineControl(this, extraTimeLine.left, extraTimeLine.width, mTimeLineImageHeight);
        mTimeLineImage.addView(mExtraTimeLineControl);
        mExtraTimeLineControl.inLayoutImage = true;
        mSelectedExtraTimeLine = extraTimeLine;
        setExtraControlVisible(false);

        mTimeLineImage.setOnDragListener(onExtraDragListener);
        mTimeLineVideo.setOnDragListener(onExtraDragListener);
        mTimeLineText.setOnDragListener(onExtraDragListener);

        String audioPath = Environment.getExternalStorageDirectory() + "/a.mp3";
        AudioTimeLine audioTimeLine = new AudioTimeLine(this, videoPath, mTimeLineImageHeight, Constants.MARGIN_LEFT_TIME_LINE);
        mLayoutAudio.addView(audioTimeLine, audioTimeLine.params);
        audioTimeLine.setOnClickListener(onAudioTimeLineClick);
        audioTimeLine.setOnLongClickListener(onAudioLongClick);
        mSelectedAudioTimeLine = audioTimeLine;

        AudioTimeLine audioTimeLine1 = new AudioTimeLine(this, videoPath, mTimeLineImageHeight, Constants.MARGIN_LEFT_TIME_LINE * 2 + audioTimeLine.width);
        mLayoutAudio.addView(audioTimeLine1, audioTimeLine1.params);
        audioTimeLine1.setOnClickListener(onAudioTimeLineClick);
        audioTimeLine1.setOnLongClickListener(onAudioLongClick);

        int leftAudioControl = Constants.MARGIN_LEFT_TIME_LINE;
        int rightAudioControl = leftAudioControl + audioTimeLine.width;
        mAudioTimeLineControl = new AudioTimeLineControl(this, leftAudioControl, rightAudioControl, mTimeLineImageHeight);
        mTimeLineAudio.addView(mAudioTimeLineControl);
        setAudioControlVisible(false);
    }

    View.OnLongClickListener onVideoLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            mSelectedMainTimeLine = (MainTimeLine) view;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(clipData, shadowBuilder, view, 0);
            } else {
                view.startDrag(clipData, shadowBuilder, view, 0);
            }
            mImageShadowParams.width = mSelectedMainTimeLine.width;
            mImageShadowParams.height = mSelectedMainTimeLine.height;
            mDragCode = DRAG_VIDEO;
            ViewGroup parent = (ViewGroup) mImageShadow.getParent();
            if (parent != null) {
                parent.removeView(mImageShadow);
            }
            mLayoutVideo.addView(mImageShadow);
            mLayoutVideo.addView(mShadowIndicator);
            mLayoutScrollView.setOnDragListener(onVideoDragListener);
            return false;
        }
    };

    View.OnDragListener onVideoDragListener = new View.OnDragListener() {
        int finalMargin;
        int changePosition;
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            if (mDragCode != DRAG_VIDEO) {
                return false;
            }
            int x = (int) dragEvent.getX();
            if (x > 0) {
                finalMargin = x - 200;
            }
            for (int i=0; i<mVideoList.size(); i++){
                MainTimeLine mainTimeLine = mVideoList.get(i);
                if (x >= mainTimeLine.left && x <= mainTimeLine.right) {
                    mShadowIndicator.setVisibility(View.VISIBLE);
                    mShadowIndicatorParams.leftMargin = mainTimeLine.left;
                    mShadowIndicator.setLayoutParams(mShadowIndicatorParams);
                    changePosition = i;
                    break;
                } else {
                    mShadowIndicator.setVisibility(View.GONE);
                }
            }

            if (finalMargin < Constants.MARGIN_LEFT_TIME_LINE) {
                finalMargin = Constants.MARGIN_LEFT_TIME_LINE;
            }
            mImageShadowParams.leftMargin = finalMargin;
            mImageShadow.setLayoutParams(mImageShadowParams);

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    MainTimeLine changeTimeLine = mVideoList.get(changePosition);
                    if (!changeTimeLine.equals(mSelectedMainTimeLine)) {
                        mSelectedMainTimeLine.setLeftMargin(changeTimeLine.left);
                        mVideoList.remove(mSelectedMainTimeLine);
                        mVideoList.add(changePosition, mSelectedMainTimeLine);
                        getLeftMargin(mCountVideo-1);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    mLayoutVideo.removeView(mImageShadow);
                    mLayoutVideo.removeView(mShadowIndicator);
                    break;
            }
            return true;
        }
    };

    View.OnDragListener onAudioDragListener = new View.OnDragListener() {
        int finalMargin;

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            if (mDragCode != DRAG_AUDIO) {
                return false;
            }
            int x = (int) dragEvent.getX();
            if (x > 0) {
                finalMargin = x - 200;
            }
            if (finalMargin < Constants.MARGIN_LEFT_TIME_LINE) {
                finalMargin = Constants.MARGIN_LEFT_TIME_LINE;
            }
            mImageShadowParams.leftMargin = finalMargin;
            mImageShadow.setLayoutParams(mImageShadowParams);


            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    mSelectedAudioTimeLine.moveTimeLine(finalMargin);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    mLayoutAudio.removeView(mImageShadow);
                    break;
            }
            return true;
        }
    };

    View.OnLongClickListener onAudioLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            mSelectedAudioTimeLine = (AudioTimeLine) view;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(clipData, shadowBuilder, view, 0);
            } else {
                view.startDrag(clipData, shadowBuilder, view, 0);
            }
            mImageShadowParams.width = mSelectedAudioTimeLine.width;
            mImageShadowParams.height = mSelectedAudioTimeLine.height;
            mDragCode = DRAG_AUDIO;
            ViewGroup parent = (ViewGroup) mImageShadow.getParent();
            if (parent != null) {
                parent.removeView(mImageShadow);
            }
            mLayoutAudio.addView(mImageShadow);
            mLayoutScrollView.setOnDragListener(onAudioDragListener);
            return false;
        }
    };

    View.OnClickListener onAudioTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSelectedAudioTimeLine = (AudioTimeLine) view;
            setAudioControlVisible(true);
            mAudioTimeLineControl.restoreTimeLineStatus(mSelectedAudioTimeLine);
        }
    };

    View.OnLongClickListener onExtraTimelineLongClick = new View.OnLongClickListener() {
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
            mDragCode = DRAG_EXTRA;
            return false;
        }
    };

    private boolean shadowInLayout(View layout) {
        if (mImageShadow.getParent() == null) {
            return false;
        }
        return mImageShadow.getParent().equals(layout);
    }

    View.OnDragListener onExtraDragListener = new View.OnDragListener() {
        boolean inLayoutImage;
        int finalMargin = 0;

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            if (mDragCode != DRAG_EXTRA) {
                return false;
            }

            int x = (int) dragEvent.getX();

            if (x != 0) {
                finalMargin = x - 200;
                if (finalMargin < Constants.MARGIN_LEFT_TIME_LINE) {
                    finalMargin = Constants.MARGIN_LEFT_TIME_LINE;
                }
                mImageShadowParams.leftMargin = finalMargin;
                mImageShadow.setLayoutParams(mImageShadowParams);

            }
            if (view.equals(mTimeLineImage) || view.equals(mTimeLineVideo)) {
                inLayoutImage = true;
            }

            if (view.equals(mTimeLineText) || view.equals(mTimeLineAudio)) {
                inLayoutImage = false;
            }

            if (inLayoutImage) {
                if (!shadowInLayout(mTimeLineImage)) {
                    if (shadowInLayout(mTimeLineText)) {
                        mTimeLineText.removeView(mImageShadow);
                    }
                    mTimeLineImage.addView(mImageShadow);
                }
            } else {
                if (!shadowInLayout(mTimeLineText)) {
                    if (shadowInLayout(mTimeLineImage)) {
                        mTimeLineImage.removeView(mImageShadow);
                    }
                    mTimeLineText.addView(mImageShadow);
                }
            }

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    mSelectedExtraTimeLine.moveTimeLine(finalMargin);
                    ViewGroup parent = (ViewGroup) mSelectedExtraTimeLine.getParent();
                    if (parent != null) {
                        parent.removeView(mSelectedExtraTimeLine);
                    }
                    if (inLayoutImage) {
                        mTimeLineImage.addView(mSelectedExtraTimeLine);
                        mSelectedExtraTimeLine.inLayoutImage = true;
                    } else {
                        mTimeLineText.addView(mSelectedExtraTimeLine);
                        mSelectedExtraTimeLine.inLayoutImage = false;
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (shadowInLayout(mTimeLineImage)) {
                        mTimeLineImage.removeView(mImageShadow);
                    }
                    if (shadowInLayout(mTimeLineText)) {
                        mTimeLineText.removeView(mImageShadow);
                    }
                    break;
            }
            return true;
        }
    };

    View.OnClickListener onExtraTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSelectedExtraTimeLine = (ExtraTimeLine) view;
            setExtraControlVisible(true);
            mExtraTimeLineControl.restoreTimeLineStatus(mSelectedExtraTimeLine);

            ViewGroup parent = (ViewGroup) mExtraTimeLineControl.getParent();
            if (parent != null) {
                parent.removeView(mExtraTimeLineControl);
            }
            if (mExtraTimeLineControl.inLayoutImage) {
                mTimeLineImage.addView(mExtraTimeLineControl);
            } else {
                mTimeLineText.addView(mExtraTimeLineControl);
            }
        }
    };

    private void setExtraControlVisible(boolean visible) {
        if (visible) {
            mExtraTimeLineControl.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
            mMainTimeLineControl.setVisibility(View.GONE);
            mAudioTimeLineControl.setVisibility(View.GONE);
        } else {
            mExtraTimeLineControl.setVisibility(View.GONE);
            mScrollView.scroll = true;
        }
    }

    View.OnClickListener onMainTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSelectedMainTimeLine = (MainTimeLine) view;
            setMainControlVisible(true);
            mMainTimeLineControl.restoreTimeLineStatus(mSelectedMainTimeLine);
        }
    };

    @Override
    public void updateMainTimeLine(int leftPosition, int width) {
        mSelectedMainTimeLine.drawTimeLine(leftPosition, width);
        getLeftMargin(mCountVideo-1);
    }

    private void log(String msg) {
        Log.e("Edit video", msg);
    }


    @Override
    public void invisibleMainControl() {
        setMainControlVisible(false);
    }

    private int getLeftMargin(int position) {
        MainTimeLine timeLine = mVideoList.get(position);
        int leftMargin;
        if (position == 0) {
            leftMargin = Constants.MARGIN_LEFT_TIME_LINE;
        } else {
            MainTimeLine mainTimeLine = mVideoList.get(position - 1);
            leftMargin = mainTimeLine.width + getLeftMargin(position - 1);
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
    public void updateExtraTimeLine(int left, int right) {
        mSelectedExtraTimeLine.drawTimeLine(left, right);
    }

    @Override
    public void invisibleExtraControl() {
        setExtraControlVisible(false);
    }

    @Override
    public void updateAudioTimeLine(int start, int end) {
        mSelectedAudioTimeLine.seekTimeLine(start, end);
    }

    @Override
    public void invisibleAudioControl() {
        setAudioControlVisible(false);
    }

    private void setMainControlVisible(boolean visible){
        if (visible) {
            mMainTimeLineControl.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
            mExtraTimeLineControl.setVisibility(View.GONE);
            mAudioTimeLineControl.setVisibility(View.GONE);
        } else {
            mMainTimeLineControl.setVisibility(View.GONE);
            mScrollView.scroll = true;
        }
    }

    private void setAudioControlVisible(boolean visible) {
        if (visible) {
            mAudioTimeLineControl.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
            mMainTimeLineControl.setVisibility(View.GONE);
            mExtraTimeLineControl.setVisibility(View.GONE);
        } else {
            mAudioTimeLineControl.setVisibility(View.GONE);
            mScrollView.scroll = true;
        }
    }
}
