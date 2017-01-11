package com.hecorat.azplugin2.main;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.addimage.FloatImage;
import com.hecorat.azplugin2.addtext.AlphaColorDrawable;
import com.hecorat.azplugin2.addtext.ColorPickerView;
import com.hecorat.azplugin2.addtext.FloatText;
import com.hecorat.azplugin2.addtext.FontAdapter;
import com.hecorat.azplugin2.addtext.FontManager;
import com.hecorat.azplugin2.audio.AddSilentAudoTask;
import com.hecorat.azplugin2.audio.VolumeEditor;
import com.hecorat.azplugin2.database.AudioObject;
import com.hecorat.azplugin2.database.AudioTable;
import com.hecorat.azplugin2.database.ImageObject;
import com.hecorat.azplugin2.database.ImageTable;
import com.hecorat.azplugin2.database.ProjectTable;
import com.hecorat.azplugin2.database.TextObject;
import com.hecorat.azplugin2.database.TextTable;
import com.hecorat.azplugin2.database.VideoObject;
import com.hecorat.azplugin2.database.VideoTable;
import com.hecorat.azplugin2.donate.IabController;
import com.hecorat.azplugin2.export.ExportFragment;
import com.hecorat.azplugin2.filemanager.FragmentAudioGallery;
import com.hecorat.azplugin2.filemanager.FragmentImagesGallery;
import com.hecorat.azplugin2.filemanager.FragmentVideosGallery;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.interfaces.DialogClickListener;
import com.hecorat.azplugin2.dialogfragment.DialogConfirm;
import com.hecorat.azplugin2.preview.CustomVideoView;
import com.hecorat.azplugin2.timeline.AudioTL;
import com.hecorat.azplugin2.timeline.AudioTLControl;
import com.hecorat.azplugin2.timeline.BigTimeMark;
import com.hecorat.azplugin2.timeline.CustomHorizontalScrollView;
import com.hecorat.azplugin2.timeline.ExtraTL;
import com.hecorat.azplugin2.timeline.ExtraTLControl;
import com.hecorat.azplugin2.timeline.SmallTimeMark;
import com.hecorat.azplugin2.timeline.TimeText;
import com.hecorat.azplugin2.timeline.VideoTL;
import com.hecorat.azplugin2.timeline.VideoTLControl;
import com.hecorat.azplugin2.video.TrimFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements VideoTLControl.OnControlTimeLineChanged,
        ExtraTLControl.OnExtraTimeLineControlChanged, AudioTLControl.OnAudioControlTimeLineChanged,
        ColorPickerView.OnColorChangedListener, DialogClickListener {
    private RelativeLayout mVideoViewLayout;
    private RelativeLayout mLayoutVideo, mLayoutImage, mLayoutText, mLayoutAudio;
    private RelativeLayout mTimeLineVideo, mTimeLineImage, mTimeLineText, mTimeLineAudio;
    private LinearLayout mLayoutScrollView;
    private ImageView mTLShadow;
    private RelativeLayout.LayoutParams mTLShadowParams;
    private ImageView mShadowIndicator;
    private RelativeLayout.LayoutParams mShadowIndicatorParams;
    private ViewPager mViewPager;
    private TextView mFolderName;
    private ImageView mBtnBack, mBtnAdd, mBtnUndo, mBtnExport,
            mBtnPlay, mBtnDelete, mBtnEditText, mBtnVolume;
    private LinearLayout mFileManager;
    private ImageView mVideoTab, mImageTab, mAudioTab;
    private LinearLayout mVideoTabLayout, mImageTabLayout, mAudioTabLayout;
    private CustomVideoView mActiveVideoView, mInActiveVideoView, mVideoView1, mVideoView2;
    private RelativeLayout mLayoutTimeLine;
    private FrameLayout mLimitTimeLineVideo, mLimitTimeLineImage, mLimitTimeLineText,
            mLimitTimeLineAudio, mSeperateLineVideo, mSeperateLineImage, mSeperateLineText;
    private LinearLayout mLayoutAdd;
    private TextView mBtnAddMedia, mBtnAddText;
    private LinearLayout mLayoutEditText;
    private EditText mEditText, mEdtColorHex;
    private Spinner mFontSpinner;
    private ImageView mBtnTextColor, mBtnTextBgrColor;
    private LinearLayout mLayoutColorPicker;
    private RelativeLayout mLayoutBtnTextColor, mLayoutBtnTextBgrColor;
    private Button mBtnCloseColorPicker;
    private ImageView mIndicatorTextColor, mIndicatorTextBgr;
    private RelativeLayout mLayoutTimeMark;
    private RelativeLayout mTopLayout, mMainLayout;
    private LinearLayout mSeekbarIndicator;
    private RelativeLayout mLayoutAnimationAddFile;
    private ImageView mImageShadowAnimation;
    public FrameLayout mLayoutFragment;
    private RelativeLayout mLayoutFloatView;
    private ImageView mBtnUpLevel;
    private LinearLayout mLayoutExtraTools;

    private Thread mThreadPreviewVideo;
    public ArrayList<VideoTL> mVideoList;
    public ArrayList<ExtraTL> mImageList;
    public ArrayList<ExtraTL> mTextList;
    public ArrayList<AudioTL> mAudioList;
    private ArrayList<ExtraTL> mListInLayoutImage, mListInLayoutText;
    public ArrayList<String> mFontPath;
    private AudioManager mAudioManager;

    public String mProjectName;
    public String mVideoPath, mImagePath, mAudioPath;

    private MainActivity mActivity;
    private FontAdapter mFontAdapter;
    private MediaPlayer mMediaPlayer;
    private VideoTL mSelectedVideoTL, mCurrentVideoTL;
    private VideoTLControl mVideoTLControl;
    private ExtraTL mSelectedExtraTL;
    private ExtraTLControl mExtraTLControl;
    private AudioTLControl mAudioTLControl;
    private AudioTL mSelectedAudioTL, mCurrentAudioTL;
    private ColorPickerView mColorPicker;
    private FragmentVideosGallery mFragmentVideosGallery;
    private FragmentImagesGallery mFragmentImagesGallery;
    private FragmentAudioGallery mFragmentAudioGallery;
    public CustomHorizontalScrollView mScrollView;
    public ExportFragment mExportFragment;
    public TrimFragment mTrimFragment;
    public VideoTable mVideoTable;
    public AudioTable mAudioTable;
    public ImageTable mImageTable;
    public TextTable mTextTable;
    public ProjectTable mProjectTable;
    private GalleryPagerAdapter mGalleryPagerAdapter;
    private FloatText mWaterMark;
    private IabController mIabController;

    private int mDragCode = DRAG_VIDEO;
    private int mCountVideo = 0;
    private int mTimeLineVideoHeight = 150;
    private int mTimeLineImageHeight = 70;
    private int mFragmentCode;
    private boolean mOpenFileManager;
    public boolean mOpenVideoSubFolder,
            mOpenImageSubFolder, mOpenAudioSubFolder;
    private boolean mRunThread;
    private int mCurrentVideoId, mTLPositionInMs;
    private int mPreviewStatus;
    private int mMaxTimeLineMs;
    public int mLeftMarginTimeLine = Constants.MARGIN_LEFT_TIME_LINE;
    private boolean mScroll;
    private boolean mOpenLayoutAdd, mOpenLayoutEditText;
    private boolean mShowColorPicker, mChooseTextColor;
    private int mSelectedTL;
    private int mSeekTimeAudio;
    private int mSystemVolume;
    public boolean mFinishExport;
    public float mVideoViewLeft;
    public boolean mFoundImage, mFoundText;
    public int mProjectId;
    public boolean mOpenLayoutProject;
    private boolean mIsVip;
    private boolean mOpenExtraTools;
    public boolean mOpenLayoutTrimVideo;
    public boolean mOpenLayoutExport;

    public static final int TIMELINE_VIDEO = 0;
    public static final int TIMELINE_EXTRA = 1;
    public static final int TIMELINE_AUDIO = 2;
    public static final int UNSELECT = 3;
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
    public static final int ADD_VIDEO = 0;
    public static final int ADD_AUDIO = 1;
    public static final int ADD_IMAGE = 2;
    public static final int ADD_TEXT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
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
        mVideoView1 = (CustomVideoView) findViewById(R.id.video_view1);
        mVideoView2 = (CustomVideoView) findViewById(R.id.video_view2);
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
        mLayoutAdd = (LinearLayout) findViewById(R.id.layout_add);
        mBtnAddMedia = (TextView) findViewById(R.id.btn_add_media);
        mBtnAddText = (TextView) findViewById(R.id.btn_add_text);
        mBtnEditText = (ImageView) findViewById(R.id.btn_edit_text);
        mLayoutEditText = (LinearLayout) findViewById(R.id.layout_edit_text);
        mEditText = (EditText) findViewById(R.id.edittext_input);
        mFontSpinner = (Spinner) findViewById(R.id.font_spinner);
        mColorPicker = (ColorPickerView) findViewById(R.id.color_picker);
        mBtnTextColor = (ImageView) findViewById(R.id.btn_text_color);
        mBtnTextBgrColor = (ImageView) findViewById(R.id.btn_text_background_color);
        mEdtColorHex = (EditText) findViewById(R.id.edt_color_hex);
        mLayoutBtnTextColor = (RelativeLayout) findViewById(R.id.layout_btn_text_color);
        mLayoutBtnTextBgrColor = (RelativeLayout) findViewById(R.id.layout_btn_text_bgr_color);
        mLayoutColorPicker = (LinearLayout) findViewById(R.id.layout_color_picker);
        mBtnCloseColorPicker = (Button) findViewById(R.id.btn_close_colorpicker);
        mIndicatorTextColor = (ImageView) findViewById(R.id.indicator_textcolor);
        mIndicatorTextBgr = (ImageView) findViewById(R.id.indicator_textbackground);
        mBtnVolume = (ImageView) findViewById(R.id.btn_volume);
        mLayoutTimeMark = (RelativeLayout) findViewById(R.id.layout_timemark);
        mTopLayout = (RelativeLayout) findViewById(R.id.top_layout);
        mMainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        mSeekbarIndicator = (LinearLayout) findViewById(R.id.seekbar_indicator);
        mLayoutFragment = (FrameLayout) findViewById(R.id.layout_fragment);
        mLayoutFloatView = (RelativeLayout) findViewById(R.id.layout_floatview);
        mBtnUpLevel = (ImageView) findViewById(R.id.btn_up_level);
        mLayoutExtraTools = (LinearLayout) findViewById(R.id.extra_toolbar);

        mColorPicker.setAlphaSliderVisible(true);
        mColorPicker.setOnColorChangedListener(this);

        mVideoTabLayout.setOnClickListener(onTabLayoutClick);
        mImageTabLayout.setOnClickListener(onTabLayoutClick);
        mAudioTabLayout.setOnClickListener(onTabLayoutClick);

        mTLShadow = new ImageView(this);
        mTLShadow.setBackgroundResource(R.drawable.shadow);
        mTLShadowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        mShadowIndicator = new ImageView(this);
        mShadowIndicator.setBackgroundResource(R.drawable.shadow_indicator);
        mShadowIndicatorParams = new RelativeLayout.LayoutParams(10, mTimeLineVideoHeight);
        mShadowIndicator.setLayoutParams(mShadowIndicatorParams);

        mActivity = this;
        setVideoRatio();
        mVideoList = new ArrayList<>();
        mImageList = new ArrayList<>();
        mTextList = new ArrayList<>();
        mAudioList = new ArrayList<>();
        mListInLayoutImage = new ArrayList<>();
        mListInLayoutText = new ArrayList<>();

        mBtnBack.setOnClickListener(onBtnBackClick);
        mBtnAdd.setOnClickListener(onBtnAddClick);
        mBtnPlay.setOnClickListener(onBtnPlayClick);
        mBtnExport.setOnClickListener(onBtnExportClick);
        mBtnUndo.setOnClickListener(onBtnUndoClick);
        mBtnDelete.setOnClickListener(onBtnDeleteClick);
        mBtnAddMedia.setOnClickListener(onBtnAddMediaClick);
        mBtnAddText.setOnClickListener(onBtnAddTextClick);
        mBtnEditText.setOnClickListener(onBtnEditClick);
        mLayoutBtnTextColor.setOnClickListener(onLayoutBtnTextColorClick);
        mLayoutBtnTextBgrColor.setOnClickListener(onLayoutBtnTextBgrColorClick);
        mBtnCloseColorPicker.setOnClickListener(onBtnCloseColorPickerClick);
        mBtnVolume.setOnClickListener(onBtnVolumeClick);
        mLayoutAdd.setOnClickListener(onLayoutAddClick);
        mBtnUpLevel.setOnClickListener(onBtnUpLevelClick);

        mEditText.setOnEditorActionListener(onEditTextActionListener);
        mEdtColorHex.setOnEditorActionListener(onEditColorActionListener);

        mTimeLineImage.setOnDragListener(onExtraDragListener);
        mTimeLineVideo.setOnDragListener(onExtraDragListener);
        mTimeLineText.setOnDragListener(onExtraDragListener);

        mCurrentVideoId = -1;
        mMaxTimeLineMs = 0;
        mPreviewStatus = BEGIN;
        mThreadPreviewVideo = new Thread(runnablePreview);

        mActiveVideoView = mVideoView1;
        mInActiveVideoView = mVideoView2;
        mScroll = true;

        mScrollView.setOnCustomScrollChanged(onCustomScrollChanged);
        mLayoutTimeLine.getViewTreeObserver().addOnGlobalLayoutListener(onLayoutTimeLineCreated);
        mVideoViewLayout.getViewTreeObserver().addOnGlobalLayoutListener(onVideoViewLayoutCreated);
        mLayoutVideo.getViewTreeObserver().addOnGlobalLayoutListener(onLayoutVideoCreated);
        mLayoutImage.getViewTreeObserver().addOnGlobalLayoutListener(onLayoutImageCreated);

        initFontManager();

        mMediaPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        saveSystemVolume();
        prepareLayoutAnimationAddFile();

        initDatabase();

        openLayoutProject();
        keepScreenOn();

        checkVip();
    }

    public void slideExtraToolsIn(boolean out) {
        if (!out && !mOpenExtraTools) {
            return;
        }
        setLayoutExtraToolsVisible(out);
        int distance = Utils.dpToPixel(this, 60);
        TranslateAnimation animation = out ? new TranslateAnimation(-distance, 0, 0, 0)
                                            : new TranslateAnimation(0, -distance, 0, 0);
        animation.setDuration(200);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        mLayoutExtraTools.startAnimation(animation);
    }

    private void setLayoutExtraToolsVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mLayoutExtraTools.setVisibility(visibility);
        mOpenExtraTools = visible;
    }

    public void setBtnBackVisible(boolean visible){
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnBack.setVisibility(visibility);
    }

    public void setBtnUpLevelVisible(boolean visible){
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnUpLevel.setVisibility(visibility);
    }

    View.OnClickListener onBtnUpLevelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            upLevelFileManager();
        }
    };

    private void checkVip() {
        mIabController = new IabController(this);
    }

    public void checkVipWithoutInternet() {
        new CheckVipWithoutInternetTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class CheckVipWithoutInternetTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            mIsVip = false;
            String vipAccount = Utils.getSharedPref(mActivity).getString(getString(R.string.pref_last_account), null);
            if (vipAccount == null) {
                return null;
            }
            AccountManager accountManager = AccountManager.get(mActivity);
            Account[] accounts = accountManager.getAccountsByType("com.google");
            for (Account account : accounts) {
                if (account.name.equals(vipAccount)) {
                    mIsVip = Utils.getSharedPref(mActivity).getBoolean(getString(R.string.pref_is_vip), false);
                    return null;
                }
            }
            return null;
        }
    }

    public void onCheckVipCompleted(boolean isVip) {
        mIsVip = isVip;
        Utils.getSharedPref(this).edit().putBoolean(getString(R.string.pref_is_vip), isVip).apply();
        if (isVip) {
            saveLastAccount();
        }
    }

    private void saveLastAccount() {
        new SaveLastAccountTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class SaveLastAccountTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            AccountManager accountManager = AccountManager.get(mActivity);
            Account[] accounts = accountManager.getAccountsByType("com.google");
            Utils.getSharedPref(mActivity).edit()
                    .putString(getString(R.string.pref_last_account), accounts[0].name).apply();
            return null;
        }
    }

    @Override
    public void onPositiveClick(int dialogId) {
        hideStatusBar();
        switch (dialogId) {
            case DialogClickListener.ASK_DONATE:
                startDonate();
                break;
            case DialogClickListener.DELETE_VIDEO:
                deleteVideo();
                updateBtnExportVisible();
                break;
            case DialogClickListener.DELETE_EXTRA:
                deleteExtraTimeline();
                updateBtnExportVisible();
                break;
            case DialogClickListener.DELETE_AUDIO:
                deleteAudioTimeLine();
                updateBtnExportVisible();
                break;
        }
    }

    private void startDonate() {
        mIabController.buyItem();
    }

    @Override
    public void onNegativeClick(int dialogId) {
        hideStatusBar();
    }

    private void initFontManager() {
        if (mFontPath != null) {
            return;
        }
        new LoadFontTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class LoadFontTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mFontPath = FontManager.getFontPaths(mActivity);
            mFontAdapter = new FontAdapter(mActivity, android.R.layout.simple_spinner_item, mFontPath);
            for (String font : mFontPath) {
                log(font);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mFontSpinner.setAdapter(mFontAdapter);
            mFontSpinner.setOnItemSelectedListener(onFontSelectedListener);
            mFontSpinner.setPrompt("hello");
        }
    }

    private void initFileManager() {
        if (mGalleryPagerAdapter != null) {
            return;
        }
        mGalleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mGalleryPagerAdapter);
        mViewPager.addOnPageChangeListener(onViewPagerChanged);
        mFragmentVideosGallery = new FragmentVideosGallery();
        mFragmentImagesGallery = new FragmentImagesGallery();
        mFragmentAudioGallery = new FragmentAudioGallery();
    }

    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initDatabase() {
        int oldDbVersion = Utils.getSharedPref(this)
                .getInt(getString(R.string.db_version), 0);
        mVideoTable = new VideoTable(this);
        mAudioTable = new AudioTable(this);
        mImageTable = new ImageTable(this);
        mTextTable = new TextTable(this);
        mProjectTable = new ProjectTable(this);
        if (oldDbVersion < Constants.DB_VERSION) {
            mProjectTable.dropTable();
            mVideoTable.dropTable();
            mImageTable.dropTable();
            mTextTable.dropTable();
            mAudioTable.dropTable();
            Utils.getSharedPref(this).edit()
                    .putInt(getString(R.string.db_version), Constants.DB_VERSION).apply();
        }
        mProjectTable.createTable();
        mVideoTable.createTable();
        mImageTable.createTable();
        mTextTable.createTable();
        mAudioTable.createTable();
    }

    public void resetActivity() {
        mVideoList.clear();
        mImageList.clear();
        mTextList.clear();
        mAudioList.clear();
        mListInLayoutImage.clear();
        mListInLayoutText.clear();
        mLayoutVideo.removeAllViews();
        mLayoutImage.removeAllViews();
        mLayoutText.removeAllViews();
        mLayoutAudio.removeAllViews();
        mLayoutFloatView.removeAllViews();
        setBtnPlayVisible(false);
        setBtnVolumeVisible(false);
        setBtnEditVisible(false);
        setBtnDeleteVisible(false);
        setBtnExportVisible(false);
        mCountVideo = 0;
        mCurrentVideoId = -1;
        mTLPositionInMs = 0;
    }

    private void openLayoutProject() {
        setLayoutFragmentVisible(true);
        ProjectFragment projectFragment = ProjectFragment.newInstance(mActivity);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment, projectFragment).commit();
    }

    private void startThreadPreview() {
        mRunThread = true;
        mThreadPreviewVideo = new Thread(runnablePreview);
        mThreadPreviewVideo.start();
        log(" Start");
    }

    public void stopThreadPreview() {
        mRunThread = false;
        log(" Stop");
    }

    private void prepareLayoutAnimationAddFile() {
        mLayoutAnimationAddFile = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLayoutAnimationAddFile.setLayoutParams(params);
        mImageShadowAnimation = new ImageView(this);
        params = new RelativeLayout.LayoutParams(200, 200);
        mImageShadowAnimation.setLayoutParams(params);
        mLayoutAnimationAddFile.addView(mImageShadowAnimation);
    }

    private void startAnimationAddFile(final int fileType,
                                       int duration, final float xDes, float yDes) {
        mMainLayout.addView(mLayoutAnimationAddFile);

        float x0 = xDes / 3;
        final float a = yDes / (xDes * xDes - 2 * x0 * xDes);
        final float b = -2 * x0 * a;

        ValueAnimator animator = ValueAnimator.ofFloat(0, xDes);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) (animation.getAnimatedValue()))
                        .floatValue();
                // Set translation of your view here. Position can be calculated
                // out of value. This code should move the view in a half circle.
                float x = value;
                float y = a * x * x + b * x;
                mImageShadowAnimation.setTranslationX(x);
                mImageShadowAnimation.setTranslationY(y);
                if (value == xDes) {
                    onAnimationAddFileCompleted(fileType);
                }
            }
        });
        animator.start();
    }

    private void onAnimationAddFileCompleted(int fileType) {
        removeLayoutAnimationAddFile();
        if (fileType == ADD_VIDEO) {
            addVideoTL();
            return;
        }

        if (fileType == ADD_IMAGE) {
            addImageTL();
            return;
        }

        if (fileType == ADD_TEXT) {
            addTextTL();
            return;
        }

        if (fileType == ADD_AUDIO) {
            addAudioTL();
        }

    }

    private void removeLayoutAnimationAddFile() {
        mMainLayout.removeView(mLayoutAnimationAddFile);
    }

    View.OnClickListener onLayoutAddClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openLayoutAdd(false);
        }
    };

    ViewTreeObserver.OnGlobalLayoutListener onLayoutVideoCreated = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mTimeLineVideoHeight = mLayoutVideo.getHeight() - 10;
            addVideoControler();
            mLayoutVideo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    public void openProject() {
        restoreAllVideoTL(mProjectId);
        restoreAllAudioTL(mProjectId);
        restoreAllImageTL(mProjectId);
        restoreAllTextTL(mProjectId);

        updateBtnExportVisible();
        fixAllVideosHasNoAudio();
    }

    ViewTreeObserver.OnGlobalLayoutListener onLayoutImageCreated = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mTimeLineImageHeight = mLayoutImage.getHeight() - 10;
            addExtraNAudioController();
            mLayoutImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    ViewTreeObserver.OnGlobalLayoutListener onVideoViewLayoutCreated = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int[] point = new int[2];
            mVideoViewLayout.getLocationOnScreen(point);
            mVideoViewLeft = point[0];
            mVideoViewLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    private void saveSystemVolume() {
        mSystemVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            saveSystemVolume();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setBtnVolumeVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnVolume.setVisibility(visibility);
    }

    public void onVolumeChanged(int volume) {
        if (mSelectedTL == TIMELINE_VIDEO) {
            mSelectedVideoTL.volume = convertVolumeToFloat(volume);
        }

        if (mSelectedTL == TIMELINE_AUDIO) {
            mSelectedAudioTL.volume = convertVolumeToFloat(volume);
        }
        updateSystemVolume();
        hideStatusBar();
    }

    public void onIncreaseVolumeTaskCompleted(boolean isVideo) {
        if (isVideo) {

        } else {
            changeAudio(mCurrentAudioTL, mSeekTimeAudio);
        }
    }

    private float convertVolumeToFloat(int volume) {
        float value = (float) volume / 100f;
        if (value > 1.1) {
            value *= 2f;
        }
        if (value < 0.8) {
            value /= 2f;
        }
        log("volume= " + value);
        return value;
    }

    private int convertVolumeToInt(float volume) {
        if (volume > 2.2) {
            volume /= 2f;
        }
        if (volume < 0.4) {
            volume *= 2f;
        }
        log("dialog volume= " + volume);
        return Math.round(volume * 100);
    }

    View.OnClickListener onBtnVolumeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int volume;
            if (mSelectedTL == TIMELINE_VIDEO) {
                volume = convertVolumeToInt(mSelectedVideoTL.volume);
            } else {
                volume = convertVolumeToInt(mSelectedAudioTL.volume);
            }
            VolumeEditor.newInstance(mActivity, volume).show(getFragmentManager()
                    .beginTransaction(), "volume");
            pausePreview();
        }
    };

    private void setToolbarVisible(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
        TranslateAnimation animation;
        if (show) {
            animation = new TranslateAnimation(-Utils.dpToPixel(this, 50), 0, 0, 0);
        } else {
            animation = new TranslateAnimation(0, -Utils.dpToPixel(this, 50), 0, 0);
        }
        animation.setDuration(200);
        view.startAnimation(animation);
    }

    View.OnClickListener onBtnCloseColorPickerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showColorPicker(false, true);
        }
    };

    TextView.OnEditorActionListener onEditColorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                int color = convertToIntegerColor(mEdtColorHex.getText().toString());
                mColorPicker.setColor(color);
                setColorForViews(color);
                mEdtColorHex.clearFocus();
            }
            return false;
        }
    };

    View.OnClickListener onLayoutBtnTextBgrColorClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mShowColorPicker) {
                if (!mChooseTextColor) {
                    showColorPicker(false, true);
                } else {
                    mChooseTextColor = false;
                    showColorPicker(true, false);
                }
            } else {
                mChooseTextColor = false;
                showColorPicker(true, true);
            }
        }
    };

    View.OnClickListener onLayoutBtnTextColorClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mShowColorPicker) {
                if (mChooseTextColor) {
                    showColorPicker(false, true);
                } else {
                    mChooseTextColor = true;
                    showColorPicker(true, false);
                }
            } else {
                mChooseTextColor = true;
                showColorPicker(true, true);
            }
        }
    };

    private void showColorPicker(boolean show, boolean animation) {
        mShowColorPicker = show;
        if (show) {
            mLayoutColorPicker.setVisibility(View.VISIBLE);
            int color;
            if (mChooseTextColor) {
                color = mSelectedExtraTL.floatText.mColor;
                mIndicatorTextColor.setVisibility(View.VISIBLE);
                mIndicatorTextBgr.setVisibility(View.INVISIBLE);
            } else {
                color = mSelectedExtraTL.floatText.mBackgroundColor;
                mIndicatorTextBgr.setVisibility(View.VISIBLE);
                mIndicatorTextColor.setVisibility(View.INVISIBLE);
            }
            mColorPicker.setColor(color);
            mEdtColorHex.setText(convertToHexColor(color, false));
        } else {
            mLayoutColorPicker.setVisibility(View.GONE);
            mIndicatorTextBgr.setVisibility(View.INVISIBLE);
            mIndicatorTextColor.setVisibility(View.INVISIBLE);
        }
        if (animation) {
            slideColorPicker(show);
        }
    }

    private void slideColorPicker(boolean show) {
        TranslateAnimation animation;
        if (show) {
            animation = new TranslateAnimation(mScrollView.getWidth() / 2, 0, 0, 0);
        } else {
            animation = new TranslateAnimation(0, mScrollView.getWidth() / 2, 0, 0);
        }
        animation.setDuration(300);
        mLayoutColorPicker.startAnimation(animation);
    }

    @Override
    public void onColorChanged(int color) {
        mEdtColorHex.setText(convertToHexColor(color, false));
        setColorForViews(color);
    }

    private void setColorForViews(int color) {
        FloatText floatText = mSelectedExtraTL.floatText;
        if (mChooseTextColor) {
            floatText.setTextColor(color);
            mBtnTextColor.setBackground(new AlphaColorDrawable(color));
        } else {
            floatText.setTextBgrColor(color);
            mBtnTextBgrColor.setBackground(new AlphaColorDrawable(color));
        }
    }

    private int convertToIntegerColor(String hexColor) {
        return Color.parseColor("#" + hexColor);
    }

    public String convertToHexColor(int color, boolean export) {
        String resultColor = "";
        String s = String.format("%08X", (0xFFFFFFFF & color));
        if (export) {
            resultColor += s.substring(2) + "@0x" + s.substring(0, 2);
        } else {
            resultColor += s;
        }
        return resultColor;
    }

    AdapterView.OnItemSelectedListener onFontSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String font = mFontPath.get(position);
            if (mSelectedExtraTL != null) {
                mSelectedExtraTL.floatText.setFont(font, position);
            }
            mFontAdapter.setSelectedItem(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    TextView.OnEditorActionListener onEditTextActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String text = mEditText.getText().toString();
                mSelectedExtraTL.setText(text);
                mSelectedExtraTL.floatText.setText(text);
                mEditText.clearFocus();
            }
            return false;
        }
    };

    View.OnClickListener onBtnEditClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSelectedTL == TIMELINE_VIDEO) {
                openLayoutTrimVideo();
            } else {
                if (mOpenLayoutEditText) {
                    openLayoutEditText(false);
                } else {
                    initFontManager();
                    openLayoutEditText(true);
                }
            }
        }
    };

    private void openLayoutTrimVideo() {
        setActiveVideoViewVisible(false);
        mTrimFragment = TrimFragment.newInstance(mActivity, mSelectedVideoTL);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment, mTrimFragment).commit();
        setLayoutFragmentVisible(true);
        mOpenLayoutTrimVideo = true;
    }

    public void closeLayoutTrimVideo() {
        mTrimFragment.close();
    }

    public void setActiveVideoViewVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mActiveVideoView.setVisibility(visibility);
    }

    public void onTrimVideoCompleted(int startTime, int endTime) {
        mSelectedVideoTL.drawTimeLineWith(startTime, endTime);
        getLeftMargin(mCountVideo - 1);
        mMaxTimeLineMs = mVideoList.get(mCountVideo - 1).endInTimeLineMs;
        scrollTo(mSelectedVideoTL.startInTimeLineMs);
        mActiveVideoView.seekTo(mSelectedVideoTL.startTimeMs);
    }

    public void setLayoutFragmentVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mLayoutFragment.setVisibility(visibility);
        mOpenLayoutProject = visible;
    }

    public void openLayoutEditText(boolean open) {
        int visible = open ? View.VISIBLE : View.GONE;
        mLayoutEditText.setVisibility(visible);
        slideEditText(open);
        mOpenLayoutEditText = open;
        if (!open) {
            showColorPicker(false, false);
            hideStatusBar();
        } else {
            updateLayoutEditText();
        }
    }

    public void updateLayoutEditText() {
        mEditText.setText(mSelectedExtraTL.text);
        FloatText floatText = mSelectedExtraTL.floatText;
        mBtnTextColor.setBackground(new AlphaColorDrawable(floatText.mColor));
        mBtnTextBgrColor.setBackground(new AlphaColorDrawable(floatText.mBackgroundColor));

        mFontAdapter.setSelectedItem(floatText.fontId);
        mFontSpinner.setSelection(floatText.fontId);
    }

    private void slideEditText(boolean open) {
        TranslateAnimation animation;
        if (open) {
            animation = new TranslateAnimation(0, 0, mScrollView.getHeight(), 0);
        } else {
            animation = new TranslateAnimation(0, 0, 0, mScrollView.getHeight());
        }
        animation.setDuration(300);
        mLayoutEditText.startAnimation(animation);
    }

    View.OnClickListener onBtnAddMediaClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initFileManager();
            openFileManager(true);
            openLayoutAdd(false);
            setBtnBackVisible(false);
            setBtnExportVisible(false);
        }
    };

    View.OnClickListener onBtnAddTextClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addText();
            openLayoutAdd(false);
        }
    };

    View.OnClickListener onBtnDeleteClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSelectedTL == TIMELINE_VIDEO) {
                DialogConfirm.newInstance(mActivity, DialogClickListener.DELETE_VIDEO)
                        .show(mActivity.getSupportFragmentManager().beginTransaction(), "delete video");
            } else if (mSelectedTL == TIMELINE_EXTRA) {
                DialogConfirm.newInstance(mActivity, DialogClickListener.DELETE_EXTRA)
                        .show(mActivity.getSupportFragmentManager().beginTransaction(), "delete extra");
            } else {
                DialogConfirm.newInstance(mActivity, DialogClickListener.DELETE_AUDIO)
                        .show(mActivity.getSupportFragmentManager().beginTransaction(), "delete audio");
            }

        }
    };

    private void deleteAudioTimeLine() {
        mAudioList.remove(mSelectedAudioTL);
        mLayoutAudio.removeView(mSelectedAudioTL);
        invisibleAudioControl();
    }

    private void deleteExtraTimeline() {
        if (mSelectedExtraTL.isImage) {
            mImageList.remove(mSelectedExtraTL);
            mLayoutFloatView.removeView(mSelectedExtraTL.floatImage);
        } else {
            mTextList.remove(mSelectedExtraTL);
            mLayoutFloatView.removeView(mSelectedExtraTL.floatText);
        }
        if (mSelectedExtraTL.inLayoutImage) {
            mLayoutImage.removeView(mSelectedExtraTL);
            mListInLayoutImage.remove(mSelectedExtraTL);
        } else {
            mLayoutText.removeView(mSelectedExtraTL);
            mListInLayoutText.remove(mSelectedExtraTL);
        }
        invisibleExtraControl();
    }

    private void deleteVideo() {
        mVideoList.remove(mSelectedVideoTL);
        mLayoutVideo.removeView(mSelectedVideoTL);
        slideExtraToolsIn(false);
        mCountVideo--;
        mCurrentVideoId--;
        if (mCountVideo > 0) {
            getLeftMargin(mCountVideo - 1);
            mMaxTimeLineMs = mVideoList.get(mCountVideo - 1).endInTimeLineMs;
            if (mCurrentVideoId < 0) {
                mCurrentVideoId = 0;
            }
            mSelectedVideoTL = mVideoList.get(mCurrentVideoId);
            if (mSelectedVideoTL.isExists) {
                setVideoViewVisible(true);
                mActiveVideoView.setVideoPath(mSelectedVideoTL.videoPath);
            } else {
                setVideoViewVisible(false);
            }
            mScrollView.scrollTo(mSelectedVideoTL.startInTimeLineMs / Constants.SCALE_VALUE, 0);
        } else {
            mMaxTimeLineMs = 0;
            mActiveVideoView.stopPlayback();
            mActiveVideoView.setVisibility(View.GONE);
            mActiveVideoView.setVisibility(View.VISIBLE);
            setBtnExportVisible(false);
            setBtnPlayVisible(false);
        }
    }

    @Override
    public void seekTo(int value, boolean scroll) {
        mActiveVideoView.seekTo(value);
        mScroll = scroll;
    }

    ViewTreeObserver.OnGlobalLayoutListener onLayoutTimeLineCreated = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mLeftMarginTimeLine = mLayoutTimeLine.getWidth() / 2 - Utils.dpToPixel(mActivity, 45);
            updateLayoutTimeLine();
            setTimeMark();
            mLayoutTimeLine.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    private void setTimeMark() {
        for (int i = 0; i <= 3600; i++) {
            if (i % 5 == 0) {
                BigTimeMark bigTimeMark = new BigTimeMark(this);
                mLayoutTimeMark.addView(bigTimeMark);
                bigTimeMark.getParams().leftMargin = mLeftMarginTimeLine + i * 50;
                TimeText timeText = new TimeText(this, i);
                timeText.getParams().leftMargin = mLeftMarginTimeLine + i * 50 + 3;
                timeText.getParams().bottomMargin = 10;
                mLayoutTimeMark.addView(timeText);
            } else {
                SmallTimeMark smallTimeMark = new SmallTimeMark(this);
                mLayoutTimeMark.addView(smallTimeMark);
                smallTimeMark.getParams().leftMargin = mLeftMarginTimeLine + i * 50;
            }
        }
    }

    View.OnClickListener onBtnUndoClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            resetVideoView();
        }
    };

    View.OnClickListener onBtnExportClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            pausePreview();
            if (checkIfFileNotExists()) {
                toast(getString(R.string.toast_file_not_exists_when_click_export));
                return;
            }
            exportVideo();
        }
    };

    public void deleteAllObjects(int projectId) {
        mVideoTable.deleteVideoOf(projectId);
        mAudioTable.deleteAudioOf(projectId);
        mImageTable.deleteImageOf(projectId);
        mTextTable.deleteTextOf(projectId);
    }

    public void saveProject() {
        deleteAllObjects(mProjectId);
        saveVideoObjects();
        saveAudioObjects();
        saveImageObjects();
        saveTextObjects();
        deleteProjectIfEmpty();
    }

    private void deleteProjectIfEmpty() {
        if (mVideoList.isEmpty() && mImageList.isEmpty()
                 && mAudioList.isEmpty()) {
            if (mIsVip && mTextList.isEmpty() || !mIsVip && mTextList.size() == 1) {
                mProjectTable.deleteProject(mProjectId);
            }
        }
    }

    public void exportVideo() {
        mExportFragment = ExportFragment.newInstance(mActivity);
        mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment, mExportFragment).commit();
        setLayoutFragmentVisible(true);
        mOpenLayoutExport = true;
    }

    @Override
    protected void onDestroy() {
        log("on destroy");
        stopThreadPreview();
        stopMediaPlayer();
        Utils.deleteTempFiles();
        super.onDestroy();
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

            mThreadPreviewVideo = null;
            log("intterupt");
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
                    updateImageVisibility();
                    updateTextVisibility();
                    updateMediaPlayer();
                    log(" Running");
                    break;
            }
        }
    };

    private void updateMediaPlayer() {
        if (mActiveVideoView.isPlaying()) {
            playAudio();
        } else {
            pauseAudio();
        }

        updateAudioVolume();

        if (mPreviewStatus == END && mMediaPlayer != null) {
            mMediaPlayer.seekTo(mCurrentAudioTL.startTimeMs);
        }

        for (int i = 0; i < mAudioList.size(); i++) {
            AudioTL audio = mAudioList.get(i);

            if (mTLPositionInMs >= audio.startInTimelineMs && mTLPositionInMs <= audio.endInTimelineMs) {
                if (!audio.equals(mCurrentAudioTL)) {
                    mCurrentAudioTL = audio;
                    changeAudio(audio, audio.startTimeMs);
                    return;
                } else {
                    mSeekTimeAudio = mTLPositionInMs - audio.startInTimelineMs + audio.startTimeMs;
                    if (mMediaPlayer == null) {
                        changeAudio(audio, mSeekTimeAudio);
                    }
                }
                return;
            }
        }

        stopMediaPlayer();
    }

    private void updateMediaPlayerOnScroll() {
        updateMediaPlayer();
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(mSeekTimeAudio);
        }
    }

    private void updateAudioVolume() {
        if (mMediaPlayer != null && mCurrentAudioTL != null) {
            float volume = mCurrentAudioTL.volumePreview;
            if (volume > 1) {
                volume = 1;
            }
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    private void updateSystemVolume() {
        float audioVolume = mCurrentAudioTL != null ? mCurrentAudioTL.volume : 1;
        float videoVolume = mCurrentVideoTL != null ? mCurrentVideoTL.volume : 1;
        float maxVolume = Math.max(audioVolume, videoVolume);
        float value;
        if (maxVolume > 1) {
            value = maxVolume - 1;
        } else {
            value = 0;
        }
        int maxSystemVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float addSystemVolume = (maxSystemVolume - mSystemVolume) * value / 6;

        if (addSystemVolume > 0) {
            float increment = addSystemVolume / maxSystemVolume;
            if (audioVolume < videoVolume) {
                if (mCurrentAudioTL != null) {
                    mCurrentAudioTL.volumePreview -= increment * mCurrentAudioTL.volume;
                }
            } else {
                if (mCurrentVideoTL != null) {
                    mCurrentVideoTL.volumePreview -= increment * mCurrentVideoTL.volume;
                }
            }
        } else {
            if (mCurrentAudioTL != null) {
                mCurrentAudioTL.volumePreview = mCurrentAudioTL.volume;
            }
            if (mCurrentVideoTL != null) {
                mCurrentVideoTL.volumePreview = mCurrentVideoTL.volume;
            }
        }

        int volume = Math.round(mSystemVolume + addSystemVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    private void updateImageVisibility() {
        for (int i = 0; i < mImageList.size(); i++) {
            ExtraTL image = mImageList.get(i);
            FloatImage floatImage = image.floatImage;
            if (mTLPositionInMs >= image.startInTimeLineMs && mTLPositionInMs <= image.endInTimeLineMs) {
                floatImage.setVisibility(View.VISIBLE);
            } else {
                floatImage.setVisibility(View.GONE);
            }
        }
    }

    private void updateTextVisibility() {
        for (int i = 0; i < mTextList.size(); i++) {
            ExtraTL text = mTextList.get(i);
            FloatText floatText = text.floatText;
            if (mTLPositionInMs >= text.startInTimeLineMs && mTLPositionInMs <= text.endInTimeLineMs) {
                floatText.setVisibility(View.VISIBLE);
            } else {
                if (!floatText.isWaterMark) {
                    floatText.setVisibility(View.GONE);
                }
            }
        }
    }

    CustomHorizontalScrollView.OnCustomScrollChanged onCustomScrollChanged =
            new CustomHorizontalScrollView.OnCustomScrollChanged() {
                @Override
                public void onStartScroll() {
                    pausePreview();
                }

                @Override
                public void onEndScroll() {
                }

                @Override
                public void onScrollChanged() {
                    if (mCountVideo < 1) {
                        return;
                    }
                    int scrollPosition = mScrollView.getScrollX();
                    mTLPositionInMs = scrollPosition * Constants.SCALE_VALUE;

                    // update VideoView
                    VideoTL videoTL = null;
                    int timelineId = 0;
                    for (int i = 0; i < mVideoList.size(); i++) {
                        videoTL = mVideoList.get(i);
                        if (mTLPositionInMs >= videoTL.startInTimeLineMs && mTLPositionInMs <= videoTL.endInTimeLineMs) {
                            timelineId = i;
                            break;
                        }
                    }
                    int positionInVideo;
                    if (timelineId > 0) {
                        VideoTL previousTimeLine = mVideoList.get(timelineId - 1);
                        positionInVideo = mTLPositionInMs - previousTimeLine.endInTimeLineMs + videoTL.startTimeMs;
                    } else {
                        positionInVideo = mTLPositionInMs + videoTL.startTimeMs;
                    }

                    if (mCurrentVideoId != timelineId && videoTL != null) {
                        mCurrentVideoId = timelineId;
                        mCurrentVideoTL = videoTL;
                        if (videoTL.isExists) {
                            setVideoViewVisible(true);
                            mActiveVideoView.setVideoPath(videoTL.videoPath);
                        } else {
                            setVideoViewVisible(false);
                            toast("Video is not found");
                        }
                    }

                    mActiveVideoView.seekTo(positionInVideo);
                    updateMediaPlayerOnScroll();
                    updateTextVisibility();
                    updateImageVisibility();
                    updateSystemVolume();
                }
            };

    private void updateBtnPlay() {
        if (mActiveVideoView.isPlaying()) {
            mBtnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            mBtnPlay.setImageResource(R.drawable.ic_play);
        }
    }

    private void updateCurrentPosition() {
        if (mTLPositionInMs >= mMaxTimeLineMs) {
            return;
        }
        if (mCurrentVideoId == -1) {
            mTLPositionInMs = 0;
        } else {
            mTLPositionInMs = 0;
            for (int i = 0; i < mCurrentVideoId; i++) {
                mTLPositionInMs += mVideoList.get(i).width * Constants.SCALE_VALUE;
            }
            VideoTL currentTimeLine = mVideoList.get(mCurrentVideoId);
            int currentTime = mActiveVideoView.getCurrentPosition();
            if (currentTime < currentTimeLine.startTimeMs) {
                currentTime = currentTimeLine.startTimeMs;
            }
            mTLPositionInMs += currentTime - currentTimeLine.startTimeMs + 50;
        }

        if (!mScroll) {
            return;
        }

        int scrollPosition = mTLPositionInMs / Constants.SCALE_VALUE;
        mScrollView.scrollTo(scrollPosition, 0);
    }

    private void updatePreviewStatus() {
        if (mActiveVideoView.isPlaying()) {
            mPreviewStatus = PLAY;

        } else {
            mPreviewStatus = PAUSE;
        }
        if (mTLPositionInMs == 0) {
            mPreviewStatus = BEGIN;
        }
        if (mTLPositionInMs >= mMaxTimeLineMs - 400) {
            mPreviewStatus = END;
        }

    }

    private void updateVideoView() {
        if (mCountVideo < 1) {
            return;
        }
        if (mTLPositionInMs >= mMaxTimeLineMs) {
            pausePreview();
            return;
        }

        VideoTL lastVideo = mVideoList.get(mCountVideo - 1);
        int halfTimeLastVideo = (lastVideo.startInTimeLineMs + lastVideo.endInTimeLineMs) / 2;
        if (mTLPositionInMs > halfTimeLastVideo && !mActiveVideoView.isPlaying()) {
            stopThreadPreview();
            return;
        }

        updateVideoVolume();

        prepareNextVideo();

        VideoTL videoTL = null;
        int timelineId = mCurrentVideoId;
        for (int i = 0; i < mVideoList.size(); i++) {
            videoTL = mVideoList.get(i);
            if (mTLPositionInMs >= videoTL.startInTimeLineMs && mTLPositionInMs <= videoTL.endInTimeLineMs) {
                timelineId = i;
                break;
            }
        }

        int halfTimeVideo = (videoTL.startInTimeLineMs + videoTL.endInTimeLineMs) / 2;

        if (mRunThread && timelineId < mCountVideo - 1 && !mActiveVideoView.isPlaying()
                && mTLPositionInMs > halfTimeVideo) {
            timelineId++;
            videoTL = mVideoList.get(timelineId);
            log("increase");
        }

        if (mCurrentVideoId != timelineId && videoTL != null) {
            if (mCurrentVideoId != mCountVideo - 1) {
                playNextVideo();
            }
            mCurrentVideoId = timelineId;
            if (mCurrentVideoId != -1 && mCountVideo > 0) {
                mCurrentVideoTL = mVideoList.get(mCurrentVideoId);
            }

            if (mPreviewStatus == BEGIN) {
                if (videoTL.isExists) {
                    setVideoViewVisible(true);
                    mActiveVideoView.setVideoPath(videoTL.videoPath);
                } else {
                    setVideoViewVisible(false);
                }
            }
            mActiveVideoView.seekTo(videoTL.startTimeMs);
            mActiveVideoView.start();
            updateSystemVolume();
        }
    }

    private void prepareNextVideo() {
        if (mCurrentVideoId != -1 && mCurrentVideoId < mCountVideo - 1) {
            VideoTL nextVideoTL = mVideoList.get(mCurrentVideoId + 1);
            if (nextVideoTL.isExists) {
                mInActiveVideoView.setVideoPath(nextVideoTL.videoPath);
                mInActiveVideoView.seekTo(10);
            }
        }
    }

    private void updateVideoVolume() {
        if (mActiveVideoView != null && mCurrentVideoTL != null) {
            float volume = mCurrentVideoTL.volumePreview;
            if (volume > 1) {
                volume = 1;
            }
            mActiveVideoView.setVolume(volume);
        }
    }

    private void playNextVideo() {
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

    private void setVideoViewVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mActiveVideoView.setVisibility(visibility);
    }

    private void resetVideoView() {
        mCurrentVideoId = -1;
        mTLPositionInMs = 0;
    }

    View.OnClickListener onBtnPlayClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mActiveVideoView.isPlaying()) {
                mBtnPlay.setImageResource(R.drawable.ic_play);
                pausePreview();
            } else {
                openFileManager(false);
                hideAllFloatNControlers();

                if (checkIfFileNotExists()) {
                    toast(getString(R.string.toast_file_not_exists_when_click_play));
                    return;
                }

                startThreadPreview();
                startPreview();
                mBtnPlay.setImageResource(R.drawable.ic_pause);
            }
        }
    };

    private void hideAllFloatNControlers() {
        setFloatImageVisible(null);
        setFloatTextVisible(null);
        setAudioControlVisible(false);
        setExtraControlVisible(false);
        unSelectVideoTL();
    }

    public void pausePreview() {
        pauseVideo();
        pauseAudio();
        stopThreadPreview();
    }

    private void startPreview() {
        playVideo();
        playAudio();
    }

    private void pauseVideo() {
        if (mActiveVideoView.isPlaying()) {
            mActiveVideoView.pause();
        }
    }

    private void playAudio() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    private void pauseAudio() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    private void playVideo() {
        if (mPreviewStatus == BEGIN || mPreviewStatus == END) {
            resetVideoView();
        }
        if (mPreviewStatus == PAUSE) {
            mActiveVideoView.start();
        }
    }

    public void addVideo(String videoPath, int videoCoord[]) {
        startAnimationAddVideo(videoPath, videoCoord);
    }

    private void startAnimationAddVideo(String videoPath, int videoCoord[]) {
        mVideoPath = videoPath;

        int[] indicatorCoord = new int[2];
        mSeekbarIndicator.getLocationOnScreen(indicatorCoord);
        int xDes = indicatorCoord[0] - videoCoord[0];
        int yDes = indicatorCoord[1] - videoCoord[1];

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageShadowAnimation.getLayoutParams();
        params.leftMargin = videoCoord[0];
        params.topMargin = videoCoord[1];
        Glide.with(this).load(videoPath).into(mImageShadowAnimation);

        startAnimationAddFile(ADD_VIDEO, 500, xDes, yDes);
    }

    public void getVideoOrder() {
        for (int i = 0; i < mVideoList.size(); i++) {
            VideoTL videoTL = mVideoList.get(i);
            videoTL.orderInList = i;
        }
    }

    public void saveVideoObjects() {
        getVideoOrder();
        for (VideoTL videoTL : mVideoList) {
            VideoObject videoObject = videoTL.getVideoObject();
            mVideoTable.insertValue(videoObject);
        }
        if (mVideoList.size() > 0) {
            VideoObject videoObject = mVideoList.get(0).getVideoObject();
            mProjectTable.updateValue(mProjectId, ProjectTable.FIRST_VIDEO, videoObject.path);
        }
    }

    public void restoreAllVideoTL(int projectId) {
        ArrayList<VideoObject> list = mVideoTable.getData(projectId);
        log("List Video = " + list.size());
        if (list.size() < 1) {
            return;
        }
        for (VideoObject video : list) {
            restoreVideoTL(video);
        }
        VideoTL videoTL = mVideoList.get(mVideoList.size() - 1);
        mMaxTimeLineMs = videoTL.endInTimeLineMs;
    }

    public void restoreVideoTL(VideoObject video) {
        VideoTL videoTL = new VideoTL(this, video.path, mTimeLineVideoHeight);
        videoTL.setOnClickListener(onVideoTimeLineClick);
        videoTL.setOnLongClickListener(onVideoLongClick);
        videoTL.restoreVideoObject(video);

        int order = Integer.parseInt(video.orderInList);
        mVideoList.add(order, videoTL);
        mLayoutVideo.addView(videoTL);
        mCountVideo++;

        setBtnPlayVisible(true);
    }

    private void addVideoTL() {
        VideoTL videoTL = new VideoTL(this, mVideoPath, mTimeLineVideoHeight);
        videoTL.setOnClickListener(onVideoTimeLineClick);
        videoTL.setOnLongClickListener(onVideoLongClick);

        mVideoList.add(videoTL);
        mLayoutVideo.addView(videoTL);
        mCountVideo++;
        getLeftMargin(mCountVideo - 1);

        mMaxTimeLineMs = videoTL.endInTimeLineMs;
        setWaterMarkEndTime(mMaxTimeLineMs);
        mTLPositionInMs = videoTL.startInTimeLineMs;
        scrollTo(mTLPositionInMs);
        mActiveVideoView.setVideoPath(videoTL.videoPath);
        mCurrentVideoId = mCountVideo - 1;
        fixIfVideoHasNoAudio(videoTL);

        mSelectedVideoTL = videoTL;
        updateBtnExportVisible();
        setBtnDeleteVisible(true);
        setBtnEditVisible(true);
        setBtnPlayVisible(true);
        setBtnVolumeVisible(true);
        setBtnEditIcon(R.drawable.ic_cut_video);
        highlightSelectedVideoTL();
        setLayoutExtraToolsVisible(true);
        mSelectedTL = TIMELINE_VIDEO;
    }

    private void setWaterMarkEndTime(int endTime){
        if (mIsVip) {
            return;
        }
        mTextList.get(0).endInTimeLineMs = endTime;
    }

    private void scrollTo(int time) {
        mScrollView.scrollTo(time / Constants.SCALE_VALUE, 0);
    }

    private void fixIfVideoHasNoAudio(VideoTL videoTL) {
        new AddSilentAudoTask(this, videoTL).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void fixAllVideosHasNoAudio() {
        new AddSilentAudoTask(this, mVideoList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setBtnPlayVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnPlay.setVisibility(visibility);
    }

    public void setBtnEditVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnEditText.setVisibility(visibility);
    }

    public void addImage(String imagePath, int imageCoord[]) {
        startAnimationAddImage(imagePath, imageCoord);
    }

    public void getTextOrder() {
        for (int i = 0; i < mListInLayoutImage.size(); i++) {
            ExtraTL extraTL = mListInLayoutImage.get(i);
            extraTL.orderInLayout = i;
        }

        for (int i = 0; i < mListInLayoutText.size(); i++) {
            ExtraTL extraTL = mListInLayoutText.get(i);
            extraTL.orderInLayout = i;
        }

        for (int i = 0; i < mTextList.size(); i++) {
            ExtraTL extraTL = mTextList.get(i);
            extraTL.orderInList = i;
        }
    }

    public void saveTextObjects() {
        getTextOrder();
        int i = 0;
        if (!mIsVip) {
            i = 1;
        }
       while (i < mTextList.size()) {
            ExtraTL extraTL = mTextList.get(i);
            TextObject text = extraTL.getTextObject();
            mTextTable.insertValue(text);
            i++;
        }
    }

    public class SaveProjectTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            saveProject();
            return null;
        }
    }

    public void getImageOrder() {
        for (int i = 0; i < mListInLayoutImage.size(); i++) {
            ExtraTL extraTL = mListInLayoutImage.get(i);
            extraTL.orderInLayout = i;
        }

        for (int i = 0; i < mListInLayoutText.size(); i++) {
            ExtraTL extraTL = mListInLayoutText.get(i);
            extraTL.orderInLayout = i;
        }

        for (int i = 0; i < mImageList.size(); i++) {
            ExtraTL extraTL = mImageList.get(i);
            extraTL.orderInList = i;
        }
    }

    public void saveImageObjects() {
        getImageOrder();
        for (ExtraTL extraTL : mImageList) {
            ImageObject image = extraTL.getImageObject();
            mImageTable.insertValue(image);
        }
    }

    public void restoreAllImageTL(int projectId) {
        ArrayList<ImageObject> listImages = mImageTable.getData(projectId);
        for (ImageObject image : listImages) {
            restoreImageTL(image);
        }
    }

    public void restoreImageTL(ImageObject image) {
        int leftMargin = Integer.parseInt(image.left);
        ExtraTL extraTL = new ExtraTL(this, image.path, mTimeLineImageHeight, leftMargin, true);
        extraTL.setOnClickListener(onExtraTimeLineClick);
        extraTL.setOnLongClickListener(onExtraTimelineLongClick);
        extraTL.restoreImageTL(image);

        mImageList.add(extraTL.orderInList, extraTL);
        if (extraTL.inLayoutImage) {
            mLayoutImage.addView(extraTL);
            mListInLayoutImage.add(extraTL.orderInLayout, extraTL);
        } else {
            mLayoutText.addView(extraTL);
            mListInLayoutText.add(extraTL.orderInLayout, extraTL);
        }

        Bitmap bitmap;
        if (extraTL.isExists) {
            bitmap = BitmapFactory.decodeFile(image.path);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_unknow_file);
        }

        FloatImage floatImage = new FloatImage(this, bitmap);
        floatImage.restoreState(image);
        extraTL.floatImage = floatImage;
        floatImage.timeline = extraTL;
        floatImage.drawBorder(false);
        floatImage.setVisibility(View.GONE);
        mLayoutFloatView.addView(floatImage);
    }

    private void addImageTL() {
        int leftMargin = mLeftMarginTimeLine + mScrollView.getScrollX();
        ExtraTL extraTL = new ExtraTL(this, mImagePath, mTimeLineImageHeight, leftMargin, true);
        extraTL.setOnClickListener(onExtraTimeLineClick);
        extraTL.setOnLongClickListener(onExtraTimelineLongClick);
        addExtraTLToTL(extraTL, leftMargin);
        mImageList.add(extraTL);

        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
        FloatImage floatImage = new FloatImage(this, bitmap);
        extraTL.floatImage = floatImage;
        floatImage.timeline = extraTL;
        mLayoutFloatView.addView(floatImage);
        setFloatImageVisible(mSelectedExtraTL);
        setFloatTextVisible(null);
        floatImage.drawBorder(true);

        restoreExtraControl(extraTL);
        setExtraControlVisible(true);
        unSelectVideoTL();

        updateBtnExportVisible();
        setBtnDeleteVisible(true);
        setBtnEditVisible(false);
        setBtnVolumeVisible(false);
        setLayoutExtraToolsVisible(true);
        mSelectedTL = TIMELINE_EXTRA;
    }

    private void startAnimationAddImage(String imagePath, int imageCoord[]) {
        mImagePath = imagePath;

        int[] indicatorCoord = new int[2];
        int[] layoutImageCoord = new int[2];
        mSeekbarIndicator.getLocationOnScreen(indicatorCoord);
        mLayoutImage.getLocationOnScreen(layoutImageCoord);

        int xDes = indicatorCoord[0] - imageCoord[0];
        int yDes = layoutImageCoord[1] - imageCoord[1];

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageShadowAnimation.getLayoutParams();
        params.leftMargin = imageCoord[0];
        params.topMargin = imageCoord[1];
        Glide.with(this).load(imagePath).into(mImageShadowAnimation);

        startAnimationAddFile(ADD_IMAGE, 500, xDes, yDes);
    }

    private void addExtraTLToTL(ExtraTL extraTL, int position) {
        if (checkAvailablePositionInLayoutImage(position)) {
            addExtraTLToLayoutImage(extraTL);
        } else {
            if (checkAvailablePositionInLayoutText(position)) {
                addExtraTLToLayoutText(extraTL);
            } else {
                addExtraTLToLayoutImage(extraTL);
            }
        }
    }

    private void addExtraTLToLayoutImage(ExtraTL extraTL) {
        mLayoutImage.addView(extraTL);
        int order = getOrderInLayoutImage(extraTL);
        mListInLayoutImage.add(order, extraTL);
        extraTL.inLayoutImage = true;
        reorganizeLayoutImage();
    }

    private void reorganizeLayoutImage() {
        for (int i = 0; i < mListInLayoutImage.size() - 1; i++) {
            ExtraTL currentTimeLine = mListInLayoutImage.get(i);
            ExtraTL nextTimeLine = mListInLayoutImage.get(i + 1);
            if (currentTimeLine.right > nextTimeLine.left) {
                nextTimeLine.moveTimeLine(currentTimeLine.right);
            }
        }
    }

    private int getOrderInLayoutImage(ExtraTL extraTL) {
        int order = 0;
        for (int i = 0; i < mListInLayoutImage.size(); i++) {
            ExtraTL timeline = mListInLayoutImage.get(i);
            if (extraTL.left < timeline.left) {
                break;
            } else {
                order = i + 1;
            }
        }
        return order;
    }

    private void addExtraTLToLayoutText(ExtraTL extraTL) {
        mLayoutText.addView(extraTL);
        int order = getOrderInLayoutText(extraTL);
        mListInLayoutText.add(order, extraTL);
        extraTL.inLayoutImage = false;
        reorganizeLayoutText();
    }

    private void reorganizeLayoutText() {
        for (int i = 0; i < mListInLayoutText.size() - 1; i++) {
            ExtraTL currentTimeLine = mListInLayoutText.get(i);
            ExtraTL nextTimeLine = mListInLayoutText.get(i + 1);
            if (currentTimeLine.right > nextTimeLine.left) {
                nextTimeLine.moveTimeLine(currentTimeLine.right);
            }
        }
    }

    private int getOrderInLayoutText(ExtraTL extraTL) {
        int order = 0;
        for (int i = 0; i < mListInLayoutText.size(); i++) {
            ExtraTL timeline = mListInLayoutText.get(i);
            if (extraTL.left < timeline.left) {
                break;
            } else {
                order = i + 1;
            }
        }
        return order;
    }

    private boolean checkAvailablePositionInLayoutImage(int position) {
        boolean isAvailable = true;
        for (int i = 0; i < mListInLayoutImage.size(); i++) {
            ExtraTL extraTL = mListInLayoutImage.get(i);
            if (position >= extraTL.left && position < extraTL.right) {
                isAvailable = false;
                break;
            }
        }
        return isAvailable;
    }

    private boolean checkAvailablePositionInLayoutText(int position) {
        boolean isAvailable = true;
        for (int i = 0; i < mListInLayoutText.size(); i++) {
            ExtraTL extraTL = mListInLayoutText.get(i);
            if (position >= extraTL.left && position < extraTL.right) {
                isAvailable = false;
                break;
            }
        }
        return isAvailable;
    }

    public void addText() {
        startAnimationAddText();
    }

    public void restoreAllTextTL(int projectId) {
        addWaterMark();
        ArrayList<TextObject> listText = mTextTable.getData(projectId);
        for (TextObject textObject : listText) {
            restoreTextTL(textObject);
        }
    }

    public void restoreTextTL(TextObject textObject) {
        String text = textObject.text;
        int leftMargin = Integer.parseInt(textObject.left);
        ExtraTL extraTL = new ExtraTL(this, text, mTimeLineImageHeight, leftMargin, false);
        extraTL.setOnClickListener(onExtraTimeLineClick);
        extraTL.setOnLongClickListener(onExtraTimelineLongClick);
        extraTL.restoreTextTL(textObject);

        mTextList.add(extraTL);
        if (extraTL.inLayoutImage) {
            mLayoutImage.addView(extraTL);
            mListInLayoutImage.add(extraTL.orderInLayout, extraTL);
        } else {
            mLayoutText.addView(extraTL);
            mListInLayoutText.add(extraTL.orderInLayout, extraTL);
        }

        FloatText floatText = new FloatText(this, text, false);
        mLayoutFloatView.addView(floatText);
        extraTL.floatText = floatText;
        floatText.timeline = extraTL;
        floatText.restoreState(textObject);
        floatText.drawBorder(false);
        floatText.setVisibility(View.GONE);
    }

    public void addWaterMark() {
        if (mIsVip) {
            return;
        }
        String text = "AZ Video Editor";
        int leftMargin = mLeftMarginTimeLine + mScrollView.getScrollX();
        ExtraTL extraTL = new ExtraTL(this, text, mTimeLineImageHeight, leftMargin, false);
        mTextList.add(extraTL);

        mWaterMark = new FloatText(this, text, true);
        mLayoutFloatView.addView(mWaterMark);
        extraTL.floatText = mWaterMark;
        mWaterMark.timeline = extraTL;

        int waterMarkX = mLayoutFloatView.getWidth() - mWaterMark.width - 40;
        int waterMarkY = mLayoutFloatView.getHeight() - mWaterMark.height - 40;
        mWaterMark.setWaterMarkPosition(waterMarkX, waterMarkY);
        setWaterMarkEndTime(mMaxTimeLineMs);
    }

    public void askDonate() {
        DialogConfirm.newInstance(this, DialogClickListener.ASK_DONATE)
                .show(getSupportFragmentManager().beginTransaction(), "ask donate");
    }

    public void removeWaterMark() {
        mIsVip = true;
        Utils.getSharedPref(this).edit().putBoolean(getString(R.string.pref_is_vip), true).apply();
        mLayoutFloatView.removeView(mWaterMark);
        mTextList.remove(mWaterMark.timeline);
        Toast.makeText(this, "Watermark was removed", Toast.LENGTH_LONG).show();
    }

    private void addTextTL() {
        String text = "Text here";
        int leftMargin = mLeftMarginTimeLine + mScrollView.getScrollX();
        ExtraTL extraTL = new ExtraTL(this, text, mTimeLineImageHeight, leftMargin, false);
        extraTL.setOnClickListener(onExtraTimeLineClick);
        extraTL.setOnLongClickListener(onExtraTimelineLongClick);
        addExtraTLToTL(extraTL, leftMargin);
        mTextList.add(extraTL);
        mSelectedExtraTL = extraTL;

        FloatText floatText = new FloatText(this, text, false);
        mLayoutFloatView.addView(floatText);
        extraTL.floatText = floatText;
        floatText.timeline = extraTL;
        setFloatImageVisible(null);
        setFloatTextVisible(extraTL);

        restoreExtraControl(extraTL);
        setExtraControlVisible(true);
        unSelectVideoTL();

        setBtnEditVisible(true);
        updateBtnExportVisible();
        setBtnDeleteVisible(true);
        setBtnVolumeVisible(false);
        setBtnEditIcon(R.drawable.ic_edit_text);
        slideExtraToolsIn(true);
        mSelectedTL = TIMELINE_EXTRA;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIabController.handleActivityResult(requestCode, resultCode, data);
    }

    private void startAnimationAddText() {
        int[] indicatorCoord = new int[2];
        int[] layoutImageCoord = new int[2];
        int[] textCoord = new int[2];
        mSeekbarIndicator.getLocationOnScreen(indicatorCoord);
        mLayoutImage.getLocationOnScreen(layoutImageCoord);
        mBtnAddText.getLocationOnScreen(textCoord);

        int xDes = indicatorCoord[0] - textCoord[0];
        int yDes = layoutImageCoord[1] - textCoord[1];

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageShadowAnimation.getLayoutParams();
        params.leftMargin = textCoord[0];
        params.topMargin = textCoord[1];
        Glide.with(this).load(R.drawable.ic_text).into(mImageShadowAnimation);

        startAnimationAddFile(ADD_TEXT, 500, xDes, yDes);
    }

    public void restoreExtraControl(ExtraTL extraTL) {
        mExtraTLControl.restoreTimeLineStatus(extraTL);
        reAddExtraControl();
        mSelectedExtraTL = extraTL;
    }

    private void reAddExtraControl() {
        ViewGroup parent = (ViewGroup) mExtraTLControl.getParent();
        if (parent != null) {
            parent.removeView(mExtraTLControl);
        }
        if (mExtraTLControl.inLayoutImage) {
            mTimeLineImage.addView(mExtraTLControl);
        } else {
            mTimeLineText.addView(mExtraTLControl);
        }
    }

    public void addAudio(String audioPath, int[] audioCoord) {
        mAudioPath = audioPath;
        startAnimationAddAudio(audioCoord);
    }

    public void getAudioOrder() {
        for (int i = 0; i < mAudioList.size(); i++) {
            AudioTL audioTL = mAudioList.get(i);
            audioTL.orderInList = i;
        }
    }

    private boolean checkIfFileNotExists() {
        for (VideoTL videoTL : mVideoList) {
            if (!videoTL.isExists) {
                return true;
            }
        }

        for (ExtraTL extraTL : mImageList) {
            if (!extraTL.isExists) {
                return true;
            }
        }

        for (AudioTL audioTL : mAudioList) {
            if (!audioTL.isExists) {

                return true;
            }
        }

        return false;
    }

    public void saveAudioObjects() {
        getAudioOrder();
        for (AudioTL audioTL : mAudioList) {
            AudioObject audio = audioTL.getAudioObject();
            mAudioTable.insertValue(audio);
        }
    }

    public void restoreAllAudioTL(int projectId) {
        ArrayList<AudioObject> list = mAudioTable.getData(projectId);
        for (AudioObject audio : list) {
            restoreAudioTL(audio);
        }
    }

    public void restoreAudioTL(AudioObject audio) {
        int leftMargin = Integer.parseInt(audio.left);
        AudioTL audioTL = new AudioTL(this, audio.path, mTimeLineImageHeight, leftMargin);
        audioTL.setOnClickListener(onAudioTimeLineClick);
        audioTL.setOnLongClickListener(onAudioLongClick);
        audioTL.restoreAudioObject(audio);
        mLayoutAudio.addView(audioTL);
        int order = Integer.parseInt(audio.orderInList);
        mAudioList.add(order, audioTL);
    }

    public void addAudioTL() {
        int leftMargin = mLeftMarginTimeLine + mScrollView.getScrollX();
        AudioTL audioTL = new AudioTL(this, mAudioPath, mTimeLineImageHeight, leftMargin);
        addAudioTLToTL(audioTL);
        audioTL.setOnClickListener(onAudioTimeLineClick);
        audioTL.setOnLongClickListener(onAudioLongClick);
        mSelectedAudioTL = audioTL;

        updateBtnExportVisible();
        setLayoutExtraToolsVisible(true);
        mSelectedTL = TIMELINE_AUDIO;
    }

    private void startAnimationAddAudio(int[] audioCoord) {
        int[] indicatorCoord = new int[2];
        int[] layoutImageCoord = new int[2];
        mSeekbarIndicator.getLocationOnScreen(indicatorCoord);
        mLayoutAudio.getLocationOnScreen(layoutImageCoord);

        int xDes = indicatorCoord[0] - audioCoord[0];
        int yDes = layoutImageCoord[1] - audioCoord[1];

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageShadowAnimation.getLayoutParams();
        params.leftMargin = audioCoord[0];
        params.topMargin = audioCoord[1];
        Glide.with(this).load(R.drawable.ic_music).into(mImageShadowAnimation);

        startAnimationAddFile(ADD_AUDIO, 500, xDes, yDes);
    }

    private void addAudioTLToTL(AudioTL audioTL) {
        mLayoutAudio.addView(audioTL, audioTL.params);
        int order = getOrderInLayoutAudio(audioTL);
        mAudioList.add(order, audioTL);
        reorganizeLayoutAudio();
    }

    private void reorganizeLayoutAudio() {
        for (int i = 0; i < mAudioList.size() - 1; i++) {
            AudioTL currentTimeLine = mAudioList.get(i);
            AudioTL nextTimeLine = mAudioList.get(i + 1);
            if (currentTimeLine.right > nextTimeLine.left) {
                nextTimeLine.moveTimeLine(currentTimeLine.right);
            }
        }
    }

    private int getOrderInLayoutAudio(AudioTL audioTL) {
        int order = 0;
        for (int i = 0; i < mAudioList.size(); i++) {
            AudioTL timeline = mAudioList.get(i);
            if (audioTL.left < timeline.left) {
                break;
            } else {
                order = i + 1;
            }
        }
        return order;
    }

    private void stopMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void changeAudio(AudioTL audio, int startTime) {
        stopMediaPlayer();
        if (!audio.isExists) {
            return;
        }
        mMediaPlayer = MediaPlayer.create(this, Uri.parse(audio.audioPath));
        mMediaPlayer.seekTo(startTime);
        updateSystemVolume();
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

    private void openLayoutAdd(boolean open) {
        int visible = open ? View.VISIBLE : View.GONE;
        mLayoutAdd.setVisibility(visible);
        mOpenLayoutAdd = open;
    }

    View.OnClickListener onBtnAddClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mOpenFileManager) {
                openFileManager(false);
                return;
            }
            if (mOpenLayoutAdd) {
                openLayoutAdd(false);
            } else {
                slideLayoutAddOut();
                setLayoutExtraToolsVisible(false);
                hideAllFloatNControlers();
                pausePreview();
            }
        }
    };

    private void slideLayoutAddOut() {
        openLayoutAdd(true);
        int distance = Utils.dpToPixel(this, 60);
        TranslateAnimation animation = new TranslateAnimation(-distance, 0, 0, 0);
        animation.setDuration(0);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        mLayoutAdd.startAnimation(animation);
    }

    private void openFileManager(boolean open) {
        if (open) {
            mFileManager.setVisibility(View.VISIBLE);
            mOpenFileManager = true;
            mBtnAdd.setImageResource(R.drawable.ic_close);
        } else {
            mFileManager.setVisibility(View.GONE);
            mOpenFileManager = false;
            mBtnAdd.setImageResource(R.drawable.ic_add_media);
            setBtnBackVisible(true);
            updateBtnExportVisible();
        }
    }

    View.OnClickListener onBtnBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackClick();
        }
    };

    private boolean upLevelFileManager() {
        switch (mFragmentCode) {
            case VIDEO_TAB:
                if (mOpenVideoSubFolder) {
                    mFragmentVideosGallery.backToMain();
                    mOpenVideoSubFolder = false;
                    setBtnUpLevelVisible(false);
                    return true;
                }
                break;
            case IMAGE_TAB:
                if (mOpenImageSubFolder) {
                    mFragmentImagesGallery.backToMain();
                    mOpenImageSubFolder = false;
                    setBtnUpLevelVisible(false);
                    return true;
                }
                break;
            case AUDIO_TAB:
                if (mOpenAudioSubFolder) {
                    mFragmentAudioGallery.backToMain();
                    mOpenAudioSubFolder = false;
                    setBtnUpLevelVisible(false);
                    return true;
                }
                break;
        }

        if (mOpenFileManager) {
            openFileManager(false);
            return true;
        }
        return false;
    }

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

    @Override
    protected void onStop() {
        super.onStop();
        new SaveProjectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setHightLighTab(int tab) {
        int videoTabId = tab == VIDEO_TAB ? R.drawable.ic_video_tab_blue : R.drawable.ic_video_tab;
        int imageTabId = tab == IMAGE_TAB ? R.drawable.ic_image_tab_blue : R.drawable.ic_image_tab;
        int audioTabId = tab == AUDIO_TAB ? R.drawable.ic_audio_tab_blue : R.drawable.ic_audio_tab;
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
            mSelectedVideoTL = (VideoTL) view;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
            mDragCode = DRAG_VIDEO;
            view.startDrag(clipData, shadowBuilder, view, 0);
            addShadowToLayoutVideo();
            mLayoutScrollView.setOnDragListener(onVideoDragListener);
            invisibleAllController();
            return false;
        }
    };

    private void addShadowToLayoutVideo() {
        mTLShadowParams.width = mSelectedVideoTL.width;
        mTLShadowParams.height = mSelectedVideoTL.height;
        ViewGroup parent = (ViewGroup) mTLShadow.getParent();
        if (parent != null) {
            parent.removeView(mTLShadow);
        }
        mLayoutVideo.addView(mTLShadow);
        mShadowIndicatorParams.height = mTimeLineVideoHeight;
        parent = (ViewGroup) mShadowIndicator.getParent();
        if (parent != null) {
            parent.removeView(mShadowIndicator);
        }
        mLayoutVideo.addView(mShadowIndicator);
    }

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
                VideoTL videoTL = mVideoList.get(i);
                if (x >= videoTL.left && x <= videoTL.right) {
                    mShadowIndicator.setVisibility(View.VISIBLE);
                    mShadowIndicatorParams.leftMargin = videoTL.left;
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
            mTLShadowParams.leftMargin = finalMargin;
            mTLShadow.setLayoutParams(mTLShadowParams);

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    VideoTL changeTimeLine = mVideoList.get(changePosition);
                    if (!changeTimeLine.equals(mSelectedVideoTL)) {
                        mSelectedVideoTL.setLeftMargin(changeTimeLine.left);
                        mVideoList.remove(mSelectedVideoTL);
                        mVideoList.add(changePosition, mSelectedVideoTL);
                        getLeftMargin(mCountVideo - 1);
                        mScrollView.scrollTo(mSelectedVideoTL.startInTimeLineMs / Constants.SCALE_VALUE, 0);
                        if (mSelectedVideoTL.isExists) {
                            mActiveVideoView.setVideoPath(mSelectedVideoTL.videoPath);
                        }
                        mCurrentVideoTL = mSelectedVideoTL;
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    mLayoutVideo.removeView(mTLShadow);
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
            mTLShadowParams.leftMargin = finalMargin;
            mTLShadow.setLayoutParams(mTLShadowParams);

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    mSelectedAudioTL.moveTimeLine(finalMargin);
                    int order = 0;
                    for (int i = 0; i < mAudioList.size(); i++) {
                        AudioTL audioTL = mAudioList.get(i);
                        if (finalMargin < audioTL.right - audioTL.width / 2) {
                            break;
                        } else {
                            order = i + 1;
                        }
                    }
                    mAudioList.remove(mSelectedAudioTL);
                    mAudioList.add(order, mSelectedAudioTL);
                    reorganizeLayoutAudio();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    mLayoutAudio.removeView(mTLShadow);
                    break;
            }
            return true;
        }
    };

    View.OnLongClickListener onAudioLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            mSelectedAudioTL = (AudioTL) view;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();

            view.startDrag(clipData, shadowBuilder, view, 0);

            mDragCode = DRAG_AUDIO;
            addShadowToLayoutAudio();
            mLayoutScrollView.setOnDragListener(onAudioDragListener);
            invisibleAllController();
            return false;
        }
    };

    private void invisibleAllController() {
        invisibleExtraControl();
        invisibleVideoControl();
        invisibleAudioControl();
    }

    private void addShadowToLayoutAudio() {
        mTLShadowParams.width = mSelectedAudioTL.width;
        mTLShadowParams.height = mSelectedAudioTL.height;
        ViewGroup parent = (ViewGroup) mTLShadow.getParent();
        if (parent != null) {
            parent.removeView(mTLShadow);
        }
        mLayoutAudio.addView(mTLShadow);
    }

    View.OnClickListener onAudioTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSelectedAudioTL = (AudioTL) view;
            setAudioControlVisible(true);
            openLayoutAdd(false);
            slideExtraToolsIn(true);
            mAudioTLControl.restoreTimeLineStatus(mSelectedAudioTL);
            mSelectedTL = TIMELINE_AUDIO;
            setBtnDeleteVisible(true);
            setBtnVolumeVisible(true);
            setBtnEditVisible(false);
            setFloatImageVisible(null);
            setFloatTextVisible(null);
            unhighlightVideoTL();
            pausePreview();
        }
    };

    View.OnLongClickListener onExtraTimelineLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            mSelectedExtraTL = (ExtraTL) view;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();

            view.startDrag(clipData, shadowBuilder, view, 0);
            mTLShadowParams.width = mSelectedExtraTL.width;
            mTLShadowParams.height = mSelectedExtraTL.height;
            mShadowIndicatorParams.height = mSelectedExtraTL.height;
            mDragCode = DRAG_EXTRA;
            invisibleAllController();
            return false;
        }
    };

    private boolean shadowInLayout(View layout) {
        if (mTLShadow.getParent() == null) {
            return false;
        }
        return mTLShadow.getParent().equals(layout);
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
                mTLShadowParams.leftMargin = finalMargin;
                mTLShadow.setLayoutParams(mTLShadowParams);
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
                        mTimeLineText.removeView(mTLShadow);
                    }
                    mTimeLineImage.addView(mTLShadow);
                }
            } else {
                if (!shadowInLayout(mTimeLineText)) {
                    if (shadowInLayout(mTimeLineImage)) {
                        mTimeLineImage.removeView(mTLShadow);
                    }
                    mTimeLineText.addView(mTLShadow);
                }
            }

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    mSelectedExtraTL.moveTimeLine(finalMargin);
                    ViewGroup parent = (ViewGroup) mSelectedExtraTL.getParent();

                    if (parent != null) {
                        parent.removeView(mSelectedExtraTL);
                    }
                    if (inLayoutImage) {
                        int order = 0;
                        for (int i = 0; i < mListInLayoutImage.size(); i++) {
                            ExtraTL extraTL = mListInLayoutImage.get(i);
                            if (finalMargin < extraTL.right - extraTL.width / 2) {
                                break;
                            } else {
                                order = i + 1;
                            }
                        }
                        if (mSelectedExtraTL.inLayoutImage) {
                            mListInLayoutImage.remove(mSelectedExtraTL);
                        } else {
                            mListInLayoutText.remove(mSelectedExtraTL);
                        }
                        mListInLayoutImage.add(order, mSelectedExtraTL);
                        mLayoutImage.addView(mSelectedExtraTL);
                        reorganizeLayoutImage();
                        mSelectedExtraTL.inLayoutImage = true;
                    } else {
                        int order = 0;
                        for (int i = 0; i < mListInLayoutText.size(); i++) {
                            ExtraTL extraTL = mListInLayoutText.get(i);
                            if (finalMargin < extraTL.right - extraTL.width / 2) {
                                break;
                            } else {
                                order = i + 1;
                            }
                        }
                        if (mSelectedExtraTL.inLayoutImage) {
                            mListInLayoutImage.remove(mSelectedExtraTL);
                        } else {
                            mListInLayoutText.remove(mSelectedExtraTL);
                        }
                        mListInLayoutText.add(order, mSelectedExtraTL);
                        mLayoutText.addView(mSelectedExtraTL);
                        reorganizeLayoutText();
                        mSelectedExtraTL.inLayoutImage = false;
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (shadowInLayout(mTimeLineImage)) {
                        mTimeLineImage.removeView(mTLShadow);
                    }
                    if (shadowInLayout(mTimeLineText)) {
                        mTimeLineText.removeView(mTLShadow);
                    }
                    break;
            }
            return true;
        }
    };

    private void closeLayoutExport() {
        mExportFragment.backToEdit();
    }

    @Override
    public void onBackPressed() {
        hideStatusBar();
        if (onBackClick()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean onBackClick() {
        if (mExportFragment.mExporting) {
            return true;
        }

        if (mOpenLayoutExport) {
            closeLayoutExport();
            return true;
        }

        if (mOpenLayoutTrimVideo) {
            closeLayoutTrimVideo();
            return true;
        }

        if (mOpenLayoutAdd) {
            openLayoutAdd(false);
            return true;
        }

        if (mOpenLayoutEditText) {
            openLayoutEditText(false);
            return true;
        }
        if (upLevelFileManager()) {
            return true;
        }

        if (mSelectedTL != UNSELECT ) {
            hideAllFloatNControlers();
            return true;
        }

        if (!mOpenLayoutProject) {
            pausePreview();
            saveProject();
            openLayoutProject();
            return true;
        }
        return false;
    }

    View.OnClickListener onExtraTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            pausePreview();
            mSelectedExtraTL = (ExtraTL) view;
            setExtraControlVisible(true);
            openLayoutAdd(false);
            slideExtraToolsIn(true);
            mExtraTLControl.restoreTimeLineStatus(mSelectedExtraTL);
            reAddExtraControl();
            scrollTo(mSelectedExtraTL.startInTimeLineMs);
            onCustomScrollChanged.onScrollChanged();

            mSelectedTL = TIMELINE_EXTRA;
            if (mSelectedExtraTL.isImage) {
                setFloatImageVisible(mSelectedExtraTL);
                setFloatTextVisible(null);
                setBtnEditVisible(false);
            } else {
                setFloatImageVisible(null);
                setFloatTextVisible(mSelectedExtraTL);
                setBtnEditVisible(true);
                setBtnEditIcon(R.drawable.ic_edit_text);
            }
            setBtnDeleteVisible(true);
            setBtnVolumeVisible(false);
            unhighlightVideoTL();
        }
    };

    public void cancelEditText() {
        if (mOpenLayoutEditText) {
            openLayoutEditText(false);
        }
    }

    private void updateBtnExportVisible() {
        if (mCountVideo > 1) {
            setBtnExportVisible(true);
        } else if (mCountVideo == 1) {
            if (mImageList.size() > 0 || mTextList.size() > 0
                    || mAudioList.size() > 0) {
                setBtnExportVisible(true);
            } else {
                setBtnExportVisible(false);
            }
        } else {
            setBtnExportVisible(false);
        }
    }

    public void setFloatTextVisible(ExtraTL extraTL) {
        for (int i = 0; i < mTextList.size(); i++) {
            FloatText floatText = mTextList.get(i).floatText;
            if (mTextList.get(i).equals(extraTL)) {
                floatText.drawBorder(true);
            } else {
                floatText.drawBorder(false);
            }
        }
    }

    public void setFloatTextVisible(float x, float y) {
        mFoundText = false;
        unhighlightVideoTL();
        for (int i = 0; i < mTextList.size(); i++) {
            FloatText floatText = mTextList.get(i).floatText;
            if (floatText.getVisibility() == View.VISIBLE) {
                if (x >= floatText.xMin && x <= floatText.xMax
                        && y >= floatText.yMin && y <= floatText.yMax) {
                    floatText.drawBorder(true);
                    mSelectedExtraTL = mTextList.get(i);
                    mFoundText = true;
                    updateLayoutEditText();
                } else {
                    floatText.drawBorder(false);
                }
            }
        }
        if (!mFoundText) {
            if (!mFoundImage) {
                setExtraControlVisible(false);
                cancelEditText();
                slideExtraToolsIn(false);
            }
        } else {
            FloatText floatText = mSelectedExtraTL.floatText;
            if (floatText != null && floatText.isWaterMark) {
                mExtraTLControl.setVisibility(View.GONE);
                slideExtraToolsIn(false);
                return;
            }
            setExtraControlVisible(true);
            restoreExtraControl(mSelectedExtraTL);
            setBtnDeleteVisible(true);
            setBtnEditVisible(true);
            setBtnEditIcon(R.drawable.ic_edit_text);
            setBtnVolumeVisible(false);
            slideExtraToolsIn(true);
        }
    }

    public void unhighlightVideoTL() {
        if (mSelectedVideoTL != null) {
            mSelectedVideoTL.setNormalTL();
        }
    }

    public void setFloatImageVisible(ExtraTL extraTL) {
        for (int i = 0; i < mImageList.size(); i++) {
            FloatImage floatImage = mImageList.get(i).floatImage;
            if (mImageList.get(i).equals(extraTL)) {
                floatImage.drawBorder(true);
            } else {
                floatImage.drawBorder(false);
            }
        }
    }

    public void setFloatImageVisible(float x, float y) {
        mFoundImage = false;
        for (int i = 0; i < mImageList.size(); i++) {
            FloatImage floatImage = mImageList.get(i).floatImage;
            if (floatImage.getVisibility() == View.VISIBLE) {
                if (x >= floatImage.xMin && x <= floatImage.xMax
                        && y >= floatImage.yMin && y <= floatImage.yMax) {
                    floatImage.drawBorder(true);
                    mSelectedExtraTL = mImageList.get(i);
                    mFoundImage = true;
                } else {
                    floatImage.drawBorder(false);
                }
            }
        }
        if (!mFoundImage) {
            if (!mFoundText) {
                setExtraControlVisible(false);
                slideExtraToolsIn(false);
            }
        } else {
            setExtraControlVisible(true);
            restoreExtraControl(mSelectedExtraTL);
            setBtnDeleteVisible(true);
            setBtnEditVisible(false);
            setBtnVolumeVisible(false);
            slideExtraToolsIn(true);
        }
        unhighlightVideoTL();
    }

    public void setExtraControlVisible(boolean visible) {
        if (visible) {
            mExtraTLControl.setVisibility(View.VISIBLE);
            mVideoTLControl.setVisibility(View.GONE);
            mAudioTLControl.setVisibility(View.GONE);
            mSelectedTL = TIMELINE_EXTRA;
        } else {
            mExtraTLControl.setVisibility(View.GONE);
            if (mSelectedExtraTL != null) {
                if (mSelectedExtraTL.isImage) {
                    mSelectedExtraTL.floatImage.drawBorder(false);
                } else {
                    mSelectedExtraTL.floatText.drawBorder(false);
                }
            }
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    View.OnClickListener onVideoTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSelectedVideoTL = (VideoTL) view;

            if (mSelectedVideoTL.isHighLight) {
                unSelectVideoTL();
                log("unselected");
            } else {
                log("selected");
                if (!mSelectedVideoTL.isExists) {
                    highlightSelectedVideoTL();
                    setBtnDeleteVisible(true);
                    return;
                }
                selectVideoTL();
                setExtraControlVisible(false);
                setAudioControlVisible(false);
                openLayoutAdd(false);
                pausePreview();
            }
        }
    };

    private void selectVideoTL() {
        mSelectedTL = TIMELINE_VIDEO;
        highlightSelectedVideoTL();
//        if (!mSelectedVideoTL.equals(mCurrentVideoTL)) {
//            mScrollView.scrollTo(mSelectedVideoTL.startInTimeLineMs / Constants.SCALE_VALUE, 0);
//            onCustomScrollChanged.onScrollChanged();
//        }
        setBtnDeleteVisible(true);
        setBtnVolumeVisible(true);
        setBtnEditVisible(true);
        setBtnEditIcon(R.drawable.ic_cut_video);
        setFloatTextVisible(null);
        setFloatImageVisible(null);
        slideExtraToolsIn(true);
    }

    private void highlightSelectedVideoTL() {
        for (int i = 0; i < mVideoList.size(); i++) {
            VideoTL videoTL = mVideoList.get(i);
            if (videoTL.equals(mSelectedVideoTL)) {
                videoTL.highlightTL();
            } else {
                videoTL.setNormalTL();
            }
        }
    }

    private void unSelectVideoTL() {
        mSelectedTL = UNSELECT;
        slideExtraToolsIn(false);
        if (mSelectedVideoTL == null) {
            return;
        }
        mSelectedVideoTL.setNormalTL();
        mSelectedVideoTL = null;
    }

    private void setBtnExportVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnExport.setVisibility(visibility);
    }

    public void setBtnDeleteVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnDelete.setVisibility(visibility);
    }

    //spare
    @Override
    public void updateVideoTimeLine(int leftPosition, int width) {
        mSelectedVideoTL.drawTimeLine(leftPosition, width);
        getLeftMargin(mCountVideo - 1);
        mMaxTimeLineMs = mVideoList.get(mCountVideo - 1).endInTimeLineMs;
    }

    private void log(String msg) {
        Log.e("Edit video", msg);
    }

    @Override
    public void invisibleVideoControl() {

    }

    private int getLeftMargin(int position) {
        VideoTL timeLine = mVideoList.get(position);
        int leftMargin;
        if (position == 0) {
            leftMargin = mLeftMarginTimeLine;
        } else {
            VideoTL videoTL = mVideoList.get(position - 1);
            leftMargin = videoTL.width + getLeftMargin(position - 1);
        }
        timeLine.setLeftMargin(leftMargin);
        return leftMargin;
    }

    public void setBtnEditIcon(int icon) {
        mBtnEditText.setImageResource(icon);
    }

    private void setVideoRatio() {
        ViewGroup.LayoutParams params = mVideoViewLayout.getLayoutParams();
        int height = (int) (Utils.getScreenHeight() * 0.6);
        params.height = height;
        params.width = (int) (params.height * 1.77);
        mVideoViewLayout.setLayoutParams(params);
        params = mTopLayout.getLayoutParams();
        params.height = height;
        mTopLayout.setLayoutParams(params);
    }

    public float getLayoutVideoScale(float realHeight) {
        return realHeight / (float) mVideoViewLayout.getHeight();
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("resume");
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
        mSelectedExtraTL.drawTimeLine(left, right);
    }

    @Override
    public void invisibleExtraControl() {
        setExtraControlVisible(false);
        slideExtraToolsIn(false);
        if (mSelectedExtraTL == null) {
            return;
        }
        if (!mSelectedExtraTL.isImage) {
            cancelEditText();
        }
    }

    @Override
    public void updateAudioTimeLine(int start, int end) {
        mSelectedAudioTL.seekTimeLine(start, end);
    }

    @Override
    public void invisibleAudioControl() {
        setAudioControlVisible(false);
        slideExtraToolsIn(false);
    }

    private void setVideoControlVisible(boolean visible) {
        visible = false;
        if (visible) {
            mVideoTLControl.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
            mExtraTLControl.setVisibility(View.GONE);
            mAudioTLControl.setVisibility(View.GONE);
        } else {
            mVideoTLControl.setVisibility(View.GONE);
            mScrollView.scroll = true;
        }
    }

    private void setAudioControlVisible(boolean visible) {
        if (visible) {
            mAudioTLControl.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
            mVideoTLControl.setVisibility(View.GONE);
            mExtraTLControl.setVisibility(View.GONE);
        } else {
            mAudioTLControl.setVisibility(View.GONE);
            mScrollView.scroll = true;
        }
    }

    private void addVideoControler() {
        mVideoTLControl = new VideoTLControl(this, 500, mTimeLineVideoHeight, mLeftMarginTimeLine);
        mTimeLineVideo.addView(mVideoTLControl, mVideoTLControl.params);
        setVideoControlVisible(false);
    }

    private void addExtraNAudioController() {
        mExtraTLControl = new ExtraTLControl(this, mLeftMarginTimeLine, 500, mTimeLineImageHeight);
        mTimeLineImage.addView(mExtraTLControl);
        mExtraTLControl.inLayoutImage = true;
        setExtraControlVisible(false);

        mAudioTLControl = new AudioTLControl(this, mLeftMarginTimeLine, mLeftMarginTimeLine + 500, mTimeLineImageHeight);
        mTimeLineAudio.addView(mAudioTLControl);
        setAudioControlVisible(false);
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
        LinearLayout.LayoutParams seperateParams = (LinearLayout.LayoutParams) mSeperateLineVideo.getLayoutParams();
        seperateParams.leftMargin = mLeftMarginTimeLine;
        seperateParams = (LinearLayout.LayoutParams) mSeperateLineImage.getLayoutParams();
        seperateParams.leftMargin = mLeftMarginTimeLine;
        seperateParams = (LinearLayout.LayoutParams) mSeperateLineText.getLayoutParams();
        seperateParams.leftMargin = mLeftMarginTimeLine;
    }
}
