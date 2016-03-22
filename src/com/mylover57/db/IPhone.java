/**
 * 描述：定义拦截掉的电话数据库表名和列名
 */
package com.mylover57.db;

public class IPhone {

	public static final String CALL_NAME = "calls";
	
	public static interface Columns{
		
		String id = "_id";
		String name = "name";
		String num = "phoneNum";
		String time = "time";
		
	}
	
}
