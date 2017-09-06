package com.lantier.kingsoft.anrfinder;

import android.app.Application;

import com.lantier.kingsoft.blockcanary.block.BlockCanary;
import com.lantier.kingsoft.blockcanary.block.BlockCanaryContext;

import java.security.AllPermission;

/**
 * Created by wuxiaolong on 2017/9/5.
 */

public class AnrApplication extends Application {
    @Override
    public void onCreate() {
        BlockCanary.install(this,new BlockCanaryContext()).start();
        super.onCreate();
    }
}
