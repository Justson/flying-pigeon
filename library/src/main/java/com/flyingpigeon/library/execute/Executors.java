/*
 * Copyright (C)  Justson(https://github.com/Justson/flying-pigeon)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flyingpigeon.library.execute;

import com.queue.library.Dispatch;
import com.queue.library.DispatchThread;
import com.queue.library.GlobalQueue;

/**
 * @author xiaozhongcen
 * @date 20-7-20
 * @since 1.0.0
 */
public class Executors {

    private volatile static Dispatch SINGLE_THREAD;

    public static Dispatch getSingleThreadExecutor() {
        if (null == SINGLE_THREAD) {
            synchronized (Executors.class) {
                if (null == SINGLE_THREAD) {
                    SINGLE_THREAD = DispatchThread.create("single");
                }
            }
        }
        return SINGLE_THREAD;
    }

    public static Dispatch getMainThreadExecutor() {
        return GlobalQueue.getMainQueue();
    }


}
