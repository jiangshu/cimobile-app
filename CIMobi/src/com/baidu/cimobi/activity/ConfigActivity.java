package com.baidu.cimobi.activity;

import com.baidu.cimobi.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;
import com.baidu.cimobi.data.Config;
import java.util.ArrayList;
import com.baidu.cimobi.data.SharedData;
import com.baidu.cimobi.data.Log;
import com.baidu.cimobi.mobileinfo.MobileModel;
import com.baidu.cimobi.service.MainService;
import com.baidu.cimobi.service.MainService.MainServiceBinder;
import com.baidu.cimobi.util.SystemTime;
import com.baidu.cimobi.connect.ConnectStatus;
import com.baidu.cimobi.connect.ConnectHandler;
import com.baidu.cimobi.service.ServiceManage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigActivity extends CommonActivity {
	private Button saveBtn = null;
	private Button connectBtn = null;
	private Button disconnectBtn = null;
	private EditText ipText = null;
	private EditText portText = null;
	private EditText aliasText = null;
	private String ip;
	private String port;
	private String alias;
	private ImageView imageViewConfig;	
	private Activity me;
	private boolean isConfigChange;
	
	/*
	 * 初始化
	 * */
	public void init(){
		me = this;
		setContentView(R.layout.config);
		
		imageViewConfig = (ImageView)findViewById(R.id.menu_config_img);
		imageViewConfig.setImageResource(R.drawable.menu_config_pressed);
		
		saveBtn = (Button)findViewById(R.id.set_save);
		connectBtn = (Button)findViewById(R.id.set_connect);
		disconnectBtn = (Button)findViewById(R.id.set_disconnect);
		ipText = (EditText)findViewById(R.id.set_ip);
		portText = (EditText)findViewById(R.id.set_port);
		aliasText = (EditText)findViewById(R.id.set_alias);
		isConfigChange = false;
		
		ArrayList<String> config = Config.getConfig(me);
		ipText.setText(config.get(0));
		portText.setText(config.get(1));
		aliasText.setText(config.get(2));
		
		ipText.addTextChangedListener(new savaStatusChangeEvent());
		portText.addTextChangedListener(new savaStatusChangeEvent());
		aliasText.addTextChangedListener(new savaStatusChangeEvent());
		
		saveBtn.setOnTouchListener(new SaveEvent());
		connectBtn.setOnTouchListener(new ConncetEvent());
		disconnectBtn.setOnTouchListener(new disConnetEvent());
				
		if(ConnectStatus.isConnected()){
			connectBtn.setEnabled(false);
		}
		
		if(ConnectStatus.canDisconnet()){
			disconnectBtn.setEnabled(true);
		}else{
			disconnectBtn.setEnabled(false);
		}
		
		saveBtn.setEnabled(false);
		showNotification();
	}
	
	/*
	 * 断开事件
	 * */
	class disConnetEvent implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == event.ACTION_DOWN) {
				Intent mainService = ServiceManage.getMainService(); 
				if(mainService != null){
					stopService(mainService);
					disconnectBtn.setEnabled(false);
					connectBtn.setEnabled(true);
					displayToast("断开连接");
				}	
			}
			return false;
		}
	}
    
    
    /*
     * 保存按钮状态改变的事件
     * */
	class savaStatusChangeEvent implements TextWatcher{
		   public void afterTextChanged(Editable s) {
				if(isConfigChange()){
				     saveBtn.setEnabled(true);
				}else{
					saveBtn.setEnabled(false);
				}
		   }
		   public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		   }
		   public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		   }
	}

		
	/*
	 * 保存按钮事件
	 * */
	class SaveEvent implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == event.ACTION_DOWN) {
				if(savaConfig()){
					saveBtn.setEnabled(false);
				}
			}
			return false;
		}
	}
	
	/*
	 * 保存配置
	 * */
	private boolean savaConfig(){
		String ip = ipText.getText().toString();
		String port = portText.getText().toString();
		String alias = aliasText.getText().toString();
		String ip_reg = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."+
		                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."+
				        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."+
		                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";		
		String port_reg = "^[1-9]+[0-9]{0,5}$";
		String alias_reg = "^(\\d|_|[a-z]|[A-Z])+$";
		if(ip.equals("") || !match(ip,ip_reg)){
			displayToast("ip输入错误");
			return false;
		}else if(port.equals("") || !match(port,port_reg)){
			displayToast("port输入错误");
			return false;
		}else if(alias.equals("") || !match(alias,alias_reg)){
			displayToast("别名只能包含数字下划线和字母");
			return false;
		}else{
			Config.setConfig(me, ip, port, alias);
			displayToast("保存成功");
			Log.insert(me,SystemTime.getSystemTime(),"配置成功");
			return true;
		}
	}
	
	/*
	 * 判断配置文件是否发生了改变
	 * */
	private boolean isConfigChange(){
		String ip = ipText.getText().toString();
		String port = portText.getText().toString();
		String alias = aliasText.getText().toString();
		
	    ArrayList<String> config  = Config.getConfig(me);
		String ip_cofig = config.get(0);
		String port_config = config.get(1);
		String alias_config = config.get(2);
		if(ip.equals(ip_cofig) && port.equals(port_config) && alias.equals(alias_config)){
			return false;
		}
		return true;
	}
	
	
	/*
	 * 注册连接按钮事件 
	 * */
	class ConncetEvent implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == event.ACTION_DOWN) {
                if(isConfigChange()){
    				if(savaConfig()){
    					saveBtn.setEnabled(false);
    				}else{
    					return false;
    				}
                }
                
				Intent mainService1 = ServiceManage.getMainService(); 
				if(mainService1 != null){
					stopService(mainService1);
					disconnectBtn.setEnabled(false);
				}	
                
//				bindService(new Intent(me,MainService.class),mainSrvConn,BIND_AUTO_CREATE);
//				startActivity(new Intent(getApplicationContext(),LogActivity.class));
				/*
				 * 采用这种非关联的方式启动service，方便关闭service
				 * */				
			    Intent mainService = new Intent(me,MainService.class);
			   	ServiceManage.setMainService(mainService);
		    	startService(mainService);
		    	startActivity(new Intent(getApplicationContext(),LogActivity.class));
			}
			return false;
		}
	}
	
	
	
    /*
     * ServiceConnection 方式处理 service；
     * 通过这种绑定的方式启动service，貌似不满足需求 
     * */
    ServiceConnection mainSrvConn = new ServiceConnection(){   	
    	public void onServiceConnected(ComponentName name, IBinder service) {
			MainServiceBinder binder = (MainServiceBinder) service;
			binder.doLog("Demo_ServiceActivity_CONTENT");
		}
    	public void onServiceDisconnected(ComponentName name) {
    		
		}		
	};
	
	/*
	 * 通知处理方法
	 * */
    private void showNotification(){
    	
    	NotificationManager notificationManager = (NotificationManager)this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    	Notification notification = new Notification(R.drawable.ic_launcher,"CiMObile",System.currentTimeMillis());
    	
    	notification.flags |= Notification.FLAG_ONGOING_EVENT;
    	notification.flags |= Notification.FLAG_NO_CLEAR;
    	notification.flags |= Notification.FLAG_SHOW_LIGHTS;
    	notification.ledARGB = Color.BLUE; 
    	notification.ledOnMS =5000; 
    	
    	CharSequence contentTitle ="CiMObile";        //通知栏标题
    	CharSequence contentText ="进入CiMObile主界面";//通知栏内容
    	
    	Intent notificationIntent =new Intent(ConfigActivity.this, ConfigActivity.class);  //点击该activity后跳转到的activity
    	PendingIntent contentItent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    	notification.setLatestEventInfo(this, contentTitle, contentText, contentItent);   	
    	notificationManager.notify(0, notification); 
    	
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
    
	
    //显示Toast函数
    private void displayToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }  
    
    
    //配置正则
	public  boolean match(String ora,String regex)
	{
		Pattern p=Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m=p.matcher(ora);
	
		if(m.find()){
			return true;
		}	
		else{
			return false;
		}
	}	
}