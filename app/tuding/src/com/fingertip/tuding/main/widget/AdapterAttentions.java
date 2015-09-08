package com.fingertip.tuding.main.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.entity.WatchEntity;
import com.fingertip.tuding.util.Tools;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

public class AdapterAttentions extends BaseAdapter {

	private Context context;
	private List<WatchEntity> arrayList = new ArrayList<WatchEntity>();
	private BitmapUtils bitmapUtils;
	
	private SparseBooleanArray selected_array = new SparseBooleanArray();

	public AdapterAttentions(Context c) {
		context = c;
		bitmapUtils = new BitmapUtils(context);
	}

	public void addAllList(List<WatchEntity> list) {
		if (list != null) {
			arrayList.clear();
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void appendList(List<WatchEntity> list) {
		if (list != null) {
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}
	
	public SparseBooleanArray getSelected(){
		return selected_array;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHoler viewHoler;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_attentionselection, parent, false);
			viewHoler = new ViewHoler();
			viewHoler.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
			viewHoler.my_watcher_name = (TextView) convertView.findViewById(R.id.my_watcher_name);
			viewHoler.my_watcher_mark = (TextView) convertView.findViewById(R.id.my_watcher_mark);
			viewHoler.my_watcher_place = (TextView) convertView.findViewById(R.id.my_watcher_place);
			viewHoler.my_watcher_up_count = (TextView) convertView.findViewById(R.id.my_watcher_up_count);
			viewHoler.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);

			convertView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHoler) convertView.getTag();
		}
		WatchEntity watch = (WatchEntity) getItem(position);

		bitmapUtils.display(viewHoler.iv_head, watch.user.head_img_url, new BitmapLoadCallBack<ImageView>() {
			@Override
			public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
				try {
					container.setImageBitmap(Tools.toRoundCorner(bitmap));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
						
			}
		});

		viewHoler.my_watcher_name.setText(watch.user.nick_name);
		viewHoler.my_watcher_mark.setText(watch.user.mark);
		viewHoler.my_watcher_place.setText(watch.user.place);
		viewHoler.my_watcher_up_count.setText(watch.up_count + "");

		if (!selected_array.get(position, false))
			viewHoler.iv_check.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_unchecked));
		else
			viewHoler.iv_check.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_checked));
		return convertView;
	}
	
	public void putSelectPosition(int position){
		if (!selected_array.get(position, false))
			selected_array.put(position, true);
		else
			selected_array.delete(position);
		notifyDataSetChanged();
	}

	class ViewHoler {
		ImageView iv_head;
		TextView my_watcher_name, my_watcher_mark, my_watcher_place, my_watcher_up_count;
		ImageView iv_check;

	}
}
