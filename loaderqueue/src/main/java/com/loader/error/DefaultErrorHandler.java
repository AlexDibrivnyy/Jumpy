package com.loader.error;

import android.text.format.DateUtils;
import android.util.Log;

import com.loader.core.QueueLoader;

import java.util.Timer;
import java.util.TimerTask;

public class DefaultErrorHandler implements ErrorHandler {

    public long delay = DateUtils.SECOND_IN_MILLIS * 5;

    private Timer timer;

    @Override
    public void handleError(final Exception e, final ErrorHandlerListener errorHandleListener) {
        Log.d(QueueLoader.TAG, "loadAll reload request " + e.getMessage() + " delay: " + delay);

        closeTimer();
        increaseDelay();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                errorHandleListener.errorHandled();
            }
        }, delay);
    }

    @Override
    public void resetTimeout() {
        Log.d(QueueLoader.TAG, "resetTimeout");

        delay = DateUtils.SECOND_IN_MILLIS * 5;
    }

    public void increaseDelay() {
        if (delay < DateUtils.MINUTE_IN_MILLIS * 15) {
            delay += DateUtils.SECOND_IN_MILLIS * 5;
        }
    }

    @Override
    public void closeTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
}