package com.mylover57.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mylover57.R;
import com.mylover57.app.MyApplication;
import com.mylover57.db.Ilock;
import com.mylover57.util.Utils;

public class PasswordActivity extends Activity {

	private static final String IOTEKPROTECTOR = "Iotekprotector";
	private Button cancel;
	private Button okBtn;
	private EditText passwordEditText;
	private SharedPreferences preference;
	private MyApplication myApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lock_password);
		myApp = (MyApplication) getApplication();
		preference = getSharedPreferences(IOTEKPROTECTOR, MODE_PRIVATE);
		passwordEditText = (EditText) findViewById(R.id.lock_code);
		cancel = (Button) findViewById(R.id.lock_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BackToDeskTop();
			}
		});
		okBtn = (Button) findViewById(R.id.lock_ok);
		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String className = myApp.getPackageName();
				if (passwordEditText.getText().toString()
						.equals(preference.getString("code", "012"))) {
					ContentValues values = new ContentValues();
					Cursor cursor = myApp.lockQueryByClass(className);
					if(cursor.moveToNext()){
						values.clear();
						values.put(Ilock.Columns.checkIn, 1);
						myApp.update(Ilock.LOCK_NAME, values, Ilock.Columns.className+" = ?", className);
					}
					if(cursor != null)
						cursor.close();
					finish();
				}else{
					Utils.code_error_Toast(PasswordActivity.this);
				}
			}
		});
	}
	//返回桌面
	private void BackToDeskTop() {
		Intent intent= new Intent(Intent.ACTION_MAIN); 
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识   
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);   
		onDestroy();
	}	
	//复写后退键，防止异常退出锁
	@Override
	public void onBackPressed() {
	}
	//如果密码所界面被遮盖，则销毁
	@Override
	public void onPause() {
		super.onPause();
		finish();
	}
}
