package com.fingertip.tuding.base;

import android.app.Application;
import android.content.Context;
import android.os.Process;
import cn.jpush.android.api.JPushInterface;

import com.fingertip.tuding.Globals;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class TudingApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader(getApplicationContext());
		CrashHandler crashHandler = new CrashHandler(this);
		Thread.setDefaultUncaughtExceptionHandler(crashHandler);
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
//				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
//				.writeDebugLogs()
				.build();
		ImageLoader.getInstance().init(config);
	}
	
	public void finishActivity() {
		Globals.clearActivityList(true);
		Process.killProcess(Process.myPid());    
	}
}