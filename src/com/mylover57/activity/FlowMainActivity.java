package com.mylover57.activity;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mylover57.R;
import com.mylover57.service.FlowUpdateService;
import com.mylover57.util.Utils;

public class FlowMainActivity extends Activity implements OnClickListener {

	private static final String NOT_ENOUGH = "总结：本月剩余流量小于10%，请注意资费";
	private static final String IOTEKPROTECTOR = "Iotekprotector";
	private static final String UPDATE = "UPDATE_FLOW";
	private static final CharSequence ENOUGH = "总结：本月剩余流量充足，请放心使用";

	private UpdateFlowReceiver updateFlowReceiver;
	private Context context;
	private ProgressBar progressBar;
	private TextView progress;
	private TextView today;
	private TextView month;
	private TextView remain;
	private Button showList;
	private Button showHistory;
	private Button back;
	private Button mealSet;
	private RelativeLayout flowSetForm;
	private PostListener postListener;
	private NegativeListener negativeListener;
	private float totalMonth;
	protected boolean isConnected;
	private TextView notice;
	private DecimalFormat df;
	private Editor editor;
	private long upload;
	private long download;
	private SharedPreferences preference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flow_main_layout);
		obtainView();
		init();
		update();
		bindFlowService();
	}

	private void obtainView() {
		progressBar = (ProgressBar) findViewById(R.id.flow_main_progressBar);
		progress = (TextView) findViewById(R.id.flow_main_percent);
		today = (TextView) findViewById(R.id.flow_today);
		month = (TextView) findViewById(R.id.flow_month);
		remain = (TextView) findViewById(R.id.flow_remain);
		showList = (Button) findViewById(R.id.flow_main_showList);
		showHistory = (Button) findViewById(R.id.flow_main_history);
		back = (Button) findViewById(R.id.flow_main_back);
		mealSet = (Button) findViewById(R.id.flow_main_meal);
		notice = (TextView) findViewById(R.id.flow_main_notice);

		showList.setOnClickListener(this);
		showHistory.setOnClickListener(this);
		back.setOnClickListener(this);
		mealSet.setOnClickListener(this);
		flowSetForm = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.flow_set_limit_layout, null);
	}

	private void init() {
		postListener = new PostListener();
		preference = getSharedPreferences(IOTEKPROTECTOR, MODE_PRIVATE);
		editor = preference.edit();
		df = new DecimalFormat("0.00000");
		progressBar.setMax(preference.getInt("flow_meal", 30));
		context = this;

		updateFlowReceiver = new UpdateFlowReceiver();
		IntentFilter updateFlowfilter = new IntentFilter(UPDATE);
		registerReceiver(updateFlowReceiver, updateFlowfilter);

	}

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isConnected = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			isConnected = true;
		}
	};

	private void bindFlowService() {
		Intent intent = new Intent(this, FlowUpdateService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	private void unBindService() {
		if (isConnected == true)
			unbindService(conn);
	}

	public void update() {
		download = TrafficStats.getTotalRxBytes(); // 获取总的接受字节数，包含Mobile等
		upload = TrafficStats.getTotalTxBytes(); // 总的发送字节数，包含Mobile等
		Log.i("protector", "download:" + download);
		Log.i("protector", "upload:" + upload);

		if (download > 0 && upload > 0) {
			today.setText(df.format((download + upload) / 1024.00 / 1024) + "");
			month.setText(df
					.format((totalMonth + download + upload) / 1024.00 / 1024)
					+ "");
		} else {
			upload = download = 0;
			month.setText(df.format((totalMonth) / 1024.00 / 1024) + "");
			today.setText(df.format(0) + "");
		}
		float allOfUsedByMonth = (float) ((totalMonth + download + upload) / 1024.00 / 1024);
		remain.setText(df.format(progressBar.getMax() - allOfUsedByMonth) + "");
		progressBar.setProgress((int) allOfUsedByMonth);
		progress.setText(df.format((allOfUsedByMonth * 100 / progressBar
				.getMax())) + "%");
		if (allOfUsedByMonth >= progressBar.getMax() * 0.9) {
			Utils.nearControl(context);
			notice.setText(NOT_ENOUGH);
			notice.setTextColor(getResources().getColor(R.color.red));
			progressBar
					.setBackgroundColor(getResources().getColor(R.color.red));
		} else {
			Utils.cancelNearControl(context);
			notice.setText(ENOUGH);
			notice.setTextColor(getResources().getColor(R.color.green));
			progressBar.setBackgroundColor(getResources().getColor(
					R.color.white));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.flow_main_showList:
			Intent showListIntent = new Intent(this, FlowListActivity.class);
			startActivity(showListIntent);
			overridePendingTransition(R.anim.in, R.anim.out);
			break;
		case R.id.flow_main_history:
			break;
		case R.id.flow_main_meal:
			showDialog(0);
			break;
		case R.id.flow_main_back:
			finish();
			break;
		}
	}

	// 设置流量总数的dialog
	@Override
	protected Dialog onCreateDialog(int id) {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setView(flowSetForm);
		builder.setPositiveButton("确定", postListener);
		builder.setNegativeButton("取消", negativeListener);
		return builder.create();
	}

	class PostListener implements
			android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			EditText max = (EditText) flowSetForm
					.findViewById(R.id.flow_set_input);
			int porgressMax = Integer.parseInt(max.getText().toString().trim());
			progressBar.setMax(porgressMax);
			update();
			editor.putInt("flow_meal", porgressMax);
			editor.commit();
		}
	}

	class NegativeListener implements
			android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
		}
	}

	class UpdateFlowReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			update();
		}
	}

	@Override
	protected void onDestroy() {
		if (updateFlowReceiver != null) {
			unregisterReceiver(updateFlowReceiver);
		}
		unBindService();
		super.onDestroy();
	}

}
