package com.zhangjie.fish.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AssetManager;
import android.util.Log;
import android.util.Xml;

public class PicParser {

	public static HashMap<String, ArrayList<ParseParam>> parser(AssetManager assets, String path) throws IOException, XmlPullParserException {
		HashMap<String, ArrayList<ParseParam>> tmpMap = null;
		ArrayList<ParseParam> arrayList = null;
		ParseParam param = null;
		String string = null;
		
		/* 从文件中读出流 */
		InputStream is = assets.open(path);
		
		/* 创建xml解析器 */
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "UTF-8");
		
		/* 遍历数据存储 */
		int eventType = parser.getEventType();
		while (XmlPullParser.END_DOCUMENT != eventType) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
			{
				tmpMap = new HashMap<String, ArrayList<ParseParam>>();
			}
				break;
			case XmlPullParser.START_TAG:
			{
				string = parser.getName();
				if (string.equals("fish")) {
					arrayList = new ArrayList<ParseParam>();
					tmpMap.put(parser.getAttributeValue(null, "name"), arrayList);
				}
				else if (string.equals("frame")) {
					param = new ParseParam();
					arrayList.add(param);
				} else if (string.equals("x")) {
					/* 取得下一个，即<x>后面的文本，返回值是4 */
					eventType = parser.next();
					param.x = Integer.parseInt(parser.getText());
				} else if (string.equals("y")) {
					eventType = parser.next();
					param.y = Integer.parseInt(parser.getText());
				} else if (string.equals("width")) {
					eventType = parser.next();
					param.width = Integer.parseInt(parser.getText());
				} else if (string.equals("height")) {
					eventType = parser.next();
					param.height = Integer.parseInt(parser.getText());
				}		
			}
				break;
			case XmlPullParser.END_TAG:
				break;
			default:
				break;
			}
			eventType = parser.next();
		}
		Log.d("picParser", tmpMap.toString());
		return tmpMap;
	}

}
