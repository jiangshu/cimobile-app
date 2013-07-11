package com.baidu.cimobi.connect;

/*
 * 全局连接状态
 * */
public class ConnectStatus {
	/*
	 * @connectStatus
	 *  0:"已连接"
	 *  1:"未连接"
	 *  2:"超过重连次数"
	 *  3:"远程服务器不存在"
	 *  4:"连接的端口错误"
	 *  5:"连接超时"
	 *  6:"其它错误"
	 * */
    public static int connectStatus = 1;
	public static String getConnectStatus() {
//		if(connectStatus == 0){
//			return "已连接";
//		}else if(connectStatus == 1){
//			return "未连接";
//		}else if(connectStatus == 2){
//			return "超过重连次数";
//		}else if(connectStatus == 3){
//			return "远程主机不存在";
//		}else if(connectStatus == 4){
//			return "连接的端口错误";
//		}else if(connectStatus ==5){
//			return "连接超时";
//		}else{
//			return "未知错误";
//		}
		
		if(connectStatus == 0){
			return "已连接";
		}else if(connectStatus == 1){
			return "未连接";
		}else{
			return "连接异常";
		}
	}
	
	public static boolean isConnected(){
		if(connectStatus == 0){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean canDisconnet(){
		if(connectStatus!=1){
		   return true;	
		}
		return false;
	}

	public static void setConnectStatus(int connectStatus) {
		ConnectStatus.connectStatus = connectStatus;
	}
    
}
