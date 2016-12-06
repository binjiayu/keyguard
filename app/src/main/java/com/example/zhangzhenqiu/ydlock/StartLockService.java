package com.example.zhangzhenqiu.ydlock;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

public class StartLockService extends Service {

    private static final boolean DBG = true;
    private static final String TAG = "FxLockService";
    private KeyguardManager mKeyguardManager = null;
    private KeyguardManager.KeyguardLock mKeyguardLock = null;
    private LockApplication mLockApplication;

    public static final String LOCK_ACTION = "lock";
    public static final String UNLOCK_ACTION = "unlock";
    private WindowManager mWinMng;
    private Context mContext;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mContext = getApplicationContext();
        mLockApplication = (LockApplication) mContext.getApplicationContext();
        mWinMng = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DBG) Log.d(TAG, "-->onDestroy()");
        stopForeground(true);
        unregisterReceiver();
        startService(new Intent(StartLockService.this, StartLockService.class));

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        if (TextUtils.equals(action, LOCK_ACTION))
            addView();
        else if (TextUtils.equals(action, UNLOCK_ACTION)) {
            removeView();
            stopSelf();
        }
        startForeground(0, null);
        registerReceiver();
        return super.onStartCommand(intent, flags, startId);
    }

    //监听来自用户按Power键点亮点暗屏幕的广播
    private BroadcastReceiver mScreenOnOrOffReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {

                mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                mKeyguardLock = mKeyguardManager.newKeyguardLock("FxLock");
                //屏蔽手机内置的锁屏
                mKeyguardLock.disableKeyguard();
                //启动该第三方锁屏
//
                addView();
            }
        }
    };


    //注册广播监听
    public void registerReceiver() {
        IntentFilter mScreenOnOrOffFilter = new IntentFilter();
        mScreenOnOrOffFilter.addAction("android.intent.action.SCREEN_ON");
        mScreenOnOrOffFilter.addAction("android.intent.action.SCREEN_OFF");
        mScreenOnOrOffFilter.addAction(StartLockView.SHOW_MUSIC);
        StartLockService.this.registerReceiver(mScreenOnOrOffReceiver, mScreenOnOrOffFilter);
    }

    //解除广播监听
    public void unregisterReceiver() {
        if (mScreenOnOrOffReceiver != null) {
            StartLockService.this.unregisterReceiver(mScreenOnOrOffReceiver);
        }
    }

    private LockView lockView;

    public void addView() {
        if (lockView == null) {
            lockView = new LockView(mContext);

            lockView.setOnFinishListener(new LockView.OnFinishListener() {
                @Override
                public void finish() {
                    removeView();
                }
            });

            WindowManager.LayoutParams param = new WindowManager.LayoutParams();
//            param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//6.0以上模拟器会挂，真机可以运行，可能需要权限
            param.type = WindowManager.LayoutParams.TYPE_TOAST;
            param.format = PixelFormat.RGBA_8888; //设置图片格式，效果为背景透明
            // mParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            param.width = WindowManager.LayoutParams.MATCH_PARENT;
            param.height = WindowManager.LayoutParams.MATCH_PARENT;
            mLockApplication.setLock(true);
            mWinMng.addView(lockView, param);

        }
    }

    public void removeView() {
        if (lockView != null) {
            mWinMng.removeView(lockView);
            lockView = null;
        }
    }

}
