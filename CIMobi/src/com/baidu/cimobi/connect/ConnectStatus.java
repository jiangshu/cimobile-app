package com.baidu.cimobi.connect;

/*
 * ȫ������״̬
 * */
public class ConnectStatus {
	/*
	 * @connectStatus
	 *  0:"������"
	 *  1:"δ����"
	 *  2:"������������"
	 *  3:"Զ�̷�����������"
	 *  4:"���ӵĶ˿ڴ���"
	 *  5:"���ӳ�ʱ"
	 *  6:"��������"
	 * */
    public static int connectStatus = 1;
	public static String getConnectStatus() {
//		if(connectStatus == 0){
//			return "������";
//		}else if(connectStatus == 1){
//			return "δ����";
//		}else if(connectStatus == 2){
//			return "������������";
//		}else if(connectStatus == 3){
//			return "Զ������������";
//		}else if(connectStatus == 4){
//			return "���ӵĶ˿ڴ���";
//		}else if(connectStatus ==5){
//			return "���ӳ�ʱ";
//		}else{
//			return "δ֪����";
//		}
		
		if(connectStatus == 0){
			return "������";
		}else if(connectStatus == 1){
			return "δ����";
		}else{
			return "�����쳣";
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
