/**
 * 描述：初始化数据库五大表，还有数据库更新方法
 */
package com.mylover57.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mylover57.db.IBlock;
import com.mylover57.db.IMessage;
import com.mylover57.db.IPhone;
import com.mylover57.db.Ilock;

public class MyDBHelper extends SQLiteOpenHelper {

	//黑名单数据库表
	private static final String block_tbl_sql = "create table if not exists %s( %s integer primary key,%s text not null unique,%s text default 拦截一切)";
	//拦截掉的短信数据库表
	private static final String message_tbl_sql = "create table if not exists %s( %s integer primary key,%s text default null,%s integer not null,"
			+ "%s text not null,%s text)";
	//拦截掉的电话数据库表
	private static final String phone_tbl_sql = "create table if not exists %s( %s integer primary key,%s text default null,%s integer not null,"
			+ "%s text not null)";
	private static final String flow_tbl_sql = "create table if not exists %s( %s integer primary key,%s text not null,%s integer unique)";
	//应用锁数据库表
	private static final String lock_tbl_sql = "create table if not exists %s( %s integer primary key,%s text not null unique,%s integer default 0,%s text not null unique)";
	private static final String drop_tbl_sql = "drop table if exists %s";

	public MyDBHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(String.format(block_tbl_sql, IBlock.BLOCK_NAME,
				IBlock.Columns.id, IBlock.Columns.num, IBlock.Columns.kind));
		db.execSQL(String.format(message_tbl_sql, IMessage.MSG_NAME,
				IMessage.Columns.id, IMessage.Columns.name,
				IMessage.Columns.num, IMessage.Columns.time,
				IMessage.Columns.content));
		db.execSQL(String.format(phone_tbl_sql, IPhone.CALL_NAME,
				IPhone.Columns.id, IPhone.Columns.name, IPhone.Columns.num,
				IPhone.Columns.time));
		db.execSQL(String.format(lock_tbl_sql, Ilock.LOCK_NAME,
				Ilock.Columns.id, Ilock.Columns.appName, Ilock.Columns.checkIn,
				Ilock.Columns.className));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(String.format(drop_tbl_sql, IBlock.BLOCK_NAME));
		db.execSQL(String.format(drop_tbl_sql, IMessage.MSG_NAME));
		db.execSQL(String.format(drop_tbl_sql, IPhone.CALL_NAME));
		db.execSQL(String.format(drop_tbl_sql, Ilock.LOCK_NAME));
		onCreate(db);
	}

}
