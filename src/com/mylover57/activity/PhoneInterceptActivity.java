/**
 * 描述：手机拦截设置界面
 */
package com.mylover57.activity;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mylover57.R;
import com.mylover57.app.MyApplication;
import com.mylover57.db.IBlock;
import com.mylover57.db.IPhone;
import com.mylover57.util.Utils;

public class PhoneInterceptActivity extends ListActivity implements
		OnClickListener {
	private static final String PHONEUPDATE = "UPDATE_PHONE_ADAPTER";
	private Context context;
	private SimpleCursorAdapter phoneAdapter;
	private MyApplication myApp;
	private EditText addBlockEt;
	private AdapterContextMenuInfo menuInfo;
	private Cursor cursor;
	private UpdatePhoneReceiver updateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_intercept_bg);
		initView();
		setAdapter();
	}

	private void initView() {
		updateReceiver = new UpdatePhoneReceiver();
		myApp = (MyApplication) getApplication();
		addBlockEt = (EditText) findViewById(R.id.phone_intercept_et);
		
		findViewById(R.id.phone_intercept_btn).setOnClickListener(this);
		
		registerForContextMenu(getListView());
		
		IntentFilter phonefilter = new IntentFilter(PHONEUPDATE);
		registerReceiver(updateReceiver, phonefilter);
		context = this;
	}

	private void setAdapter() {
		cursor = myApp.phoneQueryAll();
		phoneAdapter /*
					 * = new
					 * CommonAdapter<Phone>(this,R.layout.phone_item,R.id.phone_name
					 * , R.id.phone_num,R.id.phone_time) {
					 * 
					 * @Override protected void bindView(Phone phone, View...
					 * views) { ((TextView)views[0]).setText(phone.getName());
					 * ((TextView)views[1]).setText(phone.getPhoneNo());
					 * ((TextView)views[2]).setText(phone.getTime()); } };
					 */= new SimpleCursorAdapter(this, R.layout.phone_item,
				cursor, new String[] { IPhone.Columns.name, IPhone.Columns.num,
						IPhone.Columns.time }, new int[] { R.id.phone_name,
						R.id.phone_num, R.id.phone_time });
		setListAdapter(phoneAdapter);
	}

	/**
	 * v上下文菜单的拥有者
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.phone_context_menu, menu);
		if (v instanceof AdapterView) {
			this.menuInfo = (AdapterContextMenuInfo) menuInfo;
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Intent intent = new Intent(PHONEUPDATE);
		switch (item.getItemId()) {
		case R.id.delete_self:
			myApp.delete(
					IPhone.CALL_NAME,
					"phoneNum=?and time=?",
					((TextView) menuInfo.targetView
							.findViewById(R.id.phone_num)).getText().toString(),
					((TextView) menuInfo.targetView
							.findViewById(R.id.phone_time)).getText()
							.toString());
			sendBroadcast(intent);
			break;
		case R.id.delete_num:
			myApp.delete(IPhone.CALL_NAME, "phoneNum=?",
					((TextView) menuInfo.targetView
							.findViewById(R.id.phone_num)).getText().toString());
			sendBroadcast(intent);
			break;
		case R.id.delete_free:
			myApp.delete(IBlock.BLOCK_NAME, "phoneNum=?",
					((TextView) menuInfo.targetView
							.findViewById(R.id.phone_num)).getText().toString());
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.phone_option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_black:
			startActivity(new Intent(this, ContentActivity.class));
			overridePendingTransition(R.anim.in, R.anim.out);
			break;
		case R.id.query_black:
			startActivity(new Intent(this, BlockActivity.class));
			overridePendingTransition(R.anim.in, R.anim.out);
			break;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		if (cursor != null)
			cursor.close();
		if (updateReceiver != null)
			unregisterReceiver(updateReceiver);
		super.onDestroy();
	}

	// 将用户输入的电话加入数据库黑名单中
	@Override
	public void onClick(View v) {
		if (!addBlockEt.getText().toString().equals("")) {
			if (addBlockEt.getText().toString() != null) {
				ContentValues values = new ContentValues();
				values.clear();
				values.put(IBlock.Columns.num, addBlockEt.getText().toString());
				values.put(IBlock.Columns.kind, "拦截电话");
				addBlockEt.setText("");
				long id = 0;
				try {
					id = myApp.insert(IBlock.BLOCK_NAME, values);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (id >= 1)
					Utils.successToast(this);
				else
					Utils.failedToast(this);
			}
		}
	}

	// 广播接收器，接收到数据库改变信息则更新listview
	private class UpdatePhoneReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context cxt, Intent intent) {
			cxt = context;
			cursor = myApp.phoneQueryAll();
			phoneAdapter.changeCursor(cursor);
		}

	}

}
