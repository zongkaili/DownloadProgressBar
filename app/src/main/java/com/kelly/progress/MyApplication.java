package com.kelly.progress;

import android.app.Application;
import android.content.Intent;

import com.kelly.download.DownLoadService;

/**
 * Created by zongkaili on 2017/3/8.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, DownLoadService.class));
    }
}
