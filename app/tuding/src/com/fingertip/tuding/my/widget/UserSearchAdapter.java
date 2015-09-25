package com.fingertip.tuding.my.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.UserEntity;
import com.fingertip.tuding.util.ImageCache;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.lidroid.xutils.BitmapUtils;

public class UserSearchAdapter extends BaseAdapter {
	
	private List<UserEntity> list = new ArrayList<UserEntity>();
	private Context context;
	
	private BitmapUtils bitmapUtils;
	private SharedPreferenceUtil sp;
	
	public UserSearchAdapter(Context context) {
		this.context = context;
		bitmapUtils = new BitmapUtils(context);
		sp = new SharedPreferenceUtil(context);
	}

	public void addUsers(List<UserEntity> users) {
		list.clear();
		list.addAll(users);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public UserEntity getItem(int position) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_user_search, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.nick_txt = (TextView)convertView.findViewById(R.id.nick_txt);
			viewHolder.head_img = (ImageView)convertView.findViewById(R.id.head_img);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder)convertView.getTag();
		UserEntity user = getItem(position);
		if (!Validator.isEmptyString(user.head_img_url)) {
			try {
				ImageCache.loadUserHeadImg(user.head_img_url, user.id, sp, bitmapUtils, viewHolder.head_img);
			} catch (Exception e) {
			}
		} else
			viewHolder.head_img.setImageBitmap(Tools.toRoundCorner(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_head)));
		viewHolder.nick_txt.setText(user.nick_name);
		return convertView;
	}

	class ViewHolder {
		TextView nick_txt;
		ImageView head_img;
	}
}
