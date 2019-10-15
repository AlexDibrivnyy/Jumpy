package com.loader.core;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.loader.error.DefaultErrorHandler;
import com.loader.error.ErrorHandler;
import com.loader.interfaces.ICameraLoaderListener;
import com.loader.interfaces.IRequest;
import com.loader.interfaces.IRequestLoadedListener;
import com.loader.request.BackgroundRequest;
import com.loader.request.CustomRequest;
import com.loader.request.UIRequest;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * QueueLoader
 * Created by a_dibrivnyi on 09.11.17.
 */

public class QueueLoader {

    public static final String TAG = "QueueLoader";

    private LinkedBlockingQueue<IRequest> requests = new LinkedBlockingQueue<>();
    private ErrorHandler errorHandler = new DefaultErrorHandler();
    private static Handler uiHandler = new Handler(Looper.getMainLooper());

    private ICameraLoaderListener queueLoaderListener;
    private IRequest currentRequest;

    private volatile boolean started = false;

    private boolean debug = true;
    private boolean needToRetry = true;
    private boolean executeCallbacksOnUI = false;

    QueueLoader(boolean debug, boolean needToRetry, boolean executeCallbacksOnUI) {
        this.debug = debug;
        this.needToRetry = needToRetry;
        this.executeCallbacksOnUI = executeCallbacksOnUI;
    }

    public void add(IRequest iRequest) {
        requests.add(iRequest);
    }

    public void add(IRequest iRequest, CustomRequest.IRequestLoadListener iRequestLoadListener) {
        iRequest.setListener(iRequestLoadListener);
        requests.add(iRequest);
    }

    public void setQueueLoaderListener(ICameraLoaderListener listener) {
        this.queueLoaderListener = listener;
    }

    public synchronized void load() {
        if (!started) {
            started = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    executeRequests();
                }
            }).start();
        }
    }

    private void executeRequests() {
        if (!requests.isEmpty()) {
            try {
                currentRequest = requests.peek();
                printLog("request started " + currentRequest.getIdentifier());
                final CustomRequest.IRequestLoadListener requestLoadListener = currentRequest.getiRequestLoadListener();

                triggerStartCallback(requestLoadListener);

                if (currentRequest instanceof UIRequest) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ((UIRequest) currentRequest).load();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handleSuccessfulResponse(currentRequest);
                                    }
                                }).start();
                            } catch (Exception e) {
                                handleRequestFail(e, currentRequest);
                            }
                        }
                    });

                } else if (currentRequest instanceof BackgroundRequest) {
                    try {
                        ((BackgroundRequest) currentRequest).load();

                        handleSuccessfulResponse(currentRequest);

                    } catch (Exception e) {
                        handleRequestFail(e, currentRequest);
                    }

                } else if (currentRequest instanceof CustomRequest) {
                    ((CustomRequest) currentRequest).load(new IRequestLoadedListener<Boolean>() {
                        @Override
                        public void onFinish() {
                            handleSuccessfulResponse(currentRequest);
                        }

                        @Override
                        public void onError(final Exception e) {
                            handleRequestFail(e, currentRequest);
                        }
                    });
                }
            } catch (final Exception e) {
                e.printStackTrace();

                if (executeCallbacksOnUI) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (queueLoaderListener != null) {
                                queueLoaderListener.errorHappened(e);
                            }
                        }
                    });
                } else {
                    if (queueLoaderListener != null) {
                        queueLoaderListener.errorHappened(e);
                    }
                }
            }
        } else {
            printLog("executeRequests list is empty");
            if (executeCallbacksOnUI) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (queueLoaderListener != null) {
                            queueLoaderListener.allRequestLoaded();
                        }
                    }
                });
            } else {
                if (queueLoaderListener != null) {
                    queueLoaderListener.allRequestLoaded();
                }
            }
            started = false;
        }
    }

    private void triggerStartCallback(final CustomRequest.IRequestLoadListener requestLoadListener) {
        if (executeCallbacksOnUI) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestLoadListener != null) {
                        requestLoadListener.started(currentRequest.getCounter());
                    }
                }
            });
        } else {
            if (requestLoadListener != null) {
                requestLoadListener.started(currentRequest.getCounter());
            }
        }
    }

    private void handleSuccessfulResponse(final IRequest currentRequest) {
        final CustomRequest.IRequestLoadListener requestLoadListener = currentRequest.getiRequestLoadListener();

        if (executeCallbacksOnUI) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestLoadListener != null) {
                        requestLoadListener.finished();
                    }
                }
            });
        } else {
            if (requestLoadListener != null) {
                requestLoadListener.finished();
            }
        }

        printLog("request loaded " + currentRequest.getIdentifier());

        errorHandler.resetTimeout();

        try {
            if (!requests.isEmpty()) {
                requests.take();
            }
            executeRequests();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleRequestFail(final Exception e, final IRequest currentRequest) {
        try {
            Log.e(TAG, "onError, request failed " + currentRequest.getIdentifier() + " " + e.getMessage() + " needToRetry: " + needToRetry);
            final CustomRequest.IRequestLoadListener requestLoadListener = currentRequest.getiRequestLoadListener();
            if (needToRetry) {

                triggerFailRequestCallback(e, currentRequest, requestLoadListener);

                errorHandler.handleError(e, new ErrorHandler.ErrorHandlerListener() {
                    @Override
                    public void errorHandled() {
                        executeNextRequestAfterFail(currentRequest);
                    }
                });

            } else {
                requests.take();

                triggerFailRequestCallback(e, currentRequest, requestLoadListener);

                executeNextRequestAfterFail(currentRequest);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void triggerFailRequestCallback(final Exception e, final IRequest currentRequest,
                                            final CustomRequest.IRequestLoadListener requestLoadListener) {
        if (executeCallbacksOnUI) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestLoadListener != null) {
                        requestLoadListener.requestFailed(e, currentRequest.getCounter());
                    }
                }
            });
        } else {
            if (requestLoadListener != null) {
                requestLoadListener.requestFailed(e, currentRequest.getCounter());
            }
        }
    }

    private void executeNextRequestAfterFail(final IRequest currentRequest) {
        currentRequest.increaseCounter();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep(100);
                executeRequests();
            }
        }).start();
    }

    private void printLog(String message) {
        if (debug) {
            Log.d(TAG, message);
        }
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public int getQueueSize() {
        return requests.size();
    }

    public void clear() {
        requests.clear();
        errorHandler.closeTimer();

        if (currentRequest != null) {
            try {
                currentRequest.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        started = false;
    }
}
