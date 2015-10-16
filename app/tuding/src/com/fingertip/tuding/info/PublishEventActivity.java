package com.fingertip.tuding.info;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.entity.EventEntity.EventType;
import com.fingertip.tuding.info.widget.RichEditText;
import com.fingertip.tuding.main.widget.DialogDate;
import com.fingertip.tuding.main.widget.DialogDate.OnDateSelectdListener;
import com.fingertip.tuding.main.widget.MapPositionSelectionActivity;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.EventUtil;
import com.fingertip.tuding.util.http.UploadUtil;
import com.fingertip.tuding.util.http.UploadUtil.UploadCallback;
import com.fingertip.tuding.util.http.callback.EntityCallback;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_VALUES;
import com.fingertip.tuding.util.http.common.UploadImgEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PublishEventActivity extends BaseActivity{
	
	private static final int REQUEST_POS = 1000;
	private static final int REQUEST_PIC = 1001;
	private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	public static int MAX_PIC_SIZE = 9;
	
	private TextView tv_submit, tv_preview,
		tv_position, tv_type_hint, tv_start_time_hint, tv_end_time_hint,
		tv_special, tv_perform, tv_sociality, tv_sports, tv_study, tv_other;
	private ImageView iv_img, iv_bold, iv_big, iv_color,
		img_font_black, img_font_blue, img_font_green, img_font_purple, img_font_red, img_font_yellow;
	private EditText et_title;
	private RichEditText et_content;
	private RadioButton radio_default;
	private DialogDate start_time_dialog, end_time_dialog;
	private EventType eventType = null;
	private LinearLayout font_color_layout;
	
	private double latitude = 0, longitude = 0;
	private Map<String, String> img_upload_map = new HashMap<String, String>();
	
	private UserSession session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publishinfo_customer);
		setupViews();
		initDialog();
	}

	private void setupViews() {
		tv_submit = (TextView)findViewById(R.id.tv_submit);
		tv_preview = (TextView)findViewById(R.id.tv_preview);
		tv_position = (TextView)findViewById(R.id.tv_position);
		et_title = (EditText)findViewById(R.id.et_title);
		et_content = (RichEditText)findViewById(R.id.et_content);
		
		radio_default = (RadioButton)findViewById(R.id.radio_default);
		radio_default.setOnClickListener(onClickListener);
		
		findViewById(R.id.iv_back).setOnClickListener(onClickListener);
		tv_submit.setOnClickListener(onClickListener);
		tv_preview.setOnClickListener(onClickListener);
		
		
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
		
		iv_img = (ImageView) findViewById(R.id.iv_img);
		iv_bold = (ImageView) findViewById(R.id.iv_bold);
		iv_big = (ImageView) findViewById(R.id.iv_big);
		iv_color = (ImageView) findViewById(R.id.iv_color);
		iv_img.setOnClickListener(onClickListener);
		iv_bold.setOnClickListener(onClickListener);
		iv_big.setOnClickListener(onClickListener);
		iv_color.setOnClickListener(onClickListener);
		
		img_font_black = (ImageView) findViewById(R.id.img_font_black);
		img_font_black.setOnClickListener(onClickListener);
		img_font_blue = (ImageView) findViewById(R.id.img_font_blue);
		img_font_blue.setOnClickListener(onClickListener);
		img_font_green = (ImageView) findViewById(R.id.img_font_green);
		img_font_green.setOnClickListener(onClickListener);
		img_font_purple = (ImageView) findViewById(R.id.img_font_purple);
		img_font_purple.setOnClickListener(onClickListener);
		img_font_red = (ImageView) findViewById(R.id.img_font_red);
		img_font_red.setOnClickListener(onClickListener);
		img_font_yellow = (ImageView) findViewById(R.id.img_font_yellow);
		img_font_yellow.setOnClickListener(onClickListener);
		
		font_color_layout = (LinearLayout)findViewById(R.id.font_color_layout);
		font_color_layout.setOnClickListener(onClickListener);
		
		session = UserSession.getInstance();
	}
	
	private void initDialog(){
		start_time_dialog = new DialogDate(this, new OnDateSelectdListener() {
			@Override
			public void onDateSelectd(String time) {
				tv_start_time_hint.setText("活动开始时间  (" + start_time_dialog.getTimeString() + ")");
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
				tv_end_time_hint.setText("活动截止时间  (" + end_time_dialog.getTimeString() + ")");
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
			Uri path = data.getData();
			if (path != null) 
				et_content.insertPic(path.getPath());
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
			case R.id.tv_preview:
				previewActivity();
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
			case R.id.radio_default:
				Intent intent = new Intent();
				intent.setClass(PublishEventActivity.this, PublishInfoActivity.class);
				startActivity(intent);
				finish();
				break;
			case R.id.tv_type_hint:
				if (findViewById(R.id.layout_type).getVisibility() == View.VISIBLE){
					findViewById(R.id.layout_type).setVisibility(View.GONE);
					tv_type_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_top), null);
					tv_type_hint.setText("活动类型(" + eventType.getType() + ")");
				} else {
					findViewById(R.id.layout_type).setVisibility(View.VISIBLE);
					tv_type_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_down), null);					
					tv_type_hint.setText("活动类型");
				}
				break;
			case R.id.iv_img:
				Tools.selectSinglePic(PublishEventActivity.this, REQUEST_PIC);
				break;
			case R.id.iv_bold:
				if (!et_content.isFont_bold()) {
					et_content.setFont_bold(true);
					iv_bold.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_bold_up));
				} else {
					et_content.setFont_bold(false);
					iv_bold.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_bold_down));
				}
				break;
			case R.id.iv_big:
				if (!et_content.isFont_big()) {
					et_content.setFont_big(true);
					iv_big.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_big_up));
				} else {
					et_content.setFont_big(false);
					iv_big.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_big_down));
				}
				break;
			case R.id.iv_color:
				font_color_layout.setVisibility(View.VISIBLE);
				break;
			case R.id.img_font_black:
				font_color_layout.setVisibility(View.GONE);
				iv_color.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_color_black));
				et_content.setFont_color(RichEditText.COLOR_BLACK);
				break;
			case R.id.img_font_blue:
				font_color_layout.setVisibility(View.GONE);
				iv_color.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_color_blue));
				et_content.setFont_color(RichEditText.COLOR_BLUE);
				break;
			case R.id.img_font_green:
				font_color_layout.setVisibility(View.GONE);
				iv_color.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_color_green));
				et_content.setFont_color(RichEditText.COLOR_GREEN);
				break;
			case R.id.img_font_purple:
				font_color_layout.setVisibility(View.GONE);
				iv_color.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_color_purple));
				et_content.setFont_color(RichEditText.COLOR_PURPLE);
				break;
			case R.id.img_font_red:
				font_color_layout.setVisibility(View.GONE);
				iv_color.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_color_red));
				et_content.setFont_color(RichEditText.COLOR_RED);
				break;
			case R.id.img_font_yellow:
				font_color_layout.setVisibility(View.GONE);
				iv_color.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_color_yellow));
				et_content.setFont_color(RichEditText.COLOR_YELLOW);
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
	
	/** 发布活动 **/
	private void publicActivity(){
		if (!checkData())
			return;
		//标题
		final String title = et_title.getText().toString().trim();
		//内容
		final String content = et_content.getHtmlContent(img_upload_map);
		//图片
		final List<UploadImgEntity> images = et_content.getImages();
		//类型
		final String type = eventType.getType();
		//开始时间
		final String start_time = start_time_dialog.getTimeString();
		//截止时间
		final String end_time = end_time_dialog.getTimeString();
		//坐标
		final String address = tv_position.getText().toString();
		//发布活动
		showProgressDialog(false);
		
		//先上传图片
		if (!images.isEmpty()) {
			//先上传图片
			for (Iterator<UploadImgEntity> it = images.iterator(); it.hasNext();) {
				UploadImgEntity img = it.next();
				String small_path = img.small_file.getAbsolutePath();
				String big_path = img.big_file.getAbsolutePath();
				if (img_upload_map.containsKey(small_path) && img_upload_map.containsKey(big_path))
					it.remove();
			}
			showProgressDialog(false);
			if (!images.isEmpty()) {
				UploadUtil.uploadImgEntitys(images, new UploadCallback() {
					
					@Override
					public void succeed() {
						for (UploadImgEntity img : images) {
							String small_path = img.small_file.getAbsolutePath();
							String big_path = img.big_file.getAbsolutePath();
							img_upload_map.put(small_path, img.small_url);
							img_upload_map.put(big_path, img.big_url);
						}
						pubEvent(title, et_content.getHtmlContent(img_upload_map), type, address, 
								start_time, end_time);
					}
					
					@Override
					public void fail(int index, String error) {
						Log.e("uploadFile", index + " " + error);
						toastShort("上传图片失败");
						dismissProgressDialog();
					}
				});
			} else
				pubEvent(title, et_content.getHtmlContent(img_upload_map), type, address, start_time, end_time);
		} else
			pubEvent(title, content, type, address, start_time, end_time);
	}
	
	private void pubEvent(String title, String content, String type, String address, String start_time, String end_time) {
		Log.e("PublishEventActivity", "pubEvent");
		content = Tools.encodeString(content);
		EventUtil.publishEvent_v2(title, content, type, address, start_time, end_time, latitude + "", longitude + "", 
				PARAM_VALUES.SHOWMODE_BIG, et_content.getImages(img_upload_map), new EntityCallback<String>() {
			@Override
			public void succeed(String event_id) {
				dismissProgressDialog();
				Tools.openEvent(PublishEventActivity.this, event_id);
				finish();
			}

			@Override
			public void fail(String error) {
				dismissProgressDialog();
				toastShort(error);
			}
		});
	}

	/** 预览活动 **/
	private void previewActivity(){
		if (!checkData())
			return;
		//标题
		String title = et_title.getText().toString().trim();
		//内容
		String content = et_content.getHtmlContent(img_upload_map);
		//图片
//		final List<UploadImgEntity> images = et_content.getImages();
		//类型
//		String type = eventType.getType();
		//开始时间
		String start_time = start_time_dialog.getTimeString();
		//截止时间
		String end_time = end_time_dialog.getTimeString();
		//坐标
		String address = tv_position.getText().toString();
//		latitude + "", longitude + ""
		//预览活动
		final EventEntity event = new EventEntity();
		event.title = title;
		event.content = content;
		event.event_type = eventType;
		long now = System.currentTimeMillis();
		event.timefrom = now;
		event.timeto = now;
		try {
			event.timefrom = TIME_FORMAT.parse(start_time).getTime();
			event.timeto = TIME_FORMAT.parse(end_time).getTime();
		} catch (ParseException e) {
		}
		event.send_time = System.currentTimeMillis();
		event.address = address;
		event.sender = session.getUser();
//		if (!images.isEmpty()) {
//			//先上传图片
//			for (Iterator<UploadImgEntity> it = images.iterator(); it.hasNext();) {
//				UploadImgEntity img = it.next();
//				String small_path = img.small_file.getAbsolutePath();
//				String big_path = img.big_file.getAbsolutePath();
//				if (img_upload_map.containsKey(small_path) && img_upload_map.containsKey(big_path))
//					it.remove();
//			}
//			if (!images.isEmpty()) {
//				showProgressDialog(false);
//				UploadUtil.uploadImgEntitys(images, new UploadCallback() {
//					
//					@Override
//					public void succeed() {
//						for (UploadImgEntity img : images) {
//							String small_path = img.small_file.getAbsolutePath();
//							String big_path = img.big_file.getAbsolutePath();
//							img_upload_map.put(small_path, img.small_url);
//							img_upload_map.put(big_path, img.big_url);
//						}
//						event.content = et_content.getHtmlContent(img_upload_map);
//						dismissProgressDialog();
//						previewEvent(event);
//					}
//					
//					@Override
//					public void fail(int index, String error) {
//						Log.e("uploadFile", index + " " + error);
//						toastShort("上传图片失败");
//						dismissProgressDialog();
//					}
//				});
//			} else {
//				event.content = et_content.getHtmlContent(img_upload_map);
//				previewEvent(event);
//			}
//		} else
			previewEvent(event);
	}
	
	private void previewEvent(EventEntity event) {
		Intent intent = new Intent();
		intent.setClass(this, PreviewEventActivity.class);
		intent.putExtra(EXTRA_PARAM, event);
		startActivity(intent);
	}
	
	private boolean checkData() {
		//标题
		String title = et_title.getText().toString().trim();
		if (Validator.isEmptyString(title)) {
			toastShort("请输入活动标题");
			return false;
		}
		//内容
		String content = et_content.getText().toString().trim();
		if (Validator.isEmptyString(content)) {
			toastShort("请输入活动内容");
			return false;
		}
		//类型
		String type = eventType.getType();
		if (Validator.isEmptyString(type)) {
			toastShort("请选择活动类型");
			return false;
		}
		//开始时间
		if (Validator.isEmptyString(tv_start_time_hint.getText().toString().trim())) {
			toastShort("请选择活动开始时间");
			return false;
		}
		//截止时间
		if (Validator.isEmptyString(tv_end_time_hint.getText().toString().trim())) {
			toastShort("请选择活动截止时间");
			return false;
		}
		//坐标
		String address = tv_position.getText().toString();
		if (Validator.isEmptyString(address) || latitude == 0 || longitude == 0) {
			toastShort("请标记活动位置");
			return false;
		}
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageLoader.getInstance().clearMemoryCache();
	}
}
