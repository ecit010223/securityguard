package com.mylover57.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mylover57.R;
import com.mylover57.app.MyApplication;
import com.mylover57.app.adapter.CommonAdapter;
import com.mylover57.db.Ilock;
import com.mylover57.util.Utils;

public class CodeSelectActivity extends ListActivity implements OnClickListener {

	private CommonAdapter<HashMap<String, Object>> myAdapter;
	// ������ű���Ϣ�ļ���
	private List<PackageInfo> packs;
	private MyApplication myApp;
	private Button updateDB;
	private int count;
	private ArrayList<HashMap<String, Object>> items;
	// �����û���ѡ��Ҫ��װӦ�����ĳ���Ŀؼ�
	private CheckBox cb = null;
	// ��Ų���ʾ��������
	private TextView tv = null;
	// ���ڴ���package��Ϣ�Ŀؼ�����СΪ0�����ɼ�
	private TextView pkg = null;
	private ContentValues values = new ContentValues();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.code_select_main);
		setAdapterAndInit();
	}

	// ��ʼ����ͼ�ؼ����Ҹ�listview����������
	private void setAdapterAndInit() {
		PackageManager pckMan = getPackageManager();
		packs = pckMan.getInstalledPackages(0);
		myApp = (MyApplication) getApplication();
		updateDB = (Button) findViewById(R.id.lock_select_update);
		updateDB.setOnClickListener(this);

		myAdapter = new CommonAdapter<HashMap<String, Object>>(this,
				R.layout.code_select_item, R.id.lock_item_img,
				R.id.lock_item_name, R.id.lock_item_select,
				R.id.lock_item_package) {

			@SuppressWarnings("deprecation")
			@Override
			protected void bindView(HashMap<String, Object> map, View... views) {
				views[0].setBackgroundDrawable((Drawable) map.get("img"));
				((TextView) views[1]).setText((String) map.get("appsname"));
				Cursor cursor = myApp.lockQueryByName((String) map
						.get("appsname"));
				((CheckBox) views[2]).setChecked(cursor.moveToNext());
				((TextView) views[3]).setText((String) map.get("className"));
				if (cursor != null)
					cursor.close();
			}
		};
		items = (ArrayList<HashMap<String, Object>>) myAdapter.getList();
		new LoadListData().execute();
	}

	/**
	 * ���ܣ����������첽������ڲ��࣬����û������
	 * ��һ�����������̨�̣߳��ڶ�������onProgressUpdate;����������onPostExecute
	 * ����doInBackground�⣬������������һ���߳�������
	 */
	class LoadListData extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			count = 0;
			setListAdapter(myAdapter);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			myAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Void result) {
			// �����ȸ�����ͼ������ˢ��checkbox
			Utils.successToast(CodeSelectActivity.this);
		}

		@Override
		protected Void doInBackground(Void... params) {
			for (PackageInfo p : packs) {
				if (count > 15)
					return null;
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("img", p.applicationInfo.loadIcon(getPackageManager()));
				item.put("appsname",
						p.applicationInfo.loadLabel(getPackageManager())
								.toString());
				item.put("className", p.packageName);
				items.add(item);
				publishProgress();
				try {
					count++;
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	/*
	 * ���ܣ��û�������°�ťʱ�����˷����������ݴ������ݿ��ļ���
	 */
	@Override
	public void onClick(View view) {
		Cursor cursorUpdate = null;
		LinearLayout itemLayout = null;
		switch (view.getId()) {
		case R.id.lock_select_update:
			for (int i = 0; i < 8; i++) {
				itemLayout = (LinearLayout) (getListView().getChildAt(i));
				tv = (TextView) itemLayout.findViewById(R.id.lock_item_name);
				cb = (CheckBox) itemLayout.findViewById(R.id.lock_item_select);
				pkg = (TextView) itemLayout
						.findViewById(R.id.lock_item_package);
				if (cb.isChecked()) { // ����ѡ��������ݿ�
					values.clear();
					values.put(Ilock.Columns.appName, tv.getText().toString());
					values.put(Ilock.Columns.className, pkg.getText()
							.toString());
					cursorUpdate = myApp.lockQueryByName(tv.getText()
							.toString());
					if (!cursorUpdate.moveToNext()) {
						myApp.insert(Ilock.LOCK_NAME, values);
					}
				} else { // δ��ѡ�Ĵ����ݿ���ɾ��
					myApp.delete(Ilock.LOCK_NAME, Ilock.Columns.appName
							+ " = ? ", tv.getText().toString());
				}
				if (cursorUpdate != null)
					cursorUpdate.close();
			}
			Toast.makeText(this, "�������", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
