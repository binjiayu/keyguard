package com.example.zhangzhenqiu.ydlock;

import android.app.Application;

/**
 * Created by ZZQ on 2016/12/5.
 */

public class LockApplication extends Application {
    private boolean ifLock;


    public boolean getLock() {
        return ifLock;
    }

    public void setLock(boolean ifLock) {
        this.ifLock = ifLock;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setLock(false);
    }
}
