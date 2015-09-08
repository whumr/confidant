package com.fingertip.tuding.my.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.util.ImageCache;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.lidroid.xutils.BitmapUtils;

public class AdapterMyWatchGroup extends BaseAdapter implements OnItemClickListener {
	
	private Context context;
	private List<EventEntity> event_list = new ArrayList<EventEntity>();
	private BitmapUtils bitmapUtils;
	private SharedPreferenceUtil sp;
	
	public AdapterMyWatchGroup(Context context){
		this.context = context;
		sp = new SharedPreferenceUtil(context);
		bitmapUtils = new BitmapUtils(context);
	}
	
	public void addAllData(List<EventEntity> list){
		event_list.clear();
		if(list != null)
			event_list.addAll(list);
		notifyDataSetChanged();
	}

	public void appendAllData(List<EventEntity> list){
		if(!Validator.isEmptyList(list)) {
			event_list.addAll(list);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return event_list.size();
	}

	@Override
	public Object getItem(int position) {
		return event_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_watch_group, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.watcher_head_img = (ImageView)convertView.findViewById(R.id.watcher_head_img);
			viewHolder.watcher_name_txt = (TextView)convertView.findViewById(R.id.watcher_name_txt);
			viewHolder.event_title_txt = (TextView)convertView.findViewById(R.id.event_title_txt);
			viewHolder.event_content_txt = (TextView)convertView.findViewById(R.id.event_content_txt);
			viewHolder.event_time_txt = (TextView)convertView.findViewById(R.id.event_time_txt);
			viewHolder.event_address_txt = (TextView)convertView.findViewById(R.id.event_address_txt);
			viewHolder.event_pics_grid = (GridView)convertView.findViewById(R.id.event_pics_grid);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder)convertView.getTag();
		EventEntity event = (EventEntity)getItem(position);
		ImageCache.loadUserHeadImg(event.sender.head_img_url, event.sender.id, sp, bitmapUtils, viewHolder.watcher_head_img);				
		viewHolder.watcher_head_img.setTag(event.sender.id);
		viewHolder.watcher_name_txt.setText(event.sender.nick_name);
		viewHolder.event_title_txt.setText(event.title);
		viewHolder.event_content_txt.setText(event.content);
		viewHolder.event_time_txt.setText(Tools.getTimeStr(event.send_time));
		viewHolder.event_address_txt.setText(event.address);
		viewHolder.watcher_head_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Tools.openUser(context, v.getTag().toString());
			}
		});
		AdapterGridPic pic_adapter = new AdapterGridPic(context, (ArrayList<String>)event.pics_small, (ArrayList<String>)event.pics_big);
		viewHolder.event_pics_grid.setAdapter(pic_adapter);
		viewHolder.event_pics_grid.setOnItemClickListener(pic_adapter);
		return convertView;
	}

	class ViewHolder {
		ImageView watcher_head_img;
		TextView watcher_name_txt, event_title_txt, event_content_txt, event_time_txt, event_address_txt;
		GridView event_pics_grid;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Tools.openEvent(context, ((EventEntity)(parent.getAdapter().getItem(position))).id);
	}
}
