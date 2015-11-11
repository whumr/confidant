package com.fingertip.tuding.my;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseFragmentActivity;
import com.fingertip.tuding.my.widget.Deleteable;
import com.fingertip.tuding.my.widget.MyEventFragment;
import com.fingertip.tuding.my.widget.MyFavorEventFragment;
import com.fingertip.tuding.my.widget.MySignedEventFragment;

public class MyEventActivity extends BaseFragmentActivity implements View.OnClickListener {
	
	private ImageView iv_back;
	private ImageView iv_delete;
	private TextView tv_cancel;
	private Button btn_delete;
	private RadioButton radio_my_publish, radio_my_favor, radio_my_signed;
	
	private MyEventFragment my_event_fragment;
	private MyFavorEventFragment my_favor_event_fragment;
	private MySignedEventFragment my_signed_event_fragment;
	
	private Deleteable deleteable;
	private boolean delete = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_events);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_delete = (ImageView) findViewById(R.id.iv_delete);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		btn_delete = (Button) findViewById(R.id.btn_delete);
		radio_my_publish = (RadioButton) findViewById(R.id.radio_my_publish);
		radio_my_favor = (RadioButton) findViewById(R.id.radio_my_favor);
		radio_my_signed = (RadioButton) findViewById(R.id.radio_my_signed);
	}
	
	protected void setupViews() {
		iv_back.setOnClickListener(this);
		iv_delete.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
		btn_delete.setBackgroundColor(getResources().getColor(R.color.gray_ad));
		radio_my_publish.setOnClickListener(this);
		radio_my_favor.setOnClickListener(this);
		radio_my_signed.setOnClickListener(this);
		
		if (my_event_fragment == null)
			my_event_fragment = new MyEventFragment(this);
		getSupportFragmentManager().beginTransaction().replace(R.id.layout_tabcontent, my_event_fragment).commit();
		deleteable = my_event_fragment;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (delete)
					cancelDelete();
				else
					finish();
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_delete:
			beginDelete();
			break;
		case R.id.tv_cancel:
			cancelDelete();
			break;
		case R.id.btn_delete:
			doDelete();
			break;
		case R.id.radio_my_publish:
			if (my_event_fragment == null)
				my_event_fragment = new MyEventFragment(this);
			getSupportFragmentManager().beginTransaction().replace(R.id.layout_tabcontent, my_event_fragment).commit();
			setDeleteable(my_event_fragment);
			break;
		case R.id.radio_my_signed:
			if (my_signed_event_fragment == null)
				my_signed_event_fragment = new MySignedEventFragment(this);
			getSupportFragmentManager().beginTransaction().replace(R.id.layout_tabcontent, my_signed_event_fragment).commit();
			setDeleteable(null);
			break;
		case R.id.radio_my_favor:
			if (my_favor_event_fragment == null)
				my_favor_event_fragment = new MyFavorEventFragment(this);
			getSupportFragmentManager().beginTransaction().replace(R.id.layout_tabcontent, my_favor_event_fragment).commit();
			setDeleteable(my_favor_event_fragment);
			break;
		}
	}
	
	private void setDeleteable(Deleteable deleteable) {
		cancelDelete();
		this.deleteable = deleteable;
		if (deleteable == null)
			iv_delete.setVisibility(View.GONE);
	}
	
	private void beginDelete() {
		if (deleteable.size() > 0) {
			iv_delete.setVisibility(View.GONE);
			tv_cancel.setVisibility(View.VISIBLE);
			btn_delete.setVisibility(View.VISIBLE);
			deleteable.begainDelete();
			delete = true;
		} else
			toastShort("�����ݿ�ɾ��");
	}
	
	private void cancelDelete() {
		iv_delete.setVisibility(View.VISIBLE);
		tv_cancel.setVisibility(View.GONE);
		btn_delete.setVisibility(View.GONE);
		if (deleteable != null)
			deleteable.endDelete();
		delete = false;
	}
	
	private void doDelete() {
		if (deleteable.selectedSize() > 0)
			deleteable.doDelete();
		else
			toastShort("������ѡ��һ��");
	}
	
	public void finishDelete() {
		iv_delete.setVisibility(View.VISIBLE);
		tv_cancel.setVisibility(View.GONE);
		btn_delete.setVisibility(View.GONE);
		deleteable.endDelete();
		delete = false;
		toastShort("ɾ���ɹ�");
	}
	
	public void setSelectedCount(int count) {
		if (count > 0) {
			btn_delete.setText("(" + count + ") ɾ��");
			btn_delete.setBackgroundColor(getResources().getColor(R.color.blue_msg));
		} else {
			btn_delete.setText("ɾ��");
			btn_delete.setBackgroundColor(getResources().getColor(R.color.gray_ad));
		}
	}
	
	@Override
	protected void setPageCount() {
		_count = true;
	}
}
