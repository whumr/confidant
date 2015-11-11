package com.fingertip.tuding.my.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.my.MyEventActivity;
import com.fingertip.tuding.util.ImageCache;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.lidroid.xutils.BitmapUtils;

public class AdapterMySignedEvent extends BaseAdapter implements OnItemClickListener {
	
	private MyEventActivity activity;
	private List<EventEntity> arrayList = new ArrayList<EventEntity>();
	private BitmapUtils bitmapUtils;
	private SharedPreferenceUtil sp;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM月dd日   HH点mm分", Locale.getDefault());
	
	public AdapterMySignedEvent(MyEventActivity activity){
		this.activity = activity;
		sp = new SharedPreferenceUtil(activity);
		bitmapUtils = new BitmapUtils(activity);
	}
	
	public void addAllData(List<EventEntity> list){
		arrayList.clear();
		if(list != null)
			arrayList.addAll(list);
		notifyDataSetChanged();
	}

	public void appendAllData(List<EventEntity> list){
		if(!Validator.isEmptyList(list)) {
			arrayList.addAll(list);
			notifyDataSetChanged();
		}
	}
	
	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHoler viewHoler;
		if(convertView == null){
			convertView = LayoutInflater.from(activity).inflate(R.layout.list_item_my_signed_event, parent, false);
			viewHoler = new ViewHoler();
			viewHoler.head_img = (ImageView)convertView.findViewById(R.id.event_sender_head);
			viewHoler.sender_name_text = (TextView)convertView.findViewById(R.id.event_sender_name);
			viewHoler.event_title_text = (TextView)convertView.findViewById(R.id.event_title);
			viewHoler.event_type_text = (TextView)convertView.findViewById(R.id.event_type);
			viewHoler.event_start_time_text = (TextView)convertView.findViewById(R.id.event_start_time);
			viewHoler.event_status_text = (TextView)convertView.findViewById(R.id.event_status);
			convertView.setTag(viewHoler);
		}else {
			viewHoler = (ViewHoler)convertView.getTag();
		}
		EventEntity event = (EventEntity)getItem(position);
		try {
			ImageCache.loadUserHeadImg(event.sender.head_img_url, event.sender.id, sp, bitmapUtils, viewHoler.head_img);
		} catch (Exception e) {
		}
		viewHoler.sender_name_text.setText(event.sender.nick_name);
		viewHoler.event_title_text.setText(event.title);
		viewHoler.event_type_text.setText(event.kindof);
		if (EventEntity.STATUS_IN.equals(event.statusof))
			viewHoler.event_status_text.setTextColor(activity.getResources().getColor(R.color.green_event));
		else if (EventEntity.STATUS_OVER.equals(event.statusof))
			viewHoler.event_status_text.setTextColor(activity.getResources().getColor(R.color.red_event));
		viewHoler.event_status_text.setText(event.getStatusStr());
		viewHoler.event_start_time_text.setText(DATE_FORMAT.format(new Date(event.send_time)));
		viewHoler.event_id = event.id;
		return convertView;
	}

	class ViewHoler{
		ImageView head_img;
		TextView sender_name_text, event_title_text, event_type_text, 
			event_start_time_text, event_status_text;
		String event_id;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		ViewHoler viewHoler = (ViewHoler) view.getTag();
		//跳转到活动页
		Tools.openEvent(activity, viewHoler.event_id);
	}
}
