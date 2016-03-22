/**
 * 描述：拦截电话的服务
 */
package com.mylover57.service;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.mylover57.R;
import com.mylover57.activity.PhoneInterceptActivity;
import com.mylover57.app.MyApplication;
import com.mylover57.db.IPhone;

public class PhoneService extends Service {

	private static final String PHONEUPDATE = "UPDATE_PHONE_ADAPTER";
	private static final String IOTEKPROTECTOR = "Iotekprotector";
	private static final int PHONENOTIFY = 0x00;
	private MyApplication myApp;
	private Context context = this;
	private SharedPreferences preference;
	// 记录黑名单的List
	ArrayList<String> blockList = new ArrayList<String>();
	TelephonyManager myManager;
	// 监听通话状态的监听器
	MyPhoneCallListener myListener;

	private PhoneBinder binder = new PhoneBinder();
	public AudioManager audioManager;

	public class PhoneBinder extends Binder {

		public PhoneService getService() {
			return PhoneService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	public void InterceptPhoneMethod() {
		myListener = new MyPhoneCallListener();
		preference = getSharedPreferences(IOTEKPROTECTOR, MODE_PRIVATE);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		myManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		/*
		 * 通过TelephonyManager注册我们要监听的电话状态改变事件
		 * 这里的PhoneStateListener.LISTEN_CALL_STATE就是我们想要
		 * 监听的状态改变事件，初次之外，还有很多其他事件哦。
		 */
		myManager.listen(myListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	/*
	 * 功能：继承PhoneStateListener，方便监测电话状态
	 */
	public class MyPhoneCallListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			myApp = (MyApplication) getApplication();

			switch (state) {
			// 手机空闲
			case TelephonyManager.CALL_STATE_IDLE:
				break;
			// 电话被挂起了
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			// 手机铃声响了
			case TelephonyManager.CALL_STATE_RINGING:
				if (preference.getBoolean("state", false)
						&& isBlock(incomingNumber)) {
					Log.i("phone", preference.getInt("phone", 0) + "");
					switch (preference.getInt("phone", 0)) {
					case 0:
						callNotify();
						// case 2 的方法为case 0通用方法，故无break语句
					case 2:
						try {
							Method method = Class.forName(
									"android.os.ServiceManager").getMethod(
									"getService", String.class);
							// 获取远程TELEPHONY_SERVICE的IBinder对象的代理
							IBinder binder = (IBinder) method.invoke(null,
									new Object[] { TELEPHONY_SERVICE });
							// 将IBinder对象的代理转换为ITelephony对象
							ITelephony telephony = ITelephony.Stub
									.asInterface(binder);
							// 挂断电话
							telephony.endCall();
						} catch (Exception e) {
							e.printStackTrace();
						}
						updateDB(myApp, incomingNumber);
						break;
					case 1:
						// 通过音频管理器实现静音设置
						audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						audioManager.setStreamMute(AudioManager.STREAM_RING,
								true);
						break;
					}
				}
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

		// 通过状态栏通知用户拦截电话
		@SuppressWarnings("deprecation")
		private void callNotify() {
			Notification notification = new Notification(
					R.drawable.phone_intercept, "拦截到一个来电",
					System.currentTimeMillis());
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			Intent newIntent = new Intent(context, PhoneInterceptActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(context, "拦截来电", "成功拦截一个电话",
					contentIntent);
			NotificationManager manager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(PHONENOTIFY, notification);
		}

		// 更新数据库并发送广播提示listview更新
		private void updateDB(MyApplication myApp, String incomingNumber) {
			ContentValues values = new ContentValues();
			values.clear();
			values.put(IPhone.Columns.name, "未知");
			values.put(IPhone.Columns.num, incomingNumber);
			values.put(
					IPhone.Columns.time,
					DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
							DateFormat.MEDIUM).format(new Date()));
			myApp.insert(IPhone.CALL_NAME, values);
			Intent intent = new Intent(PHONEUPDATE);
			sendBroadcast(intent);
		}
	}

	// 判断某个电话号码是否在黑名单之内
	public boolean isBlock(String phone) {
		Cursor cursor = myApp.blockQueryAll();
		while (cursor.moveToNext()) {
			if (!cursor.getString(2).contains("短信"))
				blockList.add(cursor.getString(1));
		}
		cursor.close();
		for (String str : blockList) {
			if (str.equals(phone)) {
				return true;
			}
		}
		return false;
	}
}
