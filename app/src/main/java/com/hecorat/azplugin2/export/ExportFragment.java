package com.hecorat.azplugin2.export;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.helper.AnalyticsHelper;
import com.hecorat.azplugin2.helper.NotificationHelper;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.main.Constants;
import com.hecorat.azplugin2.main.MainActivity;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by Bkmsx on 12/8/2016.
 */

public class ExportFragment extends Fragment{
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

    public String mVideoPath;

    private boolean mStop;
    public boolean mExporting;

    public static ExportFragment newInstance(MainActivity activity) {
        ExportFragment exportFragment = new ExportFragment();
        exportFragment.mActivity = activity;
        return exportFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.export_fragment, container, false);
        mCircleProgressBar = (CircleProgressView) view.findViewById(R.id.export_progress);
        mBtnBack = (Button) view.findViewById(R.id.btn_back);
        mBtnExport = (Button) view.findViewById(R.id.btn_export);
        mLayoutProgress = (LinearLayout) view.findViewById(R.id.layout_export_progress);
        mLayoutQuality = (LinearLayout) view.findViewById(R.id.layout_choose_quality);
        mEditText = (EditText) view.findViewById(R.id.edt_output_name);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.quality_groupradio);
        mLayoutAfterExport = (LinearLayout) view.findViewById(R.id.layout_after_export);
        mBtnBackAfterExport = (Button) view.findViewById(R.id.btn_back_after_export);
        mBtnWatchVideo = (Button) view.findViewById(R.id.btn_watch_video);
        mBtnCancel = (Button) view.findViewById(R.id.btn_cancel_export);
        mTextViewTip = (TextView) view.findViewById(R.id.textview_tip);
        mBtnShare = (Button) view.findViewById(R.id.btn_share);
        mBtnBackCancel = (Button) view.findViewById(R.id.btn_back_cancel);

        mEditText.setText(mActivity.mProjectName);

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
        return view;
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
            intent.setDataAndType(Uri.parse(mVideoPath), "video/mp4");
            mActivity.startActivity(intent);

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
            mLayoutQuality.setVisibility(View.GONE);
            mLayoutProgress.setVisibility(View.VISIBLE);
            exportVideo();
            mExporting = true;
        }
    };

    private void exportVideo(){
        int id = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) mLayoutQuality.findViewById(id);
        int quality = Integer.parseInt(radioButton.getTag().toString());
        String name = mEditText.getText().toString();
        mVideoPath = Utils.getOutputFolder()+"/"+name + ".mp4";

        ExportTask exportTask = new ExportTask(mActivity, mActivity.mVideoList, mActivity.mImageList,
                mActivity.mTextList, mActivity.mAudioList, name, quality);
        exportTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        }
    }
}
