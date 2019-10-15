/*
 * Copyright 2017 Chk-In Cam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loader.core;

/**
 * QueueLoaderBuilder
 * Created by a_dibrivnyi on 09.11.17.
 */

public class QueueLoaderBuilder {

    private boolean needToRetry = true;
    private boolean executeCallbacksOnUI = true;
    private boolean debug = true;


    public QueueLoader build() {
        return new QueueLoader(debug, needToRetry, executeCallbacksOnUI);
    }

    public QueueLoaderBuilder setExecuteCallbacksOnUI(boolean executeCallbacksOnUI) {
        this.executeCallbacksOnUI = executeCallbacksOnUI;
        return this;
    }

    public QueueLoaderBuilder setNeedToRetry(boolean needToRetry) {
        this.needToRetry = needToRetry;
        return this;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
