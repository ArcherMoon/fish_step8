package com.zhangjie.fish;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button buttonNext = null;
	private Button buttonRestart = null;
	private MySurfaceView myView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* 创建surfaceView，创建绘图线程（并不启动）  */
//		MySurfaceView myView = new MySurfaceView(this);
		
		/* 设置为显示的View，此时SurfaceHolder.Callback会执行surfaceCreated，
		 * 启动绘图线程*/
//		setContentView(myView);

		setContentView(R.layout.activity_main);
//		Global global = Global.getInstance();
//		myView = (MySurfaceView)findViewById(R.id.MySurfaceView);

		buttonNext = (Button)findViewById(R.id.button_next);
		buttonRestart = (Button)findViewById(R.id.button_restart);
//		if (myView != null) {
//			Log.d("myview--->", "NULL" + myView);
//		}
//		myView.setButtonNext(buttonNext);
//		myView.setButtonRestart(buttonRestart);	
	}
		
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		/* 设置游戏状态为结束 */
		GamingInfo.getGamingInfo().setGaming(false);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		/* 设置游戏状态为开始 */
		GamingInfo.getGamingInfo().setGaming(true);
	}

}
