package com.flyingpigeon.library.execute;

import com.queue.library.Dispatch;
import com.queue.library.DispatchThread;
import com.queue.library.GlobalQueue;

import java.util.concurrent.Executor;

/**
 * @author xiaozhongcen
 * @date 20-7-20
 * @since 1.0.0
 */
public class Executors {

    private volatile static Dispatch SINGLE_THREAD;

    public static Executor getSingleThreadExecutor() {
        if (null == SINGLE_THREAD) {
            synchronized (Executors.class) {
                if (null == SINGLE_THREAD) {
                    SINGLE_THREAD = DispatchThread.create("single");
                }
            }
        }
        return SINGLE_THREAD;
    }

    public static Executor getMainThreadExecutor() {
        return GlobalQueue.getMainQueue();
    }


}
