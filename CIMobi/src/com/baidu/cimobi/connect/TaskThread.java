package com.baidu.cimobi.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.Exception;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;



/**
 * @author jiangshuguang
 * 多任务处理
 * 
 * 通过js发送 127.0.0.1:3232?progress=over 标识本测试结束
 *  
 */
public class TaskThread extends Thread{
	
	private BufferedReader input;
	private OutputStream output;
	private TextView textView;
	private String postData = "";
	private String getData = "";
	private String[] browsers;
	private String url;
	private boolean flag;
	private ServerSocket serverSocket;
	private Service service;
	private HashMap<String,String>browserMap;
	private int index;
	
	public TaskThread(String[] browsers,String url,Service service,HashMap<String,String>browserMap){
		this.browsers = browsers;
		this.url = url;
		this.service = service;
		this.browserMap = browserMap;
		this.index = 0;
		flag = true;
		
	}
	
	public void run(){
		try{
			serverSocket = new ServerSocket(2323);
			serverSocket.setReuseAddress(true);
			
			while(browsers.length>(index+1) && flag){
				Socket incoming = serverSocket.accept();
				input = new BufferedReader(new InputStreamReader(incoming.getInputStream(), "ISO-8859-2"));
				output = incoming.getOutputStream();
				String cmds = getStringFromInput(input);
				String[] cmdArr = cmds.split("&");
				String action = cmdArr[0].split("=")[1];
				
				openBrowser(browsers[index]);
				index++;
				
				send("{\"ok\":0}");
				output.close();
				incoming.close();
			}
	    }catch(SocketException e){
            run();
	    }catch(Exception e){
            run();
	    	e.printStackTrace();
	    }
	}
	
	
    /*
     * 启动浏览器
     * */
    private void openBrowser(String browser){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);   
        intent.setData(content_url);
        intent.setClassName(browser,browserMap.get(browser));
        service.startActivity(intent);
    }
	
	
	public void init(){
		destroy();
		flag = true;
	}
	
	
	public void destroy(){
	   try{
			if(null != serverSocket){
				serverSocket.close();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}

		this.interrupt();
		flag = false;
	}
	
	
	public String getStringFromInput(BufferedReader input) {
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
	

}
