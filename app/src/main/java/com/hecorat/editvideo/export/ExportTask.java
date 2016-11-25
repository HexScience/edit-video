package com.hecorat.editvideo.export;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.hecorat.editvideo.timeline.AudioTimeLine;
import com.hecorat.editvideo.timeline.ExtraTimeLine;
import com.hecorat.editvideo.timeline.MainTimeLine;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by TienDam on 11/22/2016.
 */

public class ExportTask extends AsyncTask<Void, Void, Void> {
    ArrayList<MainTimeLine> mListVideo;
    ArrayList<ExtraTimeLine> mListImage, mListText;
    ArrayList<AudioTimeLine> mListAudio;
    Context mContext;
    ProgressDialog mProgressDialog;
    public ExportTask(Context context, ArrayList<MainTimeLine> listVideo, ArrayList<ExtraTimeLine> listImage,
                      ArrayList<ExtraTimeLine> listText, ArrayList<AudioTimeLine> listAudio) {
        mContext = context;
        mListVideo = listVideo;
        mListImage = listImage;
        mListText = listText;
        mListAudio = listAudio;
        updateAllList();
    }

    private void updateAllList(){
        for (int i=0; i<mListVideo.size(); i++){
            mListVideo.get(i).updateVideoHolder();
        }

        for (int i=0; i<mListAudio.size(); i++){
            mListAudio.get(i).updateAudioHolder();
        }

        for (int i=0; i<mListImage.size(); i++){
            mListImage.get(i).updateImageHolder();
        }

        for (int i=0; i<mListText.size(); i++){
            mListText.get(i).updateTextHolder();
        }
    }

    private LinkedList<String> getCommand(){
        LinkedList<String> command = new LinkedList<>();

        String direct = Environment.getExternalStorageDirectory().toString();
        String inputBackground = direct + "/background.png";
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
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Exporting..");
        mProgressDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        log("End get info");
        mProgressDialog.dismiss();
    }

    @Override
    protected Void doInBackground(Void... params) {
        log(getCommand().toString());
        FFmpeg.executeFFmpegCommand(mContext, getCommand());
        return null;
    }

    private void log(String msg) {
        Log.e("Export Task", msg);
    }
}
