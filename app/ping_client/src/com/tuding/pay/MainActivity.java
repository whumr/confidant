package com.tuding.pay;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {

	EditText edit;
	Button alipay_btn, alipay_wap_btn, wx_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		edit = (EditText) findViewById(R.id.edit);
		alipay_btn = (Button) findViewById(R.id.alipay_btn);
		alipay_wap_btn = (Button) findViewById(R.id.alipay_wap_btn);
		wx_btn = (Button) findViewById(R.id.wx_btn);
		alipay_btn.setOnClickListener(this);
		alipay_wap_btn.setOnClickListener(this);
		wx_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.alipay_btn:
			break;
		case R.id.alipay_wap_btn:
			break;
		case R.id.wx_btn:
			break;
		}
	}
	
	private void requestCharge(String channel) {
		
	}
}
