package com.hecorat.editvideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by bkmsx on 31/10/2016.
 */
public class ExtraTimeLine extends ImageView {
    int width, height;
    Rect rectBackground;
    Paint paint;
    MediaMetadataRetriever retriever;
    int durationImage;
    Bitmap defaultBitmap;
    RelativeLayout.LayoutParams params;
    int startTime, endTime;
    int leftPosition;
    ExtraTimeLineStatus timeLineStatus;
    Bitmap bitmap;
    String text;
    boolean timelinePicture;

    ArrayList<Bitmap> listBitmap;
    public ExtraTimeLine(Context context, String pathOrText, int height, boolean isPicture) {
        super(context);
        durationImage = Constants.IMAGE_TEXT_DURATION;
        startTime = 0;
        endTime = durationImage;

        this.height = height;

        paint = new Paint();
        if (isPicture) {
            bitmap = getBitmap(pathOrText);
        } else {
            text = pathOrText;
        }
        timelinePicture = isPicture;

        params = new RelativeLayout.LayoutParams(width, height);
        setLayoutParams(params);
        defaultBitmap = createDefaultBitmap();
        drawTimeLine(startTime, endTime);
        initTimeLineStatus();

    }

    private Bitmap getBitmap(String imagePath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        return Bitmap.createScaledBitmap(bitmap, 50, height, false);
    }

    public void initTimeLineStatus(){
        timeLineStatus = new ExtraTimeLineStatus();
        timeLineStatus.startTime = 0;
        timeLineStatus.endTime = durationImage;
        timeLineStatus.widthTimeLine = width;

    }

    public void setLeftMargin(int value) {
        leftPosition = value;
        timeLineStatus.leftMargin = leftPosition - MainTimeLineControl.THUMB_WIDTH;
        log("time status left: "+timeLineStatus.leftMargin);
        params.leftMargin = value;
    }

    public void drawTimeLine(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        params.leftMargin = startTime/Constants.SCALE_VALUE + Constants.MARGIN_LEFT_TIME_LINE;
        width = (endTime - startTime)/Constants.SCALE_VALUE;
        rectBackground = new Rect(0, 0, width, height);
        params.width = width;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayoutParams(params);
        paint.setColor(getResources().getColor(R.color.background_timeline));
        canvas.drawRect(rectBackground, paint);
        if (timelinePicture) {
            canvas.drawBitmap(bitmap, 20, 0, paint);
        } else {
            paint.setColor(Color.MAGENTA);
            paint.setTextSize(35);
            canvas.drawText(text, 20, 50, paint);
            log(text);
        }
    }

    private Bitmap createDefaultBitmap(){
        Paint paint = new Paint();
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        return bitmap;
    }

    private void log(String msg){
        Log.e("Image TimeLine",msg);
    }
}
