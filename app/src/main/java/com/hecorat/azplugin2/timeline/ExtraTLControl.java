package com.hecorat.azplugin2.timeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hecorat.azplugin2.main.MainActivity;

/**
 * Created by bkmsx on 01/11/2016.
 */
public class ExtraTLControl extends AppCompatImageView {

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
    public MainActivity mActivity;

    public int MARGIN_LEFT_TIME_LINE;

    ExtraTLControl(Context context) {super(context);}

    public ExtraTLControl(Context context, int leftMargin, int widthTimeLine, int heightTimeLine) {
        super(context);
        mActivity = (MainActivity) context;
        MARGIN_LEFT_TIME_LINE = mActivity.mLeftMarginTimeLine;
        left = leftMargin;
        width = widthTimeLine;
        height = heightTimeLine;
        right = left + width;
        min = MARGIN_LEFT_TIME_LINE;
        paint = new Paint();
        params = new RelativeLayout.LayoutParams(widthTimeLine + 2 * THUMB_WIDTH, height);

        updateLayoutWidth(left, right);
        setOnTouchListener(onTouchListener);
        mOnControlTimeLineChanged = (OnExtraTimeLineControlChanged) context;
    }

    public void restoreTimeLineStatus(ExtraTL extraTL) {
        left = extraTL.left;
        right = extraTL.right;
        width = right - left;
        inLayoutImage = extraTL.inLayoutImage;
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
        float oldX, oldY, moveX;
        float epsX = 100;
        float epsY = 20;
        int touch = 0;
        int TOUCH_LEFT = 1;
        int TOUCH_RIGHT = 2;
        int TOUCH_CENTER = 3;
        int leftPosition, rightPosition;
        long startTimeTouch;
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    log("extra control -  down");
                    mActivity.mScrollView.scroll = false;
                    updateLayoutMatchParent(left, right);
                    startTimeTouch = System.currentTimeMillis();

                    oldX = motionEvent.getX()+left-THUMB_WIDTH;
                    oldY = motionEvent.getY();

                    if (oldX > right - epsX && oldX < right + epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_RIGHT;
                    } else if (oldX < left + epsX && oldX > left - epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_LEFT;
                    } else {
                        touch = TOUCH_CENTER;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE:
                    log("extra control -  move");
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

                    if (touch == TOUCH_CENTER) {
                        long timeTouch = System.currentTimeMillis() - startTimeTouch;
                        if (timeTouch > 100) {
                            mActivity.startDragExtraTL(ExtraTLControl.this);
                        }
                    }
                    updateLayoutMatchParent(leftPosition, rightPosition);
                    return true;
                case MotionEvent.ACTION_UP:
                    log("extra control -  up");
                    if (touch == TOUCH_CENTER) {
                        mOnControlTimeLineChanged.invisibleExtraControl();
                        return true;
                    }
                    left = leftPosition;
                    right = rightPosition;
                    updateLayoutWidth(left, right);
                    mOnControlTimeLineChanged.updateExtraTimeLine(left, right);
                    mActivity.mScrollView.scroll = true;
                    touch = 0;
                    return true;
                default:
                    break;
            }
            return false;
        }
    };
}