package com.fingertip.tuding.main.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.fingertip.tuding.R;

public class SignedDialog extends Dialog implements OnClickListener {

	private TextView close_txt, card_txt;
	private int screen_width, screen_height;
	private Context context;
	
	public SignedDialog(Context context, int screen_width, int screen_height) {
		super(context, R.style.commonDialogStyle);
		this.context = context;
		this.screen_width = screen_width;
		this.screen_height = screen_height;
		setupView();
	}

	@SuppressLint("InflateParams")
	private void setupView() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_signup, null);
		setContentView(view, new LayoutParams((int)(screen_width * 0.8), (int)(screen_height * 0.5)));
		close_txt = (TextView)findViewById(R.id.close_txt);
		card_txt = (TextView)findViewById(R.id.card_txt);
		close_txt.setOnClickListener(this);
		card_txt.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.close_txt:
			dismiss();
			break;
		case R.id.card_txt:
			dismiss();
			break;
		}
	}
}
