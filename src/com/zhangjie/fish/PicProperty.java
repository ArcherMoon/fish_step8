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
	/* ͼƬ���ű��� */
	private float width_ratio = 1f;
	private float height_ratio = 1f;
	private float ratio = 1f;	/* �������ű��� */
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

	/* ���þ��� */
	public void setMatrix(float dx, float dy) {
		matrix.setTranslate(dx, dy);
	}
	/* ͼƬ���󣬿����ƶ�����ת */
	public Matrix getPicMatrix() {
		return matrix;
	}
	
	/* ����ͼƬ */
	public void setCurPic(AssetManager assets, String path) throws IOException {
		/* �ֹ����ղ��õ�ͼƬ��Դ */
		if (null != bitmap && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
		/* ��AssetManager������  */
		InputStream is = assets.open(path);
		
		/* ������������λͼ */
		bitmap = BitmapFactory.decodeStream(is);
		bitmap = Bitmap.createScaledBitmap(bitmap, (int) (getPicWidth()*ratio), (int) (getPicHeight()*ratio), true);		
		return;
	}

	public float getRatio() {
		return ratio;
	}

	/* ��ȡ��ǰͼƬ */
	public Bitmap getCurPic() {
		return bitmap;
	}
	
	/* ȡ��ͼƬ�߶� */
	public int getPicHeight() {
		return bitmap.getHeight();
	}		
	
	/* ȡ��ͼƬ��� */
	public int getPicWidth() {
		return bitmap.getWidth();
	}

}
