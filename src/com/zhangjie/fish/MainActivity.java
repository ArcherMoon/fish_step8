package com.zhangjie.fish;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView = (TextView)findViewById(R.id.text1);
		
		/* 创建surfaceView，创建绘图线程（并不启动）  */
		MySurfaceView myView = new MySurfaceView(this);
		
		/* 设置为显示的View，此时SurfaceHolder.Callback会执行surfaceCreated，
		 * 启动绘图线程*/
		setContentView(myView);
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
