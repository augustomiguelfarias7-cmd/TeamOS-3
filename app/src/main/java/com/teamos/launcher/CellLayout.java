package com.teamos.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CellLayout extends ViewGroup {

    private int mCountX = 4; // default columns
    private int mCountY = 5; // default rows

    private boolean[][] mOccupied;

    public CellLayout(Context context) {
        this(context, null);
    }

    public CellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGrid(4, 5);
    }

    public void initGrid(int countX, int countY) {
        mCountX = countX;
        mCountY = countY;
        mOccupied = new boolean[mCountX][mCountY];
    }

    public boolean findVacantCell(int[] cellXY, int spanX, int spanY) {
        for (int y = 0; y < mCountY; y++) {
            for (int x = 0; x < mCountX; x++) {
                boolean isVacant = true;
                for (int i = 0; i < spanX; i++) {
                    for (int j = 0; j < spanY; j++) {
                        if (x + i >= mCountX || y + j >= mCountY || mOccupied[x + i][y + j]) {
                            isVacant = false;
                            break;
                        }
                    }
                    if (!isVacant) break;
                }
                if (isVacant) {
                    cellXY[0] = x;
                    cellXY[1] = y;
                    return true;
                }
            }
        }
        return false;
    }

    public void markCells(int cellX, int cellY, int spanX, int spanY, boolean occupied) {
        for (int x = cellX; x < cellX + spanX; x++) {
            for (int y = cellY; y < cellY + spanY; y++) {
                if (x >= 0 && x < mCountX && y >= 0 && y < mCountY) {
                    mOccupied[x][y] = occupied;
                }
            }
        }
    }

    public void clearOccupiedCells() {
        mOccupied = new boolean[mCountX][mCountY];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSpecSize, heightSpecSize);

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void measureChild(View child, int widthMeasureSpec, int heightMeasureSpec) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int cellWidth = widthSpecSize / mCountX;
        int cellHeight = heightSpecSize / mCountY;

        int childWidth = lp.spanX * cellWidth;
        int childHeight = lp.spanY * cellHeight;

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;

        int cellWidth = width / mCountX;
        int cellHeight = height / mCountY;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int childLeft = lp.cellX * cellWidth;
                int childTop = lp.cellY * cellHeight;
                child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int cellX;
        public int cellY;
        public int spanX = 1;
        public int spanY = 1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(int cellX, int cellY, int spanX, int spanY) {
            super(MATCH_PARENT, MATCH_PARENT);
            this.cellX = cellX;
            this.cellY = cellY;
            this.spanX = spanX;
            this.spanY = spanY;
        }
    }
}
