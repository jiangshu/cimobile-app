package com.baidu.cimobi.data;

import android.content.Context;
import android.content.SharedPreferences;
/*
 * 使用SharedPreferences存储数据
 * 支持单一的 key => val
 * */
public class SharedData {
	
   public static boolean set(Context context,String key,String val){
	     SharedPreferences shp=context.getSharedPreferences("temp", Context.MODE_PRIVATE);
	     SharedPreferences.Editor editor = shp.edit();
	     editor.putString(key, val);
	     editor.commit();   	
	   return true;
   }
   
   
  public static String get(Context context,String key){	  
	  Context mOtherContex;
	  try {
	      mOtherContex = context.createPackageContext("com.baidu.cimobi", Context.CONTEXT_IGNORE_SECURITY);
		  SharedPreferences preferences = mOtherContex.getSharedPreferences("temp",Context.MODE_PRIVATE);
		  return preferences.getString(key, "未知");
	  } catch (Exception e) {
          
	  }

	  return "未知";
  }
   
   
}
