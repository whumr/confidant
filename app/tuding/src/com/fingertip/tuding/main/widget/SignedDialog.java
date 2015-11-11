package com.fingertip.tuding.main.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.main.overlay.OverlayBigActivity;

public class SignedDialog extends Dialog implements OnClickListener {

	private TextView card_txt;
	private ImageView close_img;
	private int screen_width, screen_height;
	private OverlayBigActivity activity;
	
	public SignedDialog(OverlayBigActivity context, int screen_width, int screen_height) {
		super(context, R.style.commonDialogStyle);
		this.activity = context;
		this.screen_width = screen_width;
		this.screen_height = screen_height;
		setupView();
	}

	@SuppressLint("InflateParams")
	private void setupView() {
		View view = LayoutInflater.from(activity).inflate(R.layout.dialog_signup, null);
		setContentView(view, new LayoutParams((int)(screen_width * 0.8), (int)(screen_height * 0.5)));
		close_img = (ImageView)findViewById(R.id.close_img);
		card_txt = (TextView)findViewById(R.id.card_txt);
		close_img.setOnClickListener(this);
		card_txt.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.close_img:
			dismiss();
			break;
		case R.id.card_txt:
			activity.showSignCert();
			break;
		}
	}
}
