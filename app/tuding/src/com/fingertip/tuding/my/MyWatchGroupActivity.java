package com.fingertip.tuding.my;

import java.util.List;

import android.os.Bundle;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseNavActivity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.my.adapter.AdapterMyWatchGroup;
import com.fingertip.tuding.search.widget.RefreshableListView;
import com.fingertip.tuding.search.widget.RefreshableListView.RefreshListener;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.UmengConfig.PAGE;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.UserUtil;
import com.fingertip.tuding.util.http.callback.EntityListCallback;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyWatchGroupActivity extends BaseNavActivity implements RefreshListener {
	
	private RefreshableListView listView;
	private AdapterMyWatchGroup adapter;
	
	private SharedPreferenceUtil sp;
	private int current_page;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_watch_group);
		findViews();
		setupViews();
		listView.initData();
	}
	
	protected void findViews() {
		super.findViews();
		listView = (RefreshableListView) findViewById(R.id.listView);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.my_watch_group);
		adapter = new AdapterMyWatchGroup(this);
		listView.setBackgroundResource(R.color.white);
		listView.setDividerHeight(0);
		listView.setAdapter(adapter);
//		listView.setOnItemClickListener(adapter);
		listView.setRefreshListener(this);
		listView.setPageSize(20);
		listView.setNoDataString("关注人新发布的活动，会第一时间出现在这哦。");
		sp = new SharedPreferenceUtil(this);
	}
	
	private void loadData(final int page, final boolean append) {
		float latitude = sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT);
		float longitude = sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG);
		if (latitude != 0 && longitude!= 0) {
			UserUtil.getMyWatchGroup(latitude + "", longitude + "", page, new EntityListCallback<EventEntity>() {
				
				@Override
				public void succeed(List<EventEntity> list) {
					if (append)
						adapter.appendAllData(list);
					else
						adapter.addAllData(list);
					afterLoad(append, true, Validator.isEmptyList(list) ? 0 : list.size());
					sp.setBooleanValue(UserSession.getInstance().getId(), SharedPreferenceUtil.HAS_NEW_WATCH, false);
				}
				
				@Override
				public void fail(String error) {
					Tools.toastShort(MyWatchGroupActivity.this, error);
					if (append)
						current_page--;
					afterLoad(append, false, 0);
				}
			});
		} else {
			Tools.toastShort(this, "无法定位当前位置");
			if (append)
				current_page--;
			afterLoad(append, false, 0);
		}
	}
	
	private void afterLoad(boolean append, boolean succeed, int size) {
		listView.setResultSize(!append, succeed, size);
		if (append)
			listView.loadComplete();
		else
			listView.refreshComplete();
	}
	
	@Override
	protected void setPageCount() {
		setPageName(PAGE.MY_WATCH_GROUP);
	}

	@Override
	public void onRefresh() {
		loadData(1, false);
		current_page = 1;
	}

	@Override
	public void onLoadMore() {
		current_page++;
		loadData(current_page, true);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageLoader.getInstance().clearMemoryCache();
	}
}
