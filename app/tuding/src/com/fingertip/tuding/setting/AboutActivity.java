package com.fingertip.tuding.setting;

import android.os.Bundle;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseNavActivity;

public class AboutActivity extends BaseNavActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		findViews();
		setupViews();
	}
}
