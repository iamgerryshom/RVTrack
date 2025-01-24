package com.wid.rvtrack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wid.rvtrack.helper.DisplayUtils;
import com.wid.rvtrack.paint.PaintUtils;

public class RVTrackView extends View {

    private static final String _namespace = "http://schemas.android.com/apk/res-auto";
    private static final String _recyclerViewAttributeName = "recyclerView";

    private static float _defaultIndicatorRadius;
    private static final int _defaultIndicatorCount = 0;
    private static int _defaultIndicatorGapSize;

    private int _recyclerViewId;
    private int indicatorCount;
    private int activeIndex;
    private float activeIndicatorOffset;
    private Paint inactiveIndicatorPaint;
    private Paint activeIndicatorPaint;
    private float radius;
    private float indicatorGapSize;

    private static final String TAG = "RvTrackView";

    public RVTrackView(Context context) {
        super(context);
        init(context, null);
    }

    public RVTrackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RVTrackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public RVTrackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        inactiveIndicatorPaint = PaintUtils.createInactiveIndicatorPaint();
        activeIndicatorPaint = PaintUtils.createActiveIndicatorPaint();

        _defaultIndicatorRadius = DisplayUtils.dpToPx(6);
        _defaultIndicatorGapSize = DisplayUtils.dpToPx(2);

        _recyclerViewId = findRecyclerViewIdFromAttributeSet(attrs);

        defaults();

    }

    private int findRecyclerViewIdFromAttributeSet(final AttributeSet attrs) {
        if (attrs == null) return 0;
        return  attrs.getAttributeResourceValue(_namespace, _recyclerViewAttributeName, 0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        handleRecyclerViewAttr(_recyclerViewId);

    }

    private void handleRecyclerViewAttr(final int recyclerViewId) {
        if(recyclerViewId == 0) return;
        final Context context = getContext();
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            final RecyclerView recyclerView = activity.findViewById(recyclerViewId);
            if (recyclerView != null) {
                attachToRecyclerView(recyclerView);
            }
        } else {
            Log.d(TAG, "Context is not an Activity. Cannot find views.");
        }
    }

    private void defaults() {
        radius = _defaultIndicatorRadius;
        indicatorCount = _defaultIndicatorCount;
        indicatorGapSize = _defaultIndicatorGapSize;
    }

    public void attachToRecyclerView(final RecyclerView recyclerView) {

        if(recyclerView == null) throw new RuntimeException("RecyclerView cannot be null");
        recyclerView.addOnLayoutChangeListener(createRecyclerViewLayoutChangeListener(recyclerView));

    }

    private View.OnLayoutChangeListener createRecyclerViewLayoutChangeListener(final RecyclerView recyclerView) {
        return new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                final boolean hasLayoutManager = recyclerView.getLayoutManager() != null;
                final boolean hasAdapter = recyclerView.getAdapter() != null;

                if (hasAdapter) {
                    handleAdapter(recyclerView);
                }

                if (hasLayoutManager) {
                    handleLayoutManager(recyclerView);
                }

                if (hasLayoutManager && hasAdapter) {
                    cleanUpListener(recyclerView, this);
                }
            }
        };
    }

    private void handleLayoutManager(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            activeIndex = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (recyclerView.getTag(R.id.recycler_scroll_listener_attached) == null) {
                recyclerView.addOnScrollListener(createScrollListener());
                recyclerView.setTag(R.id.recycler_scroll_listener_attached, true); // Mark listener as added
            }
        }
    }

    private void handleAdapter(RecyclerView recyclerView) {
        if (recyclerView.getTag(R.id.recycler_data_observer_attached) == null) {
            indicatorCount = recyclerView.getAdapter().getItemCount(); // initial item count
            recyclerView.getAdapter().registerAdapterDataObserver(createAdapterDataObserver(recyclerView));
            recyclerView.setTag(R.id.recycler_data_observer_attached, true); // Mark observer as added
        }
    }

    private void cleanUpListener(RecyclerView recyclerView, View.OnLayoutChangeListener listener) {
        recyclerView.removeOnLayoutChangeListener(listener);
    }

    private RecyclerView.OnScrollListener createScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                handleScroll(recyclerView);
            }
        };
    }

    private RecyclerView.AdapterDataObserver createAdapterDataObserver(final RecyclerView recyclerView) {
        return new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                Log.d(TAG, itemCount + " items added from position " + positionStart);
                indicatorCount += itemCount;
                invalidate();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                Log.d(TAG, itemCount + " items removed from position " + positionStart);
                indicatorCount = indicatorCount == 0 ? 0 : indicatorCount - itemCount;
                new Handler().postDelayed(()->{
                    handleScroll(recyclerView);
                }, 6); //small delay to give recycler time to adjust it internal calculations
            }

        };
    }

    private void handleScroll(final RecyclerView recyclerView) {
        calculateActiveIndicatorOffset(recyclerView);
        invalidate();
    }

    private void calculateActiveIndicatorOffset(final RecyclerView recyclerView) {
        final float scrollDelta = recyclerView.computeHorizontalScrollOffset();
        final float totalScrollableWidth = calculateTotalScrollableWidth(recyclerView);
        final float indicatorScrollableWidth = calculateIndicatorScrollableWidth();

        // Normalize scroll delta to indicator range
        activeIndicatorOffset = (scrollDelta / totalScrollableWidth) * indicatorScrollableWidth;
    }

    private float calculateTotalScrollableWidth(final RecyclerView recyclerView) {
        return (indicatorCount - 1) * recyclerView.getWidth(); // Total scrollable area
    }

    private float calculateIndicatorScrollableWidth() {
        return (radius * 2 + indicatorGapSize) * (indicatorCount - 1); // Indicator movement area
    }

    private float getWrapWidth() {
        return radius + (indicatorCount - 1) * (radius * 2 + indicatorGapSize) + radius;
    }

    private float getWrapHeight() {
        return radius * 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Calculate the desired width and height
        final int desiredWidth = (int) getWrapWidth();
        final int desiredHeight = (int) getWrapHeight();

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        // Measure width
        if (widthMode == MeasureSpec.EXACTLY) {
            // Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            // Be whatever you want
            width = desiredWidth;
        }

        // Measure height
        if (heightMode == MeasureSpec.EXACTLY) {
            // Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            // Be whatever you want
            height = desiredHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        drawCircleIndicators(canvas);
    }

    private void drawCircleIndicators(final Canvas canvas) {
        drawInactiveCircleIndicator(canvas);
        drawActiveCircleIndicator(canvas);
    }

    private void drawInactiveCircleIndicator(final Canvas canvas) {
        for(int i = 0; i < indicatorCount; i++) {
            final float cx = radius + i * (radius * 2f + indicatorGapSize); // radius * 2 is the diameter, plus the gap
            canvas.drawCircle(cx, getHeight() / 2f, radius, inactiveIndicatorPaint);
        }
    }

    private void drawActiveCircleIndicator(final Canvas canvas) {
        // Calculate the position of the active indicator based on the scroll distance (smallerWidth)
        final float activeCx = radius + activeIndicatorOffset;  // smallerWidth is the horizontal scroll offset scaled to the indicator's movement
        canvas.drawCircle(activeCx, getHeight() / 2f, radius, activeIndicatorPaint);
    }


}
