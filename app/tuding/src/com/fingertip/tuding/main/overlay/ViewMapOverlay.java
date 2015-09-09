package com.fingertip.tuding.main.overlay;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.entity.EventEntity.EventType;
import com.fingertip.tuding.util.ImageCache;
import com.lidroid.xutils.BitmapUtils;

/**
 * 地图marker子控件
 * @author Administrator
 *
 */
public class ViewMapOverlay extends FrameLayout {
	private LinearLayout layout_content;
	private ImageView iv_head;
	private ImageView iv_arrowDown;
	private TextView tv_title;
	private TextView tv_name;
	private TextView tv_collection;
	private TextView tv_time;
	
	private EventEntity event;
	private BitmapUtils bitmapUtils;
	private SharedPreferenceUtil sp;

	public ViewMapOverlay(Context context) {
		super(context);
		setupViews();
	}
	
	public ViewMapOverlay(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupViews();
	}
	
	public ViewMapOverlay(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupViews();
	}
	
	private void setupViews(){
		LayoutInflater inflater ;
		inflater = (LayoutInflater) ViewMapOverlay.this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_overlay, this);
		
		layout_content = (LinearLayout)findViewById(R.id.layout_content);
		iv_head = (ImageView)findViewById(R.id.iv_head);
		iv_arrowDown = (ImageView)findViewById(R.id.iv_arrow_down);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_name = (TextView)findViewById(R.id.tv_name);
		tv_collection = (TextView)findViewById(R.id.tv_collection);
		tv_time = (TextView)findViewById(R.id.tv_time);
		
		int width = getContext().getResources().getDisplayMetrics().widthPixels - 170;
		tv_name.setMaxWidth(width);
		
		bitmapUtils = new BitmapUtils(getContext());
		sp = new SharedPreferenceUtil(getContext());
	}
	
	public void setEventEntity(EventEntity event){
		this.event = event;
		setupViewsData();
	}
	
	private void setupViewsData() {
		if (event != null) {
			tv_title.setText(event.title);
			tv_name.setText(event.sender.nick_name);
			tv_collection.setText("" + event.viewcount);//调用浏览量
			tv_time.setText(event.send_time_str);
			ImageCache.loadUserHeadImg(event.sender.head_img_url, event.sender.id, sp, bitmapUtils, iv_head);
			setOverlayType();
		}
	}
	
	private void setOverlayType(){
		if (event.event_type == EventType.SPORTS) {
			layout_content.setBackgroundResource(R.drawable.bg_recommend);
			iv_arrowDown.setImageResource(R.drawable.icon_arrow_downrecommend);
		} else if (event.event_type == EventType.SOCIALITY) {
			layout_content.setBackgroundResource(R.drawable.bg_friend);
			iv_arrowDown.setImageResource(R.drawable.icon_arrow_downfriend);
		} else if (event.event_type == EventType.PERFORM) {
			layout_content.setBackgroundResource(R.drawable.bg_activity);
			iv_arrowDown.setImageResource(R.drawable.icon_arrow_downactivity);
		} else if (event.event_type == EventType.STUDY) {
			layout_content.setBackgroundResource(R.drawable.bg_event);
			iv_arrowDown.setImageResource(R.drawable.icon_arrow_downevent);
		} else if (event.event_type == EventType.SPECIAL) {
			layout_content.setBackgroundResource(R.drawable.bg_buy);
			iv_arrowDown.setImageResource(R.drawable.icon_arrow_downbuy);
		} else if (event.event_type == EventType.OTHER) {
			layout_content.setBackgroundResource(R.drawable.bg_activity);
			iv_arrowDown.setImageResource(R.drawable.icon_arrow_downactivity);
		} else {
			layout_content.setBackgroundResource(R.drawable.bg_activity);
			iv_arrowDown.setImageResource(R.drawable.icon_arrow_downactivity);
		}
	}
}
