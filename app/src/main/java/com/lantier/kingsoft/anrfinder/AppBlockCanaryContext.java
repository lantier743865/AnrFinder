package com.lantier.kingsoft.anrfinder;


import android.util.Log;

import com.lantier.kingsoft.develop.block.BlockCanaryContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by WUXIAOLONG on 2017/9/4.
 */

class AppBlockCanaryContext extends BlockCanaryContext {
    private static final String TAG = "AppBlockCanaryContext";
    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
    @Override
    public int provideMonitorDuration() {
        return 9999;
    }

    @Override
    public int provideBlockThreshold() {
        return 500;
    }

    @Override
    public void upload(File zippedFile) {
        Log.e(TAG, "---->>upload: "+zippedFile.getAbsolutePath() );
        //此处可以上传文件
        super.upload(zippedFile);
    }

    @Override
    public boolean zip(File[] src, File dest) {
        try {
            zipFiles(src,dest);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "---->>zip: zip faied" );
        }
        return true;
    }
    public static void zipFiles(File[] src, File zipFile) throws IOException {
        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                zipFile), BUFF_SIZE));
        for (File resFile : src) {
            zipFile(resFile, zipout, "");
        }
        zipout.close();
    }
    private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath)
            throws FileNotFoundException, IOException {
        rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator)
                + resFile.getName();
        rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            for (File file : fileList) {
                zipFile(file, zipout, rootpath);
            }
        } else {
            byte buffer[] = new byte[BUFF_SIZE];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile),
                    BUFF_SIZE);
            zipout.putNextEntry(new ZipEntry(rootpath));
            int realLength;
            while ((realLength = in.read(buffer)) != -1) {
                zipout.write(buffer, 0, realLength);
            }
            in.close();
            zipout.flush();
            zipout.closeEntry();
        }
    }
}
