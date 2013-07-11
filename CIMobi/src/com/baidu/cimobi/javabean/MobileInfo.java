package com.baidu.cimobi.javabean;

import java.util.ArrayList;

/*
 * mobile������Ϣbean
 * */

public class MobileInfo implements java.io.Serializable{
    private String Uid;                 //�ֻ���Ψһ��ʶ  
	private String mobileType;          //�ֻ���Ʒ�Ƽ��ͺ�
	private String androidVersion;      //ϵͳ�İ汾
	private String alias;               //�ֻ�����
	private ArrayList<String> browsers; //������б�������
	private ArrayList<String> packages; //mobile���а�װ����(����)
	private String group;               //�����ķ���
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setAndroidVersion(String androidVersion) {
		this.androidVersion = androidVersion;
	}

	public ArrayList<String> getPackages() {
		return packages;
	}

	public void setPackages(ArrayList<String> packages) {
		this.packages = packages;
	}

	public ArrayList<String> getBrowsers() {
		return browsers;
	}

	public void setBrowsers(ArrayList<String> browsers) {
		this.browsers = browsers;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getMobileType() {
		return mobileType;
	}
	
	public void setMobileType(String mobileType) {
		this.mobileType = mobileType;
	}
	
	public String getAndroidVersion() {
		return androidVersion;
	}
	
	public String getUid() {
		return Uid;
	}

	public void setUid(String uid) {
		Uid = uid;
	}

}
