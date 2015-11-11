package com.fingertip.tuding.main.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import com.fingertip.tuding.R;
import com.fingertip.tuding.main.overlay.OverlayBigActivity;

public class SignedMenuDialog extends Dialog implements OnClickListener {

	private Button cert_btn, quit_btn, cancel_btn;
	private int screen_width, screen_height;
	private OverlayBigActivity activity;
	
	public SignedMenuDialog(OverlayBigActivity context, int screen_width, int screen_height) {
		super(context, R.style.commonDialogStyle);
		this.activity = context;
		this.screen_width = screen_width;
		this.screen_height = screen_height;
		setupView();
	}

	@SuppressLint("InflateParams")
	private void setupView() {
		View view = LayoutInflater.from(activity).inflate(R.layout.dialog_sign_menu, null);
		setContentView(view, new LayoutParams(screen_width, (int)(screen_height * 0.3)));
		cert_btn = (Button)findViewById(R.id.cert_btn);
		quit_btn = (Button)findViewById(R.id.quit_btn);
		cancel_btn = (Button)findViewById(R.id.cancel_btn);
		cert_btn.setOnClickListener(this);
		quit_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cert_btn:
			activity.showSignCert();
			break;
		case R.id.quit_btn:
			activity.cancelSign();
			break;
		case R.id.cancel_btn:
			dismiss();
			break;
		}
	}
}
