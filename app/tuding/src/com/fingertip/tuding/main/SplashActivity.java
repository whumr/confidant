package com.fingertip.tuding.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.UserUtil;

public class SplashActivity extends BaseActivity {
	
	SharedPreferenceUtil sp;
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			String login_id = sp.getStringValue(SharedPreferenceUtil.LAST_LOGIN_ID);
			String user_id = sp.getStringValue(SharedPreferenceUtil.LAST_UID);
			if (!Validator.isEmptyString(user_id) && !Validator.isEmptyString(login_id)) {
				UserSession session = UserSession.getInstance();
				session.setId(user_id);
				session.setLogin_id(login_id);
				session.setLogin(true);
				UserUtil.loadFavorList();
				UserUtil.loadWatchList();
			}
			Intent intent = new Intent();
			intent.setClass(SplashActivity.this, MainActivity.class);
			startActivity(intent);
			finish();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sp = new SharedPreferenceUtil(this);
		handler.sendEmptyMessageDelayed(0, 500);
	}

}
