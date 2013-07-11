package com.baidu.cimobi.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.IBinder;
import com.baidu.cimobi.data.Log;

import android.os.Binder;
import android.os.IBinder;
import com.baidu.cimobi.connect.ConnectHandler;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import java.io.PrintWriter;

import com.baidu.cimobi.mobileinfo.DeviceUuidFactory;
import com.baidu.cimobi.mobileinfo.MobileModel;
import com.baidu.cimobi.util.SystemTime;

/*
 * auth:jiangshuguang
 * 
 * 1.后台程序
 * 2.处理连接等问题 
 * 
 * */

public class MainService extends Service {
  
	private static final String TAG = "MainService"; 
	private PrintWriter out = null;
	private ConnectHandler connectHandler;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	public void onCreate(){
		super.onCreate();
		MobileModel mobileInstance = new MobileModel(this); 
//		Intent intent = new Intent("com.baidu.cimobi.service.broadcast");
//		
//		String time = SystemTime.getSystemTime();
//		intent.putExtra("log", time+"@*@ connect \n");
//		this.sendBroadcast(intent);
//		
//		Log.insert(this, time, "@*@ connect");
//		Log.insert(this,SystemTime.getSystemTime(),"Service启动成功");
		connectHandler = new ConnectHandler(this,mobileInstance);
		connectHandler.start();
		
	}
	
	

	
    /*
     * 接收 MainService的广播消息；
     * */
    private BroadcastReceiver receiverLog = new BroadcastReceiver(){
    	public void onReceive(Context context, Intent intent) {
    		String str = intent.getExtras().getString("info");
//    		Log.i(TAG, str); 
    	}
    }; 
	
    public void stopConnect(){
    	connectHandler.stop();
    }
    
    public void onStart(Intent intent, int startId) { 
//        Log.i(TAG, "onStart"); 
        super.onStart(intent, startId); 
    }
       
    public void onStop(){
//    	Log.insert(this,SystemTime.getSystemTime(),"onStop");
    }
    
    public void onDestroy(){
//    	Log.insert(this,SystemTime.getSystemTime(),"Service销毁成功");
    	if(null != connectHandler){
    		connectHandler.stop();
    	}
    	
    }
    
	public class MainServiceBinder extends Binder{
		public void doLog(String logContent){
//			Log.d(MainService.TAG, "invoke MyServiceBinder doLog:"+logContent);
		}
	}

}
