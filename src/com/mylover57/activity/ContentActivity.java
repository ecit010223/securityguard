/**
 * ����������ϵ���б�����Ҫ����ĺ�����
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
		// �õ���ͼ�ؼ����ҳ�ʼ��������������
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
		 * ͨ��ϵͳuri�������ݿ�õ���ϵ����Ϣ
		 */
		cursor = getContentResolver()
				.query(ContactsContract.Data.CONTENT_URI, null,
						"mimetype='vnd.android.cursor.item/phone_v2'", null,
						null);
		// ��ϵͳ����cursor��ʹ���activity����������ͬ
		// Activity���е�managedQuery��startManagingCursor��stopManagingCursor�����ѷ�������CursorLoaderȡ����֮��
		startManagingCursor(cursor);
		adapter = new SimpleCursorAdapter(this, R.layout.content_item_layout,
				cursor, new String[] {
						ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
						ContactsContract.CommonDataKinds.Phone.NUMBER },
				new int[] { R.id.content_item_name, R.id.content_item_num });
		setListAdapter(adapter);
	}

	// ����֮ǰ����ʹ�ù���ϵͳ��Դ
	@Override
	protected void onDestroy() {
		if (cursor != null)
			cursor.close();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.content_select_all: // ��ȫ����ϵ�˼��������
			for (int i = 0; i < adapter.getCount(); i++) {
				View view = getListView().getChildAt(i);
				CheckBox checkBox = (CheckBox) (view
						.findViewById(R.id.content_item_cb));
				checkBox.setChecked(true);
			}
			break;
		case R.id.content_unselect_all: // ȡ������ѡ�е���ϵ��
			for (int i = 0; i < adapter.getCount(); i++) {
				View unview = getListView().getChildAt(i);
				CheckBox uncheckBox = (CheckBox) (unview
						.findViewById(R.id.content_item_cb));
				uncheckBox.setChecked(false);
			}
			break;
		case R.id.content_add_btn: // ��ѡ�е���ϵ�˼������ݿ�
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
	 * ���ܣ��ж��Ƿ��ں�����֮�� ������num��Ҫ�жϵĵ绰����
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
