package com.fingertip.tuding.main.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.util.http.UserUtil;
import com.google.zxing.WriterException;

public class SignCertDialog extends Dialog implements OnClickListener {

	private TextView close_txt, title_txt;
	private ImageView barcode_img;
	private Button save_btn;
	private int screen_width, screen_height;
	private Context context;
	private EventEntity event;
	
	public SignCertDialog(Context context, int screen_width, int screen_height, EventEntity event) {
		super(context, R.style.commonDialogStyle);
		this.context = context;
		this.screen_width = screen_width;
		this.screen_height = screen_height;
		this.event = event;
		setupView();
	}

	@SuppressLint("InflateParams")
	private void setupView() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_sign_cert, null);
		setContentView(view, new LayoutParams((int)(screen_width * 0.9), (int)(screen_height * 0.8)));
		close_txt = (TextView)findViewById(R.id.close_txt);
		title_txt = (TextView)findViewById(R.id.card_txt);
		barcode_img = (ImageView)findViewById(R.id.barcode_img);
		save_btn = (Button)findViewById(R.id.save_btn);
		close_txt.setOnClickListener(this);
		save_btn.setOnClickListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		title_txt.setText(event.title);
		try {
			barcode_img.setImageBitmap(UserUtil.createQRCode(UserSession.getInstance().getId(), 500));
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.close_txt:
			dismiss();
			break;
		case R.id.save_btn:
			dismiss();
			break;
		}
	}
}
