package com.fingertip.tuding.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.fingertip.tuding.Globals;
import com.fingertip.tuding.common.ProgressLoading;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * ����(Activity)���࣬Ŀǰֻ��������������ҳ�����ƣ�����ͳ�ƣ�
 * @author Administrator
 *
 */
public class BaseActivity extends Activity{
	public static final String EXTRA_PARAM = "extra_param";
	
	protected boolean _count = false;
	protected String _page_name = null;
	
	private ProgressLoading progressLoading;
	private SharedPreferenceUtil sharedPreferenceUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//���� 
		Globals.addActivity(this);
		setPageCount();
	}
	
	/** ��ȡ������� **/
	protected SharedPreferenceUtil getSP() {
		if(sharedPreferenceUtil == null)
			sharedPreferenceUtil = new SharedPreferenceUtil(this);
		return sharedPreferenceUtil;
	}
	
	/** 
	 * @param isDismiss:�Ƿ��ȡ��
	 *  **/
	public void showProgressDialog(boolean isDismiss) {
		if(progressLoading == null){
			progressLoading = new ProgressLoading(this);
			progressLoading.setCancelable(isDismiss);
		}
		progressLoading.show();
	}
	
	public void dismissProgressDialog() {
		if(progressLoading != null)
			progressLoading.dismiss();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (_count) {
			MobclickAgent.onPageStart(_page_name);
			MobclickAgent.onResume(this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (_count) {
			MobclickAgent.onPageEnd(_page_name);
			MobclickAgent.onPause(this);
		}
	}
	
	protected void setPageCount() {
	}
	
	protected void setPageName(String page_name) {
		this._count = true;
		this._page_name = page_name;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissProgressDialog();
		Globals.removeActivity(this);
	}
	
	public void toastShort(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public void toastLong(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
}
