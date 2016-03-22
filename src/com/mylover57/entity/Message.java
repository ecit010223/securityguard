/**
 * √Ë ˆ£∫∂Ã–≈œÓ
 */
package com.mylover57.entity;

public class Message {

	private String name;
	private String phoneNum;
	private String time;
	private String content;
	
	public Message(String name,String phoneNo, String time, String content) {
		super();
		this.name = name;
		this.phoneNum = phoneNo;
		this.time = time;
		this.content = content;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getPhoneNo() {
		return phoneNum;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNum = phoneNo;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
