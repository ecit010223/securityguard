/**
 * ���������ص绰�ķ���
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
	// ��¼��������List
	ArrayList<String> blockList = new ArrayList<String>();
	TelephonyManager myManager;
	// ����ͨ��״̬�ļ�����
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
		 * ͨ��TelephonyManagerע������Ҫ�����ĵ绰״̬�ı��¼�
		 * �����PhoneStateListener.LISTEN_CALL_STATE����������Ҫ
		 * ������״̬�ı��¼�������֮�⣬���кܶ������¼�Ŷ��
		 */
		myManager.listen(myListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	/*
	 * ���ܣ��̳�PhoneStateListener��������绰״̬
	 */
	public class MyPhoneCallListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			myApp = (MyApplication) getApplication();

			switch (state) {
			// �ֻ�����
			case TelephonyManager.CALL_STATE_IDLE:
				break;
			// �绰��������
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			// �ֻ���������
			case TelephonyManager.CALL_STATE_RINGING:
				if (preference.getBoolean("state", false)
						&& isBlock(incomingNumber)) {
					Log.i("phone", preference.getInt("phone", 0) + "");
					switch (preference.getInt("phone", 0)) {
					case 0:
						callNotify();
						// case 2 �ķ���Ϊcase 0ͨ�÷���������break���
					case 2:
						try {
							Method method = Class.forName(
									"android.os.ServiceManager").getMethod(
									"getService", String.class);
							// ��ȡԶ��TELEPHONY_SERVICE��IBinder����Ĵ���
							IBinder binder = (IBinder) method.invoke(null,
									new Object[] { TELEPHONY_SERVICE });
							// ��IBinder����Ĵ���ת��ΪITelephony����
							ITelephony telephony = ITelephony.Stub
									.asInterface(binder);
							// �Ҷϵ绰
							telephony.endCall();
						} catch (Exception e) {
							e.printStackTrace();
						}
						updateDB(myApp, incomingNumber);
						break;
					case 1:
						// ͨ����Ƶ������ʵ�־�������
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

		// ͨ��״̬��֪ͨ�û����ص绰
		@SuppressWarnings("deprecation")
		private void callNotify() {
			Notification notification = new Notification(
					R.drawable.phone_intercept, "���ص�һ������",
					System.currentTimeMillis());
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			Intent newIntent = new Intent(context, PhoneInterceptActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(context, "��������", "�ɹ�����һ���绰",
					contentIntent);
			NotificationManager manager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(PHONENOTIFY, notification);
		}

		// �������ݿⲢ���͹㲥��ʾlistview����
		private void updateDB(MyApplication myApp, String incomingNumber) {
			ContentValues values = new ContentValues();
			values.clear();
			values.put(IPhone.Columns.name, "δ֪");
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

	// �ж�ĳ���绰�����Ƿ��ں�����֮��
	public boolean isBlock(String phone) {
		Cursor cursor = myApp.blockQueryAll();
		while (cursor.moveToNext()) {
			if (!cursor.getString(2).contains("����"))
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
