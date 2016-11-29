package com.hecorat.editvideo.export;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.main.MainActivity;
import com.hecorat.editvideo.timeline.AudioTL;
import com.hecorat.editvideo.timeline.ExtraTL;
import com.hecorat.editvideo.timeline.VideoTL;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by TienDam on 11/22/2016.
 */

public class ExportTask extends AsyncTask<Void, Void, Void> {
    ArrayList<VideoTL> mListVideo;
    ArrayList<ExtraTL> mListImage, mListText;
    ArrayList<AudioTL> mListAudio;
    MainActivity mActivity;
    ProgressDialog mProgressDialog;
    public ExportTask(MainActivity context, ArrayList<VideoTL> listVideo, ArrayList<ExtraTL> listImage,
                      ArrayList<ExtraTL> listText, ArrayList<AudioTL> listAudio) {
        mActivity = context;
        mListVideo = listVideo;
        mListImage = listImage;
        mListText = listText;
        mListAudio = listAudio;
        updateAllList();
    }

    private void updateAllList(){
        float layoutScale = mActivity.getLayoutVideoScale(1280f);

        for (int i=0; i<mListVideo.size(); i++){
            mListVideo.get(i).updateVideoHolder();
        }

        for (int i=0; i<mListAudio.size(); i++){
            mListAudio.get(i).updateAudioHolder();
        }

        for (int i=0; i<mListImage.size(); i++){
            mListImage.get(i).updateImageHolder(layoutScale);
        }

        for (int i=0; i<mListText.size(); i++){
            mListText.get(i).updateTextHolder(layoutScale);
        }
    }

    private void copyToStorage(String path){
        Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.raw.background);
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, 1280, 720, false);
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

    private LinkedList<String> getCommand(){
        LinkedList<String> command = new LinkedList<>();

        String direct = Environment.getExternalStorageDirectory().toString();
        String inputBackground = direct + "/background.png";
        copyToStorage(inputBackground);
        String output = direct + "/outputFile.mp4";

        String inVideo = "[v]";
        String outVideo = "[outVideo]";
        String outAudio = "[outAudio];";
        int order = 1;
        String filter = MergeFilter.getFilter(mListVideo, order);
        if (mListImage.size()==0&&mListText.size()==0){
            outVideo = "[v]";
            outAudio = "[outAudio]";
        }

        int audioPosition = order+mListVideo.size();
        filter += AudioFilter.getFilter(1f, outAudio, mListAudio, audioPosition);
        if (mListImage.size()>0){
            outVideo = "[inText];";
            if (mListText.size()==0){
                outVideo="[inText]";
            }
            int imagePosition = audioPosition + mListAudio.size();
            filter += ImageFilter.getFilter(inVideo, outVideo, mListImage, imagePosition);
        }
        if (mListText.size()>0){
            inVideo = "[inText]";
            outVideo = "[outVideo]";
            if (mListImage.size()==0){
                inVideo = "[v]";
            }
            filter += TextFilter.getFilter(inVideo, outVideo, mListText);
        }
        outAudio = "[outAudio]";
        command.add("-loop");
        command.add("1");
        command.add("-i");
        command.add(inputBackground);
        for (int i=0; i<mListVideo.size(); i++){
            VideoHolder video = mListVideo.get(i).videoHolder;
            command.add("-ss");
            command.add(video.startTime+"");
            command.add("-t");
            command.add(video.duration+"");
            command.add("-i");
            command.add(video.videoPath);
        }
        for (int i=0; i<mListAudio.size(); i++){
            AudioHolder audio = mListAudio.get(i).audioHolder;
            command.add("-ss");
            command.add(audio.startTime+"");
            command.add("-t");
            command.add(audio.duration+"");
            command.add("-i");
            command.add(audio.audioPath);
        }
        for (int i=0; i<mListImage.size(); i++){
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
        command.add("-c:a");
        command.add("aac");
        command.add("-y");
        command.add(output);
        return command;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        log("Start get info");
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("Exporting..");
        mProgressDialog.show();
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        log("End get info");
        mProgressDialog.dismiss();
        mActivity.hideStatusBar();
    }

    @Override
    protected Void doInBackground(Void... params) {
        log(getCommand().toString());
        FFmpeg ffmpeg = FFmpeg.newInstance(mActivity);
        ffmpeg.executeFFmpegCommand(getCommand());
        return null;
    }

    private void log(String msg) {
        Log.e("Export Task", msg);
    }
}
