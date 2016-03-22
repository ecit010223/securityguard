/**
 * 描述：处理拦截短信的服务
 */
package com.mylover57.receiver;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.mylover57.R;
import com.mylover57.activity.MsgInterceptActivity;
import com.mylover57.app.MyApplication;
import com.mylover57.db.IMessage;

public class MsgReceiver extends BroadcastReceiver {

	private static final String MSGUPDATE = "UPDATE_MSG_ADAPTER";
	private static final String IOTEKPROTECTOR = "Iotekprotector";
	private static final int MSG_NOTIFY = 0x00001;
	public static final String MSG_FROM = "msg_from";
	public static final String MSG_CONTENT = "msg_content";
	private Context context;
	private MyApplication myApp;
	private ArrayList<String> blockList = new ArrayList<String>();
	private SharedPreferences preference;
	private AudioManager audioManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		myApp = (MyApplication) context.getApplicationContext();
		preference = context.getSharedPreferences(IOTEKPROTECTOR, 0);
		Bundle bundle = intent.getExtras();
		// 获取短信内容
		Object[] msgs = (Object[]) bundle.get("pdus");

		SmsMessage msg = SmsMessage.createFromPdu((byte[]) msgs[0]);

		if (preference.getBoolean("state", false) && isBlocked(msg)) {
			switch (preference.getInt("msg", 0)) {
			case 0:
				abortBroadcast();
				notify(msg);
				updateDB(msg);
				break;
			case 1:
				audioManager = (AudioManager) context
						.getSystemService(Context.AUDIO_SERVICE);
				//将手机的指定类型的声音调整为静音
				//STREAM_NOTIFICATION,系统提示的声音
				audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION,
						true);
				break;
			case 2:
				abortBroadcast();
				updateDB(msg);
				break;
			}
		}
	}

	// 判断是否在黑名单之中
	private boolean isBlocked(SmsMessage msg) {
		Cursor cursor = myApp.blockQueryAll();
		while (cursor.moveToNext()) {
			if (!cursor.getString(2).contains("电话"))
				blockList.add(cursor.getString(1));
		}
		cursor.close();
		for (String str : blockList) {
			// 获取短信发送者的号码
			if (str.equals(msg.getDisplayOriginatingAddress())) {
				return true;
			}
		}
		return false;
	}

	// 状态栏提示用户拦截掉短信
	private void notify(SmsMessage msg) {
		Notification n = new Notification(R.drawable.msg_intercept, "message",
				System.currentTimeMillis());
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(context, MsgInterceptActivity.class);
		intent.putExtra(MSG_CONTENT, msg.getDisplayMessageBody());
		intent.putExtra(MSG_FROM, msg.getDisplayOriginatingAddress());
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);

		n.setLatestEventInfo(context, msg.getDisplayOriginatingAddress(),
				msg.getDisplayMessageBody(), contentIntent);
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(MSG_NOTIFY, n);
	}

	// 更新数据库信息
	private void updateDB(SmsMessage msg) {
		ContentValues values = new ContentValues();
		values.clear();
		values.put(IMessage.Columns.name, "未知");
		values.put(IMessage.Columns.num, msg.getDisplayOriginatingAddress());
		values.put(
				IMessage.Columns.time,
				DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
						DateFormat.MEDIUM).format(new Date()));
		values.put(IMessage.Columns.content, msg.getDisplayMessageBody());
		MyApplication myApp = (MyApplication) context.getApplicationContext();
		myApp.insert(IMessage.MSG_NAME, values);
		// 发送广播提醒拦截页面的listview更新
		Intent intent = new Intent(MSGUPDATE);
		myApp.sendBroadcast(intent);
	}

}
