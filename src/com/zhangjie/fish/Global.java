package com.zhangjie.fish;

import android.app.Application;

public class Global extends Application{
	private static Global instance;
	private int deviceHeight;
	private int deviceWidth;
	private int hitCount = 0;		/* 击中鱼的统计计数 */
	private int escapeCount = 0;	/* 逃出鱼的统计计数 */
	private int taskHitCount = 0;		/* 任务胜利击中数 */
	private int taskEscapeCount = 0;	/* 任务失败逃出数 */
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
