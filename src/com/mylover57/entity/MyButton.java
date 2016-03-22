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
 * 自定义按钮，实现360的功能按钮
 */
public class MyButton extends LinearLayout {
	// 按钮中的文字
	TextView tv = null;
	// 按钮中的图标
	ImageView img = null;
	// 整个按钮的布局
	LinearLayout layout = null;

	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 使用布局管理器，获取关联的布局实例
		View view = LayoutInflater.from(context).inflate(
				R.layout.button, this);
		// 根据xml中的配置属性img，获取按钮中图标的图片资源
		int imageResId = attrs.getAttributeResourceValue(null, "img",
				R.drawable.smsunread);
		// 根据xml中的配置属性txtName，获取按钮中文字
		String name = attrs.getAttributeValue(null, "txtName");
		// 获取实例
		img = (ImageView) view.findViewById(R.id.img);
		img.setImageResource(imageResId);
		tv = (TextView) view.findViewById(R.id.tv_name);
		// 设置成白色文字
		tv.setTextColor(Color.WHITE);
		tv.setText(name);
	}

}
