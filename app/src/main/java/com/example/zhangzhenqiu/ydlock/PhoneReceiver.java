package com.example.zhangzhenqiu.ydlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.TELEPHONY_SERVICE;

public class PhoneReceiver extends BroadcastReceiver {
    public  static String TAG = "AAAAA";
    LockApplication mLockApplication;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mLockApplication = (LockApplication) mContext.getApplicationContext();
        System.out.println("action" + intent.getAction());
        //如果是去电
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent
                    .getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG, "call OUT:" + phoneNumber);
        } else {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
//
//            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
//            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
//            //设置一个监听器
        }
    }
    private boolean isLock;


    PhoneStateListener listener = new PhoneStateListener() {


        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            //注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mLockApplication.getLock()){
                        Intent i = new Intent(mContext, StartLockService.class);
                        i.setAction(StartLockService.LOCK_ACTION);
                        mContext.startService(i);
                    }
                    Log.d(TAG, "onCallStateChanged: 挂断");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG, "onCallStateChanged: 接听");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(TAG, "onCallStateChanged: 响铃:来电号码" + incomingNumber);
                    Toast.makeText(mContext,"响铃:来电号码 + incomingNumber", Toast.LENGTH_SHORT);
                    //输出来电号码
                    Intent i = new Intent(mContext, StartLockService.class);
                    i.setAction(StartLockService.UNLOCK_ACTION);
                    mContext.startService(i);
                    break;
            }
        }
    };
}