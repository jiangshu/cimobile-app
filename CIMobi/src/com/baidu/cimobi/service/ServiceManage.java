package com.baidu.cimobi.service;


import android.content.Intent;
/**
 * @author jiangshuguang
 *
 */
public class ServiceManage {
    private static Intent mainService;

	public static Intent getMainService() {
		return mainService;
	}

	public static void setMainService(Intent mainService) {
		ServiceManage.mainService = mainService;
	}    
}
