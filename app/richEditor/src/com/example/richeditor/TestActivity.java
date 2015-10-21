package com.example.richeditor;

import java.io.File;
import java.net.URLDecoder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.example.richeditor.pic.SelectPicActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

@SuppressLint("NewApi")
public class TestActivity extends Activity {
	
	static int PIC = 1001;

	WebView web;
	TextView txt, txt1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.build();
		ImageLoader.getInstance().init(config);
		
		Button btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TestActivity.this, SelectPicActivity.class);
				intent.putExtra(SelectPicActivity.KEY_SINGLE, true);
				startActivityForResult(intent, PIC);				
			}
		});
		
		web = (WebView) findViewById(R.id.web);
//		web.getSettings().setAllowFileAccess(true);
		web.getSettings().setDefaultTextEncodingName("UTF-8") ;
//		web.getSettings().setAllowContentAccess(true);
//		web.getSettings().setAllowUniversalAccessFromFileURLs(true);
//		web.getSettings().setLoadsImagesAutomatically(true);
//		String html = "&lt;font color=\"red\"&gt;xxx&lt;/font&gt;";
		
//		String path = "http://www.baidu.com/img/bdlogo.png";
//		String path = "content://com.example.richeditor" + Environment.getExternalStorageDirectory().getAbsolutePath()  + "/1.jpg";
//		String path = "file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/1.jpg";
//		String path = "content://com.example.richeditor/1.jpg";
//		html += "<br><img src=\"" + path + "\" width=\"100%\">";
//		String path1 = "content://com.example.richeditor/sdcard/1.jpg";
//		html += "<br><img src=\"" + path1 + "\" width=\"100%\">";
//		String path2 = "content://com.example.richeditor" + Environment.getExternalStorageDirectory().getAbsolutePath()  + "/1.jpg";
//		html += "<br><img src=\"" + path2 + "\" width=\"100%\">";
//		String path3 = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/1.jpg";
//		html += "<br><img src=\"" + path3 + "\" width=\"100%\">";
//		String path4 = Cp.URI_PREFIX + "/1.jpg";
//		html += "<br><img src=\"" + path4 + "\" width=\"100%\">";
//		String path5 = "file:/" + Environment.getExternalStorageDirectory().getAbsolutePath()  + "/1.jpg";
//		html += "<br><img src=\"" + path5 + "\" width=\"100%\">";
		
//		html += "<br><font color=\"red\">aaaa</font>";
		
		
		txt = (TextView) findViewById(R.id.txt);
		txt1 = (TextView) findViewById(R.id.txt1);
		
		
//		web.loadDataWithBaseURL("", s1 + html + s2, "text/html; charset=UTF-8", "UTF-8", null);
//			web.loadData(html, "text/html; charset=UTF-8", "UTF-8");
//			web.loadData(s1 + html + s2, "text/html; charset=UTF-8", "UTF-8");
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == PIC) {
			Uri uri = data.getData();
			System.out.println(uri.getPath());
			txt1.setText(uri.getPath() + "       " + Environment.getExternalStorageDirectory().getAbsolutePath());
			showWeb(uri.getPath());
		}
	}
	
	private void showWeb(String path) {
		String html = "<font color=\"red\">xxx</font>";
		html += "<br><img src=\"" + Cp.URI_PREFIX + path + "\" width=\"100%\">";
		html += "<br><font color=\"red\">aaaa</font>";
		txt.setText(html);
//		web.loadDataWithBaseURL("", html, "text/html; charset=UTF-8", "UTF-8", null);
		web.loadData(html, "text/html; charset=UTF-8", "UTF-8");
	}
	
	static String s1 = "<html><head></head><body>";
//	static String s1 = "<html><head><base href=\"file:///sdcard\"></head><body>";
	static String s2 = "</body></html>";

}
