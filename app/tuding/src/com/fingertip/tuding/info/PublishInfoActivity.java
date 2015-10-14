package com.fingertip.tuding.info;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.EventEntity.EventType;
import com.fingertip.tuding.entity.EventTemplate;
import com.fingertip.tuding.info.EventTemplateDialog.OnTemplateSelected;
import com.fingertip.tuding.main.widget.DialogDate;
import com.fingertip.tuding.main.widget.DialogDate.OnDateSelectdListener;
import com.fingertip.tuding.main.widget.MapPositionSelectionActivity;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.EventUtil;
import com.fingertip.tuding.util.http.UploadUtil;
import com.fingertip.tuding.util.http.UploadUtil.UploadCallback;
import com.fingertip.tuding.util.http.callback.EntityCallback;
import com.fingertip.tuding.util.http.callback.EntityListCallback;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_VALUES;
import com.fingertip.tuding.util.http.common.UploadImgEntity;
import com.fingertip.tuding.widget.SelectPicActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PublishInfoActivity extends BaseActivity{
	
	private static final int REQUEST_POS = 1000;
	private static final int REQUEST_PIC = 1001;
	public static int MAX_PIC_SIZE = 9;
	
	private GridView img_gridView;
	private PublishPicAdapter pic_adapter;
	private TextView tv_submit, tv_template,
		tv_position, tv_type_hint, tv_img_hint, tv_start_time_hint, tv_end_time_hint,
		tv_special,tv_perform,tv_sociality,tv_sports,tv_study,tv_other;
	private EditText et_title, et_content;
	private RadioButton radio_customer;
	
	private DialogDate start_time_dialog, end_time_dialog;
	private EventTemplateDialog event_template_dialog;
	private UserSession session;
	private EventType eventType = null;
	
	private static String hintText = "活动介绍：\n活动地点：\n活动时间：\n报名方式：\n活动费用：\n提示： \n";
	private double latitude = 0, longitude = 0;
	private int screen_width, screen_height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publishinfo);
		setupViews();
		initData();
	}

	private void setupViews() {
		img_gridView = (GridView)findViewById(R.id.img_gridView);
		tv_submit = (TextView)findViewById(R.id.tv_submit);
		tv_position = (TextView)findViewById(R.id.tv_position);
		et_title = (EditText)findViewById(R.id.et_title);
		et_content =(EditText)findViewById(R.id.et_content);
		radio_customer = (RadioButton)findViewById(R.id.radio_customer);
		radio_customer.setOnClickListener(onClickListener);
		
		tv_template = (TextView)findViewById(R.id.tv_template);
		tv_template.setOnClickListener(onClickListener);
		
		findViewById(R.id.iv_back).setOnClickListener(onClickListener);
		tv_submit.setOnClickListener(onClickListener);
		
		
		tv_type_hint = (TextView)findViewById(R.id.tv_type_hint);
		tv_img_hint = (TextView)findViewById(R.id.tv_img_hint);
		tv_start_time_hint = (TextView)findViewById(R.id.tv_start_time_hint);
		tv_end_time_hint = (TextView)findViewById(R.id.tv_end_time_hint);
		tv_type_hint.setOnClickListener(onClickListener);
		tv_img_hint.setOnClickListener(onClickListener);
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
		
		pic_adapter = new PublishPicAdapter(this, new ArrayList<String>());
		img_gridView.setAdapter(pic_adapter);
		img_gridView.setOnItemClickListener(pic_adapter);
		
		session = UserSession.getInstance();
		
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		screen_width = outMetrics.widthPixels;
		screen_height = outMetrics.heightPixels;
	}
	
	
	private void initData() {
		et_content.setText(hintText);
		initDialog();
		if (!session.isLoad_event_template() || Validator.isEmptyList(session.getEvent_templates())) {
			EventUtil.loadEventTemplates(new EntityListCallback<EventTemplate>() {
				@Override
				public void succeed(List<EventTemplate> list) {
					if (!Validator.isEmptyList(list)) {
						session.getEvent_templates().addAll(list);
						session.setLoad_event_template(true);
						tv_template.setVisibility(View.VISIBLE);
					}
				}
				
				@Override
				public void fail(String error) {
				}
			});
		} else
			tv_template.setVisibility(View.VISIBLE);
		locateAddress();
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
		
		event_template_dialog = new EventTemplateDialog(this, screen_width, screen_height, new OnTemplateSelected() {
			@Override
			public void onSelected(int index) {
				EventTemplate template = session.getEvent_templates().get(index);
				et_title.setText(template.titleof);
				et_content.setText(Tools.convertLine(template.content));
				setTypeBackground(template.kindof);
				//图片
				List<String> pics = pic_adapter.getPics();
				if (Validator.isEmptyList(pics) && !Validator.isEmptyString(template.picof)) {
					List<String> list = new ArrayList<String>();
					list.add(template.picof);
					pic_adapter.addPics(list);
				}
				event_template_dialog.dismiss();
			}
		});
	}
	
	public void addImg() {
		Tools.selectPics(this, MAX_PIC_SIZE - pic_adapter.getCount() + 1, REQUEST_PIC);
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
				pic_adapter.addPics(pics);
		}
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			Context context = PublishInfoActivity.this;
			int v_id = v.getId();
			if(v_id == R.id.iv_back)
				finish();
			else if(v_id == R.id.tv_submit)
				publicActivity();
			else if(v_id == R.id.tv_position) {
				Intent intent = new Intent();
				intent.setClass(PublishInfoActivity.this, MapPositionSelectionActivity.class);
				startActivityForResult(intent, REQUEST_POS);
			} else if(v_id == R.id.tv_start_time_hint)
				start_time_dialog.show();
			else if (v_id == R.id.tv_end_time_hint)
				end_time_dialog.show();
			else if (v_id == R.id.radio_customer) {
				Intent intent = new Intent();
				intent.setClass(PublishInfoActivity.this, PublishEventActivity.class);
				startActivity(intent);
				finish();
			} else if (v_id == R.id.tv_img_hint) {
				if(img_gridView.getVisibility() == View.VISIBLE) {
					img_gridView.setVisibility(View.GONE);
					tv_img_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_top), null);
				}else {
					img_gridView.setVisibility(View.VISIBLE);
					tv_img_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_down), null);
				}
			} else if(v_id == R.id.tv_type_hint){
				if(findViewById(R.id.layout_type).getVisibility() == View.VISIBLE){
					findViewById(R.id.layout_type).setVisibility(View.GONE);
					tv_type_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_top), null);
					tv_type_hint.setText("活动类型(" + eventType.getType() + ")");
				}else {
					findViewById(R.id.layout_type).setVisibility(View.VISIBLE);
					tv_type_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_down), null);					
					tv_type_hint.setText("活动类型");
				}
			//快捷发布
			} else if(v_id == R.id.tv_template)
				event_template_dialog.show();
			else if(v_id == R.id.tv_special)
				setTypeBackground(EventType.SPECIAL);
			else if(v_id == R.id.tv_perform)
				setTypeBackground(EventType.PERFORM);
			else if(v_id == R.id.tv_sociality)
				setTypeBackground(EventType.SOCIALITY);
			else if(v_id == R.id.tv_sports)
				setTypeBackground(EventType.SPORTS);
			else if(v_id == R.id.tv_study)
				setTypeBackground(EventType.STUDY);
			else if(v_id == R.id.tv_other)
				setTypeBackground(EventType.OTHER);
		}
	};
	
	/** 发布活动 **/
	private void publicActivity(){
		//标题
		final String title = et_title.getText().toString().trim();
		if (Validator.isEmptyString(title)) {
			toastShort("请输入活动标题");
			return;
		}
		//内容
		final String content = et_content.getText().toString().trim();
		if (Validator.isEmptyString(content)) {
			toastShort("请输入活动内容");
			return;
		}
		//类型
		final String type = eventType.getType();
		if (Validator.isEmptyString(type)) {
			toastShort("请选择活动类型");
			return;
		}
		//图片
		List<String> pics = pic_adapter.getPics();
		if (Validator.isEmptyList(pics)) {
			toastShort("请选择活动图片");
			return;
		}
		//开始时间
		final String start_time = start_time_dialog.getTimeString();
		if (Validator.isEmptyString(tv_start_time_hint.getText().toString().trim())) {
			toastShort("请选择活动开始时间");
			return;
		}
		//截止时间
		final String end_time = end_time_dialog.getTimeString();
		if (Validator.isEmptyString(tv_end_time_hint.getText().toString().trim())) {
			toastShort("请选择活动截止时间");
			return;
		}
		//坐标
		String address = tv_position.getText().toString();
		if (Validator.isEmptyString(address) || latitude == 0 || longitude == 0) {
			toastShort("请标记活动位置");
			return;
		}
		if (address.indexOf(":") > 0)
			address = address.split(":")[1];
		final String fianl_address = address;
		//发布活动
		showProgressDialog(false);
		//先上传图片
		final List<UploadImgEntity> entitys = new ArrayList<UploadImgEntity>();
		UploadUtil.uplodaImg(pics, entitys, new UploadCallback() {
			
			@Override
			public void succeed() {
				EventUtil.publishEvent(title, content, type, fianl_address, start_time, end_time, latitude + "", longitude + "", 
						PARAM_VALUES.SHOWMODE_DEFAULT, entitys, new EntityCallback<String>() {
					@Override
					public void succeed(String event_id) {
						dismissProgressDialog();
						Tools.openEvent(PublishInfoActivity.this, event_id);
						finish();
					}

					@Override
					public void fail(String error) {
						dismissProgressDialog();
						toastShort(error);
					}
				});
			}
			
			@Override
			public void fail(int index, String error) {
				Log.e("uploadFile", index + " " + error);
				toastShort("上传图片失败");
				dismissProgressDialog();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageLoader.getInstance().clearMemoryCache();
	}
	
	private void locateAddress() {
		final float lat = getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT);
		final float lon = getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG);
		if (lat > 0 && lon > 0) {
			final GeoCoder mSearch = GeoCoder.newInstance();
			mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
				
				@Override
				public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
					if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
						toastShort("未能确定当前位置");
					} else {
						tv_position.setText("当前位置:" + result.getAddress());
						latitude = lat;
						longitude = lon;
					}
					mSearch.destroy();
				}
				
				@Override
				public void onGetGeoCodeResult(GeoCodeResult result) {
				}
			});
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(lat, lon)));
		}
	}
}
