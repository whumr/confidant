package com.fingertip.tuding.main.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.CommentEntity;
import com.fingertip.tuding.util.ImageCache;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.lidroid.xutils.BitmapUtils;

public class AdapterComment extends BaseAdapter {

	private Context context;
	private List<CommentEntity> arrayList = new ArrayList<CommentEntity>();
	private BitmapUtils bitmapUtils;
	private SharedPreferenceUtil sp;

	public AdapterComment(Context context) {
		this.context = context;
		bitmapUtils = new BitmapUtils(context);
		sp = new SharedPreferenceUtil(context);
	}

	public void addAllList(List<CommentEntity> list) {
		if (list != null) {
			arrayList.clear();
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void appendList(List<CommentEntity> list) {
		if (list != null) {
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}
	
	public void insertComment(CommentEntity comment) {
		arrayList.add(0, comment);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public CommentEntity getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHoler viewHoler;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_comment, parent, false);
			viewHoler = new ViewHoler();
			viewHoler.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
			viewHoler.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHoler.tv_reply = (TextView) convertView.findViewById(R.id.tv_reply);
			viewHoler.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			convertView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHoler) convertView.getTag();
		}
		final CommentEntity comment = getItem(position);
		ImageCache.loadUserHeadImg(comment.userEntity.head_img_url, comment.userEntity.id, sp, bitmapUtils, viewHoler.iv_head);
		viewHoler.tv_name.setText(comment.userEntity.nick_name);
		if (!Validator.isEmptyString(comment.reply)) {
			viewHoler.tv_reply.setText(comment.reply);
			viewHoler.tv_reply.setVisibility(View.VISIBLE);
		}
		viewHoler.tv_content.setText(comment.comment);
		viewHoler.iv_head.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tools.openUser(context, comment.userEntity.id);
			}
		});
		return convertView;
	}
	
	class ViewHoler {
		ImageView iv_head;
		TextView tv_name, tv_reply, tv_content;
	}
}
