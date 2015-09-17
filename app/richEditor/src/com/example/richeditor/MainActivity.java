package com.example.richeditor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	TextView txt, img_txt, bold_txt, big_txt, color_txt;
	EditText edt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		edt = (EditText) findViewById(R.id.edt);
		txt = (TextView) findViewById(R.id.txt);
		img_txt = (TextView) findViewById(R.id.img_txt);
		bold_txt = (TextView) findViewById(R.id.bold_txt);
		big_txt = (TextView) findViewById(R.id.big_txt);
		color_txt = (TextView) findViewById(R.id.color_txt);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.img_txt:
			break;
		case R.id.bold_txt:
			break;
		case R.id.big_txt:
			break;
		case R.id.color_txt:
			break;
		}
	}
}
