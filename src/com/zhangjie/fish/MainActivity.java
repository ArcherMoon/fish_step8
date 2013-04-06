package com.zhangjie.fish;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView = (TextView)findViewById(R.id.text1);
		
		/* ����surfaceView��������ͼ�̣߳�����������  */
		MySurfaceView myView = new MySurfaceView(this);
		
		/* ����Ϊ��ʾ��View����ʱSurfaceHolder.Callback��ִ��surfaceCreated��
		 * ������ͼ�߳�*/
		setContentView(myView);
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
