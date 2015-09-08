package com.fingertip.tuding.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseNavActivity;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.CommonUtil;
import com.fingertip.tuding.util.http.callback.DefaultCallback;

public class ReportActivity extends BaseNavActivity implements View.OnClickListener {
	
	private RelativeLayout line1, line2, line3, line4;
	private ImageView check1, check2, check3, check4;
	private TextView text1, text2, text3, text4, content_txt;
	private Button commit_btn;
	private boolean checked1 = false, checked2 = false, checked3 = false, checked4 = false;
	
	private EventEntity event;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		content_txt = (TextView) findViewById(R.id.report_content);
		commit_btn = (Button) findViewById(R.id.report_commit);

		text1 = (TextView) findViewById(R.id.text1);
		text2 = (TextView) findViewById(R.id.text2);
		text3 = (TextView) findViewById(R.id.text3);
		text4 = (TextView) findViewById(R.id.text4);
		check1 = (ImageView) findViewById(R.id.check1);
		check2 = (ImageView) findViewById(R.id.check2);
		check3 = (ImageView) findViewById(R.id.check3);
		check4 = (ImageView) findViewById(R.id.check4);
		line1 = (RelativeLayout) findViewById(R.id.line1);
		line2 = (RelativeLayout) findViewById(R.id.line2);
		line3 = (RelativeLayout) findViewById(R.id.line3);
		line4 = (RelativeLayout) findViewById(R.id.line4);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(getString(R.string.report));
		commit_btn.setOnClickListener(this);
		line1.setOnClickListener(this);
		line2.setOnClickListener(this);
		line3.setOnClickListener(this);
		line4.setOnClickListener(this);
		
		event = (EventEntity) getIntent().getSerializableExtra(EXTRA_PARAM);
		if(event == null) {
			toastShort("数据错误");
			finish();
			return;
		}
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.report_commit:
			commitReport();
			break;
		case R.id.line1:
			checked1 = !checked1;
			checkReason(check1, checked1);
			break;
		case R.id.line2:
			checked2 = !checked2;
			checkReason(check2, checked2);
			break;
		case R.id.line3:
			checked3 = !checked3;
			checkReason(check3, checked3);
			break;
		case R.id.line4:
			checked4 = !checked4;
			checkReason(check4, checked4);
			break;
		}
	}
	
	private void checkReason(ImageView img, boolean checked) {
		if (checked)
			img.setImageResource(R.drawable.icon_checked);
		else
			img.setImageResource(R.drawable.icon_unchecked);
	}
	
	private String getReportReason() {
		StringBuilder buffer = new StringBuilder();
		String result = "";
		buffer.append(checked1 ? text1.getText().toString() + "," : "")
			.append(checked2 ? text2.getText().toString() + "," : "")
			.append(checked3 ? text3.getText().toString() + "," : "")
			.append(checked4 ? text4.getText().toString() + "," : "");
		if (buffer.length() > 0)
			result = "【" + buffer.substring(0, buffer.length() - 1) + "】";
		String content = content_txt.getText().toString();
		if (!Validator.isEmptyString(content))
			result += "," + content;
		return result;
	}
	
	private void commitReport() {
		String content = getReportReason();
		if (Validator.isEmptyString(content))
			toastShort("请至少输入一项举报原因");
		else {
			showProgressDialog(false);
			CommonUtil.report(content, event.id, event.event_type.getType(), new DefaultCallback() {
				@Override
				public void succeed() {
					dismissProgressDialog();
					toastShort("感谢您的举报，我们将尽快核实相关信息");
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
