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

package com.loader.interfaces;

/**
 * IRequest
 * Created by a_dibrivnyi on 09.11.17.
 */

public abstract class IRequest {

    private IRequestLoadListener iRequestLoadListener;

    private int counter = 1;

    public interface IRequestLoadListener {
        void started(int attempt);

        void finished();

        void requestFailed(final Exception e, int attempt);
    }


    public void setListener(IRequestLoadListener iRequestLoadListener) {
        this.iRequestLoadListener = iRequestLoadListener;
    }

    public IRequestLoadListener getiRequestLoadListener() {
        return iRequestLoadListener;
    }

    public int getCounter() {
        return counter;
    }

    public void increaseCounter() {
        ++counter;
    }

    public String getIdentifier() {
        return toString();
    }

    public abstract void destroy();
}
