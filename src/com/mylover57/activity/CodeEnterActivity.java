package com.mylover57.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mylover57.R;

public class CodeEnterActivity extends Activity {

	// 用来获取本程序独有的sharedpreference
	private static final String IOTEKPROTECTOR = "Iotekprotector";
	private Context context;
	private SharedPreferences preference;
	private Editor editor;
	private boolean checkinOrchange = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.code_main_layout);
		context = this;
		init();
	}

	// 初始化并添加自定义视图控件
	private void init() {
		LinearLayout root = (LinearLayout) findViewById(R.id.code_line_view);
		NinePointLineView myView = new NinePointLineView(this);
		root.addView(myView);
	}

	/**
	 * 描述：九宫格内部类，继承View，属于自定义视图控件
	 */
	public class NinePointLineView extends View {

		Paint whiteLinePaint = new Paint();
		Paint textPaint = new Paint();
		// 由于两个图片都是正方形，所以获取一个长度就行了
		Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.code_point_selected);
		int defaultBitmapRadius = defaultBitmap.getWidth() / 2;
		// 初始化被选中图片的直径、半径
		Bitmap selectedBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.code_point);
		int selectedBitmapDiameter = selectedBitmap.getWidth();
		int selectedBitmapRadius = selectedBitmapDiameter / 2;
		// 定义好9个点的数组
		PointInfo[] points = new PointInfo[9];
		// 相应ACTION_DOWN的那个点
		PointInfo startPoint = null;
		// 屏幕的宽高
		int width, height;
		// 当ACTION_MOVE时获取的X，Y坐标
		int moveX, moveY;
		// 是否发生ACTION_UP
		boolean isUp = false;
		// 最终生成的用户锁序列
		StringBuffer lockString = new StringBuffer();

		/*
		 * 描述：内部类构造函数 参数：context上下文
		 */
		public NinePointLineView(Context context) {
			super(context);
			setBackgroundResource(R.drawable.code_bg);
			initPaint();
		}

		public NinePointLineView(Context context, AttributeSet attrs) {
			super(context, attrs);
			setBackgroundResource(R.drawable.code_bg);
			initPaint();
		}

		/*
		 * 功能：初始化整个视图
		 */
		private void initPaint() {
			initTextPaint(textPaint);
			initWhiteLinePaint(whiteLinePaint);
		}

		/*
		 * 初始化文本画笔
		 */
		private void initTextPaint(Paint paint) {
			textPaint.setTextSize(30);
			textPaint.setAntiAlias(true); // 无锯齿平滑显示
			//设置字体
			//Typeface.MONOSPACE:等宽字体类型
			textPaint.setTypeface(Typeface.MONOSPACE);
		}

		/*
		 * 初始化白线画笔
		 */
		private void initWhiteLinePaint(Paint paint) {
			paint.setColor(getResources().getColor(R.color.point_line));
			paint.setStrokeWidth(defaultBitmap.getWidth() / 5 + 5);
			paint.setAntiAlias(true);
			//画笔笔刷类型
			//
			paint.setStrokeCap(Cap.ROUND);
		}

		/*
		 * 描述：初始化屏幕大小 参数：widthMeasureSpec
		 */
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			width = getWidth();
			height = getHeight();
			if (width != 0 && height != 0) {
				initPoints(points);
			}
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

		/*
		 * 功能：初始化九个点
		 */
		private void initPoints(PointInfo[] points) {
			int len = points.length;
			int seletedSpacing = (width - selectedBitmapDiameter * 3) / 4;
			// 被选择时显示图片的左上角坐标
			int seletedX = seletedSpacing;
			int seletedY = height - width + seletedSpacing;
			// 没被选时图片的左上角坐标
			int defaultX = seletedX + selectedBitmapRadius
					- defaultBitmapRadius;
			int defaultY = seletedY + selectedBitmapRadius
					- defaultBitmapRadius;
			// 绘制好9个点
			for (int i = 0; i < len; i++) {
				if (i == 3 || i == 6) {
					seletedX = seletedSpacing;
					seletedY += selectedBitmapDiameter + seletedSpacing;
					defaultX = seletedX + selectedBitmapRadius
							- defaultBitmapRadius;
					defaultY += selectedBitmapDiameter + seletedSpacing;
				}
				points[i] = new PointInfo(i, defaultX, defaultY, seletedX,
						seletedY);
				seletedX += selectedBitmapDiameter + seletedSpacing;
				defaultX += selectedBitmapDiameter + seletedSpacing;
			}
		}

		private int startX = 0, startY = 0;

		@Override
		protected void onDraw(Canvas canvas) {
			textPaint.setColor(Color.WHITE);
			canvas.drawText("滑动顺序：" + lockString, 10, 50, textPaint);
			if (moveX != 0 && moveY != 0 && startX != 0 && startY != 0) {
				// 绘制当前活动的线段
				drawLine(canvas, startX, startY, moveX, moveY);
			}
			drawNinePoint(canvas);
			super.onDraw(canvas);
		}

		/*
		 * 绘制已完成的部分
		 */
		@SuppressLint("ResourceAsColor")
		private void drawNinePoint(Canvas canvas) {
			if (startPoint != null) {
				drawEachLine(canvas, startPoint);
			}
			// 绘制每个点的图片
			for (PointInfo pointInfo : points) {
				if (pointInfo.isSelected()) {// 绘制大圈
					canvas.drawBitmap(selectedBitmap, pointInfo.getSeletedX(),
							pointInfo.getSeletedY(), null);
				}
				// 绘制点
				canvas.drawBitmap(defaultBitmap, pointInfo.getDefaultX(),
						pointInfo.getDefaultY(), null);
			}
		}

		/*
		 * 递归绘制每两个点之间的线段
		 * 
		 * @param canvas 画布
		 * 
		 * @param point 一个点
		 */
		private void drawEachLine(Canvas canvas, PointInfo point) {
			if (point.hasNextId()) {
				int n = point.getNextId();
				drawLine(canvas, point.getCenterX(), point.getCenterY(),
						points[n].getCenterX(), points[n].getCenterY());
				// 递归
				drawEachLine(canvas, points[n]);
			}
		}

		/*
		 * 描述：DOWN和MOVE、UP是成对的，如果没从UP释放，就不会再获得DOWN；
		 * 获得DOWN时，一定要确认消费该事件，否则MOVE和UP不会被这个View的onTouchEvent接收
		 */
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			boolean flag = true;
			if (isUp) {
				// 如果已滑完，重置每个点的属性和lockString
				finishDraw();
				// 当UP后，要返回false，把事件释放给系统，否则无法获得Down事件
				flag = false;
			} else {// 没滑完，则继续绘制
				handlingEvent(event);
				// 这里要返回true，代表该View消耗此事件，否则不会收到MOVE和UP事件
				flag = true;
			}
			return flag;
		}

		/*
		 * 功能：区分密码输入和密码修改 第一次为密码登陆，正确后标识符改变，第二次为新密码输入
		 */
		private void codeUpdate() {
			preference = context.getSharedPreferences(IOTEKPROTECTOR,
					MODE_PRIVATE);
			editor = preference.edit();

			if (!checkinOrchange) {
				if (lockString.toString().equals(
						preference.getString("code", "012"))) {
					Toast.makeText(context, "密码正确,请输入新密码", Toast.LENGTH_SHORT)
							.show();
					checkinOrchange = true;
				} else {
					Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show();
				}
			} else {
				if (lockString.toString().length() > 2) {
					editor.putString("code", lockString.toString());
					editor.commit();
					Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(context, "密码长度必须大于等于3位", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}

		/*
		 * 功能：处理 触屏的移动，离开和按下动作
		 */
		private void handlingEvent(MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				moveX = (int) event.getX();
				moveY = (int) event.getY();
				for (PointInfo temp : points) {
					if (temp.isInMyPlace(moveX, moveY) && temp.isNotSelected()) {
						temp.setSelected(true);
						startX = temp.getCenterX();
						startY = temp.getCenterY();
						int len = lockString.length();
						if (len != 0) {
							int preId = lockString.charAt(len - 1) - 48;
							points[preId].setNextId(temp.getId());
						}
						lockString.append(temp.getId());
						break;
					}
				}
				invalidate();
				break;
			case MotionEvent.ACTION_DOWN:
				int downX = (int) event.getX();
				int downY = (int) event.getY();
				for (PointInfo temp : points) {
					if (temp.isInMyPlace(downX, downY)) {
						temp.setSelected(true);
						startPoint = temp;
						startX = temp.getCenterX();
						startY = temp.getCenterY();
						lockString.append(temp.getId());
						break;
					}
				}
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				codeUpdate();
				startX = startY = moveX = moveY = 0;
				isUp = true;
				invalidate();
				break;
			default:
				break;
			}
		}

		/*
		 * 功能：UP后再次触摸初始化视图并清空stringbuffers
		 */
		private void finishDraw() {
			for (PointInfo temp : points) {
				temp.setSelected(false);
				temp.setNextId(temp.getId());
			}
			lockString.delete(0, lockString.length());
			isUp = false;
			invalidate();
		}

		/*
		 * 绘制线段
		 * 
		 * @param canvas 画布
		 * 
		 * @param startX 开始X坐标
		 * 
		 * @param startY 开始Y坐标
		 * 
		 * @param stopX 停止X坐标
		 * 
		 * @param stopY 停止Y坐标
		 */
		private void drawLine(Canvas canvas, float startX, float startY,
				float stopX, float stopY) {
			canvas.drawLine(startX, startY, stopX, stopY, whiteLinePaint);
		}

		/**
		 * 用来表示一个点
		 */
		private class PointInfo {
			// 一个点的ID
			private int id;
			// 当前点所指向的下一个点的ID，当没有时为自己ID
			private int nextId;
			// 是否被选中
			private boolean selected;
			// 默认时图片的左上角X坐标
			private int defaultX;
			// 默认时图片的左上角Y坐标
			private int defaultY;
			// 被选中时图片的左上角X坐标
			private int seletedX;
			// 被选中时图片的左上角Y坐标
			private int seletedY;

			public PointInfo(int id, int defaultX, int defaultY, int seletedX,
					int seletedY) {
				this.id = id;
				this.nextId = id;
				this.defaultX = defaultX;
				this.defaultY = defaultY;
				this.seletedX = seletedX;
				this.seletedY = seletedY;
			}

			public boolean isSelected() {
				return selected;
			}

			public boolean isNotSelected() {
				return !isSelected();
			}

			public void setSelected(boolean selected) {
				this.selected = selected;
			}

			public int getId() {
				return id;
			}

			public int getDefaultX() {
				return defaultX;
			}

			public int getDefaultY() {
				return defaultY;
			}

			public int getSeletedX() {
				return seletedX;
			}

			public int getSeletedY() {
				return seletedY;
			}

			public int getCenterX() {
				return seletedX + selectedBitmapRadius;
			}

			public int getCenterY() {
				return seletedY + selectedBitmapRadius;
			}

			public boolean hasNextId() {
				return nextId != id;
			}

			public int getNextId() {
				return nextId;
			}

			public void setNextId(int nextId) {
				this.nextId = nextId;
			}

			/**
			 * 坐标(x,y)是否在当前点的范围内
			 * 
			 * @param x坐标
			 * @param y坐标
			 * @return 在范围内返true，不在返false
			 */
			public boolean isInMyPlace(int x, int y) {
				boolean inX = x > seletedX
						&& x < (seletedX + selectedBitmapDiameter);
				boolean inY = y > seletedY
						&& y < (seletedY + selectedBitmapDiameter);
				return (inX && inY);
			}
		}

		public StringBuffer getLockString() {
			return lockString;
		}

		public void setLockString(StringBuffer lockString) {
			this.lockString = lockString;
		}
	}
}
