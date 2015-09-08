package com.fingertip.tuding.main.widget;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.entity.CommentEntity;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.util.http.EventUtil;
import com.fingertip.tuding.util.http.callback.DefaultCallback;

public class PublicRecommendActivity extends BaseActivity{
	public static final String EXTRA_COMMENT = "extra_comment";
	
	private EditText et_content;
	private EventEntity event;
	/** ��Ϊ���ۻظ����ñ�����Ϊ�� **/
	private CommentEntity commentEntity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publicrecommend);
		setupViews();
		initData();
	}

	private void setupViews() {
		findViewById(R.id.tv_more).setVisibility(View.GONE);
		et_content = (EditText)findViewById(R.id.et_content);
		findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		findViewById(R.id.tv_config).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				String string_content = et_content.getText().toString();
				if(string_content.length() == 0)
					toastShort("��û��д����Ӵ��");
				else if(string_content.length() > 300)
					toastShort("�ѳ���300�֣���������");
				else {
					showProgressDialog(false);
					if(commentEntity != null)
						requestPublicRecommendReply();
					else
						requestPublicRecommend();
				}
			}
		});
	}

	private void initData(){
		event = (EventEntity) getIntent().getSerializableExtra(EXTRA_PARAM);
		if (event == null){
			toastShort("���ݴ���");
			finish();
		}
		commentEntity = (CommentEntity) getIntent().getSerializableExtra(EXTRA_COMMENT);
		TextView tv_title = (TextView)findViewById(R.id.tv_title);
		if(commentEntity != null)
			tv_title.setText("���ۻظ�");
		else 
			tv_title.setText("��������");
	}
	
	/** �������� **/
	private void requestPublicRecommend(){
		EventUtil.pubEventComment(event.id, et_content.getText().toString(), new DefaultCallback() {
			@Override
			public void succeed() {
				dismissProgressDialog();
				toastShort("�������ɹ�");
				finish();
			}
			
			@Override
			public void fail(String error) {
				dismissProgressDialog();
				toastShort(error);
			}
		});
	}
	
	/** ���ۻظ� **/
	private void requestPublicRecommendReply(){
		EventUtil.replyEventComment(event.id, commentEntity.id, et_content.getText().toString(), new DefaultCallback() {
			@Override
			public void succeed() {
				dismissProgressDialog();
				toastShort("���ۻظ��ɹ�");
				finish();
			}
			
			@Override
			public void fail(String error) {
				dismissProgressDialog();
				toastShort(error);
			}
		});
	}
}