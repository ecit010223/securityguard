package com.mylover57.util;

import java.util.TimerTask;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.mylover57.app.MyApplication;

public class LockTask extends TimerTask {

	private static final String IOTEKPROTECTOR = "Iotekprotector";
	public static final String TAG = "LockTask";
	private Context mContext;
	private MyApplication myApp;
	private SharedPreferences preference;
	private ActivityManager mActivityManager;

	public LockTask(Context context) {
		mContext = context;
		mActivityManager = (ActivityManager) context
				.getSystemService("activity");
		myApp = (MyApplication) context.getApplicationContext();
		preference = context.getSharedPreferences(IOTEKPROTECTOR, 0);
	}

	@Override
	public void run() {
		if(preference.getBoolean("lock", false)){
			ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
			String packageName = topActivity.getPackageName();
			Cursor cursor = myApp.lockQueryAll();
			while (cursor.moveToNext()) {
				if (cursor.getString(3).equals(packageName)&&(cursor.getInt(2)!=1)) {
					Intent intent = new Intent();
					intent.setClassName("com.mylover57",
							"com.mylover57.activity.PasswordActivity");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
					myApp.setPackageName(packageName);
				}
			}
			cursor.close();
		}
	}
}
