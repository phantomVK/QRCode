package com.phantomvk.qrcode.core.widget;

import android.content.Context;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class QRCodeView extends RelativeLayout implements Camera.PreviewCallback {

    protected Camera mCamera;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

    {
        mPaint.setStyle(Paint.Style.FILL);
    }

    public QRCodeView(Context context) {
        super(context);
    }

    public QRCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QRCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onPostResult(String result) {
    }
}
