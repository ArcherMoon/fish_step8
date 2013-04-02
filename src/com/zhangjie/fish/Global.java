package com.zhangjie.fish;

import android.app.Application;

public class Global extends Application{
	private static Global instance;
	private int deviceHeight;
	private int deviceWidth;
	
	/* 程序运行时直接获取实例，这样即使不在Activity里，也不在View里，也能取到全局变量 */
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
