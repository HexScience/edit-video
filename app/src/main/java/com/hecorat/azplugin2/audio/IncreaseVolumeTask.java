package com.hecorat.azplugin2.audio;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.hecorat.azplugin2.export.FFmpeg;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.main.MainActivity;
import com.hecorat.azplugin2.timeline.AudioTL;
import com.hecorat.azplugin2.timeline.VideoTL;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by TienDam on 11/30/2016.
 */

public class IncreaseVolumeTask extends AsyncTask<Void, Void, Void> {
    private MainActivity mActivity;
    private VideoTL videoTL;
    private AudioTL audioTL;
    private boolean isVideo;
    private ProgressDialog progressDialog;
    public IncreaseVolumeTask (MainActivity activity, VideoTL videoTL){
        mActivity = activity;
        this.videoTL = videoTL;
        isVideo = true;
    }
    public IncreaseVolumeTask (MainActivity activity, AudioTL audioTL){
        mActivity = activity;
        this.audioTL = audioTL;
        isVideo = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Changing volume..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
        mActivity.onIncreaseVolumeTaskCompleted(isVideo);
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... params) {
        String input;
        String output;
        String name;
        float volume;
        if (isVideo){
            input = videoTL.videoPath;
            volume = videoTL.volume;
            name = new File(input).getName();
            output = Utils.getTempFolder()+"/"+name.substring(0, name.length()-4)+"_temp.aac";
            videoTL.audioPreview = output;
        } else {
            input = audioTL.audioPath;
            volume = audioTL.volume;
            name = new File(input).getName();
            output = Utils.getTempFolder()+"/"+name.substring(0, name.length()-4)+"_temp.aac";
            audioTL.audioPreview = output;
        }
        LinkedList<String> command = new LinkedList<>();
        String filter = "[0:a]volume="+volume;
        command.add("-i");
        command.add(input);
        command.add("-filter_complex");
        command.add(filter);
        command.add("-preset");
        command.add("ultrafast");
        command.add("-y");
        command.add(output);
        FFmpeg.getInstance(mActivity).executeFFmpegCommand(command);
        return null;
    }

    private void log(String msg){
        Log.e("Increase Volume Task", msg);
    }
}
