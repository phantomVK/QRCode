package com.phantomvk.qrcode.zxing;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.phantomvk.qrcode.core.callback.CodeReader;
import com.phantomvk.qrcode.core.widget.QRCodeView;

public class ZXingView extends QRCodeView implements CodeReader {

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

    @Nullable
    @Override
    public String decodePath(String filePath) {
        return null;
    }

    @Nullable
    @Override
    public String decodeBitmap(@NonNull Bitmap bitmap) {
        return Decoder.decode(bitmap);
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
