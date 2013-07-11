package com.baidu.cimobi.action;

import android.view.View;
import android.content.Intent;
import android.net.Uri;

public class BrowserAction extends Action {
	public void doAction(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");    
        Uri content_url = Uri.parse("http://www.baidu.com");   
        intent.setData(content_url);           
        intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");  
	}
}
