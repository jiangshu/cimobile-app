package com.baidu.cimobi.util;

import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.content.Context;
import android.widget.TextView;
import com.baidu.cimobi.R;
import com.baidu.cimobi.activity.LogActivity;
/*
 * 消息面板处理
 * */
public class MessageHandler {

	private String msgContent;
	private EditText container;
	private Context context;
	private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
        	container.append((msg.obj).toString()+"\n");
        }
    };
    
	public MessageHandler(EditText container){
        this.container = container;	
//        TextView logView = (TextView)findViewById(R.id.main_log);
	}
	
    public void addMessage(String str){
		Message msg = new Message();
        msg.obj = str;
        mHandler.sendMessage(msg);
    }
    
    
//    //消息处理
//    mHandler = new Handler(){
//        public void handleMessage(Message msg){
//        	serverInfo.append((msg.obj).toString()+"\n");
////	        Intent intent = new Intent();
////	        intent.setAction("android.intent.action.VIEW");    
////	        Uri content_url = Uri.parse("http://www.baidu.com");   
////	        intent.setData(content_url);           
////	        intent.setClassName("com.android.browser","com.android.browser.BrowserActivity"); 
////	        startActivity(intent);
////            displayToast("启动浏览器");
//        }
//    };
}
