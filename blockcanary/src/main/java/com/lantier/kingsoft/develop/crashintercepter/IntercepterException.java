package com.lantier.kingsoft.develop.crashintercepter;

/**
 * Created by wuxiaolong on 2017/9/9.
 */

public class IntercepterException extends RuntimeException {
    public IntercepterException() {
    }

    public IntercepterException(String message) {
        super(message);
    }

    public IntercepterException(String message, Throwable cause) {
        super(message, cause);
    }

    public IntercepterException(Throwable cause) {
        super(cause);
    }

    public IntercepterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
