package com.fingertip.tuding.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Toast;

import com.fingertip.tuding.Globals;
import com.fingertip.tuding.common.ProgressLoading;
import com.umeng.analytics.MobclickAgent;

/**
 * ����(FragmentActivity)���࣬Ŀǰֻ��������������ҳ�����ƣ�����ͳ�ƣ�
 * @author Administrator
 *
 */
public class BaseFragmentActivity extends FragmentActivity {
	
	protected boolean _count = false;
	
	private ProgressLoading progressLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// ����
		Globals.addActivity(this);
		setPageCount();
	}

	/** 
	 * @param isDismiss:�Ƿ��ȡ��
	 *  **/
	public void showProgressDialog(boolean isDismiss) {
		if(progressLoading == null){
			progressLoading = new ProgressLoading(BaseFragmentActivity.this);
			progressLoading.setCancelable(isDismiss);
		}
		progressLoading.show();
	}
	
	public void dimissProgressDialog() {
		if(progressLoading != null)
			progressLoading.dismiss();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (_count)
			MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (_count)
			MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dimissProgressDialog();
		Globals.removeActivity(this);
	}
	
	protected void setPageCount() {
	}
	
	public void toastShort(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public void toastLong(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
}
