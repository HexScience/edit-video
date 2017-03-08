package com.hecorat.azplugin2.main;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
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
import com.hecorat.azplugin2.database.ProjectObject;
import com.hecorat.azplugin2.database.ProjectTable;
import com.hecorat.azplugin2.database.TextObject;
import com.hecorat.azplugin2.database.TextTable;
import com.hecorat.azplugin2.database.VideoObject;
import com.hecorat.azplugin2.database.VideoTable;
import com.hecorat.azplugin2.dialogfragment.DialogConfirm;
import com.hecorat.azplugin2.export.ExportFragment;
import com.hecorat.azplugin2.filemanager.FragmentAudioGallery;
import com.hecorat.azplugin2.filemanager.FragmentImagesGallery;
import com.hecorat.azplugin2.filemanager.FragmentVideosGallery;
import com.hecorat.azplugin2.filemanager.GalleryState;
import com.hecorat.azplugin2.helper.AnalyticsHelper;
import com.hecorat.azplugin2.helper.NameDialog;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.interfaces.DialogClickListener;
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
import com.hecorat.azplugin2.video.FragmentCrop;
import com.hecorat.azplugin2.video.TrimFragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.hecorat.azplugin2.main.Constants.DEFAULT_PROJECT_NAME;

public class MainActivity extends AppCompatActivity implements VideoTLControl.OnControlTimeLineChanged,
        ExtraTLControl.OnExtraTimeLineControlChanged, AudioTLControl.OnAudioControlTimeLineChanged,
        ColorPickerView.OnColorChangedListener, DialogClickListener, ProjectListDialog.Callback,
        NameDialog.DialogClickListener {
    private static final int TIMELINE_VIDEO = 0;
    private static final int TIMELINE_EXTRA = 1;
    private static final int TIMELINE_AUDIO = 2;
    private static final int UNSELECT = 3;
    private static final int DRAG_VIDEO = 0;
    private static final int DRAG_EXTRA = 1;
    private static final int DRAG_AUDIO = 2;
    private static final int VIDEO_TAB = 0;
    private static final int IMAGE_TAB = 1;
    private static final int AUDIO_TAB = 2;
    private static final int ACCEPT_TAB = 3;
    private static final int MSG_CURRENT_POSITION = 0;
    private static final int BEGIN = 0;
    private static final int PLAY = 1;
    private static final int PAUSE = 2;
    private static final int END = 3;
    private static final int UPDATE_STATUS_PERIOD = 200;
    private static final int ADD_VIDEO = 0;
    private static final int ADD_AUDIO = 1;
    private static final int ADD_IMAGE = 2;
    private static final int ADD_TEXT = 3;
    private static final int LAYOUT_ANIMATION_DURATION = 100;
    private static final String RECENT_PROJECT_LIST_FG = "recent_project_list_fg";

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
    private ImageView mBtnBack, mBtnAdd, mBtnUndo, mBtnExport, mBtnPlay;
    private LinearLayout mFileManager;
    private ImageView mVideoTab, mImageTab, mAudioTab;
    private LinearLayout mVideoTabLayout, mImageTabLayout, mAudioTabLayout, mAcceptTabLayout;
    public CustomVideoView mActiveVideoView, mInActiveVideoView, mVideoView1, mVideoView2;
    private RelativeLayout mLayoutTimeLine;
    private View mLimitTimeLineVideo, mLimitTimeLineImage, mLimitTimeLineText,
            mLimitTimeLineAudio, mSeperateLineVideo, mSeperateLineImage, mSeperateLineText;
    private RelativeLayout mLayoutAdd;
    private TextView mBtnAddVideo, mBtnAddImage, mBtnAddAudio, mBtnAddText, mBtnDelete, mBtnEdit, mBtnVolume,
            mBtnCrop, mBtnRecentProject, mBtnNewProject, mBtnTrim;
    private LinearLayout mLayoutEditText;
    private EditText mEditText, mEdtColorHex;
    private Spinner mFontSpinner;
    private ImageView mBtnTextColor, mBtnTextBgrColor;
    private RelativeLayout mLayoutColorPicker;
    private RelativeLayout mLayoutBtnTextColor, mLayoutBtnTextBgrColor;
    private Button mBtnCloseColorPicker;
    private ImageView mIndicatorTextColor, mIndicatorTextBgr;
    private RelativeLayout mLayoutTimeMark;
    private RelativeLayout mTopLayout, mMainLayout;
    private View mSeekbarIndicator;
    private RelativeLayout mLayoutAnimationAddFile;
    private ImageView mImageShadowAnimation;
    private FrameLayout mLayoutFragment;
    private RelativeLayout mLayoutFloatView;
    private ImageView mBtnUpLevel;
    private RelativeLayout mLayoutExtraTools;
    private TextView mBtnReport;
    private ImageView mBtnSetting;
    private TextView mBtnUpgrade;
    private RelativeLayout mLayoutSetting;
    private ImageView mBtnExportGif;
    private FrameLayout mBtnTrimContainer;

    private MainActivity mActivity;
    private FontAdapter mFontAdapter;
    private MediaPlayer mMediaPlayer;
    private VideoTL mSelectedVideoTL, mCurrentVideoTL;
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
    private FragmentCrop mFragmentCrop;

    private AudioManager mAudioManager;
    private Thread mThreadPreviewVideo;
    public ArrayList<String> mFontPath;
    public ArrayList<VideoTL> mVideoList = new ArrayList<>();
    public ArrayList<ExtraTL> mImageList = new ArrayList<>();
    public ArrayList<ExtraTL> mTextList = new ArrayList<>();
    public ArrayList<AudioTL> mAudioList = new ArrayList<>();
    private ArrayList<ExtraTL> mListInLayoutImage = new ArrayList<>();
    private ArrayList<ExtraTL> mListInLayoutText = new ArrayList<>();
    private ArrayList<ExtraTL> mTempListLayoutImage = new ArrayList<>();
    private ArrayList<ExtraTL> mTempListLayoutText = new ArrayList<>();

    public String mProjectName;
    public String mVideoPath, mImagePath, mAudioPath;
    public String mOutputDirectory;

    private int mDragCode = DRAG_VIDEO;
    private int mCountVideo = 0;
    private int mTimeLineVideoHeight = 150;
    private int mTimeLineImageHeight = 70;
    private int mFragmentCode;
    private boolean mOpenFileManager;
    public boolean mOpenImageSubFolder;
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
    public boolean mIsVip;
    private boolean mOpenExtraTools;
    public boolean mOpenLayoutTrimVideo;
    public boolean mOpenLayoutExport;
    private boolean mFirstAnchor;
    private int mDragAnchor;
    private int mVideoViewHeight;
    private boolean mOpenLayoutSetting;
    public boolean mUseSdCard;
    private int mInitProjectId;
    private boolean mOpenFromDialog;

    private ProgressHandler mHandler;
    private CustomScrollChanged mScrollChangedListener = new CustomScrollChanged();
    private Handler mTimerTaskHandler = new Handler();
    private BroadcastReceiver mReceiverCopyCompleted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mExportFragment.onBroadcastReceived(intent, Constants.ACTION_COPY_COMPLETED);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setViewsListener();
        getVideoPathFromAzRecorder();
        mColorPicker.setAlphaSliderVisible(true);
        mColorPicker.setOnColorChangedListener(this);
        mColorPicker.setAlphaSliderText(R.string.color_picker_opacity_title);
        mActivity = this;
        setVideoRatio();

        mCurrentVideoId = -1;
        mMaxTimeLineMs = 0;
        mPreviewStatus = BEGIN;
        mThreadPreviewVideo = new Thread(runnablePreview);
        mHandler = new ProgressHandler(this);
        mScroll = true;

        initFontManager();

        mMediaPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        saveSystemVolume();

        prepareLayoutAnimationAddFile();
        initDatabase();
        keepScreenOn();
        initVideoView();
        setFullscreen();
        checkVip();

        registerReceiver(mReceiverCopyCompleted, new IntentFilter(Constants.ACTION_COPY_COMPLETED));
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        new SaveProjectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onDestroy() {
        stopThreadPreview();
        stopMediaPlayer();
        Utils.deleteTempFiles();
        unregisterReceiver(mReceiverCopyCompleted);
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) setFullscreen();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        sendBroadcast(new Intent("dismiss_waiting_dialog"));
        findViewById(R.id.layout_waiting).setVisibility(View.GONE);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            saveSystemVolume();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        AnalyticsHelper.getInstance().send(mActivity, Constants.CATEGORY_CLICK_BACK,
                Constants.ACTION_CLICK_NAVIGATION_BACK);
        onBackClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_PURCHASE:
                log("REQUEST_CODE_PURCHASE");
                if (resultCode == Activity.RESULT_OK) {
                    boolean purchaseResult = data.getBooleanExtra("purchase_result", false);
                    log("purchaseResult = " + purchaseResult);
                    if (purchaseResult) {
                        removeWaterMark();
                    }
                }
                break;
        }
    }

    @Override
    public void onPositiveClick(String name, int type) {
        switch (type) {
            case NameDialog.SAVE_PROJECT:
                mProjectTable.updateValue(mProjectId, ProjectTable.PROJECT_NAME, name);
                new SaveProjectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                backToAzRecorderApp();
                break;
            case NameDialog.CREATE_PROJECT:
                saveProject(mProjectId);
                mProjectId = (int) mProjectTable.insertValue(name,
                        System.currentTimeMillis() + "");
                mProjectName = name;
                resetActivity();
                resetTempListLayout();
                addWaterMark();
                break;
        }
    }

    @Override
    public void onNegativeClick() {
    }

    @Override
    public void onPositiveClick(int dialogId, String detail) {
        switch (dialogId) {
            case DialogClickListener.ASK_DONATE:
                startDonate(detail);
                break;
            case DialogClickListener.DELETE_VIDEO:
                deleteVideo();
                updateBtnExportVisible();
                break;
            case DialogClickListener.DELETE_IMAGE:
                deleteExtraTimeline();
                updateBtnExportVisible();
                break;
            case DialogClickListener.DELETE_TEXT:
                deleteExtraTimeline();
                updateBtnExportVisible();
                break;
            case DialogClickListener.DELETE_AUDIO:
                deleteAudioTimeLine();
                updateBtnExportVisible();
                break;
            case DialogClickListener.SAVE_PROJECT:
                if (mProjectId != mInitProjectId) {
                    new SaveProjectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    backToAzRecorderApp();
                    return;
                }
                NameDialog.newInstance(mActivity, mActivity, NameDialog.SAVE_PROJECT, mProjectName)
                        .show(getSupportFragmentManager(), "save project");
                break;
            default:
                break;
        }
    }

    @Override
    public void onNegativeClick(int dialogId) {
        switch (dialogId) {
            case DialogClickListener.SAVE_PROJECT:
                if (mProjectId == mInitProjectId) {
                    mProjectTable.deleteProject(mProjectId);
                }
                deleteProjectIfEmpty(mProjectId);
                backToAzRecorderApp();
                break;
        }
    }

    @Override
    public void onColorChanged(int color) {
        mEdtColorHex.setText(convertToHexColor(color, false));
        setColorForViews(color);
    }

    @Override
    public void seekTo(int value, boolean scroll) {
        mActiveVideoView.seekTo(value);
        mScroll = scroll;
    }

    @Override
    public void updateVideoTimeLine(int leftPosition, int width) {
        mSelectedVideoTL.drawTimeLine(leftPosition, width);
        getLeftMargin(mCountVideo - 1);
        mMaxTimeLineMs = mVideoList.get(mCountVideo - 1).endInTimeLineMs;
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

    @Override
    public void invisibleVideoControl() {

    }

    @Override
    public void onOpenProjectClicked(final ProjectObject projectObject) {
        ProjectListDialog videoListDialog = (ProjectListDialog) getSupportFragmentManager()
                .findFragmentByTag(RECENT_PROJECT_LIST_FG);
        if (videoListDialog != null) videoListDialog.dismiss();

        final int projectId = projectObject.id;
        final String projectName = projectObject.name;

        if (mProjectId == projectId) {
            Toast.makeText(MainActivity.this,
                    getString(R.string.toast_open_current_project),
                    Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.dialog_open_project_title))
                    .setMessage(getString(R.string.dialog_open_project_mess, projectName == null ?
                            DEFAULT_PROJECT_NAME : projectName))
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    saveProject(mProjectId);
                                    openProject(projectObject);
                                }
                            });
            builder.show();
        }
        AnalyticsHelper.getInstance()
                .send(mActivity, Constants.CATEGORY_PROJECT, Constants.ACTION_OPEN_PROJECT);
    }

    private void initViews(){
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
        mAcceptTabLayout = (LinearLayout) findViewById(R.id.accept_tab_layout);
        mLayoutSetting = (RelativeLayout) findViewById(R.id.layout_setting);

        mLayoutTimeLine = (RelativeLayout) findViewById(R.id.layout_timeline);
        mLimitTimeLineVideo = findViewById(R.id.limit_timeline_video);
        mLimitTimeLineImage = findViewById(R.id.limit_timeline_image);
        mLimitTimeLineText = findViewById(R.id.limit_timeline_text);
        mLimitTimeLineAudio = findViewById(R.id.limit_timeline_audio);
        mSeperateLineVideo = findViewById(R.id.seperate_line_video);
        mSeperateLineImage = findViewById(R.id.seperate_line_image);
        mSeperateLineText = findViewById(R.id.seperate_line_text);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mBtnDelete = (TextView) findViewById(R.id.btn_delete);
        mFolderName = (TextView) findViewById(R.id.text_folder_name);
        mLayoutAdd = (RelativeLayout) findViewById(R.id.layout_add);
        mBtnAddVideo = (TextView) findViewById(R.id.btn_add_video);
        mBtnAddImage = (TextView) findViewById(R.id.btn_add_image);
        mBtnAddAudio = (TextView) findViewById(R.id.btn_add_audio);
        mBtnRecentProject = (TextView) findViewById(R.id.btn_recent_projects);
        mBtnNewProject = (TextView) findViewById(R.id.btn_new_project);
        mBtnAddText = (TextView) findViewById(R.id.btn_add_text);
        mBtnEdit = (TextView) findViewById(R.id.btn_edit);
        mBtnTrim = (TextView) findViewById(R.id.btn_trim);
        mLayoutEditText = (LinearLayout) findViewById(R.id.layout_edit_text);
        mEditText = (EditText) findViewById(R.id.edittext_input);
        mFontSpinner = (Spinner) findViewById(R.id.font_spinner);
        mColorPicker = (ColorPickerView) findViewById(R.id.color_picker);
        mBtnTextColor = (ImageView) findViewById(R.id.btn_text_color);
        mBtnTextBgrColor = (ImageView) findViewById(R.id.btn_text_background_color);
        mEdtColorHex = (EditText) findViewById(R.id.edt_color_hex);
        mLayoutBtnTextColor = (RelativeLayout) findViewById(R.id.layout_btn_text_color);
        mLayoutBtnTextBgrColor = (RelativeLayout) findViewById(R.id.layout_btn_text_bgr_color);
        mLayoutColorPicker = (RelativeLayout) findViewById(R.id.layout_color_picker);
        mBtnCloseColorPicker = (Button) findViewById(R.id.btn_close_colorpicker);
        mIndicatorTextColor = (ImageView) findViewById(R.id.indicator_textcolor);
        mIndicatorTextBgr = (ImageView) findViewById(R.id.indicator_textbackground);
        mBtnVolume = (TextView) findViewById(R.id.btn_volume);
        mLayoutTimeMark = (RelativeLayout) findViewById(R.id.layout_timemark);
        mTopLayout = (RelativeLayout) findViewById(R.id.top_layout);
        mMainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        mSeekbarIndicator = findViewById(R.id.seekbar_indicator);
        mLayoutFragment = (FrameLayout) findViewById(R.id.layout_fragment);
        mLayoutFloatView = (RelativeLayout) findViewById(R.id.layout_floatview);
        mBtnUpLevel = (ImageView) findViewById(R.id.btn_up_level);
        mLayoutExtraTools = (RelativeLayout) findViewById(R.id.extra_toolbar_left);
        mBtnReport = (TextView) findViewById(R.id.btn_report);
        mBtnCrop = (TextView) findViewById(R.id.btn_crop);
        mBtnSetting = (ImageView) findViewById(R.id.btn_setting);
        mBtnUpgrade = (TextView) findViewById(R.id.btn_upgrade);
        mBtnExportGif = (ImageView) findViewById(R.id.btn_gif);
        mBtnTrimContainer = (FrameLayout) findViewById(R.id.btn_trim_container);

        mTLShadow = new ImageView(this);
        mTLShadow.setBackgroundResource(R.drawable.shadow);
        mTLShadowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        mShadowIndicator = new ImageView(this);
        mShadowIndicator.setBackgroundResource(R.drawable.shadow_indicator);
        mShadowIndicatorParams = new RelativeLayout.LayoutParams(10, mTimeLineVideoHeight);
        mShadowIndicator.setLayoutParams(mShadowIndicatorParams);
    }

    private void setViewsListener(){
        mVideoTabLayout.setOnClickListener(onTabLayoutClick);
        mImageTabLayout.setOnClickListener(onTabLayoutClick);
        mAudioTabLayout.setOnClickListener(onTabLayoutClick);
        mAcceptTabLayout.setOnClickListener(onTabLayoutClick);
        mBtnBack.setOnClickListener(onBtnBackClick);
        mBtnAdd.setOnClickListener(onBtnAddClick);
        mBtnPlay.setOnClickListener(onBtnPlayClick);
        mBtnExport.setOnClickListener(onBtnExportClick);
        mBtnUndo.setOnClickListener(onBtnUndoClick);
        mBtnDelete.setOnClickListener(onBtnDeleteClick);
        mBtnAddVideo.setOnClickListener(onBtnsAddMediaClick);
        mBtnAddImage.setOnClickListener(onBtnsAddMediaClick);
        mBtnAddAudio.setOnClickListener(onBtnsAddMediaClick);
        mBtnAddText.setOnClickListener(onBtnAddTextClick);
        mBtnEdit.setOnClickListener(onBtnEditClick);
        mLayoutBtnTextColor.setOnClickListener(onLayoutBtnTextColorClick);
        mLayoutBtnTextBgrColor.setOnClickListener(onLayoutBtnTextBgrColorClick);
        mBtnCloseColorPicker.setOnClickListener(onBtnCloseColorPickerClick);
        mBtnVolume.setOnClickListener(onBtnVolumeClick);
        mLayoutAdd.setOnClickListener(onLayoutAddClick);
        mBtnUpLevel.setOnClickListener(onBtnUpLevelClick);
        mTopLayout.setOnClickListener(onHideStatusClick);
        mBtnSetting.setOnClickListener(onBtnSettingClick);
        mBtnUpgrade.setOnClickListener(onBtnUpgradeClick);
        mBtnReport.setOnClickListener(onBtnReportClick);
        mBtnCrop.setOnClickListener(onBtnCropClick);
        mBtnExportGif.setOnClickListener(onBtnExportGifClick);
        mBtnRecentProject.setOnClickListener(onBtnRecentProject);
        mBtnNewProject.setOnClickListener(onBtnAddNewProjectClick);
        mBtnTrim.setOnClickListener(onBtnTrimClick);
        findViewById(R.id.btn_ok_edit_text).setOnClickListener(onBtnEditClick);
        mEditText.setOnEditorActionListener(onEditTextActionListener);
        mEdtColorHex.setOnEditorActionListener(onEditColorActionListener);
        mTimeLineImage.setOnDragListener(onExtraDragListener);
        mTimeLineVideo.setOnDragListener(onExtraDragListener);
        mTimeLineText.setOnDragListener(onExtraDragListener);
        mTimeLineAudio.setOnDragListener(onExtraDragListener);
        mScrollView.setOnCustomScrollChanged(mScrollChangedListener);
        mLayoutTimeLine.getViewTreeObserver().addOnGlobalLayoutListener(onLayoutTimeLineCreated);
        mVideoViewLayout.getViewTreeObserver().addOnGlobalLayoutListener(onVideoViewLayoutCreated);
        mLayoutVideo.getViewTreeObserver().addOnGlobalLayoutListener(onLayoutVideoCreated);
        mLayoutImage.getViewTreeObserver().addOnGlobalLayoutListener(onLayoutImageCreated);
    }

    private void backToAzRecorderApp() {
        finish();
        if (mOpenFromDialog) {
            sendBroadcast(new Intent("show_floating_controller"));
            return;
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.hecorat.screenrecorder.free",
                "com.hecorat.screenrecorder.free.preferences.MainSettings"));
        intent.putExtra(Constants.FRAGMENT_CODE, Constants.GALLERY_CODE);
        startActivity(intent);
        AnalyticsHelper.getInstance().send(mActivity, Constants.CATEGORY_CLICK_BACK,
                Constants.ACTION_CLICK_BUTTON_BACK);
    }

    private void getVideoPathFromAzRecorder() {
        Intent intent = getIntent();
        if (intent != null) {
            mVideoPath = intent.getStringExtra(Constants.VIDEO_FILE_PATH);
            mUseSdCard = intent.getBooleanExtra(Constants.USE_SD_CARD, false);
            mOutputDirectory = intent.getStringExtra(Constants.DIRECTORY);
            mIsVip = intent.getBooleanExtra(Constants.IS_VIP, false);
            mOpenFromDialog = intent.getBooleanExtra(Constants.OPEN_FROM_DIALOG, true);
            if (mVideoPath == null) return;
            String videoName = new File(mVideoPath).getName();
            mProjectName = videoName.substring(0, videoName.length() - 4);
        }
    }

    protected void setFullscreen() {
        Window window = getWindow();
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initVideoView() {
        mVideoView1 = new CustomVideoView(this);
        mVideoView2 = new CustomVideoView(this);
        RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mVideoViewLayout.addView(mVideoView1, params);
//        params = new RelativeLayout.LayoutParams
//                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mVideoViewLayout.addView(mVideoView2, params);
        mActiveVideoView = mVideoView1;
        mInActiveVideoView = mVideoView2;
        mActiveVideoView.bringToFront();
        mLayoutFloatView.bringToFront();
        mActiveVideoView.setOnClickListener(onHideStatusClick);
        mInActiveVideoView.setOnClickListener(onHideStatusClick);
    }

    public void openLayoutCropVideo(boolean open) {
        if (open) {
            mFragmentCrop = FragmentCrop.newInstance(mActivity, mSelectedVideoTL);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_fragment, mFragmentCrop).commit();
            setLayoutFragmentVisible(true);
            setActiveVideoViewVisible(false);
        } else {
            View view = mFragmentCrop.getView();
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
            setLayoutFragmentVisible(false);
            setActiveVideoViewVisible(true);
        }
    }

    public void slideExtraToolsIn(boolean in) {
        if (!in && !mOpenExtraTools) {
            return;
        }
        if (in) {
            slideLayoutSettingIn(false);
        }
        setLayoutExtraToolsVisible(in);
        int distance = Utils.dpToPixel(this, 60);
        TranslateAnimation animation = in ? new TranslateAnimation(-distance, 0, 0, 0)
                : new TranslateAnimation(0, -distance, 0, 0);
        animation.setDuration(LAYOUT_ANIMATION_DURATION);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        mLayoutExtraTools.startAnimation(animation);
    }

    private void setLayoutExtraToolsVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mLayoutExtraTools.setVisibility(visibility);
        mOpenExtraTools = visible;
    }

    public void setBtnCropVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnCrop.setVisibility(visibility);
    }

    public void setBtnUpLevelVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnUpLevel.setVisibility(visibility);
    }

    private void checkVip() {
        onCheckVipCompleted(mIsVip);
        log("isVip = " + mIsVip);
    }

    public void checkVipWithoutInternet() {
        new CheckVipWithoutInternetTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void onCheckVipCompleted(boolean isVip) {
        Utils.getSharedPref(this).edit().putBoolean(getString(R.string.pref_is_vip), isVip).apply();
        if (isVip) {
            mBtnUpgrade.setVisibility(View.GONE);
            findViewById(R.id.text_pro_1).setVisibility(View.GONE);
            findViewById(R.id.text_pro_2).setVisibility(View.GONE);
        } else {
            mBtnUpgrade.setVisibility(View.VISIBLE);
        }
    }

    private void createProject() {
        slideLayoutSettingIn(false);
        NameDialog.newInstance(mActivity, mActivity, NameDialog.CREATE_PROJECT, Utils.getTime())
                .show(getSupportFragmentManager(), "create project");
    }

    private void startDonate(String detail) {
        Intent intent = new Intent(Constants.ACTION_IABTABLE);
        intent.putExtra("from", detail);
        startActivityForResult(intent, Constants.REQUEST_CODE_PURCHASE);
    }

    private void initFontManager() {
        if (mFontPath != null) {
            return;
        }
        new LoadFontTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initFileManager() {
        if (mGalleryPagerAdapter != null) {
            return;
        }
        mGalleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mGalleryPagerAdapter);
        mViewPager.addOnPageChangeListener(onViewPagerChanged);
        mFragmentVideosGallery = FragmentVideosGallery.newInstance(mActivity);
        mFragmentImagesGallery = FragmentImagesGallery.newInstance(mActivity);
        mFragmentAudioGallery = FragmentAudioGallery.newInstance(mActivity);
    }

    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initDatabase() {
        int oldDbVersion = Utils.getSharedPref(this).getInt(getString(R.string.db_version), 0);

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
            Utils.getSharedPref(this).edit().putInt(getString(R.string.db_version), Constants.DB_VERSION).apply();
        }
        mProjectTable.createTable();
        mVideoTable.createTable();
        mImageTable.createTable();
        mTextTable.createTable();
        mAudioTable.createTable();

        mInitProjectId = (int) mProjectTable.insertValue(mProjectName, System.currentTimeMillis() + "");
        mProjectId = mInitProjectId;
        mProjectTable.updateValue(mProjectId, ProjectTable.PROJECT_FIRST_VIDEO, mVideoPath);
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
        mActiveVideoView.stopPlayback();
        setVideoViewVisible(false);
        setVideoViewVisible(true);
        setBtnPlayVisible(false);
        setBtnVolumeVisible(false);
        setBtnEditVisible(false);
        setBtnTrimVisible(false);
        setBtnDeleteVisible(false);
        setBtnExportVisible(false);
        setBtnCropVisible(false);
        mCountVideo = 0;
        mCurrentVideoId = -1;
        mTLPositionInMs = 0;
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

    private void startAnimationAddFile(final int fileType, final int duration,
                                       final float xDes, float yDes) {
        ViewGroup parent = (ViewGroup) mLayoutAnimationAddFile.getParent();
        if (parent == null) {
            mMainLayout.addView(mLayoutAnimationAddFile);
        }

        float x0 = xDes / 3;
        final float a = yDes / (xDes * xDes - 2 * x0 * xDes);
        final float b = -2 * x0 * a;

        ValueAnimator animator = ValueAnimator.ofFloat(0, xDes);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) (animation.getAnimatedValue());
                float y = a * x * x + b * x;
                mImageShadowAnimation.setTranslationX(x);
                mImageShadowAnimation.setTranslationY(y);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                onAnimationAddFileCompleted(fileType);
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

    private void openProject(ProjectObject projectObject) {
        mProjectId = projectObject.id;
        mProjectName = projectObject.name;
        resetActivity();
        resetTempListLayout();
        restoreAllVideoTL(mProjectId);
        restoreAllAudioTL(mProjectId);
        restoreAllImageTL(mProjectId);
        restoreAllTextTL(mProjectId);
        restoreListInLayout();

        updateBtnExportVisible();
        fixAllVideosHasNoAudio();
    }

    private void resetTempListLayout() {
        mTempListLayoutImage.clear();
        mTempListLayoutText.clear();
    }

    private void saveSystemVolume() {
        mSystemVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
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
    }

    public void onIncreaseVolumeTaskCompleted(boolean isVideo) {
        if (!isVideo) {
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
        String s = String.format("%08X", color);
        if (export) {
            resultColor += s.substring(2) + "@0x" + s.substring(0, 2);
        } else {
            resultColor += s;
        }
        return resultColor;
    }

    private void openLayoutTrimVideo() {
        setActiveVideoViewVisible(false);
        mTrimFragment = TrimFragment.newInstance(mActivity, mSelectedVideoTL);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment,
                mTrimFragment).commit();
        setLayoutFragmentVisible(true);
        mOpenLayoutTrimVideo = true;
        AnalyticsHelper.getInstance()
                .send(mActivity, Constants.CATEGORY_VIDEO, Constants.ACTION_TRIM_VIDEO);
    }

    public void closeLayoutTrimVideo() {
        mTrimFragment.close();
    }

    public void setActiveVideoViewVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mInActiveVideoView.stopPlayback();
        mInActiveVideoView.setVisibility(View.GONE);
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
        } else {
            updateLayoutEditText();
        }
    }

    public void updateLayoutEditText() {
        if (mSelectedExtraTL == null) {
            return;
        }
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

    private void deleteAudioTimeLine() {
        mAudioList.remove(mSelectedAudioTL);
        mLayoutAudio.removeView(mSelectedAudioTL);
        AnalyticsHelper.getInstance()
                .send(mActivity, Constants.CATEGORY_AUDIO, Constants.ACTION_DELETE_AUDIO);
        invisibleAudioControl();
    }

    private void deleteExtraTimeline() {
        if (mSelectedExtraTL.isImage) {
            mImageList.remove(mSelectedExtraTL);
            mLayoutFloatView.removeView(mSelectedExtraTL.floatImage);
            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_IMAGE, Constants.ACTION_DELETE_IMAGE);
        } else {
            mTextList.remove(mSelectedExtraTL);
            mLayoutFloatView.removeView(mSelectedExtraTL.floatText);
            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_TEXT, Constants.ACTION_DELETE_TEXT);
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
            updateVideoViewSize(mActiveVideoView, mSelectedVideoTL);
        } else {
            mMaxTimeLineMs = 0;
            mActiveVideoView.stopPlayback();
            setBtnExportVisible(false);
            setBtnPlayVisible(false);
        }
        AnalyticsHelper.getInstance()
                .send(mActivity, Constants.CATEGORY_VIDEO, Constants.ACTION_DELETE_VIDEO);
    }

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

    public void deleteAllObjects(int projectId) {
        mVideoTable.deleteVideoOf(projectId);
        mAudioTable.deleteAudioOf(projectId);
        mImageTable.deleteImageOf(projectId);
        mTextTable.deleteTextOf(projectId);
    }

    public void saveProject(int projectId) {
        deleteAllObjects(projectId);
        saveVideoObjects(projectId);
        saveAudioObjects(projectId);
        saveImageObjects(projectId);
        saveTextObjects(projectId);
        deleteProjectIfEmpty(projectId);
    }

    private void deleteProjectIfEmpty(int projectId) {
        if (mVideoList.isEmpty() && mImageList.isEmpty()
                && mAudioList.isEmpty()) {
            if (mIsVip && mTextList.isEmpty() || !mIsVip && mTextList.size() == 1) {
                mProjectTable.deleteProject(projectId);
            }
        }
    }

    public void exportVideo(boolean exportVideo) {
        mExportFragment = ExportFragment.newInstance(mActivity, exportVideo);
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_fragment, mExportFragment).commit();
        setLayoutFragmentVisible(true);
        mOpenLayoutExport = true;
    }

    public void updateMediaPlayer() {
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

    public void updateImageVisibility() {
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

    public void updateTextVisibility() {
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

    public void updateBtnPlay() {
        if (mActiveVideoView.isPlaying()) {
            mBtnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            mBtnPlay.setImageResource(R.drawable.ic_play);
        }
    }

    public void updateCurrentPosition() {
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

    public void updatePreviewStatus() {
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

    public void updateVideoView() {
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

        if (mCurrentVideoId == -1) {
            prepareNextVideo();
        }

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

        if (mCurrentVideoId != timelineId && videoTL != null && videoTL.isExists) {
            playNextVideo();
            mCurrentVideoId = timelineId;
            mCurrentVideoTL = mVideoList.get(mCurrentVideoId);
            mActiveVideoView.seekTo(videoTL.startTimeMs);
            mActiveVideoView.start();
            updateSystemVolume();
            prepareNextVideo();
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
        setActiveVideoViewVisible(true);
    }

    private void prepareNextVideo() {
        if (mCurrentVideoId < mCountVideo - 1) {
            VideoTL nextVideoTL = mVideoList.get(mCurrentVideoId + 1);
            if (nextVideoTL.isExists) {
                mInActiveVideoView.setVideoPath(nextVideoTL.videoPath);
                mInActiveVideoView.seekTo(10);
                updateVideoViewSize(mInActiveVideoView, nextVideoTL);
            }
        }
    }

    public void cropVideo(VideoTL videoTL) {
        int selectedVideoId = mVideoList.indexOf(videoTL);
        if (selectedVideoId == mCurrentVideoId) {
            updateVideoViewSize(mActiveVideoView, videoTL);
        }
    }

    public void updateVideoViewSize(CustomVideoView videoView, VideoTL videoTL) {
        ViewGroup.LayoutParams params = videoView.getLayoutParams();
        params.width = (int) (mVideoViewHeight * videoTL.videoRatio);
        videoView.setLayoutParams(params);
        videoView.setVideoSize(videoTL.leftSide, videoTL.rightSide,
                videoTL.bottomSide, videoTL.topSide);
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

    private void setVideoViewVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mActiveVideoView.setVisibility(visibility);
    }

    private void resetVideoView() {
        mCurrentVideoId = -1;
        mTLPositionInMs = 0;
    }

    private void hideAllFloatControllers() {
        setFloatImageVisible(null);
        setFloatTextVisible(null);
        setAudioControlVisible(false);
        setExtraControlVisible(false);
        setLayoutSettingVisible(false);
        setLayoutExtraToolsVisible(false);
        setLayoutAddVisible(false);
        unSelectVideoTL();
    }

    public void pausePreview() {
        if (mCountVideo < 1) {
            return;
        }
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

    public void saveVideoObjects(int projectId) {
        getVideoOrder();
        for (VideoTL videoTL : mVideoList) {
            VideoObject videoObject = videoTL.getVideoObject();
            mVideoTable.insertValue(videoObject, projectId);
        }
        if (mVideoList.size() > 0) {
            VideoObject videoObject = mVideoList.get(0).getVideoObject();
            mProjectTable.updateValue(projectId, ProjectTable.PROJECT_FIRST_VIDEO, videoObject.path);
        }
    }

    public void restoreAllVideoTL(int projectId) {
        ArrayList<VideoObject> list = mVideoTable.getData(projectId);
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

    public void addVideoTL() {
        try {
            VideoTL videoTL = new VideoTL(this, mVideoPath, mTimeLineVideoHeight);
            videoTL.setOnClickListener(onVideoTimeLineClick);
            videoTL.setOnLongClickListener(onVideoLongClick);

            mVideoList.add(videoTL);
            mLayoutVideo.addView(videoTL);
            mCountVideo++;
            getLeftMargin(mCountVideo - 1);

            mMaxTimeLineMs = videoTL.endInTimeLineMs;
            mTLPositionInMs = videoTL.startInTimeLineMs;
            scrollTo(mTLPositionInMs);
            mActiveVideoView.setVideoPath(videoTL.videoPath);
            mActiveVideoView.seekTo(10);
            updateVideoViewSize(mActiveVideoView, videoTL);
            mCurrentVideoId = mCountVideo - 1;
            fixIfVideoHasNoAudio(videoTL);
            setActiveVideoViewVisible(true);

            mSelectedVideoTL = videoTL;
            updateBtnExportVisible();
            setBtnDeleteVisible(true);
            setBtnPlayVisible(true);
            setBtnVolumeVisible(true);
            setBtnEditVisible(false);
            setBtnTrimVisible(true);
            highlightSelectedVideoTL();
            setLayoutExtraToolsVisible(true);
            setBtnCropVisible(true);
            mSelectedTL = TIMELINE_VIDEO;
            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_ADD_FILE, Constants.ACTION_ADD_VIDEO);
        } catch (Exception e) {
            Toast.makeText(this, R.string.toast_add_video_fail, Toast.LENGTH_LONG).show();
        }
    }

    public void setWaterMarkEndTime(int endTime) {
        if (mIsVip) return;

        mTextList.get(0).endInTimeLineMs = endTime;
        mTextList.get(0).startInTimeLineMs = 0;
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
        findViewById(R.id.preview_container).setVisibility(visibility);
    }

    public void setBtnEditVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnEdit.setVisibility(visibility);
    }

    public void setBtnTrimVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnTrimContainer.setVisibility(visibility);
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

    public void saveTextObjects(int projectId) {
        getTextOrder();
        int i = mIsVip ? 0 : 1;
        while (i < mTextList.size()) {
            ExtraTL extraTL = mTextList.get(i);
            TextObject text = extraTL.getTextObject();
            mTextTable.insertValue(text, projectId);
            i++;
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

    public void saveImageObjects(int projectId) {
        getImageOrder();
        for (ExtraTL extraTL : mImageList) {
            ImageObject image = extraTL.getImageObject();
            mImageTable.insertValue(image, projectId);
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

        mImageList.add(extraTL);
        if (extraTL.inLayoutImage) {
            mLayoutImage.addView(extraTL);
            mTempListLayoutImage.add(extraTL);
        } else {
            mLayoutText.addView(extraTL);
            mTempListLayoutText.add(extraTL);
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
        setBtnTrimVisible(false);
        setBtnVolumeVisible(false);
        setBtnCropVisible(false);
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

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                mImageShadowAnimation.getLayoutParams();
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

    private void restoreListInLayout() {
        for (int i = 0; i < mTempListLayoutImage.size(); i++) {
            for (ExtraTL extraTL : mTempListLayoutImage) {
                if (i == extraTL.orderInLayout) {
                    mListInLayoutImage.add(extraTL);
                    break;
                }
            }
        }

        for (int i = 0; i < mTempListLayoutText.size(); i++) {
            for (ExtraTL extraTL : mTempListLayoutText) {
                if (i == extraTL.orderInLayout) {
                    mListInLayoutText.add(extraTL);
                    break;
                }
            }
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
            mTempListLayoutImage.add(extraTL);
        } else {
            mLayoutText.addView(extraTL);
            mTempListLayoutText.add(extraTL);
        }

        FloatText floatText = new FloatText(this, text, false);
        mLayoutFloatView.addView(floatText);
        extraTL.floatText = floatText;
        floatText.timeline = extraTL;
        floatText.restoreState(textObject);
        floatText.drawBorder(false);
        floatText.setVisibility(View.GONE);

        AnalyticsHelper.getInstance()
                .send(mActivity, Constants.CATEGORY_ADD_FILE, Constants.ACTION_ADD_TEXT);
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
        DialogConfirm.newInstance(this, this, DialogClickListener.ASK_DONATE,
                Constants.EVENT_ACTION_DIALOG_FROM_WATERMARK)
                .show(getSupportFragmentManager(), "ask donate");
        AnalyticsHelper.getInstance().send(mActivity, Constants.CATEGORY_DONATE,
                Constants.ACTION_REMOVE_WATERMARK);
    }

    public void removeWaterMark() {
        mIsVip = true;
        mBtnUpgrade.setVisibility(View.GONE);
        Utils.getSharedPref(this).edit().putBoolean(getString(R.string.pref_is_vip), true).apply();
        mLayoutFloatView.removeView(mWaterMark);
        mTextList.remove(mWaterMark.timeline);
//        Toast.makeText(this, "Watermark was removed", Toast.LENGTH_LONG).show();
        AnalyticsHelper.getInstance().send(this, Constants.CATEGORY_DONATE,
                Constants.ACTION_REMOVE_SUCCESSFUL);
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

        updateBtnExportVisible();
        setBtnDeleteVisible(true);
        setBtnVolumeVisible(false);
        setBtnTrimVisible(false);
        setBtnEditVisible(true);
        setBtnCropVisible(false);
        slideExtraToolsIn(true);
        mSelectedTL = TIMELINE_EXTRA;
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

    public void saveAudioObjects(int projectId) {
        getAudioOrder();
        for (AudioTL audioTL : mAudioList) {
            AudioObject audio = audioTL.getAudioObject();
            mAudioTable.insertValue(audio, projectId);
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
        mAudioList.add(audioTL);
    }

    public void addAudioTL() {
        try {
            int leftMargin = mLeftMarginTimeLine + mScrollView.getScrollX();
            AudioTL audioTL = new AudioTL(this, mAudioPath, mTimeLineImageHeight, leftMargin);
            addAudioTLToTL(audioTL);
            audioTL.setOnClickListener(onAudioTimeLineClick);
            audioTL.setOnLongClickListener(onAudioLongClick);
            mSelectedAudioTL = audioTL;

            updateBtnExportVisible();
            setLayoutExtraToolsVisible(true);
            mSelectedTL = TIMELINE_AUDIO;

            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_ADD_FILE, Constants.ACTION_ADD_AUDIO);
        } catch (Exception e) {
            Toast.makeText(this, R.string.toast_add_video_fail, Toast.LENGTH_LONG).show();

        }
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

    private void selectTabLayout(int tabNumber) {
        switch (tabNumber) {
            case VIDEO_TAB:
                mViewPager.setCurrentItem(VIDEO_TAB, true);
                setHighLightTab(VIDEO_TAB);
                setFolderName(mFragmentVideosGallery.mFolderName);
                setBtnUpLevelVisible(
                        mFragmentVideosGallery.galleryState != GalleryState.VIDEO_FOLDER);
                break;
            case IMAGE_TAB:
                mViewPager.setCurrentItem(IMAGE_TAB, true);
                setHighLightTab(IMAGE_TAB);
                setFolderName(mFragmentImagesGallery.mFolderName);
                setBtnUpLevelVisible(
                        mFragmentImagesGallery.galleryState != GalleryState.IMAGE_FOLDER);
                break;
            case AUDIO_TAB:
                mViewPager.setCurrentItem(AUDIO_TAB, true);
                setHighLightTab(AUDIO_TAB);
                setFolderName(mFragmentAudioGallery.mFolderName);
                setBtnUpLevelVisible(
                        mFragmentAudioGallery.galleryState != GalleryState.AUDIO_FOLDER);
                break;
            case ACCEPT_TAB:
                openFileManager(false);
                break;
            default:
                break;
        }
    }

    public void setFolderName(String name) {
        mFolderName.setText(name);
    }

    private void setLayoutAddVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mLayoutAdd.setVisibility(visibility);
        mOpenLayoutAdd = visible;
        highlightSelectedLayout(R.id.btn_add_container, visible);
    }

    private void slideLayoutAddIn(boolean in) {
        if (!in && !mOpenLayoutAdd) {
            return;
        }
        if (in) {
            slideLayoutSettingIn(false);
        }
        setLayoutAddVisible(in);
        int distance = Utils.dpToPixel(this, 60);
        TranslateAnimation animation = in ? new TranslateAnimation(-distance, 0, 0, 0) :
                new TranslateAnimation(0, -distance, 0, 0);
        animation.setDuration(LAYOUT_ANIMATION_DURATION);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        mLayoutAdd.startAnimation(animation);
    }

    private void slideLayoutSettingIn(boolean in) {
        if (!in && !mOpenLayoutSetting) {
            return;
        }
        setLayoutSettingVisible(in);
        int distance = mLayoutSetting.getWidth();
        TranslateAnimation animation = in ? new TranslateAnimation(distance, 0, 0, 0) :
                new TranslateAnimation(0, distance, 0, 0);
        animation.setDuration(LAYOUT_ANIMATION_DURATION);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        mLayoutSetting.startAnimation(animation);
    }

    private void setLayoutSettingVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mLayoutSetting.setVisibility(visibility);
        mOpenLayoutSetting = visible;
        highlightSelectedLayout(R.id.btn_setting_container, visible);
    }

    private void openFileManager(boolean open) {
        if (open) {
            mFileManager.setVisibility(View.VISIBLE);
            mOpenFileManager = true;
        } else {
            mFileManager.setVisibility(View.GONE);
            mOpenFileManager = false;
            updateBtnExportVisible();
        }
    }

    private boolean upLevelFileManager() {
        if (!mOpenFileManager) {
            return false;
        }
        switch (mFragmentCode) {
            case VIDEO_TAB:
                if (mFragmentVideosGallery.galleryState
                        != GalleryState.VIDEO_FOLDER) {
                    mFragmentVideosGallery.upLevel();
                    return true;
                }
                break;
            case IMAGE_TAB:
                if (mFragmentImagesGallery.galleryState
                        != GalleryState.IMAGE_FOLDER) {
                    mFragmentImagesGallery.upLevel();
                    return true;
                }
                break;
            case AUDIO_TAB:
                if (mFragmentAudioGallery.galleryState
                        != GalleryState.AUDIO_FOLDER) {
                    mFragmentAudioGallery.upLevel();
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

    private void setHighLightTab(int tab) {
        int videoTabId = tab == VIDEO_TAB ? R.drawable.ic_video_tab_blue : R.drawable.ic_video_tab;
        int imageTabId = tab == IMAGE_TAB ? R.drawable.ic_image_tab_blue : R.drawable.ic_image_tab;
        int audioTabId = tab == AUDIO_TAB ? R.drawable.ic_audio_tab_blue : R.drawable.ic_audio_tab;
        mVideoTab.setImageResource(videoTabId);
        mImageTab.setImageResource(imageTabId);
        mAudioTab.setImageResource(audioTabId);
    }

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

    public void startDragAudioTL(View view) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
        ClipData clipData = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.startDragAndDrop(clipData, shadowBuilder, view, 0);
        } else {
            view.startDrag(clipData, shadowBuilder, view, 0);
        }

        mDragCode = DRAG_AUDIO;
        addShadowToLayoutAudio();
        mLayoutScrollView.setOnDragListener(onAudioDragListener);
        invisibleAllController();
        mFirstAnchor = true;
        mDragAnchor = 200;
    }

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

    private void closeLayoutExport() {
        mExportFragment.backToEdit();
    }

    private void onBackClick() {
        if (mOpenLayoutExport) {
            log("mOpenLayoutExport = true");
            if (mExportFragment.mExporting) {
                log("mExportFragment.mExporting");
            } else {
                closeLayoutExport();
            }
            return;
        }

        if (mOpenLayoutTrimVideo) {
            closeLayoutTrimVideo();
            return;
        }

        if (mOpenLayoutEditText) {
            openLayoutEditText(false);
            return;
        }

        if (upLevelFileManager()) {
            return;
        }

        if (mOpenExtraTools || mOpenLayoutSetting || mOpenLayoutAdd) {
            hideAllFloatControllers();
            return;
        }

        DialogConfirm.newInstance(mActivity, mActivity, DialogClickListener.SAVE_PROJECT, "")
                .show(getSupportFragmentManager(), "save project");
    }

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
                setBtnExportVisible(true);
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
        slideLayoutSettingIn(false);
        setLayoutAddVisible(false);
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
            setBtnTrimVisible(false);
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
            setBtnTrimVisible(false);
            setBtnVolumeVisible(false);
            slideExtraToolsIn(true);
        }
        unhighlightVideoTL();
        slideLayoutSettingIn(false);
        setLayoutAddVisible(false);
    }

    public void setExtraControlVisible(boolean visible) {
        if (visible) {
            mExtraTLControl.setVisibility(View.VISIBLE);
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

    private void selectVideoTL() {
        mSelectedTL = TIMELINE_VIDEO;
        highlightSelectedVideoTL();
//        if (!mSelectedVideoTL.equals(mCurrentVideoTL)) {
//            mScrollView.scrollTo(mSelectedVideoTL.startInTimeLineSec / Constants.SCALE_VALUE, 0);
//            mScrollChangedListener.onScrollChanged();
//        }
        setBtnDeleteVisible(true);
        setBtnVolumeVisible(true);
        setBtnEditVisible(false);
        setBtnTrimVisible(true);
        setBtnCropVisible(true);
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
        findViewById(R.id.export_container).setVisibility(visibility);
        findViewById(R.id.export_gif_container).setVisibility(visibility);
    }

    public void setBtnDeleteVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mBtnDelete.setVisibility(visibility);
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

    private void setVideoRatio() {
        ViewGroup.LayoutParams params = mVideoViewLayout.getLayoutParams();
        int height = (int) (Utils.getScreenHeight(mActivity) * 0.6);
        log("Width = " + Utils.getScreenHeight(mActivity));
        params.height = height;
        mVideoViewHeight = height;
        params.width = (int) (params.height * 1.77);
        mVideoViewLayout.setLayoutParams(params);
        params = mTopLayout.getLayoutParams();
        params.height = height;
        mTopLayout.setLayoutParams(params);
    }

    public float getLayoutVideoScale(float realHeight) {
        return realHeight / (float) mVideoViewLayout.getHeight();
    }

    private void setAudioControlVisible(boolean visible) {
        if (visible) {
            mAudioTLControl.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
            mExtraTLControl.setVisibility(View.GONE);
        } else {
            mAudioTLControl.setVisibility(View.GONE);
            mScrollView.scroll = true;
        }
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

    public void startDragExtraTL(View view) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
        ClipData clipData = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.startDragAndDrop(clipData, shadowBuilder, view, 0);
        } else {
            view.startDrag(clipData, shadowBuilder, view, 0);
        }
        mTLShadowParams.width = mSelectedExtraTL.width;
        mTLShadowParams.height = mSelectedExtraTL.height;
        mShadowIndicatorParams.height = mSelectedExtraTL.height;
        mDragCode = DRAG_EXTRA;
        invisibleAllController();
        mFirstAnchor = true;
        mDragAnchor = 200;
    }

    private void highlightSelectedLayout(int viewId, boolean highlight) {
        findViewById(viewId).setBackgroundResource(highlight ?
                R.color.dark_blue : android.R.color.transparent);
    }

    private void log(String msg) {
        Log.e("MainActivity", msg);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private final class LoadFontTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mFontPath = FontManager.getFontPaths(mActivity);
            mFontAdapter = new FontAdapter(mActivity, android.R.layout.simple_spinner_item, mFontPath);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mFontSpinner.setAdapter(mFontAdapter);
            mFontSpinner.setOnItemSelectedListener(onFontSelectedListener);
            addWaterMark();
        }
    }

    private final class CheckVipWithoutInternetTask extends AsyncTask<Void, Void, Void> {
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

    private final class GalleryPagerAdapter extends FragmentPagerAdapter {

        GalleryPagerAdapter(FragmentManager fm) {
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

    private final class DelayTask extends TimerTask {
        @Override
        public void run() {
            mTimerTaskHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVideoPath != null) {
                        addVideoTL();
                        resetVideoView();
                    }
                }
            });
        }
    }

    private final class SaveProjectTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            saveProject(mProjectId);
            return null;
        }
    }

    private final class CustomScrollChanged implements
            CustomHorizontalScrollView.OnCustomScrollChanged {
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
                if (mTLPositionInMs >= videoTL.startInTimeLineMs
                        && mTLPositionInMs <= videoTL.endInTimeLineMs) {
                    timelineId = i;
                    break;
                }
            }
            int positionInVideo;
            if (timelineId > 0) {
                VideoTL previousTimeLine = mVideoList.get(timelineId - 1);
                positionInVideo = mTLPositionInMs - previousTimeLine.endInTimeLineMs
                        + videoTL.startTimeMs;
            } else {
                positionInVideo = mTLPositionInMs + videoTL.startTimeMs;
            }

            if (mCurrentVideoId != timelineId) {
                mCurrentVideoId = timelineId;
                mCurrentVideoTL = videoTL;
                if (videoTL.isExists) {
                    setActiveVideoViewVisible(true);
                    mActiveVideoView.setVideoPath(videoTL.videoPath);
                    updateVideoViewSize(mActiveVideoView, videoTL);
                    prepareNextVideo();
                } else {
                    setActiveVideoViewVisible(false);
                    toast("Video is not found");
                }
            }

            mActiveVideoView.seekTo(positionInVideo);
            updateMediaPlayerOnScroll();
            updateTextVisibility();
            updateImageVisibility();
            updateSystemVolume();
        }
    }

    private static class ProgressHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        ProgressHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mActivity.get();
            switch (msg.what) {
                case MSG_CURRENT_POSITION:
                    activity.updateCurrentPosition();
                    activity.updatePreviewStatus();
                    activity.updateBtnPlay();
                    activity.updateVideoView();
                    activity.updateImageVisibility();
                    activity.updateTextVisibility();
                    activity.updateMediaPlayer();
                    activity.log(" Running");
                    break;
            }
        }
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
        }
    };

    AdapterView.OnItemSelectedListener onFontSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String font = mFontPath.get(position);
            if (mSelectedExtraTL != null) {
                mSelectedExtraTL.floatText.setFont(font, position);
            }
            mFontAdapter.setSelectedItem(position);
            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_TEXT, Constants.ACTION_CHANGE_TEXT_FONT);
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

                AnalyticsHelper.getInstance()
                        .send(mActivity, Constants.CATEGORY_TEXT, Constants.ACTION_CHANGE_TEXT);
            }
            return false;
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

                AnalyticsHelper.getInstance()
                        .send(mActivity, Constants.CATEGORY_TEXT, Constants.ACTION_CHANGE_HEX_COLOR);
            }
            return false;
        }
    };

    View.OnDragListener onExtraDragListener = new View.OnDragListener() {
        boolean inLayoutImage;
        int finalMargin = 0;


        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            if (mDragCode != DRAG_EXTRA) {
                return false;
            }

            int x = (int) dragEvent.getX();

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    log("ACTION_DRAG_STARTED: x = " + dragEvent.getX());
                    mTLShadowParams.leftMargin = mSelectedExtraTL.left;
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    log("ACTION_DRAG_LOCATION: x = " + dragEvent.getX());
                    if (mFirstAnchor) {
                        mDragAnchor = x - mSelectedExtraTL.left;
                        mFirstAnchor = false;
                    }
                    finalMargin = x - mDragAnchor;
                    if (finalMargin < mLeftMarginTimeLine) {
                        finalMargin = mLeftMarginTimeLine;
                    }
                    mTLShadowParams.leftMargin = finalMargin;
                    mTLShadow.setLayoutParams(mTLShadowParams);
                    if (view.equals(mTimeLineImage) || view.equals(mTimeLineVideo)) {
                        inLayoutImage = true;
                    } else if (view.equals(mTimeLineText) || view.equals(mTimeLineAudio)) {
                        inLayoutImage = false;
                    }

                    ViewGroup shadowParent = (ViewGroup) mTLShadow.getParent();

                    if (shadowParent != null) {
                        shadowParent.removeView(mTLShadow);
                    }

                    if (inLayoutImage) {
                        mTimeLineImage.addView(mTLShadow);
                    } else {
                        mTimeLineText.addView(mTLShadow);
                    }
                    break;
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
                    shadowParent = (ViewGroup) mTLShadow.getParent();
                    if (shadowParent != null) {
                        shadowParent.removeView(mTLShadow);
                    }
                    break;
            }
            return true;
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

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    mTLShadowParams.leftMargin = mSelectedVideoTL.left;
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    if (mFirstAnchor) {
                        mDragAnchor = x - mSelectedVideoTL.left;
                        mFirstAnchor = false;
                    }
                    finalMargin = x - mDragAnchor;
                    if (finalMargin < mLeftMarginTimeLine) {
                        finalMargin = mLeftMarginTimeLine;
                    }
                    mTLShadowParams.leftMargin = finalMargin;
                    mTLShadow.setLayoutParams(mTLShadowParams);
                    break;
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

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    mTLShadowParams.leftMargin = mSelectedAudioTL.left;
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    if (mFirstAnchor) {
                        mDragAnchor = x - mSelectedAudioTL.left;
                        mFirstAnchor = false;
                    }
                    finalMargin = x - mDragAnchor;
                    if (finalMargin < mLeftMarginTimeLine) {
                        finalMargin = mLeftMarginTimeLine;
                    }
                    mTLShadowParams.leftMargin = finalMargin;
                    mTLShadow.setLayoutParams(mTLShadowParams);
                    break;
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
            startDragAudioTL(view);
            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_AUDIO, Constants.ACTION_DRAG_AUDIO);
            return false;
        }
    };

    View.OnLongClickListener onExtraTimelineLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            mSelectedExtraTL = (ExtraTL) view;
            startDragExtraTL(view);

            if (mSelectedExtraTL.isImage) {
                AnalyticsHelper.getInstance()
                        .send(mActivity, Constants.CATEGORY_IMAGE, Constants.ACTION_DRAG_IMAGE);
            } else {
                AnalyticsHelper.getInstance()
                        .send(mActivity, Constants.CATEGORY_TEXT, Constants.ACTION_DRAG_TEXT);
            }
            return false;
        }
    };

    View.OnClickListener onBtnCropClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openLayoutCropVideo(true);
        }
    };

    View.OnClickListener onBtnReportClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            slideLayoutSettingIn(false);
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"az.video.edit@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Report problems");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello Hecorat,");
            try {
                mActivity.startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mActivity, R.string.toast_no_email_apps_installed, Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnClickListener onHideStatusClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            slideLayoutSettingIn(false);
            slideLayoutAddIn(false);
            if (mSelectedTL == TIMELINE_VIDEO) {
                slideExtraToolsIn(false);
                unhighlightVideoTL();
            }
        }
    };

    View.OnClickListener onLayoutAddClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setLayoutAddVisible(false);
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
            pausePreview();
            if (checkIfFileNotExists()) {
                toast(getString(R.string.toast_file_not_exists_when_click_export));
                return;
            }
            hideAllFloatControllers();
            exportVideo(true);
        }
    };

    View.OnClickListener onBtnEditClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOpenLayoutEditText) {
                openLayoutEditText(false);
            } else {
                initFontManager();
                openLayoutEditText(true);
            }
        }
    };

    View.OnClickListener onBtnTrimClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSelectedTL == TIMELINE_VIDEO) {
                openLayoutTrimVideo();
            }
        }
    };

    View.OnClickListener onBtnExportGifClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mVideoList.isEmpty()) return;

            int duration = mVideoList.get(mCountVideo - 1).endInTimeLineMs / 1000;
            if (duration > 20) {
                DialogConfirm.newInstance(mActivity, mActivity, DialogClickListener.WARNING_DURATION_GIF, "")
                        .show(getSupportFragmentManager(), "warning gif duration");
                return;
            }
            if (!mIsVip) {
                DialogConfirm.newInstance(mActivity, mActivity, DialogClickListener.ASK_DONATE,
                        Constants.EVENT_ACTION_DIALOG_FROM_NEW_GIF)
                        .show(getSupportFragmentManager(), "donate");
                return;
            }
            pausePreview();
            hideAllFloatControllers();
            exportVideo(false);
        }
    };

    View.OnClickListener onBtnSettingClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOpenLayoutSetting) {
                slideLayoutSettingIn(false);
            } else {
                hideAllFloatControllers();
                slideLayoutSettingIn(true);
            }
        }
    };

    View.OnClickListener onBtnUpgradeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            slideLayoutSettingIn(false);
            startDonate(Constants.EVENT_ACTION_DIALOG_FROM_WATERMARK);
        }
    };

    View.OnClickListener onBtnVolumeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int volume;
            if (mSelectedTL == TIMELINE_VIDEO) {
                volume = convertVolumeToInt(mSelectedVideoTL.volume);
                AnalyticsHelper.getInstance()
                        .send(mActivity, Constants.CATEGORY_VIDEO, Constants.ACTION_CHANGE_VOLUME_VIDEO);
            } else {
                volume = convertVolumeToInt(mSelectedAudioTL.volume);
                AnalyticsHelper.getInstance()
                        .send(mActivity, Constants.CATEGORY_AUDIO, Constants.ACTION_CHANGE_VOLUME_AUDIO);
            }
            VolumeEditor.newInstance(mActivity, volume).show(getFragmentManager(), "volume");
            pausePreview();
        }
    };

    View.OnClickListener onBtnAddNewProjectClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            createProject();
        }
    };

    View.OnClickListener onBtnCloseColorPickerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showColorPicker(false, true);
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

            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_TEXT, Constants.ACTION_CHANGE_TEXT_BACKGROUND);
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

            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_TEXT, Constants.ACTION_CHANGE_TEXT_COLOR);
        }
    };

    View.OnClickListener onBtnPlayClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mActiveVideoView.isPlaying()) {
                mBtnPlay.setImageResource(R.drawable.ic_play);
                pausePreview();
            } else {
                hideAllFloatControllers();
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

    View.OnClickListener onTabLayoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view.equals(mVideoTabLayout)) selectTabLayout(VIDEO_TAB);
            if (view.equals(mImageTabLayout)) selectTabLayout(IMAGE_TAB);
            if (view.equals(mAudioTabLayout)) selectTabLayout(AUDIO_TAB);
            if (view.equals(mAcceptTabLayout)) selectTabLayout(ACCEPT_TAB);
        }
    };

    View.OnClickListener onBtnAddClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mOpenLayoutAdd) {
                slideLayoutAddIn(false);
            } else {
                hideAllFloatControllers();
                pausePreview();
                slideLayoutAddIn(true);
            }
        }
    };

    View.OnClickListener onBtnBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackClick();
        }
    };

    View.OnClickListener onBtnRecentProject = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            slideLayoutSettingIn(false);
            FragmentManager fragmentManager = getSupportFragmentManager();
            ProjectListDialog dialog = ProjectListDialog.newInstance(mProjectId);
            dialog.show(fragmentManager, RECENT_PROJECT_LIST_FG);
        }
    };

    ViewPager.OnPageChangeListener onViewPagerChanged = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mFragmentCode = position;
            setHighLightTab(position);
            switch (position) {
                case VIDEO_TAB:
                    setFolderName(mFragmentVideosGallery.mFolderName);
                    setBtnUpLevelVisible(
                            mFragmentVideosGallery.galleryState != GalleryState.VIDEO_FOLDER);
                    break;
                case IMAGE_TAB:
                    setFolderName(mFragmentImagesGallery.mFolderName);
                    setBtnUpLevelVisible(
                            mFragmentImagesGallery.galleryState != GalleryState.IMAGE_FOLDER);
                    break;
                case AUDIO_TAB:
                    setFolderName(mFragmentAudioGallery.mFolderName);
                    setBtnUpLevelVisible(
                            mFragmentAudioGallery.galleryState != GalleryState.AUDIO_FOLDER);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    View.OnLongClickListener onVideoLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            mSelectedVideoTL = (VideoTL) view;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
            mDragCode = DRAG_VIDEO;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(clipData, shadowBuilder, view, 0);
            } else {
                view.startDrag(clipData, shadowBuilder, view, 0);
            }

            addShadowToLayoutVideo();
            mLayoutScrollView.setOnDragListener(onVideoDragListener);
            invisibleAllController();
            mFirstAnchor = true;
            mDragAnchor = 200;

            AnalyticsHelper.getInstance().send(mActivity, Constants.CATEGORY_VIDEO, Constants.ACTION_DRAG_VIDEO);
            return false;
        }
    };

    View.OnClickListener onExtraTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            pausePreview();
            mSelectedExtraTL = (ExtraTL) view;
            setExtraControlVisible(true);
            setLayoutAddVisible(false);
            slideExtraToolsIn(true);
            mExtraTLControl.restoreTimeLineStatus(mSelectedExtraTL);
            reAddExtraControl();
            scrollTo(mSelectedExtraTL.startInTimeLineMs);
            mScrollChangedListener.onScrollChanged();

            mSelectedTL = TIMELINE_EXTRA;
            if (mSelectedExtraTL.isImage) {
                setFloatImageVisible(mSelectedExtraTL);
                setFloatTextVisible(null);
                setBtnEditVisible(false);
            } else {
                setFloatImageVisible(null);
                setFloatTextVisible(mSelectedExtraTL);
                setBtnEditVisible(true);
                setBtnTrimVisible(false);
            }
            setBtnTrimVisible(false);
            setBtnDeleteVisible(true);
            setBtnVolumeVisible(false);
            setBtnCropVisible(false);
            unhighlightVideoTL();
        }
    };

    View.OnClickListener onAudioTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSelectedAudioTL = (AudioTL) view;
            setAudioControlVisible(true);
            setLayoutAddVisible(false);
            slideExtraToolsIn(true);
            mAudioTLControl.restoreTimeLineStatus(mSelectedAudioTL);
            mSelectedTL = TIMELINE_AUDIO;
            setBtnDeleteVisible(true);
            setBtnVolumeVisible(true);
            setBtnEditVisible(false);
            setBtnCropVisible(false);
            setBtnTrimVisible(false);
            setFloatImageVisible(null);
            setFloatTextVisible(null);
            unhighlightVideoTL();
            pausePreview();
        }
    };

    View.OnClickListener onBtnUpLevelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            upLevelFileManager();
        }
    };

    View.OnClickListener onVideoTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSelectedVideoTL = (VideoTL) view;
            log("mSelectedVideoTL PROJECT_ID = " + mVideoList.indexOf(mSelectedVideoTL));
            log("mCurrentId = " + mCurrentVideoId);
            if (mSelectedVideoTL.isHighLight) {
                unSelectVideoTL();
            } else {
                if (!mSelectedVideoTL.isExists) {
                    highlightSelectedVideoTL();
                    setBtnDeleteVisible(true);
                    setBtnEditVisible(false);
                    setBtnTrimVisible(false);
                    return;
                }
                selectVideoTL();
                setExtraControlVisible(false);
                setAudioControlVisible(false);
                setLayoutAddVisible(false);
                pausePreview();
            }
        }
    };

    View.OnClickListener onBtnsAddMediaClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initFileManager();
            openFileManager(true);
            setLayoutAddVisible(false);
            setBtnExportVisible(false);
            if (v.equals(mBtnAddVideo)) selectTabLayout(VIDEO_TAB);
            else if (v.equals(mBtnAddImage)) selectTabLayout(IMAGE_TAB);
            else if (v.equals(mBtnAddAudio)) selectTabLayout(AUDIO_TAB);
        }
    };

    View.OnClickListener onBtnAddTextClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addText();
            setLayoutAddVisible(false);
        }
    };

    View.OnClickListener onBtnDeleteClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSelectedTL == TIMELINE_VIDEO) {
                DialogConfirm.newInstance(mActivity, mActivity, DialogClickListener.DELETE_VIDEO, "")
                        .show(mActivity.getSupportFragmentManager(), "delete video");
            } else if (mSelectedTL == TIMELINE_EXTRA) {
                int type = mSelectedExtraTL.isImage ? DialogClickListener.DELETE_IMAGE
                        : DialogClickListener.DELETE_TEXT;
                DialogConfirm.newInstance(mActivity, mActivity, type, "")
                        .show(mActivity.getSupportFragmentManager(), "delete extra");
            } else {
                DialogConfirm.newInstance(mActivity, mActivity, DialogClickListener.DELETE_AUDIO, "")
                        .show(mActivity.getSupportFragmentManager(), "delete audio");
            }
        }
    };

    ViewTreeObserver.OnGlobalLayoutListener onLayoutImageCreated =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mLayoutImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mTimeLineImageHeight = mLayoutImage.getHeight() - 10;
                    addExtraNAudioController();
                    log("onLayoutImageCreated");
                }
            };

    ViewTreeObserver.OnGlobalLayoutListener onVideoViewLayoutCreated =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mVideoViewLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int[] point = new int[2];
                    mVideoViewLayout.getLocationOnScreen(point);
                    mVideoViewLeft = point[0];
                    log("onVideoViewLayoutCreated");
                }
            };

    ViewTreeObserver.OnGlobalLayoutListener onLayoutTimeLineCreated =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mLayoutTimeLine.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mLeftMarginTimeLine = mLayoutTimeLine.getWidth() / 2 - Utils.dpToPixel(mActivity, 45);
                    updateLayoutTimeLine();
                    setTimeMark();
                    log("onLayoutTimeLineCreated");
                }
            };

    ViewTreeObserver.OnGlobalLayoutListener onLayoutVideoCreated =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mLayoutVideo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mTimeLineVideoHeight = mLayoutVideo.getHeight() - 10;
                    new Timer().schedule(new DelayTask(), 100);
                    log("onLayoutVideoCreated");
                }
            };
}
