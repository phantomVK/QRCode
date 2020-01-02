package com.phantomvk.qrcode.zbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.phantomvk.qrcode.core.callback.CodeReader;
import com.phantomvk.qrcode.core.widget.QRCodeView;

public class ZBarView extends QRCodeView implements CodeReader {

    public ZBarView(Context context) {
        super(context);
    }

    public ZBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
    }

    @Nullable
    @Override
    public String decodePath(String filePath) {
        return null;
    }

    @Nullable
    @Override
    public String decodeBitmap(@NonNull Bitmap bitmap) {
        return null;
    }

    @Nullable
    @Override
    public String decodeBytes(@NonNull byte[] bytes, int width, int height) {
        return null;
    }

    @Override
    public void onPostCodeDecode(@Nullable String result) {

    }
}
