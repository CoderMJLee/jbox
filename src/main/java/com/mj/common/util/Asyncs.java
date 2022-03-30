package com.mj.common.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步操作
 *
 * @author MJ
 */
public class Asyncs {
    private static final ThreadPoolExecutor EXECUTOR;
    static {
        EXECUTOR = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        // 核心线程空闲也会被干掉
        EXECUTOR.allowCoreThreadTimeOut(true);
    }

    public static void run(Runnable runnable) {
        EXECUTOR.execute(runnable);
    }

    public static void run(Runnable runnable, long delay) {
        Times.delay(() -> EXECUTOR.execute(runnable), delay);
    }
}
