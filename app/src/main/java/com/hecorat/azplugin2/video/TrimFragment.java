package com.hecorat.azplugin2.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.dialogfragment.DialogConfirm;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.helper.picktime.PickTimePanel;
import com.hecorat.azplugin2.interfaces.DialogClickListener;
import com.hecorat.azplugin2.main.Constants;
import com.hecorat.azplugin2.main.MainActivity;
import com.hecorat.azplugin2.timeline.VideoTL;

/**
 * Created by Bkmsx on 12/8/2016.
 */

public class TrimFragment extends Fragment implements RangeSeekBar.OnSeekBarChangedListener,
        PickTimePanel.OnPickTimeListener {
    MainActivity mActivity;
    VideoView mVideoView;
    FrameLayout mLayoutSeekbar;
    RelativeLayout mLayoutVideoView;
    LinearLayout mLayoutFragment;
    RangeSeekBar mRangeSeekBar;
    Button mBtnOk, mBtnCancel;
    FrameLayout mLayoutPickTime;
    PickTimePanel mPickTimePanel;
    View mView;

    int startTimeMs, endTimeMs;
    String mVideoPath;
    VideoTL mVideoTL;

    public static TrimFragment newInstance(MainActivity activity, VideoTL videoTL){
        TrimFragment trimFragment = new TrimFragment();
        trimFragment.mActivity = activity;
        trimFragment.mVideoPath = videoTL.videoPath;
        trimFragment.mVideoTL = videoTL;
        trimFragment.inflateViews();
        return trimFragment;
    }

    private void inflateViews() {
        mView = LayoutInflater.from(mActivity).inflate(R.layout.trim_fragment, null);
        mVideoView = (VideoView) mView.findViewById(R.id.video_view);
        mLayoutSeekbar = (FrameLayout) mView.findViewById(R.id.layout_seekbar);
        mLayoutVideoView = (RelativeLayout) mView.findViewById(R.id.videoview_layout);
        mLayoutFragment = (LinearLayout) mView.findViewById(R.id.layout_fragment);
        mBtnOk = (Button) mView.findViewById(R.id.btn_ok_crop);
        mBtnCancel = (Button) mView.findViewById(R.id.btn_cancel_crop);
        mLayoutPickTime = (FrameLayout) mView.findViewById(R.id.layout_pick_time_trim);

        mBtnOk.setOnClickListener(onBtnOkClick);
        mBtnCancel.setOnClickListener(onBtnCancelClick);
        setLayoutTrimVideo();
        mVideoView.setVideoPath(mVideoPath);
        int seekTime = Math.max(10, mVideoTL.startTimeMs);
        mVideoView.seekTo(seekTime);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mView;
    }

    @Override
    public void onPickTimeCompleted(int minMs, int maxMs) {
        setSeekbarPosition(minMs, maxMs);
        mPickTimePanel.setTextValues(minMs, maxMs);
    }

    public void close() {
        mVideoView.setVisibility(View.GONE);
        mActivity.setActiveVideoViewVisible(true);
        mActivity.setLayoutFragmentVisible(false);
        setInvisible();
        mActivity.mOpenLayoutTrimVideo = false;
    }

    public void setSeekbarPosition(int startMs, int endMs){
        startTimeMs = startMs;
        endTimeMs = endMs;
        mRangeSeekBar.setSelectedValue(startMs, endMs);
        log("start: "+startMs+" end: "+ endMs);
    }

    private void log(String msg){
        Log.e("Trim fragment",msg);
    }

    View.OnClickListener onBtnCancelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            close();
        }
    };

    View.OnClickListener onBtnOkClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!mActivity.mIsVip) {
                DialogConfirm.newInstance(mActivity, mActivity, DialogClickListener.ASK_DONATE,
                        Constants.EVENT_ACTION_DIALOG_FROM_NEW_TRIM)
                        .show(mActivity.getSupportFragmentManager(), "donate");
                return;
            }
            close();
            mActivity.onTrimVideoCompleted(startTimeMs, endTimeMs);
        }
    };

    private void setInvisible(){
        View view = getView();
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void seekVideoTo(int value) {
        mVideoView.seekTo(value);
    }

    @Override
    public void updateSelectedTime(int minMs, int maxMs) {
        startTimeMs = minMs;
        endTimeMs = maxMs;
        mPickTimePanel.setTextValues(minMs, maxMs);
    }

    private void setLayoutTrimVideo(){
        float layoutHeight = Utils.getScreenHeight(mActivity)*0.9f;
        ViewGroup.LayoutParams layoutParams = mLayoutFragment.getLayoutParams();
        layoutParams.height = (int) layoutHeight;

        float videoLayoutHeight = layoutHeight*0.55f;
        float videoLayoutWidth = videoLayoutHeight*16/9;
        ViewGroup.LayoutParams videoLayoutParams = mLayoutVideoView.getLayoutParams();
        videoLayoutParams.height = (int) videoLayoutHeight;
        videoLayoutParams.width = (int) videoLayoutWidth;

        float layoutWidth = videoLayoutWidth*1.3f;
        layoutParams.width = (int) layoutWidth;

        mRangeSeekBar = new RangeSeekBar(mActivity, this, (int)layoutWidth, 100, mVideoPath);
        mLayoutSeekbar.addView(mRangeSeekBar);
        setSeekbarPosition(mVideoTL.startTimeMs, mVideoTL.endTimeMs);

        mPickTimePanel = new PickTimePanel(mActivity, this, mVideoTL.durationVideo);
        mLayoutPickTime.addView(mPickTimePanel);
        mPickTimePanel.setTextValues(mVideoTL.startTimeMs, mVideoTL.endTimeMs);
    }
}
