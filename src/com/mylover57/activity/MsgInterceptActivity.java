/**
 * 描述：与用户交互的短信拦截设置界面
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
import com.mylover57.db.IMessage;
import com.mylover57.util.Utils;

public class MsgInterceptActivity extends ListActivity implements
		OnClickListener {

	private static final String MSGUPDATE = "UPDATE_MSG_ADAPTER";
	private SimpleCursorAdapter msgAdapter;
	private EditText addBlockEt;
	private ContentValues values;
	private MyApplication myApp;
	private AdapterContextMenuInfo menuInfo;
	private Cursor cursor;
	private UpdateMSGReceiver updateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_intercept_bg);
		obtainAndInit();
		setAdapter();
	}

	private void obtainAndInit() {
		findViewById(R.id.msg_intercept_btn).setOnClickListener(this);
		updateReceiver = new UpdateMSGReceiver();
		addBlockEt = (EditText) findViewById(R.id.msg_intercept_et);
		myApp = (MyApplication) getApplication();
		IntentFilter msgfilter = new IntentFilter(MSGUPDATE);
		registerReceiver(updateReceiver, msgfilter);
		//上下文菜单注册
		registerForContextMenu(getListView());
	}

	private void setAdapter() {
		cursor = myApp.messageQueryAll();
		msgAdapter/*
				 * = new
				 * CommonAdapter<Message>(this,R.layout.msg_item,R.id.msg_name,
				 * R.id.msg_phoneNo,R.id.msg_time,R.id.msg_content) {
				 * 
				 * @Override protected void bindView(Message Message, View...
				 * views) { ((TextView)views[0]).setText(Message.getName());
				 * ((TextView)views[1]).setText(Message.getPhoneNo());
				 * ((TextView)views[2]).setText(Message.getTime());
				 * ((TextView)views[3]).setText(Message.getContent()); } };
				 * setListAdapter(msgAdapter); msgAdapter.add(new
				 * Message("张三","10086", "now","Hello"));
				 */
		= new SimpleCursorAdapter(this, R.layout.msg_item, cursor,
				new String[] { IMessage.Columns.name, IMessage.Columns.num,
						IMessage.Columns.time, IMessage.Columns.content },
				new int[] { R.id.msg_name, R.id.msg_phoneNo, R.id.msg_time,
						R.id.msg_content });
		setListAdapter(msgAdapter);
	}
	//上下文菜单创建
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.phone_context_menu, menu);
		if (v instanceof AdapterView) {
			this.menuInfo = (AdapterContextMenuInfo) menuInfo;
		}
		super.onCreateContextMenu(menu, v, menuInfo);

	}
	//上下文菜单点击事件处理
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Intent intent = new Intent(MSGUPDATE);
		switch (item.getItemId()) {
		case R.id.delete_self:
			myApp.delete(IMessage.MSG_NAME, "phoneNum=?and time=?",
					((TextView) menuInfo.targetView
							.findViewById(R.id.msg_phoneNo)).getText()
							.toString(), ((TextView) menuInfo.targetView
							.findViewById(R.id.msg_time)).getText().toString());
			sendBroadcast(intent);
			break;
		case R.id.delete_num:
			myApp.delete(IMessage.MSG_NAME, "phoneNum=?",
					((TextView) menuInfo.targetView
							.findViewById(R.id.msg_phoneNo)).getText()
							.toString());
			sendBroadcast(intent);
			break;
		case R.id.delete_free:
			myApp.delete(IBlock.BLOCK_NAME, "phoneNum=?",
					((TextView) menuInfo.targetView
							.findViewById(R.id.msg_phoneNo)).getText()
							.toString());
			break;
		}
		return super.onContextItemSelected(item);
	}
	//操作文菜单创建
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.phone_option_menu, menu);
		return true;
	}
	//操作文菜单点击事件处理
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
	//添加黑名单的处理（默认只拦截短信模式）
	@Override
	public void onClick(View v) {
		values = new ContentValues();
		values.clear();
		values.put(IBlock.Columns.num, addBlockEt.getText().toString());
		values.put(IBlock.Columns.kind, "拦截短信");
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

	public class UpdateMSGReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			cursor = myApp.messageQueryAll();
			msgAdapter.changeCursor(cursor);
		}
	}

}