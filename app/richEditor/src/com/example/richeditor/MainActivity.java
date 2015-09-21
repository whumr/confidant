package com.example.richeditor;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.richeditor.RichEditText.EditorListner;

public class MainActivity extends Activity implements OnClickListener {

	TextView txt, img_txt, bold_txt, big_txt, color_txt;
	RichEditText edt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		edt = (RichEditText) findViewById(R.id.edt);
		txt = (TextView) findViewById(R.id.txt);
		img_txt = (TextView) findViewById(R.id.img_txt);
		bold_txt = (TextView) findViewById(R.id.bold_txt);
		big_txt = (TextView) findViewById(R.id.big_txt);
		color_txt = (TextView) findViewById(R.id.color_txt);
		img_txt.setOnClickListener(this);
		bold_txt.setOnClickListener(this);
		big_txt.setOnClickListener(this);
		color_txt.setOnClickListener(this);
		
//		edt.setText(Html.fromHtml("<font color=\"red\"><b>th<big>i</big>s</b> is a <big>test</big></font>xxx"));
		edt.setEditorListner(new EditorListner() {
			@Override
			public void afterEdit(String str) {
				txt.setText(Html.toHtml(edt.getText()));
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.img_txt:
			break;
		case R.id.bold_txt:
			if (!edt.isFont_bold()) {
				edt.setFont_bold(true);
				bold_txt.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
			} else {
				edt.setFont_bold(false);
				bold_txt.setBackgroundColor(getResources().getColor(android.R.color.white));
			}
			break;
		case R.id.big_txt:
			if (!edt.isFont_big()) {
				edt.setFont_big(true);
				big_txt.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
			} else {
				edt.setFont_big(false);
				big_txt.setBackgroundColor(getResources().getColor(android.R.color.white));
			}
			break;
		case R.id.color_txt:
			if (RichEditText.COLOR_BLACK.equals(edt.getFont_color())) {
				edt.setFont_color(RichEditText.COLOR_BLUE);
				color_txt.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
			} else {
				edt.setFont_color(RichEditText.COLOR_BLACK);
				color_txt.setBackgroundColor(getResources().getColor(android.R.color.white));
			}
			break;
		}
	}
}
