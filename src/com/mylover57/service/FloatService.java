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
	
	// sharedpreference 获取的键值
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
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService("window");
//		batteryReceiver = new BatteryReceiver();
//		registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		// 创建悬浮窗
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
	// 获得总内存
    public static long getmem_TOLAL() {
        long mTotal;
        // /proc/meminfo读出的内核信息进行解释
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
        // 截取字符串信息

	content = content.substring(begin + 1, end).trim();
        mTotal = Integer.parseInt(content);
        return mTotal;
    }
    
	/**
	 * 功能：实现了悬浮窗的初始化创建工作，并为之配置鼠标拖动的监听事件
	 */
	private void createView() {
		// 获取悬浮窗LayoutParams对象，用于设定悬浮窗参数
		wmParams = new WindowManager.LayoutParams();
		// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		//标志类：无焦点标志
		//设置此属性后，窗体将没有焦点，窗体没有视图对象部分的事件将会被传递到窗体下面的组件
		wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// 调整悬浮窗口至左上角
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = 0;
		wmParams.y = 0;
		// 设置悬浮窗口长宽数据
		wmParams.width = LayoutParams.WRAP_CONTENT;
		wmParams.height = LayoutParams.WRAP_CONTENT;
		// 取消边缘的黑色轮廓线，使悬浮窗体更自然得存在于窗口前端
		wmParams.format = PixelFormat.RGBA_8888;
		// 在窗口最前端添加悬浮窗
		wm.addView(view, wmParams);

		// 为悬浮窗设置拖拽事件
		view.setOnTouchListener(new OnTouchListener() {
			// 当鼠标触碰悬浮窗则会调用此方法
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 获取相对屏幕的坐标，即以屏幕左上角为原点
				x = event.getRawX();
				// 38是系统状态栏的高度
				y = event.getRawY() - 38;
				switch (event.getAction()) {
				/* 如果按下鼠标，获取当前点的x，y坐标 */
				case MotionEvent.ACTION_DOWN:
					// 获取相对View的坐标，即以此View左上角为原点
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					break;
				/* 如果拖动鼠标 ，则更新悬浮窗体的位置 */
				case MotionEvent.ACTION_MOVE:
					updateViewPosition();
					break;
				/* 如果松开鼠标 ，则更新悬浮窗体的位置并初始化存储拖动距离的横向，纵向参数 */
				case MotionEvent.ACTION_UP:
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				// 返回true表示此次拖动事件结束
				return true;
			}
		});
	}
	/**
	 * 功能：更新悬浮窗体的位置 
	 * 		新坐标.x => 屏幕坐标-悬浮窗坐标 
	 * 		新坐标.y => 屏幕坐标(已经减掉状态栏高度)-悬浮窗坐标
	 */
	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(view, wmParams);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// 活动销毁时移除悬浮窗
		wm.removeView(view);
		quit = true;
	}

}
