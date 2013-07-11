package com.baidu.cimobi.activity;

import com.baidu.cimobi.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public abstract class CommonActivity extends Activity {
	private static final int LOG = 1;
	private static final int CONFIG = 2;
	private static final int INFO = 3;
	public Intent page = new Intent();
	
	/* �˵����¼����� */
	private ImageView imageViewLog = null ;
	private ImageView imageViewConfig = null ;
	private ImageView imageViewInfo = null ;
	
	private LinearLayout layoutLog = null;
	private LinearLayout layoutConfig = null; 
	private LinearLayout layoutInfo = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
		addMenuEvent();
	}
	
	public abstract void init(); //��ʼ������
	
	protected void addMenuEvent(){
		/*
		 * ���������¼�����imageView�ϻᵼ���л���������ͼƬ̫С��
		 * ���Խ��л����¼���������layout��
		 * */
		
		imageViewConfig = (ImageView)findViewById(R.id.menu_config_img);
		imageViewLog = (ImageView)findViewById(R.id.menu_log_img);
		imageViewInfo = (ImageView)findViewById(R.id.menu_info_img);	
		layoutConfig = (LinearLayout)findViewById(R.id.menu_config);
		layoutLog = (LinearLayout)findViewById(R.id.menu_log);
		layoutInfo = (LinearLayout)findViewById(R.id.menu_info);
		
		layoutConfig.setOnTouchListener(new LayoutConfigEvent());
		layoutLog.setOnTouchListener(new LayoutLogEvent());
		layoutInfo.setOnTouchListener(new LayoutInfoEvent());
		
//		imageViewLog.setOnTouchListener(new ImageViewLogEvent());
//		imageViewConfig.setOnTouchListener(new imageViewConfigEvent());
//		imageViewInfo.setOnTouchListener(new imageViewInfoEvent());
	}
	
	/*
	 * config
	 * */
	class LayoutConfigEvent implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == event.ACTION_DOWN) {
				startActivity(new Intent(getApplicationContext(),ConfigActivity.class));
//				imageViewConfig.setImageResource(R.drawable.menu_config_pressed);
			}
			return false;
		}
	}
	
	
	/*
	 * log
	 * */
	class LayoutLogEvent implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == event.ACTION_DOWN) {								
				startActivity(new Intent(getApplicationContext(),LogActivity.class));
//				imageViewConfig.setImageResource(R.drawable.menu_log_pressed);
			}
			return false;
		}
	}
	
	
	/*
	 * info
	 * */
	class LayoutInfoEvent implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == event.ACTION_DOWN) {
				startActivity(new Intent(getApplicationContext(),SysInfoActivity.class));
//				imageViewInfo.setImageResource(R.drawable.menu_info_pressed);
			}
			return false;
		}
	}
    
		
}
