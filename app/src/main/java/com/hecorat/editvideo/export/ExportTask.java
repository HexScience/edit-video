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

    public class TextHolder {
        String textFile, fontPath;
        String fontColor, boxColor;
        int size, x, y;
        float rotate;
        int startTime, endTime;
        TextHolder (String textFile, String fontPath, int size, String fontColor,
                    String boxColor, int x, int y, float rotate, int startTime, int endTime){
            this.textFile = textFile;
            this.fontPath = fontPath;
            this.fontColor = fontColor;
            this.boxColor = boxColor;
            this.size = size;
            this.x = x;
            this.y = y;
            this.rotate = rotate;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    public class AudioHolder {
        String audioPath;
        float volume;
        int startTime, endTime;
        int begin;
        AudioHolder(String audioPath, float volume, int startTime, int endTime, int begin){
            this.audioPath = audioPath;
            this.volume = volume;
            this.startTime = startTime;
            this.endTime = endTime;
            this.begin = begin;
        }
    }

    public class VideoHolder {
        String videoPath;
        int begin, end;
        VideoHolder(String videoPath, int begin, int end){
            this.videoPath = videoPath;
            this.begin = begin;
            this.end = end;
        }
    }

    private LinkedList<String> getCommand(){
        LinkedList<String> command = new LinkedList<>();
        ArrayList<ImageHolder> listImage = new ArrayList<>();
        ArrayList<TextHolder> listText = new ArrayList<>();
        ArrayList<AudioHolder> listAudio = new ArrayList<>();
        ArrayList<VideoHolder> listVideo = new ArrayList<>();

        String direct = Environment.getExternalStorageDirectory().toString();
        String inputBackground = direct + "/background.png";
        String inputVideo1 = direct + "/in.mp4";
        String inputVideo2 = direct + "/in1.mp4";
        String inputImage1 = direct + "/img1.png";
        String inputImage2 = direct + "/img2.png";
        String inputText = direct + "/text.txt";
        String fontText = direct + "/font.otf";
        String inputAudio1 = direct + "/dhkb.mp3";
        String inputAudio2 = direct + "/mm.mp3";
        String output = direct + "/imageVideo.mp4";

        ImageHolder image = new ImageHolder(inputImage1, 300, 400, 100, 100, 1.05f, 5, 10);
        listImage.add(image);
        image = new ImageHolder(inputImage2, 200, 300, 300, 300, 1.3f, 0, 5);
        listImage.add(image);

        TextHolder text = new TextHolder(inputText, fontText, 36, "red", "blue", 200, 200, 1.05f, 0, 5);
        listText.add(text);
        text = new TextHolder(inputText, fontText, 46, "red", "blue", 400, 200, 1.5f, 0, 10);
        listText.add(text);

        AudioHolder audio = new AudioHolder(inputAudio1, 1f, 5, 5, 30);
        listAudio.add(audio);
        audio = new AudioHolder(inputAudio2, 0.5f, 0, 5, 30);
        listAudio.add(audio);

        VideoHolder video = new VideoHolder(inputVideo1, 0, 9);
        listVideo.add(video);
        video = new VideoHolder(inputVideo2, 0, 9);
        listVideo.add(video);

        String filter = MergeFilter.getFilter(listVideo, 1);
        String inStream = "[v]";
        String auOutStream = "[aout]";
        filter += AudioFilter.getFilter(0.5f, auOutStream+";", listAudio, 5);
        String outStream = "[outText];";
        filter += ImageFilter.getFilter(inStream, outStream, listImage,3);
        inStream = "[outText]";
        outStream = "[video]";
        filter += TextFilter.getFilter(inStream, outStream, listText);
        log(filter);

        command.add("-loop");
        command.add("1");
        command.add("-i");
        command.add(inputBackground);
        command.add("-i");
        command.add(inputVideo1);
        command.add("-i");
        command.add(inputVideo2);
        command.add("-i");
        command.add(inputImage1);
        command.add("-i");
        command.add(inputImage2);
        command.add("-i");
        command.add(inputAudio1);
        command.add("-i");
        command.add(inputAudio2);
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
        FFmpeg.executeFFmpegCommand(mContext, getCommand());
//        getCommand();
        return null;
    }

    private void log(String msg) {
        Log.e("Export Task", msg);
    }
}
