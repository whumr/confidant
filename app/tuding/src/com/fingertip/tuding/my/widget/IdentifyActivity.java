package com.fingertip.tuding.my.widget;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseNavActivity;
import com.fingertip.tuding.common.UserSession;

public class IdentifyActivity extends BaseNavActivity implements OnClickListener {
	
	private Button view_account_btn, cash_btn;
	private TextView yue_txt, income_txt, identify_txt;
	private LinearLayout identify_layout;
	private UserSession session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_identify);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		yue_txt = (TextView) findViewById(R.id.yue_txt);
		income_txt = (TextView) findViewById(R.id.income_txt);
		identify_txt = (TextView) findViewById(R.id.identify_txt);
		view_account_btn = (Button) findViewById(R.id.view_account_btn);
		cash_btn = (Button) findViewById(R.id.cash_btn);
		identify_layout = (LinearLayout) findViewById(R.id.identify_layout);
		session = UserSession.getInstance();
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.my_account);
		view_account_btn.setOnClickListener(this);
		cash_btn.setOnClickListener(this);
		identify_layout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.view_account_btn:
			break;
		case R.id.cash_btn:
			break;
		case R.id.identify_layout:
			identify();
			break;
		}
	}
	
	private void identify() {
		
	}
}
