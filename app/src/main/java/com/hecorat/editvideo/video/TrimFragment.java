package com.hecorat.editvideo.video;

import android.os.Bundle;
import android.os.Environment;
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

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.helper.Utils;
import com.hecorat.editvideo.main.MainActivity;
import com.hecorat.editvideo.timeline.VideoTL;

/**
 * Created by Bkmsx on 12/8/2016.
 */

public class TrimFragment extends Fragment implements RangeSeekBar.OnSeekBarChangedListener{
    static MainActivity mActivity;
    VideoView mVideoView;
    FrameLayout mLayoutSeekbar;
    RelativeLayout mLayoutVideoView;
    LinearLayout mLayoutFragment;
    RangeSeekBar mRangeSeekBar;
    Button mBtnOk;

    int startTime, endTime;
    static String mVideoPath;
    static VideoTL mVideoTL;

    public static TrimFragment newInstance(MainActivity activity, VideoTL videoTL){
        mActivity = activity;
        mVideoPath = videoTL.videoPath;
        mVideoTL = videoTL;
        return new TrimFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trim_fragment, container, false);
        mVideoView = (VideoView) view.findViewById(R.id.video_view);
        mLayoutSeekbar = (FrameLayout) view.findViewById(R.id.layout_seekbar);
        mLayoutVideoView = (RelativeLayout) view.findViewById(R.id.videoview_layout);
        mLayoutFragment = (LinearLayout) view.findViewById(R.id.layout_fragment);
        mBtnOk = (Button) view.findViewById(R.id.btn_ok);

        mBtnOk.setOnClickListener(onBtnOkClick);
        setLayoutTrimVideo();
        mVideoView.setVideoPath(mVideoPath);
        int seekTime = Math.max(10, mVideoTL.startTime);
        mVideoView.seekTo(seekTime);
        return view;
    }

    public void setInitPosition(int start, int end){
        startTime = start;
        endTime = end;
        mRangeSeekBar.setSelectedValue(start, end);
        log("start: "+start+" end: "+ end);
    }

    private void log(String msg){
        Log.e("Trim fragment",msg);
    }

    View.OnClickListener onBtnOkClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mVideoView.setVisibility(View.GONE);
            mActivity.setActiveVideoViewVisible(true);
            mActivity.setLayoutFragmentVisible(false);
            setInvisible();
            mActivity.onTrimVideoCompleted(startTime, endTime);
        }
    };

    private void setInvisible(){
        getView().setVisibility(View.GONE);
    }

    @Override
    public void seekVideoTo(int value) {
        mVideoView.seekTo(value);
    }

    @Override
    public void updateSelectedTime(int min, int max) {
        startTime = min;
        endTime = max;
    }

    private void setLayoutTrimVideo(){
        float layoutHeight = Utils.getScreenWidth()*0.9f;
        ViewGroup.LayoutParams layoutParams = mLayoutFragment.getLayoutParams();
        layoutParams.height = (int) layoutHeight;

        float videoLayoutHeight = layoutHeight*0.6f;
        float videoLayoutWidth = videoLayoutHeight*16/9;
        ViewGroup.LayoutParams videoLayoutParams = mLayoutVideoView.getLayoutParams();
        videoLayoutParams.height = (int) videoLayoutHeight;
        videoLayoutParams.width = (int) videoLayoutWidth;

        float layoutWidth = videoLayoutWidth*1.1f;
        layoutParams.width = (int) layoutWidth;

        mRangeSeekBar = new RangeSeekBar(mActivity, (int)layoutWidth, 100, mVideoPath);
        mLayoutSeekbar.addView(mRangeSeekBar);
        setInitPosition(mVideoTL.startTime, mVideoTL.endTime);
    }
}
