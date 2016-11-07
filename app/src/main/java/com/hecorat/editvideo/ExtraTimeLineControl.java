package com.hecorat.editvideo;

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

/**
 * Created by bkmsx on 01/11/2016.
 */
public class ExtraTimeLineControl extends ImageView {
    int widthTimeLine, height;
    static final int THUMB_WIDTH = 30;
    int lineWeight = 4;
    int round = 10;
    int start, end;
    int startTime, endTime;
    int limitLeftMargin;
    int leftMargin;
    boolean inLayoutImage;
    RectF thumbLeft, thumbRight;
    Rect lineAbove, lineBelow;
    Paint paint;
    RelativeLayout.LayoutParams params;
    OnExtraTimeLineControlChanged mOnControlTimeLineChanged;
    ExtraTimeLineStatus extraTimeLineStatus;

    public ExtraTimeLineControl(Context context, int leftMargin, int widthTimeLine, int heightTimeLine) {
        super(context);
        this.widthTimeLine = widthTimeLine;
        height = heightTimeLine;
        paint = new Paint();
        params = new RelativeLayout.LayoutParams(widthTimeLine + 2 * THUMB_WIDTH, height);
        start = THUMB_WIDTH;
        limitLeftMargin = Constants.MARGIN_LEFT_TIME_LINE - THUMB_WIDTH;
        end = start + widthTimeLine;
        setLayoutParams(params);
        updateLayout(leftMargin, widthTimeLine);
        setOnTouchListener(onTouchListener);
        mOnControlTimeLineChanged = (OnExtraTimeLineControlChanged) context;
    }

    private void updateLayoutRuntime(int startPosition, int endPosition) {
        thumbLeft = new RectF(startPosition - THUMB_WIDTH, 0, startPosition, height);
        thumbRight = new RectF(endPosition, 0, endPosition + THUMB_WIDTH, height);
        lineAbove = new Rect(startPosition - THUMB_WIDTH / 2, 0, endPosition + THUMB_WIDTH / 2, lineWeight);
        lineBelow = new Rect(startPosition - THUMB_WIDTH / 2, height - lineWeight, endPosition + THUMB_WIDTH / 2, height);
        invalidate();
    }

    public void updateLayout(int leftMargin, int widthTimeLine) {
        this.widthTimeLine = widthTimeLine;
        this.leftMargin = leftMargin;

        int start = THUMB_WIDTH;
        int end = start + widthTimeLine;
        thumbLeft = new RectF(start - THUMB_WIDTH, 0, start, height);
        thumbRight = new RectF(end, 0, end + THUMB_WIDTH, height);
        lineAbove = new Rect(start - THUMB_WIDTH / 2, 0, end + THUMB_WIDTH / 2, lineWeight);
        lineBelow = new Rect(start - THUMB_WIDTH / 2, height - lineWeight, end + THUMB_WIDTH / 2, height);

        params.leftMargin = leftMargin;
        params.width = widthTimeLine + 2 * THUMB_WIDTH;
        setLayoutParams(params);

        invalidate();
        log("Left Margin: " + leftMargin + " Width: " + widthTimeLine);
    }

    public void saveTimeLineStatus(ExtraTimeLine extraTimeLine) {
        extraTimeLineStatus = extraTimeLine.timeLineStatus;
        extraTimeLineStatus.leftMargin = leftMargin;
        extraTimeLineStatus.widthTimeLine = widthTimeLine;
        extraTimeLineStatus.startTime = startTime;
        extraTimeLineStatus.endTime = endTime;
        extraTimeLineStatus.inLayoutImage = inLayoutImage;
    }

    public void restoreTimeLineStatus(ExtraTimeLine extraTimeLine) {
        extraTimeLineStatus = extraTimeLine.timeLineStatus;
        leftMargin = extraTimeLineStatus.leftMargin;
        widthTimeLine = extraTimeLineStatus.widthTimeLine;
        startTime = extraTimeLineStatus.startTime;
        endTime = extraTimeLineStatus.endTime;
        start = THUMB_WIDTH;
        end = widthTimeLine + THUMB_WIDTH;
        inLayoutImage = extraTimeLineStatus.inLayoutImage;
        updateLayout(leftMargin, widthTimeLine);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.CYAN);
        canvas.drawRect(lineAbove, paint);
        canvas.drawRect(lineBelow, paint);
        canvas.drawRoundRect(thumbLeft, round, round, paint);
        canvas.drawRoundRect(thumbRight, round, round, paint);
    }

    public interface OnExtraTimeLineControlChanged {
        void updateExtraTimeLine(int start, int end);
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
        int startPosition, endPosition, margin;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();

                    if (oldX > end - epsX && oldX < end + epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_RIGHT;
                    } else if (oldX < start + epsX && oldX > start - epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_LEFT;
                    } else {
                        touch = TOUCH_CENTER;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:

                    moveX = motionEvent.getX() - oldX;
                    moveY = motionEvent.getY() - oldY;
                    if (touch == TOUCH_LEFT) {
                        startPosition = start + (int) moveX;
                        endPosition = end;
                        margin = leftMargin + (int) moveX;
                        if (startPosition > end - 20) {
                            startPosition = end - 20;
                            margin = leftMargin + startPosition - start;
                        }
                        if (margin < limitLeftMargin) {
                            margin = limitLeftMargin;
                        }
                        updateLayoutRuntime(startPosition, endPosition);
                    }
                    if (touch == TOUCH_RIGHT) {
                        startPosition = start;
                        endPosition = end + (int) moveX;
                        margin = leftMargin;
                        if (endPosition < start + 20) {
                            endPosition = start + 20;
                        }
                        updateLayoutRuntime(startPosition, endPosition);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    if (touch == TOUCH_CENTER) {
                        mOnControlTimeLineChanged.invisibleExtraControl();
                        return true;
                    }
                    startPosition = start + margin - leftMargin;
                    leftMargin = margin;
                    widthTimeLine = endPosition - startPosition;
                    startTime = (leftMargin - limitLeftMargin) * Constants.SCALE_VALUE;
                    endTime = startTime + widthTimeLine * Constants.SCALE_VALUE;
                    mOnControlTimeLineChanged.updateExtraTimeLine(startTime, endTime);

                    updateLayout(leftMargin, widthTimeLine);

                    end = start + widthTimeLine;
                    touch = 0;
                    return true;
                default:
                    break;
            }
            return false;
        }
    };
}