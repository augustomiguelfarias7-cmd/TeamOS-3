package com.teamos.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class Workspace extends ViewGroup {

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private int mCurrentScreen = 0;
    private int mTouchSlop;
    private float mLastMotionX;
    private boolean mIsBeingDragged = false;

    private OnPageChangeListener mOnPageChangeListener;

    public interface OnPageChangeListener {
        void onPageChanged(int currentPage);
    }

    public Workspace(Context context) {
        this(context, null);
    }

    public Workspace(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Workspace(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
        if (listener != null) {
            listener.onPageChanged(mCurrentScreen);
        }
    }

    public int getCurrentPage() {
        return mCurrentScreen;
    }

    public CellLayout getCellLayoutAt(int index) {
        if (index >= 0 && index < getChildCount()) {
            return (CellLayout) getChildAt(index);
        }
        return null;
    }

    public void snapToPage(int page) {
        page = Math.max(0, Math.min(page, getChildCount() - 1));
        mCurrentScreen = page;
        int deltaX = page * getWidth() - getScrollX();
        mScroller.startScroll(getScrollX(), 0, deltaX, 0, 400); // 400ms duration
        invalidate();
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageChanged(mCurrentScreen);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        scrollTo(mCurrentScreen * widthSize, 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int childLeft = 0;
        int width = r - l;
        int height = b - t;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.layout(childLeft, 0, childLeft + width, height);
                childLeft += width;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && mIsBeingDragged) {
            return true;
        }

        final float x = ev.getX();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                if (xDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                    mLastMotionX = x;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mIsBeingDragged = !mScroller.isFinished();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        final int action = event.getAction();
        final float x = event.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsBeingDragged) {
                    int deltaX = (int) (mLastMotionX - x);
                    mLastMotionX = x;

                    // Bound checks
                    int scrollX = getScrollX() + deltaX;
                    int maxScrollX = (getChildCount() - 1) * getWidth();
                    if (scrollX < 0) {
                        scrollX = 0;
                    } else if (scrollX > maxScrollX) {
                        scrollX = maxScrollX;
                    }
                    scrollTo(scrollX, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    mVelocityTracker.computeCurrentVelocity(1000);
                    float velocityX = mVelocityTracker.getXVelocity();

                    int page;
                    if (velocityX > 500 && mCurrentScreen > 0) {
                        page = mCurrentScreen - 1;
                    } else if (velocityX < -500 && mCurrentScreen < getChildCount() - 1) {
                        page = mCurrentScreen + 1;
                    } else {
                        // Snap to closest screen
                        int screenWidth = getWidth();
                        page = (getScrollX() + screenWidth / 2) / screenWidth;
                    }
                    snapToPage(page);
                    mIsBeingDragged = false;
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
        }
        return true;
    }
}
