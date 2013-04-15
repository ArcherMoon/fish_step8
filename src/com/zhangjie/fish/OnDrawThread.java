package com.zhangjie.fish;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

public class OnDrawThread extends Thread{
	private SurfaceHolder myHolder = null;
	private MySurfaceView myView = null;
	private static final long SLEEP_TIME = 30;
	private Handler handler = null;
	
	public OnDrawThread(MySurfaceView surfaceView) {
		this.myView =  surfaceView;
		this.myHolder = surfaceView.getHolder();
		handler = surfaceView.getHandler();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		Canvas canvas = null;
		/* ��ȡȫ�ֱ��� */
		Global global = Global.getInstance();
		/* ���û��� */
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		int textSize = (int)(myView.deviceHeight / 16f);
		paint.setTextSize(textSize);
		paint.setAntiAlias(true);	/* ���ÿ���� */
		Typeface font = Typeface.create("����", Typeface.BOLD);
		paint.setTypeface(font);
		
		/* �����û��� */
		Paint paintClear = new Paint();
		paintClear.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
		
		/* ѭ�� */
		while (GamingInfo.getGamingInfo().isGaming()) {
			try {
				/* ��������  */
				canvas = myHolder.lockCanvas();
				if (null == canvas) {
					Log.d("onDrawThread", "-->canvas is null");
				}
				
				/* �������Ƿ��Ӱ��Ч�ʣ��� */
				canvas.drawPaint(paintClear);
		
				/* ��ͼ */
				myView.onDraw(canvas);
				/* �����ı� */
				if (global.isYouWin()) {
//					Message msg = Message.obtain();
//					Bundle bundle = new Bundle();
//					bundle.putString("RESULT", "YOU_WIN");
//					msg.setData(bundle);
//					handler.sendMessage(msg);
//					global.setYouWin(false);

					canvas.drawText("��Ӯ��!", global.getDeviceWidth() / 2, global.getDeviceHeight() / 2, paint);
	
					/* ���г���װ������ʤ��˵��ͨ���� */
					if (++myView.scene > 2) {
						global.setYouWin(false);
						continue;
					}
					myView.loadScene("images/bg/fishlightbg_" + myView.scene + ".jpg");
				}
				else if (global.isYouLose()) {
//					Message msg = Message.obtain();
//					Bundle bundle = new Bundle();
//					bundle.putString("RESULT", "YOU_LOSE");
//					msg.setData(bundle);
//					handler.sendMessage(msg);
					canvas.drawText("������!", global.getDeviceWidth() / 2, global.getDeviceHeight() / 2, paint);
					myView.loadScene("images/bg/fishlightbg_" + myView.scene + ".jpg");
				}
				else {
					canvas.drawText("ʤ������������" + global.getTaskHitCount() + 
									"\n�ѻ��У�" + global.getHitCount() + 
									"\nʧ���������ӳ�" + global.getTaskEscapeCount() + 
									"\n���ӳ���" + global.getEscapeCount(), 
									textSize / 2, textSize, paint);
					if (myView.scene > 2) {
						canvas.drawText("��ϲͨ��!", global.getDeviceWidth() / 2, global.getDeviceHeight() / 2, paint);
					}
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
