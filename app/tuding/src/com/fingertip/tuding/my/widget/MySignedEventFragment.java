package com.fingertip.tuding.my.widget;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseFragment;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.my.MyEventActivity;
import com.fingertip.tuding.my.adapter.AdapterMySignedEvent;
import com.fingertip.tuding.search.widget.RefreshableListView;
import com.fingertip.tuding.search.widget.RefreshableListView.RefreshListener;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.EventUtil;
import com.fingertip.tuding.util.http.callback.EntityListCallback;

public class MySignedEventFragment extends BaseFragment implements RefreshListener {
	
	private View mView;
	private RefreshableListView listView;
	private AdapterMySignedEvent adapter;
	private MyEventActivity activity;
	
	private int current_page;
	
	public MySignedEventFragment(MyEventActivity activity) {
		super();
		this.activity = activity;
		setPageName("my_signed_event");
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
		if (parent != null)
			parent.removeAllViewsInLayout();
		return mView;
	}
	
	private void findViews() {
		listView = (RefreshableListView)mView.findViewById(R.id.listView);
		listView.setBackgroundResource(R.color.white);
		listView.setDividerHeight(5);
	}

	private void setupViews() {
		adapter = new AdapterMySignedEvent(activity);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapter);
		listView.setRefreshListener(this);
		listView.setPageSize(20);
	}
	
	private void loadData(final boolean append) {
		EventUtil.getSignedEvents(current_page, new EntityListCallback<EventEntity>() {
			@Override
			public void succeed(List<EventEntity> list) {
				if (append)
					adapter.appendAllData(list);
				else
					adapter.addAllData(list);
				afterLoad(append, true, Validator.isEmptyList(list) ? 0 : list.size());
			}
			
			@Override
			public void fail(String error) {
				Tools.toastShort(activity, "º”‘ÿ ß∞‹£¨«Î÷ÿ ‘");
				if (append)
					current_page--;
				afterLoad(append, false, 0);
			}
		});
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
