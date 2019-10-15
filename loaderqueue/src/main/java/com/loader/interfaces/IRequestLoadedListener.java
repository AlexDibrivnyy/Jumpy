package com.loader.interfaces;


public abstract class IRequestLoadedListener<T> {

    public abstract void onFinish();

    public void onError(Exception e) {
        e.printStackTrace();
    }
}
