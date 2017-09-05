package com.lantier.kingsoft.blockcanary.block;

import android.content.Context;

/**
 * Created by WUXIAOLONG on 2017/9/4.
 */

interface BlockInterceptor {
    void onBlock(Context var1, BlockInfo var2);
}
