package com.fingertip.tuding.info;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.entity.EventEntity.EventType;
import com.fingertip.tuding.info.widget.RichEditText;
import com.fingertip.tuding.main.widget.DialogDate;
import com.fingertip.tuding.main.widget.DialogDate.OnDateSelectdListener;
import com.fingertip.tuding.main.widget.MapPositionSelectionActivity;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.widget.SelectPicActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PublishEventActivity extends BaseActivity{
	
	private static final int REQUEST_POS = 1000;
	private static final int REQUEST_PIC = 1001;
	public static int MAX_PIC_SIZE = 9;
	
	private TextView tv_submit, tv_title,
		tv_position, tv_type_hint, tv_start_time_hint, tv_end_time_hint,
		tv_special, tv_perform, tv_sociality, tv_sports, tv_study, tv_other;
	private EditText et_title;
	private RichEditText et_content;
	private DialogDate start_time_dialog, end_time_dialog;
	private UserSession session;
	private EventType eventType = null;
	
	private double latitude = 0, longitude = 0;
	
	private TextView img_txt, bold_txt, big_txt, color_txt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publishinfo_customer);
		setupViews();
		initDialog();
	}

	private void setupViews() {
		tv_submit = (TextView)findViewById(R.id.tv_submit);
		tv_position = (TextView)findViewById(R.id.tv_position);
		et_title = (EditText)findViewById(R.id.et_title);
		et_content = (RichEditText)findViewById(R.id.et_content);
		
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("�����");
		
		findViewById(R.id.iv_back).setOnClickListener(onClickListener);
		tv_submit.setOnClickListener(onClickListener);
		
		
		tv_type_hint = (TextView)findViewById(R.id.tv_type_hint);
		tv_start_time_hint = (TextView)findViewById(R.id.tv_start_time_hint);
		tv_end_time_hint = (TextView)findViewById(R.id.tv_end_time_hint);
		tv_type_hint.setOnClickListener(onClickListener);
		tv_start_time_hint.setOnClickListener(onClickListener);
		tv_end_time_hint.setOnClickListener(onClickListener);
		
		tv_special = (TextView)findViewById(R.id.tv_special);
		tv_perform = (TextView)findViewById(R.id.tv_perform);
		tv_sociality = (TextView)findViewById(R.id.tv_sociality);
		tv_sports = (TextView)findViewById(R.id.tv_sports);
		tv_study = (TextView)findViewById(R.id.tv_study);
		tv_other = (TextView)findViewById(R.id.tv_other);
		tv_special.setOnClickListener(onClickListener);
		tv_perform.setOnClickListener(onClickListener);
		tv_sociality.setOnClickListener(onClickListener);
		tv_sports.setOnClickListener(onClickListener);
		tv_study.setOnClickListener(onClickListener);
		tv_other.setOnClickListener(onClickListener);
		tv_position.setOnClickListener(onClickListener);
		setTypeBackground(EventType.SOCIALITY);
		
		session = UserSession.getInstance();
		
		img_txt = (TextView) findViewById(R.id.img_txt);
		bold_txt = (TextView) findViewById(R.id.bold_txt);
		big_txt = (TextView) findViewById(R.id.big_txt);
		color_txt = (TextView) findViewById(R.id.color_txt);
		img_txt.setOnClickListener(onClickListener);
		bold_txt.setOnClickListener(onClickListener);
		big_txt.setOnClickListener(onClickListener);
		color_txt.setOnClickListener(onClickListener);
	}
	
	private void initDialog(){
		start_time_dialog = new DialogDate(this, new OnDateSelectdListener() {
			@Override
			public void onDateSelectd(String time) {
				tv_start_time_hint.setText("���ʼʱ��  (" + start_time_dialog.getTimeString() + ")");
			}
		});
		Window start_window = start_time_dialog.getWindow();
		start_window.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lParams = start_window.getAttributes();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		lParams.width = dm.widthPixels;
		start_window.setAttributes(lParams);

		end_time_dialog = new DialogDate(this, new OnDateSelectdListener() {
			@Override
			public void onDateSelectd(String time) {
				tv_end_time_hint.setText("���ֹʱ��  (" + end_time_dialog.getTimeString() + ")");
			}
		});
		Window end_window = end_time_dialog.getWindow();
		end_window.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lParams1 = end_window.getAttributes();
		DisplayMetrics dm1 = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm1);
		lParams1.width = dm1.widthPixels;
		end_window.setAttributes(lParams1);
	}
	
	private void setTypeBackground(EventType type){
		this.eventType = type;
		int color = getResources().getColor(R.color.transparent);
		int res_drawable_gray = R.drawable.bg_gray_click;
		tv_special.setBackgroundColor(color);
		tv_perform.setBackgroundColor(color);
		tv_sociality.setBackgroundColor(color);
		tv_sports.setBackgroundColor(color);
		tv_study.setBackgroundColor(color);
		tv_other.setBackgroundColor(color);
		switch (eventType) {
		case SPECIAL:
			tv_special.setBackgroundResource(res_drawable_gray);
			break;
		case PERFORM:
			tv_perform.setBackgroundResource(res_drawable_gray);
			break;
		case SOCIALITY:
			tv_sociality.setBackgroundResource(res_drawable_gray);
			break;
		case SPORTS:
			tv_sports.setBackgroundResource(res_drawable_gray);
			break;
		case STUDY:
			tv_study.setBackgroundResource(res_drawable_gray);
			break;
		case OTHER:
			tv_other.setBackgroundResource(res_drawable_gray);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null || resultCode != RESULT_OK)
			return;
		if (requestCode == REQUEST_POS) {
			tv_position.setText(data.getStringExtra(MapPositionSelectionActivity.KEY_ADDRESS));
			latitude = data.getDoubleExtra(MapPositionSelectionActivity.KEY_LAT, 0);
			longitude = data.getDoubleExtra(MapPositionSelectionActivity.KEY_LONG, 0);
		} else if (requestCode == REQUEST_PIC) {
			ArrayList<String> pics = data.getStringArrayListExtra(SelectPicActivity.KEY_PICS);
			if (!Validator.isEmptyList(pics))
				;
		}
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			Context context = PublishEventActivity.this;
			int v_id = v.getId();
			
			switch (v_id) {
			case R.id.iv_back:
				finish();
				break;
			case R.id.tv_submit:
				publicActivity();
				break;
			case R.id.tv_position:
				Tools.choosePosition(PublishEventActivity.this, REQUEST_POS);
				break;
			case R.id.tv_start_time_hint:
				start_time_dialog.show();
				break;
			case R.id.tv_end_time_hint:
				end_time_dialog.show();
				break;
			case R.id.tv_type_hint:
				if (findViewById(R.id.layout_type).getVisibility() == View.VISIBLE){
					findViewById(R.id.layout_type).setVisibility(View.GONE);
					tv_type_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_top), null);
					tv_type_hint.setText("�����(" + eventType.getType() + ")");
				} else {
					findViewById(R.id.layout_type).setVisibility(View.VISIBLE);
					tv_type_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_down), null);					
					tv_type_hint.setText("�����");
				}
				break;
//				img_txt = (TextView) findViewById(R.id.img_txt);
//				bold_txt = (TextView) findViewById(R.id.bold_txt);
//				big_txt = (TextView) findViewById(R.id.big_txt);
//				color_txt = (TextView) findViewById(R.id.color_txt);
			case R.id.img_txt:
				break;
			case R.id.bold_txt:
				if (!et_content.isFont_bold()) {
					et_content.setFont_bold(true);
					bold_txt.setBackgroundColor(getResources().getColor(R.color.blue_msg));
				} else {
					et_content.setFont_bold(false);
					bold_txt.setBackgroundColor(getResources().getColor(R.color.transparent));
				}
				break;
			case R.id.big_txt:
				if (!et_content.isFont_big()) {
					et_content.setFont_big(true);
					big_txt.setBackgroundColor(getResources().getColor(R.color.blue_msg));
				} else {
					et_content.setFont_big(false);
					big_txt.setBackgroundColor(getResources().getColor(R.color.transparent));
				}
				break;
			case R.id.color_txt:
				if (RichEditText.COLOR_BLACK == et_content.getFont_color()) {
					et_content.setFont_color(RichEditText.COLOR_BLUE);
					color_txt.setBackgroundColor(getResources().getColor(R.color.blue_msg));
				} else {
					et_content.setFont_color(RichEditText.COLOR_BLACK);
					color_txt.setBackgroundColor(getResources().getColor(R.color.transparent));
				}
				break;
			case R.id.tv_special:
				setTypeBackground(EventType.SPECIAL);
				break;
			case R.id.tv_perform:
				setTypeBackground(EventType.PERFORM);
				break;
			case R.id.tv_sociality:
				setTypeBackground(EventType.SOCIALITY);
				break;
			case R.id.tv_sports:
				setTypeBackground(EventType.SPORTS);
				break;
			case R.id.tv_study:
				setTypeBackground(EventType.STUDY);
				break;
			case R.id.tv_other:
				setTypeBackground(EventType.OTHER);
				break;
			default:
				break;
			}
		}
	};
	
	/** ����� **/
	private void publicActivity(){
		//����
		final String title = et_title.getText().toString().trim();
		if (Validator.isEmptyString(title)) {
			toastShort("����������");
			return;
		}
		//����
		final String content = et_content.getText().toString().trim();
		if (Validator.isEmptyString(content)) {
			toastShort("����������");
			return;
		}
		//����
		final String type = eventType.getType();
		if (Validator.isEmptyString(type)) {
			toastShort("��ѡ������");
			return;
		}
		//��ʼʱ��
		final String start_time = start_time_dialog.getTimeString();
		if (Validator.isEmptyString(tv_start_time_hint.getText().toString().trim())) {
			toastShort("��ѡ����ʼʱ��");
			return;
		}
		//��ֹʱ��
		final String end_time = end_time_dialog.getTimeString();
		if (Validator.isEmptyString(tv_end_time_hint.getText().toString().trim())) {
			toastShort("��ѡ����ֹʱ��");
			return;
		}
		//����
		final String address = tv_position.getText().toString();
		if (Validator.isEmptyString(address) || latitude == 0 || longitude == 0) {
			toastShort("���ǻλ��");
			return;
		}
		//�����
		showProgressDialog(false);
		//���ϴ�ͼƬ
//		final List<UploadImgEntity> entitys = new ArrayList<UploadImgEntity>();
//		UploadUtil.uplodaImg(pics, entitys, new UploadCallback() {
//			
//			@Override
//			public void succeed() {
//				EventUtil.publishEvent(title, content, type, address, start_time, end_time, latitude + "", longitude + "", 
//						PARAM_VALUES.SHOWMODE_DEFAULT, entitys, new EntityCallback<String>() {
//					@Override
//					public void succeed(String event_id) {
//						dismissProgressDialog();
//						Tools.openEvent(PublishEventActivity.this, event_id);
//						finish();
//					}
//
//					@Override
//					public void fail(String error) {
//						dismissProgressDialog();
//						toastShort(error);
//					}
//				});
//			}
//			
//			@Override
//			public void fail(int index, String error) {
//				Log.e("uploadFile", index + " " + error);
//				toastShort("�ϴ�ͼƬʧ��");
//				dismissProgressDialog();
//			}
//		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageLoader.getInstance().clearMemoryCache();
	}
}