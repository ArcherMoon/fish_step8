package com.zhangjie.fish;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zhangjie.fish.parse.ParseParam;
import com.zhangjie.fish.parse.PicParser;

@SuppressLint({ "DrawAllocation", "UseSparseArrays" })
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{

	private OnDrawThread myDrawThread;
	public AssetManager assets = null;
	public  int deviceWidth = 0;
	public  int deviceHeight = 0;
	
	/* ���˶��Ĺ켣��ֱ�ߣ����ߣ�������� */
	public static final int MOVE_STRAIGHT = 0;
	public static final int MOVE_CURVE = 1;
	public static final int MOVE_RANDOM = 2;
	
	/* ���������xml����������Сͼ */
	public HashMap<String, HashMap<String, ArrayList<ParseParam>>> actPicsMap = new HashMap<String, HashMap<String, ArrayList<ParseParam>>>();
	
	/* ��ͼ���������ɾ�����µĺ� */
	public static final int CHANGE_MODE_UPDATE = 1;
	public static final int CHANGE_MODE_ADD = 2;
	public static final int CHANGE_MODE_REMOVE = 3;
	/* ͼ���Ƿ���Ҫ���� */
	private static boolean isLayerChange = false;
	
	/* ���ڻ���ͼ���hash�� */
	private HashMap<Integer, ArrayList<PicProperty>> picLayer = new HashMap<Integer, ArrayList<PicProperty>>();
	/* ������ʱ����Ҫ����ͼƬ��Ҫɾ��ͼƬ��hash�� */
	private HashMap<Integer, ArrayList<PicProperty>> addPicLayer = new HashMap<Integer, ArrayList<PicProperty>>();
	private HashMap<Integer, ArrayList<PicProperty>> removePicLayer = new HashMap<Integer, ArrayList<PicProperty>>();
	
	public static final int BOTTOM_LAYER = 1; 
	public static final int MIDDLE_LAYER = 2; 
	public static final int TOP_LAYER = 3; 
	
	/* ���������� */
	public int touchPosX = 0;
	public int touchPosY = 0;
	
	public Fish bulletTmp = null;
	public Bitmap globalBitmap = null;
	
	private Global global = null;
	private PicProperty bg = null;
	public int scene = 0;
	
	public MySurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		Log.d("MySurfaceView--->", "chenggong");
		
		/* ��ȡȫ�ֱ��� */
		global = Global.getInstance();
		/* ��assets�ļ��л�ȡͼƬ��Դ��  */
		assets = context.getAssets();
		
		/*
		 * �Ѹ�SurfaceViewע���SurfaceHolder��
		 * ����SurfaceView�仯ʱSurfaceHolder.Callback�Ϳ���ִ��
		 */
		SurfaceHolder surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		/* ������ͼ�߳� */
		myDrawThread = new OnDrawThread(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.d("surfaceChanged--->", "width = " + width + " height = " + height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub	
	
		/* ȡ���豸�ĳ��� */
		deviceWidth = this.getWidth();
		deviceHeight = this.getHeight();
		global.setDeviceWidth(deviceWidth);
		global.setDeviceHeight(deviceHeight);
		
		Log.d("surfaceCreated--->", "�ɹ� " + global.getDeviceWidth() + global.getDeviceHeight());
		
		/* ��ʼ��ͼ�㣨�����㣬�м�㣬���㣩 */
		ArrayList<PicProperty> bottomPicList = new ArrayList<PicProperty>();
		picLayer.put(BOTTOM_LAYER, bottomPicList);
		ArrayList<PicProperty> middlePicList = new ArrayList<PicProperty>();
		picLayer.put(MIDDLE_LAYER, middlePicList);
		ArrayList<PicProperty> topPicList = new ArrayList<PicProperty>();
		picLayer.put(TOP_LAYER, topPicList);
		
		bg = new PicProperty();
		
		/* ����ͼ����xml�ļ������ɶ���Сͼ���꣬�ߴ�洢��hash�� */
		try {
			actPicsMap.put("fish", PicParser.parser(assets, "images/fish/fish.xml"));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (XmlPullParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
			
		try {
			globalBitmap = BitmapFactory.decodeStream(assets.open("images/fish/fish.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		loadScene("images/bg/fishlightbg_" + scene + ".jpg");
		
		/* ������ͼ�߳� */
		myDrawThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d("surfaceDestroyed--->", "");
	}
	
	/* ����ͼ��,����ͼƬ�����Ӻ�ɾ��������ͼƬ���ԣ�ƽ�ƣ���ת�ȣ�����ÿ���̵߳������� */
	public synchronized void updatePicLayer(int changeMode, int layerID, PicProperty pic) throws IOException {
		switch (changeMode) {
		/* �����ӣ�ɾ�����е�ͼƬ���µ������õı���picLayer */
		case CHANGE_MODE_UPDATE:
			if (true == isLayerChange) {
				for (Integer id : addPicLayer.keySet()) {
					/* ����ÿ��ͼ���ͼƬ���� */
					for (PicProperty picProperty : addPicLayer.get(id)) {
						picLayer.get(id).add(picProperty);
					}			
				}	
				addPicLayer.clear();
				
				for (Integer id : removePicLayer.keySet()) {
					/* ����ÿ��ͼ���ͼƬ���� */
					for (PicProperty picProperty : removePicLayer.get(id)) {
						picLayer.get(id).remove(picProperty);
						Log.d("updatelayer-->", picProperty.toString());
					}			
				}
				removePicLayer.clear();
				isLayerChange = false;
			}			
			break;
		/* �����ӵ�ͼƬ��ӵ����ӱ��� */
		case CHANGE_MODE_ADD:
			ArrayList<PicProperty> addList = addPicLayer.get(layerID);
			if (null == addList) {
				addList = new ArrayList<PicProperty>();
				addPicLayer.put(layerID, addList);
			}
			addList.add(pic);
			isLayerChange = true;
			break;
		/* ��ɾ����ͼƬ��ӵ�ɾ������ */
		case CHANGE_MODE_REMOVE:
			ArrayList<PicProperty> removeList = removePicLayer.get(layerID);
			if (null == removeList) {
				removeList = new ArrayList<PicProperty>();
				removePicLayer.put(layerID, removeList);
			}
			removeList.add(pic);
			isLayerChange = true;
			Log.d("updatelayer-->", "�Ƴ�ͼƬ");
			break;

		default:
			break;
		}
	}
	
	/* ��һ֡ͼ */
	public void onDraw(Canvas canvas) {
		/* ����ͼ�� */
		try {
			updatePicLayer(CHANGE_MODE_UPDATE, 0, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* ��������ͼ�� */
		for (Integer id : picLayer.keySet()) {
			/* ����ÿ��ͼ���ͼƬ���� */
			for (PicProperty picProperty : picLayer.get(id)) {
				canvas.drawBitmap(picProperty.getCurPic(), picProperty.getPicMatrix(), null);
			}			
		}		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d("onTouchEvent--->", "����");
			touchPosX = (int)event.getX();
			touchPosY = (int)event.getY();
			Log.d("onTouchEvent--->", "touchPosX" + touchPosX + "touchPosY" + touchPosY);
			/* ����̨����ʼ���꣬����������꣬����б�ʣ��Ӷ��ó��˶�·�� */
			Fish bullet = new Fish(deviceWidth / 2, deviceHeight, touchPosX, touchPosY, 320);
			try {
				bullet.setActPics(actPicsMap.get("fish"), globalBitmap, "fish03");
				updatePicLayer(CHANGE_MODE_ADD, MIDDLE_LAYER, bullet);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			bullet.setIsFish(false);
			FishRunThread bulletThread = new FishRunThread(bullet, MOVE_STRAIGHT, null, this);
			bulletThread.start();
			bulletTmp = bullet;
			break;
		case MotionEvent.ACTION_UP:
			Log.d("onTouchEvent--->", "̧��");
			touchPosX = 0;
			touchPosY = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("onTouchEvent--->", "�϶�");
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * װ�ر���
	 */
	public void loadBG(String string) {
		/* �ӱ����������� */
		try {
			bg.setCurPic(assets, string);
			updatePicLayer(CHANGE_MODE_ADD, BOTTOM_LAYER, bg);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.d("set bg", "");
			e1.printStackTrace();
		}
		return;
	}
	
	/**
	 * װ�س���
	 */
	public void loadScene(String string) {
		/* ��ʼ������������ */
		global.setHitCount(0);
		global.setEscapeCount(0);
		global.setYouLose(false);
		global.setYouWin(false);
		
		loadBG(string);
	
		/* ��һ���㵽�м�� */
		Fish fish = null;
		fish = new Fish(deviceWidth, 160, 0 ,0, 50);
		
		/* �ӵ�2���㵽�м�� */
		Fish fish2 = null;
		fish2 = new Fish(50);
		try {
			fish.setActPics(actPicsMap.get("fish"), globalBitmap, "fish01");
			updatePicLayer(CHANGE_MODE_ADD, MIDDLE_LAYER, fish);
			fish2.setActPics(actPicsMap.get("fish"), globalBitmap, "fish02");
			updatePicLayer(CHANGE_MODE_ADD, MIDDLE_LAYER, fish2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("set fish", "");
			e.printStackTrace();
		}
	
		/* ���˶����߳� */
		FishRunThread fishRunThread = new FishRunThread(fish, MOVE_CURVE, bulletTmp, this);
		fishRunThread.start();
		
		FishRunThread fishRunThread2 = new FishRunThread(fish2, MOVE_STRAIGHT, bulletTmp, this);
		fishRunThread2.start();
		
		/* ��ı����������߳� */
		FishActThread fishActThread = new FishActThread(fish);
		fishActThread.start();
		
		FishActThread fishActThread2 = new FishActThread(fish2);
		fishActThread2.start();
		return;		
	}
}
