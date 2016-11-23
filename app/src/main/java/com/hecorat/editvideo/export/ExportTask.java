package com.hecorat.editvideo.export;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.speech.tts.Voice;
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
    }

    public class ImageHolder {
        String path;
        int width, height, x, y;
        float rotate;
        int startTime, endTime;
        ImageHolder (String path, int width, int height,
                     int x, int y, float rotate, int startTime, int endTime){
            this.path = path;
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
            this.rotate = rotate;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    private LinkedList<String> getCommand(){
        LinkedList<String> command = new LinkedList<>();
        ArrayList<ImageHolder> listImage = new ArrayList<>();


        String direct = Environment.getExternalStorageDirectory().toString();
        String inputVideo = direct + "/in.mp4";
        String inputImage1 = direct + "/img1.png";
        String inputImage2 = direct + "img2.png";
        String output = direct + "/imageVideo.mp4";
        ImageHolder image = new ImageHolder(inputImage1, 300, 400, 100, 100, 1.05f, 5, 10);
        listImage.add(image);
        image = new ImageHolder(inputImage2, 200, 300, 300, 300, 1.3f, 0, 5);
        listImage.add(image);
        String inStream = "[0:v]";
        String outStream = "[video]";
        String filter = ImageFilter.getFilter(inStream, outStream, listImage);
        log(filter);
        command.add("-i");
        command.add(inputVideo);
        command.add("-i");
        command.add(inputImage1);
        command.add("-filter_complex");
        command.add(filter);
        command.add("-map");
        command.add(outStream);
        command.add("-format");
        command.add("yuv420p");
        command.add("-preset");
        command.add("ultrafast");
        command.add("-c:a");
        command.add("copy");
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
//        FFmpeg.executeFFmpegCommand(mContext, getCommand());
        getCommand();
        return null;
    }

    private void log(String msg) {
        Log.e("Export Task", msg);
    }
}
