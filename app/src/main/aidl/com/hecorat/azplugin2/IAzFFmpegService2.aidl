// IAzFFmpegService2.aidl
package com.hecorat.azplugin2;

// Declare any non-default types here with import statements

interface IAzFFmpegService2 {
    Bundle convertVideoToGif(String inputFile, String outputFile, int fps, int scale, int start, int duration, int loop);
   	int getPluginVersion();
   	void setAzRecorderVersion(int versionCode);

   	Bundle cropVideo(String input, String output, int width, int height, int x, int y, int quality);
   	Bundle trimVideo(String inputFile, String outputFile, String begin, String end);
   	Bundle concatVideo(String inputFile, String outputFile);
   	String getLineLog();
   	Bundle convertToAAC(String inputFile, String outputFile, String start, String end, boolean isAAC);
   	Bundle loopAudio(String inputText, String outputFile, String duration);
   	Bundle mergeAudioVideo(String inputVideo, String inputAudio, String outputFile, boolean isRepeat);
   	Bundle compressFileSize(String inputFile, String outputFile, int videoWidth, int videoHeight, double birate);
   	Bundle addImageToVideo(String inputVideo, in String[] inputImage, int numberImages, String outputFile, in float[] x, in float[] y, in int[] width, in int[] height, in float[] rotation, in float[] startTime, in float[] endTime);
   	Bundle adjustVolume(String inputVideo, String outputVideo, float volume);
   	Bundle mergeAudioVideoWithVolume(String inputVideo, String inputAudio, String outputFile, boolean isRepeat, float volume);
   	Bundle addTextToVideo(String inputVideo, in String[] inputText, int numberTexts, String outputFile, in String[] textFonts, in int[] textSizes, in String[] textColors, in String[] backgrounds, in float[] starts, in float[] ends, in float[] x, in float[] y);
    Bundle resizeVideo(String inputVideo, String outputVideo, int width, int height);
    Bundle getVideoInfo(String inputVideo, String outputFile);
}
