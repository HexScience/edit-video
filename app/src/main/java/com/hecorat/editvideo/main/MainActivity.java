package com.hecorat.editvideo.main;

import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.bumptech.glide.Glide;
import com.hecorat.editvideo.R;
import com.hecorat.editvideo.addimage.FloatImage;
import com.hecorat.editvideo.addtext.AlphaColorDrawable;
import com.hecorat.editvideo.addtext.ColorPickerView;
import com.hecorat.editvideo.addtext.FloatText;
import com.hecorat.editvideo.addtext.FontAdapter;
import com.hecorat.editvideo.addtext.FontManager;
import com.hecorat.editvideo.audio.AddSilentAudoTask;
import com.hecorat.editvideo.audio.VolumeEditor;
import com.hecorat.editvideo.export.ExportFragment;
import com.hecorat.editvideo.filemanager.FragmentAudioGallery;
import com.hecorat.editvideo.filemanager.FragmentImagesGallery;
import com.hecorat.editvideo.filemanager.FragmentVideosGallery;
import com.hecorat.editvideo.helper.Utils;
import com.hecorat.editvideo.preview.CustomVideoView;
import com.hecorat.editvideo.timeline.AudioTL;
import com.hecorat.editvideo.timeline.AudioTLControl;
import com.hecorat.editvideo.timeline.BigTimeMark;
import com.hecorat.editvideo.timeline.CustomHorizontalScrollView;
import com.hecorat.editvideo.timeline.ExtraTL;
import com.hecorat.editvideo.timeline.ExtraTLControl;
import com.hecorat.editvideo.timeline.SmallTimeMark;
import com.hecorat.editvideo.timeline.TimeText;
import com.hecorat.editvideo.timeline.VideoTL;
import com.hecorat.editvideo.timeline.VideoTLControl;
import com.hecorat.editvideo.video.TrimFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements VideoTLControl.OnControlTimeLineChanged,
        ExtraTLControl.OnExtraTimeLineControlChanged, AudioTLControl.OnAudioControlTimeLineChanged,
        ColorPickerView.OnColorChangedListener {
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
    private ImageView mBtnBold, mBtnItalic, mBtnTextColor, mBtnTextBgrColor;
    private LinearLayout mLayoutBtnBold, mLayoutBtnItalic;
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

    private Thread mThreadPreviewVideo;
    public ArrayList<VideoTL> mVideoList;
    public ArrayList<ExtraTL> mImageList;
    public ArrayList<ExtraTL> mTextList;
    public ArrayList<AudioTL> mAudioList;
    private ArrayList<ExtraTL> mListInLayoutImage, mListInLayoutText;
    public ArrayList<String> mFontPath, mFontName;
    public String mVideoPath, mImagePath, mAudioPath;
    private AudioManager mAudioManager;

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
    private CustomHorizontalScrollView mScrollView;
    public ExportFragment mExportFragment;
    public TrimFragment mTrimFragment;

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
    private int mMaxTimeLine;
    public int mLeftMarginTimeLine = Constants.MARGIN_LEFT_TIME_LINE;
    private boolean mScroll;
    private boolean mOpenLayoutAdd, mOpenLayoutEditText;
    private boolean mStyleBold, mStyleItalic;
    private boolean mShowColorPicker, mChooseTextColor;
    private int mSelectedTL;
    private int mSeekTimeAudio;
    private int mSystemVolume;
    public boolean mFinishExport;
    public float mVideoViewLeft;
    public boolean mFoundImage, mFoundText;

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
        mBtnBold = (ImageView) findViewById(R.id.btn_bold);
        mBtnItalic = (ImageView) findViewById(R.id.btn_italic);
        mLayoutBtnBold = (LinearLayout) findViewById(R.id.layout_btn_bold);
        mLayoutBtnItalic = (LinearLayout) findViewById(R.id.layout_btn_italic);
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
        mBtnAddMedia.setOnClickListener(onBtnAddMediaClick);
        mBtnAddText.setOnClickListener(onBtnAddTextClick);
        mBtnEditText.setOnClickListener(onBtnEditClick);
        mLayoutBtnBold.setOnClickListener(onLayoutBtnBold);
        mLayoutBtnItalic.setOnClickListener(onLayoutBtnItalic);
        mLayoutBtnTextColor.setOnClickListener(onLayoutBtnTextColorClick);
        mLayoutBtnTextBgrColor.setOnClickListener(onLayoutBtnTextBgrColorClick);
        mBtnCloseColorPicker.setOnClickListener(onBtnCloseColorPickerClick);
        mBtnVolume.setOnClickListener(onBtnVolumeClick);
        mLayoutAdd.setOnClickListener(onLayoutAddClick);

        mEditText.setOnEditorActionListener(onEditTextActionListener);
        mEdtColorHex.setOnEditorActionListener(onEditColorActionListener);

        mTimeLineImage.setOnDragListener(onExtraDragListener);
        mTimeLineVideo.setOnDragListener(onExtraDragListener);
        mTimeLineText.setOnDragListener(onExtraDragListener);

        mCurrentVideoId = -1;
        mMaxTimeLine = 0;
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

        mFontPath = FontManager.getFontPaths();
        mFontName = FontManager.getFontName();
        mFontAdapter = new FontAdapter(this, android.R.layout.simple_spinner_item, mFontName);
        mFontSpinner.setAdapter(mFontAdapter);
        mFontSpinner.setOnItemSelectedListener(onFontSelectedListener);

        mMediaPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        saveSystemVolume();
        prepareLayoutAnimationAddFile();
    }

    private void startThreadPreview() {
        mRunThread = true;
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

    private void setBtnVolumeVisible(boolean visible) {
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

    View.OnClickListener onLayoutBtnBold = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mStyleBold) {
                setBoldStyle(false);
            } else {
                setBoldStyle(true);
            }
        }
    };

    private void setBoldStyle(boolean bold) {
        int color = bold ? Color.DKGRAY : Color.TRANSPARENT;
        mBtnBold.setBackgroundColor(color);
        mStyleBold = bold;
        updateTextStyle();
    }

    private void updateTextStyle() {
        FloatText floatText = mSelectedExtraTL.floatText;
        if (mStyleBold) {
            if (mStyleItalic) {
                floatText.setStyle(Typeface.BOLD_ITALIC);
            } else {
                floatText.setStyle(Typeface.BOLD);
            }
        } else {
            if (mStyleItalic) {
                floatText.setStyle(Typeface.ITALIC);
            } else {
                floatText.setStyle(Typeface.NORMAL);
            }
        }
    }

    private void setItalicStyle(boolean italic) {
        int color = italic ? Color.DKGRAY : Color.TRANSPARENT;
        mBtnItalic.setBackgroundColor(color);
        mStyleItalic = italic;
        updateTextStyle();
    }

    View.OnClickListener onLayoutBtnItalic = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mStyleItalic) {
                setItalicStyle(false);
            } else {
                setItalicStyle(true);
            }
        }
    };

    AdapterView.OnItemSelectedListener onFontSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String font = mFontPath.get(position);
            if (mSelectedExtraTL != null) {
                mSelectedExtraTL.floatText.setFont(font);
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
                    openLayoutEditText(true);
                }
            }
        }
    };

    private void openLayoutTrimVideo() {
        setLayoutFragmentVisible(true);
        setActiveVideoViewVisible(false);
        mTrimFragment = TrimFragment.newInstance(mActivity, mSelectedVideoTL);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment, mTrimFragment).commit();
    }

    public void setActiveVideoViewVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mActiveVideoView.setVisibility(visibility);
    }

    public void onTrimVideoCompleted(int startTime, int endTime) {
        mSelectedVideoTL.drawTimeLineWith(startTime, endTime);
        getLeftMargin(mCountVideo - 1);
        mMaxTimeLine = mVideoList.get(mCountVideo - 1).endInTimeLine;
        scrollTo(mSelectedVideoTL.startInTimeLine);
        mActiveVideoView.seekTo(mSelectedVideoTL.startTime);
    }

    public void setLayoutFragmentVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mLayoutFragment.setVisibility(visibility);
    }

    public void openLayoutEditText(boolean open) {
        int visible = open ? View.VISIBLE : View.GONE;
        mLayoutEditText.setVisibility(visible);
        slideEditText(open);
        mOpenLayoutEditText = open;
        if (open) {
            mEditText.setText(mSelectedExtraTL.text);
            FloatText floatText = mSelectedExtraTL.floatText;
            mBtnTextColor.setBackground(new AlphaColorDrawable(floatText.mColor));
            mBtnTextBgrColor.setBackground(new AlphaColorDrawable(floatText.mBackgroundColor));
        } else {
            showColorPicker(false, false);
        }
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
            openFileManager(true);
            openLayoutAdd(false);

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
                deleteVideo();
            } else if (mSelectedTL == TIMELINE_EXTRA) {
                deleteExtraTimeline();
            } else {
                deleteAudioTimeLine();
            }
            updateBtnExportVisible();
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
            mVideoViewLayout.removeView(mSelectedExtraTL.floatImage);
        } else {
            mTextList.remove(mSelectedExtraTL);
            mVideoViewLayout.removeView(mSelectedExtraTL.floatText);
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
        invisibleVideoControl();
        setBtnEditVisible(false);
        mCountVideo--;
        mCurrentVideoId--;
        if (mCountVideo > 0) {
            getLeftMargin(mCountVideo - 1);
            mMaxTimeLine = mVideoList.get(mCountVideo - 1).endInTimeLine;
            if (mCurrentVideoId < 0) {
                mCurrentVideoId = 0;
            }
            mSelectedVideoTL = mVideoList.get(mCurrentVideoId);
            mActiveVideoView.setVideoPath(mSelectedVideoTL.videoPath);
            mScrollView.scrollTo(mSelectedVideoTL.startInTimeLine / Constants.SCALE_VALUE, 0);
        } else {
            mMaxTimeLine = 0;
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
            exportVideo();
        }
    };

    public void exportVideo() {
        mExportFragment = ExportFragment.newInstance(mActivity);
        mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment, mExportFragment).commit();
        setLayoutFragmentVisible(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopThreadPreview();
        stopMediaPlayer();
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

        for (int i = 0; i < mAudioList.size(); i++) {
            AudioTL audio = mAudioList.get(i);

            if (mTLPositionInMs >= audio.startInTimeline && mTLPositionInMs <= audio.endInTimeline) {
                if (!audio.equals(mCurrentAudioTL)) {
                    mCurrentAudioTL = audio;
                    changeAudio(audio, audio.startTime);
                    return;
                } else {
                    mSeekTimeAudio = mTLPositionInMs - audio.startInTimeline + audio.startTime;
                    if (mMediaPlayer == null) {
                        changeAudio(audio, mSeekTimeAudio);
                    }
                }
                return;
            }
        }
        stopMediaPlayer();
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
            if (mTLPositionInMs >= image.startInTimeLine && mTLPositionInMs <= image.endInTimeLine) {
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
            if (mTLPositionInMs >= text.startInTimeLine && mTLPositionInMs <= text.endInTimeLine) {
                floatText.setVisibility(View.VISIBLE);
            } else {
                floatText.setVisibility(View.GONE);
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
                        if (mTLPositionInMs >= videoTL.startInTimeLine && mTLPositionInMs <= videoTL.endInTimeLine) {
                            timelineId = i;
                            break;
                        }
                    }
                    int positionInVideo;
                    if (timelineId > 0) {
                        VideoTL previousTimeLine = mVideoList.get(timelineId - 1);
                        positionInVideo = mTLPositionInMs - previousTimeLine.endInTimeLine + videoTL.startTime;
                    } else {
                        positionInVideo = mTLPositionInMs + videoTL.startTime;
                    }

                    if (mCurrentVideoId != timelineId && videoTL != null) {
                        mCurrentVideoId = timelineId;
                        mCurrentVideoTL = videoTL;
                        mActiveVideoView.setVideoPath(videoTL.videoPath);
                    }

                    mActiveVideoView.seekTo(positionInVideo);

                    updateMediaPlayer();
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
        if (mTLPositionInMs >= mMaxTimeLine) {
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
            if (currentTime < currentTimeLine.startTime) {
                currentTime = currentTimeLine.startTime;
            }
            mTLPositionInMs += currentTime - currentTimeLine.startTime + 50;
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
        if (mTLPositionInMs >= mMaxTimeLine) {
            mPreviewStatus = END;
        }
    }

    private void updateVideoView() {
        if (mCountVideo < 1){
            return;
        }
        if (mTLPositionInMs >= mMaxTimeLine) {
            pausePreview();
            return;
        }

        VideoTL lastVideo = mVideoList.get(mCountVideo-1);
        int halfTimeLastVideo = (lastVideo.startInTimeLine+lastVideo.endInTimeLine)/2;
        if (mTLPositionInMs > halfTimeLastVideo  && !mActiveVideoView.isPlaying()){
            stopThreadPreview();
            return;
        }

        updateVideoVolume();

        prepareNextVideo();

        VideoTL videoTL = null;
        int timelineId = mCurrentVideoId;
        for (int i = 0; i < mVideoList.size(); i++) {
            videoTL = mVideoList.get(i);
            if (mTLPositionInMs >= videoTL.startInTimeLine && mTLPositionInMs <= videoTL.endInTimeLine) {
                timelineId = i;
                break;
            }
        }

        int halfTimeVideo = (videoTL.startInTimeLine+videoTL.endInTimeLine)/2;

        if (mRunThread && timelineId<mCountVideo-1 && !mActiveVideoView.isPlaying()
                && mTLPositionInMs > halfTimeVideo){
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
                mActiveVideoView.setVideoPath(videoTL.videoPath);
            }
            mActiveVideoView.seekTo(videoTL.startTime);
            mActiveVideoView.start();
            updateSystemVolume();
        }
    }

    private void prepareNextVideo() {
        if (mCurrentVideoId != -1 && mCurrentVideoId < mCountVideo - 1) {
            VideoTL nextVideoTL = mVideoList.get(mCurrentVideoId + 1);
            mInActiveVideoView.setVideoPath(nextVideoTL.videoPath);
            mInActiveVideoView.seekTo(10);
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
                mBtnPlay.setImageResource(R.drawable.ic_pause);
                openFileManager(false);
                startThreadPreview();
                startPreview();
                hideAllFloatNControlers();
            }
        }
    };

    private void hideAllFloatNControlers(){
        setFloatImageVisible(null);
        setFloatTextVisible(null);
        setAudioControlVisible(false);
        setExtraControlVisible(false);
        setBtnDeleteVisible(false);
        setBtnEditVisible(false);
        setBtnVolumeVisible(false);
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

    private void addVideoTL() {
        VideoTL videoTL = new VideoTL(this, mVideoPath, mTimeLineVideoHeight);
        videoTL.setOnClickListener(onVideoTimeLineClick);
        videoTL.setOnLongClickListener(onVideoLongClick);

        mVideoList.add(videoTL);
        mLayoutVideo.addView(videoTL);
        mCountVideo++;
        getLeftMargin(mCountVideo - 1);

        mMaxTimeLine = videoTL.endInTimeLine;
        mTLPositionInMs = videoTL.startInTimeLine;
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
        highlightSelectedVideoTL();
        mSelectedTL = TIMELINE_VIDEO;
    }

    private void scrollTo(int time){
        mScrollView.scrollTo(time/Constants.SCALE_VALUE, 0);
    }

    private void fixIfVideoHasNoAudio(VideoTL videoTL) {
        new AddSilentAudoTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, videoTL);
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
        mVideoViewLayout.addView(floatImage);
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

    private void addTextTL() {
        String text = "Lai Trung Tien";
        int leftMargin = mLeftMarginTimeLine + mScrollView.getScrollX();
        ExtraTL extraTL = new ExtraTL(this, text, mTimeLineImageHeight, leftMargin, false);
        extraTL.setOnClickListener(onExtraTimeLineClick);
        extraTL.setOnLongClickListener(onExtraTimelineLongClick);
        addExtraTLToTL(extraTL, leftMargin);
        mTextList.add(extraTL);
        mSelectedExtraTL = extraTL;

        FloatText floatText = new FloatText(this, text);
        mVideoViewLayout.addView(floatText);
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
        readdExtraControl();
        mSelectedExtraTL = extraTL;
    }

    private void readdExtraControl() {
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

    public void addAudioTL() {
        int leftMargin = mLeftMarginTimeLine + mScrollView.getScrollX();
        AudioTL audioTL = new AudioTL(this, mAudioPath, mTimeLineImageHeight, leftMargin);
        addAudioTLToTL(audioTL);
        audioTL.setOnClickListener(onAudioTimeLineClick);
        audioTL.setOnLongClickListener(onAudioLongClick);
        mSelectedAudioTL = audioTL;

        updateBtnExportVisible();
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
        mMediaPlayer = MediaPlayer.create(this, Uri.parse(audio.audioPreview));
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
                openLayoutAdd(true);
                pausePreview();
            }
        }
    };

    private void openFileManager(boolean open) {
        if (open) {
            mFileManager.setVisibility(View.VISIBLE);
            mOpenFileManager = true;
            mBtnAdd.setImageResource(R.drawable.ic_close);
        } else {
            mFileManager.setVisibility(View.GONE);
            mOpenFileManager = false;
            mBtnAdd.setImageResource(R.drawable.ic_add_media);
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
                    return true;
                }
                break;
            case IMAGE_TAB:
                if (mOpenImageSubFolder) {
                    mFragmentImagesGallery.backToMain();
                    mOpenImageSubFolder = false;
                    return true;
                }
                break;
            case AUDIO_TAB:
                if (mOpenAudioSubFolder) {
                    mFragmentAudioGallery.backToMain();
                    mOpenAudioSubFolder = false;
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
                        mScrollView.scrollTo(mSelectedVideoTL.startInTimeLine / Constants.SCALE_VALUE, 0);
                        mActiveVideoView.setVideoPath(mSelectedVideoTL.videoPath);
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
            unSelectVideoTL();
            mAudioTLControl.restoreTimeLineStatus(mSelectedAudioTL);
            mSelectedTL = TIMELINE_AUDIO;
            setBtnDeleteVisible(true);
            setBtnVolumeVisible(true);
            setBtnEditVisible(false);
            setFloatImageVisible(null);
            setFloatTextVisible(null);

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

    @Override
    public void onBackPressed() {
        hideStatusBar();
        if (onBackClick()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean onBackClick() {
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

        if (mSelectedTL == TIMELINE_VIDEO) {
            unSelectVideoTL();
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
            unSelectVideoTL();
            mExtraTLControl.restoreTimeLineStatus(mSelectedExtraTL);
            readdExtraControl();
            scrollTo(mSelectedExtraTL.startInTimeLine);
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
            }
            setBtnDeleteVisible(true);
            setBtnVolumeVisible(false);

        }
    };

    public void cancelEditText() {
        setBtnEditVisible(false);
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
        for (int i = 0; i < mTextList.size(); i++) {
            FloatText floatText = mTextList.get(i).floatText;
            if (floatText.getVisibility() == View.VISIBLE) {
                if (x >= floatText.xMin && x <= floatText.xMax
                        && y >= floatText.yMin && y <= floatText.yMax) {
                    floatText.drawBorder(true);
                    mSelectedExtraTL = mTextList.get(i);
                    mFoundText = true;
                } else {
                    floatText.drawBorder(false);
                }
            }
        }
        if (!mFoundText) {
            if (!mFoundImage) {
                setExtraControlVisible(false);
                cancelEditText();
                setBtnDeleteVisible(false);
            }
        } else {
            setExtraControlVisible(true);
            restoreExtraControl(mSelectedExtraTL);
            setBtnDeleteVisible(true);
            setBtnEditVisible(true);
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
                setBtnDeleteVisible(false);
            }
        } else {
            setExtraControlVisible(true);
            restoreExtraControl(mSelectedExtraTL);
            setBtnDeleteVisible(true);
        }
    }

    public void setExtraControlVisible(boolean visible) {
        if (visible) {
            mExtraTLControl.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
            mVideoTLControl.setVisibility(View.GONE);
            mAudioTLControl.setVisibility(View.GONE);
        } else {
            mExtraTLControl.setVisibility(View.GONE);
            mScrollView.scroll = true;
            if (mSelectedExtraTL != null) {
                if (mSelectedExtraTL.isImage) {
                    mSelectedExtraTL.floatImage.drawBorder(false);
                } else {
                    mSelectedExtraTL.floatText.drawBorder(false);
                }
            }
        }
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
                selectVideoTL();
                setExtraControlVisible(false);
                setAudioControlVisible(false);
                pausePreview();
            }
        }
    };

    private void selectVideoTL() {
        mSelectedTL = TIMELINE_VIDEO;
        highlightSelectedVideoTL();
//        if (!mSelectedVideoTL.equals(mCurrentVideoTL)) {
//            mScrollView.scrollTo(mSelectedVideoTL.startInTimeLine / Constants.SCALE_VALUE, 0);
//            onCustomScrollChanged.onScrollChanged();
//        }
        setBtnDeleteVisible(true);
        setBtnVolumeVisible(true);
        setBtnEditVisible(true);
        setFloatTextVisible(null);
        setFloatImageVisible(null);
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
        setBtnDeleteVisible(false);
        setBtnVolumeVisible(false);
        setBtnEditVisible(false);
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

    @Override
    public void updateVideoTimeLine(int leftPosition, int width) {
        mSelectedVideoTL.drawTimeLine(leftPosition, width);
        getLeftMargin(mCountVideo - 1);
        mMaxTimeLine = mVideoList.get(mCountVideo - 1).endInTimeLine;
    }

    private void log(String msg) {
        Log.e("Edit video", msg);
    }

    @Override
    public void invisibleVideoControl() {
        setVideoControlVisible(false);
        setBtnDeleteVisible(false);
        setBtnExportVisible(true);
        setBtnVolumeVisible(false);
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
        int height = (int) (Utils.getScreenWidth() * 0.6);
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
        setBtnDeleteVisible(false);
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
        setBtnDeleteVisible(false);
        setBtnVolumeVisible(false);
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
