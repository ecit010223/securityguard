package com.mylover57.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class FlowUpdateService extends Service {

	private static final String UPDATE = "UPDATE_FLOW";
	private MyBinder binder = new MyBinder();
	private boolean quit = false;

	class MyBinder extends Binder {
		// ������������������������ϰ�ߣ����ﲢδ������
		public FlowUpdateService getService() {
			return FlowUpdateService.this;
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "������ؿ���", Toast.LENGTH_LONG).show();
		final Intent updateIntent = new Intent(UPDATE);	
		
		new Thread() {
			@Override
			public void run() {
				while (!quit) {
					try {
						sendBroadcast(updateIntent);
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		quit = true;
		Toast.makeText(this, "������عر�", Toast.LENGTH_LONG).show();
		super.onDestroy();
	}
}

