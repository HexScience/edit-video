package com.hecorat.azplugin2.video;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.main.MainActivity;
import com.hecorat.azplugin2.timeline.VideoTL;

/**
 * Created by bkmsx on 2/16/2017.
 */

public class FragmentCrop extends Fragment implements View.OnClickListener{
    View mView;
    MainActivity mainActivity;
    VideoTL videoTL;
    VideoView videoView;
    ImageView btnPlay;
    SeekBar seekBar;
    LinearLayout mainlayout;
    RelativeLayout layoutVideoView;
    CropFrame cropFrame;

    boolean play;

    public static FragmentCrop newInstance(MainActivity mainActivity, VideoTL videoTL) {
        FragmentCrop fragmentCrop = new FragmentCrop();
        fragmentCrop.mainActivity = mainActivity;
        fragmentCrop.videoTL = videoTL;
        fragmentCrop.inflateViews();
        return fragmentCrop;
    }

    private void inflateViews() {
        mView = LayoutInflater.from(mainActivity).inflate(R.layout.crop_fragment, null);
        mView.findViewById(R.id.btn_ok_crop).setOnClickListener(this);
        mView.findViewById(R.id.btn_cancel_crop).setOnClickListener(this);
        videoView = (VideoView) mView.findViewById(R.id.videoview_crop);
        mainlayout = (LinearLayout) mView.findViewById(R.id.mainlayout_crop);
        layoutVideoView = (RelativeLayout) mView.findViewById(R.id.layout_videoview_crop);
        seekBar = (SeekBar) mView.findViewById(R.id.seekbar_crop);
        btnPlay = (ImageView) mView.findViewById(R.id.btn_play_crop);
        btnPlay.setOnClickListener(this);

        ViewGroup.LayoutParams mainLayoutParams = mainlayout.getLayoutParams();
        mainLayoutParams.height = (int) (Utils.getScreenHeight(mainActivity) * 0.98f);
        ViewGroup.LayoutParams layoutVideoViewParams = layoutVideoView.getLayoutParams();
        layoutVideoViewParams.height = (int) (mainLayoutParams.height * 0.75f);
        layoutVideoViewParams.width = (int) (layoutVideoViewParams.height * videoTL.originVideoRatio);
        mainLayoutParams.width = (int) (layoutVideoViewParams.height * 1.9f);

        int framewidth = layoutVideoViewParams.width;
        int frameHeight = layoutVideoViewParams.height;
        int frameLeft = (int) (videoTL.leftSide * framewidth);
        int frameRight = (int) (videoTL.rightSide * framewidth);
        int frameTop = (int) ((1 - videoTL.topSide) * frameHeight);
        int frameBottom = (int) ((1 - videoTL.bottomSide) * frameHeight);
        Point[] points = new Point[4];
        points[0] = new Point(0, 0);
        points[1] = new Point(framewidth, frameHeight);
        points[2] = new Point(frameLeft, frameTop);
        points[3] = new Point(frameRight, frameBottom);
        cropFrame = new CropFrame(mainActivity, points);
        layoutVideoView.addView(cropFrame);

        videoView.setVideoPath(videoTL.videoPath);
        videoView.seekTo(10);

        seekBar.setMax(videoTL.durationVideo);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoView.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void log(String msg){
        Log.e("FragmentCrop", msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok_crop:
                mainActivity.openLayoutCropVideo(false);
                videoTL.setVideoSides(cropFrame.left, cropFrame.right,
                        cropFrame.bottom, cropFrame.top);
                mainActivity.cropVideo(videoTL);
                break;
            case R.id.btn_cancel_crop:
                mainActivity.openLayoutCropVideo(false);
                break;
            case R.id.btn_play_crop:
                onBtnPlayClick();
                break;
        }
    }

    private void onBtnPlayClick(){
        if (play) {
            videoView.pause();
            play = false;
            btnPlay.setImageResource(R.drawable.ic_play_crop);
        } else {
            videoView.start();
            btnPlay.setImageResource(R.drawable.ic_pause_crop);
            play = true;
            new TaskUpdateSeekbar().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    class TaskUpdateSeekbar extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (play) {
                publishProgress();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            seekBar.setProgress(videoView.getCurrentPosition());
            if (!videoView.isPlaying()) {
                play = false;
                btnPlay.setImageResource(R.drawable.ic_play_crop);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return mView;
    }
}
