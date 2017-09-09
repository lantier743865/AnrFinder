package com.lantier.kingsoft.develop.hprof;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by WUXIAOLONG on 2017/9/4.
 */

public class Dump {
    private static final String TAG = "Dump";
    public static boolean createDumpFile(Context context) {
        String LOG_PATH = "/email/dump.gc/";
        boolean bool = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ssss");
        String createTime = sdf.format(new Date(System.currentTimeMillis()));
        String state = Environment.getExternalStorageState();
        // 判断SdCard是否存在并且是可用的
        if(Environment.MEDIA_MOUNTED.equals(state)){
            File file = new File(Environment.getExternalStorageDirectory().getPath()+LOG_PATH);
            if(!file.exists()) {
                file.mkdirs();
            }
            String hprofPath = file.getAbsolutePath();
            if(!hprofPath.endsWith("/")) {
                hprofPath+= "/";
            }

            hprofPath+= createTime + ".hprof";
            Log.e(TAG, "---->>hprofPath: "+hprofPath );
            try {
                android.os.Debug.dumpHprofData(hprofPath);
                bool= true;
                Log.d("ANDROID_LAB", "create dumpfile done!");
            }catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            bool= false;
            Log.d("ANDROID_LAB", "nosdcard!");
        }

        return bool;
    }
}
