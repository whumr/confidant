package com.fingertip.tuding.base;

import android.support.v4.app.Fragment;

import com.fingertip.tuding.common.ProgressLoading;
import com.umeng.analytics.MobclickAgent;

/**
 * ����(Fragment)���࣬Ŀǰֻ��������������ҳ�����ƣ�����ͳ�ƣ�
 * @author Administrator
 * 
 */
public class BaseFragment extends Fragment {
	
	protected boolean _count = false;
	protected String _page_name = null;
	
	private ProgressLoading progressLoading;
	
	/** 
	 * @param isDismiss:�Ƿ��ȡ��
	 *  **/
	protected void showProgressDialog(boolean isDismiss) {
		if(progressLoading == null){
			progressLoading = new ProgressLoading(getActivity());
			progressLoading.setCancelable(isDismiss);
		}
		progressLoading.show();
	}
	
	protected void dimissProgressDialog() {
		if(progressLoading != null)
			progressLoading.dismiss();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (_count)
			MobclickAgent.onPageEnd(_page_name);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (_count)
			MobclickAgent.onPageStart(_page_name);
	}
	
	protected void setPageName(String page_name) {
		this._count = true;
		this._page_name = page_name;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dimissProgressDialog();
	}
}
