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
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.filemanager.FragmentAudioGallery;
import com.hecorat.editvideo.filemanager.FragmentImagesGallery;
import com.hecorat.editvideo.filemanager.FragmentVideosGallery;
import com.hecorat.editvideo.helper.Utils;
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
    private MainTimeLine mSelectedMainTimeLine, mCurrentMainTimeLine;
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
    private ImageView mBtnBack, mBtnAdd, mBtnUndo, mBtnExport, mBtnPlay, mBtnDelete;
    private LinearLayout mFileManager;
    private ImageView mVideoTab, mImageTab, mAudioTab;
    private LinearLayout mVideoTabLayout, mImageTabLayout, mAudioTabLayout;
    private VideoView mActiveVideoView, mInActiveVideoView, mVideoView1, mVideoView2;
    private RelativeLayout mLayoutTimeLine;
    private FrameLayout mLimitTimeLineVideo, mLimitTimeLineImage, mLimitTimeLineText,
                        mLimitTimeLineAudio, mSeperateLineVideo, mSeperateLineImage, mSeperateLineText;


    private int mDragCode = DRAG_VIDEO;
    private int mCountVideo = 0;
    private int mTimeLineVideoHeight = 150;
    private int mTimeLineImageHeight = 70;
    private int mFragmentCode;
    private boolean mOpenFileManager;
    public boolean mOpenVideoSubFolder,
            mOpenImageSubFolder, mOpenAudioSubFolder;
    private boolean mRunThread;
    private int mCurrentVideoId, mCurrentPosition;
    private int mPreviewStatus;
    private int mMaxTimeLine;
    public int mLeftMarginTimeLine = Constants.MARGIN_LEFT_TIME_LINE;
    private boolean mScroll;

    private Thread mThreadPreviewVideo;
    private MainActivity mActivity;

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
        mVideoView1 = (VideoView) findViewById(R.id.video_view1);
        mVideoView2 = (VideoView) findViewById(R.id.video_view2);
        mLayoutTimeLine = (RelativeLayout) findViewById(R.id.layout_timeline);
        mLimitTimeLineVideo = (FrameLayout) findViewById(R.id.limit_timeline_video);
        mLimitTimeLineImage = (FrameLayout) findViewById(R.id.limit_timeline_image);
        mLimitTimeLineText = (FrameLayout) findViewById(R.id.limit_timeline_text);
        mLimitTimeLineAudio = (FrameLayout) findViewById(R.id.limit_timeline_audio);
        mSeperateLineVideo = (FrameLayout) findViewById(R.id.seperate_line_video);
        mSeperateLineImage = (FrameLayout) findViewById(R.id.seperate_line_image);
        mSeperateLineText = (FrameLayout) findViewById(R.id.seperate_line_text);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mBtnDelete = (ImageView) findViewById(R.id.btn_delete);
        mFolderName = (TextView) findViewById(R.id.text_folder_name);


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

        mActivity = this;
        setVideoRatio();
        mVideoList = new ArrayList<>();

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
        mBtnUndo.setOnClickListener(onBtnUndoClick);
        mBtnDelete.setOnClickListener(onBtnDeleteClick);

        mTimeLineImage.setOnDragListener(onExtraDragListener);
        mTimeLineVideo.setOnDragListener(onExtraDragListener);
        mTimeLineText.setOnDragListener(onExtraDragListener);

        mThreadPreviewVideo = new Thread(runnablePreview);
        mCurrentVideoId = -1;
        mMaxTimeLine = 0;
        mPreviewStatus = BEGIN;
        mRunThread = true;
        mThreadPreviewVideo.start();
        mActiveVideoView = mVideoView1;
        mInActiveVideoView = mVideoView2;
        mScrollView.setOnCustomScrollChanged(onCustomScrollChanged);
        mLayoutTimeLine.getViewTreeObserver().addOnGlobalLayoutListener(onLayoutTimeLineCreated);
        mScroll = true;
    }

    View.OnClickListener onBtnDeleteClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mVideoList.remove(mSelectedMainTimeLine);
            mLayoutVideo.removeView(mSelectedMainTimeLine);
            invisibleMainControl();
            mCountVideo--;
            mCurrentVideoId--;
            if (mCountVideo>0) {
                getLeftMargin(mCountVideo-1);
                mMaxTimeLine = mVideoList.get(mCountVideo-1).endInTimeLine;
                if (mCurrentVideoId<0) {
                    mCurrentVideoId = 0;
                }
                mSelectedMainTimeLine = mVideoList.get(mCurrentVideoId);
                mActiveVideoView.setVideoPath(mSelectedMainTimeLine.videoPath);
                mScrollView.scrollTo(mSelectedMainTimeLine.startInTimeLine/Constants.SCALE_VALUE, 0);
            } else {
                mMaxTimeLine = 0;
            }
        }
    };

    @Override
    public void seekTo(int value, boolean scroll) {
        mActiveVideoView.seekTo(value);
        mScroll = scroll;
    }

    ViewTreeObserver.OnGlobalLayoutListener onLayoutTimeLineCreated = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mLeftMarginTimeLine = mLayoutTimeLine.getWidth()/2 - Utils.dpToPixel(mActivity, 45);
            updateLayoutTimeLine();
            addControler();
            mLayoutTimeLine.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    View.OnClickListener onBtnUndoClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            resetVideoView();
        }
    };

    View.OnClickListener onBtnExportClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

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
                    updateBtnPlay();
                    updateVideoView();
                    break;
            }
        }
    };

    CustomHorizontalScrollView.OnCustomScrollChanged onCustomScrollChanged = new CustomHorizontalScrollView.OnCustomScrollChanged() {
        @Override
        public void onScrollChanged() {
            log("scroll: "+mScrollView.getScrollX());
            if (mCountVideo < 1){
                return;
            }
            int scrollPosition = mScrollView.getScrollX();
            mCurrentPosition = scrollPosition * Constants.SCALE_VALUE;

//            if (mCurrentPosition > mMaxTimeLine) {
//                mCurrentPosition = mMaxTimeLine;
//            }

            log("current position: "+mCurrentPosition);
            log("max timeline: "+mMaxTimeLine);

            MainTimeLine mainTimeLine = null;
            int timelineId=0;
            for (int i=0; i<mVideoList.size(); i++){
                mainTimeLine = mVideoList.get(i);
                if (mCurrentPosition >= mainTimeLine.startInTimeLine && mCurrentPosition <= mainTimeLine.endInTimeLine) {
                    timelineId = i;
                    break;
                }
            }
            int positionInVideo=0;
            if (timelineId > 0) {
                MainTimeLine previousTimeLine = mVideoList.get(timelineId-1);
                positionInVideo = mCurrentPosition - previousTimeLine.endInTimeLine + mainTimeLine.startTime;
            } else {
                positionInVideo = mCurrentPosition + mainTimeLine.startTime;
            }

            if (mCurrentVideoId != timelineId && mainTimeLine!=null) {
                mCurrentVideoId = timelineId;
                mActiveVideoView.setVideoPath(mainTimeLine.videoPath);
//                if (mPreviewStatus == PLAY) {
//                    mActiveVideoView.start();
//                }
            }

            mActiveVideoView.seekTo(positionInVideo);
            log("mActiveVideo: "+mActiveVideoView.getCurrentPosition());
        }
    };

    private void updateBtnPlay() {
        if (mActiveVideoView.isPlaying()) {
            mBtnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            mBtnPlay.setImageResource(R.drawable.ic_play);
        }
    }

    private void updateCurrentPosition(){
        if (mCurrentVideoId == -1) {
            mCurrentPosition = 0;
        } else {
            mCurrentPosition = 0;
            for (int i=0; i<mCurrentVideoId; i++) {
                mCurrentPosition += mVideoList.get(i).width*Constants.SCALE_VALUE;
            }
            MainTimeLine currentTimeLine = mVideoList.get(mCurrentVideoId);
            int currentVideoView = mActiveVideoView.getCurrentPosition();
            if (currentVideoView < currentTimeLine.startTime) {
                currentVideoView = currentTimeLine.startTime;
            }
            mCurrentPosition += currentVideoView - currentTimeLine.startTime +50;
        }
        if (!mScroll){
            return;
        }
        if (mCurrentPosition >= mMaxTimeLine) {
            return;
        }
        int scrollPosition = mCurrentPosition/Constants.SCALE_VALUE;
        mScrollView.scrollTo(scrollPosition, 0);
    }
    private void updatePreviewStatus(){
        if (mActiveVideoView.isPlaying()) {
            mPreviewStatus = PLAY;

        } else {
            mPreviewStatus = PAUSE;
        }
        if (mCurrentPosition == 0) {
            mPreviewStatus = BEGIN;
        }
        if (mCurrentPosition >= mMaxTimeLine) {
            mPreviewStatus = END;
        }
    }

    private void updateVideoView() {
        if (mCurrentPosition>=mMaxTimeLine) {
            mActiveVideoView.pause();
            return;
        }
        MainTimeLine mainTimeLine = null;
        int timelineId=mCurrentVideoId;
        for (int i=0; i<mVideoList.size(); i++){
            mainTimeLine = mVideoList.get(i);
            if (mCurrentPosition >= mainTimeLine.startInTimeLine && mCurrentPosition <= mainTimeLine.endInTimeLine) {
                timelineId = i;
                break;
            }
        }

        if (mCurrentVideoId != -1 && mCurrentVideoId<mCountVideo-1) {
            MainTimeLine currentMainTimeLine = mVideoList.get(mCurrentVideoId);
            MainTimeLine nextMainTimeLine = mVideoList.get(mCurrentVideoId+1);

            if (mCurrentPosition>=currentMainTimeLine.endInTimeLine-200) {
                mInActiveVideoView.setVideoPath(nextMainTimeLine.videoPath);
                mInActiveVideoView.seekTo(10);
            }
        }
        if (mCurrentVideoId != timelineId && mainTimeLine!=null) {
            if (mCurrentVideoId != mCountVideo-1) {
                toggleVideoView();
            }
            mCurrentVideoId = timelineId;
            if (mPreviewStatus == BEGIN){
                mActiveVideoView.setVideoPath(mainTimeLine.videoPath);
            }
            mActiveVideoView.seekTo(mainTimeLine.startTime);
            mActiveVideoView.start();
        }


        if (mCurrentVideoId != -1 && mCountVideo>0) {
            mCurrentMainTimeLine = mVideoList.get(mCurrentVideoId);
        }

    }

    private void toggleVideoView() {
        if (mActiveVideoView.equals(mVideoView1)) {
            mActiveVideoView = mVideoView2;
            mInActiveVideoView = mVideoView1;
        } else {
            mActiveVideoView = mVideoView1;
            mInActiveVideoView = mVideoView2;
        }
        mActiveVideoView.setVisibility(View.VISIBLE);
        mInActiveVideoView.setVisibility(View.GONE);
    }

    private void resetVideoView(){
        mCurrentVideoId = -1;
        mCurrentPosition=0;
    }

    View.OnClickListener onBtnPlayClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mActiveVideoView.isPlaying()) {
                mActiveVideoView.pause();
                mBtnPlay.setImageResource(R.drawable.ic_play);
            } else {
                playVideo();
                mBtnPlay.setImageResource(R.drawable.ic_pause);
                openFileManager(false);
            }
        }
    };

    private void playVideo() {
        if (mPreviewStatus == BEGIN || mPreviewStatus == END) {
            resetVideoView();
        }
        if (mPreviewStatus == PAUSE) {
            mActiveVideoView.start();
        }
    }

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
        mActiveVideoView.setVideoPath(mainTimeLine.videoPath);
        mCurrentVideoId = mCountVideo-1;
    }

    public void addImage(String imagePath){
        ExtraTimeLine extraTimeLine = new ExtraTimeLine(this, imagePath, mTimeLineImageHeight, mLeftMarginTimeLine, true);
        extraTimeLine.setOnClickListener(onExtraTimeLineClick);
        extraTimeLine.setOnLongClickListener(onExtraTimelineLongClick);
        mLayoutImage.addView(extraTimeLine);
    }

    public void addAudio(String audioPath) {
        AudioTimeLine audioTimeLine = new AudioTimeLine(this, audioPath, mTimeLineImageHeight, mLeftMarginTimeLine);
        mLayoutAudio.addView(audioTimeLine, audioTimeLine.params);
        audioTimeLine.setOnClickListener(onAudioTimeLineClick);
        audioTimeLine.setOnLongClickListener(onAudioLongClick);
        mSelectedAudioTimeLine = audioTimeLine;
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

            if (finalMargin < mLeftMarginTimeLine) {
                finalMargin = mLeftMarginTimeLine;
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
                        mScrollView.scrollTo(mSelectedMainTimeLine.startInTimeLine/Constants.SCALE_VALUE, 0);
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
            if (finalMargin < mLeftMarginTimeLine) {
                finalMargin = mLeftMarginTimeLine;
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
                if (finalMargin < mLeftMarginTimeLine) {
                    finalMargin = mLeftMarginTimeLine;
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
            if (!mSelectedMainTimeLine.equals(mCurrentMainTimeLine)) {
                mScrollView.scrollTo(mSelectedMainTimeLine.startInTimeLine/Constants.SCALE_VALUE, 0);
                onCustomScrollChanged.onScrollChanged();
            }
            setMainControlVisible(true);
            mMainTimeLineControl.restoreTimeLineStatus(mSelectedMainTimeLine);
            mBtnExport.setVisibility(View.GONE);
            mBtnDelete.setVisibility(View.VISIBLE);
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
        mBtnDelete.setVisibility(View.GONE);
        mBtnExport.setVisibility(View.VISIBLE);
    }

    private int getLeftMargin(int position) {
        MainTimeLine timeLine = mVideoList.get(position);
        int leftMargin;
        if (position == 0) {
            leftMargin = mLeftMarginTimeLine;
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

    private void addControler() {
        mMainTimeLineControl = new MainTimeLineControl(this, 500, mTimeLineVideoHeight, mLeftMarginTimeLine);
        mTimeLineVideo.addView(mMainTimeLineControl, mMainTimeLineControl.params);
        setMainControlVisible(false);

        mExtraTimeLineControl = new ExtraTimeLineControl(this, mLeftMarginTimeLine, 500, mTimeLineImageHeight);
        mTimeLineImage.addView(mExtraTimeLineControl);
        mExtraTimeLineControl.inLayoutImage = true;
        setExtraControlVisible(false);

        mAudioTimeLineControl = new AudioTimeLineControl(this, mLeftMarginTimeLine, mLeftMarginTimeLine+500, mTimeLineImageHeight);
        mTimeLineAudio.addView(mAudioTimeLineControl);
        setAudioControlVisible(false);

        int left = mLeftMarginTimeLine;
        ExtraTimeLine extraTimeLine2 = new ExtraTimeLine(this, "Lai Trung Tien", mTimeLineImageHeight, left, false);
        extraTimeLine2.setOnClickListener(onExtraTimeLineClick);
        extraTimeLine2.setOnLongClickListener(onExtraTimelineLongClick);
        mLayoutText.addView(extraTimeLine2);

    }



    private void updateLayoutTimeLine() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLimitTimeLineVideo.getLayoutParams();
        params.leftMargin = mLeftMarginTimeLine;
        params = (RelativeLayout.LayoutParams) mLimitTimeLineImage.getLayoutParams();
        params.leftMargin = mLeftMarginTimeLine;
        params = (RelativeLayout.LayoutParams) mLimitTimeLineText.getLayoutParams();
        params.leftMargin = mLeftMarginTimeLine;
        params = (RelativeLayout.LayoutParams) mLimitTimeLineAudio.getLayoutParams();
        params.leftMargin = mLeftMarginTimeLine;
        LinearLayout.LayoutParams seperateParams= (LinearLayout.LayoutParams) mSeperateLineVideo.getLayoutParams();
        seperateParams.leftMargin = mLeftMarginTimeLine;
        seperateParams = (LinearLayout.LayoutParams) mSeperateLineImage.getLayoutParams();
        seperateParams.leftMargin = mLeftMarginTimeLine;
        seperateParams = (LinearLayout.LayoutParams) mSeperateLineText.getLayoutParams();
        seperateParams.leftMargin = mLeftMarginTimeLine;
    }
}
