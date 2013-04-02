package com.zhangjie.fish;

import android.app.Application;

public class Global extends Application{
	private static Global instance;
	private int deviceHeight;
	private int deviceWidth;
	
	/* ��������ʱֱ�ӻ�ȡʵ����������ʹ����Activity�Ҳ����View�Ҳ��ȡ��ȫ�ֱ��� */
	public static Global getInstance() {
		return instance;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		deviceWidth = 480;
		deviceHeight = 320;
	}
	public int getDeviceHeight() {
		return deviceHeight;
	}
	public void setDeviceHeight(int deviceHeight) {
		this.deviceHeight = deviceHeight;
	}
	public int getDeviceWidth() {
		return deviceWidth;
	}
	public void setDeviceWidth(int deviceWidth) {
		this.deviceWidth = deviceWidth;
	}
	
}
