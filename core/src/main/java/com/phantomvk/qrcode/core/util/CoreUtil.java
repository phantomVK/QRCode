package com.phantomvk.qrcode.core.util;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

public class CoreUtil {

    public static float dp(Context context, int value) {
        return context.getResources().getDisplayMetrics().density * value;
    }

    public static float sp(Context context, int value) {
        return context.getResources().getDisplayMetrics().scaledDensity * value;
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
