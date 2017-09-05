package com.lantier.kingsoft.blockcanary.block;

import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.lantier.kingsoft.blockcanary.hprof.Dump;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by WUXIAOLONG on 2017/9/4.
 */

public class BlockCanaryInternals {
    private static final String TAG = "BlockCanaryInternals";

    LooperMonitor monitor;
    StackSampler stackSampler;
    CpuSampler cpuSampler;

    private static BlockCanaryInternals sInstance;
    private static BlockCanaryContext sContext;

    private List<BlockInterceptor> mInterceptorChain = new LinkedList<>();

    public BlockCanaryInternals() {

        stackSampler = new StackSampler(
                Looper.getMainLooper().getThread(),
                sContext.provideDumpInterval());

        cpuSampler = new CpuSampler(sContext.provideDumpInterval());

        setMonitor(new LooperMonitor(new LooperMonitor.BlockListener() {

            @Override
            public void onBlockEvent(long realTimeStart, long realTimeEnd,
                                     long threadTimeStart, long threadTimeEnd) {
                // Get recent thread-stack entries and cpu usage
                ArrayList<String> threadStackEntries = stackSampler
                        .getThreadStackEntries(realTimeStart, realTimeEnd);
                if (!threadStackEntries.isEmpty()) {
                    BlockInfo blockInfo = BlockInfo.newInstance()
                            .setMainThreadTimeCost(realTimeStart, realTimeEnd, threadTimeStart, threadTimeEnd)
                            .setCpuBusyFlag(cpuSampler.isCpuBusy(realTimeStart, realTimeEnd))
                            .setRecentCpuRate(cpuSampler.getCpuRateInfo())
                            .setThreadStackEntries(threadStackEntries)
                            .flushString();
                    Log.e(TAG, "---->>onBlockEvent: "+blockInfo.toString() );
                    String save = LogWriter.save(blockInfo.toString());
                    boolean dumpFile = Dump.createDumpFile(sContext.provideContext());
                    Log.e(TAG, "---->>dumpFile: "+dumpFile );

                    Log.e(TAG, "---->>save: "+save );
//                    Uploader.zipAndUpload();
//                    copyLogToSDcard(save);
                    if (mInterceptorChain.size() != 0) {
                        for (BlockInterceptor interceptor : mInterceptorChain) {
                            interceptor.onBlock(getContext().provideContext(), blockInfo);
                        }
                    }
                }
            }
        }, getContext().provideBlockThreshold(), getContext().stopWhenDebugging()));

        LogWriter.cleanObsolete();
    }
    private static String ANR_KINGSOFT = "/anr_kingsoft/";
    private void copyLogToSDcard(String save) {
        String newDir = Environment.getExternalStorageDirectory() + ANR_KINGSOFT;
        if (!new File(save).exists()){
            return;
        }
        File file = new File(newDir);
        if(!file.exists()) {
            file.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ssss");
        String createTime = sdf.format(new Date(System.currentTimeMillis()));
        String newFile = newDir+createTime+".log";
        Log.e(TAG, "---->>newFile: "+newFile );
        copyFile(save,newFile);
    }
    private static void copyFile(String oldPath, String newPath)
    {
        try
        {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (!newfile.exists())
            {
                newfile.createNewFile();
            }
            if (oldfile.exists())
            {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[2000];
                while ((byteread = inStream.read(buffer)) != -1)
                {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                Log.e(TAG, "---->>copyFile: success" );
            }
        }
        catch (Exception e)
        {
            Log.d(TAG,"copy file err!");
            e.printStackTrace();

        }

    }

    /**
     * Get BlockCanaryInternals singleton
     *
     * @return BlockCanaryInternals instance
     */
    static BlockCanaryInternals getInstance() {
        if (sInstance == null) {
            synchronized (BlockCanaryInternals.class) {
                if (sInstance == null) {
                    sInstance = new BlockCanaryInternals();
                }
            }
        }
        return sInstance;
    }

    /**
     * set {@link BlockCanaryContext} implementation
     *
     * @param context context
     */
    public static void setContext(BlockCanaryContext context) {
        sContext = context;
    }

    public static BlockCanaryContext getContext() {
        return sContext;
    }

    void addBlockInterceptor(BlockInterceptor blockInterceptor) {
        mInterceptorChain.add(blockInterceptor);
    }

    private void setMonitor(LooperMonitor looperPrinter) {
        monitor = looperPrinter;
    }

    long getSampleDelay() {
        return (long) (BlockCanaryInternals.getContext().provideBlockThreshold() * 0.8f);
    }

    static String getPath() {
        String state = Environment.getExternalStorageState();
        String logPath = BlockCanaryInternals.getContext()
                == null ? "" : BlockCanaryInternals.getContext().providePath();

        if (Environment.MEDIA_MOUNTED.equals(state)
                && Environment.getExternalStorageDirectory().canWrite()) {
            return Environment.getExternalStorageDirectory().getPath() + logPath;
        }
        return Environment.getDataDirectory().getAbsolutePath() + BlockCanaryInternals.getContext().providePath();
    }

    static File detectedBlockDirectory() {
        File directory = new File(getPath());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    public static File[] getLogFiles() {
        File f = detectedBlockDirectory();
        if (f.exists() && f.isDirectory()) {
            return f.listFiles(new BlockLogFileFilter());
        }
        return null;
    }

    private static class BlockLogFileFilter implements FilenameFilter {

        private String TYPE = ".log";

        BlockLogFileFilter() {

        }

        @Override
        public boolean accept(File dir, String filename) {
            return filename.endsWith(TYPE);
        }
    }
}
