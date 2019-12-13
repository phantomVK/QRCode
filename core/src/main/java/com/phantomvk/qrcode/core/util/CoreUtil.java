package com.phantomvk.qrcode.core.util;

import android.content.Context;
import android.util.DisplayMetrics;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static android.util.TypedValue.applyDimension;

public class CoreUtil {

    public static float dp(Context context, int value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return applyDimension(COMPLEX_UNIT_DIP, value, metrics);
    }

    public static float sp(Context context, int value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return applyDimension(COMPLEX_UNIT_SP, value, metrics);
    }
}
