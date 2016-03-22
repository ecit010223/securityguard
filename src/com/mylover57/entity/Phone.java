/**
 * √Ë ˆ£∫µÁª∞œÓ
 */
package com.mylover57.entity;

public class Phone {

	private String name;
	private String phoneNo;
	private String time;
	
	
	public Phone() {
	}
	public Phone(String name, String phoneNo, String time) {
		super();
		this.name = name;
		this.phoneNo = phoneNo;
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
}
