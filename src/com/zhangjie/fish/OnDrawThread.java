package com.zhangjie.fish;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
		/* ���û��� */
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(12);
		paint.setAntiAlias(true);	/* ���ÿ���� */
		Typeface font = Typeface.create("����", Typeface.BOLD);
		paint.setTypeface(font);
		
		/* ��ȡȫ�ֱ��� */
		Global global = Global.getInstance();
		
		/* ѭ�� */
		while (GamingInfo.getGamingInfo().isGaming()) {
			try {
				/* ��������  */
				canvas = myHolder.lockCanvas();
				if (null == canvas) {
					Log.d("onDrawThread-->", "canvas is null");
				}
				/* ��ͼ */
				myView.onDraw(canvas);
				/* �����ı� */
				if (global.isYouWin()) {
					canvas.drawText("��Ӯ��!", global.getDeviceWidth() / 2, global.getDeviceHeight() / 2, paint);
					++myView.scene;
					myView.loadScene("images/bg/fishlightbg_" + myView.scene + ".jpg");
				}
				else if (global.isYouLose()) {
					canvas.drawText("������!", global.getDeviceWidth() / 2, global.getDeviceHeight() / 2, paint);
					myView.loadScene("images/bg/fishlightbg_" + myView.scene + ".jpg");
				}
				else {
					canvas.drawText("ʤ������������" + global.getTaskHitCount() + 
									"\n�ѻ��У�" + global.getHitCount() + 
									"\nʧ���������ӳ�" + global.getTaskEscapeCount() + 
									"\n���ӳ���" + global.getEscapeCount(), 
									10, 22, paint);
				}
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("onDrawThread-->", "");
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
