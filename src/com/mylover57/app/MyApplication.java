/**
 * 描述：重写应用，主要包含数据库操作，还有className与lockServiceIntent的中转
 */
package com.mylover57.app;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;

import com.mylover57.db.IBlock;
import com.mylover57.db.IMessage;
import com.mylover57.db.IPhone;
import com.mylover57.db.Ilock;
import com.mylover57.db.helper.MyDBHelper;

public class MyApplication extends Application {

	private MyDBHelper myHelper;
	private String[] msgColumns = new String[] { IMessage.Columns.id,
			IMessage.Columns.name, IMessage.Columns.num, IMessage.Columns.time,
			IMessage.Columns.content };
	private String[] callColumns = new String[] { IMessage.Columns.id,
			IMessage.Columns.name, IMessage.Columns.num, IMessage.Columns.time };
	private String packageName;

	@Override
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		myHelper = new MyDBHelper(this, "protector.db", 1);
	}

	// Lock表库操作封装方法
	public Cursor lockQueryByName(String className) {
		return myHelper.getReadableDatabase().query(
				Ilock.LOCK_NAME,
				new String[] { Ilock.Columns.id, Ilock.Columns.appName,
						Ilock.Columns.checkIn, Ilock.Columns.className },
				Ilock.Columns.appName + " =? ", new String[] { className },
				null, null, null);
	}

	public Cursor lockQueryAll() {
		return myHelper.getReadableDatabase().query(
				Ilock.LOCK_NAME,
				new String[] { Ilock.Columns.id, Ilock.Columns.appName,
						Ilock.Columns.checkIn, Ilock.Columns.className }, null,
				null, null, null, null);
	}

	public Cursor lockQueryByClass(String packageName) {
		return myHelper.getReadableDatabase().query(
				Ilock.LOCK_NAME,
				new String[] { Ilock.Columns.id, Ilock.Columns.appName,
						Ilock.Columns.checkIn, Ilock.Columns.className },
				Ilock.Columns.className + " =? ", new String[] { packageName },
				null, null, null);
	}

	// 通用库操作封装方法
	public long insert(String table, ContentValues values) {
		return myHelper.getWritableDatabase().insert(table, null, values);
	}

	public int delete(String table, String whereClause, String... whereArgs) {
		return myHelper.getWritableDatabase().delete(table, whereClause,
				whereArgs);
	}

	public int update(String table, ContentValues values, String whereClause,
			String... whereArgs) {
		return myHelper.getWritableDatabase().update(table, values,
				whereClause, whereArgs);
	}

	// Block表库操作封装方法
	public Cursor blockQueryByNum(String num) {
		return myHelper.getReadableDatabase().query(
				IBlock.BLOCK_NAME,
				new String[] { IBlock.Columns.id, IBlock.Columns.num,
						IBlock.Columns.kind }, IBlock.Columns.num + "=?",
				new String[] { num }, null, null, null);
	}

	public Cursor blockQueryAll() {
		return myHelper.getReadableDatabase().query(
				IBlock.BLOCK_NAME,
				new String[] { IBlock.Columns.id, IBlock.Columns.num,
						IBlock.Columns.kind }, null, null, null, null, null);
	}

	// Message表库操作封装方法
	public Cursor messageQueryByNum(String num) {
		return myHelper.getReadableDatabase().query(IMessage.MSG_NAME,
				msgColumns, IMessage.Columns.num + "=?", new String[] { num },
				null, null, null);
	}

	public Cursor messageQueryAll() {
		return myHelper.getReadableDatabase().query(IMessage.MSG_NAME,
				msgColumns, null, null, null, null, null);
	}

	// phoneCall表库操作封装方法
	public Cursor phoneQueryByNum(String num) {
		return myHelper.getReadableDatabase().query(IPhone.CALL_NAME,
				callColumns, IPhone.Columns.num + "=?", new String[] { num },
				null, null, null);
	}

	public Cursor phoneQueryAll() {
		return myHelper.getReadableDatabase().query(IPhone.CALL_NAME,
				callColumns, null, null, null, null, null);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		if (myHelper != null)
			myHelper.close();
	}
}
