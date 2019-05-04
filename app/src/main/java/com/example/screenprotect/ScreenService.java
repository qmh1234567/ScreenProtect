package com.example.screenprotect;
//https://blog.csdn.net/u010072711/article/details/50096181
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ScreenService extends Service {
    KeyguardManager mKeyguardManager = null;
    private KeyguardLock mKeyguardLock = null;
    BroadcastReceiver mMasterResetReciever;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate() {
        //Log.e("ScreenService","onCreate()");
        // TODO Auto-generated method stub
        startScreenService();
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // Log.e("ScreenService","onStart");
        // TODO Auto-generated method stub
        startScreenService();
    }

    private void startScreenService(){
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManager.newKeyguardLock("");


        mKeyguardLock.disableKeyguard();

        //Intent.ACTION_SCREEN_OFF
        mMasterResetReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    Intent i = new Intent();
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setClass(context, ScreenSaverActivity.class);
                    context.startActivity(i);
                } catch (Exception e) {
                    Log.i("mMasterResetReciever:", e.toString());
                }
            }
        };
        registerReceiver(mMasterResetReciever, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public void onDestroy() {
        //Log.e("ScreenService","onDestroy()");
        super.onDestroy();
        unregisterReceiver(mMasterResetReciever);
        ScreenService.this.stopSelf();
    }
}

