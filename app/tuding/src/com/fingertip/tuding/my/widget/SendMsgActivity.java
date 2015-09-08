package com.fingertip.tuding.my.widget;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fingertip.tuding.Globals;
import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.util.Validator;

public class SendMsgActivity extends BaseActivity implements View.OnClickListener {
	
	private TextView set_text_title, set_text_save_btn;
	private EditText set_text_content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_text);
		findViews();
		setupViews();
	}
	
	private void findViews() {
		set_text_title = (TextView) findViewById(R.id.set_text_title);
		set_text_save_btn = (TextView) findViewById(R.id.set_text_save_btn);
		set_text_content = (EditText) findViewById(R.id.set_text_content);
	}
	
	private void setupViews() {
		set_text_title.setText(getString(R.string.send_msg));
		set_text_save_btn.setText(getString(R.string.send));
		set_text_save_btn.setOnClickListener(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				finish();
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		String result = null;
		switch (id) {
		case R.id.set_text_save_btn:
			result = set_text_content.getText().toString().trim();
			//�����ǳƵ�������
			if (Validator.isEmptyString(result))
				toastShort("���ܷ��Ϳ���Ϣ");
			else {
				Intent intent = new Intent();
				intent.putExtra(Globals.COMMON_RESULT, result);
				setResult(RESULT_OK, intent);
				finish();
			}
			break;
		}
	}
}
