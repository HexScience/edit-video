package com.hecorat.azplugin2.export;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.dialogfragment.DialogConfirm;
import com.hecorat.azplugin2.helper.AnalyticsHelper;
import com.hecorat.azplugin2.helper.NotificationHelper;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.interfaces.DialogClickListener;
import com.hecorat.azplugin2.main.Constants;
import com.hecorat.azplugin2.main.MainActivity;

import java.io.File;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by Bkmsx on 12/8/2016.
 */

public class ExportFragment extends Fragment implements DialogClickListener{
    MainActivity mActivity;

    public Button mBtnBack, mBtnExport;
    public CircleProgressView mCircleProgressBar;
    public LinearLayout mLayoutQuality, mLayoutProgress;
    public EditText mEditText;
    public RadioGroup mRadioGroup;
    public Button mBtnBackAfterExport, mBtnWatchVideo;
    public LinearLayout mLayoutAfterExport;
    public TextView mTextViewTip;
    public Button mBtnShare, mBtnCancel, mBtnBackCancel;
    public View mView;
    public LinearLayout mLayoutQualityGif;
    public SeekBar mSeekbarFps, mSeekbarScale, mSeekbarLoop;
    public TextView mTextViewFps, mTextViewScale, mTextViewLoop;
    public TextView mTextViewNameTitle;
    public TextView mTextViewProcessTitle;
    private LinearLayout mLayoutQualityVideo;
    private CheckBox mCheckboxRatio;

    private ExportTask mExportTask;

    public String mOutputPath;
    private String[] mFilesInFolder;
    public String mExtension;

    private boolean mStop;
    public boolean mExporting;
    private boolean mExportVideo;

    public static ExportFragment newInstance(MainActivity activity, boolean exportVideo) {
        ExportFragment exportFragment = new ExportFragment();
        exportFragment.mActivity = activity;
        exportFragment.mExportVideo = exportVideo;
        exportFragment.inflateView();
        exportFragment.setOutputName();
        return exportFragment;
    }

    private void inflateView() {
        mView = LayoutInflater.from(mActivity).inflate(R.layout.export_fragment, null);
        mCircleProgressBar = (CircleProgressView) mView.findViewById(R.id.export_progress);
        mBtnBack = (Button) mView.findViewById(R.id.btn_back);
        mBtnExport = (Button) mView.findViewById(R.id.btn_export);
        mLayoutProgress = (LinearLayout) mView.findViewById(R.id.layout_export_progress);
        mLayoutQuality = (LinearLayout) mView.findViewById(R.id.layout_choose_quality);
        mEditText = (EditText) mView.findViewById(R.id.edt_output_name);
        mRadioGroup = (RadioGroup) mView.findViewById(R.id.quality_groupradio);
        mLayoutAfterExport = (LinearLayout) mView.findViewById(R.id.layout_after_export);
        mBtnBackAfterExport = (Button) mView.findViewById(R.id.btn_back_after_export);
        mBtnWatchVideo = (Button) mView.findViewById(R.id.btn_watch_video);
        mBtnCancel = (Button) mView.findViewById(R.id.btn_cancel_export);
        mTextViewTip = (TextView) mView.findViewById(R.id.textview_tip);
        mBtnShare = (Button) mView.findViewById(R.id.btn_share);
        mBtnBackCancel = (Button) mView.findViewById(R.id.btn_back_cancel);
        mLayoutQualityGif = (LinearLayout) mView.findViewById(R.id.layout_quality_gif);
        mSeekbarFps = (SeekBar) mView.findViewById(R.id.seekbar_fps);
        mSeekbarScale = (SeekBar) mView.findViewById(R.id.seekbar_scale);
        mSeekbarLoop = (SeekBar) mView.findViewById(R.id.seekbar_loop);
        mTextViewFps = (TextView) mView.findViewById(R.id.textview_fps);
        mTextViewScale = (TextView) mView.findViewById(R.id.textview_scale);
        mTextViewLoop = (TextView) mView.findViewById(R.id.textview_loop);
        mTextViewNameTitle = (TextView) mView.findViewById(R.id.name_title);
        mTextViewProcessTitle = (TextView) mView.findViewById(R.id.process_title);
        mLayoutQualityVideo = (LinearLayout) mView.findViewById(R.id.layout_quality_video);
        mCheckboxRatio = (CheckBox) mView.findViewById(R.id.checkbox_original_ratio);

        if (mExportVideo) {
            mLayoutQualityVideo.setVisibility(View.VISIBLE);
            mLayoutQualityGif.setVisibility(View.GONE);
            mBtnExport.setText(mActivity.getString(R.string.btn_export_video));
            mTextViewNameTitle.setText(mActivity.getString(R.string.video_name_title));
            boolean onlyOneVideo = mActivity.mVideoList.size() == 1 && mActivity.mImageList.isEmpty() &&
                        mActivity.mTextList.isEmpty() && mActivity.mAudioList.isEmpty();
            if (!onlyOneVideo) {
                mCheckboxRatio.setVisibility(View.GONE);
                setRadioGroupEnable(true);
            } else {
                mCheckboxRatio.setVisibility(View.VISIBLE);
                mCheckboxRatio.setChecked(true);
                mCheckboxRatio.setOnCheckedChangeListener(onCheckedChangeListener);
                setRadioGroupEnable(false);
            }
        } else {
            mLayoutQualityVideo.setVisibility(View.GONE);
            mLayoutQualityGif.setVisibility(View.VISIBLE);
            mBtnExport.setText(mActivity.getString(R.string.btn_export_gif));
            mTextViewNameTitle.setText(mActivity.getString(R.string.gif_name_title));
        }

        setProcessTitle("");

        mBtnExport.setOnClickListener(onBtnExportClick);
        mBtnBack.setOnClickListener(onBtnBackClick);
        mBtnBackAfterExport.setOnClickListener(onBtnBackClick);
        mEditText.setOnEditorActionListener(onNameEdited);
        mBtnWatchVideo.setOnClickListener(onBtnWatchVideoClick);
        mBtnShare.setOnClickListener(onBtnShareClick);
        mBtnCancel.setOnClickListener(onBtnCancelClick);
        mBtnBackCancel.setOnClickListener(onBtnBackClick);

        initSeekbar();

        mCircleProgressBar.setTextMode(TextMode.TEXT);
        setExportProgress(0);
    }

    private void setRadioGroupEnable(boolean enable) {
        for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
            mRadioGroup.getChildAt(i).setEnabled(enable);
        }
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            setRadioGroupEnable(!isChecked);
        }
    };

    public void setProcessTitle(String title) {
        mTextViewProcessTitle.setText(title);
    }

    private void initSeekbar() {
        if (mExportVideo) {
            return;
        }
        mSeekbarFps.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mSeekbarScale.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mSeekbarLoop.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mSeekbarFps.setMax(10);
        mSeekbarScale.setMax(480);
        mSeekbarLoop.setMax(11);

        int fpsProgress = Utils.getSharedPref(mActivity).getInt(mActivity.getString(R.string.pref_fps_gif), 5);
        int scaleProgress = Utils.getSharedPref(mActivity).getInt(mActivity.getString(R.string.pref_scale_gif), 200);
        int loopProgress = Utils.getSharedPref(mActivity).getInt(mActivity.getString(R.string.pref_loop_gif), 1);

        mSeekbarFps.setProgress(fpsProgress);
        mSeekbarScale.setProgress(scaleProgress);
        mSeekbarLoop.setProgress(loopProgress);
        setTextFps(fpsProgress);
        setTextScale(scaleProgress);
        setTextLoop(loopProgress);
    }

    private void setTextFps(int progress) {
        int fps = getFps(progress);
        mTextViewFps.setText(String.valueOf(fps));
    }

    private void setTextScale(int progress) {
        int scaleWidth = getScale(progress);
        int scaleHeight = scaleWidth * 9 / 16;
        String text = scaleWidth + " x " + scaleHeight;
        mTextViewScale.setText(text);
    }

    private void setTextLoop(int progress) {
        int loop = getLoop(progress);
        String text;
        if (loop == -1) {
            text = "No loop";
        } else if (loop == 0) {
            text = "Forever";
        } else {
            text = loop + "";
        }
        mTextViewLoop.setText(text);
    }

    private int getFps(int progress) {
        return  progress + 5;
    }

    private int getScale(int progress) {
        return progress + 120;
    }

    private int getLoop(int progress) {
        return progress - 1;
    }

    private void saveFps(int progress) {
        Utils.getSharedPref(mActivity).edit().putInt(mActivity.getString(R.string.pref_fps_gif), progress).apply();
    }

    private void saveScale(int progress) {
        Utils.getSharedPref(mActivity).edit().putInt(mActivity.getString(R.string.pref_scale_gif), progress).apply();
    }

    private void saveLoop(int progress) {
        Utils.getSharedPref(mActivity).edit().putInt(mActivity.getString(R.string.pref_loop_gif), progress).apply();
    }


    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar.equals(mSeekbarFps)) {
                setTextFps(progress);
            } else if (seekBar.equals(mSeekbarScale)) {
                setTextScale(progress);
            } else {
                setTextLoop(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (seekBar.equals(mSeekbarFps)) {
                saveFps(seekBar.getProgress());
            } else if (seekBar.equals(mSeekbarScale)) {
                saveScale(seekBar.getProgress());
            } else {
                saveLoop(seekBar.getProgress());
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mView;
    }

    private void setOutputName() {
        if (mExportVideo) {
            mExtension = ".mp4";
        } else {
            mExtension = ".gif";
        }
        String outputName = mActivity.mProjectName + Utils.getNameExtension();
        String outName = outputName + mExtension;
        boolean unique;
        mFilesInFolder = new File(Utils.getOutputFolder()).list();
        do {
            unique = true;
            for (String fileName : mFilesInFolder) {
                if (fileName.equals(outName)) {
                    unique = false;
                    outputName += "_copy";
                    outName = outputName + mExtension;
                    break;
                }
            }
        } while (!unique);

        mEditText.setText(outputName);
    }

    private void log(String msg) {
        Log.e("Export Fragment", msg);
    }

    View.OnClickListener onBtnCancelClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FFmpeg.getInstance(mActivity).stop();
            mStop = true;

            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_EXPORT, Constants.ACTION_CANCEL_EXPORT);
        }
    };

    View.OnClickListener onBtnShareClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MediaScannerConnection.scanFile(mActivity, new String[] {mOutputPath}, null,
                    onScanCompletedListener);
            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_EXPORT, Constants.ACTION_SHARE_VIDEO);
        }
    };

    private void shareVideo(Uri uri) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String type = mExportVideo ?  "video/mp4" : "image/gif";
        sendIntent.setType(type);
        String subject = mExportVideo ? "Video" : "Gif";
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        String text = mExportVideo ? "Enjoy the Video" : "Enjoy the Gif";
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mActivity.startActivity(Intent.createChooser(sendIntent, "Email:"));
    }

    MediaScannerConnection.OnScanCompletedListener onScanCompletedListener = new MediaScannerConnection.OnScanCompletedListener() {

        @Override
        public void onScanCompleted(String path, Uri uri) {
            shareVideo(uri);
        }
    };

    View.OnClickListener onBtnWatchVideoClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File videoFile = new File(mOutputPath);
            if (mExportVideo) {
                intent.setDataAndType(Uri.fromFile(videoFile), "video/mp4");
            } else {
                intent.setDataAndType(Uri.fromFile(videoFile), "image/gif");
            }

            try {
                mActivity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mActivity, R.string.msg_need_install_video_player, Toast.LENGTH_LONG).show();
            }

            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_EXPORT, Constants.ACTION_WATCH_VIDEO);
        }
    };

    TextView.OnEditorActionListener onNameEdited = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mEditText.clearFocus();
//                mActivity.hideStatusBar();
            }
            return false;
        }
    };

    View.OnClickListener onBtnBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            backToEdit();
        }
    };

    public void backToEdit() {
        mActivity.setLayoutFragmentVisible(false);
        mLayoutQuality.setVisibility(View.VISIBLE);
        mLayoutProgress.setVisibility(View.GONE);
        setFragmentInvisible();
        mActivity.mOpenLayoutExport = false;
    }

    private void setFragmentInvisible(){
        View view = getView();
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    View.OnClickListener onBtnExportClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String name = mEditText.getText().toString() + mExtension;
            for (String fileName : mFilesInFolder) {
                if (name.equals(fileName)) {
                    DialogConfirm.newInstance(mActivity, ExportFragment.this, DialogClickListener.OVERWRITE_FILE, "")
                            .show(mActivity.getSupportFragmentManager(), "overwrite file");
                    return;
                }
            }
            startExport();
        }
    };

    private void startExport() {
        mLayoutQuality.setVisibility(View.GONE);
        mLayoutProgress.setVisibility(View.VISIBLE);
        mExporting = true;

        if (mExportVideo) {
            exportVideo();
        } else {
            exportGif();
        }
    }

    private void exportGif(){
        String name = mEditText.getText().toString();
        int quality = getScale(mSeekbarScale.getProgress());
        int fps = getFps(mSeekbarFps.getProgress());
        int loop = getLoop(mSeekbarLoop.getProgress());
        mOutputPath = Utils.getOutputFolder() + "/" + name + mExtension;
        mExportTask = new ExportTask(mActivity, mActivity.mVideoList, mActivity.mImageList, mActivity.mTextList,
                mActivity.mAudioList, name, quality, fps, loop);
        mExportTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onPositiveClick(int dialogId, String detail) {
        switch (dialogId) {
            case DialogClickListener.OVERWRITE_FILE:
//                mActivity.hideStatusBar();
                startExport();
                break;
        }
    }

    @Override
    public void onNegativeClick(int dialogId) {
        switch (dialogId) {
            case DialogClickListener.OVERWRITE_FILE:
//                mActivity.hideStatusBar();
                break;
        }
    }

    private void exportVideo(){
        int id = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) mLayoutQuality.findViewById(id);
        int quality = Integer.parseInt(radioButton.getTag().toString());
        String name = mEditText.getText().toString();
        mOutputPath = Utils.getOutputFolder()+"/"+name + mExtension;
        boolean keepRatio = mCheckboxRatio.isChecked();

        mExportTask = new ExportTask(mActivity, mActivity.mVideoList, mActivity.mImageList,
                mActivity.mTextList, mActivity.mAudioList, name, quality, keepRatio);
        mExportTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        AnalyticsHelper.getInstance()
                .send(mActivity, Constants.CATEGORY_EXPORT, Constants.ACTION_CLICK_EXPORT);
    }

    public void onBroadcastReceived(Intent intent, String action) {
        mExportTask.onBroadcastReceived(intent, action);
    }

    public void setExportProgress(int value) {
        mCircleProgressBar.setValue(value);
        if (value == 0) {
            if (mStop) {
                mCircleProgressBar.setText("Canceled!");
            } else {
                mCircleProgressBar.setText("Preparing..");
            }
            mCircleProgressBar.setTextSize(50);
        } else if (value == -1) {
            mCircleProgressBar.setText("Completed!");
            mCircleProgressBar.setTextSize(50);
            mCircleProgressBar.setValue(100);
        } else if (value < 100){
            mCircleProgressBar.setText(value+"%");
            mCircleProgressBar.setTextSize(70);

        } else {
            mCircleProgressBar.setText("Completing..");
            mCircleProgressBar.setTextSize(50);
        }
        if (mExporting) {
            NotificationHelper.updateNotification(mActivity, value, mOutputPath, mExportVideo);
        }
    }

    public void onExportCompleted() {
        mBtnCancel.setVisibility(View.GONE);
        mExporting = false;
        if (mStop) {
            setExportProgress(0);
            mBtnBackCancel.setVisibility(View.VISIBLE);
            NotificationHelper.cancelNotification(mActivity);
        } else {
            setExportProgress(-1);
            if (mExportVideo) {
                mBtnWatchVideo.setText(mActivity.getString(R.string.btn_watch_video));
            } else {
                mBtnWatchVideo.setText(mActivity.getString(R.string.btn_view_gif));
            }
            mLayoutAfterExport.setVisibility(View.VISIBLE);
            mTextViewTip.setVisibility(View.INVISIBLE);
            NotificationHelper.updateNotification(mActivity, -1, mOutputPath, mExportVideo);

            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_EXPORT, Constants.ACTION_EXPORT_SUCCESSFUL);
        }
    }
}
