package com.fingertip.tuding.my.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.my.MyEventActivity;
import com.fingertip.tuding.my.widget.Deleteable;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.http.UserUtil;
import com.fingertip.tuding.util.http.callback.DefaultCallback;

public class AdapterMyEvent extends BaseAdapter implements OnItemClickListener, Deleteable {
	
	private MyEventActivity activity;
	private View empty_view;
	private List<EventEntity> arrayList  = new Vector<EventEntity>();
	
	private boolean delete;
	private List<String> delete_ids = new ArrayList<String>();
	
	public AdapterMyEvent(MyEventActivity activity, View empty_view){
		this.activity = activity;
		this.empty_view = empty_view;
		this.delete = false;
	}

	public void addAllList(List<EventEntity> list){
		if(list != null){
			arrayList.clear();
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void appendList(List<EventEntity> list){
		if(list != null){
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		int count = arrayList.size();
		if (count <= 0)
			empty_view.setVisibility(View.VISIBLE);
		else
			empty_view.setVisibility(View.GONE);
		return count;
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.list_item_my_event, parent, false);
			viewHoler = new ViewHoler();
			viewHoler.v_delete_check = (LinearLayout)convertView.findViewById(R.id.v_delete_check);
			viewHoler.iv_delete_check = (ImageView)convertView.findViewById(R.id.iv_delete_check);
			viewHoler.event_title_text = (TextView)convertView.findViewById(R.id.event_title);
			viewHoler.event_send_time_text = (TextView)convertView.findViewById(R.id.event_send_time);
			viewHoler.event_status_text = (TextView)convertView.findViewById(R.id.event_status);
			
//			viewHoler.tv_delete = (TextView)convertView.findViewById(R.id.tv_delete);
//			viewHoler.tv_delete.setOnClickListener(this);
			
			convertView.setTag(viewHoler);
		}else {
			viewHoler = (ViewHoler)convertView.getTag();
		}
		
		EventEntity event = (EventEntity)getItem(position);
		viewHoler.event_title_text.setText(event.title);
//		viewHoler.event_send_time_text.setText(event.getSendTimeStr());
		viewHoler.event_send_time_text.setText(Tools.getTimeStr(event.send_time));
		if (EventEntity.STATUS_IN.equals(event.statusof))
			viewHoler.event_status_text.setTextColor(activity.getResources().getColor(R.color.green_event));
		else if (EventEntity.STATUS_OVER.equals(event.statusof))
			viewHoler.event_status_text.setTextColor(activity.getResources().getColor(R.color.red_event));
		viewHoler.event_status_text.setText(event.getStatusStr());
		if (delete) {
//			viewHoler.tv_delete.setVisibility(View.VISIBLE);
			viewHoler.v_delete_check.setVisibility(View.VISIBLE);
			if (viewHoler.checked)
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_checked));
			else
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_unchecked));
		} else {
//			viewHoler.tv_delete.setVisibility(View.GONE);
			viewHoler.v_delete_check.setVisibility(View.GONE);
			viewHoler.checked = false;
		}
		viewHoler.event_id = event.id;
//		viewHoler.position = position;
		return convertView;
	}

	class ViewHoler{
		LinearLayout v_delete_check;
		ImageView iv_delete_check;
		
		TextView event_title_text;
		TextView event_send_time_text;
		TextView event_status_text;
		//删除
//		TextView tv_delete;
		String event_id;
//		int position;
		boolean checked;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		ViewHoler viewHoler = (ViewHoler) view.getTag();
		if(delete) {
			if (viewHoler.checked) {
				delete_ids.remove(viewHoler.event_id);
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_unchecked));
			} else {
				delete_ids.add(viewHoler.event_id);
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_checked));
			}
			viewHoler.checked = !viewHoler.checked;
			activity.setSelectedCount(delete_ids.size());
		} else {
			//跳转到活动页
			Tools.openEvent(activity, viewHoler.event_id);
		}
	}

	@Override
	public void begainDelete() {
		delete = true;
		notifyDataSetChanged();
	}

	@Override
	public void endDelete() {
		delete = false;
		delete_ids.clear();
		notifyDataSetChanged();		
	}
	
	@Override
	public void doDelete() {
		deleteEvents();
	}

	@Override
	public int size() {
		return arrayList.size();
	}
	
	@Override
	public int selectedSize() {
		return delete_ids.size();
	}
	
	private void deleteEvents() {
		if (delete_ids.size() > 0) {
			activity.showProgressDialog(false);
			for (String id : delete_ids) {
				deleteEvent(id);
			}
		}
	}
	
	private void deleteEvent(final String event_id) {
		UserUtil.deleteMyEvent(event_id, new DefaultCallback() {
			@Override
			public void succeed() {
				afterDelete(event_id, true);
			}
			
			@Override
			public void fail(String error) {
				activity.toastShort(error);
				afterDelete(event_id, false);
			}
		});
	}
	
	private void afterDelete(String event_id, boolean delete) {
		delete_ids.remove(event_id);
		if (delete) {
			for (Iterator<EventEntity> it = arrayList.iterator(); it.hasNext();) {
				EventEntity event = it.next();
				if (event_id.equals(event.id))
					it.remove();
			}
		}
		if (delete_ids.isEmpty()) {
			activity.finishDelete();
			activity.dimissProgressDialog();
		}
	}
}
