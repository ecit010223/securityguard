package com.mylover57.entity;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylover57.R;

/**
 * �Զ��尴ť��ʵ��360�Ĺ��ܰ�ť
 */
public class MyButton extends LinearLayout {
	// ��ť�е�����
	TextView tv = null;
	// ��ť�е�ͼ��
	ImageView img = null;
	// ������ť�Ĳ���
	LinearLayout layout = null;

	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// ʹ�ò��ֹ���������ȡ�����Ĳ���ʵ��
		View view = LayoutInflater.from(context).inflate(
				R.layout.button, this);
		// ����xml�е���������img����ȡ��ť��ͼ���ͼƬ��Դ
		int imageResId = attrs.getAttributeResourceValue(null, "img",
				R.drawable.smsunread);
		// ����xml�е���������txtName����ȡ��ť������
		String name = attrs.getAttributeValue(null, "txtName");
		// ��ȡʵ��
		img = (ImageView) view.findViewById(R.id.img);
		img.setImageResource(imageResId);
		tv = (TextView) view.findViewById(R.id.tv_name);
		// ���óɰ�ɫ����
		tv.setTextColor(Color.WHITE);
		tv.setText(name);
	}

}
