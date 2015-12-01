package com.fingertip.tuding.my;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fingertip.tuding.R;
import com.fingertip.tuding.util.http.common.ServerConstants;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.pingplusplus.android.PaymentActivity;

public class TestpayActivity extends Activity implements OnClickListener {
	
	static String URL_CHARGE = "http://tutuapp.aliapp.com/pingpp-php/ppay/pay.php",
			CHANNEL_ALIPAY = "alipay",
			CHANNEL_WX = "weixin";
	
	static int REQUEST_CODE_PAYMENT = 1000;

	EditText edit;
	Button alipay_btn, wx_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_11);
		
		edit = (EditText) findViewById(R.id.edit);
		alipay_btn = (Button) findViewById(R.id.alipay_btn);
		wx_btn = (Button) findViewById(R.id.wx_btn);
		alipay_btn.setOnClickListener(this);
		wx_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.alipay_btn:
			requestCharge(CHANNEL_ALIPAY);
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
			log("��������ȷ�Ľ��");
			return;
		}
//		"amount", "subject", "body", "channel"
		RequestParams params = new RequestParams();
		params.addBodyParameter("amount", amount + "");
		params.addBodyParameter("channel", channel);
		params.addBodyParameter(ServerConstants.PARAM_KEYS.USERID, "13311111111");
		params.addBodyParameter(ServerConstants.PARAM_KEYS.ACTIONID, "-1");
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
		//֧��ҳ�淵�ش���
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* ������ֵ
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // ������Ϣ
                String extraMsg = data.getExtras().getString("extra_msg"); // ������Ϣ
                log(result + "\n" + errorMsg + "\n" + extraMsg);
            }
        }
	}
	
	private void log(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
