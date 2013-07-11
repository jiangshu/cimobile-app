package com.baidu.cimobi.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.cimobi.service.MainService;
import com.baidu.cimobi.util.MessageHandler;
import com.baidu.cimobi.data.Config;
import java.util.ArrayList;
import com.baidu.cimobi.R;
import android.widget.LinearLayout;
import com.baidu.cimobi.data.Log;
import java.util.HashMap;
import java.util.ArrayList;
import android.widget.Button;

public class LogActivity extends CommonActivity {
	private ImageView imageViewLog = null;
	private LinearLayout logContainer = null;
	private Button clrBtn;
	private Activity me;

	
	public void init(){
		setContentView(R.layout.log);
		logContainer = (LinearLayout)findViewById(R.id.log_container);
		
		imageViewLog = (ImageView)findViewById(R.id.menu_log_img);
		imageViewLog.setImageResource(R.drawable.menu_log_pressed);
	   
		me = this;
        clrBtn = (Button)findViewById(R.id.log_clean);
        clrBtn.setOnTouchListener(new CleanEvent());
        
        IntentFilter filter = new IntentFilter("com.baidu.cimobi.service.broadcast");
        registerReceiver(receiverLog,filter);
        
        ArrayList<HashMap> logs = Log.get(this);
        HashMap<String,String>logItem;
        for(int i=0; i<logs.size(); i++){
        	logItem = logs.get(i);
        	addLog(logItem.get("time"),logItem.get("content").replace("&&", "\n"));
        }
	}
	
	/*
	 * 注册连接按钮事件 
	 * */
	class CleanEvent implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == event.ACTION_DOWN) {
				Log.clean(me);
				logContainer.removeAllViews();
			}
			return false;
		}
	}
	
	
    /*
     * 接收 MainService的广播消息；
     * */
    private BroadcastReceiver receiverLog = new BroadcastReceiver(){
    	public void onReceive(Context context, Intent intent) {
    		String[] logArr = (intent.getExtras().getString("log")).split("@\\*@");
    		addLog(logArr[0],logArr[1].replace("&&", "\n"));
    	}
    }; 
    
	
	/*
	 * 禁用后退按钮
	 * */
    public boolean onKeyDown(int keyCode,KeyEvent event){
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		return false;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
	
	private void addLog(String time,String msg){
		LinearLayout layout = new LinearLayout(this);
		TextView timeView = new TextView(this);
		TextView logView = new TextView(this);
		
		layout.setOrientation(LinearLayout.VERTICAL);		
		timeView.setText("◇ "+time);
		timeView.setTextColor(getResources().getColor(R.color.time));
		timeView.setPadding(0,15,0,0);
		
		logView.setText(msg);
		logView.setTextColor(getResources().getColor(R.color.log));
		logView.setBackgroundDrawable(getResources().getDrawable(R.drawable.log_corner));
		logView.setPadding(5,5,5,5);
//		logView.setHeight(CONTEXT_RESTRICTED);
		
		layout.addView(timeView);
		layout.addView(logView);
		
		logContainer.addView(layout);
	}

}
