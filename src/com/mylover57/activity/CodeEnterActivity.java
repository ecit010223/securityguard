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

	// ������ȡ��������е�sharedpreference
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

	// ��ʼ��������Զ�����ͼ�ؼ�
	private void init() {
		LinearLayout root = (LinearLayout) findViewById(R.id.code_line_view);
		NinePointLineView myView = new NinePointLineView(this);
		root.addView(myView);
	}

	/**
	 * �������Ź����ڲ��࣬�̳�View�������Զ�����ͼ�ؼ�
	 */
	public class NinePointLineView extends View {

		Paint whiteLinePaint = new Paint();
		Paint textPaint = new Paint();
		// ��������ͼƬ���������Σ����Ի�ȡһ�����Ⱦ�����
		Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.code_point_selected);
		int defaultBitmapRadius = defaultBitmap.getWidth() / 2;
		// ��ʼ����ѡ��ͼƬ��ֱ�����뾶
		Bitmap selectedBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.code_point);
		int selectedBitmapDiameter = selectedBitmap.getWidth();
		int selectedBitmapRadius = selectedBitmapDiameter / 2;
		// �����9���������
		PointInfo[] points = new PointInfo[9];
		// ��ӦACTION_DOWN���Ǹ���
		PointInfo startPoint = null;
		// ��Ļ�Ŀ��
		int width, height;
		// ��ACTION_MOVEʱ��ȡ��X��Y����
		int moveX, moveY;
		// �Ƿ���ACTION_UP
		boolean isUp = false;
		// �������ɵ��û�������
		StringBuffer lockString = new StringBuffer();

		/*
		 * �������ڲ��๹�캯�� ������context������
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
		 * ���ܣ���ʼ��������ͼ
		 */
		private void initPaint() {
			initTextPaint(textPaint);
			initWhiteLinePaint(whiteLinePaint);
		}

		/*
		 * ��ʼ���ı�����
		 */
		private void initTextPaint(Paint paint) {
			textPaint.setTextSize(30);
			textPaint.setAntiAlias(true); // �޾��ƽ����ʾ
			//��������
			//Typeface.MONOSPACE:�ȿ���������
			textPaint.setTypeface(Typeface.MONOSPACE);
		}

		/*
		 * ��ʼ�����߻���
		 */
		private void initWhiteLinePaint(Paint paint) {
			paint.setColor(getResources().getColor(R.color.point_line));
			paint.setStrokeWidth(defaultBitmap.getWidth() / 5 + 5);
			paint.setAntiAlias(true);
			//���ʱ�ˢ����
			//
			paint.setStrokeCap(Cap.ROUND);
		}

		/*
		 * ��������ʼ����Ļ��С ������widthMeasureSpec
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
		 * ���ܣ���ʼ���Ÿ���
		 */
		private void initPoints(PointInfo[] points) {
			int len = points.length;
			int seletedSpacing = (width - selectedBitmapDiameter * 3) / 4;
			// ��ѡ��ʱ��ʾͼƬ�����Ͻ�����
			int seletedX = seletedSpacing;
			int seletedY = height - width + seletedSpacing;
			// û��ѡʱͼƬ�����Ͻ�����
			int defaultX = seletedX + selectedBitmapRadius
					- defaultBitmapRadius;
			int defaultY = seletedY + selectedBitmapRadius
					- defaultBitmapRadius;
			// ���ƺ�9����
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
			canvas.drawText("����˳��" + lockString, 10, 50, textPaint);
			if (moveX != 0 && moveY != 0 && startX != 0 && startY != 0) {
				// ���Ƶ�ǰ����߶�
				drawLine(canvas, startX, startY, moveX, moveY);
			}
			drawNinePoint(canvas);
			super.onDraw(canvas);
		}

		/*
		 * ��������ɵĲ���
		 */
		@SuppressLint("ResourceAsColor")
		private void drawNinePoint(Canvas canvas) {
			if (startPoint != null) {
				drawEachLine(canvas, startPoint);
			}
			// ����ÿ�����ͼƬ
			for (PointInfo pointInfo : points) {
				if (pointInfo.isSelected()) {// ���ƴ�Ȧ
					canvas.drawBitmap(selectedBitmap, pointInfo.getSeletedX(),
							pointInfo.getSeletedY(), null);
				}
				// ���Ƶ�
				canvas.drawBitmap(defaultBitmap, pointInfo.getDefaultX(),
						pointInfo.getDefaultY(), null);
			}
		}

		/*
		 * �ݹ����ÿ������֮����߶�
		 * 
		 * @param canvas ����
		 * 
		 * @param point һ����
		 */
		private void drawEachLine(Canvas canvas, PointInfo point) {
			if (point.hasNextId()) {
				int n = point.getNextId();
				drawLine(canvas, point.getCenterX(), point.getCenterY(),
						points[n].getCenterX(), points[n].getCenterY());
				// �ݹ�
				drawEachLine(canvas, points[n]);
			}
		}

		/*
		 * ������DOWN��MOVE��UP�ǳɶԵģ����û��UP�ͷţ��Ͳ����ٻ��DOWN��
		 * ���DOWNʱ��һ��Ҫȷ�����Ѹ��¼�������MOVE��UP���ᱻ���View��onTouchEvent����
		 */
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			boolean flag = true;
			if (isUp) {
				// ����ѻ��꣬����ÿ��������Ժ�lockString
				finishDraw();
				// ��UP��Ҫ����false�����¼��ͷŸ�ϵͳ�������޷����Down�¼�
				flag = false;
			} else {// û���꣬���������
				handlingEvent(event);
				// ����Ҫ����true�������View���Ĵ��¼������򲻻��յ�MOVE��UP�¼�
				flag = true;
			}
			return flag;
		}

		/*
		 * ���ܣ�������������������޸� ��һ��Ϊ�����½����ȷ���ʶ���ı䣬�ڶ���Ϊ����������
		 */
		private void codeUpdate() {
			preference = context.getSharedPreferences(IOTEKPROTECTOR,
					MODE_PRIVATE);
			editor = preference.edit();

			if (!checkinOrchange) {
				if (lockString.toString().equals(
						preference.getString("code", "012"))) {
					Toast.makeText(context, "������ȷ,������������", Toast.LENGTH_SHORT)
							.show();
					checkinOrchange = true;
				} else {
					Toast.makeText(context, "�������", Toast.LENGTH_SHORT).show();
				}
			} else {
				if (lockString.toString().length() > 2) {
					editor.putString("code", lockString.toString());
					editor.commit();
					Toast.makeText(context, "�޸ĳɹ�", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(context, "���볤�ȱ�����ڵ���3λ", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}

		/*
		 * ���ܣ����� �������ƶ����뿪�Ͱ��¶���
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
		 * ���ܣ�UP���ٴδ�����ʼ����ͼ�����stringbuffers
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
		 * �����߶�
		 * 
		 * @param canvas ����
		 * 
		 * @param startX ��ʼX����
		 * 
		 * @param startY ��ʼY����
		 * 
		 * @param stopX ֹͣX����
		 * 
		 * @param stopY ֹͣY����
		 */
		private void drawLine(Canvas canvas, float startX, float startY,
				float stopX, float stopY) {
			canvas.drawLine(startX, startY, stopX, stopY, whiteLinePaint);
		}

		/**
		 * ������ʾһ����
		 */
		private class PointInfo {
			// һ�����ID
			private int id;
			// ��ǰ����ָ�����һ�����ID����û��ʱΪ�Լ�ID
			private int nextId;
			// �Ƿ�ѡ��
			private boolean selected;
			// Ĭ��ʱͼƬ�����Ͻ�X����
			private int defaultX;
			// Ĭ��ʱͼƬ�����Ͻ�Y����
			private int defaultY;
			// ��ѡ��ʱͼƬ�����Ͻ�X����
			private int seletedX;
			// ��ѡ��ʱͼƬ�����Ͻ�Y����
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
			 * ����(x,y)�Ƿ��ڵ�ǰ��ķ�Χ��
			 * 
			 * @param x����
			 * @param y����
			 * @return �ڷ�Χ�ڷ�true�����ڷ�false
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
