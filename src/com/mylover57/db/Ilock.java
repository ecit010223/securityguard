/**
 * ����������Ӧ�������ݿ����������
 */
package com.mylover57.db;

public class Ilock {

	public static final String LOCK_NAME = "locks";

	public static interface Columns {

		String id = "_id";
		String appName = "class";
		String checkIn = "unlocked";
		String className = "package";
	}

}
