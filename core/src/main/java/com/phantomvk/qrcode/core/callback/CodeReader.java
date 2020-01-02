package com.phantomvk.qrcode.core.callback;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

public interface CodeReader {

    @WorkerThread
    @Nullable
    String decodePath(String filePath);

    @WorkerThread
    @Nullable
    String decodeBitmap(@NonNull Bitmap bitmap);

    @WorkerThread
    @Nullable
    String decodeBytes(@NonNull byte[] bytes, int width, int height);

    @UiThread
    void onPostCodeDecode(@Nullable String result);
}
