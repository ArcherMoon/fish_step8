package com.zhangjie.fish;

import android.app.Application;

public class Global extends Application{
	private static Global instance;
	private int deviceHeight;
	private int deviceWidth;
	private int hitCount = 0;		/* �������ͳ�Ƽ��� */
	private int escapeCount = 0;	/* �ӳ����ͳ�Ƽ��� */
	private int taskHitCount = 0;		/* ����ʤ�������� */
	private int taskEscapeCount = 0;	/* ����ʧ���ӳ��� */
	private boolean youWin = false;
	private boolean youLose = false;
	
	public boolean isYouWin() {
		return youWin;
	}

	public void setYouWin(boolean youWin) {
		this.youWin = youWin;
	}

	public boolean isYouLose() {
		return youLose;
	}

	public void setYouLose(boolean youLose) {
		this.youLose = youLose;
	}

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
		taskHitCount = 3;
		taskEscapeCount = 3;
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

	public int getHitCount() {
		return hitCount;
	}

	public synchronized void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	public int getEscapeCount() {
		return escapeCount;
	}

	public synchronized void setEscapeCount(int escapeCount) {
		this.escapeCount = escapeCount;
	}

	public int getTaskHitCount() {
		return taskHitCount;
	}

	public int getTaskEscapeCount() {
		return taskEscapeCount;
	}
	
}
