package com.zhangjie.fish;

import java.io.IOException;

import android.graphics.Matrix;
import android.util.Log;

public class FishRunThread extends Thread{
	private Fish fish = null;
	private Fish peer = null;
	private Matrix tmpMatrix = null;
	private int movePath = 0;	/* 运动的方式，直线，曲线或随机 */
	private float curPosX = 0;	/* 运动的当前坐标 */
	private float curPosY = 0;
	private int speed = 0;		/* 运动速度 */
	private MoveCallBack moveCallBack = null;
	private MySurfaceView myView = null;
	Global global = null;			/* 指向保存全局变量的类 */
	private int hitCount = 0;		/* 击中鱼的统计计数 */
	private int escapeCount = 0;	/* 逃出鱼的统计计数 */
	private int taskHitCount = 0;		/* 任务胜利击中数 */
	private int taskEscapeCount = 0;	/* 任务失败逃出数 */
	
	public FishRunThread(Fish fish) {
		super();
		this.fish = fish;
		curPosX = fish.getCurPosX();
		curPosY = fish.getCurPosY();
	}
	
	/**
	 * 
	 * @param fish
	 * @param movePath	运动的路径
	 * @param peer		检查碰撞的对象
	 * @param myView
	 */
	public FishRunThread(Fish fish, int movePath, Fish peer, MySurfaceView myView) {
		super();
		this.fish = fish;
		global = Global.getInstance();
		taskHitCount = global.getTaskHitCount();
		taskEscapeCount = global.getTaskEscapeCount();
		Log.d("FishRunThread", fish.getToPosX() + "");
		/* 表明鱼的起始点，终点随机,待重构 */
		if (-1 == fish.getToPosX()) {
			int minY = 0;
			int maxY = global.getDeviceHeight() - fish.getPicHeight();
			int rangeY = maxY - minY;
			/* 随机数的范围是 < 1的，所以要先乘1000000 */
			curPosY = (int)(Math.random() * 1000000) % rangeY;
			fish.setToPosY((int)(Math.random() * 1000000) % rangeY);
			curPosX = global.getDeviceWidth();	
			fish.setToPosX(0);
		}
		else {
			curPosX = fish.getCurPosX();
			curPosY = fish.getCurPosY();
		}

		this.movePath = movePath;
		this.speed = fish.getSpeed() / 50;
		this.peer = peer;
		this.myView = myView;
	}

	/**
	 * 创建接口，实现运动回调函数
	 * @author zhangjie
	 *
	 */
	interface MoveCallBack {
		public void move();
	}
	
	/**
	 *  沿曲线运动，目前是正弦线,实现接口中的move方法，作为回调
	 */
	class MoveAlongCurve implements MoveCallBack
	{
		@Override
		public void move() {
			tmpMatrix = fish.getPicMatrix();
			/**
			 * 想让鱼做正弦运动，需要正弦曲线y=sinx，为了增加幅度乘以80，即屏幕
			 * 高度的1/4,为了降低震荡频率，x乘以1.57/120,即在屏幕宽度的1/4处达到
			 * 最大值，此时y=80*sin(0.0131x),由于屏幕以左上角为原点，为了让正弦
			 * 线在屏幕高度的一半开始，要变为y=160 - 80*sin(0.0131x)
			 * 注：以屏幕480*320为例
			 */
			curPosY = 160 - (float)(Math.sin(0.0131 * curPosX)*80);
			/* 先将原有矩阵清除，再做平移 */
			tmpMatrix.setTranslate(curPosX, curPosY);
			fish.setCurPosX((int)curPosX);
			fish.setCurPosY((int)curPosY);
			/**
			 * 要做旋转必须要知道正弦上某一点的倾斜角，通过对该点求导可以得到斜率，
			 * y'=-80*cos(0.0131x)*0.0131,
			 * 由斜率可以求得倾斜角度 tanθ= y',θ= arctan(y')=-cos(0.0131x)*80*0.0131
			 */
			/* 在平移的基础上做旋转 */
			tmpMatrix.postRotate(-(float)Math.toDegrees(Math.atan(Math.cos(0.0131 * curPosX)*80*0.0131)), curPosX, curPosY);
			curPosX--;		
		}	
	}
	
	/**
	 * 沿直线运动,实现接口中的move方法，作为回调
	 */
	class MoveAlongStraight implements MoveCallBack
	{
		private float a = 0;
		private float b = 0;
		private float extraDegree = 0;		/* 斜率为负时，旋转角需再加180度 */
		
		public MoveAlongStraight() {
			Log.d("MoveAlongStraight--->", "curPosY  " + curPosY + "  fish.getToPosY()  " + fish.getToPosY());
			/* 计算斜率，截距 */
			a = (fish.getToPosY() - curPosY) / (fish.getToPosX() - curPosX);
			b = fish.getToPosY() - a * fish.getToPosX();
			Log.d("MoveAlongStraight--->", "a=  " + a + "  b =  " + b);
			if (a < 0 && (true != fish.isFish())) {
				extraDegree = 180;
			}
		}

		@Override
		public void move() {
			tmpMatrix = fish.getPicMatrix();
			curPosY = a * curPosX + b;
			tmpMatrix.setTranslate(curPosX, curPosY);
			tmpMatrix.postRotate((float)Math.toDegrees(Math.atan(a)) + extraDegree, curPosX, curPosY);
			fish.setCurPosX((int)curPosX);
			fish.setCurPosY((int)curPosY);
			if (a < 0 && (true != fish.isFish())) {
				curPosX += speed;	
			} 
			else {
				curPosX -= speed;
			}		
		}
	}
	

	@Override
	public void run() {	
		switch (movePath) {
		case MySurfaceView.MOVE_STRAIGHT:
			moveCallBack = new MoveAlongStraight();
			break;
		case MySurfaceView.MOVE_CURVE:
			moveCallBack = new MoveAlongCurve();
			break;
		case MySurfaceView.MOVE_RANDOM:
			break;
		default:
			break;
		}
		
		while (GamingInfo.getGamingInfo().isGaming()) {	
			
			/* 碰撞检测，如果被撞，删除图片，退出线程 */
			if (fish.isAlreadyHit ||
				fish.isHitBy(myView.bulletTmp) ||
				fish.isOutScene() ||
				global.isYouLose() ||
				global.isYouWin()) {
				try {
					/* 子弹已经击中一条鱼了，不能再使用了 */
					myView.bulletTmp = null;
					myView.updatePicLayer(MySurfaceView.CHANGE_MODE_REMOVE, 
										  MySurfaceView.MIDDLE_LAYER, fish);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (global.isYouLose() || global.isYouWin()) {
					break;
				}
				
				/* 如果删除的是子弹则不需要新增加鱼 */
				if (false == fish.isFish()) {
					break;
				}
				
				/* 逃出鱼的统计计数增加 */
				if (fish.isOutScene()) {
					escapeCount = global.getEscapeCount() + 1;
					global.setEscapeCount(escapeCount);
					Log.d("FishRunThread-->", "已逃出" + escapeCount);
					if (escapeCount >= taskEscapeCount) {
						global.setYouLose(true);
						break;
					}
				}
				/* 击中鱼的统计计数增加 */
				else {
					hitCount = global.getHitCount() + 1;
					global.setHitCount(hitCount);
					Log.d("FishRunThread-->", "已击中" + hitCount);
					if (hitCount >= taskHitCount) {
						global.setYouWin(true);
						break;
					}
				}
				
				/* 删一个，加一个 */
				Fish tmpFish = new Fish(50);
				int i = (int)(Math.random() * 100) % 2 + 1;
				try {
					tmpFish.setActPics(myView.actPicsMap.get("fish"), myView.globalBitmap, "fish0" + i);
					myView.updatePicLayer(MySurfaceView.CHANGE_MODE_ADD, MySurfaceView.MIDDLE_LAYER, tmpFish);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FishRunThread tmpFishThread = new FishRunThread(tmpFish, i-1, myView.bulletTmp, myView);
				tmpFishThread.start();
				FishActThread tmpFishThread2 = new FishActThread(tmpFish);
				tmpFishThread2.start();
				break;
			}
			
			moveCallBack.move();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
