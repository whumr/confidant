package com.fingertip.tuding.account;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseNavActivity;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.common.ServerConstants;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_VALUES;
import com.fingertip.tuding.util.http.common.ServerConstants.URL;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class RegisterActivity extends BaseNavActivity implements
		View.OnClickListener {

	public static String RESET_PASSWORD = "reset_password";

	private EditText et_account, et_password;
	private TextView tv_next;
	private boolean reset_pwd;
	// 发送验证码模块
	private Button btn_send_emscode;
	private TimeCount time;// 倒计时
	private EditText et_emscode;// 验证码输入框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initData();
		findViews();
		setupViews();
	}

	protected void findViews() {
		super.findViews();
		et_account = (EditText) findViewById(R.id.et_phonenum);
		tv_next = (TextView) findViewById(R.id.btn_next);

		et_emscode = (EditText) findViewById(R.id.et_emscode);
		et_password = (EditText) findViewById(R.id.et_password);
		// 验证码倒计时
		time = new TimeCount(60000, 1000);
		btn_send_emscode = (Button) findViewById(R.id.btn_send_emscode);
		btn_send_emscode.setOnClickListener(this);
	}

	protected void setupViews() {
		super.setupViews();
		if (reset_pwd)
			nav_title_txt.setText(getString(R.string.account_reset_pwd));
		else
			nav_title_txt.setText(getString(R.string.account_reg));
		tv_next.setOnClickListener(this);
		if (reset_pwd)
			tv_next.setText(getString(R.string.find_password));
		et_account.addTextChangedListener(new TextWatcher() {
			private String string_before = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (et_account.getText().toString().length() > 11) {
					et_account.setText(string_before);
					et_account.setSelection(start);
					toastShort(getResources().getString(
							R.string.enterElevenPhone));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				string_before = et_account.getText().toString();
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void initData() {
		reset_pwd = getIntent().getBooleanExtra(RESET_PASSWORD, false);
	}

	@Override
	public void onClick(View v) {
		String account = et_account.getText().toString().trim();
		int id = v.getId();
		switch (id) {

		case R.id.btn_send_emscode:
			if (TextUtils.isEmpty(account) || !Validator.isMobilePhone(account)) {
				toastShort(getResources().getString(R.string.enterElevenPhone)+"55555");
			} else {

				if (reset_pwd) {
					time.start();
					sendrestpasswordmsg();
					Toast.makeText(this, "验证码已发送,请查看并输入您所接收到的短信重置验证码",
							Toast.LENGTH_SHORT).show();
				} else {
					time.start();
					sendMsg();
					Toast.makeText(this, "验证码已发送,请查看并输入您所接收到的手机短信验证码",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.btn_next:
			if (reset_pwd) {
				resetPassword();
			} else {
				checkMsg();
			}
			break;
		}
	}

	/** 检验验证码是否正确 **/
	private void checkMsg() {
		final String account = et_account.getText().toString();
		final String msgCode = et_emscode.getText().toString().trim();
		final String password = et_password.getText().toString().trim();
		if (!Validator.isMobilePhone(account)) {
			toastShort(getResources().getString(R.string.enterElevenPhone));
			
				
		} else {
			if(!TextUtils.isEmpty(password)){
				if(password.length()<6){
					toastShort("密码不能小于6位");
				}else if (password.length()>12) {
					toastShort("密码不能大于12位");
				}
			}else {
				toastShort("密码不能为空");
			}
			JSONObject data = new JSONObject();
			// {"fc":"reg_phone_password_verify", "phone":"18979528420",
			// "pass":"123456", "verifycode":"2155"}
			try {
				data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_REG_PHONE_PASSWORD);
				data.put(PARAM_KEYS.PHONE, account);
				data.put(PARAM_KEYS.MSGCODE, msgCode);
				if(!msgCode.equals(data.getString(PARAM_KEYS.MSGCODE))){
					toastShort("hahaha");
				}else {
					if(!TextUtils.isEmpty(password)){
						if(password.length()<6){
							toastShort("密码不能小于6位");
						}else if (password.length()>12) {
							toastShort("密码不能大于12位");
						}else {
							
							data.put(PARAM_KEYS.PASSWORD, password);
						}
						
					}else {
						toastShort("密码不能为空");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				System.out.println("报异常啦11111111111111111111111111111111111");
			}
			RequestParams params = new RequestParams();
			params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
			HttpUtils http = Tools.getHttpUtils();
			http.send(HttpRequest.HttpMethod.POST, URL.CHECK_SENDMSG, params,
					new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String result = Tools.decodeString(responseInfo.result);
							String error = null;
							try {
								JSONObject json = new JSONObject(result);
								if (PARAM_VALUES.RESULT_FAIL.equals(json
										.getString(PARAM_KEYS.RESULT_STATUS)))
									error = json
											.getString(PARAM_KEYS.RESULT_ERROR);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							if (error != null)
								toastShort(error);
							 else {
								 finish();
							 }
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							toastShort(ServerConstants.NET_ERROR_TIP);
						}

					});
		}

	}

	/** 倒计时 **/
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			btn_send_emscode.setClickable(false);
			btn_send_emscode.setBackgroundResource(R.drawable.btn_emscode_p);
			btn_send_emscode.setText("(" + millisUntilFinished / 1000 + ")"
					+ " 重新获取");
		}

		@Override
		public void onFinish() {

			btn_send_emscode.setBackgroundResource(R.drawable.btn_emscode_n);
			btn_send_emscode.setText("发送验证码");
			btn_send_emscode.setClickable(true);
		}

	}

	private void sendMsg() {
		final String account = et_account.getText().toString();
		if (!Validator.isMobilePhone(account))
			toastShort(getResources().getString(R.string.enterElevenPhone));
		else {
			JSONObject data = new JSONObject();
			// {"fc":"user_reg_sendmsg", "phone":"18979528420"}
			try {
				data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_REG_SENDMSG);
				data.put(PARAM_KEYS.PHONE, account);
			} catch (JSONException e) {
			}
			RequestParams params = new RequestParams();
			params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
			HttpUtils http = Tools.getHttpUtils();
			http.send(HttpRequest.HttpMethod.POST, URL.REG_SENDMSG, params,
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String result = Tools.decodeString(responseInfo.result);
							String error = null;
							try {
								JSONObject json = new JSONObject(result);
								if (PARAM_VALUES.RESULT_FAIL.equals(json
										.getString(PARAM_KEYS.RESULT_STATUS)))
									error = json
											.getString(PARAM_KEYS.RESULT_ERROR);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							if (error != null)
								toastShort(error);
							 else {
							 }
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							toastShort(ServerConstants.NET_ERROR_TIP);
						}
					});
		}
	}

	private void resetPassword() {
		final String account = et_account.getText().toString();
		final String msgCode = et_emscode.getText().toString().trim();
		final String password = et_password.getText().toString().trim();
		if (!Validator.isMobilePhone(account)) {
			toastShort(getResources().getString(R.string.enterElevenPhone));
			
				
		} else {
			if(!TextUtils.isEmpty(password)){
				if(password.length()<6){
					toastShort("密码不能小于6位");
				}else if (password.length()>12) {
					toastShort("密码不能大于12位");
				}
			}else {
				toastShort("密码不能为空");
			}
			JSONObject data = new JSONObject();
			// {"fc":"reset_phone_password_verify", "phone":"18979528420",
			// "pass":"123456", "verifycode":"2155"}
			try {
				
				data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_RESET_PHONE_PASSWPRD);
				data.put(PARAM_KEYS.PHONE, account);
				data.put(PARAM_KEYS.MSGCODE, msgCode);
				if(!msgCode.equals(data.getString(PARAM_KEYS.MSGCODE))){
				}else {
							data.put(PARAM_KEYS.PASSWORD, password);
						}
					
				
			} catch (JSONException e) {
				e.printStackTrace();
				System.out.println("报异常222222222");
			}
			RequestParams params = new RequestParams();
			params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
			HttpUtils http = Tools.getHttpUtils();
			http.send(HttpRequest.HttpMethod.POST, URL.RESET_UESRPASSWORD,
					params, new RequestCallBack<String>() {
						

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String result = Tools.decodeString(responseInfo.result);
							String error = null;
							try {
								JSONObject json = new JSONObject(result);
							
								if (PARAM_VALUES.RESULT_FAIL.equals(json
										.getString(PARAM_KEYS.RESULT_STATUS))){
									error = json
											.getString(PARAM_KEYS.RESULT_ERROR);}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							if (error != null)
								toastShort(error);
							else {
								
								finish();
							}
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							toastShort(ServerConstants.NET_ERROR_TIP);
						}
					});
		}
	}

	/** 找回密码的发送验证码 **/
	private void sendrestpasswordmsg() {
		final String account = et_account.getText().toString();
		if (!Validator.isMobilePhone(account)) {
			toastShort(getResources().getString(R.string.enterElevenPhone));
		} else {
			JSONObject data = new JSONObject();
			// {"fc":"user_pass_reset", "userid":"18979528420"}
			try {
				data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_RESET_PASSWORD);
				data.put(PARAM_KEYS.PHONE, account);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			RequestParams params = new RequestParams();
			params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
			HttpUtils http = Tools.getHttpUtils();
			http.send(HttpRequest.HttpMethod.POST, URL.RESET_PASSWORD, params,
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String result = Tools.decodeString(responseInfo.result);
							String error = null;
							try {
								JSONObject json = new JSONObject(result);
								if (PARAM_VALUES.RESULT_FAIL.equals(json
										.getString(PARAM_KEYS.RESULT_STATUS)))
									error = json
											.getString(PARAM_KEYS.RESULT_ERROR);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							if (error != null)
								toastShort(error);
							
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							toastShort(ServerConstants.NET_ERROR_TIP);
						}
					});
	}
	}
}
