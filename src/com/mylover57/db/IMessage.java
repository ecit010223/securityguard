/**
 * �������������ص��Ķ������ݿ����������
 */
package com.mylover57.db;

public class IMessage {

	public static final String MSG_NAME = "messages";
	
	public static interface Columns{
		
		String id = "_id";
		String name = "name";
		String num = "phoneNum";
		String time = "time";
		String content = "content";
		
	}
}
