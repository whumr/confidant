package com.fingertip.tuding.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseNavActivity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.util.http.CommonUtil;
import com.fingertip.tuding.util.http.callback.DefaultCallback;

public class SuggestActivity extends BaseNavActivity implements View.OnClickListener {
	
	private TextView content_txt;
	private TextView phone_txt;
	private Button commit_btn;
	
	private UserSession session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggest);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		content_txt = (TextView) findViewById(R.id.suggest_content);
		phone_txt = (TextView) findViewById(R.id.suggest_phone);
		commit_btn = (Button) findViewById(R.id.suggest_commit);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(getString(R.string.suggest_title));
		commit_btn.setOnClickListener(this);
		
		session = UserSession.getInstance();
		if (session.isLogin())
			phone_txt.setText(session.getId());
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.suggest_commit:
			commitSuggestion();
			break;
		}
	}
	
	private void commitSuggestion() {
		String content = content_txt.getText().toString();
		String phone = phone_txt.getText().toString();
		if (content == null || "".equals(content.trim()))
			toastShort("请输入反馈意见");
		else if (phone == null || "".equals(phone.trim()))
			toastShort("请输入联系电话");
		else {
			showProgressDialog(false);
			CommonUtil.commitSuggestion(phone, content, new DefaultCallback() {
				@Override
				public void succeed() {
					toastShort("感谢您的反馈，我们将尽快与您联系");
					dismissProgressDialog();
					finish();						
				}
				
				@Override
				public void fail(String error) {
					toastShort(error);
		        	dismissProgressDialog();
				}
			});
		}
	}
}
