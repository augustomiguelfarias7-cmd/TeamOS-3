package com.teamos.launcher;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

    public interface OnGestureListener {
        void onSwipeUp();
        void onSwipeDown();
        void onDoubleTap();
    }

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private final OnGestureListener mListener;

    public SwipeGestureDetector(OnGestureListener listener) {
        this.mListener = listener;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null) {
            return false;
        }
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffX) < Math.abs(diffY)) {
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    mListener.onSwipeDown();
                } else {
                    mListener.onSwipeUp();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        mListener.onDoubleTap();
        return true;
    }
}
