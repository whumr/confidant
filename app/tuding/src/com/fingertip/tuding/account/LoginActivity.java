package com.fingertip.tuding.account;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseNavActivity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.util.LocationUtil;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.UserUtil;
import com.fingertip.tuding.util.http.callback.JsonCallback;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;

public class LoginActivity extends BaseNavActivity implements View.OnClickListener {
	
	private TextView tv_register;
	private TextView tv_forgetPwd;
	private TextView btn_submit;
	private EditText et_account;
	private EditText et_pwd;
	private ImageView iv_login_back;
	
	private RelativeLayout relativeLayout;
	private SharedPreferenceUtil sp;
	
	public static String KEY_BACK = "back";
	private boolean can_go_back = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViews();
		setupViews();
	}

	protected void findViews() {
		super.findViews();
		tv_register = (TextView)findViewById(R.id.tv_register);
		tv_forgetPwd = (TextView)findViewById(R.id.tv_hint);
		btn_submit = (TextView)findViewById(R.id.btn_submit);
		et_account = (EditText)findViewById(R.id.et_phonenum);
		et_pwd = (EditText)findViewById(R.id.et_pwd);
		iv_login_back=(ImageView) findViewById(R.id.iv_login_back);
		//½«µÇÂ½¿òµÄNavTitleÒþ²Ø
		relativeLayout=(RelativeLayout) findViewById(R.id.relayout_nav_title);
		relativeLayout.setVisibility(View.GONE);
	}

	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(getString(R.string.account_login));
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(KEY_BACK))
			can_go_back = intent.getBooleanExtra(KEY_BACK, false);
		if (!can_go_back)
			back_btn.setVisibility(View.GONE);
		
		sp = new SharedPreferenceUtil(this);
		String last_account = sp.getStringValue(SharedPreferenceUtil.LAST_UID);
		et_account.setText(last_account == null ? "" : last_account);
		
		//ÏÂ»®Ïß
//		tv_forgetPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_forgetPwd.setOnClickListener(this);
		tv_register.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
		iv_login_back.setOnClickListener(this);
		
		et_account.addTextChangedListener(new TextWatcher() {		
			private String string_before = "";
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(et_account.getText().toString().length() > 11){
					et_account.setText(string_before);
					et_account.setSelection(start);
					toastShort(getResources().getString(R.string.enterElevenPhone));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				string_before = et_account.getText().toString();
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		et_pwd.addTextChangedListener(new TextWatcher() {	
			private String string_before = "";
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(et_pwd.getText().toString().length() > 12){
					et_pwd.setText(string_before);
					et_pwd.setSelection(start);
					toastShort(getResources().getString(R.string.enterRightPwd));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				string_before = et_pwd.getText().toString();
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		et_pwd.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive())
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

					login();
				}
				return false;
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		Intent intent;
		int id = v.getId();
		switch (id) {
		case R.id.tv_hint:
			intent = new Intent();
			intent.putExtra(RegisterActivity.RESET_PASSWORD, true);
			intent.setClass(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_register:
			intent = new Intent();
			intent.setClass(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_submit:
			
			login();
			break;
		case R.id.iv_login_back:
			finish();
		}
	}
	
	private void login() {
		final String account = et_account.getText().toString();
		String password = et_pwd.getText().toString();
		if (!Validator.isMobilePhone(account))
			toastShort(getResources().getString(R.string.enterElevenPhone));
		else if (!Validator.isPassword(password))
			toastShort(getResources().getString(R.string.enterRightPwd));
		else {
			Location location = LocationUtil.getLocation(this);
			showProgressDialog(false);
			UserUtil.Login(account, password, location, new JsonCallback() {
				@Override
				public void succeed(JSONObject json) {
					dismissProgressDialog();
					try {
						String login_id = json.getString(PARAM_KEYS.LOGINID);
						loginSucceed(account, login_id);
					} catch (JSONException e) {
						toastShort("µÇÂ¼Ê§°Ü");
					}
				}
				
				@Override
				public void fail(String error) {
					toastShort(error);
					dismissProgressDialog();
				}
			});
		}
	}
	
	private void loginSucceed(String user_id, String login_id) {
		sp.setStringValue(SharedPreferenceUtil.LAST_UID, user_id);
		sp.setStringValue(SharedPreferenceUtil.LAST_LOGIN_ID, login_id);
		UserSession session = UserSession.getInstance();
		session.setLogin(true);
		session.setId(user_id);
		session.setLogin_id(login_id);
		finish();
	}
}

