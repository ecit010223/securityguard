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
	// 用来存放报信息的集合
	private List<PackageInfo> packs;
	private MyApplication myApp;
	private Button updateDB;
	private int count;
	private ArrayList<HashMap<String, Object>> items;
	// 便于用户勾选需要安装应用锁的程序的控件
	private CheckBox cb = null;
	// 存放并显示程序名称
	private TextView tv = null;
	// 用于传递package信息的控件，大小为0，不可见
	private TextView pkg = null;
	private ContentValues values = new ContentValues();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.code_select_main);
		setAdapterAndInit();
	}

	// 初始化视图控件并且给listview设置适配器
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
	 * 功能：用来处理异步任务的内部类，提高用户体验度
	 * 第一个参数传入后台线程；第二个传入onProgressUpdate;第三个传入onPostExecute
	 * 除了doInBackground外，其他方法都在一个线程中运行
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
			// 必须先更新视图，才能刷新checkbox
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
	 * 功能：用户点击更新按钮时触发此方法，将数据存入数据库文件中
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
				if (cb.isChecked()) { // 将勾选项存入数据库
					values.clear();
					values.put(Ilock.Columns.appName, tv.getText().toString());
					values.put(Ilock.Columns.className, pkg.getText()
							.toString());
					cursorUpdate = myApp.lockQueryByName(tv.getText()
							.toString());
					if (!cursorUpdate.moveToNext()) {
						myApp.insert(Ilock.LOCK_NAME, values);
					}
				} else { // 未勾选的从数据库中删除
					myApp.delete(Ilock.LOCK_NAME, Ilock.Columns.appName
							+ " = ? ", tv.getText().toString());
				}
				if (cursorUpdate != null)
					cursorUpdate.close();
			}
			Toast.makeText(this, "更新完毕", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
