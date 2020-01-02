package com.phantomvk.qrcode.core.task;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.phantomvk.qrcode.core.callback.CodeReader;

import java.lang.ref.WeakReference;

public class CoreAsyncTask extends AsyncTask<Void, Void, String> {

    private WeakReference<CodeReader> mWeakRef;
    private Camera.Size mSize;
    private byte[] mBytes;
    private String mPath;
    private Bitmap mBitmap;

    public CoreAsyncTask(@NonNull CodeReader reader, @NonNull Camera.Size size,
                         @NonNull byte[] bytes) {
        mWeakRef = new WeakReference<>(reader);
        mSize = size;
        mBytes = bytes;
    }

    public CoreAsyncTask(@NonNull CodeReader reader, @NonNull String filePath) {
        mWeakRef = new WeakReference<>(reader);
        mPath = filePath;
    }

    public CoreAsyncTask(@NonNull CodeReader reader, @NonNull Bitmap bitmap) {
        mWeakRef = new WeakReference<>(reader);
        mBitmap = bitmap;
    }

    @Override
    protected String doInBackground(Void... voids) {
        CodeReader reader = mWeakRef.get();
        if (reader == null || isCancelled()) {
            return null;
        } else if (mPath != null) {
            return reader.decodePath(mPath);
        } else if (mBitmap != null) {
            return reader.decodeBitmap(mBitmap);
        } else if (mBytes != null && mSize != null) {
            return reader.decodeBytes(mBytes, mSize.width, mSize.height);
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        CodeReader view = mWeakRef.get();
        if (view != null && s != null) view.onPostCodeDecode(s);
    }

    @Override
    protected void onCancelled() {
    }
}
