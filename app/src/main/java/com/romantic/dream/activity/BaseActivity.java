package com.romantic.dream.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${chenM} on ${2017}.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected static final String TAG_ESC_ACTIVITY = "com.broader.esc";
    private MyBroaderEsc receiver;//广播
    private Unbinder butterKnife;//取消绑定
    protected boolean startBlockKeys = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        receiver = new MyBroaderEsc();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TAG_ESC_ACTIVITY);
        registerReceiver(receiver, filter);
        butterKnife = ButterKnife.bind(this);
        initData();
    }

    protected abstract int getLayoutId();//获取布局layout

    protected abstract void initData();//初始化数据

    class MyBroaderEsc extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TAG_ESC_ACTIVITY.equals(action)){
                butterKnife.unbind();
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_BACK:
                if (startBlockKeys) return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
