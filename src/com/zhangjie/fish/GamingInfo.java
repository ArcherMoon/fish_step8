package com.zhangjie.fish;

/* ��Ϊ����ģʽ */
public class GamingInfo {
	/* ����һ��ȫ�ֱ��� */
	private static GamingInfo gamingInfo = null;
	/* �ж���Ϸ�Ƿ��������� */
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
