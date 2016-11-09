package com.hecorat.editvideo.timeline;

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

import com.hecorat.editvideo.main.Constants;

/**
 * Created by bkmsx on 01/11/2016.
 */
public class ExtraTimeLineControl extends ImageView {

    public static final int THUMB_WIDTH = 30, LINE_HEIGHT=4, ROUND = 10;
    public int width, height;
    public int left, right;
    public int min;
    public boolean inLayoutImage;
    public RectF thumbLeft, thumbRight;
    public Rect lineAbove, lineBelow;
    public Paint paint;
    public RelativeLayout.LayoutParams params;
    public OnExtraTimeLineControlChanged mOnControlTimeLineChanged;

    public ExtraTimeLineControl(Context context, int leftMargin, int widthTimeLine, int heightTimeLine) {
        super(context);
        left = leftMargin;
        width = widthTimeLine;
        height = heightTimeLine;
        right = left + width;
        min = Constants.MARGIN_LEFT_TIME_LINE;
        paint = new Paint();
        params = new RelativeLayout.LayoutParams(widthTimeLine + 2 * THUMB_WIDTH, height);

        updateLayoutWidth(left, right);
        setOnTouchListener(onTouchListener);
        mOnControlTimeLineChanged = (OnExtraTimeLineControlChanged) context;
    }

    public void restoreTimeLineStatus(ExtraTimeLine extraTimeLine) {
        left = extraTimeLine.left;
        right = extraTimeLine.right;
        width = right - left;
        inLayoutImage = extraTimeLine.inLayoutImage;
        updateLayoutWidth(left, right);
    }

    public void updateLayoutMatchParent(int left, int right) {
        width = right - left;
        thumbLeft = new RectF(left - THUMB_WIDTH, 0, left, height);
        thumbRight = new RectF(right, 0, right + THUMB_WIDTH, height);
        lineAbove = new Rect(left - THUMB_WIDTH/2, 0, right+THUMB_WIDTH/2, LINE_HEIGHT);
        lineBelow = new Rect(left - THUMB_WIDTH/2, height-LINE_HEIGHT, right+THUMB_WIDTH/2, height);
        params.leftMargin = 0;
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        setLayoutParams(params);
        invalidate();
    }

    public void updateLayoutWidth(int left, int right) {
        width = right - left;
        thumbLeft = new RectF(0, 0, THUMB_WIDTH, height);
        thumbRight = new RectF(width+THUMB_WIDTH, 0, width + 2*THUMB_WIDTH, height);
        lineAbove = new Rect(THUMB_WIDTH / 2, 0, width + 3*THUMB_WIDTH / 2, LINE_HEIGHT);
        lineBelow = new Rect(THUMB_WIDTH / 2, height - LINE_HEIGHT, width + 3*THUMB_WIDTH / 2, height);

        params.leftMargin = left - THUMB_WIDTH;
        params.width = width + 2 * THUMB_WIDTH;
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

    public interface OnExtraTimeLineControlChanged {
        void updateExtraTimeLine(int left, int right);
        void invisibleExtraControl();
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
        int leftPosition, rightPosition;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    updateLayoutMatchParent(left, right);

                    oldX = motionEvent.getX()+left-THUMB_WIDTH;
                    oldY = motionEvent.getY();

                    if (oldX > right - epsX && oldX < right + epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_RIGHT;
                    } else if (oldX < left + epsX && oldX > left - epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_LEFT;
                    } else {
                        touch = TOUCH_CENTER;
                    }
                    log("touch extra: "+touch);
                    return true;
                case MotionEvent.ACTION_MOVE:

                    moveX = motionEvent.getX() - oldX;

                    if (touch == TOUCH_LEFT) {
                        leftPosition = left + (int) moveX;
                        rightPosition = right;
                        if (leftPosition < min) {
                            leftPosition = min;
                        }
                        if (leftPosition > right - 10) {
                            leftPosition = right - 10;
                        }
                    }
                    if (touch == TOUCH_RIGHT) {
                        leftPosition = left;
                        rightPosition = right + (int) moveX;
                        if (rightPosition < left + 10) {
                            rightPosition = left + 10;
                        }
                    }
                    updateLayoutMatchParent(leftPosition, rightPosition);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (touch == TOUCH_CENTER) {
                        mOnControlTimeLineChanged.invisibleExtraControl();
                        return true;
                    }
                    left = leftPosition;
                    right = rightPosition;
                    updateLayoutWidth(left, right);
                    mOnControlTimeLineChanged.updateExtraTimeLine(left, right);
                    touch = 0;
                    return true;
                default:
                    break;
            }
            return false;
        }
    };
}