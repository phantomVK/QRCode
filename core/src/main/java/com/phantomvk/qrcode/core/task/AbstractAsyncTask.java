package com.phantomvk.qrcode.core.task;

import android.os.AsyncTask;

public abstract class AbstractAsyncTask extends AsyncTask<Void, Void, String> {

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
    }

    @Override
    protected void onCancelled() {
    }
}
