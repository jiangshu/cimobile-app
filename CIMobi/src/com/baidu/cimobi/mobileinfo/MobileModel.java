package com.baidu.cimobi.mobileinfo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.baidu.cimobi.javabean.MobileInfo;
import java.util.HashMap;
import java.util.List;
import android.app.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
import com.baidu.cimobi.data.Config;
import java.util.ArrayList;


public class MobileModel {
  
   private MobileInfo MobileInstance;
   private Context service;
   private HashMap<String,String> browserAcitivityMap;
   private ArrayList<String>browsers;
   private ArrayList<String>browserPackages;
   
   public MobileModel(Context service){
	   this.MobileInstance  = new MobileInfo();
	   this.browsers = new ArrayList<String>();
	   this.browserAcitivityMap = new HashMap<String,String>();
	   this.browserPackages = new ArrayList<String>();
	   this.service = service;
	   getBrowserInfo(); //��ȡ�������Ϣ
	   
	   MobileInstance.setUid(getDeviceId());
	   MobileInstance.setMobileType(android.os.Build.MODEL);              //�ֻ���Ʒ�Ƽ��ͺ�
	   MobileInstance.setAndroidVersion(android.os.Build.VERSION.RELEASE);//ϵͳ�İ汾
	   MobileInstance.setAlias(Config.getConfig(service).get(2));
	   MobileInstance.setGroup("ge");     //todo ��ӿ�����
	   MobileInstance.setPackages(null);  //Ԥ��  ��������
	   MobileInstance.setBrowsers(browserPackages);
   }
   
   /*
    * moible��Ϣʵ��
    * */
   public MobileInfo getMobileInfo(){
	   return MobileInstance;
   }
   
   /*
    * ���������activityӳ��
    * */
   public HashMap<String,String>getBrowserActivityMap(){
	   return browserAcitivityMap;
   }
   
   /*
    * ��ȡ������б� ������/��ƣ�
    * */
   public ArrayList<String>getBrowsers(){
	   return browsers;
   }
   
   
	/*
	 * ��ȡ�ֻ����Ѱ�װ�ĳ���  
	 * Ԥ������������
	 * */
   private HashMap<String,String> getPackages(){
   	HashMap<String,String> packageList = new HashMap<String,String>();
   	List<PackageInfo> packages = service.getPackageManager().getInstalledPackages(0); 
   	String packageLable = "";
   	String packageName = "";
          
       for(int i=0;i<packages.size();i++) { 
	        PackageInfo packageInfo = packages.get(i);
	        packageLable = packageInfo.applicationInfo.loadLabel(service.getPackageManager()).toString(); //app����
	        packageName = packageInfo.packageName;                                                        //app���� 
	        packageList.put(packageLable, packageName);
       }
       return packageList;        
   }
   
   
   /*
    * �������Ϣ
    *    ���� = [���������activity]
    * 1. native=[com.android.browser, com.android.browser.BrowserActivity]
    * 2. uc=[com.UCMobile, com.UCMobile.main.UCMobile]
    * 3. QQ=[com.tencent.mtt, com.tencent.mtt.SplashActivity] 
    * 4. opera=[com.opera.mini.android, com.opera.mini.android.Browser]  
    * 5. chrome=[com.android.chrome, com.google.android.apps.chrome.Main] 
    * 6. firefox=[org.mozilla.firefox, org.mozilla.firefox.App]
    * 7. maxthon==[com.mx.browser, com.mx.browser.MxBrowserActivity]
    * 8. 360=[com.qihoo.browser, com.qihoo.browser.BrowserActivity]
    * 
    * ���ڴ�ͳ�Ʒ�Χ��
    *  ���� = [���������activity]
    * 
    * */
   private void getBrowserInfo(){      
       Intent intent = new Intent(Intent.ACTION_VIEW);    
       Uri content_url = Uri.parse("http://www.baidu.com");   
       intent.setData(content_url); 
       String browserName = "",
              packageName = "";
       PackageManager pm = service.getPackageManager(); 
       List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0); 
       if (null != activities) { 
           for (int i = 0; i < activities.size(); i++) { 
               ResolveInfo info = activities.get(i); 
               packageName = info.activityInfo.packageName;
               browserPackages.add(packageName);
               browserAcitivityMap.put(packageName, info.activityInfo.name);
               
               if(isFind("^.*opera.*$",packageName)){
            	   browserName = "opera";
               }else if(isFind("^.*tencent.*$",packageName)){
            	   browserName = "qq";
               }else if(isFind("^.*UC.*$",packageName)){
            	   browserName = "uc";
               }else if(isFind("^.*chrome.*$",packageName)){
            	   browserName = "chrome";
               }else if(isFind("^.*com\\.android\\.browser.*$",packageName)){
            	   browserName = "native";
               }else if(isFind("^.*firefox.*$",packageName)){
            	   browserName = "firefox";
               }else if(isFind("^.*mx.*$",packageName)){
            	   browserName = "maxthon";
               }else if(isFind("^.*qihoo.*$",packageName)){
            	   browserName = "360";
               }else{
            	   browserName = packageName;
               }               
               browsers.add(browserName); 
           } 
       }   	
   }
   
   
   /*
    * ��ȡ�ֻ���Ψһ��ʶ
    * */
   private String getDeviceId(){
       DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(service);
       String Uid = deviceUuidFactory.getDeviceUuid().toString();
//       Uid = Uid.substring(0, 13);
       return Uid.substring(0, 12);
   }
   
	private boolean isFind(String reg,String target){
		Pattern p = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
		Matcher m =  p.matcher(target);
		if(m.find()){
			return true;
		}
		return false;
	}
}

