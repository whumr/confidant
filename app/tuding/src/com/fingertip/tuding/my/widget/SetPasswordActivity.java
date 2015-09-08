package com.fingertip.tuding.my.widget;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseNavActivity;
import com.fingertip.tuding.util.http.UserUtil;
import com.fingertip.tuding.util.http.callback.DefaultCallback;

public class SetPasswordActivity extends BaseNavActivity implements View.OnClickListener {
	
	private TextView set_password_btn;
	private EditText set_password_old, set_password_new, set_password_new2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_password);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		set_password_btn = (TextView) findViewById(R.id.set_password_btn);
		set_password_old = (EditText) findViewById(R.id.set_password_old);
		set_password_new = (EditText) findViewById(R.id.set_password_new);
		set_password_new2 = (EditText) findViewById(R.id.set_password_new2);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(getString(R.string.set_password));
		set_password_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.set_password_btn:
			setPassword();
			break;
		}
	}
	
	private void setPassword() {
		String old_pwd = set_password_old.getText().toString();
		String new_pwd1 = set_password_new.getText().toString();
		String new_pwd2 = set_password_new2.getText().toString();
		String msg = null;
		if (old_pwd.length() <= 0)
			msg = "请输入旧密码";
		else if (new_pwd1.length() <= 0)
			msg = "请输入新密码";
		else if (new_pwd2.length() <= 0)
			msg = "请输入确认新密码";
		else if (!new_pwd1.equals(new_pwd2))
			msg = "两次输入的新密码不一致";
		if (msg != null)
			toastShort(msg);
		else {
			showProgressDialog(false);
			UserUtil.resetPassword(old_pwd, new_pwd1, new DefaultCallback() {
				@Override
				public void succeed() {
					dismissProgressDialog();
					toastShort("修改成功");
					finish();
				}
				
				@Override
				public void fail(String error) {
					dismissProgressDialog();
					toastShort(error);
				}
			});
		}
	}
}
