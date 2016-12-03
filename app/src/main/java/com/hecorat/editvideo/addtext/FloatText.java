package com.hecorat.editvideo.addtext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.main.MainActivity;
import com.hecorat.editvideo.timeline.ExtraTL;


/**
 * Created by TienDam on 11/14/2016.
 */

public class FloatText extends ImageView {
    public Bitmap bitmap, mainBitmap, rotateBitmap, scaleBitmap;
    public Paint paint;
    public RelativeLayout.LayoutParams params;
    public Rect rectBorder, rectBackground;
    public MainActivity mActivity;
    public ExtraTL timeline;
    public Point initScalePoint, initCenterPoint, initRotatePoint,
            initTopRightPoint, initBottomLeftPoint, initTopLeftPoint, initBottomRightPoint;
    public String text;
    public TextPaint textPaint;
    public Rect bound;
    public Typeface mTypeface;
    public String fontPath;

    public int width, height;
    public float x, y, translateX, translateY, xExport, yExport;
    public float rotation = 0;
    public float[] scalePoint, centerPoint, rotatePoint,
            topRightPoint, bottomLeftPoint, topLeftPoint, bottomRightPoint;
    public float scaleValue = 1f;
    public float widthScale, heightScale;
    public boolean isCompact;
    public boolean drawBorder;
    public int maxDimensionLayout;
    public int mStyle;
    public int mColor;
    public int mBackgroundColor;
    public float size, sizeScale;

    public static final int ROTATE_CONSTANT = 30;
    public static final int INIT_X = 300, INIT_Y = 300, INIT_SIZE = 60;
    public static final int PADDING = 30;

    public FloatText(Context context, String text) {
        super(context);
        x = INIT_X;
        y = INIT_Y;
        size = INIT_SIZE;
        sizeScale = size;
        this.text = text;
        mActivity = (MainActivity) context;
        fontPath = mActivity.mFontPath.get(0);
        mTypeface = Typeface.createFromFile(fontPath);
        paint = new Paint();
        textPaint = new TextPaint();
        textPaint.setTextSize(size);
        setText(text);

        rotatePoint = new float[2];
        scalePoint = new float[2];
        centerPoint = new float[2];
        topRightPoint = new float[2];
        bottomLeftPoint = new float[2];
        topLeftPoint = new float[2];
        bottomRightPoint = new float[2];
        rotatePoint[0] = initRotatePoint.x;
        rotatePoint[1] = initRotatePoint.y;
        scalePoint[0] = initScalePoint.x;
        scalePoint[1] = initScalePoint.y;
        centerPoint[0] = initCenterPoint.x;
        centerPoint[1] = initCenterPoint.y;
        topRightPoint[0] = initTopRightPoint.x;
        topRightPoint[1] = initTopRightPoint.y;
        bottomLeftPoint[0] = initBottomLeftPoint.x;
        bottomLeftPoint[1] = initBottomLeftPoint.y;
        topLeftPoint[0] = initTopLeftPoint.x;
        topLeftPoint[1] = initTopLeftPoint.y;
        bottomRightPoint[0] = initBottomRightPoint.x;
        bottomRightPoint[1] = initBottomRightPoint.y;

        rotateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rotate);
        scaleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_scale);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setFullLayout();
        setOnTouchListener(onTouchListener);
        setOnClickListener(onClickListener);
        drawBorder(true);
        mColor = Color.RED;
        mBackgroundColor = Color.TRANSPARENT;
    }

    public void setTextBgrColor(int color){
        mBackgroundColor = color;
        invalidate();
    }

    public void setTextColor(int color){
        mColor = color;
        invalidate();
    }

    public void setText(String text){
        this.text = text;
        resetLayout();
    }

    public void setStyle(int style){
        mStyle = style;
        resetLayout();
    }

    public void setFont(String fontPath){
        this.fontPath = fontPath;
        mTypeface = Typeface.createFromFile(fontPath);
        resetLayout();
    }

    public void resetLayout(){
        textPaint.setTypeface(Typeface.create(mTypeface, mStyle));
        bound = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bound);
        width = (int) textPaint.measureText(text);
        height = bound.height();
        rectBorder = new Rect(-PADDING, -PADDING, width+PADDING, height+PADDING);
        rectBackground = new Rect(-PADDING, -PADDING, width+PADDING, height+PADDING);
        widthScale = width*scaleValue;
        heightScale = height*scaleValue;

        initRotatePoint = new Point(-PADDING, -PADDING);
        initCenterPoint = new Point(width/2, height/2);
        initScalePoint = new Point(width+PADDING, height+PADDING);
        initBottomLeftPoint = new Point(0, height);
        initTopRightPoint = new Point(width, 0);
        initTopLeftPoint = new Point(0, 0);
        initBottomRightPoint = new Point(width, height);
        invalidate();
    }

    public void drawBorder(boolean draw){
        drawBorder = draw;
        if (draw) {
            bringToFront();
        }
        invalidate();
    }

    private void setCompactLayout(){
        translateX = (int) (maxDimensionLayout-widthScale)/2;
        translateY = (int) (maxDimensionLayout-heightScale)/2;
        log("translateX = "+translateX+" translateY = "+translateY);
        params.width = maxDimensionLayout;
        params.height = maxDimensionLayout;

        params.leftMargin = (int) (x - translateX);
        params.topMargin = (int) (y - translateY);
        setLayoutParams(params);
        invalidate();
        isCompact = true;
    }

    private void setFullLayout(){
        translateX = x;
        translateY = y;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.topMargin = 0;
        params.leftMargin = 0;
        setLayoutParams(params);
        invalidate();
        isCompact = false;
    }

    private double getAngle(double xTouch, double yTouch) {
        double x = xTouch - centerPoint[0];
        double y = centerPoint[1] - yTouch;

        switch (getQuadrant(x, y)) {

            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }

    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }
    public void scaleText(float moveX, float moveY){
        if (Math.abs(scalePoint[0]-centerPoint[0])>100){
            if (scalePoint[0] >= centerPoint[0]) {
                widthScale += moveX;
            } else {
                widthScale -= moveX;
            }
            scaleValue = widthScale/width;
            heightScale = scaleValue*height;
        } else {
            if (scalePoint[1] >= centerPoint[1]){
                heightScale += moveY;
            } else {
                heightScale -= moveY;
            }
            scaleValue = heightScale/height;
            widthScale = scaleValue*width;
        }
        sizeScale = size*scaleValue;
        invalidate();
    }

    public void moveText(float moveX, float moveY) {
        x += moveX;
        y += moveY;
        translateX = x;
        translateY = y;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        Matrix matrix = new Matrix();
        matrix.postTranslate(translateX, translateY);
        rotatePoint[0] = initRotatePoint.x;
        rotatePoint[1] = initRotatePoint.y;
        scalePoint[0] = initScalePoint.x;
        scalePoint[1] = initScalePoint.y;
        centerPoint[0] = initCenterPoint.x;
        centerPoint[1] = initCenterPoint.y;
        topRightPoint[0] = initTopRightPoint.x;
        topRightPoint[1] = initTopRightPoint.y;
        bottomLeftPoint[0] = initBottomLeftPoint.x;
        bottomLeftPoint[1] = initBottomLeftPoint.y;
        topLeftPoint[0] = initTopLeftPoint.x;
        topLeftPoint[1] = initTopLeftPoint.y;
        bottomRightPoint[0] = initBottomRightPoint.x;
        bottomRightPoint[1] = initBottomRightPoint.y;

        matrix.mapPoints(centerPoint);

        matrix.postScale(scaleValue, scaleValue, centerPoint[0], centerPoint[1]);
        matrix.postRotate(-rotation, centerPoint[0], centerPoint[1]);

        matrix.mapPoints(scalePoint);
        matrix.mapPoints(rotatePoint);
        matrix.mapPoints(bottomLeftPoint);
        matrix.mapPoints(topRightPoint);
        matrix.mapPoints(topLeftPoint);
        matrix.mapPoints(bottomRightPoint);

        xExport = Math.min(Math.min(Math.min(topLeftPoint[0], bottomRightPoint[0]), bottomLeftPoint[0]), topRightPoint[0]);
        yExport = Math.min(Math.min(Math.min(topLeftPoint[1], bottomRightPoint[1]), bottomLeftPoint[1]), topRightPoint[1]);

        // befor N canvas apply matrix from leftside of screen
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            matrix.postTranslate(mActivity.mVideoViewLeft, 0);
        }

        canvas.setMatrix(matrix);
        textPaint.setColor(mColor);
        int canvasWidth = canvas.getWidth();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            canvasWidth = canvas.getWidth()+ (int)mActivity.mVideoViewLeft;
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mBackgroundColor);
        canvas.drawRect(rectBackground, paint);

        StaticLayout textLayout = new StaticLayout(text, textPaint,canvasWidth,
                Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
        log("canvas width: "+canvas.getWidth());
        textLayout.draw(canvas);

        if (!drawBorder) {
            return;
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(3);
        paint.setPathEffect(new DashPathEffect(new float[] {8,6}, 0));
        canvas.drawRect(rectBorder, paint);
        canvas.restore();

        canvas.drawBitmap(rotateBitmap, rotatePoint[0]-ROTATE_CONSTANT, rotatePoint[1]-ROTATE_CONSTANT, paint);
        canvas.drawBitmap(scaleBitmap, scalePoint[0]-ROTATE_CONSTANT, scalePoint[1]-ROTATE_CONSTANT, paint);
    }

    OnTouchListener onTouchListener = new OnTouchListener() {
        float oldX, oldY, moveX, moveY;
        double startAngle, currentAngle;
        int touch = 0;
        float delta = 10;
        boolean isTouch;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    bringToFront();
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();
                    startAngle = getAngle(oldX, oldY);
                    int eps = 75;
                    float epsMove = Math.max(widthScale/2, heightScale/2);
                    if (oldX < scalePoint[0]+eps && oldX > scalePoint[0]-eps && oldY < scalePoint[1]+eps && oldY > scalePoint[1]-eps){
                        touch = 1;
                    } else if (oldX < rotatePoint[0]+eps && oldX > rotatePoint[0]-eps && oldY < rotatePoint[1]+eps && oldY > rotatePoint[1]-eps){
                        touch = 3;
                    } else if (oldX < centerPoint[0]+epsMove && oldX > centerPoint[0]-epsMove && oldY < centerPoint[1]+epsMove && oldY > centerPoint[1]-epsMove) {
                        touch = 2;
                    }  else {
                        touch = 0;
                    }

                    isTouch = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveX = motionEvent.getX() - oldX;
                    moveY = motionEvent.getY() - oldY;
                    if (Math.abs(moveX) >= delta && Math.abs(moveY)>= delta) {
                        isTouch = true;
                    }

                    if (!isTouch || !drawBorder) {
                        return false;
                    }

                    if (touch == 1) {
                        scaleText(moveX, moveY);
                    }
                    if (touch == 2) {
                        moveText(moveX, moveY);
                    }
                    if (touch == 3) {
                        currentAngle = getAngle(motionEvent.getX(), motionEvent.getY());
                        rotation += (currentAngle-startAngle);
                        invalidate();
                        startAngle = currentAngle;
                    }
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isTouch){
                        performClick();
                    }
                    break;
            }
            return true;
        }
    };


    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
        if (drawBorder) {
            drawBorder(false);
            mActivity.setExtraControlVisible(false);
            mActivity.cancelEditText();
            mActivity.setBtnDeleteVisible(false);
            mActivity.hideStatusBar();
        } else {
            drawBorder(true);
            mActivity.setExtraControlVisible(true);
            mActivity.restoreExtraControl(timeline);
            mActivity.setFloatTextVisible(timeline);
            mActivity.setBtnEditTextVisible(true);
            mActivity.setBtnDeleteVisible(true);
        }
        invalidate();
        }
    };
    private void log(String msg){
        Log.e("Log for FloatImage",msg);
    }
}
