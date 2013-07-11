package com.baidu.cimobi.activity;

import java.util.HashMap;

import com.baidu.cimobi.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import java.util.Iterator;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.net.InetAddress;
import java.util.ArrayList;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;

import com.baidu.cimobi.mobileinfo.MobileModel;
import com.baidu.cimobi.service.ServiceManage;
import com.baidu.cimobi.connect.ConnectStatus;


public class SysInfoActivity extends CommonActivity {
	private ImageView imageViewInfo;
	private TextView info_type;
	private TextView info_version;
	private TextView info_ip;
	private TextView info_id;
	private TextView info_browser;
	private TextView info_status;
	private Button disConnectBtn = null;

	
	public void init(){
		
		setContentView(R.layout.sysinfo);
		imageViewInfo = (ImageView)findViewById(R.id.menu_info_img);
		imageViewInfo.setImageResource(R.drawable.menu_info_pressed);
		
		info_type = (TextView)findViewById(R.id.info_type);
		info_version = (TextView)findViewById(R.id.info_version);
		info_ip = (TextView)findViewById(R.id.info_ip);
		info_id = (TextView)findViewById(R.id.info_id);
		info_browser = (TextView)findViewById(R.id.info_browser);
		info_status = (TextView)findViewById(R.id.info_status);
		disConnectBtn = (Button)findViewById(R.id.disConnect);
		disConnectBtn.setOnTouchListener(new disConnetEvent());
		
		MobileModel mobileModel = new MobileModel(this);
		ArrayList<String> browserList =  mobileModel.getBrowsers();
		String browsers="";
        for(int i=0;i<browserList.size();i++){
        	browsers+= "◆ " + browserList.get(i) + " 浏览器\n";
        } 
		info_type.setText(mobileModel.getMobileInfo().getMobileType());
		info_version.setText(mobileModel.getMobileInfo().getAndroidVersion());
		info_ip.setText(getIp());
		info_id.setText(mobileModel.getMobileInfo().getUid());
		info_browser.setText(browsers);
		info_status.setText(ConnectStatus.getConnectStatus());
		
		if(ConnectStatus.canDisconnet()){
			disConnectBtn.setEnabled(true);
		}else{
			disConnectBtn.setEnabled(false);
		}
		
	}
	
	
  
	/*
	 * 连接按钮断开事件
	 * */
	class disConnetEvent implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == event.ACTION_DOWN) {
				Intent mainService = ServiceManage.getMainService(); 
				if(mainService != null){
					stopService(mainService);
					disConnectBtn.setEnabled(false);
					displayToast("连接已断开");
				}	
			}
			return false;
		}
	}
	
	/*
	 * 3G环境获取IP
	 * */
	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); 
			en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                     return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (Exception ex) {
	       
	    }
	    return "网络未连接";
	}
	
	
	/*
	 * 禁用后退按钮
	 * */
    public boolean onKeyDown(int keyCode,KeyEvent event){
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		return false;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    
    
	
	/*
	 * wifi环境获取ip
	 * */
    private String getIp(){
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getSystemService(this.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);  
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();     
        int ipAddress = wifiInfo.getIpAddress(); 
        String ip = intToIp(ipAddress); 
        return ip;
    }
    
    private String intToIp(int i) {             
        return (i & 0xFF ) + "." +     
      ((i >> 8 ) & 0xFF) + "." +     
      ((i >> 16 ) & 0xFF) + "." +     
      ( i >> 24 & 0xFF) ;
   }
    
    //显示Toast函数
    private void displayToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    } 
}
