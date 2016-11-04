package com.hecorat.editvideo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by bkmsx on 01/11/2016.
 */
public class CustomHorizontalScrollView extends HorizontalScrollView {

    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    float startX;
    public boolean scroll = true;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (scroll) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveX = Math.abs(event.getX()-startX);
                    if (moveX>100) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float deltaX = x - startX;
                smoothScrollBy((int)(-3*deltaX), 0);
                startX = event.getX();
                break;
        }

        return true;
    }

    private void log(String msg){
        Log.e("Horizotal Scrollview", msg);
    }
}
