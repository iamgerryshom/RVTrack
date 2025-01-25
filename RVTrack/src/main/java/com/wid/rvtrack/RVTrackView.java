package com.wid.rvtrack;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wid.rvtrack.helper.DisplayUtils;
import com.wid.rvtrack.paint.PaintUtils;

public class RVTrackView extends View {

    private static final String _namespace = "http://schemas.android.com/apk/res-auto";
    private static final String _recyclerViewAttributeName = "recycler_view";
    private static final String _inactiveIndicatorColorAttributeName = "inactive_indicator_color";
    private static final String _activeIndicatorColorAttributeName = "active_indicator_color";

    private static float _defaultIndicatorRadius;
    private static final int _defaultIndicatorCount = 0;
    private static final int _defaultToolsIndicatorCount = 3;
    private static int _defaultIndicatorGapSize;

    private int _recyclerViewId;
    private int indicatorCount;
    private int visibleIndicatorCount;
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
        inactiveIndicatorPaint = PaintUtils.createInactiveIndicatorPaint(Color.GRAY);
        activeIndicatorPaint = PaintUtils.createActiveIndicatorPaint(Color.WHITE);

        _defaultIndicatorRadius = DisplayUtils.dpToPx(6);
        _defaultIndicatorGapSize = DisplayUtils.dpToPx(2);

        indicatorCount = _defaultToolsIndicatorCount;
        visibleIndicatorCount = _defaultToolsIndicatorCount;

        defaults();

        handleAttributeSet(attrs);

    }

    private void handleAttributeSet(final AttributeSet attrs) {

        _recyclerViewId = findRecyclerViewIdFromAttributeSet(attrs);

        final String inactiveIndicatorColorString = attrs.getAttributeValue(_namespace, _inactiveIndicatorColorAttributeName);
        final String activeIndicatorColorString = attrs.getAttributeValue(_namespace, _activeIndicatorColorAttributeName);

        float circleIndicatorRadius = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RecyclerViewWrapper, // Your custom styleable name
                0, // Default style
                0  // Default value
        ).getDimension(R.styleable.RecyclerViewWrapper_circle_indicator_radius, _defaultIndicatorRadius);

        Log.d(TAG, "inactiveIndicatorColor: " + inactiveIndicatorColorString);
        Log.d(TAG, "activeIndicatorColor: " + activeIndicatorColorString);
        Log.d(TAG, "circleIndicatorRadius: " + circleIndicatorRadius);

        if (inactiveIndicatorColorString != null || !inactiveIndicatorColorString.isEmpty()) {
            if (inactiveIndicatorColorString.startsWith("#")) {
                inactiveIndicatorPaint.setColor(Color.parseColor(inactiveIndicatorColorString));
            } else {
                final int inactiveIndicatorColorResId = attrs.getAttributeResourceValue(_namespace, _inactiveIndicatorColorAttributeName, 0);
                if (inactiveIndicatorColorResId != 0) {
                    inactiveIndicatorPaint.setColor(ContextCompat.getColor(getContext(), inactiveIndicatorColorResId));
                }
            }
        }

        if (activeIndicatorColorString != null || !activeIndicatorColorString.isEmpty()) {
            if (activeIndicatorColorString.startsWith("#")) {
                activeIndicatorPaint.setColor(Color.parseColor(activeIndicatorColorString));
            } else {
                final int inactiveIndicatorColorResId = attrs.getAttributeResourceValue(_namespace, _activeIndicatorColorAttributeName, 0);
                if (inactiveIndicatorColorResId != 0) {
                    activeIndicatorPaint.setColor(ContextCompat.getColor(getContext(), inactiveIndicatorColorResId));
                }
            }
        }

        this.radius = circleIndicatorRadius;

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
        final Activity activity = resolveActivityFromContext(getContext());
        if(activity == null) return;
        attachToRecyclerView(activity.findViewById(recyclerViewId));
    }

    private Activity resolveActivityFromContext(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context; // Found an Activity
            }
            context = ((ContextWrapper) context).getBaseContext(); // Unwrap ContextWrapper
        }
        return null;
    }


    private void defaults() {
        radius = _defaultIndicatorRadius;
        indicatorCount = _defaultIndicatorCount;
        visibleIndicatorCount = _defaultIndicatorCount;
        indicatorGapSize = _defaultIndicatorGapSize;
    }

    public void attachToRecyclerView(final RecyclerView recyclerView) {

        if(recyclerView != null) {
            Log.d(TAG, "RecyclerView was found");
        }

        if(recyclerView == null) throw new RuntimeException("RecyclerView cannot be null");

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(createRecyclerViewGlobalLayoutListener(recyclerView));
    }

    private ViewTreeObserver.OnGlobalLayoutListener createRecyclerViewGlobalLayoutListener(final RecyclerView recyclerView) {
        return new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final boolean hasLayoutManager = recyclerView.getLayoutManager() != null;
                final boolean hasAdapter = recyclerView.getAdapter() != null;

                if (hasAdapter && !hasLayoutManager) Log.w(TAG, "Warning: The RecyclerView indicator detected that the RecyclerView has an adapter set but no LayoutManager. A LayoutManager is required for proper indicator functionality.");
                if (hasLayoutManager && !hasAdapter) Log.w(TAG, "Warning: The RecyclerView indicator detected that the RecyclerView has a LayoutManager set but no adapter. An adapter is required for the indicator to function properly.");

                if (hasLayoutManager && hasAdapter) {
                    handleAdapter(recyclerView);
                    handleLayoutManager(recyclerView);
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

            }
        };
    }


    private void handleLayoutManager(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            if (recyclerView.getTag(R.id.recycler_scroll_listener_attached) == null) {
                recyclerView.addOnScrollListener(createScrollListener());
                recyclerView.setTag(R.id.recycler_scroll_listener_attached, true); // Mark listener as added
            }
        }
    }

    private void handleAdapter(RecyclerView recyclerView) {
        if (recyclerView.getTag(R.id.recycler_data_observer_attached) == null) {
            setIndicatorCount(recyclerView.getAdapter().getItemCount());
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
                setIndicatorCount(indicatorCount + itemCount);
                invalidate();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                Log.d(TAG, itemCount + " items removed from position " + positionStart);
                setIndicatorCount(indicatorCount == 0 ? 0 : indicatorCount - itemCount);
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

    private float previousActiveIndicatorOffset;
    private void calculateActiveIndicatorOffset(final RecyclerView recyclerView) {
        // Get the current horizontal scroll offset of the RecyclerView
        final float horizontalScrollOffset = recyclerView.computeHorizontalScrollOffset();

        // Calculate the total scrollable width of the RecyclerView (the area over which the user can scroll)
        final float totalScrollRange = recyclerView.computeHorizontalScrollRange() - recyclerView.getWidth();

        // Calculate the total range for indicator scrolling (total scrollable area adjusted for visible items)
        final float totalIndicatorScrollRange = totalScrollRange - ((indicatorCount - visibleIndicatorCount) * recyclerView.getWidth());

        // Calculate the total width occupied by the indicators (width of one indicator + gap between indicators)
        final float totalIndicatorOffsetWidth = (radius * 2f + indicatorGapSize) * (visibleIndicatorCount - 1);

        // Normalize the scroll offset to the corresponding position of the active indicator
        activeIndicatorOffset = (horizontalScrollOffset / totalIndicatorScrollRange) * totalIndicatorOffsetWidth;

        // Determine scrolling direction
        final boolean isScrollingForward = (activeIndicatorOffset > previousActiveIndicatorOffset);
        // Update the previous offset
        previousActiveIndicatorOffset = activeIndicatorOffset;


        if (isScrollingForward) {
            handleForwardOffset(totalIndicatorOffsetWidth, totalIndicatorScrollRange);
        } else {
            handleForwardOffset(totalIndicatorOffsetWidth, totalIndicatorScrollRange);
        }

       handleEdgeCase(horizontalScrollOffset, totalScrollRange, totalIndicatorOffsetWidth);
    }

    private void handleForwardOffset(final float totalIndicatorOffsetWidth, final float totalIndicatorScrollRange) {
        // Handle forward scrolling
        while (activeIndicatorOffset >= totalIndicatorOffsetWidth) {
            // Move the indicator back by one indicator width
            activeIndicatorOffset -= ((radius * 2) + indicatorGapSize);

            // Ensure the offset stays within the bounds of the total indicator scroll range
            activeIndicatorOffset = Math.max(0, Math.min(activeIndicatorOffset, totalIndicatorScrollRange));
        }
    }

    private void handleReverseOffset(final float totalIndicatorOffsetWidth, final float totalIndicatorScrollRange) {
        while (activeIndicatorOffset >= totalIndicatorOffsetWidth - (radius * 2 + indicatorGapSize)) {
            // Move the indicator back by one indicator width
            activeIndicatorOffset += ((radius * 2) + indicatorGapSize);

            // Ensure the offset stays within the bounds of the total indicator scroll range
            activeIndicatorOffset = Math.max(0, Math.min(activeIndicatorOffset, totalIndicatorScrollRange));
        }
    }

    private void handleEdgeCase(final float horizontalScrollOffset, final float totalScrollRange, final float totalIndicatorOffsetWidth) {
        // Handle the edge case when scrolling reaches the end
        if (horizontalScrollOffset >= totalScrollRange) {
            activeIndicatorOffset = totalIndicatorOffsetWidth;
        } else if (horizontalScrollOffset <= 0) {
            activeIndicatorOffset = 0;
        }
    }

    private float getWrapWidth() {
        return radius + (indicatorCount - 1) * (radius * 2f + indicatorGapSize) + radius;
    }

    private float getWrapHeight() {
        return radius * 2;
    }

    private void setIndicatorCount(final int count) {
        indicatorCount = count;
        visibleIndicatorCount = calculateVisibleIndicatorCount();
        requestLayout();
    }

    private int calculateVisibleIndicatorCount() {
        // Get the total width taken by one indicator (circle diameter + gap)
        final float indicatorWidth = radius * 2f + indicatorGapSize;

        // If wrapWidth is greater than available width, calculate how many fit in the available width
        if (getWrapWidth() > getWidth()) {
            // Calculate how many indicators fit in the available width
            return (int) (getWidth() / indicatorWidth);
        }

        // If wrapWidth fits within the available width, return all indicators
        return indicatorCount;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Calculate the desired width and height
        final int desiredWidth = (int) getWrapWidth();
        final int desiredHeight = (int) getWrapHeight();

        Log.d(TAG, "OnMeasure width: " + desiredWidth);
        Log.d(TAG, "OnMeasure height: " + desiredWidth);


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

    public void setCircleIndicatorRadius(final float circleRadius) {
        this.radius = circleRadius;
        invalidate();
    }

    public void setActiveIndicatorColor(final int activeIndicatorColor) {
        activeIndicatorPaint.setColor(activeIndicatorColor);
        invalidate();
    }

    public void setInactiveIndicatorColor(final int inactiveIndicatorColor) {
        inactiveIndicatorPaint.setColor(inactiveIndicatorColor);
        invalidate();
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
        // Calculate total width of all indicators and gaps
        final float totalWidth = visibleIndicatorCount * (radius * 2f) + (visibleIndicatorCount - 1) * indicatorGapSize;

        // Calculate the starting x-coordinate to center the indicators
        final float startX = (getWidth() - totalWidth) / 2f;

        for (int i = 0; i < visibleIndicatorCount; i++) {
            // Calculate the x-coordinate of each indicator
            final float cx = startX + radius + i * (radius * 2f + indicatorGapSize);
            canvas.drawCircle(cx, getHeight() / 2f, radius, inactiveIndicatorPaint);
        }
    }

    private void drawActiveCircleIndicator(final Canvas canvas) {
        // Calculate total width of all indicators and gaps
        final float totalWidth = visibleIndicatorCount * (radius * 2f) + (visibleIndicatorCount - 1) * indicatorGapSize;

        // Calculate the starting x-coordinate to center the indicators
        final float startX = (getWidth() - totalWidth) / 2f;

        // Calculate the position of the active indicator based on the scroll distance
        final float activeCx = startX + radius + activeIndicatorOffset;
        canvas.drawCircle(activeCx, getHeight() / 2f, radius, activeIndicatorPaint);
    }


}
