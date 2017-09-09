package com.lantier.kingsoft.develop.block;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by WUXIAOLONG on 2017/9/4.
 */

class ProcessUtils {
    private static volatile String sProcessName;
    private final static Object sNameLock = new Object();

    private ProcessUtils() {
        throw new InstantiationError("Must not instantiate this class");
    }

    public static String myProcessName() {
        if (sProcessName != null) {
            return sProcessName;
        }
        synchronized (sNameLock) {
            if (sProcessName != null) {
                return sProcessName;
            }
            sProcessName = obtainProcessName(BlockCanaryInternals.getContext().provideContext());
            return sProcessName;
        }
    }

    private static String obtainProcessName(Context context) {
        final int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listTaskInfo = am.getRunningAppProcesses();
        if (listTaskInfo != null && !listTaskInfo.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo info : listTaskInfo) {
                if (info != null && info.pid == pid) {
                    return info.processName;
                }
            }
        }
        return null;
    }
}
