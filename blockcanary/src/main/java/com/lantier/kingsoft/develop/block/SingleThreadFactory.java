package com.lantier.kingsoft.develop.block;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

/**
 * Created by WUXIAOLONG on 2017/9/4.
 */

class SingleThreadFactory implements ThreadFactory {
    private final String threadName;

    SingleThreadFactory(String threadName) {
        this.threadName = "BlockCanary-" + threadName;
    }

    @Override
    public Thread newThread(@NonNull Runnable runnable) {
        return new Thread(runnable,this.threadName);
    }
}
