package com.tuding.pay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.pingplusplus.android.PaymentActivity;

public class MainActivity extends Activity implements OnClickListener {
	
	static String URL_BASE = "http://192.168.9.99/", 
			URL_CHARGE = URL_BASE + "charge",
			CHANNEL_ALIPAY = "alipay",
			CHANNEL_ALIPAY_WAP = "alipay_wap",
			CHANNEL_WX = "wx";
	
	static int REQUEST_CODE_PAYMENT = 1000;

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
			requestCharge(CHANNEL_ALIPAY);
			break;
		case R.id.alipay_wap_btn:
			requestCharge(CHANNEL_ALIPAY_WAP);
			break;
		case R.id.wx_btn:
			requestCharge(CHANNEL_WX);
			break;
		}
	}
	
	private void requestCharge(String channel) {
		int amount = -1;
		try {
			amount = Integer.parseInt(edit.getText().toString().trim());
		} catch (Exception e) {
		}
		if (amount <= 0) {
			log("请输入正确的金额");
			return;
		}
//		"amount", "subject", "body", "channel"
		RequestParams params = new RequestParams();
		params.addBodyParameter("amount", amount + "");
		params.addBodyParameter("subject", "test");
		params.addBodyParameter("body", "this is a test");
		params.addBodyParameter("channel", channel);
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL_CHARGE, params, new RequestCallBack<String>() {
	        @Override
	        public void onSuccess(ResponseInfo<String> responseInfo) {
				log(responseInfo.result);
				pay(responseInfo.result);
	        }

	        @Override
	        public void onFailure(HttpException error, String msg) {
	        	log(msg);
	        }
		});
	}
	
	private void pay(String charge) {
		Intent intent = new Intent();
		String packageName = getPackageName();
		ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
		intent.setComponent(componentName);
		intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
		startActivityForResult(intent, REQUEST_CODE_PAYMENT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                log(result + "\n" + errorMsg + "\n" + extraMsg);
            }
        }
	}
	
	private void log(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
