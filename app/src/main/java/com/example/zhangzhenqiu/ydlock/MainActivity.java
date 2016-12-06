package com.example.zhangzhenqiu.ydlock;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SearchEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final boolean DBG = true;
    private static final String TAG = "MainActivity";
    public static final int MSG_LAUNCH_HOME = 0;
    public static final int MSG_LAUNCH_DIAL = 1;
    public static final int MSG_LAUNCH_SMS = 2;
    public static final int MSG_LAUNCH_CAMERA = 3;

    private StartLockView mLockView;
    public static StatusViewManager mStatusViewManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DBG) Log.d(TAG, "onCreate()");

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        initViews();
        mStatusViewManager = new StatusViewManager(this, this.getApplicationContext());
//        getWindow().getDecorView().setSystemUiVisibility(Window.FEATURE_ACTION_BAR_OVERLAY);//状态栏的隐藏：
        goTOLock();


    }

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;


    public void goTOLock() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            Intent i = new Intent(MainActivity.this, StartLockService.class);
            i.setAction(StartLockService.LOCK_ACTION);
            startService(i);
            mLockView.setMainHandler(mHandler);
            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                goTOLock();
            } else
            {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //接收来自StarLockView发送的消息，处理解锁、启动相关应用的功能
    private Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            // TODO Auto-generated method stub
            switch (msg.what)
            {
                case MSG_LAUNCH_HOME:
                    finish();
                    break;
                case MSG_LAUNCH_SMS:
                    launchSms();
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

    //启动短信应用
    private void launchSms() {

        //mFocusView.setVisibility(View.GONE);
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.android.mms",
                "com.android.mms.ui.ConversationList");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);

    }

    //启动拨号应用
    private void launchDial() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
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
        startActivity(intent);

    }

    //使back键，音量加减键失效
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return disableKeycode(keyCode, event);
    }

    private boolean disableKeycode(int keyCode, KeyEvent event)
    {
        int key = event.getKeyCode();
        switch (key)
        {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(DBG) Log.d(TAG, "onDestroy()");

    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        if(DBG) Log.d(TAG, "onResume()");
    }

    @Override
    public void onDetachedFromWindow()
    {
        // TODO Auto-generated method stub
        super.onDetachedFromWindow();
        if(DBG) Log.d(TAG, "onDetachedFromWindow()");
        //解除监听
        mStatusViewManager.unregisterComponent();
    }

    public void initViews()
    {
        // TODO Auto-generated method stub
        mLockView = (StartLockView) findViewById(R.id.FxView);
    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return super.onSearchRequested(searchEvent);
    }
}
