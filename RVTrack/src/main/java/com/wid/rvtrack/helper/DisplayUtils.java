package com.wid.rvtrack.helper;

import android.content.res.Resources;

public class DisplayUtils {
    public static int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    public static int pxToDp(int px) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (px / density);
    }

}
