package com.zhangjie.fish;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class OnDrawThread extends Thread{
	private SurfaceHolder myHolder = null;
	private MySurfaceView myView = null;
	private static final long SLEEP_TIME = 30;
	
	
	public OnDrawThread(MySurfaceView surfaceView) {
		this.myView =  surfaceView;
		this.myHolder = surfaceView.getHolder();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		Canvas canvas = null;
		/* ѭ�� */
		while (GamingInfo.getGamingInfo().isGaming()) {
			try {
				/* ��������  */
				canvas = myHolder.lockCanvas();
				if (null == canvas) {
					Log.d("onDrawThread--", "canvas is null");
				}
				/* ��ͼ */
				myView.onDraw(canvas);
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("onDrawThread--", "");
			}finally {
				/* ���������������޸� */
				if (null != canvas) {
					myHolder.unlockCanvasAndPost(canvas);
				}
			}

			/* ���ƻ�ͼ���ٶ� */
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(this.getName(), e.toString());
			}
		}
	}
}
