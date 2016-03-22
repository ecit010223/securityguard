package com.mylover57.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;

import com.mylover57.R;

public class FloatService extends Service{
	
	// sharedpreference ��ȡ�ļ�ֵ
	private static final String IOTEKPROTECTOR = "Iotekprotector";
	private WindowManager wm = null;
	private ActivityManager am = null;
	private ActivityManager.MemoryInfo mi = null;
	private WindowManager.LayoutParams wmParams = null;
	private View view = null;
	private boolean quit = false;
	private DecimalFormat df = null;
	private float mTouchStartX = 0;
	private float mTouchStartY = 0;
	private float x = 0;
	private float y = 0;
	private TextView batteryTV = null;
	private SharedPreferences preference;
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		preference = getSharedPreferences(IOTEKPROTECTOR, MODE_PRIVATE);
		view = inflater.inflate(R.layout.floating, null);
		batteryTV = (TextView) (view.findViewById(R.id.battery_percent));
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		mi = new ActivityManager.MemoryInfo();
		df = new DecimalFormat("0.0");
		// ��ȡWindowManager
		wm = (WindowManager) getApplicationContext().getSystemService("window");
//		batteryReceiver = new BatteryReceiver();
//		registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		// ����������
		createView();
		super.onCreate();
		new Thread(new Runnable() {
			MyHandler myHandler = new MyHandler();
			
			@Override
			public void run() {
				while(!quit){
					Message msg = Message.obtain();
					myHandler.sendMessage(msg);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			am.getMemoryInfo(mi);
			batteryTV.setText(df.format(100-mi.availMem/10.24/getmem_TOLAL())+"%");
			if(preference.getBoolean("flow", false)){
				view.setVisibility(View.VISIBLE);
			}else{
				view.setVisibility(View.GONE);
			}
		}
	}
	// ������ڴ�
    public static long getmem_TOLAL() {
        long mTotal;
        // /proc/meminfo�������ں���Ϣ���н���
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        int begin = content.indexOf(':');
        // endIndex
        int end = content.indexOf('k');
        // ��ȡ�ַ�����Ϣ

	content = content.substring(begin + 1, end).trim();
        mTotal = Integer.parseInt(content);
        return mTotal;
    }
    
	/**
	 * ���ܣ�ʵ�����������ĳ�ʼ��������������Ϊ֮��������϶��ļ����¼�
	 */
	private void createView() {
		// ��ȡ������LayoutParams���������趨����������
		wmParams = new WindowManager.LayoutParams();
		// �������ṩ���û���������������Ӧ�ó����Ϸ���������״̬������
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		//��־�ࣺ�޽����־
		//���ô����Ժ󣬴��彫û�н��㣬����û����ͼ���󲿷ֵ��¼����ᱻ���ݵ�������������
		wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// �����������������Ͻ�
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		
		// ����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ
		wmParams.x = 0;
		wmParams.y = 0;
		// �����������ڳ�������
		wmParams.width = LayoutParams.WRAP_CONTENT;
		wmParams.height = LayoutParams.WRAP_CONTENT;
		// ȡ����Ե�ĺ�ɫ�����ߣ�ʹ�����������Ȼ�ô����ڴ���ǰ��
		wmParams.format = PixelFormat.RGBA_8888;
		// �ڴ�����ǰ�����������
		wm.addView(view, wmParams);

		// Ϊ������������ק�¼�
		view.setOnTouchListener(new OnTouchListener() {
			// ����괥�������������ô˷���
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// ��ȡ�����Ļ�����꣬������Ļ���Ͻ�Ϊԭ��
				x = event.getRawX();
				// 38��ϵͳ״̬���ĸ߶�
				y = event.getRawY() - 38;
				switch (event.getAction()) {
				/* ���������꣬��ȡ��ǰ���x��y���� */
				case MotionEvent.ACTION_DOWN:
					// ��ȡ���View�����꣬���Դ�View���Ͻ�Ϊԭ��
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					break;
				/* ����϶���� ����������������λ�� */
				case MotionEvent.ACTION_MOVE:
					updateViewPosition();
					break;
				/* ����ɿ���� ����������������λ�ò���ʼ���洢�϶�����ĺ���������� */
				case MotionEvent.ACTION_UP:
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				// ����true��ʾ�˴��϶��¼�����
				return true;
			}
		});
	}
	/**
	 * ���ܣ��������������λ�� 
	 * 		������.x => ��Ļ����-���������� 
	 * 		������.y => ��Ļ����(�Ѿ�����״̬���߶�)-����������
	 */
	private void updateViewPosition() {
		// ���¸�������λ�ò���
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(view, wmParams);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// �����ʱ�Ƴ�������
		wm.removeView(view);
		quit = true;
	}

}
