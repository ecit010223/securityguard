/**
 * 描述：从联系人列表导出需要加入的黑名单
 */
package com.mylover57.activity;

import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mylover57.R;
import com.mylover57.app.MyApplication;
import com.mylover57.db.IBlock;
import com.mylover57.util.Utils;

public class ContentActivity extends ListActivity implements OnClickListener {

	private MyApplication myApp;
	private Cursor cursor;
	private Button selectAll;
	private Button unselectAll;
	private ListAdapter adapter;
	private Button addBtn;
	private int count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_layout);
		// 得到视图控件并且初始化，设置适配器
		init();
	}

	private void init() {
		selectAll = (Button) findViewById(R.id.content_select_all);
		unselectAll = (Button) findViewById(R.id.content_unselect_all);
		addBtn = (Button) findViewById(R.id.content_add_btn);
		selectAll.setOnClickListener(this);
		unselectAll.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		myApp = (MyApplication) getApplication();
		/*
		 * 通过系统uri访问数据库得到联系人信息
		 */
		cursor = getContentResolver()
				.query(ContactsContract.Data.CONTENT_URI, null,
						"mimetype='vnd.android.cursor.item/phone_v2'", null,
						null);
		// 让系统回收cursor，使其和activity生命周期相同
		// Activity类中的managedQuery、startManagingCursor和stopManagingCursor方法已废弃，由CursorLoader取而代之。
		startManagingCursor(cursor);
		adapter = new SimpleCursorAdapter(this, R.layout.content_item_layout,
				cursor, new String[] {
						ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
						ContactsContract.CommonDataKinds.Phone.NUMBER },
				new int[] { R.id.content_item_name, R.id.content_item_num });
		setListAdapter(adapter);
	}

	// 销毁之前回收使用过的系统资源
	@Override
	protected void onDestroy() {
		if (cursor != null)
			cursor.close();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.content_select_all: // 将全部联系人加入黑名单
			for (int i = 0; i < adapter.getCount(); i++) {
				View view = getListView().getChildAt(i);
				CheckBox checkBox = (CheckBox) (view
						.findViewById(R.id.content_item_cb));
				checkBox.setChecked(true);
			}
			break;
		case R.id.content_unselect_all: // 取消所有选中的联系人
			for (int i = 0; i < adapter.getCount(); i++) {
				View unview = getListView().getChildAt(i);
				CheckBox uncheckBox = (CheckBox) (unview
						.findViewById(R.id.content_item_cb));
				uncheckBox.setChecked(false);
			}
			break;
		case R.id.content_add_btn: // 将选中的联系人加入数据库
			count = 0;
			for (int i = 0; i < adapter.getCount(); i++) {
				View view = getListView().getChildAt(i);
				CheckBox checkBox = (CheckBox) (view
						.findViewById(R.id.content_item_cb));
				TextView numTv = (TextView) view
						.findViewById(R.id.content_item_num);
				String num = numTv.getText().toString();
				if (checkBox.isChecked() && !isBlocked(num)) {
					ContentValues values = new ContentValues();
					values.put(IBlock.Columns.num, num);
					myApp.insert(IBlock.BLOCK_NAME, values);
					count++;
				}
			}
			if (count == 0)
				Utils.failedToast(this);
			else
				Utils.successToast(this);
			break;
		}
	}

	/*
	 * 功能：判断是否在黑名单之中 参数：num需要判断的电话号码
	 */
	public boolean isBlocked(String num) {
		cursor = myApp.blockQueryAll();
		cursor.moveToFirst();
		while (cursor.moveToNext()) {
			if (cursor.getString(1).equals(num)) {
				return true;
			}
		}
		return false;

	}
}
