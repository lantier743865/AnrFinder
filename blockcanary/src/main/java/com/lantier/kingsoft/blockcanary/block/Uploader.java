package com.lantier.kingsoft.blockcanary.block;

import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by WUXIAOLONG on 2017/9/4.
 */

class Uploader {
    private static final String TAG = "Uploader";
    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);

    private Uploader() {
        throw new InstantiationError("Must not instantiate this class");
    }

    private static File zip() {
        String timeString = Long.toString(System.currentTimeMillis());
        try {
            timeString = FORMAT.format(new Date());
        } catch (Throwable e) {
            Log.e(TAG, "zip: ", e);
        }
        File zippedFile = LogWriter.generateTempZip("BlockCanary-" + timeString);
        Log.e(TAG, "--->>zip: "+zippedFile.getAbsolutePath() );
        BlockCanaryInternals.getContext().zip(BlockCanaryInternals.getLogFiles(), zippedFile);
//        LogWriter.deleteAll();
        return zippedFile;
    }

    public static void zipAndUpload() {
        Log.e(TAG, "---->>zipAndUpload: this is the place of zip begaing" );
        HandlerThreadFactory.getWriteLogThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                final File file = zip();
                if (file.exists()) {
                    Log.e(TAG, "---->>run: file.exists()" );
                    BlockCanaryInternals.getContext().upload(file);
                }
            }
        });
    }
}
