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
	private float width_ratio = 480 / 800f;
	private float height_ratio = 320 / 480f;
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
		/* ��AssetManager������  */
		InputStream is = assets.open(path);
		
		/* ������������λͼ */
		bitmap = BitmapFactory.decodeStream(is);
		bitmap = Bitmap.createScaledBitmap(bitmap, (int) (getPicWidth()*height_ratio), (int) (getPicHeight()*height_ratio), true);		
		return;
	}

	public float getWidth_ratio() {
		return width_ratio;
	}

	public float getHeight_ratio() {
		return height_ratio;
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
