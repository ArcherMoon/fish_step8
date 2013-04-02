package com.zhangjie.fish;

import android.util.Log;

public class FishActThread extends Thread{
	private Fish fish = null;

	public FishActThread(Fish fish) {
		this.fish = fish;
	}

	@Override
	public void run() {		
		int index = 0;
		while (GamingInfo.getGamingInfo().isGaming()) {
			if (true == fish.isAlreadyHit ||
				true == fish.isOutScene) {
				break;
			}
			fish.setCurPicIndex(index);
			Log.d("FishActThread-->", "picIndex = " + index);
			index++;
			if (fish.getActs() == index) {
				index = 0;
			}
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
