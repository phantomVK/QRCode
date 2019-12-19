package com.phantomvk.qrcode.core.util;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;
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

    public static void getDisplaySize(Context context, Point point) {
        WindowManager m = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        if (m != null) {
            m.getDefaultDisplay().getSize(point);
        } else {
            point.x = point.y = -1;
        }
    }
}
