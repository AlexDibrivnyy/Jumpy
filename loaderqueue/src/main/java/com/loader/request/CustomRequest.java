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

package com.loader.request;

import com.loader.interfaces.IRequest;
import com.loader.interfaces.IRequestLoadedListener;

public abstract class CustomRequest extends IRequest {

    public abstract void load(final IRequestLoadedListener<Boolean> listener);

    @Override
    public void destroy() {
    }
}
