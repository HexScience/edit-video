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
public class MainTimeLineControl extends ImageView {
    int width, height;
    static final int THUMB_WIDTH = 30;
    int lineWeight = 4;
    int round = 10;
    int minPosition, maxPosition, currentMinPosition, currentMaxPosition;
    int start, end;
    int startTime, endTime;
    RectF thumbLeft, thumbRight;
    Rect lineAbove, lineBelow;
    Paint paint;
    RelativeLayout.LayoutParams params;
    OnControlTimeLineChanged mOnControlTimeLineChanged;

    public MainTimeLineControl(Context context, int widthTimeLine, int heightTimeLine) {
        super(context);
        width = widthTimeLine + THUMB_WIDTH * 2;
        height = heightTimeLine;
        minPosition = 0;
        maxPosition = widthTimeLine;
        currentMaxPosition = maxPosition;
        currentMinPosition = 0;
        start = minPosition;
        end = maxPosition;
        paint = new Paint();
        params = new RelativeLayout.LayoutParams(width, height);
        setLayoutParams(params);
        updateLayout(minPosition, maxPosition, true);
        setOnTouchListener(onTouchListener);
        mOnControlTimeLineChanged = (OnControlTimeLineChanged) context;
    }

    public void updateLayout(int start, int end) {
        start += THUMB_WIDTH;
        end += THUMB_WIDTH;
        thumbLeft = new RectF(start - THUMB_WIDTH, 0, start, height);
        thumbRight = new RectF(end, 0, end + THUMB_WIDTH, height);
        lineAbove = new Rect(start - THUMB_WIDTH / 2, 0, end + THUMB_WIDTH / 2, lineWeight);
        lineBelow = new Rect(start - THUMB_WIDTH / 2, height - lineWeight, end + THUMB_WIDTH / 2, height);
        invalidate();
    }

    public void updateLayout(int start, int end, boolean changeWidth) {
        if (changeWidth) {
            params.width = end -start +2* THUMB_WIDTH;
            setLayoutParams(params);
        }
        updateLayout(start, end);
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

    public interface OnControlTimeLineChanged {
        void updateTimeLine(int start, int end);
        void invisibleControl();
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
        boolean negativeTouch = false;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();
                    if (oldX < start + epsX && oldX > start - epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_LEFT;
                    }
                    else if (oldX > end - epsX && oldX < end + epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_RIGHT;
                    }
                    else {
                        touch = TOUCH_CENTER;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:

                    moveX = motionEvent.getX() - oldX;
                    moveY = motionEvent.getY() - oldY;
                    if (touch == TOUCH_LEFT) {
                        startPosition = start + (int) moveX;
                        endPosition = end;
                        if (startPosition > end) {
                            startPosition = end;
                        }
                        if (startPosition < minPosition) {

                            if (currentMinPosition == 0) {
                                startPosition = minPosition;
                            } else {
                                negativeTouch = true;
                            }
                        }
                        updateLayout(startPosition, endPosition, false);
                    }
                    if (touch == TOUCH_RIGHT) {
                        startPosition = start;
                        endPosition = end + (int) moveX;
                        if (endPosition > currentMaxPosition) {
                            endPosition = currentMaxPosition;
                        }
                        if (endPosition < minPosition) {
                            endPosition = minPosition;
                        }
                        updateLayout(startPosition, endPosition, false);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    if (touch == TOUCH_CENTER) {
                        mOnControlTimeLineChanged.invisibleControl();
                        return true;
                    }
                    int currentDuration = endPosition - startPosition;
                    start = minPosition;
                    end = start + currentDuration;
                    if (touch == TOUCH_LEFT) {
                        currentMaxPosition -= moveX;
                        currentMinPosition += moveX;
                    }
                    if (currentMaxPosition > maxPosition) {
                        currentMaxPosition = maxPosition;
                        currentMinPosition = 0;
                    }
                    if (end>maxPosition) {
                        end = maxPosition;
                    }
                    currentDuration = end - start;
                    startTime = currentMinPosition*Constants.SCALE_VALUE;
                    endTime = (currentMinPosition + currentDuration) * Constants.SCALE_VALUE;
                    mOnControlTimeLineChanged.updateTimeLine(startTime, endTime);

                    updateLayout(start, end, true);
                    touch = 0;
                    return true;
                default:
                    break;
            }
            return false;
        }
    };
}