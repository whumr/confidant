package com.example.richeditor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.WebView;
import android.widget.TextView;

public class TestActivity extends Activity {

	WebView web;
	TextView txt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		
		web = (WebView) findViewById(R.id.web);
//		web.getSettings().setAllowFileAccess(true);
//		web.getSettings().setAllowContentAccess(true);
//		String html = "&lt;font color=\"red\"&gt;xxx&lt;/font&gt;";
		String html = "<font color=\"red\">xxx</font>";
		String path = "content://com.example.richeditor/1.jpg";
//		String path = "content://com.example.richeditor" + Environment.getExternalStorageDirectory().getAbsolutePath()  + "/1.jpg";
//		String path = "file:/" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/1.jpg";
		html += "<br><img src=\"" + path + "\" width=\"100%\">";
		
		
		txt = (TextView) findViewById(R.id.txt);
		txt.setText(html);
		
//		web.loadDataWithBaseURL("", html, "text/html; charset=UTF-8", "UTF-8", null);
		web.loadData(s1 + html + s2, "text/html; charset=UTF-8", "UTF-8");
	}
	
	static String s1 = "<html><head></head><body>";
//	static String s1 = "<html><head><base href=\"file:///sdcard\"></head><body>";
	static String s2 = "</body></html>";

}
