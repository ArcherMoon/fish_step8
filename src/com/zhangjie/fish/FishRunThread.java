package com.zhangjie.fish;

import java.io.IOException;

import android.R.bool;
import android.R.xml;
import android.graphics.Matrix;
import android.os.IInterface;
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
	Global global = null;			/* ָ�򱣴�ȫ�ֱ������� */
	private int hitCount = 0;		/* �������ͳ�Ƽ��� */
	private int escapeCount = 0;	/* �ӳ����ͳ�Ƽ��� */
	private int taskHitCount = 0;		/* ����ʤ�������� */
	private int taskEscapeCount = 0;	/* ����ʧ���ӳ��� */
	
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
		global = Global.getInstance();
		taskHitCount = global.getTaskHitCount();
		taskEscapeCount = global.getTaskEscapeCount();
		Log.d("FishRunThread", fish.getToPosX() + "");
		/* ���������ʼ�㣬�յ����,���ع� */
		if (-1 == fish.getToPosX()) {
			int minY = 0;
			int maxY = global.getDeviceHeight() - fish.getPicHeight();
			int rangeY = maxY - minY;
			/* ������ķ�Χ�� < 1�ģ�����Ҫ�ȳ�1000000 */
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
		/* ����˶��������� y= a - b*sin(c*x + d) */
		private float a = 0;
		private float b = 1;
		private float c = 1;
		private float d = 0;

		public MoveAlongCurve() {
			/* ��������� */
			int random = (int)(Math.random() * 1000000);
			/* a��Ϊ��Ļ�߶ȵ�1/3 ~ 2/3,ע�⿼���ڲ���ʱ��ĸ߶ȣ���ҪԽ�� */
			int h1 = myView.deviceHeight / 3;
			a = h1 + random % (h1 - fish.getPicHeight());
			
			/* ���b��Ϊ��Ļ�߶ȵ�1/6 ~ 1/3 */
			int h2 = myView.deviceHeight / 6;
			b = h2 + random % h2;
			
			/* c��ȡֵʹ��������Ļ��ȵ�1/4 ~ 1/2�ﵽ��� */
			int w1 = myView.deviceWidth / 4;
			int temp = w1 + random % w1;
			c = 1.57f / temp;
			
			/* d��ȡֵΪ0 ~ 3.14 */
			d = random % 3;
		}

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
			curPosY = a - (float)(b * Math.sin(c * curPosX + d));
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
			tmpMatrix.postRotate(-(float)Math.toDegrees(Math.atan(Math.cos(c * curPosX + d)*b*c)), curPosX, curPosY);
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
		private float startPosX = 0;	/* ��ʼ������ */
		private float startPosY = 0;
		
		public MoveAlongStraight() {
			Log.d("MoveAlongStraight--->", "curPosY = " + curPosY + "�� fish.getToPosY()  " + fish.getToPosY());
			startPosX = curPosX;
			startPosY = curPosY;
			/* ����б�ʣ��ؾ� */
			a = (fish.getToPosY() - curPosY) / (fish.getToPosX() - curPosX);
			b = fish.getToPosY() - a * fish.getToPosX();
			Log.d("MoveAlongStraight--->", "a=  " + a + "  b =  " + b);
			if (fish.getToPosX() > startPosX) {
				extraDegree = 180;
			}
		}
		
		@Override
		public void move() {
			tmpMatrix = fish.getPicMatrix();
			if (Math.abs(a) <= 1) {
				curPosY = a * curPosX + b;
			}
			else {
				curPosX = (curPosY - b) / a;
			}
			tmpMatrix.setTranslate(curPosX, curPosY);
			tmpMatrix.postRotate((float)Math.toDegrees(Math.atan(a)) + extraDegree, curPosX, curPosY);
			fish.setCurPosX((int)curPosX);
			fish.setCurPosY((int)curPosY);
			if (Math.abs(a) <= 1) {
				if (fish.getToPosX() > startPosX) {
					curPosX += speed;	
				} 
				else {
					curPosX -= speed;
				}	
			}
			else {
				if (fish.getToPosY() > startPosY) {
					curPosY += speed;	
				} 
				else {
					curPosY -= speed;
				}	
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
				fish.isOutScene() ||
				global.isYouLose() ||
				global.isYouWin()) {
				try {
					/* �ӵ��Ѿ�����һ�����ˣ�������ʹ���� */
					myView.bulletTmp = null;
					myView.updatePicLayer(MySurfaceView.CHANGE_MODE_REMOVE, 
										  MySurfaceView.MIDDLE_LAYER, fish);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/* ���Ӯ�����ˣ������˶��Ͷ����̶߳�Ҫ�˳� */
				if (global.isYouLose() || global.isYouWin()) {
					break;
				}
				
				/* ���ɾ�������ӵ�����Ҫ�������� */
				if (false == fish.isFish()) {
					break;
				}
				
				/* �ӳ����ͳ�Ƽ������� */
				if (fish.isOutScene()) {
					escapeCount = global.getEscapeCount() + 1;
					global.setEscapeCount(escapeCount);
					Log.d("FishRunThread-->���ӳ�", "���ӳ�" + escapeCount + "," + global.getDeviceWidth());
					if (escapeCount >= taskEscapeCount) {
						global.setYouLose(true);
						break;
					}
				}
				/* �������ͳ�Ƽ������� */
				else {
					hitCount = global.getHitCount() + 1;
					global.setHitCount(hitCount);
					Log.d("FishRunThread-->", "�ѻ���" + hitCount);
					if (hitCount >= taskHitCount) {
						global.setYouWin(true);
						break;
					}
				}
				
				/* ɾһ������һ�� */
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
