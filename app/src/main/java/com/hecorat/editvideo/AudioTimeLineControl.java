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
public class AudioTimeLineControl extends ImageView {
    RectF thumbLeft, thumbRight;
    Rect lineAbove, lineBelow;
    int min, max;
    int left , right;
    int height;
    Paint paint;
    RelativeLayout.LayoutParams params;
    OnAudioControlTimeLineChanged mOnAudioControlTimeLineChanged;
    static final int THUMB_WIDTH = 30, LINE_HEIGHT=4, ROUND = 10;

    public AudioTimeLineControl(Context context, int left, int right, int height) {
        super(context);
        mOnAudioControlTimeLineChanged = (OnAudioControlTimeLineChanged) context;
        min = left;
        max = right;
        this.height = height;
        paint = new Paint();
        setOnTouchListener(onTouchListener);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        setLayoutParams(params);
        updateLayoutMatchParent(left, right);
    }

    // layout Match_parent
    public void updateLayoutMatchParent(int left, int right) {
        thumbLeft = new RectF(left - THUMB_WIDTH, 0, left, height);
        thumbRight = new RectF(right, 0, right + THUMB_WIDTH, height);
        lineAbove = new Rect(left - THUMB_WIDTH/2, 0, right + THUMB_WIDTH/2, LINE_HEIGHT);
        lineBelow = new Rect(left- THUMB_WIDTH/2, height - LINE_HEIGHT, right+THUMB_WIDTH/2, height);
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.leftMargin = 0;
        setLayoutParams(params);
        invalidate();
    }

    // layout with exact width
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

    public void restoreTimeLineStatus(AudioTimeLine audioTimeLine) {
        min = audioTimeLine.min;
        max = audioTimeLine.max;
        left = audioTimeLine.left;
        right = audioTimeLine.right;
        updateLayoutWidth(left, right);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.CYAN);
        canvas.drawRoundRect(thumbLeft, ROUND, ROUND, paint);
        canvas.drawRoundRect(thumbRight, ROUND, ROUND, paint);
        canvas.drawRect(lineAbove, paint);
        canvas.drawRect(lineBelow, paint);
    }

    public interface OnAudioControlTimeLineChanged {
        void updateAudioTimeLine(int start, int end);
        void invisibleAudioControl();
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
                    oldX = motionEvent.getX() + left - THUMB_WIDTH;
                    oldY = motionEvent.getY() ;
                    if (oldX > right - epsX && oldX < right + epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_RIGHT;
                    } else if (oldX < left + epsX && oldX > left - epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_LEFT;
                    } else {
                        touch = TOUCH_CENTER;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE:

                    moveX = motionEvent.getX() - oldX;
                    moveY = motionEvent.getY() - oldY;
                    if (touch == TOUCH_LEFT) {
                        startPosition = left + (int)moveX;
                        endPosition = right;
                        if (startPosition < min) {
                            startPosition = min;
                        }

                        if (startPosition < Constants.MARGIN_LEFT_TIME_LINE) {
                            startPosition = Constants.MARGIN_LEFT_TIME_LINE;
                        }

                        if (startPosition > right - 10) {
                             startPosition = right - 10;
                        }
                    }
                    if (touch == TOUCH_RIGHT) {
                        startPosition = left;
                        endPosition = right + (int) moveX;
                        if (endPosition > max) {
                            endPosition = max;
                        }
                        if (endPosition < left + 10) {
                            endPosition = left + 10;
                        }
                    }

                    updateLayoutMatchParent(startPosition, endPosition);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (touch == TOUCH_CENTER) {
                        mOnAudioControlTimeLineChanged.invisibleAudioControl();
                        return true;
                    }
                    left = startPosition;
                    right = endPosition;
                    updateLayoutWidth(left, right);
                    mOnAudioControlTimeLineChanged.updateAudioTimeLine(left, right);
                    touch = 0;
                    return true;
                default:
                    break;
            }
            return false;
        }
    };

    private void log(String msg) {
        Log.e("Log for audio Timeline", msg);
    }
}