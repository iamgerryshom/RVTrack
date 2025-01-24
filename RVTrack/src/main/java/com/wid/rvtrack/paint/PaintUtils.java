package com.wid.rvtrack.paint;

import android.graphics.Color;
import android.graphics.Paint;

public class PaintUtils {
    public static Paint createInactiveIndicatorPaint() {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        return paint;
    }
    public static Paint createActiveIndicatorPaint() {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        return paint;
    }
}
