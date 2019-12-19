package com.phantomvk.qrcode.core.widget;

import android.content.Context;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class QRCodeView extends RelativeLayout implements Camera.PreviewCallback {

    protected Camera mCamera;

    private final Paint mPaint = new Paint();

    public QRCodeView(Context context) {
        super(context);
    }

    public QRCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QRCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }
}
