package com.zhangjie.fish;

import android.R.xml;
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
		
		String string = null;
		Canvas canvas = null;
		/* 设置画笔 */
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(12);
		paint.setAntiAlias(true);	/* 设置抗锯齿 */
		Typeface font = Typeface.create("宋体", Typeface.BOLD);
		paint.setTypeface(font);
		
		/* 获取全局变量 */
		Global global = Global.getInstance();
		
		/* 循环 */
		while (GamingInfo.getGamingInfo().isGaming()) {
			try {
				/* 锁定画布  */
				canvas = myHolder.lockCanvas();
				if (null == canvas) {
					Log.d("onDrawThread-->", "canvas is null");
				}
				/* 画图 */
				myView.onDraw(canvas);
				/* 绘制文本 */
				if (global.isYouWin()) {
//					global.setHitCount(0);
//					global.setEscapeCount(0);
//					global.setYouWin(false);
					canvas.drawText("你赢了!", global.getDeviceWidth() / 2, global.getDeviceHeight() / 2, paint);
					
				}
				else if (global.isYouLose()) {
//					global.setHitCount(0);
//					global.setEscapeCount(0);
//					global.setYouLose(false);
					canvas.drawText("你输了!", global.getDeviceWidth() / 2, global.getDeviceHeight() / 2, paint);
				}
				else {
					canvas.drawText("胜利条件：击中" + global.getTaskHitCount() + 
									"\n已击中：" + global.getHitCount() + 
									"\n失败条件：逃出" + global.getTaskEscapeCount() + 
									"\n已逃出：" + global.getEscapeCount(), 
									10, 22, paint);
				}
				
//				if (global.isYouWin() || global.isYouLose()) {
//					global.setHitCount(0);
//					global.setEscapeCount(0);
//					if (global.isYouWin()) {
//						string = "你赢了！";		
//					} 
//					else if (global.isYouLose()) {
//						string = "你输了！";
//					}
//					x = global.getDeviceWidth() / 2;
//					y = global.getDeviceHeight() / 2;
//					global.setYouLose(false);
//					global.setYouWin(false);
//				}
//				else {
//					string = "胜利条件：击中" + global.getTaskHitCount() + 
//							" 已击中：" + global.getHitCount() + 
//							" 失败条件：逃出" + global.getTaskEscapeCount() + 
//							" 已逃出：" + global.getEscapeCount();
//				}
//				canvas.drawText(string, global.getDeviceWidth() / 2, global.getDeviceHeight() / 2, paint);
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("onDrawThread-->", "");
			}finally {
				/* 解锁画布，可以修改 */
				if (null != canvas) {
					myHolder.unlockCanvasAndPost(canvas);
				}
			}

			/* 控制绘图的速度 */
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
