package com.phantomvk.qrcode.core.callback;

import androidx.annotation.Nullable;

public interface OnCodeListener {

    void onResult(@Nullable String result);

    void onCameraBrightnessChange(boolean lowBrightness);

    void onOpenCameraError();
}
