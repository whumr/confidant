package com.fingertip.tuding;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fingertip.tuding.main.MainActivity;

import android.app.Activity;

public class Globals {

	/** 手机目录名称 **/
	public static final String PATH_BASE = "tuding";
	/** 缓存目录 **/
	public static final String PATH_CACHE = PATH_BASE + File.separator + "cache";

	public static final String IMG_DIR = "img", UPLOAD_DIR = "upload", LOG_DIR = "log", PREVIEW_DIR = "preview";
	
	/** 微信appid **/
	public static final String APPID_WX = "wx2ba593638ec9fab5";
	/** 微信secret **/
	public static final String SECRET_WX = "336126680718253602e839242e67cbc5";
	/** debug **/
	//public static final String SECRET_WX = "d4624c36b6795d1d99dcf0547af5443d";
	
	public static final String COMMON_RESULT = "result";
	
	private static List<Activity> activity_list = new ArrayList<Activity>();
	
	public static void addActivity(Activity activity) {
		activity_list.add(activity);
	}
	
	public static void removeActivity(Activity activity) {
		activity_list.remove(activity);
	}
	
	public static void clearActivityList(boolean all) {
		for (Iterator<Activity> it = activity_list.iterator(); it.hasNext(); ) {
			Activity activity = it.next();
			if (activity != null && (all || activity.getClass() != MainActivity.class)) {
				activity.finish();
				it.remove();
			}
		}
	}
	
	public int getActivitySize() {
		return activity_list.size();
	}
}
