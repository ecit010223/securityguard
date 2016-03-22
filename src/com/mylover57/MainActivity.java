/**
 * 描述：应用程序的用户主界面
 */
package com.mylover57;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mylover57.activity.CodeEnterActivity;
import com.mylover57.activity.CodeSelectActivity;
import com.mylover57.activity.FlowMainActivity;
import com.mylover57.activity.MsgInterceptActivity;
import com.mylover57.activity.PhoneInterceptActivity;
import com.mylover57.activity.PreferenceActivity;
import com.mylover57.app.MyApplication;
import com.mylover57.db.Ilock;
import com.mylover57.entity.MyButton;
import com.mylover57.receiver.MsgReceiver;
import com.mylover57.service.FloatService;
import com.mylover57.service.LockService;
import com.mylover57.service.PhoneService;
import com.mylover57.service.PhoneService.PhoneBinder;

public class MainActivity extends Activity implements OnClickListener {

	private static final String MSG_FILTER = "android.provider.Telephony.SMS_RECEIVED";
	// sharedpreference 获取的键值
	private static final String IOTEKPROTECTOR = "Iotekprotector";
	// 电话拦截service是否连接的标识符
	private boolean phoneIsConnected = false;
	private MsgReceiver myMSGReceiver;
	private SharedPreferences preference;
	private LinearLayout codeInputLayout;
	private Intent lockIntent;
	private Intent floatIntent;
	private Context context;
	private MyButton phoneBtn;
	private MyButton msgBtn;
	private MyButton setBtn;
	private MyButton flowBtn;
	private MyButton codeBtn;
	private MyButton lockBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mian_layout);

		// 获取试图控件并且初始化
		obtainAndInitView();
		
		////处理拦截短信的服务
		getPreferenceAndInit();
		
		codeInputLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.preference_login, null);
		preference = getSharedPreferences(IOTEKPROTECTOR, MODE_PRIVATE);

		lockIntent = new Intent(context, LockService.class);
		startService(lockIntent);
		floatIntent = new Intent(context, FloatService.class);
		startService(floatIntent);
	}

	private void unBindService() {
		if (phoneIsConnected == true)
			unbindService(conn);
	}

	private void bindPhoneService() {
		Intent intent = new Intent(this, PhoneService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			phoneIsConnected = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			PhoneBinder phoneBinder = (PhoneBinder) service;
			PhoneService phoneService = phoneBinder.getService();
			phoneService.InterceptPhoneMethod();
			phoneIsConnected = true;

		}
	};

	//处理拦截短信的服务
	private void getPreferenceAndInit() {
		myMSGReceiver = new MsgReceiver();
		IntentFilter msgfilter = new IntentFilter(MSG_FILTER);
		msgfilter.setPriority(10);
		registerReceiver(myMSGReceiver, msgfilter);
		bindPhoneService();
	}

	// 获取试图控件并且初始化
	private void obtainAndInitView() {
		context = this;
		phoneBtn = (MyButton) findViewById(R.id.menu_phone);
		msgBtn = (MyButton) findViewById(R.id.menu_msg);
		setBtn = (MyButton) findViewById(R.id.menu_set);
		flowBtn = (MyButton) findViewById(R.id.menu_flow);
		codeBtn = (MyButton) findViewById(R.id.menu_code);
		lockBtn = (MyButton) findViewById(R.id.menu_lock);

		phoneBtn.setOnClickListener(this);
		msgBtn.setOnClickListener(this);
		setBtn.setOnClickListener(this);
		flowBtn.setOnClickListener(this);
		codeBtn.setOnClickListener(this);
		lockBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_phone: // 进入手机拦截
			startActivity(new Intent(this, PhoneInterceptActivity.class));
			/*
			 * Activity的切换动画指的是从一个activity跳转到另外一个activity时的动画.
			 * 一部分是第一个activity退出时的动画；另外一部分时第二个activity进入时的动画；
			 */
			overridePendingTransition(R.anim.in, R.anim.out);
			break;
		case R.id.menu_msg: // 进入短信拦截
			startActivity(new Intent(this, MsgInterceptActivity.class));
			overridePendingTransition(R.anim.in, R.anim.out);
			break;
		case R.id.menu_set: // 进入设置前的密码验证
			showDialog(0);
			break;
		case R.id.menu_flow: // 进入流量设置
			startActivity(new Intent(this, FlowMainActivity.class));
			overridePendingTransition(R.anim.in, R.anim.out);
			break;
		case R.id.menu_code: // 进入密码设置
			startActivity(new Intent(this, CodeEnterActivity.class));
			overridePendingTransition(R.anim.in, R.anim.out);
			break;
		case R.id.menu_lock: // 进入应用锁
			startActivity(new Intent(this, CodeSelectActivity.class));
			overridePendingTransition(R.anim.in, R.anim.out);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setView(codeInputLayout);
		Button ok_btn = (Button) codeInputLayout.findViewById(R.id.ok_btn);
		ok_btn.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText codeET = (EditText) codeInputLayout
						.findViewById(R.id.preference_login_code);
				if (codeET.getText().toString()
						.equals(preference.getString("code", "012"))) {
					dismissDialog(0);
					startActivity(new Intent(context, PreferenceActivity.class));
					overridePendingTransition(R.anim.in, R.anim.out);
				} else {
					Toast.makeText(
							context,
							codeET.getText() + "密码错误"
									+ preference.getString("code", "012"),
							Toast.LENGTH_SHORT).show();
				}

				codeET.setText("");
			}
		});

		return builder.create();
	}

	/*
	 * 功 能：主程序退出的时候调用，回收资源（取消服务绑定，取消广播注册）
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		initLockDB();
		if (myMSGReceiver != null)
			unregisterReceiver(myMSGReceiver);
		unBindService();
		stopService(floatIntent);
	}

	/*
	 * 功 能：主程序退出的时候初始化程序锁的标识符
	 */
	private void initLockDB() {
		ContentValues values = new ContentValues();
		values.clear();
		values.put(Ilock.Columns.checkIn, 0);
		((MyApplication) getApplication()).update(Ilock.LOCK_NAME, values,
				Ilock.Columns.id + " >? ", "0");
	}

}
