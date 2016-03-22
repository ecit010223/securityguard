/**
 * �������ṩ��̬�������ɹ���ʧ�ܵ���ʾ���������Ŀ�ʼ�ͽ���������Ԥ�ƵĿ�ʼ�ͽ���
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
		Toast toast = Toast.makeText(context, "Yeah�������ɹ���", Toast.LENGTH_LONG);	
		toast.setGravity(Gravity.CENTER, 0, 0);
		//��ȡToast��ʾ��ԭ�е�View
		View toastView = toast.getView();
		//����һ��ImageView
		image.setImageResource(R.drawable.ok);
		//����һ��LinearLayout����
		LinearLayout ll = new LinearLayout(context);
		ll.setGravity(Gravity.CENTER);
		//��LinearLayout�����ͼƬ��ԭ�е�View
		ll.addView(image);
		ll.addView(toastView);
		toast.setView(ll);
		toast.show();	
	}
	
	public static void failedToast(Context context){
		image = new ImageView(context);
		Toast toast = Toast.makeText(context, "����ʧ�ܣ�", Toast.LENGTH_SHORT);	
		toast.setGravity(Gravity.CENTER, 0, 0);
		//��ȡToast��ʾ��ԭ�е�View
		View toastView = toast.getView();
		//����һ��ImageView
		image.setImageResource(R.drawable.cry);
		//����һ��LinearLayout����
		LinearLayout ll = new LinearLayout(context);
		ll.setGravity(Gravity.CENTER);
		//��LinearLayout�����ͼƬ��ԭ�е�View
		ll.addView(image);
		ll.addView(toastView);
		toast.setView(ll);
		toast.show();	
	}

	public static void nearControl(Context context) {
		Notification notification = new Notification(R.drawable.over_flow, "�������ﾯ����",
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		/*Intent intent = new Intent(context, FlowMainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);*/
		notification.setLatestEventInfo(context, "����Ԥ��", "��ʹ�������Ѿ�����10%", null);
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(NOTIFY_NEAR_CONTROL, notification);
	}
	
	public static void cancelNearControl(Context context) {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(NOTIFY_NEAR_CONTROL);
	}
	public static void code_error_Toast(Context context) {
		image = new ImageView(context);
		Toast toast = Toast.makeText(context, "�������", Toast.LENGTH_SHORT);	
		toast.setGravity(Gravity.CENTER, 0, 0);
		//��ȡToast��ʾ��ԭ�е�View
		View toastView = toast.getView();
		//����һ��LinearLayout����
		LinearLayout ll = new LinearLayout(context);
		ll.setGravity(Gravity.CENTER);
		//��LinearLayout�����ͼƬ��ԭ�е�View
		ll.addView(image);
		ll.addView(toastView);
		toast.setView(ll);
		toast.show();	
	}
}

