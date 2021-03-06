package com.zhangjie.fish;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class PicProperty {
	private Bitmap bitmap = null;
	private Matrix matrix = new Matrix();
	/* 图片缩放比例 */
	private float width_ratio = 1f;
	private float height_ratio = 1f;
	private float ratio = 1f;	/* 最终缩放比例 */
	private int curPosX = 0;
	private int curPosY = 0;
	public boolean isAlreadyHit = false;
	
	public int getCurPosX() {
		return curPosX;
	}

	public void setCurPosX(int curPosX) {
		this.curPosX = curPosX;
	}

	public int getCurPosY() {
		return curPosY;
	}

	public void setCurPosY(int curPosY) {
		this.curPosY = curPosY;
	}

	public PicProperty() {
		matrix.setTranslate(0, 0);
		Global global = Global.getInstance();
		width_ratio = global.getDeviceWidth() / 800f;
		height_ratio = global.getDeviceHeight() / 480f;
		if (width_ratio > height_ratio) {
			ratio = width_ratio;
		}
		else {
			ratio = height_ratio;
		}
	}

	/* 设置矩阵 */
	public void setMatrix(float dx, float dy) {
		matrix.setTranslate(dx, dy);
	}
	/* 图片矩阵，控制移动，旋转 */
	public Matrix getPicMatrix() {
		return matrix;
	}
	
	/* 设置图片 */
	public void setCurPic(AssetManager assets, String path) throws IOException {
		/* 手工回收不用的图片资源 */
		if (null != bitmap && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
		/* 从AssetManager读入流  */
		InputStream is = assets.open(path);
		
		/* 从输入流解析位图 */
		bitmap = BitmapFactory.decodeStream(is);
		bitmap = Bitmap.createScaledBitmap(bitmap, (int) (getPicWidth()*ratio), (int) (getPicHeight()*ratio), true);		
		return;
	}

	public float getRatio() {
		return ratio;
	}

	/* 获取当前图片 */
	public Bitmap getCurPic() {
		return bitmap;
	}
	
	/* 取得图片高度 */
	public int getPicHeight() {
		return bitmap.getHeight();
	}		
	
	/* 取得图片宽度 */
	public int getPicWidth() {
		return bitmap.getWidth();
	}

}
