/**
 * ÃèÊö£ººÚÃûµ¥Ïî
 */
package com.mylover57.entity;

public class BlockItem {

	private String id;
	private String phoneNum;
	private String kind;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	
	public BlockItem(String id, String phoneNum, String kind) {
		super();
		this.id = id;
		this.phoneNum = phoneNum;
		this.kind = kind;
	}
	public BlockItem() {
		super();
	}
	
}
