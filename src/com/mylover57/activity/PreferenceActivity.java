/**
 * 描述：配置信息设置界面
 */
package com.mylover57.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.mylover57.R;

public class PreferenceActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private static final String PHONE_DIALOG = "电话拦截选项";
	private static final String MSG_DIALOG = "短信拦截选项";
	private static final String IOTEKPROTECTOR = "Iotekprotector";
	private static final int PHONEDIALOG = 0x110;
	private static final int MSGDIALOG = 0x111;
	private Button backBtn;
	private Button phoneBtn;
	private Button msgBtn;
	private ToggleButton interceptTgbtn;
	private ToggleButton flowTgbtn;
	private ToggleButton lockTgbtn;
	private SharedPreferences preference;
	private Editor editor;
	private int flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preference_set_);
		obtainViewAndInit();

		phoneBtn.setOnClickListener(this);
		msgBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		interceptTgbtn.setOnCheckedChangeListener(this);
		flowTgbtn.setOnCheckedChangeListener(this);
		lockTgbtn.setOnCheckedChangeListener(this);
	}

	private void obtainViewAndInit() {
		phoneBtn = (Button) findViewById(R.id.preference_phone_btn);
		msgBtn = (Button) findViewById(R.id.preference_msg_btn);
		interceptTgbtn = (ToggleButton) findViewById(R.id.preference_state_tgbtn);
		flowTgbtn = (ToggleButton) findViewById(R.id.preference_flow_tgbtn);
		lockTgbtn = (ToggleButton) findViewById(R.id.preference_lock_tgbtn);
		backBtn = (Button) findViewById(R.id.preference_back);

		preference = getSharedPreferences(IOTEKPROTECTOR, MODE_PRIVATE);
		editor = preference.edit();
		boolean interceptIsChecked = preference.getBoolean("state", false);
		boolean lockIsChecked = preference.getBoolean("lock", false);	
		boolean flowIsChecked = preference.getBoolean("flow", false);
		interceptTgbtn.setChecked(interceptIsChecked);
		lockTgbtn.setChecked(lockIsChecked);
		flowTgbtn.setChecked(flowIsChecked);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case PHONEDIALOG:
			builder.setIcon(R.drawable.icon_);
			builder.setTitle(PHONE_DIALOG);
			builder.setSingleChoiceItems(new String[] { "拦截电话", "静音", "和谐模式" },
					preference.getInt("phone", 1), new MyDialogListener());
			builder.setPositiveButton("确定", null);
			flag = 1;
			return builder.create();
		case MSGDIALOG:
			builder.setIcon(R.drawable.icon_);
			builder.setTitle(MSG_DIALOG);
			builder.setSingleChoiceItems(new String[] { "拦截短信", "静音", "和谐模式" },
					preference.getInt("msg", 1), new MyDialogListener());
			builder.setPositiveButton("确定", null);
			flag = 2;
			return builder.create();
		}
		return null;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.preference_state_tgbtn:
			if (isChecked) {
				editor.putBoolean("state", true);
			} else {
				editor.putBoolean("state", false);
			}
			editor.commit();
			break;
		case R.id.preference_lock_tgbtn:
			if (isChecked) {
				editor.putBoolean("lock", true);
			} else {
				editor.putBoolean("lock", false);
			}
			editor.commit();
			break;
		case R.id.preference_flow_tgbtn:
			if (isChecked) {
				editor.putBoolean("flow", true);
			} else {
				editor.putBoolean("flow", false);
			}
			editor.commit();
			break;
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.preference_phone_btn:
			showDialog(PHONEDIALOG);
			break;
		case R.id.preference_msg_btn:
			showDialog(MSGDIALOG);
			break;
		case R.id.preference_back:
			finish();
		}
	}

	class MyDialogListener implements
			android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (flag) {
			case 1:
				editor.putInt("phone", which);
				break;
			case 2:
				editor.putInt("msg", which);
				break;
			}
			editor.commit();
		}
	}
}
