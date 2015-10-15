package com.fingertip.tuding.setting;

import java.io.File;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.tuding.Globals;
import com.fingertip.tuding.R;
import com.fingertip.tuding.account.LoginActivity;
import com.fingertip.tuding.base.BaseNavActivity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.util.FileUtil;

public class SettingActivity extends BaseNavActivity implements View.OnClickListener {
	
	private TextView cache_size_txt, version_txt;
	private LinearLayout about;
	private LinearLayout suggest;
	private LinearLayout clear_cache;
	private LinearLayout logout;
	
	private String cache_dir;
	private SharedPreferenceUtil sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		cache_size_txt = (TextView) findViewById(R.id.cache_size_txt);
		version_txt = (TextView) findViewById(R.id.version_txt);
		about = (LinearLayout) findViewById(R.id.setting_about);
		suggest = (LinearLayout) findViewById(R.id.setting_suggest);
		clear_cache = (LinearLayout) findViewById(R.id.setting_delete_cache);
		logout = (LinearLayout) findViewById(R.id.setting_logout);
		sp = new SharedPreferenceUtil(this);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.setting_title);
		
		about.setOnClickListener(this);
		suggest.setOnClickListener(this);
		clear_cache.setOnClickListener(this);
		logout.setOnClickListener(this);
		
		cache_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Globals.PATH_CACHE;
		
		getCacheSize();
		try {
			PackageManager pm = getPackageManager();
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			version_txt.setText("V" + info.versionName);
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.setting_about:
			intent = new Intent();
			intent.setClass(this, AboutActivity.class);
			break;
		case R.id.setting_suggest:
			intent = new Intent();
			intent.setClass(this, SuggestActivity.class);
			break;
		case R.id.setting_delete_cache:
			clearCache();
			break;
		case R.id.setting_logout:
			UserSession.logout();
			sp.setStringValue(SharedPreferenceUtil.LAST_LOGIN_ID, "");
			intent = new Intent();
			Globals.clearActivityList(false);
			intent.setClass(this, LoginActivity.class);
			break;
		}
		if (intent != null)
			startActivity(intent);
	}
	
	private void getCacheSize() {
		cache_size_txt.setText(FileUtil.getCacheSize(cache_dir));
	}
	
	private void clearCache() {
		FileUtil.deleteDir(new File(cache_dir));
		getCacheSize();
		toastShort("缓存清理成功");
	}
	
}
