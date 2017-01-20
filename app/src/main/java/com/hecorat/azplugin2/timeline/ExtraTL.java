package com.hecorat.azplugin2.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.widget.RelativeLayout;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.addimage.FloatImage;
import com.hecorat.azplugin2.addtext.FloatText;
import com.hecorat.azplugin2.database.ImageObject;
import com.hecorat.azplugin2.database.TextObject;
import com.hecorat.azplugin2.export.ImageHolder;
import com.hecorat.azplugin2.export.TextHolder;
import com.hecorat.azplugin2.helper.AnalyticsHelper;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.main.Constants;
import com.hecorat.azplugin2.main.MainActivity;

import java.io.File;

/**
 * Created by bkmsx on 31/10/2016.
 */
public class ExtraTL extends AppCompatImageView {
    public int width, height;
    public int durationImage;
    public int startInTimeLineMs, endInTimeLineMs;
    public int left, right;
    public boolean isImage;
    public boolean inLayoutImage;
    public int leftMarginTimeLine;
    public int projectId;
    public int orderInLayout, orderInList;
    public boolean isExists;
    public int background;

    public MainActivity mActivity;
    public RelativeLayout.LayoutParams params;
    public Rect rectBackground, rectTop,rectBottom, rectLeft, rectRight;
    public Paint paint;
    public Bitmap bitmap;
    public String text;
    public FloatImage floatImage;
    public FloatText floatText;
    public String imagePath;
    public ImageHolder imageHolder;
    public TextHolder textHolder;

    ExtraTL(Context context) {
        super(context);
    }

    public ExtraTL(Context context, String pathOrText, int height, int leftMargin, boolean isImage) {
        super(context);
        mActivity = (MainActivity) context;
        projectId = mActivity.mProjectId;
        durationImage = Constants.DEFAULT_DURATION;
        this.height = height;
        background = ContextCompat.getColor(mActivity, R.color.background_timeline);
        paint = new Paint();
        this.isImage = isImage;
        left = leftMargin;

        if (isImage) {
            imagePath = pathOrText;
            isExists = new File(imagePath).exists();
            if (isExists) {
                bitmap = getBitmap(imagePath);
            } else {
                Bitmap rawBitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.ic_unknow_file);
                bitmap = Bitmap.createScaledBitmap(rawBitmap, 50, height, false);
                background = Color.RED;
            }
            inLayoutImage = true;
            imageHolder = new ImageHolder();
        } else {
            text = pathOrText;
            inLayoutImage = false;
            textHolder = new TextHolder();
        }

        leftMarginTimeLine = mActivity.mLeftMarginTimeLine;
        width = durationImage/ Constants.SCALE_VALUE;
        right = left + width;
        params = new RelativeLayout.LayoutParams(width, height);

        setLayoutParams(params);
        drawTimeLine(left, right);
        updateTimeLineStatus();
    }

    public void updateImageHolder(float layoutScale) {
        imageHolder.imagePath = imagePath;
        imageHolder.width = (int)(floatImage.widthScale*layoutScale);
        imageHolder.height = (int) (floatImage.heightScale*layoutScale);
        imageHolder.x = floatImage.xExport*layoutScale;
        imageHolder.y = floatImage.yExport*layoutScale;
        imageHolder.rotate = (float) (-floatImage.rotation* Math.PI/180);
        imageHolder.startInTimeLineSec = startInTimeLineMs /1000f;
        imageHolder.endInTimeLineSec = endInTimeLineMs /1000f;

        int imageDuration = (int) (imageHolder.endInTimeLineSec - imageHolder.startInTimeLineSec);
        AnalyticsHelper.getInstance()
                .sendCustomDimension(mActivity, Constants.DIMENSION_IMAGE_DURATION, imageDuration + "");
        if (imageHolder.rotate != 0) {
            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_IMAGE, Constants.ACTION_ROTATE_IMAGE);
        }
    }

    public void updateTextHolder(float layoutScale){
        float textCorrection = 20;
        String textFile = Utils.getTempFolder()+"/"+System.currentTimeMillis()+".txt";
        Utils.writeToFile(new File(textFile), text);
        textHolder.textPath = textFile;
        textHolder.fontPath = floatText.fontPath;
        textHolder.size = floatText.sizeScale*layoutScale;
        textHolder.fontColor = convertToHexColor(floatText.mColor);
        textHolder.boxColor = convertToHexColor(floatText.mBackgroundColor);
        textHolder.x = floatText.xExport*layoutScale - textCorrection;
        textHolder.y = floatText.yExport*layoutScale - textCorrection;
        textHolder.startInTimeLineSec = startInTimeLineMs /1000f;
        textHolder.endInTimeLineSec = endInTimeLineMs /1000f;
        textHolder.rotate = (float) (-floatText.rotation* Math.PI/180);
        textHolder.width = (int) ((floatText.widthScale+FloatText.PADDING*2)*layoutScale);
        textHolder.height = (int) ((floatText.heightScale+FloatText.PADDING*2)*layoutScale);
        textHolder.padding = FloatText.PADDING * layoutScale;

        int textDuration = (int) (textHolder.endInTimeLineSec - textHolder.startInTimeLineSec);
        AnalyticsHelper.getInstance()
                .sendCustomDimension(mActivity, Constants.DIMENSION_TEXT_DURATION, textDuration + "");
        if (textHolder.rotate != 0) {
            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_TEXT, Constants.ACTION_ROTATE_TEXT);
        }
    }

    public String convertToHexColor(int color) {
        String resultColor = "";
        String s = String.format("%08X", color);
        resultColor += s.substring(2) + "@0x" + s.substring(0, 2);
        return resultColor;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public void updateTimeLineStatus(){
        startInTimeLineMs = (left - leftMarginTimeLine)* Constants.SCALE_VALUE;
        endInTimeLineMs = (right - leftMarginTimeLine)* Constants.SCALE_VALUE;
    }

    private Bitmap getBitmap(String imagePath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        return Bitmap.createScaledBitmap(bitmap, 50, height, false);
    }

    public void restoreTextTL(TextObject textObject) {
        projectId = textObject.projectId;
        text = textObject.text;
        left = Integer.parseInt(textObject.left);
        right = Integer.parseInt(textObject.right);
        inLayoutImage = Integer.parseInt(textObject.inLayoutImage) == 1;
        orderInLayout = Integer.parseInt(textObject.orderInLayout);
        orderInList = Integer.parseInt(textObject.orderInList);

        drawTimeLine(left, right);
    }

    public TextObject getTextObject() {
        TextObject textObject = new TextObject();
        textObject.projectId = projectId;
        textObject.text = text;
        textObject.left = left + "";
        textObject.right = right + "";
        textObject.inLayoutImage = inLayoutImage? "1" : "0";
        textObject.orderInLayout = orderInLayout + "";
        textObject.orderInList = orderInList + "";

        textObject.x = floatText.x + "";
        textObject.y = floatText.y + "";
        textObject.scale = floatText.scaleValue + "";
        textObject.rotation = floatText.rotation + "";
        textObject.size = floatText.size + "";
        textObject.fontPath = floatText.fontPath + "";
        textObject.fontColor = floatText.mColor + "";
        textObject.boxColor = floatText.mBackgroundColor + "";
        textObject.fontId = floatText.fontId + "";
        return textObject;
    }

    public void restoreImageTL(ImageObject image){
        projectId = image.projectId;
        left = Integer.parseInt(image.left);
        right = Integer.parseInt(image.right);
        inLayoutImage = Integer.parseInt(image.inLayoutImage) == 1;
        orderInLayout = Integer.parseInt(image.orderInLayout);
        orderInList = Integer.parseInt(image.orderInList);

        drawTimeLine(left, right);
    }

    public ImageObject getImageObject() {
        ImageObject image = new ImageObject();
        image.projectId = projectId;
        image.path = imagePath;
        image.left = left + "";
        image.right = right + "";
        image.inLayoutImage = inLayoutImage? "1":"0";
        image.orderInLayout = orderInLayout + "";
        image.orderInList = orderInList + "";

        image.x = floatImage.x + "";
        image.y = floatImage.y + "";
        image.scale = floatImage.scaleValue + "";
        image.rotation = floatImage.rotation + "";
        return image;
    }

    public void drawTimeLine(int left, int right) {
        this.left = left;
        this.right = right;
        width = right - left;

        drawLayout();
    }

    private void drawLayout() {
        rectBackground = new Rect(0, 0, width, height);
        rectTop = new Rect(0, 0, width, Constants.BORDER_WIDTH);
        rectBottom = new Rect(0, height- Constants.BORDER_WIDTH, width, height);
        rectLeft = new Rect(0, 0, Constants.BORDER_WIDTH, height);
        rectRight = new Rect(width- Constants.BORDER_WIDTH, 0, width, height);
        params.leftMargin = left;
        params.width = width;
        setLayoutParams(params);
        invalidate();
        updateTimeLineStatus();
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
        paint.setColor(background);
        canvas.drawRect(rectBackground, paint);
        if (isImage) {
            canvas.drawBitmap(bitmap, 20, 0, paint);
        } else {
            paint.setColor(Color.MAGENTA);
            paint.setTextSize(35);
            canvas.drawText(text, 20, 50, paint);
        }
        paint.setColor(ContextCompat.getColor(mActivity, R.color.border_timeline_color));
        canvas.drawRect(rectTop, paint);
        canvas.drawRect(rectBottom, paint);
        canvas.drawRect(rectLeft, paint);
        canvas.drawRect(rectRight, paint);
    }

    private void log(String msg){
        Log.e("Image TimeLine",msg);
    }
}
