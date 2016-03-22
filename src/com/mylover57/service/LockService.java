package com.mylover57.service;

import java.util.Timer;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.mylover57.util.LockTask;

public class LockService extends Service {  
    private Timer mTimer;  
    public static final int FOREGROUND_ID = 0;  
  
    private void startTimer() {  
        if (mTimer == null) {  
            mTimer = new Timer();  
            LockTask lockTask = new LockTask(this);  
            //循环执行task的run方法
            mTimer.schedule(lockTask, 0L, 3000L);  
        }  
    }    
    @Override
	public IBinder onBind(Intent intent) {  
        return null;  
    }    
    @Override
	public void onCreate() {  
        super.onCreate();  
        startForeground(FOREGROUND_ID, new Notification());
    }    
    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {  
        startTimer();  
        return super.onStartCommand(intent, flags, startId);  
    }  
    @Override
	public void onDestroy() {  
        stopForeground(true);  
        mTimer.cancel();  
        mTimer.purge();  
        mTimer = null;  
        super.onDestroy();  
    }  
}
