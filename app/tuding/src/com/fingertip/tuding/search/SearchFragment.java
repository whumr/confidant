package com.fingertip.tuding.search;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseFragment;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.entity.EventEntity.EventType;
import com.fingertip.tuding.search.widget.RefreshableListView;
import com.fingertip.tuding.search.widget.RefreshableListView.RefreshListener;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.UmengConfig.PAGE;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.EventUtil;
import com.fingertip.tuding.util.http.EventUtil.Type;
import com.fingertip.tuding.util.http.callback.EntityListCallback;

public class SearchFragment extends BaseFragment implements RefreshListener {
	
	private View mView;
	private RefreshableListView listView;
	private AdapterSearch adapterSearch;
	
	private Type seach_type;
	private SearchMainActivity search_activity;
	private int current_page;
	
	private SharedPreferenceUtil sp;
	
	public SearchFragment(SearchMainActivity search_activity, Type seach_type) {
		super();
		this.seach_type = seach_type;
		this.search_activity = search_activity;
		switch (seach_type) {
		case nearest:
			setPageName(PAGE.SEARCH_EVENT_NEAR);
			break;
		case newest:
			setPageName(PAGE.SEARCH_EVENT_NEW);
			break;
		case hotest:
			setPageName(PAGE.SEARCH_EVENT_HOT);
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mView == null){
			mView = inflater.inflate(R.layout.listview_common_refreshable, container, false);
			findViews();
			setupViews();
			listView.initData();
		}
		ViewGroup parent = (ViewGroup)mView.getParent();
		if(parent != null){
			parent.removeAllViewsInLayout();
		}
		return mView;
	}
	
	private void findViews() {
		listView = (RefreshableListView)mView.findViewById(R.id.listView);
		listView.setBackgroundResource(R.color.white);
		listView.setDividerHeight(0);
	}

	private void setupViews() {
		adapterSearch = new AdapterSearch(mView.getContext());
		listView.setAdapter(adapterSearch);
		listView.setOnItemClickListener(adapterSearch);
		listView.setRefreshListener(this);
		listView.setPageSize(20);
		
		sp = new SharedPreferenceUtil(search_activity);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	private void loadData(final boolean append) {
		float latitude = sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT);
		float longitude = sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG);
		if (latitude != 0 && longitude!= 0) {
			EventUtil.searchEvents(seach_type, EventType.ALL.getType(), longitude + "", latitude + "", current_page, new EntityListCallback<EventEntity>() {
				@Override
				public void succeed(List<EventEntity> list) {
					if (append)
						adapterSearch.appendAllData(list);
					else
						adapterSearch.addAllData(list);
					afterLoad(append, true, Validator.isEmptyList(list) ? 0 : list.size());
				}
				
				@Override
				public void fail(String error) {
					Tools.toastShort(search_activity, error);
					if (append)
						current_page--;
					afterLoad(append, false, 0);
				}
			});
		} else {
			Tools.toastShort(search_activity, "无法定位当前位置");
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
	public void onRefresh() {
		current_page = 1;
		loadData(false);
	}

	@Override
	public void onLoadMore() {
		current_page++;
		loadData(true);
	}
}
