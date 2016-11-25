package com.hecorat.editvideo.export;

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

        String filter = MergeFilter.getFilter(mListVideo, 1);
        String inStream = "[v]";
        String auOutStream = "[aout]";
        int audioPosition = 1+mListVideo.size();
        filter += AudioFilter.getFilter(1f, auOutStream+";", mListAudio, audioPosition);
        String outStream = "[outText];";
        int imagePosition = audioPosition + mListAudio.size();
        filter += ImageFilter.getFilter(inStream, outStream, mListImage, imagePosition);
        inStream = "[outText]";
        outStream = "[video]";
        filter += TextFilter.getFilter(inStream, outStream, mListText);

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
        command.add(outStream);
        command.add("-map");
        command.add(auOutStream);
        command.add("-format");
        command.add("yuv420p");
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
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        log("End get info");
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
