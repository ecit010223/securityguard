/**
 * 描述：定义黑名单数据库表名和列名
 */
package com.mylover57.db;

public class IBlock{
	
	public static final String BLOCK_NAME = "blocks";
	
	public static interface Columns{
		
		String id = "_id";
		String num = "phoneNum";
		String kind = "kind";
	}

}
