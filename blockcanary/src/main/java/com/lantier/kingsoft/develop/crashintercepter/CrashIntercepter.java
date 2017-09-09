package com.lantier.kingsoft.develop.crashintercepter;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by wuxiaolong on 2017/9/9.
 */

public class CrashIntercepter {
    public CrashIntercepter() {
    }

    public interface ExceptionHandler{
        void handleException(Thread thread,Throwable throwable);
    }

    private static ExceptionHandler sExceptionHandler;
    private static Thread.UncaughtExceptionHandler sUncaughtExceptionHandler;
    private static boolean sInstalled = false; //标记位，避免重复安装卸载

    /**
     * 当主线程或子线程抛出异常时会调用exceptionHandler.handlerException(Thread thread , Throwable throwable)
     * exceptionHandler.handlerException可能运行在非ui线程中
     * 若设置了Thread.setDefaultUncaughtExceptionHandler则可能无法捕获子线程异常。
     * @param exceptionHandler
     */
    public static synchronized void install(ExceptionHandler exceptionHandler){
        if (sInstalled){
            return;
        }
        sInstalled = true;
        sExceptionHandler = exceptionHandler;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        Looper.loop();
                    } catch (Throwable e){
                        if (e instanceof IntercepterException){
                            return;
                        }
                        if (sExceptionHandler != null){
                            sExceptionHandler.handleException(Looper.getMainLooper().getThread(),e);
                        }
                    }
                }
            }
        });
        sUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                if (sExceptionHandler != null){
                    sExceptionHandler.handleException(thread,throwable);
                }
            }
        });
    }

    public static synchronized void unInstall(){
        if (!sInstalled){
            return;
        }
        sInstalled = false;
        sExceptionHandler = null;
        //卸载后恢复默认的异常处理逻辑，否则主线程再次抛出异常后将导致ANR，并且无法捕获到异常位置
        Thread.setDefaultUncaughtExceptionHandler(sUncaughtExceptionHandler);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //主线程抛出异常，迫使while(true){}结束
                throw new IntercepterException("Quit InterceptCrash...");
            }
        });
    }
}
