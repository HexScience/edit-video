package com.hecorat.azplugin2.addtext;

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
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.database.TextObject;
import com.hecorat.azplugin2.main.MainActivity;
import com.hecorat.azplugin2.timeline.ExtraTL;


/**
 * Created by TienDam on 11/14/2016.
 */

public class FloatText extends AppCompatImageView {
    public Bitmap rotateBitmap, scaleBitmap;
    public Paint paint;
    public RelativeLayout.LayoutParams params;
    public Rect rectBorder, rectBackground, rectBound;
    public MainActivity mActivity;
    public ExtraTL timeline;
    public Point initBorderBottomRight, initCenterPoint, initBorderTopLeft, initBorderTopRight, initBorderBottomLeft,
            initTopRightPoint, initBottomLeftPoint, initTopLeftPoint, initBottomRightPoint;
    public String text;
    public TextPaint textPaint;
    public Rect bound;
    public Typeface mTypeface;
    public String fontPath;
    private Matrix matrix;
    private StaticLayout textLayout;
    private DashPathEffect dashPathEffect;

    public int width, height;
    public float x, y, xExport, yExport,
            xMax, yMax, xMin, yMin;
    public float rotation = 0;
    public float[] borderBottomRight, centerPoint, borderTopLeft, borderBottomLeft, borderTopRight,
            topRightPoint, bottomLeftPoint, topLeftPoint, bottomRightPoint;
    public float scaleValue = 1f;
    public float widthScale, heightScale;
    public boolean isCompact;
    public boolean drawBorder;
    public int widthMax, heightMax;
    public int mStyle;
    public int mColor;
    public int mBackgroundColor;
    public float size, sizeScale;
    public int fontId;
    public boolean isWaterMark;
    private boolean firstTime;

    public static final int ROTATE_CONSTANT = 30;
    public static final int INIT_X = 300, INIT_Y = 300, INIT_SIZE = 60;
    public static final int PADDING = 30;

    FloatText(Context context) {super(context);}

    public FloatText(MainActivity activity, String text, boolean isWaterMark) {
        super(activity);
        this.isWaterMark = isWaterMark;
        x = INIT_X;
        y = INIT_Y;
        if (isWaterMark) {
            size = 35;
            sizeScale = size;
        } else {
            size = INIT_SIZE;
            sizeScale = size;
        }
        this.text = text;
        mActivity = activity;
        fontId = 0;
        fontPath = mActivity.mFontPath.get(fontId);
        mTypeface = Typeface.createFromFile(fontPath);
        paint = new Paint();
        textPaint = new TextPaint();
        textPaint.setTextSize(size);
        setText(text);

        borderTopLeft = new float[2];
        borderBottomRight = new float[2];
        borderBottomLeft = new float[2];
        borderTopRight = new float[2];
        centerPoint = new float[2];
        topRightPoint = new float[2];
        bottomLeftPoint = new float[2];
        topLeftPoint = new float[2];
        bottomRightPoint = new float[2];

        if (isWaterMark) {
            rotateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_remove_watermark);
            mColor = Color.WHITE;
        } else {
            rotateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rotate);
            mColor = Color.RED;
        }
        drawBorder(true);
        scaleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_scale);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setFullLayout();
        setOnTouchListener(onTouchListener);
        setOnClickListener(onClickListener);

        mBackgroundColor = ContextCompat.getColor(mActivity, R.color.init_bgr_float_text);

        matrix = new Matrix();
        dashPathEffect = new DashPathEffect(new float[] {8,6}, 0);
        firstTime = true;
    }

    public void restoreState(TextObject textObject) {
        x = Float.parseFloat(textObject.x);
        y = Float.parseFloat(textObject.y);
        scaleValue = Float.parseFloat(textObject.scale);
        rotation = Float.parseFloat(textObject.rotation);

        size = Float.parseFloat(textObject.size);
        fontId = Integer.parseInt(textObject.fontId);
        fontPath = textObject.fontPath;
        mColor = Integer.parseInt(textObject.fontColor);
        mBackgroundColor = Integer.parseInt(textObject.boxColor);
        sizeScale = size * scaleValue;
        mTypeface = Typeface.createFromFile(fontPath);
        resetLayout();
    }

    private void invalidateText() {
        textPaint.setColor(mColor);
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        textLayout = new StaticLayout(text, textPaint, width,
                Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
        invalidate();
    }

    public void setTextBgrColor(int color){
        mBackgroundColor = color;
        invalidateText();
    }

    public void setTextColor(int color){
        mColor = color;
        invalidateText();
    }

    public void setText(String text){
        this.text = text;
        resetLayout();
    }

    public void setStyle(int style){
        mStyle = style;
        resetLayout();
    }

    public void setFont(String fontPath, int position){
        this.fontId = position;
        this.fontPath = fontPath;
        mTypeface = Typeface.createFromFile(fontPath);
        resetLayout();
    }

    private void updateTextDimesions(){
        String[] lines = text.split("\n");
        int lineCount = lines.length;

        float maxWidth = 0;
        for (String line: lines) {
            float lineWidth = textPaint.measureText(line);
            if (maxWidth < lineWidth) {
                maxWidth = lineWidth;
            }
        }
        width = (int) maxWidth;

        int lineSpace = 3;
        bound = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bound);
        height = bound.height()*lineCount + lineSpace*(lineCount-1);

        widthScale = width * scaleValue;
        heightScale = height * scaleValue;
    }

    public void resetLayout(){
        textPaint.setTypeface(Typeface.create(mTypeface, mStyle));
        updateTextDimesions();

        rectBorder = new Rect(-PADDING, -PADDING, width+PADDING, height+PADDING);

        rectBackground = new Rect(-PADDING, -PADDING, width+PADDING, height+PADDING);
        widthScale = width*scaleValue;
        heightScale = height*scaleValue;

        initBorderTopLeft = new Point(-PADDING, -PADDING);
        initCenterPoint = new Point(width/2, height/2);
        initBorderBottomRight = new Point(width+PADDING, height+PADDING);
        initBorderBottomLeft = new Point(-PADDING, height+PADDING);
        initBorderTopRight = new Point(width+PADDING, -PADDING);
        initBottomLeftPoint = new Point(0, height);
        initTopRightPoint = new Point(width, 0);
        initTopLeftPoint = new Point(0, 0);
        initBottomRightPoint = new Point(width, height);
        invalidateText();
    }

    public void drawBorder(boolean draw){
        drawBorder = draw;
        if (draw) {
            bringToFront();
        }
        invalidateText();
    }

    private void setFullLayout(){
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.topMargin = 0;
        params.leftMargin = 0;
        setLayoutParams(params);
        invalidateText();
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
        if (Math.abs(borderBottomRight[0]-centerPoint[0])>100){
            if (borderBottomRight[0] >= centerPoint[0]) {
                widthScale += moveX;
            } else {
                widthScale -= moveX;
            }
            scaleValue = widthScale/width;
            heightScale = scaleValue*height;
        } else {
            if (borderBottomRight[1] >= centerPoint[1]){
                heightScale += moveY;
            } else {
                heightScale -= moveY;
            }
            scaleValue = heightScale/height;
            widthScale = scaleValue*width;
        }
        sizeScale = size*scaleValue;
        invalidateText();
    }

    public void moveText(float moveX, float moveY) {
        x += moveX;
        y += moveY;
        invalidateText();
    }

    public void setWaterMarkPosition(float x, float y) {
        this.x = x;
        this.y = y;
        invalidateText();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        initBorderPoints();

        matrix.reset();

        matrix.postTranslate(x, y);

        matrix.mapPoints(centerPoint);

        matrix.postScale(scaleValue, scaleValue, centerPoint[0], centerPoint[1]);
        matrix.postRotate(-rotation, centerPoint[0], centerPoint[1]);

        getBorderPointsCoord(matrix);
        getLayoutLimit();

        // befor N canvas apply matrix from leftside of screen
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            matrix.postTranslate(mActivity.mVideoViewLeft, 0);
            rectBound = canvas.getClipBounds();
            canvas.clipRect(rectBound.left, rectBound.top,
                    rectBound.right + mActivity.mVideoViewLeft, rectBound.bottom, Region.Op.REPLACE);
        }

        canvas.setMatrix(matrix);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mBackgroundColor);
        canvas.drawRect(rectBackground, paint);

        if (firstTime) {
            invalidateText();
            firstTime = false;
        }

        textLayout.draw(canvas);

        if (!drawBorder) {
            return;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(3);
        paint.setPathEffect(dashPathEffect);
        canvas.drawRect(rectBorder, paint);
        canvas.restore();

        canvas.drawBitmap(rotateBitmap, borderTopLeft[0]-ROTATE_CONSTANT, borderTopLeft[1]-ROTATE_CONSTANT, paint);
        if (isWaterMark) {
            return;
        }
        canvas.drawBitmap(scaleBitmap, borderBottomRight[0]-ROTATE_CONSTANT, borderBottomRight[1]-ROTATE_CONSTANT, paint);
    }

    private void getLayoutLimit(){
        // text x, y
        xExport = Math.min(Math.min(Math.min(topLeftPoint[0], bottomRightPoint[0]), bottomLeftPoint[0]), topRightPoint[0]);
        yExport = Math.min(Math.min(Math.min(topLeftPoint[1], bottomRightPoint[1]), bottomLeftPoint[1]), topRightPoint[1]);
        // border x, y
        xMax = Math.max(Math.max(Math.max(borderTopRight[0], borderTopLeft[0]), borderBottomLeft[0]), borderBottomRight[0]);
        yMax = Math.max(Math.max(Math.max(borderTopRight[1], borderTopLeft[1]), borderBottomLeft[1]), borderBottomRight[1]);
        xMin = Math.min(Math.min(Math.min(borderTopRight[0], borderTopLeft[0]), borderBottomLeft[0]), borderBottomRight[0]);
        yMin = Math.min(Math.min(Math.min(borderTopRight[1], borderTopLeft[1]), borderBottomLeft[1]), borderBottomRight[1]);
        widthMax = (int)(xMax - xMin);
        heightMax = (int) (yMax - yMin);
    }

    private void getBorderPointsCoord(Matrix matrix){
        matrix.mapPoints(borderBottomRight);
        matrix.mapPoints(borderTopLeft);
        matrix.mapPoints(borderBottomLeft);
        matrix.mapPoints(borderTopRight);

        matrix.mapPoints(bottomLeftPoint);
        matrix.mapPoints(topRightPoint);
        matrix.mapPoints(topLeftPoint);
        matrix.mapPoints(bottomRightPoint);
    }

    private void initBorderPoints(){
        borderTopLeft[0] = initBorderTopLeft.x;
        borderTopLeft[1] = initBorderTopLeft.y;
        borderBottomRight[0] = initBorderBottomRight.x;
        borderBottomRight[1] = initBorderBottomRight.y;
        borderTopRight[0] = initBorderTopRight.x;
        borderTopRight[1] = initBorderTopRight.y;
        borderBottomLeft[0] = initBorderBottomLeft.x;
        borderBottomLeft[1] = initBorderBottomLeft.y;

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
                    if (oldX < borderBottomRight[0]+eps && oldX > borderBottomRight[0]-eps && oldY < borderBottomRight[1]+eps && oldY > borderBottomRight[1]-eps){
                        touch = 1;
                    } else if (oldX < borderTopLeft[0]+eps && oldX > borderTopLeft[0]-eps && oldY < borderTopLeft[1]+eps && oldY > borderTopLeft[1]-eps){
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

                    if (!isTouch || !drawBorder || isWaterMark) {
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
                        invalidateText();
                        startAngle = currentAngle;
                    }
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isTouch){
                        if (touch == 3 && isWaterMark && drawBorder) {
                            mActivity.askDonate();
                        } else if (touch != 0) {
                            performClick();
                        } else {
                            mActivity.setFloatTextVisible(oldX, oldY);
                            mActivity.setFloatImageVisible(oldX, oldY);
                        }
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
            if (!isWaterMark) {
                mActivity.setExtraControlVisible(false);
                mActivity.cancelEditText();
                mActivity.slideExtraToolsIn(false);
            }

        } else {
            drawBorder(true);
            if (!isWaterMark) {
                mActivity.setExtraControlVisible(true);
                mActivity.restoreExtraControl(timeline);
                mActivity.setBtnEditVisible(true);
                mActivity.setBtnTrimVisible(false);
                mActivity.setBtnDeleteVisible(true);
                mActivity.setBtnVolumeVisible(false);
                mActivity.updateLayoutEditText();
                mActivity.slideExtraToolsIn(true);
            } else {
                mActivity.slideExtraToolsIn(false);
            }
            mActivity.unhighlightVideoTL();
            mActivity.setFloatTextVisible(timeline);
            mActivity.pausePreview();
        }
        invalidateText();
        }
    };
    private void log(String msg){
        Log.e("Log for FloatImage",msg);
    }
}
