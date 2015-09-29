package com.fingertip.tuding.main.widget;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

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
		getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL);
	    getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		setContentView(R.layout.dialog_publiccrecoomend);
		setupViews();
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); 
		layoutParams.gravity = Gravity.BOTTOM;
		layoutParams.width = getResources().getDisplayMetrics().widthPixels;
		initData();
	}

	private void setupViews() {
		et_content = (EditText)findViewById(R.id.et_content);
		
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
		if (commentEntity != null)
			et_content.setHint("�ظ��û�" + commentEntity.userEntity.nick_name + "�����ۣ�");
		else
			et_content.setHint("����������");
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
