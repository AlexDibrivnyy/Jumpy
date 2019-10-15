package com.loader.error;

public interface ErrorHandler {

    interface ErrorHandlerListener {
        void errorHandled();
    }

    void closeTimer();

    void handleError(Exception e, ErrorHandlerListener errorHandleListener);

    void resetTimeout();

}
