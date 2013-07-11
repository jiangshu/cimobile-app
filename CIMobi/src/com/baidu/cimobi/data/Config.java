package com.baidu.cimobi.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import java.util.ArrayList;
import com.baidu.cimobi.activity.ConfigActivity;
import com.baidu.cimobi.R;
import com.baidu.cimobi.mobileinfo.DeviceUuidFactory;

public class Config {
   
	/*
     * 保存配置信息
     * 1.如果有sd卡，保存在sd中，永久存储
     * 2.如果没有sd卡，利用SharedPreferences 暂存配置信息 
     * */
	
    public static String setConfig(Context context,String ip, String port, String alias){
//	   	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){		
//	   		 String filePath = Environment.getExternalStorageDirectory().getPath()+"/cimobi/";
//		     if(!(new File(filePath)).exists()){
//	            new File(filePath).mkdirs();	          
//	         }
//		     Preferences preferences = Preferences.userNodeForPackage(ConfigActivity.class);	          
//		     preferences.put("ip", ip);  
//		     preferences.put("port", port);  
//		     preferences.put("alias", alias);
//	   	     try {  
//		         preferences.exportNode(new BufferedOutputStream(new FileOutputStream(filePath+"configure.xml"))); 
//		         return "配置永久保存";
//		     } catch (FileNotFoundException e) {  
//	
//		     } catch (IOException e) {  
//	 
//		     } catch (BackingStoreException e) {  
//		
//		     } 			
//		 }
	     SharedPreferences shp=context.getSharedPreferences("config", Context.MODE_WORLD_READABLE);
	     SharedPreferences.Editor editor = shp.edit();
	     editor.putString("ip", ip);
	     editor.putString("port", port);
	     editor.putString("alias", alias);
	     editor.commit();   	
	     return "配置保存在暂存区";
    }
    
    public static ArrayList<String>getConfig(Context context){
    	ArrayList<String> config = new ArrayList<String>();
    	String ip = null;
    	String port = null;
    	String alias = null;
//    	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//	   	     String filePath = Environment.getExternalStorageDirectory().getPath()+"/cimobi/";
//	   	     Preferences preferences = Preferences.userNodeForPackage(ConfigActivity.class);
//	   	     try {  
//	   	            Preferences.importPreferences(new BufferedInputStream(new FileInputStream(filePath+"configure.xml")));  
//	   	     } catch (FileNotFoundException e) {  
//
//	   	     } catch (IOException e) {  
//  
//	   	     } catch (InvalidPreferencesFormatException e) {  
//	   	 
//	   	     }  
//	   	     config.add(preferences.get("ip", null));
//	   	     config.add(preferences.get("port", null));
//	   	     config.add(preferences.get("alias", null));
//       }else{
   		    Context mOtherContex;
   		    try {
	   			mOtherContex = context.createPackageContext("com.baidu.cimobi", Context.CONTEXT_IGNORE_SECURITY);
	   			SharedPreferences preferences = mOtherContex.getSharedPreferences("config",Context.MODE_WORLD_READABLE);
	   			ip = preferences.getString("ip","");
	   			port = preferences.getString("port","");
	   			alias = preferences.getString("alias","");    		    	
   		    } catch (Exception e) {

   		    }
//   	    }
    	if(ip == null || "".equals(ip)){
    		ip  = context.getResources().getString(R.string.ip); 
    	}
    	if(port == null || "".equals(port)){
    		port  = context.getResources().getString(R.string.port); 
    	}
    	if(alias == null || "".equals(alias)){
    		DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(context);
    		alias  = android.os.Build.MODEL+"_"+deviceUuidFactory.getDeviceUuid().toString().substring(0, 13); 
    	}
  	    config.add(ip);
  	    config.add(port);
  	    config.add(alias);
    	return config;
    }
}
