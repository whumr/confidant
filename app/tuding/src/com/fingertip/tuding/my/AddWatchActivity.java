package com.fingertip.tuding.my;

import java.io.Serializable;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.fingertip.tuding.Globals;
import com.fingertip.tuding.R;
import com.fingertip.tuding.barcode.ScanBarcodeActivity;
import com.fingertip.tuding.barcode.ScanBarcodeActivity.BarcodeValidator;
import com.fingertip.tuding.base.BaseNavActivity;
import com.fingertip.tuding.entity.UserEntity;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.UserUtil;
import com.fingertip.tuding.util.http.callback.EntityCallback;
import com.google.zxing.Result;

public class AddWatchActivity extends BaseNavActivity implements View.OnClickListener {
	
	private EditText search_edit;
	private LinearLayout my_watch_myinfo, my_watch_scan, my_watch_contacts;
	
	private BarcodeValidater barcodeValidater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_watch);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		search_edit = (EditText) findViewById(R.id.my_watch_search_edt);
		my_watch_myinfo = (LinearLayout) findViewById(R.id.my_watch_myinfo);
		my_watch_scan = (LinearLayout) findViewById(R.id.my_watch_scan);
		my_watch_contacts = (LinearLayout) findViewById(R.id.my_watch_contacts);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.my_watch_add);
		
		search_edit.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
					searchUser();
				return false;
			}
		});
		my_watch_myinfo.setOnClickListener(this);
		my_watch_scan.setOnClickListener(this);
		my_watch_contacts.setOnClickListener(this);
		
		barcodeValidater = new BarcodeValidater();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.my_watch_myinfo:
			intent = new Intent();
			intent.setClass(this, MyBarcodeActivity.class);
			break;
		case R.id.my_watch_scan:
			Intent in = new Intent();
			in.setClass(this, ScanBarcodeActivity.class);
			in.putExtra(ScanBarcodeActivity.KEY_VALIDATOR, barcodeValidater);
			startActivityForResult(in, R.id.decode);
			break;
		case R.id.my_watch_contacts:
			intent = new Intent();
			intent.setClass(this, MyContactsActivity.class);
			break;
		}
		if (intent != null)
			startActivity(intent);
	}
	
	private void searchUser() {
		String search_key = search_edit.getText().toString().trim();
		if (Validator.isEmptyString(search_key)) {
			toastShort("�������ֻ�������ǳ�");
		} else {
			showProgressDialog(false);
			UserUtil.getUserInfo(search_key, new EntityCallback<UserEntity>() {
				@Override
				public void succeed(UserEntity user) {
					Tools.openUser(AddWatchActivity.this, user);
					dismissProgressDialog();
				}
				
				@Override
				public void fail(String error) {
					toastShort(error);
					dismissProgressDialog();
				}
			});
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case R.id.decode:
				String result = data.getStringExtra(Globals.COMMON_RESULT);
				String user_id = UserUtil.parseBarcode(result);
				if (user_id != null)
					Tools.openUser(this, user_id);
				else
					Tools.viewWeb(this, result);
				break;
			}
		}
	}
}

class BarcodeValidater implements BarcodeValidator, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean canProcess(Result result) {
		String str = result.getText();
		if (UserUtil.parseBarcode(str) != null || str.startsWith("http://"))
			return true;
		return false;
	}
	
}
