package com.zhangjie.fish;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.zhangjie.fish.parse.ParseParam;

public class Fish extends PicProperty{
	private ArrayList<Bitmap> acts = new ArrayList<Bitmap>();
	private int index = 0;
	private float toPosX = 0;			/* 到哪一点 */
	private float toPosY = 0;
	private int speed = 0;				/* 每秒钟移动速度 */
	private boolean isFish = true;	/* 判断是鱼还是子弹 */
	public boolean isOutScene = false;
	private int deviceWidth = 0;
	private int deviceHeight = 0;

	/* 动作图片数组的长度 */
	public int getActs() {
		return acts.size();
	}
	
	/**
	 * @param curPosX	起始坐标
	 * @param curPosY
	 * @param toPosX	目的坐标
	 * @param toPosY
	 * @param speed		运动速度
	 */
	public Fish(int curPosX, int curPosY, int toPosX, int toPosY, int speed) {
		setCurPosX(curPosX);
		setCurPosY(curPosY);
		this.toPosX = toPosX;
		this.toPosY = toPosY;
		this.speed = speed;
		Global global = Global.getInstance();
		deviceWidth = global.getDeviceWidth();
		deviceHeight = global.getDeviceHeight();
		Log.d("Fish--->", "deviceWidth = " + deviceWidth +", deviceHeight = "+ deviceHeight);
	}

	/**
	 * 创建随机起始点，终点的鱼
	 * @param speed
	 */
	public Fish(int speed) {
		this.toPosX = -1;
		this.speed = speed;
		Global global = Global.getInstance();
		deviceWidth = global.getDeviceWidth();
		deviceHeight = global.getDeviceHeight();
	}

	public boolean isHitBy(PicProperty peer) {
		if (null == peer || peer == this) {
			return false;
		}
		
		int x1 = this.getCurPosX();
		int x2 = peer.getCurPosX();
		int y1 = this.getCurPosY();
		int y2 = peer.getCurPosY();
		int width1 = this.getPicWidth();
		int width2 = peer.getPicWidth();
		int height1 = this.getPicHeight();
		int height2 = peer.getPicHeight();
		
		if ((Math.abs((x1 + width1/2) - (x2 + width2/2)) < (width1/2 + width2/2)) &&
			(Math.abs((y1 + height1/2) - (y2 + height2/2)) < (height1/2 + height2/2))) {
			peer.isAlreadyHit = true;
			this.isAlreadyHit = true;
			Log.d("isHit--->", "撞上了" + x1 +","+ y1 +"," + x2 +"," + y2);
			return true;
		}
		Log.d("isHit--->", "没撞上" + x1 +","+ y1 +"," + x2 +"," + y2);
		return false;
	}
	
	/* 判断鱼是否被触摸到 */
	public boolean isTouched(int x, int y) {
		int x1 = this.getCurPosX();
		int y1 = this.getCurPosY();
		int w1 = this.getPicWidth();
		int h1 = this.getPicHeight();
		if ((x > x1) && (x < x1 + w1) &&
			(y > y1) && (y < y1 + h1)) {
			this.isAlreadyHit = true;
			return true;		
		}
		return false;
	}
	
	/* 判断鱼是否飞出屏幕 */
	public boolean isOutScene() {
		/**
		 * x坐标大于左边界-鱼长，小于右边界+鱼长，并且
		 * y坐标大于上边界-鱼长，小于下边界加鱼长，则在屏幕内，
		 * 否则判断为飞出屏幕，考虑鱼长是为了能绘制完半个鱼身。
		 */
		int x = getCurPosX();
		int y = getCurPosY();
		int w = getPicWidth();
		if ((x < -w) ||
			(x > deviceWidth + w) ||
			(y < -w) ||
			(y > deviceHeight + w)) {
			isOutScene = true;
			Log.d("isOutScene--->", "deviceWidth = " + deviceWidth +", deviceHeight = "+ deviceHeight);
		}
		return isOutScene;
	}
	
	/**
	 * 从一张大图中截取需要的小图，桩函数
	 */
	public void setActPics_stub(AssetManager assets, String path, String fishname) throws IOException {
//		acts[0] = BitmapFactory.decodeStream(assets.open("images/fish/fish00_01.png"));
//		acts[1] = BitmapFactory.decodeStream(assets.open("images/fish/fish00_02.png"));
//		acts[2] = BitmapFactory.decodeStream(assets.open("images/fish/fish00_03.png"));
//		acts[3] = BitmapFactory.decodeStream(assets.open("images/fish/fish00_04.png"));
//		acts[4] = BitmapFactory.decodeStream(assets.open("images/fish/fish00_05.png"));
//		acts[5] = BitmapFactory.decodeStream(assets.open("images/fish/fish00_06.png"));
//		acts[6] = BitmapFactory.decodeStream(assets.open("images/fish/fish00_07.png"));
//		acts[7] = BitmapFactory.decodeStream(assets.open("images/fish/fish00_08.png"));
//		acts[8] = BitmapFactory.decodeStream(assets.open("images/fish/fish00_09.png"));
//		acts[9] = BitmapFactory.decodeStream(assets.open("images/fish/fish00_10.png"));
		
		return;
	}
	
	
	/**
	 * 从保存图片坐标，大小的hash表中，取得数据截出动作图片
	 * @param 1		hash表
	 * @param 2		要截取的动作图片的名称
	 */
	public void setActPics(HashMap<String, ArrayList<ParseParam>> map, Bitmap pic, String fishname) throws XmlPullParserException, Exception {
		/* 设置图片的变换矩阵，如调整图片适应屏幕大小 */
		Matrix scaleMatrix = new Matrix();
		
		/* 根据图片名称从hash表取得相应的图片参数数组 */
		ArrayList<ParseParam> arrayList =  map.get(fishname);
		
		/* 遍历数组参数从大图中截取小图 */
		Bitmap tmpBitmap = null;
		for (ParseParam parseParam : arrayList) {
			int width = parseParam.getWidth();
			int height = parseParam.getHeight();
			scaleMatrix.setScale(getHeight_ratio(), getHeight_ratio());
			tmpBitmap = Bitmap.createBitmap(pic, 
					parseParam.getX(), 
					parseParam.getY(), 
					width, 
					height,
					scaleMatrix,
					true);
			acts.add(tmpBitmap);
		}
	}
	
	public void setCurPicIndex(int index){
		this.index = index;
	}

	@Override
	public void setMatrix(float dx, float dy) {
		// TODO Auto-generated method stub
		super.setMatrix(dx, dy);
	}

	@Override
	public Matrix getPicMatrix() {
		// TODO Auto-generated method stub
		return super.getPicMatrix();
	}

	@Override
	public Bitmap getCurPic() {
		return acts.get(index);
	}

	@Override
	public int getPicHeight() {
		return acts.get(index).getHeight();
	}

	@Override
	public int getPicWidth() {
		return acts.get(index).getWidth();
	}

	public float getToPosX() {
		return toPosX;
	}

	public float getToPosY() {
		return toPosY;
	}

	public void setToPosX(float toPosX) {
		this.toPosX = toPosX;
	}

	public void setToPosY(float toPosY) {
		this.toPosY = toPosY;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public boolean isFish() {
		return isFish;
	}

	public void setIsFish(boolean isFish) {
		this.isFish = isFish;
	}
}
