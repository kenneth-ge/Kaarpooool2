package com.example.carpool.util;

import android.os.AsyncTask;

public class EasyAsync extends AsyncTask<Void, Integer, Void> {

    private Runnable runnable;

    public EasyAsync(Runnable runnable){
        this.runnable = runnable;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        runnable.run();
        return null;
    }
}
