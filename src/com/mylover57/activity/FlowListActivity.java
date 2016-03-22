package com.mylover57.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mylover57.R;
import com.mylover57.app.adapter.CommonAdapter;
import com.mylover57.util.Utils;

public class FlowListActivity extends ListActivity {

	private CommonAdapter<HashMap<String, Object>> myAdapter;
	private List<PackageInfo> packs;
	private ArrayList<HashMap<String, Object>> items;
	private LoadListData lld = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		myAdapter = new CommonAdapter<HashMap<String, Object>>(this,
				R.layout.flow_list_item, R.id.flow_item_img,
				R.id.flow_item_name, R.id.flow_item_upload,
				R.id.flow_item_download, R.id.flow_item_total) {

			@Override
			protected void bindView(HashMap<String, Object> map, View... views) {
				views[0].setBackgroundDrawable((Drawable) map.get("img"));
				((TextView) views[1]).setText((String) map.get("appsname"));
				((TextView) views[2]).setText((String) map.get("upload"));
				((TextView) views[3]).setText((String) map.get("download"));
				((TextView) views[4]).setText((String) map.get("all"));
			}
		};

		PackageManager pckMan = getPackageManager();
		// 获取已经注册的包的list
		packs = pckMan.getInstalledPackages(0);
		items = (ArrayList<HashMap<String, Object>>) myAdapter.getList();
		// 使用异步任务来处理数据量比较大的任务
		setListAdapter(myAdapter);
		lld = new LoadListData();
		lld.execute();
	}

	class LoadListData extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			Utils.successToast(FlowListActivity.this);
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			myAdapter.notifyDataSetChanged();
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Void... params) {
			for (PackageInfo p : packs) {
				int appid = p.applicationInfo.uid;
				long upload = TrafficStats.getUidRxBytes(appid);
				long download = TrafficStats.getUidTxBytes(appid);
				long total = upload + download;
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("img", p.applicationInfo.loadIcon(getPackageManager()));
				item.put("appsname",
						p.applicationInfo.loadLabel(getPackageManager())
								.toString());
				item.put("upload", upload > 0 ? upload + "" : "0");
				item.put("download", download > 0 ? download + "" : "0");
				item.put("all", total > 0 ? total + "" : "0");
				items.add(item);
				publishProgress();

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}
}
