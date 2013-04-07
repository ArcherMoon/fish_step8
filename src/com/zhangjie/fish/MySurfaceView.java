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
	
	/* 鱼运动的轨迹，直线，曲线，还是随机 */
	public static final int MOVE_STRAIGHT = 0;
	public static final int MOVE_CURVE = 1;
	public static final int MOVE_RANDOM = 2;
	
	/* 用来保存从xml解析出来的小图 */
	public HashMap<String, HashMap<String, ArrayList<ParseParam>>> actPicsMap = new HashMap<String, HashMap<String, ArrayList<ParseParam>>>();
	
	/* 对图层进行增，删，更新的宏 */
	public static final int CHANGE_MODE_UPDATE = 1;
	public static final int CHANGE_MODE_ADD = 2;
	public static final int CHANGE_MODE_REMOVE = 3;
	/* 图层是否需要更改 */
	private static boolean isLayerChange = false;
	
	/* 用于绘制图层的hash表 */
	private HashMap<Integer, ArrayList<PicProperty>> picLayer = new HashMap<Integer, ArrayList<PicProperty>>();
	/* 用于临时保存要增加图片和要删除图片的hash表 */
	private HashMap<Integer, ArrayList<PicProperty>> addPicLayer = new HashMap<Integer, ArrayList<PicProperty>>();
	private HashMap<Integer, ArrayList<PicProperty>> removePicLayer = new HashMap<Integer, ArrayList<PicProperty>>();
	
	public static final int BOTTOM_LAYER = 1; 
	public static final int MIDDLE_LAYER = 2; 
	public static final int TOP_LAYER = 3; 
	
	/* 触摸点坐标 */
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
		
		/* 获取全局变量 */
		global = Global.getInstance();
		/* 从assets文件夹获取图片资源等  */
		assets = context.getAssets();
		
		/*
		 * 把该SurfaceView注册给SurfaceHolder，
		 * 这样SurfaceView变化时SurfaceHolder.Callback就可以执行
		 */
		SurfaceHolder surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		/* 创建绘图线程 */
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
	
		/* 取得设备的长宽 */
		deviceWidth = this.getWidth();
		deviceHeight = this.getHeight();
		global.setDeviceWidth(deviceWidth);
		global.setDeviceHeight(deviceHeight);
		
		Log.d("surfaceCreated--->", "成功 " + global.getDeviceWidth() + global.getDeviceHeight());
		
		/* 初始化图层（背景层，中间层，顶层） */
		ArrayList<PicProperty> bottomPicList = new ArrayList<PicProperty>();
		picLayer.put(BOTTOM_LAYER, bottomPicList);
		ArrayList<PicProperty> middlePicList = new ArrayList<PicProperty>();
		picLayer.put(MIDDLE_LAYER, middlePicList);
		ArrayList<PicProperty> topPicList = new ArrayList<PicProperty>();
		picLayer.put(TOP_LAYER, topPicList);
		
		bg = new PicProperty();
		
		/* 将大图按照xml文件解析成多张小图坐标，尺寸存储在hash表 */
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
		
		/* 启动绘图线程 */
		myDrawThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d("surfaceDestroyed--->", "");
	}
	
	/* 更新图层,包括图片的增加和删除，关于图片属性（平移，旋转等），由每个线程单独控制 */
	public synchronized void updatePicLayer(int changeMode, int layerID, PicProperty pic) throws IOException {
		switch (changeMode) {
		/* 将增加，删除表中的图片更新到绘制用的表中picLayer */
		case CHANGE_MODE_UPDATE:
			if (true == isLayerChange) {
				for (Integer id : addPicLayer.keySet()) {
					/* 遍历每个图层的图片链表 */
					for (PicProperty picProperty : addPicLayer.get(id)) {
						picLayer.get(id).add(picProperty);
					}			
				}	
				addPicLayer.clear();
				
				for (Integer id : removePicLayer.keySet()) {
					/* 遍历每个图层的图片链表 */
					for (PicProperty picProperty : removePicLayer.get(id)) {
						picLayer.get(id).remove(picProperty);
						Log.d("updatelayer-->", picProperty.toString());
					}			
				}
				removePicLayer.clear();
				isLayerChange = false;
			}			
			break;
		/* 将增加的图片添加到增加表中 */
		case CHANGE_MODE_ADD:
			ArrayList<PicProperty> addList = addPicLayer.get(layerID);
			if (null == addList) {
				addList = new ArrayList<PicProperty>();
				addPicLayer.put(layerID, addList);
			}
			addList.add(pic);
			isLayerChange = true;
			break;
		/* 将删除的图片添加到删除表中 */
		case CHANGE_MODE_REMOVE:
			ArrayList<PicProperty> removeList = removePicLayer.get(layerID);
			if (null == removeList) {
				removeList = new ArrayList<PicProperty>();
				removePicLayer.put(layerID, removeList);
			}
			removeList.add(pic);
			isLayerChange = true;
			Log.d("updatelayer-->", "移除图片");
			break;

		default:
			break;
		}
	}
	
	/* 画一帧图 */
	public void onDraw(Canvas canvas) {
		/* 更新图层 */
		try {
			updatePicLayer(CHANGE_MODE_UPDATE, 0, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* 遍历三个图层 */
		for (Integer id : picLayer.keySet()) {
			/* 遍历每个图层的图片链表 */
			for (PicProperty picProperty : picLayer.get(id)) {
				canvas.drawBitmap(picProperty.getCurPic(), picProperty.getPicMatrix(), null);
			}			
		}		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d("onTouchEvent--->", "按下");
			touchPosX = (int)event.getX();
			touchPosY = (int)event.getY();
			Log.d("onTouchEvent--->", "touchPosX" + touchPosX + "touchPosY" + touchPosY);
			/* 由炮台的起始坐标，触摸点的坐标，计算斜率，从而得出运动路线 */
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
			Log.d("onTouchEvent--->", "抬起");
			touchPosX = 0;
			touchPosY = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("onTouchEvent--->", "拖动");
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 装载背景
	 */
	public void loadBG(String string) {
		/* 加背景到背景层 */
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
	 * 装载场景
	 */
	public void loadScene(String string) {
		/* 初始化场景的条件 */
		global.setHitCount(0);
		global.setEscapeCount(0);
		global.setYouLose(false);
		global.setYouWin(false);
		
		loadBG(string);
	
		/* 加一条鱼到中间层 */
		Fish fish = null;
		fish = new Fish(deviceWidth, 160, 0 ,0, 50);
		
		/* 加第2条鱼到中间层 */
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
	
		/* 鱼运动的线程 */
		FishRunThread fishRunThread = new FishRunThread(fish, MOVE_CURVE, bulletTmp, this);
		fishRunThread.start();
		
		FishRunThread fishRunThread2 = new FishRunThread(fish2, MOVE_STRAIGHT, bulletTmp, this);
		fishRunThread2.start();
		
		/* 鱼改变自身动作的线程 */
		FishActThread fishActThread = new FishActThread(fish);
		fishActThread.start();
		
		FishActThread fishActThread2 = new FishActThread(fish2);
		fishActThread2.start();
		return;		
	}
}
