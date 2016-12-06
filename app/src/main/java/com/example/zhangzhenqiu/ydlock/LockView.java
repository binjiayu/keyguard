package com.example.zhangzhenqiu.ydlock;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by zhangzhenqiu on 2016/11/30.
 */

public class LockView extends FrameLayout {

    private static Context mContext;
    private View rootView;

//    private Button btnUnlock;


    private static final boolean DBG = true;
    private static final String TAG = "aaaaaaLockView";
    public static final int MSG_LAUNCH_HOME = 0;
    public static final int MSG_LAUNCH_DIAL = 1;
    public static final int MSG_LAUNCH_SMS = 2;
    public static final int MSG_LAUNCH_CAMERA = 3;

    private StartLockView mLockView;
    public static StatusViewManager mStatusViewManager;
    private TextView mTimeView;
    private TextView mDataView;
    private TimeChangedReceiver mIntentReceiver;


    public LockView(Context context) {
        this(context, null);

    }

    public LockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.activity_main, this);

        initViews();
        refreshDate();
        mStatusViewManager = new StatusViewManager(this, mContext);
        //关闭系统锁屏
//        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
//        keyguardLock.disableKeyguard();
        mContext.startService(new Intent(mContext, StartLockService.class));
        mLockView.setMainHandler(mHandler);

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.d(TAG, "onWindowFocusChanged: FDFFFFFFF");
        System.out.println("hasfocus--->>>" + hasWindowFocus);
        super.onWindowFocusChanged(hasWindowFocus);
        try {
            Object service = mContext.getSystemService("statusbar");
            Class<?> statusbarManager = Class
                    .forName("android.app.StatusBarManager");
            Method test = statusbarManager.getMethod("collapse");
            test.invoke(service);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        if(!hasWindowFocus) {
//            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            getContext().sendBroadcast(it);//只能隐藏长按电源的弹出框


//
//            try {
//                Object statusBarManager = getContext().getSystemService("statusbar");
//                Method collapse;
//
//                if (Build.VERSION.SDK_INT <= 16) {
//                    collapse = statusBarManager.getClass().getMethod("collapse");
//                } else {
//                    collapse = statusBarManager.getClass().getMethod("collapsePanels");
//                }
//                collapse.invoke(statusBarManager);
//            } catch (Exception localException) {
//                localException.printStackTrace();
//            }
//        }

    }

    //接收来自StarLockView发送的消息，处理解锁、启动相关应用的功能
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MSG_LAUNCH_HOME:
                    finish();
                    break;
                case MSG_LAUNCH_SMS:
                    launchRead();
                    finish();
                    break;
                case MSG_LAUNCH_DIAL:
                    launchDial();
                    finish();
                    break;
                case MSG_LAUNCH_CAMERA:
                    launchCamera();
                    finish();
                    break;
            }
        }

    };

    private void finish() {
        LockApplication lockApplication = (LockApplication)mContext.getApplicationContext();
        lockApplication.setLock(false);
        if (mOnFinishListener != null) {
            mOnFinishListener.finish();
        }
    }

    interface OnFinishListener {
        void finish();
    }

    private OnFinishListener mOnFinishListener;

    public void setOnFinishListener(OnFinishListener onFinishlistener) {
        mOnFinishListener = onFinishlistener;
    }

    //启动阅读应用
    private void launchRead() {

        //mFocusView.setVisibility(View.GONE);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        ComponentName comp = new ComponentName("yd.jv.yl.qqread",
                "yd.jv.yl.qqread.MainActivity");
        intent.setComponent(comp);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        mContext.startActivity(intent);

    }

    //启动拨号应用
    private void launchDial() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        mContext.startActivity(intent);
    }

    //启动相机应用
    private void launchCamera() {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.android.camera",
                "com.android.camera.Camera");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        mContext.startActivity(intent);
    }

    //使back键，音量加减键失效
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return disableKeycode(keyCode, event);
    }

    private boolean disableKeycode(int keyCode, KeyEvent event) {
        int key = event.getKeyCode();
        switch (key) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDetachedFromWindow() {
        // TODO Auto-generated method stub
        super.onDetachedFromWindow();
        if (DBG) Log.d(TAG, "onDetachedFromWindow()");
        //解除监听
        mStatusViewManager.unregisterComponent();
    }

    public void initViews() {
        mLockView = (StartLockView) rootView.findViewById(R.id.FxView);
        mTimeView = (TextView) rootView.findViewById(R.id.time);
        mDataView = (TextView) rootView.findViewById(R.id.date);
        mDateFormat = mContext.getString(R.string.month_day_year);
        registReceiver();
    }

    private void registReceiver() {
        if (mIntentReceiver == null) {
            mIntentReceiver = new TimeChangedReceiver(this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            mContext.registerReceiver(mIntentReceiver, filter);
        }
    }

    private String mDateFormat;
    private void refreshDate() {
        if (mDataView != null) {
            //锁屏界面显示日期
            mDataView.setText(DateFormat.format(mDateFormat, new Date()));
        }
    }
    private static class TimeChangedReceiver extends BroadcastReceiver {
        private WeakReference<LockView> mLockViewWeakReference;
        //private Context mContext;

        public TimeChangedReceiver(LockView lockView) {
            mLockViewWeakReference = new WeakReference(lockView);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
             LockView lockView = mLockViewWeakReference.get();
            if (lockView!=null){
                lockView.refreshDate();
            }else {
                mContext.unregisterReceiver(this);
            }

        }

    }

}
