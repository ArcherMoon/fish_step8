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
					canvas.drawText("你赢了!", global.getDeviceWidth() / 2, global.getDeviceHeight() / 2, paint);
					++myView.scene;
					myView.loadScene("images/bg/fishlightbg_" + myView.scene + ".jpg");
				}
				else if (global.isYouLose()) {
					canvas.drawText("你输了!", global.getDeviceWidth() / 2, global.getDeviceHeight() / 2, paint);
					myView.loadScene("images/bg/fishlightbg_" + myView.scene + ".jpg");
				}
				else {
					canvas.drawText("胜利条件：击中" + global.getTaskHitCount() + 
									"\n已击中：" + global.getHitCount() + 
									"\n失败条件：逃出" + global.getTaskEscapeCount() + 
									"\n已逃出：" + global.getEscapeCount(), 
									10, 22, paint);
				}
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
