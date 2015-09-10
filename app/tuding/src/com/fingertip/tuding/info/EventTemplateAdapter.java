package com.fingertip.tuding.info;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.entity.EventTemplate;
import com.fingertip.tuding.util.Tools;

public class EventTemplateAdapter extends BaseAdapter {
	
	private List<EventTemplate> list = new ArrayList<EventTemplate>();
	private Context context;
	
	public EventTemplateAdapter(Context context) {
		this.context = context;
	}

	public void addTemplates(List<EventTemplate> templates) {
		list.clear();
		list.addAll(templates);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public EventTemplate getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_event_template, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.title_txt = (TextView)convertView.findViewById(R.id.title_txt);
			viewHolder.content_txt = (TextView)convertView.findViewById(R.id.content_txt);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder)convertView.getTag();
		EventTemplate template = getItem(position);
		viewHolder.title_txt.setText(template.titleof);
		viewHolder.content_txt.setText(Tools.getShortString(template.content, 9, 6));
		return convertView;
	}

	class ViewHolder {
		TextView title_txt;
		TextView content_txt;
	}
}
