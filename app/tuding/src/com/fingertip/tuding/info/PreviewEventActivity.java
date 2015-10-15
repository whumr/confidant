package com.fingertip.tuding.info;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.entity.EventEntity.EventType;
import com.fingertip.tuding.util.ImageCache;
import com.lidroid.xutils.BitmapUtils;

/**
 * ��ͼ������
 * 
 * @author Administrator
 *
 */
public class PreviewEventActivity extends BaseActivity {

	private LinearLayout layout_main;
	private ImageView iv_head;
	private TextView tv_title, tv_name, tv_time, tv_collection, 
		 tv_recommendTopic, tv_recommend, tv_accusation;
	private WebView wv_detail;
	
	private EventEntity event;
	private BitmapUtils bitmapUtils;
	private SharedPreferenceUtil sp;

//	// ��Ļ���Ҽ��
//	private int screenMargin = 80;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_event);

//		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//		layoutParams.gravity = Gravity.CENTER;
//		layoutParams.width = getResources().getDisplayMetrics().widthPixels - screenMargin;

		findViews();
		initEntity();
	}

	private void findViews() {
		layout_main = (LinearLayout) findViewById(R.id.layout_main);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_collection = (TextView) findViewById(R.id.tv_collection);
		tv_recommendTopic = (TextView) findViewById(R.id.tv_recommend_topic);
		tv_recommend = (TextView) findViewById(R.id.tv_recommend);
		tv_time = (TextView) findViewById(R.id.tv_time);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		tv_accusation = (TextView) findViewById(R.id.tv_accusation);
		wv_detail = (WebView) findViewById(R.id.wv_detail);
	}

	private void initEntity() {
		event = (EventEntity) getIntent().getSerializableExtra(EXTRA_PARAM);
		if (event == null) {
			toastShort("���ݴ���");
			finish();
			return;
		}
		tv_title.setText(event.title);
		tv_name.setText(event.sender.nick_name);
		wv_detail.setVisibility(View.VISIBLE);
		wv_detail.getSettings().setDefaultTextEncodingName("UTF-8");
		wv_detail.loadData(event.content, "text/html; charset=UTF-8", "UTF-8");
//		wv_detail.loadData("<img width=\"100%\" src=\"http://tutu-oss.oss-cn-qingdao.aliyuncs.com/action/20151014/5aec28c-21F0BBB.jpg\" /> ", 
//				"text/html; charset=UTF-8", "UTF-8");
		tv_collection.setText("" + event.viewcount);// �����������
		tv_recommendTopic.setText("���ۣ�" + event.replycount + "��");
		tv_time.setText(event.send_time_str);

		bitmapUtils = new BitmapUtils(this);
		sp = new SharedPreferenceUtil(this);
		ImageCache.loadUserHeadImg(event.sender.head_img_url, event.sender.id, sp, bitmapUtils, iv_head);
		setOverlayType();
	}

	private void setOverlayType() {
		if (event.event_type == EventType.SPORTS) {
			layout_main.setBackgroundResource(R.drawable.bg_recommend);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_recommend);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_recommend);
		} else if (event.event_type == EventType.SOCIALITY) {
			layout_main.setBackgroundResource(R.drawable.bg_friend);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_friend);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_friend);
		} else if (event.event_type == EventType.PERFORM) {
			layout_main.setBackgroundResource(R.drawable.bg_activity);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_activity);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_activity);
		} else if (event.event_type == EventType.STUDY) {
			layout_main.setBackgroundResource(R.drawable.bg_event);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_event);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_event);
		} else if (event.event_type == EventType.SPECIAL) {
			layout_main.setBackgroundResource(R.drawable.bg_buy);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_buy);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_buy);
		} else if (event.event_type == EventType.OTHER) {
			layout_main.setBackgroundResource(R.drawable.bg_activity);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_activity);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_activity);
		} else {
			layout_main.setBackgroundResource(R.drawable.bg_activity);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_activity);
		}
	}
}
