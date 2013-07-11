package com.baidu.cimobi.util;


import java.util.Date;
import java.text.SimpleDateFormat;

public class SystemTime {
   public static String getSystemTime(){
   	Date now = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(now);
   }
}

//import java.text.DateFormat;
//import java.util.Calendar;
//import java.util.Date;
//
///*
// * 获取系统时间
// * */
//public class SystemTime {
//    public static String getSystemTime(){
//    	Date now = new Date();
//    	Calendar cal = Calendar.getInstance();
////    	DateFormat d1 = DateFormat.getDateInstance();
//    	DateFormat d2 = DateFormat.getInstance();
//    	String str1 = d2.format(now);
//        return str1;
//    }
//}
