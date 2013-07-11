package com.baidu.cimobi.data;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintStream;
import android.content.Context;
import java.util.HashMap;
import java.util.ArrayList;
import android.os.Environment;
import com.baidu.cimobi.util.SystemTime;

/*
 * log–≈œ¢∂¡–¥
 * */
public class Log {
	public static Context me = null;
    public static void insert(Context context,String time,String msg){
    	me = context;
    	try{
    		FileOutputStream fileOutputStream=context.openFileOutput("log.txt",Context.MODE_APPEND);
    		PrintStream  printStream = new PrintStream(fileOutputStream);
    		printStream.println(time+"@*@"+msg);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }
    
    public static ArrayList<HashMap> get(Context context){	
    	ArrayList<HashMap> logs = new ArrayList<HashMap>();
    	try{
    		FileInputStream fileInputStream = context.openFileInput("log.txt");
    		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
    		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    		String logItem = "";
    		String[] logTep;
    		
    		while((logItem = bufferedReader.readLine())!=null){
    			HashMap<String,String>log = new HashMap<String,String>();
    			logTep  = logItem.split("@\\*@");
    			String time = "";
    			String content = "";
    			if(logTep.length == 2){
    				time = logTep[0];
    				content = logTep[1];
    				log.put("time",time);
    				log.put("content", content);
    				logs.add(log);
    			}
    		}
    	}catch(Exception e){
		    
		} 
    	return logs;
    }
    
    
    
    public static void clean(Context context){
    	if(me == null){
    		me = context;
    	}
    	try{
    		FileOutputStream fileOutputStream=me.openFileOutput("log.txt",Context.MODE_PRIVATE);
    		PrintStream  printStream = new PrintStream(fileOutputStream);
    		printStream.println("");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}
