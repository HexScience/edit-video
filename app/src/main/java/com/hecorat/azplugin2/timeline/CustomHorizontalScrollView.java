package com.hecorat.azplugin2.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by bkmsx on 01/11/2016.
 */
public class CustomHorizontalScrollView extends HorizontalScrollView {

    OnCustomScrollChanged mOnCustomScrollChanged;

    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    float startX;
    public boolean scroll = true;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
//        log(" scroll = " + scroll);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                log("intercept - down");
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
//                    log("intercept - move");
                float moveX = Math.abs(event.getX() - startX);
                if (moveX > 50 && scroll) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
//                    log("intercept - up");
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                log("touch - down");
                startX = event.getX();
                if (mOnCustomScrollChanged != null) {
                    mOnCustomScrollChanged.onStartScroll();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
//                log("touch - up");
                if (mOnCustomScrollChanged != null) {
                    mOnCustomScrollChanged.onEndScroll();
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                log("touch - move");
                float x = event.getX();
                float deltaX = x - startX;
                scrollBy((int) (-1 * deltaX), 0);
                startX = event.getX();
                if (mOnCustomScrollChanged != null) {
                    mOnCustomScrollChanged.onScrollChanged();
                }
                break;
        }

        return true;
    }

    public void setOnCustomScrollChanged(OnCustomScrollChanged listener) {
        mOnCustomScrollChanged = listener;
    }

    public interface OnCustomScrollChanged {
        void onScrollChanged();

        void onStartScroll();

        void onEndScroll();
    }

    private void log(String msg) {
        Log.e("Horizotal Scrollview", msg);
    }
}
