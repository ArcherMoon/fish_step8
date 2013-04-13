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
		
		/* ����surfaceView��������ͼ�̣߳�����������  */
//		MySurfaceView myView = new MySurfaceView(this);
		
		/* ����Ϊ��ʾ��View����ʱSurfaceHolder.Callback��ִ��surfaceCreated��
		 * ������ͼ�߳�*/
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
		
		/* ������Ϸ״̬Ϊ���� */
		GamingInfo.getGamingInfo().setGaming(false);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		/* ������Ϸ״̬Ϊ��ʼ */
		GamingInfo.getGamingInfo().setGaming(true);
	}

}
