/**
 * 描述：提供静态方法：成功和失败的提示；流量监测的开始和结束；流量预计的开始和结束
 */
package com.mylover57.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mylover57.R;

public class Utils {
	
	private static final int NOTIFY_NEAR_CONTROL = 0x0008;
	private static ImageView image;
	
	public static void successToast(Context context){
		image = new ImageView(context);
		Toast toast = Toast.makeText(context, "Yeah！操作成功！", Toast.LENGTH_LONG);	
		toast.setGravity(Gravity.CENTER, 0, 0);
		//获取Toast提示里原有的View
		View toastView = toast.getView();
		//创建一个ImageView
		image.setImageResource(R.drawable.ok);
		//创建一个LinearLayout容器
		LinearLayout ll = new LinearLayout(context);
		ll.setGravity(Gravity.CENTER);
		//向LinearLayout中添加图片、原有的View
		ll.addView(image);
		ll.addView(toastView);
		toast.setView(ll);
		toast.show();	
	}
	
	public static void failedToast(Context context){
		image = new ImageView(context);
		Toast toast = Toast.makeText(context, "操作失败！", Toast.LENGTH_SHORT);	
		toast.setGravity(Gravity.CENTER, 0, 0);
		//获取Toast提示里原有的View
		View toastView = toast.getView();
		//创建一个ImageView
		image.setImageResource(R.drawable.cry);
		//创建一个LinearLayout容器
		LinearLayout ll = new LinearLayout(context);
		ll.setGravity(Gravity.CENTER);
		//向LinearLayout中添加图片、原有的View
		ll.addView(image);
		ll.addView(toastView);
		toast.setView(ll);
		toast.show();	
	}

	public static void nearControl(Context context) {
		Notification notification = new Notification(R.drawable.over_flow, "流量到达警戒线",
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		/*Intent intent = new Intent(context, FlowMainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);*/
		notification.setLatestEventInfo(context, "流量预警", "可使用流量已经少于10%", null);
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(NOTIFY_NEAR_CONTROL, notification);
	}
	
	public static void cancelNearControl(Context context) {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(NOTIFY_NEAR_CONTROL);
	}
	public static void code_error_Toast(Context context) {
		image = new ImageView(context);
		Toast toast = Toast.makeText(context, "密码错误！", Toast.LENGTH_SHORT);	
		toast.setGravity(Gravity.CENTER, 0, 0);
		//获取Toast提示里原有的View
		View toastView = toast.getView();
		//创建一个LinearLayout容器
		LinearLayout ll = new LinearLayout(context);
		ll.setGravity(Gravity.CENTER);
		//向LinearLayout中添加图片、原有的View
		ll.addView(image);
		ll.addView(toastView);
		toast.setView(ll);
		toast.show();	
	}
}

