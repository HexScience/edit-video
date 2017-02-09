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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    public String mVideoPath;
    private String[] mFilesInFolder;

    private boolean mStop;
    public boolean mExporting;

    public static ExportFragment newInstance(MainActivity activity) {
        ExportFragment exportFragment = new ExportFragment();
        exportFragment.mActivity = activity;
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

        mBtnExport.setOnClickListener(onBtnExportClick);
        mBtnBack.setOnClickListener(onBtnBackClick);
        mBtnBackAfterExport.setOnClickListener(onBtnBackClick);
        mEditText.setOnEditorActionListener(onNameEdited);
        mBtnWatchVideo.setOnClickListener(onBtnWatchVideoClick);
        mBtnShare.setOnClickListener(onBtnShareClick);
        mBtnCancel.setOnClickListener(onBtnCancelClick);
        mBtnBackCancel.setOnClickListener(onBtnBackClick);

        mCircleProgressBar.setTextMode(TextMode.TEXT);
        setExportProgress(0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mView;
    }

    private void setOutputName() {
        String outputName = mActivity.mProjectName;
        String outName = outputName + ".mp4";
        boolean unique;
        mFilesInFolder = new File(Utils.getOutputFolder()).list();
        do {
            unique = true;
            for (String fileName : mFilesInFolder) {
                if (fileName.equals(outName)) {
                    unique = false;
                    outputName += "_copy";
                    outName = outputName + ".mp4";
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
            MediaScannerConnection.scanFile(mActivity, new String[] { mVideoPath }, null,
                    onScanCompletedListener);
            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_EXPORT, Constants.ACTION_SHARE_VIDEO);
        }
    };

    private void shareVideo(Uri uri) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("video/mp4");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Video");
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Enjoy the Video");
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
            File videoFile = new File(mVideoPath);
            intent.setDataAndType(Uri.fromFile(videoFile), "video/mp4");
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
                mActivity.hideStatusBar();
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
            String name = mEditText.getText().toString() + ".mp4";
            for (String fileName : mFilesInFolder) {
                if (name.equals(fileName)) {
                    DialogConfirm.newInstance(mActivity, ExportFragment.this, DialogClickListener.OVERWRITE_FILE)
                            .show(mActivity.getSupportFragmentManager(), "overwrite file");
                    return;
                }
            }
            exportVideo();
        }
    };

    @Override
    public void onPositiveClick(int dialogId) {
        switch (dialogId) {
            case DialogClickListener.OVERWRITE_FILE:
                mActivity.hideStatusBar();
                exportVideo();
                break;
        }
    }

    @Override
    public void onNegativeClick(int dialogId) {
        switch (dialogId) {
            case DialogClickListener.OVERWRITE_FILE:
                mActivity.hideStatusBar();
                break;
        }
    }

    private void exportVideo(){
        mLayoutQuality.setVisibility(View.GONE);
        mLayoutProgress.setVisibility(View.VISIBLE);
        mExporting = true;

        int id = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) mLayoutQuality.findViewById(id);
        int quality = Integer.parseInt(radioButton.getTag().toString());
        String name = mEditText.getText().toString();
        mVideoPath = Utils.getOutputFolder()+"/"+name + ".mp4";

        ExportTask exportTask = new ExportTask(mActivity, mActivity.mVideoList, mActivity.mImageList,
                mActivity.mTextList, mActivity.mAudioList, name, quality);
        exportTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        AnalyticsHelper.getInstance()
                .send(mActivity, Constants.CATEGORY_EXPORT, Constants.ACTION_CLICK_EXPORT);
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
            NotificationHelper.updateNotification(mActivity, value, mVideoPath);
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
            mLayoutAfterExport.setVisibility(View.VISIBLE);
            mTextViewTip.setVisibility(View.INVISIBLE);
            NotificationHelper.updateNotification(mActivity, -1, mVideoPath);

            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_EXPORT, Constants.ACTION_EXPORT_SUCCESSFUL);
        }
    }
}
