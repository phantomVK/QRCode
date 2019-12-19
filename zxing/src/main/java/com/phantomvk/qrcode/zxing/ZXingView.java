package com.phantomvk.qrcode.zxing;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

import com.phantomvk.qrcode.core.widget.QRCodeView;

public class ZXingView extends QRCodeView {

    public ZXingView(Context context) {
        super(context);
    }

    public ZXingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZXingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
    }
}
