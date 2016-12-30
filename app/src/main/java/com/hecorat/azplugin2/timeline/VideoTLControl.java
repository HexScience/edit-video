package com.hecorat.azplugin2.timeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hecorat.azplugin2.main.Constants;

/**
 * Created by bkmsx on 01/11/2016.
 */
public class VideoTLControl extends ImageView {
    public int width, height;
    public static final int THUMB_WIDTH = 30;
    public static final int LINE_HEIGHT = 4;
    public static final int ROUND = 10;
    public int min, max;
    public int left, right;
    public RectF thumbLeft, thumbRight;
    public Rect lineAbove, lineBelow;
    public Paint paint;
    public RelativeLayout.LayoutParams params;
    public OnControlTimeLineChanged mOnControlTimeLineChanged;

    public VideoTLControl(Context context, int widthTimeLine, int heightTimeLine, int left) {
        super(context);
        mOnControlTimeLineChanged = (OnControlTimeLineChanged) context;
        width = widthTimeLine;
        height = heightTimeLine;
        this.left = left;
        right = left + width;
        min = left;
        max = left + width;

        paint = new Paint();
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        setLayoutParams(params);
        updateLayoutMatchParent(left, right);
        setOnTouchListener(onTouchListener);
    }

    public void restoreTimeLineStatus(VideoTL videoTL) {
        min = videoTL.min;
        max = videoTL.max;
        left = videoTL.left;
        width = videoTL.width;
        right = left + width;
        updateLayoutWidth(left, right);
    }

    public void updateLayoutMatchParent(int left, int right) {
        thumbLeft = new RectF(left - THUMB_WIDTH, 0, left, height);
        thumbRight = new RectF(right, 0, right+THUMB_WIDTH, height );
        lineAbove = new Rect(left - THUMB_WIDTH/2, 0, right+THUMB_WIDTH/2, LINE_HEIGHT);
        lineBelow = new Rect(left-THUMB_WIDTH/2, height-LINE_HEIGHT, right+THUMB_WIDTH/2, height);
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.leftMargin = 0;
        setLayoutParams(params);
        invalidate();
    }

    // layout with exact widthSeekbar
    public void updateLayoutWidth(int left, int right) {
        int widthTimeLine = right - left;
        thumbLeft = new RectF(0, 0, THUMB_WIDTH, height);
        thumbRight = new RectF(widthTimeLine+THUMB_WIDTH, 0, widthTimeLine+2*THUMB_WIDTH, height);
        lineAbove = new Rect(THUMB_WIDTH/2, 0, widthTimeLine+(int)(1.5*THUMB_WIDTH), LINE_HEIGHT);
        lineBelow = new Rect(THUMB_WIDTH/2, height - LINE_HEIGHT, widthTimeLine+(int)(1.5*THUMB_WIDTH), height);
        params.leftMargin = left - THUMB_WIDTH;
        params.width = widthTimeLine + 2*THUMB_WIDTH;
        setLayoutParams(params);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.CYAN);
        canvas.drawRect(lineAbove, paint);
        canvas.drawRect(lineBelow, paint);
        canvas.drawRoundRect(thumbLeft, ROUND, ROUND, paint);
        canvas.drawRoundRect(thumbRight, ROUND, ROUND, paint);
    }

    public interface OnControlTimeLineChanged {
        void updateVideoTimeLine(int leftPosition, int width);
        void invisibleVideoControl();
        void seekTo(int value, boolean scroll);
    }

    private void log(String msg) {
        Log.e("Log for Control", msg);
    }

    OnTouchListener onTouchListener = new OnTouchListener() {
        float oldX, oldY, moveX, moveY;
        float epsX = 100;
        float epsY = 20;
        int touch = 0;
        int TOUCH_LEFT = 1;
        int TOUCH_RIGHT = 2;
        int TOUCH_CENTER = 3;
        int startPosition, endPosition;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    updateLayoutMatchParent(left, right);
                    oldX = motionEvent.getX()+left - THUMB_WIDTH;
                    oldY = motionEvent.getY();
                    if (oldX < left + epsX && oldX > left - epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_LEFT;
                    }
                    else if (oldX > right - epsX && oldX < right + epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_RIGHT;
                    }
                    else {
                        touch = TOUCH_CENTER;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE:
                    moveX = motionEvent.getX() - oldX;
                    if (touch == TOUCH_LEFT) {
                        startPosition = left + (int) moveX;
                        endPosition = right;
                        if (startPosition < min) {
                            startPosition = min;
                        }
                        if (startPosition > right-10) {
                            startPosition = right - 10;
                        }
                        int currentVideo = (startPosition - min)* Constants.SCALE_VALUE;
                        mOnControlTimeLineChanged.seekTo(currentVideo, false);
                    }
                    if (touch == TOUCH_RIGHT) {
                        startPosition = left;
                        endPosition = right + (int) moveX;
                        if (endPosition > max) {
                            endPosition = max;
                        }
                        if (endPosition < left+10) {
                            endPosition = left+10;
                        }
                        int currentVideo = (endPosition - min)* Constants.SCALE_VALUE;
                        mOnControlTimeLineChanged.seekTo(currentVideo, false);
                    }
                    updateLayoutMatchParent(startPosition, endPosition);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (touch == TOUCH_CENTER) {
                        mOnControlTimeLineChanged.invisibleVideoControl();
                        return true;
                    }
                    width = endPosition - startPosition;
                    right = left + width;
                    min += left - startPosition;
                    max += left - startPosition;
                    updateLayoutWidth(left, right);
                    int currentVideo;
                    if (touch == TOUCH_LEFT) {
                        currentVideo = (left - min)* Constants.SCALE_VALUE;
                    } else {
                        currentVideo = (right - min)* Constants.SCALE_VALUE;
                    }
                    mOnControlTimeLineChanged.seekTo(currentVideo, true);
                    mOnControlTimeLineChanged.updateVideoTimeLine(startPosition, width);
                    touch = 0;
                    return true;
                default:
                    break;
            }
            return false;
        }
    };
}