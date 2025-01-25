package com.wid.rvtrack.paint;

import android.graphics.Color;
import android.graphics.Paint;

public class PaintUtils {
    public static Paint createInactiveIndicatorPaint(final int color) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        return paint;
    }
    public static Paint createActiveIndicatorPaint(final int color) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        return paint;
    }
}
