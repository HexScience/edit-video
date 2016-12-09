package com.hecorat.editvideo.export;

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

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.helper.NotificationHelper;
import com.hecorat.editvideo.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by Bkmsx on 12/8/2016.
 */

public class ExportFragment extends Fragment{
    static MainActivity mActivity;

    public Button mBtnBack, mBtnExport;
    public CircleProgressView mCircleProgressBar;
    public LinearLayout mLayoutQuality, mLayoutProgress;
    public EditText mEditText;
    public RadioGroup mRadioGroup;
    public Button mBtnBackAfterExport, mBtnWatchVideo, mBtnCancel;
    public LinearLayout mLayoutAfterExport;
    public TextView mTextViewTip;

    public String mVideoPath;

    public static ExportFragment newInstance(MainActivity activity) {
        mActivity = activity;
        return new ExportFragment();
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

        String defaultName = new SimpleDateFormat("yy_MM_dd_HH_mm_ss", Locale.getDefault())
                .format(new Date(System.currentTimeMillis()));
        mEditText.setText(defaultName);

        mBtnExport.setOnClickListener(onBtnExportClick);
        mBtnBack.setOnClickListener(onBtnBackClick);
        mBtnBackAfterExport.setOnClickListener(onBtnBackClick);
        mEditText.setOnEditorActionListener(onNameEdited);

        mCircleProgressBar.setTextMode(TextMode.TEXT);
        setExportProgress(0);
        return view;
    }

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
            mActivity.setLayoutFragmentVisible(false);
            mLayoutQuality.setVisibility(View.VISIBLE);
            mLayoutProgress.setVisibility(View.GONE);
            setFragmentInvisible();
        }
    };

    private void setFragmentInvisible(){
        getView().setVisibility(View.GONE);
    }

    View.OnClickListener onBtnExportClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mLayoutQuality.setVisibility(View.GONE);
            mLayoutProgress.setVisibility(View.VISIBLE);
            exportVideo();
        }
    };

    private void exportVideo(){
        int id = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) mLayoutQuality.findViewById(id);
        int quality = Integer.parseInt(radioButton.getTag().toString());
        String name = mEditText.getText().toString();
        ExportTask exportTask = new ExportTask(mActivity, mActivity.mVideoList, mActivity.mImageList,
                mActivity.mTextList, mActivity.mAudioList, name, quality);
        exportTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setExportProgress(int value) {
        mCircleProgressBar.setValue(value);
        if (value == 0) {
            mCircleProgressBar.setText("Preparing..");
            mCircleProgressBar.setTextSize(50);
        } else if (value==100){
            mCircleProgressBar.setText("Completed!");
            mCircleProgressBar.setTextSize(50);
        } else {
            mCircleProgressBar.setText(value+"%");
            mCircleProgressBar.setTextSize(70);
        }
    }

    public void onExportCompleted() {
        setExportProgress(100);
        mBtnCancel.setVisibility(View.GONE);
        mLayoutAfterExport.setVisibility(View.VISIBLE);
        mTextViewTip.setVisibility(View.INVISIBLE);
        NotificationHelper.notify(mActivity, "Export video is successful");
    }
}
