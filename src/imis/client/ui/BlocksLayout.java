/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package imis.client.ui;

import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.provider.MediaStore;
import imis.client.R;
import imis.client.ui.adapter.EventsAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Custom layout that contains and organizes a {@link TimeRulerView} and several
 * instances of {@link BlockView}. Also positions current "now" divider using
 * {@link R.id#blocks_now} view when applicable.
 */
public class BlocksLayout extends AdapterView<EventsAdapter> {// implements
    // android.widget.AdapterView.OnItemClickListener
    private static final String TAG = BlocksLayout.class.getSimpleName();
    private static final int INVALID_INDEX = -1;
    private Rect mRect;

    private EventsAdapter mAdapter;
    private TimeRulerView mRulerView = null;
    private View mNowView = null;

    /**
     * User is not touching the list
     */
    private static final int TOUCH_STATE_RESTING = 0;

    /**
     * User is touching the list and right now it's still a "click"
     */
    private static final int TOUCH_STATE_CLICK = 1;

    /**
     * User is scrolling the list
     */
    private static final int TOUCH_STATE_SCROLL = 2;

    /**
     * Current touch state
     */
    private int touchState = TOUCH_STATE_RESTING;

    public BlocksLayout(Context context) {
        this(context, null);
    }

    public BlocksLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlocksLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "BlocksLayout()");

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlocksLayout, defStyle,
                0);

        a.recycle();
    }

    private void ensureChildren() {
        if (mRulerView == null) {
            mRulerView = new TimeRulerView(getContext());
            mRulerView.setDrawingCacheEnabled(true);
            mRulerView.setId(Integer.MAX_VALUE);
            mRulerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            addViewInLayout(mRulerView, -1, mRulerView.getLayoutParams());
        }
        if (mNowView == null) {
            mNowView = new View(getContext());
            mNowView.setDrawingCacheEnabled(true);
            mNowView.setId(Integer.MAX_VALUE - 1);
            mNowView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            //mNowView.setBackgroundColor(R.color.block_column_3);
            NinePatchDrawable buttonDrawable = (NinePatchDrawable) getContext().getResources().getDrawable(
                    R.drawable.now_bar);
            mNowView.setBackgroundDrawable(buttonDrawable);
            addViewInLayout(mNowView, -1, mNowView.getLayoutParams());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("BlocksLayout", "onMeasure()");
        ensureChildren();

        mRulerView.measure(widthMeasureSpec, heightMeasureSpec);
        mNowView.measure(widthMeasureSpec, heightMeasureSpec);

        final int width = mRulerView.getMeasuredWidth();
        final int height = mRulerView.getMeasuredHeight();

        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout()");
        ensureChildren();

        if (mAdapter != null) {
            removeViewsInLayout(0, getChildCount() - 2);
            printAllChilds();
            final int count = mAdapter.getCount();
            Log.d(TAG, "count: " + count);
            for (int i = 0; i < count; i++) {
                final BlockView blockView = (BlockView) mAdapter.getView(i, null, this);
                if (blockView == null) {
                    Log.d(TAG, "onLayout() blockView=null");
                    continue;
                }


                Log.d(TAG, "onLayout() new id: " + blockView.getArriveId());
                blockView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));//TODO staci 1
                blockView.setId(blockView.getArriveId());
                addViewInLayout(blockView, -1, blockView.getLayoutParams(), true);
                bringChildToFront(mRulerView);
                bringChildToFront(mNowView);
            }
        }
        positionItems();
        printAllChilds();
        Log.d(TAG, "onLayout() end");
    }

    private void positionItems() {
        //position rulerview
        final int headerWidth = mRulerView.getHeaderWidth();
        final int columnWidth = getWidth() - headerWidth;
        mRulerView.layout(0, 0, getWidth(), getHeight());

        int top, bottom, left, right;
        //postion blocks
        View block;
        BlockView blockView;
        for (int index = 0; index < getChildCount(); index++) {
            block = getChildAt(index);
            if (block instanceof BlockView) {
                blockView = (BlockView) block;
                top = mRulerView.getTimeVerticalOffset(blockView.getStartTime());
                bottom = mRulerView.getTimeVerticalOffset(blockView.getEndTime());
                left = headerWidth;
                right = left + columnWidth;
                blockView.layout(left, top, right, bottom);
                Log.d(TAG, "left: " + left + " top: " + top + " right: " + right + " bottom: " + bottom
                        + " ruler height: " + mRulerView.getHeight());
            }
        }

        //position now view
        final long now = System.currentTimeMillis();
        top = mRulerView.getTimeVerticalOffset(now);
        bottom = top + mNowView.getMeasuredHeight();
        left = 0;
        right = getWidth();
        mNowView.layout(left, top, right, bottom);
    }


    public int getPosOflastBlock() {
        return 0; // TODO vrati pozici kam se ma nastavait scroll
    }

    @Override
    public EventsAdapter getAdapter() {
        Log.d(TAG, "getAdapter()");
        return mAdapter;
    }

    @Override
    public View getSelectedView() {
        Log.d(TAG, "getSelectedView()");
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void setAdapter(EventsAdapter adapter) {
        Log.d(TAG, "setAdapter()");
        mAdapter = adapter;
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override
    public void setSelection(int arg0) {
        Log.d(TAG, "setSelection()");
        throw new UnsupportedOperationException("Not supported");
    }

    /*
     * @Override public void onItemClick(AdapterView<?> parent, View view, int
     * position, long id) { Log.d(TAG, "onItemClick() position: " + position +
     * " id: " + id);
     *
     * }
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getChildCount() == 0) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.d(TAG, "ACTION_DOWN");
                startTouch();
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d(TAG, "ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                //Log.d(TAG, "ACTION_UP");
                if (touchState == TOUCH_STATE_CLICK) {
                    clickChildAt((int) event.getX(), (int) event.getY());
                }
                endTouch();
                break;
        }
        // return super.onTouchEvent(event);
        return true;
    }

    private void startTouch() {
        touchState = TOUCH_STATE_CLICK;
    }

    private void endTouch() {
        touchState = TOUCH_STATE_RESTING;
    }

    private void clickChildAt(final int x, final int y) {

        final int index = getContainingChildIndex(x, y);
        // Log.d(TAG, "onTouchEvent x:" + x + " y:" + y + " index: " + index);
        if (index != INVALID_INDEX) {
            final View itemView = getChildAt(index);
            final int position = index;
            final long id = Long.valueOf(((BlockView) itemView).getArriveId());// mAdapter.getItemId(position);
            performItemClick(itemView, position, id);
        }
    }

    private int getContainingChildIndex(final int x, final int y) {
        if (mRect == null) {
            mRect = new Rect();
        }
        for (int index = 0; index < getChildCount(); index++) {

            View child = getChildAt(index);
            // Log.d(TAG, "index:" + index + " class: " + child.toString());
            if (child instanceof BlockView) {
                child.getHitRect(mRect);
                if (mRect.contains(x, y)) {
                    return index;
                }
            }

        }
        return INVALID_INDEX;
    }

    private void printAllChilds() {
        for (int index = 0; index < getChildCount(); index++) {
            View child = getChildAt(index);
            Log.d("BlocksLayout", "child i=" + index + " " + child.toString() + " id: " + child.getId());
        }
    }

}
