package com.hecorat.editvideo.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hecorat.editvideo.addimage.FloatImage;
import com.hecorat.editvideo.addtext.FloatText;
import com.hecorat.editvideo.main.Constants;
import com.hecorat.editvideo.R;
import com.hecorat.editvideo.main.MainActivity;

/**
 * Created by bkmsx on 31/10/2016.
 */
public class ExtraTimeLine extends ImageView {
    public int width, height;
    public int durationImage;
    public int startInTimeLine, endInTimeLine;
    public int left, right;
    public boolean isPicture;
    public boolean inLayoutImage;
    public int leftMarginTimeLine;

    public MainActivity mActivity;
    public RelativeLayout.LayoutParams params;
    public Rect rectBackground;
    public Paint paint;
    public Bitmap bitmap;
    public String text;
    public FloatImage floatImage;
    public FloatText floatText;

    public ExtraTimeLine(Context context, String pathOrText, int height, int leftMargin, boolean isPicture) {
        super(context);
        mActivity = (MainActivity) context;
        durationImage = Constants.IMAGE_TEXT_DURATION;
        this.height = height;

        paint = new Paint();
        if (isPicture) {
            bitmap = getBitmap(pathOrText);
            inLayoutImage = true;
        } else {
            text = pathOrText;
            inLayoutImage = false;
        }
        this.isPicture = isPicture;
        left = leftMargin;
        leftMarginTimeLine = leftMargin;
        width = durationImage/Constants.SCALE_VALUE;
        right = left + width;
        params = new RelativeLayout.LayoutParams(width, height);
        setLayoutParams(params);
        drawTimeLine(left, right);
        getTimeLineStatus();
    }
    public void getTimeLineStatus(){
        startInTimeLine = (left - leftMarginTimeLine)*Constants.SCALE_VALUE;
        endInTimeLine = (right - leftMarginTimeLine)*Constants.SCALE_VALUE;
        log("start: "+startInTimeLine+" end: "+endInTimeLine);
    }

    private Bitmap getBitmap(String imagePath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        return Bitmap.createScaledBitmap(bitmap, 50, height, false);
    }

    public void drawTimeLine(int left, int right) {
        this.left = left;
        this.right = right;
        width = right - left;
        rectBackground = new Rect(0, 0, width, height);
        params.leftMargin = left;
        params.width = width;
        setLayoutParams(params);
        invalidate();
        getTimeLineStatus();
    }

    public void moveTimeLine(int left) {
        this.left = left;
        this.right = left + width;
        drawTimeLine(left, right);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayoutParams(params);
        paint.setColor(getResources().getColor(R.color.background_timeline));
        canvas.drawRect(rectBackground, paint);
        if (isPicture) {
            canvas.drawBitmap(bitmap, 20, 0, paint);
        } else {
            paint.setColor(Color.MAGENTA);
            paint.setTextSize(35);
            canvas.drawText(text, 20, 50, paint);
        }
    }

    private void log(String msg){
        Log.e("Image TimeLine",msg);
    }
}
