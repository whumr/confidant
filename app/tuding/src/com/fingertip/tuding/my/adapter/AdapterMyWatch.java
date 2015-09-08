package com.fingertip.tuding.my.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.WatchEntity;
import com.fingertip.tuding.my.MyWatchListActivity;
import com.fingertip.tuding.my.widget.Deleteable;
import com.fingertip.tuding.util.ImageCache;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.http.UserUtil;
import com.fingertip.tuding.util.http.callback.DefaultCallback;
import com.lidroid.xutils.BitmapUtils;

public class AdapterMyWatch extends BaseAdapter implements OnItemClickListener, Deleteable {
	
	private MyWatchListActivity activity;
	private View empty_view;
	private List<WatchEntity> arrayList  = new ArrayList<WatchEntity>();
	
	private BitmapUtils bitmapUtils;
	private SharedPreferenceUtil sp;
	
	private boolean delete;
	private List<String> delete_ids = new ArrayList<String>();
	
	public AdapterMyWatch(MyWatchListActivity activity, View empty_view){
		this.activity = activity;
		this.empty_view = empty_view;
		
		sp = new SharedPreferenceUtil(activity);
		bitmapUtils = new BitmapUtils(activity);
		this.delete = false;
	}

	public void addAllList(List<WatchEntity> list){
		if(list != null){
			arrayList.clear();
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}
	
	public void appendList(List<WatchEntity> list){
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.list_item_my_watch, parent, false);
			viewHoler = new ViewHoler();
			viewHoler.my_watcher_head = (ImageView)convertView.findViewById(R.id.my_watcher_head);
			viewHoler.my_watcher_name = (TextView)convertView.findViewById(R.id.my_watcher_name);
			viewHoler.my_watcher_mark = (TextView)convertView.findViewById(R.id.my_watcher_mark);
			viewHoler.my_watcher_place = (TextView)convertView.findViewById(R.id.my_watcher_place);
			viewHoler.my_watcher_up_count = (TextView)convertView.findViewById(R.id.my_watcher_up_count);
			viewHoler.v_delete_check = (LinearLayout)convertView.findViewById(R.id.v_delete_check);
			viewHoler.iv_delete_check = (ImageView)convertView.findViewById(R.id.iv_delete_check);
			
			convertView.setTag(viewHoler);
		}else {
			viewHoler = (ViewHoler)convertView.getTag();
		}
		WatchEntity watch = (WatchEntity)getItem(position);
		try {
			ImageCache.loadUserHeadImg(watch.user.head_img_url, watch.user.id, sp, bitmapUtils, viewHoler.my_watcher_head);
		} catch (Exception e) {
		}
		viewHoler.my_watcher_name.setText(watch.user.nick_name);
		viewHoler.my_watcher_mark.setText(watch.user.mark);
		viewHoler.my_watcher_place.setText(watch.user.place);
		viewHoler.my_watcher_up_count.setText(watch.up_count + "");
		viewHoler.id = watch.user.id;
		if (delete) {
			viewHoler.v_delete_check.setVisibility(View.VISIBLE);
			if (viewHoler.checked)
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_checked));
			else
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_unchecked));
		} else {
			viewHoler.v_delete_check.setVisibility(View.GONE);
			viewHoler.checked = false;
		}
		return convertView;
	}

	class ViewHoler{
		ImageView my_watcher_head;
		TextView my_watcher_name, my_watcher_mark, my_watcher_place, my_watcher_up_count;
		LinearLayout v_delete_check;
		ImageView iv_delete_check;
		
		String id;
		boolean checked;
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
		if (delete_ids.size() > 0) {
			activity.showProgressDialog(false);
			for (String id : delete_ids) {
				deleteWatch(id);
			}
		}
	}

	@Override
	public int size() {
		return arrayList.size();
	}

	@Override
	public int selectedSize() {
		return delete_ids.size();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		ViewHoler viewHoler = (ViewHoler) view.getTag();
		if(delete) {
			if (viewHoler.checked) {
				delete_ids.remove(viewHoler.id);
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_unchecked));
			} else {
				delete_ids.add(viewHoler.id);
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_checked));
			}
			viewHoler.checked = !viewHoler.checked;
			activity.setSelectedCount(delete_ids.size());
		} else {
			//��ת���ҳ
			Tools.openUser(activity, viewHoler.id);
		}
	}
	
	private void deleteWatch(final String user_id) {
		final UserSession session = UserSession.getInstance();
		UserUtil.deleteMyWatcher(user_id, new DefaultCallback() {
			
			@Override
			public void succeed() {
				session.getWatcher_list().remove(user_id);
				afterDelete(user_id, true);
			}
			
			@Override
			public void fail(String error) {
				activity.toastShort(error);
				afterDelete(user_id, false);
			}
		});
	}
	
	private void afterDelete(String user_id, boolean delete) {
		delete_ids.remove(user_id);
		if (delete) {
			for (Iterator<WatchEntity> it = arrayList.iterator(); it.hasNext();) {
				WatchEntity watch = it.next();
				if (user_id.equals(watch.user.id))
					it.remove();
			}
		}
		if (delete_ids.isEmpty()) {
			activity.finishDelete();
			activity.dismissProgressDialog();
		}
	}
}
