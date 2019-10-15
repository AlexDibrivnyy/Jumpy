package com.alexdib.jumpy

import android.os.Handler
import android.os.Looper

class AsyncJob {

    companion object {

        private val uiHandler = Handler(Looper.getMainLooper())

        /**
         * Executes the provided code immediately on the UI Thread
         *
         * @param onMainThreadJob Interface that wraps the code to execute
         */
        fun doOnMainThread(block: () -> Unit) {
            uiHandler.post { block() }
        }

        /**
         * Executes the provided code with delay on the UI Thread
         *
         * @param onMainThreadJob Interface that wraps the code to execute
         */
        fun doOnMainThread(block: () -> Unit, delay: Int) {
            uiHandler.postDelayed({ block() }, delay.toLong())
        }

        /**
         * Executes the provided code immediately on a background thread
         *
         * @param onBackgroundJob Interface that wraps the code to execute
         */
        fun doInBackground(block: () -> Unit) {
            Thread(Runnable { block() }).start()
        }
    }


}