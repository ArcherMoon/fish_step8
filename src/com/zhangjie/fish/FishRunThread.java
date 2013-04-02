package com.zhangjie.fish;

import java.io.IOException;

import android.graphics.Matrix;
import android.util.Log;

public class FishRunThread extends Thread{
	private Fish fish = null;
	private Fish peer = null;
	private Matrix tmpMatrix = null;
	private int movePath = 0;	/* �˶��ķ�ʽ��ֱ�ߣ����߻���� */
	private float curPosX = 0;	/* �˶��ĵ�ǰ���� */
	private float curPosY = 0;
	private int speed = 0;		/* �˶��ٶ� */
	private MoveCallBack moveCallBack = null;
	private MySurfaceView myView = null;
	
	public FishRunThread(Fish fish) {
		super();
		this.fish = fish;
		curPosX = fish.getCurPosX();
		curPosY = fish.getCurPosY();
	}
	
	/**
	 * 
	 * @param fish
	 * @param movePath	�˶���·��
	 * @param peer		�����ײ�Ķ���
	 * @param myView
	 */
	public FishRunThread(Fish fish, int movePath, Fish peer, MySurfaceView myView) {
		super();
		this.fish = fish;
		Log.d("FishRunThread", fish.getToPosX() + "");
		/* ���������ʼ�㣬�յ����,���ع� */
		if (-1 == fish.getToPosX()) {
			int minY = 0;
			int maxY = myView.DEVICE_HEIGHT - fish.getPicHeight();
			int rangeY = maxY - minY;
			/* ������ķ�Χ�� < 1�ģ�����Ҫ�ȳ�1000000 */
			curPosY = (int)(Math.random() * 1000000) % rangeY;
			fish.setToPosY((int)(Math.random() * 1000000) % rangeY);
			curPosX = myView.DEVICE_WIDTH;	
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
	 * �����ӿڣ�ʵ���˶��ص�����
	 * @author zhangjie
	 *
	 */
	interface MoveCallBack {
		public void move();
	}
	
	/**
	 *  �������˶���Ŀǰ��������,ʵ�ֽӿ��е�move��������Ϊ�ص�
	 */
	class MoveAlongCurve implements MoveCallBack
	{
		@Override
		public void move() {
			tmpMatrix = fish.getPicMatrix();
			/**
			 * �������������˶�����Ҫ��������y=sinx��Ϊ�����ӷ��ȳ���80������Ļ
			 * �߶ȵ�1/4,Ϊ�˽�����Ƶ�ʣ�x����1.57/120,������Ļ��ȵ�1/4���ﵽ
			 * ���ֵ����ʱy=80*sin(0.0131x),������Ļ�����Ͻ�Ϊԭ�㣬Ϊ��������
			 * ������Ļ�߶ȵ�һ�뿪ʼ��Ҫ��Ϊy=160 - 80*sin(0.0131x)
			 * ע������Ļ480*320Ϊ��
			 */
			curPosY = 160 - (float)(Math.sin(0.0131 * curPosX)*80);
			/* �Ƚ�ԭ�о������������ƽ�� */
			tmpMatrix.setTranslate(curPosX, curPosY);
			fish.setCurPosX((int)curPosX);
			fish.setCurPosY((int)curPosY);
			/**
			 * Ҫ����ת����Ҫ֪��������ĳһ�����б�ǣ�ͨ���Ըõ��󵼿��Եõ�б�ʣ�
			 * y'=-80*cos(0.0131x)*0.0131,
			 * ��б�ʿ��������б�Ƕ� tan��= y',��= arctan(y')=-cos(0.0131x)*80*0.0131
			 */
			/* ��ƽ�ƵĻ���������ת */
			tmpMatrix.postRotate(-(float)Math.toDegrees(Math.atan(Math.cos(0.0131 * curPosX)*80*0.0131)), curPosX, curPosY);
			curPosX--;		
		}	
	}
	
	/**
	 * ��ֱ���˶�,ʵ�ֽӿ��е�move��������Ϊ�ص�
	 */
	class MoveAlongStraight implements MoveCallBack
	{
		private float a = 0;
		private float b = 0;
		private float extraDegree = 0;		/* б��Ϊ��ʱ����ת�����ټ�180�� */
		
		public MoveAlongStraight() {
			Log.d("MoveAlongStraight--->", "curPosY  " + curPosY + "  fish.getToPosY()  " + fish.getToPosY());
			/* ����б�ʣ��ؾ� */
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
			/* ��ײ��⣬�����ײ��ɾ��ͼƬ���˳��߳� */
			if (fish.isAlreadyHit ||
				fish.isHitBy(myView.bulletTmp) ||
				fish.isOutScene()) {
				try {
					/* �ӵ��Ѿ�����һ�����ˣ�������ʹ���� */
					myView.bulletTmp = null;
					myView.updatePicLayer(MySurfaceView.CHANGE_MODE_REMOVE, 
										  MySurfaceView.MIDDLE_LAYER, fish);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/* ���ɾ�������ӵ�����Ҫ�������� */
				if (false == fish.isFish()) {
					break;
				}
				
				/* ɾһ������һ�� */
				Fish tmpFish = new Fish(50);
				try {
					tmpFish.setActPics(myView.actPicsMap.get("fish"), myView.globalBitmap, "fish02");
					myView.updatePicLayer(MySurfaceView.CHANGE_MODE_ADD, MySurfaceView.MIDDLE_LAYER, tmpFish);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FishRunThread tmpFishThread = new FishRunThread(tmpFish, MySurfaceView.MOVE_STRAIGHT, myView.bulletTmp, myView);
				tmpFishThread.start();
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
