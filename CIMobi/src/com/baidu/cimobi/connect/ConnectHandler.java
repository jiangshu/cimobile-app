package com.baidu.cimobi.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.app.Activity;
import android.net.Uri;
import android.widget.TextView;

import com.baidu.cimobi.mobileinfo.MobileModel;
import com.baidu.cimobi.javabean.InfoType;
import com.baidu.cimobi.javabean.CommandExecute;
import com.baidu.cimobi.data.Config;
import com.baidu.cimobi.data.Log;
import com.baidu.cimobi.util.SystemTime;
import com.baidu.cimobi.data.SharedData;

/*
 * @auth:jiangshuguang
 * 1.建立socket连接；
 * 2.log信息；
 * 3.异常重连
 * 
 * @service :用于广播
 * @mobileInstance :mobile基本信息
 * */
public class ConnectHandler{
	private Socket clientSocket = null;
	private PrintWriter out;
	private ObjectOutputStream oOut;
	private ObjectInputStream oIn;
	private BufferedReader in;
	private OutputStream bOut;
	private ConnectThread connectThread ;
	private Service service;
	private Intent intent;
	private MobileModel mobileInstance;
	private InfoType infoType;
	private HashMap<String,String>browserMap;
	private String ip;
	private int port;
	private String alias;
	private int secCount; //10s 重连次数
	private int minCount; //1分钟重连次数
	private int tenMinCount; //10分钟重连次数 
	private String curRate; //当前连接频率
	private boolean isStop;
	private JsThread taskThread;
	String browserList[];
	String url;
	
	//------------- js交互--------------
	private ServerSocket serverSocket;
	private BufferedReader input;
	private OutputStream output;
	private String postData = "";
	private String getData = "";
	//------------- js交互--------------
	
	private ArrayList<String> testBrowsers;
	
	public ConnectHandler(Service service,MobileModel mobileInstance){
		this.mobileInstance = mobileInstance;
		this.service = service;
		this.browserMap = mobileInstance.getBrowserActivityMap();
		this.intent = new Intent("com.baidu.cimobi.service.broadcast");
		this.infoType = new InfoType();
		this.isStop = false;
		this.browserList  = new String[]{"native"};
		this.taskThread = null;
		url = "http://www.baidu.com";
		ArrayList<String> config = Config.getConfig(service);
		ip = config.get(0);
		port = Integer.parseInt(config.get(1));
		alias = config.get(2);
		initCount();
	}	
	
	/*
	 *  异步启动浏览器
	 * */
    class JsThread extends Thread{
    	String browserList[];
    	boolean jsFlag;
    	public JsThread(String browserList[]){
    		this.browserList = browserList;
    		this.jsFlag = true;
    	}
    	
    	public void run(){
            try{
                // 处理多任务的情况，通过js发送通知，告知本次测试任务完成，开启下一个测试任务
                serverSocket = null;
                if(browserList.length>1){
        			serverSocket = new ServerSocket(port);
        			serverSocket.setReuseAddress(true);
        			int index = 1;
        			while(jsFlag && index<browserList.length){
        				try{
            				Socket incoming = serverSocket.accept();
            				input = new BufferedReader(new InputStreamReader(incoming.getInputStream(), "ISO-8859-2"));
            				output = incoming.getOutputStream();
            				String cmds = getStringFromInput(input);
            				String[] cmdArr = cmds.split("&");
            				String cmdType = cmdArr[0].split("=")[1];
            				openBrowser(browserList[index],url);
            				printLog("命令：打开浏览器 &&浏览器："+browserList[index]+"浏览器&&url: "+url);
            				index++;
            				send("{\"ok\":0}");
            				output.close();
            				incoming.close();
        				}catch(Exception jse){
        					if(null != input){
        						input.close();
        					}
        					if(null != output){
        						output.close();
        					}
        					if(null != serverSocket){
        						serverSocket.close();
        					}
        				}
        			}
        			taskThread = null;
                }
    			if(null != input){
    				input.close();
    			}
    			if(null != output){
    				output.close();
    			}
    			if(null != serverSocket){
    				serverSocket.close();
    			}
            }catch(Exception e){
            	taskThread = null;
            	printLog("异步启动浏览器服务启动失败!");
            } 
    	}
    	
    	public void end(){
    		this.jsFlag = false;
    		this.interrupt();
    		try{
    			if(null != input){
    				input.close();
    			}
    			if(null != output){
    				output.close();
    			}
    			if(null != serverSocket){
    				serverSocket.close();
    			}
    		}catch(Exception e){
    			
    		}
    		taskThread = null;
    	}
    }	
	
	
	class ConnectThread extends Thread{		
			public void run(){					
				try{
					if(!isStop){
						clientSocket = new Socket();
						SocketAddress remoteAddr = new InetSocketAddress(ip,port);
						clientSocket.connect(remoteAddr,2000);
						printLog("服务器连接成功");
						ConnectStatus.setConnectStatus(0);          //设置连接状态为"已连接"
						initCount();
//						out = new PrintWriter(clientSocket.getOutputStream(),true);
						oOut = new ObjectOutputStream(clientSocket.getOutputStream());
//						in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						oIn = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));					
						infoType.setType("mobileInfo");
						oOut.writeObject(infoType);
						sleep(500);
						oOut.writeObject(mobileInstance.getMobileInfo());
						
						while(!isStop){						
//							clientSocket.sendUrgentData(0xFF); //判断连接是否断开	
							CommandExecute commandExecute = (CommandExecute)oIn.readObject();
							if(null != taskThread){
								taskThread.end();
								
							}
	                        String action = commandExecute.getAction();
	                        String url = commandExecute.getArgument().get("url");
	                        String browsers = commandExecute.getArgument().get("browsers");
	                        String sequence = commandExecute.getArgument().get("sequence");
	                        String browserList[] = browsers.split("&");
	                        if(browserList.length>0){
		                        openBrowser(browserList[0],url);
		                        printLog("命令：打开浏览器  &&浏览器："+browserList[0]+"&&url: "+url);
	                        }
	                        if(browserList.length>1){
	                        	if("aync".equals(sequence)){
	                        		taskThread = new JsThread(browserList);
	                        		taskThread.start();
	                        	}else{
	                        		for(int i=1;i<browserList.length;i++){
	                        			sleep(3000);
	    		                        openBrowser(browserList[i],url);
	    		                        printLog("命令：打开浏览器  &&浏览器："+browserList[i]+"&&url: "+url);
	                        		}
	                        	}
	                        }
					    }
					}

				 }catch(UnknownHostException e){
					 printLog("远程主机不存在!");
					 ConnectStatus.setConnectStatus(2);
					 reConnect();
				 }catch(ConnectException e){
					 printLog("连接错误[无网络？]");
					 ConnectStatus.setConnectStatus(2);
					 reConnect();
				 }catch(SocketTimeoutException e){
					 printLog("连接超时[远程主机不存在？[端口错误？]]");	
					 ConnectStatus.setConnectStatus(2);
					 reConnect();
				 }catch(Exception e) {
					 if(!isStop){
						 printLog("连接异常!");
						 ConnectStatus.setConnectStatus(2);
						 reConnect();
					 }
				 }
			}
			
			
			/*
			 * 终止线程
			 * */
			public void end(){
				if(null != taskThread){
					taskThread.end();
				}
				this.interrupt();
				try{
					if(null != oIn){
						oIn.close();
					}
					if(null != oOut){
						oOut.close();
					}
					if(null != clientSocket){
						clientSocket.close();
					}
				}catch(Exception e){
					
				}
				ConnectStatus.setConnectStatus(1);
				printLog("连接终止![同时关闭重新连接]");
			}
			
			
			/*
			 * 异常重连
			 * */
			private void reConnect(){
				if(!isStop){
					try{
						int time = getConnectRate();
						sleep(1000);
						printLog("正在以"+curRate+"的频率等待重连...");
						if(time>0){
							sleep(time);
							if(null != clientSocket){
								clientSocket.close();
							}
							if(!isStop){
								run();
							}else{
								ConnectStatus.setConnectStatus(1);
							}
						}else{
							ConnectStatus.setConnectStatus(1);
							if(service != null){
								
							}	
						}
					}catch(Exception e){
						//线程已销毁 sleep发生异常
//						ConnectStatus.setConnectStatus(2);
//						printLog("连接已断开  ********************");
					}
				}
			}
	}
	 
	
    /*
     * 启动连接线程
     * */
    public void start(){
    	connectThread = new ConnectThread();
    	printLog("正在连接服务器...");
    	connectThread.start();
    }
    
	/*
	 * 终止线程
	 * 对外接口 
	 * */
	public void stop(){
		isStop = true;
		if(null != connectThread){
			connectThread.end();
		}
	}
	
	/*
	 * 保证只有一个连接线程 
	 * */
	public Thread getConnectThread(){
		return connectThread;
	}
    
    /*
     * 启动浏览器
     * */
    private void openBrowser(String browser,String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);   
        intent.setData(content_url);
        intent.setClassName(browser,browserMap.get(browser));
        service.startActivity(intent);
          
    }
    
    
    
    /*
     * 关闭浏览器
     * */
    public static void killall(){
    	ArrayList<Activity> activityList = new ArrayList<Activity>();
//    	activityList.add("com.android.browser.BrowserActivity");
        int siz=activityList.size();
        for(int i=0;i<siz;i++){
            if(activityList.get(i)!=null){
                activityList.get(i).finish();
            }
        }
    }
        
    
    /*
     * 提供输入接口
     * */
	public PrintWriter getSocketOut(){
		return this.out;
	}
	
	public BufferedReader getSocketIn(){
		return this.in;
	}
	
	public Socket getSocket(){
		return this.clientSocket;
	}
	
	public ConnectThread getConnnet(){
		return this.connectThread;
	}
	
	//------------- js交互--------------
	private String getStringFromInput(BufferedReader input) {
        StringBuilder sb = new StringBuilder();
        String sTemp; 
        try {
			while ((sTemp = input.readLine())!=null&&(!sTemp.equals("")))  {
				sb.append(sTemp+"\n");
				//post最后一行没有换行符号，要用buffer获取。
				if(sTemp.toLowerCase().contains("get ")){
					//String[] tmp_arr = sTemp.split(" ");
					getData = sTemp.substring(sTemp.indexOf("?")+1, sTemp.lastIndexOf(" "));
					return getData;
				}
				if(sTemp.contains("Content-Length:")){
					int content_len = Integer.parseInt(sTemp.split(":")[1].trim());
					//偏移量
					if(content_len<=0){
						postData = "";
						break;
					}
					int offerset = 2;
					char[] tmp_char = new char[content_len+offerset];
					input.read(tmp_char, 0, content_len+offerset);
					postData = new String(tmp_char).trim();
					sb.append(postData+"\n");
					break;
				}
			}
		} catch (IOException e) {
			return "";
		}
        return sb.toString();
	}
	
	
	private void send(String s) {
	    String header=
	    		"HTTP/1.1 200 OK\n" +
	    		"Connection: close\n"+
	    		"Content-type: text/html; charset=utf-8\n"+
	    		"Content-Length: "+s.length()+"\n" +
	    		"\n";
	    try {
	    	output.write((header+s).getBytes());
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	}
	
	//------------- js交互--------------
	
	/*
	 * log信息处理
	 * 1.广播
	 * 2.永久保存
	 * */
	private void printLog(String log){
		if(tenMinCount<3){
			String time = SystemTime.getSystemTime();
			intent.putExtra("log", time+"@*@"+log);
			service.sendBroadcast(intent);
			Log.insert(service, time, log);
		}
	}
	
	/*
	 * 初始化重连计数
	 * */
	private void initCount(){
		secCount = 0;
		minCount = 0;
		tenMinCount = 0;
		curRate = "10秒";
	}
	
	/*
	 * 控制重连的频率 
	 * 1. 先以10s的频率重连2次
	 * 2. 再以1分钟频率重连3次
	 * 3. 然后以10分钟的频率重连
	 * 4. 如果两天内连接都失败，放弃重连，必须以手动的方式重连
	 * */
	private int getConnectRate(){
		if(secCount<2){
			secCount++;
			curRate = "10秒";
			return 10*1000;
		}else if(minCount<3){
			curRate = "1分钟";
			minCount++;
			return 60*1000;
		}else if(tenMinCount<288){
			curRate = "10分钟";
			tenMinCount++;
			return 10*60*1000;
		}else{
			return 0;
		}
	}	 
}




