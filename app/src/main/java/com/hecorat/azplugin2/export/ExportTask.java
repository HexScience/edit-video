package com.hecorat.azplugin2.export;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.helper.AnalyticsHelper;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.main.Constants;
import com.hecorat.azplugin2.main.MainActivity;
import com.hecorat.azplugin2.timeline.AudioTL;
import com.hecorat.azplugin2.timeline.ExtraTL;
import com.hecorat.azplugin2.timeline.VideoTL;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by bkmsx on 11/22/2016.
 */

class ExportTask extends AsyncTask<Void, Void, Void> {
    private ExportFragment mExportFragment;

    private ArrayList<VideoTL> mListVideo;
    private ArrayList<ExtraTL> mListImage, mListText;
    private ArrayList<AudioTL> mListAudio;
    private MainActivity mActivity;
    private String mName;
    private String mTempVideo;
    private String mOutputPath;
    private String mOutputDir;

    private int mVideoDurationSec;
    private float mQuality;
    private int mFps;
    private int mLoop;
    private boolean mExportVideo;
    private boolean mOnlyOneVideo;
    private boolean mKeepRatio;
    private boolean mCopyingFile;

    private FFmpeg mFFmpeg;

    ExportTask(MainActivity context, ArrayList<VideoTL> listVideo, ArrayList<ExtraTL> listImage,
               ArrayList<ExtraTL> listText, ArrayList<AudioTL> listAudio, String name, float quality, boolean keepRatio) {
        mActivity = context;
        mExportFragment = mActivity.mExportFragment;
        mListVideo = listVideo;
        mListImage = listImage;
        mListText = listText;
        mListAudio = listAudio;
        mQuality = quality;
        mName = name;
        mKeepRatio = keepRatio;
        mExportVideo = true;
        updateAllList();
        mOnlyOneVideo = mListImage.isEmpty() && mListText.isEmpty()
                && mListAudio.isEmpty() && mListVideo.size() == 1;
    }

    ExportTask(MainActivity context, ArrayList<VideoTL> listVideo, ArrayList<ExtraTL> listImage,
               ArrayList<ExtraTL> listText, ArrayList<AudioTL> listAudio, String name, float quality,
               int fps, int loop) {
        mActivity = context;
        mExportFragment = mActivity.mExportFragment;
        mListVideo = listVideo;
        mListImage = listImage;
        mListText = listText;
        mListAudio = listAudio;
        mQuality = quality;
        mName = name;
        mFps = fps;
        mLoop = loop;
        mExportVideo = false;
        mKeepRatio = true;
        updateAllList();
        mOnlyOneVideo = mListImage.isEmpty() && mListText.isEmpty()
                && mListVideo.size() == 1;
    }

    private void updateAllList() {
        float layoutScale = mActivity.getLayoutVideoScale(mQuality);
        AnalyticsHelper.getInstance()
                .sendCustomDimension(mActivity, Constants.DIMENSION_OUTPUT_QUALITY, (int) mQuality + "");
        mVideoDurationSec = 0;
        for (int i = 0; i < mListVideo.size(); i++) {
            VideoHolder videoHolder = mListVideo.get(i).updateVideoHolder();
            mVideoDurationSec += videoHolder.durationSec;
        }

        AnalyticsHelper.getInstance()
                .sendCustomDimension(mActivity, Constants.DIMENSION_OUTPUT_DURATION, mVideoDurationSec + "");

        mActivity.setWaterMarkEndTime(mVideoDurationSec * 1000 + 500);

        for (int i = 0; i < mListAudio.size(); i++) {
            mListAudio.get(i).updateAudioHolder();
        }

        for (int i = 0; i < mListImage.size(); i++) {
            mListImage.get(i).updateImageHolder(layoutScale);
        }

        for (int i = 0; i < mListText.size(); i++) {
            mListText.get(i).updateTextHolder(layoutScale);
        }
    }

    private void copyImageToStorage(String path) {
        Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.raw.background);
        int height = (int) mQuality;
        int width = (int) (mQuality * 16 / 9);
        if (width % 2 != 0) {
            width++;
        }
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        File file = new File(path);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            scaleBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportVideoSlow() {
        if (mExportVideo) {
            mOutputPath = mOutputDir + "/" + mName + ".mp4";
        } else {
            mTempVideo = System.currentTimeMillis() + "";
            mOutputPath = Utils.getTempFolder() + "/" + mTempVideo + ".mp4";
        }

        LinkedList<String> command = new LinkedList<>();
        String inputBackground = Utils.getTempFolder() + "/background.png";
        copyImageToStorage(inputBackground);

        String inVideo = "[v]";
        String outVideo = "[outVideo]";
        String outAudio = "[outAudio];";
        int order = 1;
        String filter = MergeFilter.getFilter(mListVideo, (int) mQuality, order);
        if (mListImage.size() == 0 && mListText.size() == 0) {
            outVideo = "[v]";
            outAudio = "[outAudio]";
        }

        int audioPosition = order + mListVideo.size();
        filter += AudioFilter.getFilter(1f, outAudio, mListAudio, audioPosition);
        if (mListImage.size() > 0) {
            outVideo = "[inText];";
            if (mListText.size() == 0) {
                outVideo = "[inText]";
            }
            int imagePosition = audioPosition + mListAudio.size();
            filter += ImageFilter.getFilter(inVideo, outVideo, mListImage, imagePosition);
        }
        if (mListText.size() > 0) {
            inVideo = "[inText]";
            outVideo = "[outVideo]";
            if (mListImage.size() == 0) {
                inVideo = "[v]";
            }
            filter += TextFilter.getFilter(inVideo, outVideo, mListText);
        }
        outAudio = "[outAudio]";
        command.add("-loop");
        command.add("1");
        command.add("-i");
        command.add(inputBackground);
        for (int i = 0; i < mListVideo.size(); i++) {
            VideoHolder video = mListVideo.get(i).videoHolder;
            command.add("-ss");
            command.add(video.startTimeSecond + "");
            command.add("-t");
            command.add(video.durationSec + "");
            command.add("-i");
            command.add(video.videoPath);
        }
        for (int i = 0; i < mListAudio.size(); i++) {
            AudioHolder audio = mListAudio.get(i).audioHolder;
            command.add("-ss");
            command.add(audio.startTimeMs + "");
            command.add("-t");
            command.add(audio.durationSec + "");
            command.add("-i");
            command.add(audio.audioPath);
        }
        for (int i = 0; i < mListImage.size(); i++) {
            command.add("-i");
            command.add(mListImage.get(i).imagePath);
        }
        command.add("-filter_complex");
        command.add(filter);
        command.add("-map");
        command.add(outVideo);
        command.add("-map");
        command.add(outAudio);
        command.add("-format");
        command.add("yuva420p");
        command.add("-preset");
        command.add("ultrafast");
        command.add("-video_track_timescale");
        command.add("90k");
        command.add("-b:v");
        command.add("2000k");
        command.add("-c:v");
        command.add("libx264");
        command.add("-bufsize");
        command.add("64k");
        command.add("-c:a");
        command.add("aac");
        command.add("-bsf");
        command.add("aac_adtstoasc");
        command.add("-strict");
        command.add("-2");
        command.add("-y");
        command.add(mOutputPath);
        mFFmpeg.executeFFmpegCommand(command);
    }

    private void exportGifSlow() {
        LinkedList<String> command = new LinkedList<>();
        String input = Utils.getTempFolder() + "/" + mTempVideo + ".mp4";
        String palette = Utils.getTempFolder() + "/" + System.currentTimeMillis() + ".png";
        mOutputPath = mOutputDir + "/" + mName + ".gif";
        String filter = "[0:v]fps=" + mFps + ",scale=" + (int) mQuality + ":-1:" +
                "flags=lanczos,palettegen";
        command.add("-i");
        command.add(input);
        command.add("-vf");
        command.add(filter);
        command.add("-y");
        command.add(palette);
        mFFmpeg.executeFFmpegCommand(command);

        command = new LinkedList<>();
        filter = "fps=" + mFps + ",scale=" + (int) mQuality
                + ":-1:flags=lanczos [x]; [x][1:v] paletteuse";
        command.add("-i");
        command.add(input);
        command.add("-i");
        command.add(palette);
        command.add("-lavfi");
        command.add(filter);
        command.add("-loop");
        command.add(String.valueOf(mLoop));
        command.add("-y");
        command.add(mOutputPath);
        mFFmpeg.executeFFmpegCommand(command);
    }

    private void exportVideoFast() {
        mOutputPath = mOutputDir + "/" + mName + ".mp4";
        LinkedList<String> command = new LinkedList<>();
        VideoHolder video = mListVideo.get(0).videoHolder;
        command.add("-ss");
        command.add(video.startTimeSecond + "");
        command.add("-t");
        command.add(video.durationSec + "");
        command.add("-i");
        command.add(video.videoPath);
        command.add("-c");
        command.add("copy");
        command.add("-y");
        command.add(mOutputPath);
        mFFmpeg.executeFFmpegCommand(command);
    }

    private void exportGifFast() {
        LinkedList<String> command = new LinkedList<>();
        VideoHolder video = mListVideo.get(0).videoHolder;
        String input = video.videoPath;
        String palette = Utils.getTempFolder() + "/" + System.currentTimeMillis() + ".png";
        mOutputPath = mOutputDir + "/" + mName + ".gif";
        String filter = "[0:v]fps=" + mFps + ",scale=" + (int) mQuality + ":-1:" +
                "flags=lanczos,palettegen";
        command.add("-ss");
        command.add(video.startTimeSecond + "");
        command.add("-t");
        command.add(video.durationSec + "");
        command.add("-i");
        command.add(input);
        command.add("-vf");
        command.add(filter);
        command.add("-y");
        command.add(palette);
        mFFmpeg.executeFFmpegCommand(command);

        command = new LinkedList<>();
        filter = "fps=" + mFps + ",scale=" + (int) mQuality
                + ":-1:flags=lanczos [x]; [x][1:v] paletteuse";

        command.add("-ss");
        command.add(video.startTimeSecond + "");
        command.add("-t");
        command.add(video.durationSec + "");
        command.add("-i");
        command.add(input);
        command.add("-i");
        command.add(palette);
        command.add("-lavfi");
        command.add(filter);
        command.add("-loop");
        command.add(String.valueOf(mLoop));
        command.add("-y");
        command.add(mOutputPath);
        mFFmpeg.executeFFmpegCommand(command);
    }

    private void exportVideoCrop() {
        LinkedList<String> command = new LinkedList<>();
        VideoHolder video = mListVideo.get(0).videoHolder;
        mTempVideo = System.currentTimeMillis() + "";
        mOutputPath = Utils.getTempFolder() + "/" + mTempVideo + ".mp4";
        String filter = "[0:v]crop=" + video.width + ":" + video.height +
                ":" + video.left + ":" + video.top;
        command.add("-ss");
        command.add(video.startTimeSecond + "");
        command.add("-t");
        command.add(video.durationSec + "");
        command.add("-i");
        command.add(video.videoPath);
        command.add("-filter_complex");
        command.add(filter);
        command.add("-preset");
        command.add("ultrafast");
        command.add("-y");
        command.add(mOutputPath);
        mFFmpeg.executeFFmpegCommand(command);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!mExportVideo) {
            VideoHolder video = mListVideo.get(0).videoHolder;
            if (mOnlyOneVideo && !video.crop) {
                mExportFragment.setProcessTitle(mActivity.getString(R.string.exporting_gif));
            } else {
                mExportFragment.setProcessTitle(mActivity.getString(R.string.preparing_video));
            }
        }
        if (mActivity.mUseSdCard) {
            mOutputDir = Utils.getTempFolder();
        } else {
            mOutputDir = mActivity.mOutputDirectory;
        }
        new ExportProgress(mActivity, mVideoDurationSec)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mActivity.mFinishExport = true;
        mExportFragment.setProcessTitle("");
        Utils.sendMediaScannerBroadcast(mActivity, mOutputPath);
        log("mOutputPath: " + mOutputPath);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        mExportFragment.setProcessTitle(mActivity.getString(R.string.exporting_gif));
    }

    @Override
    protected Void doInBackground(Void... params) {
        mFFmpeg = FFmpeg.getInstance(mActivity);
        VideoHolder video = mListVideo.get(0).videoHolder;
        if (mActivity.mIsVip && mOnlyOneVideo && mKeepRatio) {
            if (mExportVideo) {
                if (video.crop || video.changeVolume) {
                    exportVideoSlow();
                } else {
                    exportVideoFast();
                }
            } else {
                if (video.crop) {
                    exportVideoCrop();
                    publishProgress();
                    exportGifSlow();
                } else {
                    exportGifFast();
                }
            }
        } else {
            if (mExportVideo) {
                exportVideoSlow();
            } else {
                exportVideoSlow();
                publishProgress();
                exportGifSlow();
            }
        }

        //need mOutputPath;
        Intent intent = new Intent(Constants.ACTION_COPY_FILE_TO_SDCARD);
        intent.putExtra(Constants.VIDEO_FILE_PATH, mOutputPath);
        mActivity.sendBroadcast(intent);
        mCopyingFile = true;
        while (mCopyingFile) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    String getOutputPath() {
        return mOutputPath;
    }

    private void log(String msg) {
        Log.e("Export Task", msg);
    }

    void onBroadcastReceived(Intent intent, String action) {
        switch (action) {
            case Constants.ACTION_COPY_COMPLETED:
                mCopyingFile = false;
                break;
        }
    }
}
