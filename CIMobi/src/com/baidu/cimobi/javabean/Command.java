package com.baidu.cimobi.javabean;

import java.util.ArrayList;
/*
 * ÃüÁîbean
 * */
public class Command implements java.io.Serializable{
    private String type;
    private ArrayList<String> argument;
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<String> getArgument() {
		return argument;
	}
	public void setArgument(ArrayList<String> argument) {
		this.argument = argument;
	}     
}
