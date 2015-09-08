package com.fingertip.tuding.common;

import android.app.Dialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.fingertip.tuding.R;

public class ProgressLoading extends Dialog{
	private ImageView imageView;
	private Animation animation;
	
	public ProgressLoading(Context context) {
		super(context, R.style.progressDialogStyle);
		setContentView(R.layout.progress_circleloading);
		initViews();
	}

	private void initViews() {
		imageView = (ImageView)findViewById(R.id.iv_loadingicon);
		
	}//end initViews

	@Override
	public void show() {
		try {
			super.show();
			LinearInterpolator lin = new LinearInterpolator();
			animation = new RotateAnimation(0, +360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			// ������ʼ��������ִ��ʱ��(1000 = 1 ��)
			animation.setDuration(2000);
			// �����ظ�����(-1 ��ʾһֱ�ظ�)
			animation.setRepeatCount(-1);
			animation.setRepeatCount(Animation.INFINITE);
			animation.setInterpolator(lin);
			imageView.setAnimation(animation);
			animation.start();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}//end show

	@Override
	public void dismiss() {
		try {
			animation.cancel();
			super.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
	}

	
}
