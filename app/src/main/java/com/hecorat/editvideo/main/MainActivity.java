package com.hecorat.editvideo.main;

import android.content.ClipData;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.filemanager.FragmentAudioGallery;
import com.hecorat.editvideo.filemanager.FragmentImagesGallery;
import com.hecorat.editvideo.filemanager.FragmentVideosGallery;
import com.hecorat.editvideo.timeline.AudioTimeLine;
import com.hecorat.editvideo.timeline.AudioTimeLineControl;
import com.hecorat.editvideo.timeline.CustomHorizontalScrollView;
import com.hecorat.editvideo.timeline.ExtraTimeLine;
import com.hecorat.editvideo.timeline.ExtraTimeLineControl;
import com.hecorat.editvideo.timeline.MainTimeLine;
import com.hecorat.editvideo.timeline.MainTimeLineControl;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainTimeLineControl.OnControlTimeLineChanged, ExtraTimeLineControl.OnExtraTimeLineControlChanged, AudioTimeLineControl.OnAudioControlTimeLineChanged {
    private RelativeLayout mVideoViewLayout;
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
    private ViewPager mViewPager;
    private TextView mFolderName;
    private FragmentVideosGallery mFragmentVideosGallery;
    private FragmentImagesGallery mFragmentImagesGallery;
    private FragmentAudioGallery mFragmentAudioGallery;
    private ImageView mBtnBack, mBtnAdd, mBtnUndo, mBtnExport, mBtnPlay;
    private LinearLayout mFileManager;
    private ImageView mVideoTab, mImageTab, mAudioTab;
    private LinearLayout mVideoTabLayout, mImageTabLayout, mAudioTabLayout;
    private VideoView mVideoView;

    private int mDragCode = DRAG_VIDEO;
    private int mCountVideo = 0;
    private int mTimeLineVideoHeight = 150;
    private int mTimeLineImageHeight = 70;
    private int mFragmentCode;
    private boolean mOpenFileManager;
    public boolean mOpenVideoSubFolder,
            mOpenImageSubFolder, mOpenAudioSubFolder;
    private boolean mRunThread;
    private int mCurrentVideoId, mCurrentInVideo, mCurrentPosition;
    private int mPreviewStatus;
    private int mMaxTimeLine;

    private Thread mThreadPreviewVideo;

    public static final int DRAG_VIDEO = 0;
    public static final int DRAG_EXTRA = 1;
    public static final int DRAG_AUDIO = 2;
    public static final int VIDEO_TAB = 0;
    public static final int IMAGE_TAB = 1;
    public static final int AUDIO_TAB = 2;
    public static final int MSG_CURRENT_POSITION = 0;
    public static final int BEGIN = 0;
    public static final int PLAY = 1;
    public static final int PAUSE = 2;
    public static final int END = 3;
    public static final int UPDATE_STATUS_PERIOD = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        mVideoViewLayout = (RelativeLayout) findViewById(R.id.video_view_layout);
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
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnAdd = (ImageView) findViewById(R.id.btn_add);
        mBtnUndo = (ImageView) findViewById(R.id.btn_undo);
        mBtnExport = (ImageView) findViewById(R.id.btn_export);
        mBtnPlay = (ImageView) findViewById(R.id.btn_play);
        mFileManager = (LinearLayout) findViewById(R.id.layout_file_manager);
        mVideoTab = (ImageView) findViewById(R.id.video_tab);
        mImageTab = (ImageView) findViewById(R.id.image_tab);
        mAudioTab = (ImageView) findViewById(R.id.audio_tab);
        mVideoTabLayout = (LinearLayout) findViewById(R.id.video_tab_layout);
        mImageTabLayout = (LinearLayout) findViewById(R.id.image_tab_layout);
        mAudioTabLayout = (LinearLayout) findViewById(R.id.audio_tab_layout);
        mVideoView = (VideoView) findViewById(R.id.video_view);

        mVideoTabLayout.setOnClickListener(onTabLayoutClick);
        mImageTabLayout.setOnClickListener(onTabLayoutClick);
        mAudioTabLayout.setOnClickListener(onTabLayoutClick);

        mImageShadow = new ImageView(this);
        mImageShadow.setBackgroundResource(R.drawable.shadow);
        mImageShadowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mShadowIndicator = new ImageView(this);
        mShadowIndicator.setBackgroundResource(R.drawable.shadow_indicator);
        mShadowIndicatorParams = new RelativeLayout.LayoutParams(10, mTimeLineVideoHeight);
        mShadowIndicator.setLayoutParams(mShadowIndicatorParams);

        setVideoRatio();
        mVideoList = new ArrayList<>();

        mMainTimeLineControl = new MainTimeLineControl(this, 500, mTimeLineVideoHeight, Constants.MARGIN_LEFT_TIME_LINE);
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
        AudioTimeLine audioTimeLine = new AudioTimeLine(this, audioPath, mTimeLineImageHeight, Constants.MARGIN_LEFT_TIME_LINE);
        mLayoutAudio.addView(audioTimeLine, audioTimeLine.params);
        audioTimeLine.setOnClickListener(onAudioTimeLineClick);
        audioTimeLine.setOnLongClickListener(onAudioLongClick);
        mSelectedAudioTimeLine = audioTimeLine;

        AudioTimeLine audioTimeLine1 = new AudioTimeLine(this, audioPath, mTimeLineImageHeight, Constants.MARGIN_LEFT_TIME_LINE * 2 + audioTimeLine.width);
        mLayoutAudio.addView(audioTimeLine1, audioTimeLine1.params);
        audioTimeLine1.setOnClickListener(onAudioTimeLineClick);
        audioTimeLine1.setOnLongClickListener(onAudioLongClick);

        int leftAudioControl = Constants.MARGIN_LEFT_TIME_LINE;
        int rightAudioControl = leftAudioControl + audioTimeLine.width;
        mAudioTimeLineControl = new AudioTimeLineControl(this, leftAudioControl, rightAudioControl, mTimeLineImageHeight);
        mTimeLineAudio.addView(mAudioTimeLineControl);
        setAudioControlVisible(false);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mFolderName = (TextView) findViewById(R.id.text_folder_name);
        GalleryPagerAdapter galleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(galleryPagerAdapter);
        mViewPager.addOnPageChangeListener(onViewPagerChanged);
        mFragmentVideosGallery = new FragmentVideosGallery();
        mFragmentImagesGallery = new FragmentImagesGallery();
        mFragmentAudioGallery = new FragmentAudioGallery();

        mBtnBack.setOnClickListener(onBtnBackClick);
        mBtnAdd.setOnClickListener(onBtnAddClick);
        mBtnPlay.setOnClickListener(onBtnPlayClick);
        mBtnExport.setOnClickListener(onBtnExportClick);

        mThreadPreviewVideo = new Thread(runnablePreview);
        mCurrentVideoId = -1;
        mMaxTimeLine = 0;
        mPreviewStatus = BEGIN;
        mRunThread = true;
        mThreadPreviewVideo.start();
    }

    View.OnClickListener onBtnExportClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mRunThread = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunThread = false;
    }

    Runnable runnablePreview = new Runnable() {
        @Override
        public void run() {

            while (mRunThread) {
                try {
                    Thread.sleep(UPDATE_STATUS_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_CURRENT_POSITION;
                mHandler.sendMessage(msg);
            }
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CURRENT_POSITION:
                    updateCurrentPosition();
                    updatePreviewStatus();
//                    updateBtnPlay();
//                    updateVideoView();
                    break;
            }
        }
    };

    private void updateBtnPlay() {
        if (mVideoView.isPlaying()) {
            mBtnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            mBtnPlay.setImageResource(R.drawable.ic_play);
        }
        log(mVideoView.getCurrentPosition()+"");
    }

    private void updateCurrentPosition(){
        if (mCurrentVideoId == -1) {
            mCurrentPosition = 0;
        } else {
            mCurrentPosition = 0;
            for (int i=0; i<mCurrentVideoId; i++) {
                mCurrentPosition += mVideoList.get(i).durationVideo;
            }
            mCurrentPosition += mVideoView.getCurrentPosition();
        }
        log(mCurrentPosition+"");
    }
    private void updatePreviewStatus(){
        if (mVideoView.isPlaying()) {
            mPreviewStatus = PLAY;

        } else {
            mPreviewStatus = PAUSE;
        }
        if (mCurrentPosition == 0) {
            mPreviewStatus = BEGIN;
        }
        if (mCurrentPosition > mMaxTimeLine) {
            mPreviewStatus = END;
        }
        log("Status: "+mPreviewStatus);
    }

    private void updateVideoView() {
        MainTimeLine mainTimeLine = mVideoList.get(0);
        int timelineId=0;
        for (int i=0; i<mVideoList.size(); i++){
            mainTimeLine = mVideoList.get(i);
            if (mCurrentPosition >= mainTimeLine.startInTimeLine && mCurrentPosition <= mainTimeLine.endInTimeLine) {
                timelineId = i;
                break;
            }
        }
        log("current position " + mCurrentPosition+" timeline Position "+timelineId);
        if (mCurrentVideoId != timelineId) {
            mCurrentVideoId = timelineId;
            mVideoView.setVideoPath(mainTimeLine.videoPath);
            mVideoView.seekTo(mainTimeLine.startTime);
            mVideoView.start();
        }

        if (mCurrentPosition>mVideoList.get(mCountVideo-1).endInTimeLine) {
            mVideoView.pause();
        }
    }

    private void resetVideoView(){
        mCurrentVideoId = -1;
        mCurrentPosition=0;
    }

    View.OnClickListener onBtnPlayClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
                mBtnPlay.setImageResource(R.drawable.ic_play);
                mRunThread = false;
            } else {

                mBtnPlay.setImageResource(R.drawable.ic_pause);
                mRunThread = true;
                mThreadPreviewVideo.start();
            }
        }
    };

    public void addVideo(String videoPath){
        MainTimeLine mainTimeLine = new MainTimeLine(this, videoPath, mTimeLineVideoHeight);
        mainTimeLine.setOnClickListener(onMainTimeLineClick);
        mainTimeLine.setOnLongClickListener(onVideoLongClick);
        mVideoList.add(mainTimeLine);
        mLayoutVideo.addView(mainTimeLine);
        mCountVideo++;
        getLeftMargin(mCountVideo - 1);
        mMaxTimeLine = mainTimeLine.endInTimeLine;
        mCurrentPosition = mainTimeLine.startInTimeLine;
        mVideoView.setVideoPath(mainTimeLine.videoPath);
        mCurrentVideoId = mCountVideo-1;
    }

    View.OnClickListener onTabLayoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.equals(mVideoTabLayout)) {
                mViewPager.setCurrentItem(VIDEO_TAB, true);
                setHightLighTab(VIDEO_TAB);
                setFolderName(mFragmentVideosGallery.mFolderName);
            }

            if (view.equals(mImageTabLayout)) {
                mViewPager.setCurrentItem(IMAGE_TAB, true);
                setHightLighTab(IMAGE_TAB);
                setFolderName(mFragmentImagesGallery.mFolderName);
            }

            if (view.equals(mAudioTabLayout)) {
                mViewPager.setCurrentItem(AUDIO_TAB, true);
                setHightLighTab(AUDIO_TAB);
                setFolderName(mFragmentAudioGallery.mFolderName);
            }
        }
    };

    public void setFolderName(String name) {
        mFolderName.setText(name);
    }

    View.OnClickListener onBtnAddClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mOpenFileManager) {
                openFileManager(false);
            } else {
                openFileManager(true);
            }
            mRunThread = false;
        }
    };

    private void openFileManager(boolean open) {
        if (open) {
            mFileManager.setVisibility(View.VISIBLE);
            mOpenFileManager = true;
        } else {
            mFileManager.setVisibility(View.GONE);
            mOpenFileManager = false;
        }
    }

    View.OnClickListener onBtnBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (mFragmentCode) {
                case VIDEO_TAB:
                    if (mOpenVideoSubFolder) {
                        mFragmentVideosGallery.backToMain();
                        mOpenVideoSubFolder = false;
                        return;
                    }
                    break;
                case IMAGE_TAB:
                    if (mOpenImageSubFolder) {
                        mFragmentImagesGallery.backToMain();
                        mOpenImageSubFolder = false;
                        return;
                    }
                    break;
                case AUDIO_TAB:
                    if (mOpenAudioSubFolder) {
                        mFragmentAudioGallery.backToMain();
                        mOpenAudioSubFolder = false;
                        return;
                    }
                    break;
            }

            if (mOpenFileManager) {
                openFileManager(false);
            }
        }
    };

    ViewPager.OnPageChangeListener onViewPagerChanged = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mFragmentCode = position;
            setHightLighTab(position);
            switch (position) {
                case VIDEO_TAB:

                    setFolderName(mFragmentVideosGallery.mFolderName);
                    break;
                case IMAGE_TAB:

                    setFolderName(mFragmentImagesGallery.mFolderName);
                    break;
                case AUDIO_TAB:

                    setFolderName(mFragmentAudioGallery.mFolderName);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setHightLighTab(int tab) {
        int videoTabId = tab==VIDEO_TAB? R.drawable.ic_video_tab_blue:R.drawable.ic_video_tab;
        int imageTabId = tab==IMAGE_TAB? R.drawable.ic_image_tab_blue:R.drawable.ic_image_tab;
        int audioTabId = tab==AUDIO_TAB? R.drawable.ic_audio_tab_blue:R.drawable.ic_audio_tab;
        mVideoTab.setImageResource(videoTabId);
        mImageTab.setImageResource(imageTabId);
        mAudioTab.setImageResource(audioTabId);
    }

    private class GalleryPagerAdapter extends FragmentPagerAdapter {

        public GalleryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mFragmentVideosGallery;
                case 1:
                    return mFragmentImagesGallery;
                case 2:
                    return mFragmentAudioGallery;
                default:
                    return mFragmentVideosGallery;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
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
            for (int i = 0; i < mVideoList.size(); i++) {
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
                        getLeftMargin(mCountVideo - 1);
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
        getLeftMargin(mCountVideo - 1);
        mMaxTimeLine = mVideoList.get(mCountVideo-1).endInTimeLine;
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

    private void setMainControlVisible(boolean visible) {
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
