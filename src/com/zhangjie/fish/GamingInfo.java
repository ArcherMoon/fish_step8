package com.zhangjie.fish;

/* 此为单例模式 */
public class GamingInfo {
	/* 定义一个全局变量 */
	private static GamingInfo gamingInfo = null;
	/* 判断游戏是否正在运行 */
	private boolean isGaming = false;
	
	private GamingInfo() {
	}

	public static GamingInfo getGamingInfo() {
		if (null == gamingInfo) {
			gamingInfo = new GamingInfo();
		}
		return gamingInfo;
		
	}
	public boolean isGaming() {
		return isGaming;
	}

	public void setGaming(boolean isGaming) {
		this.isGaming = isGaming;
	}	
}
