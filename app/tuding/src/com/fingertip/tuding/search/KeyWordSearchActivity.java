package com.fingertip.tuding.search;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.search.widget.RefreshableListView;
import com.fingertip.tuding.search.widget.RefreshableListView.RefreshListener;
import com.fingertip.tuding.util.UmengConfig.PAGE;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.EventUtil;
import com.fingertip.tuding.util.http.callback.EntityListCallback;

public class KeyWordSearchActivity extends BaseActivity implements RefreshListener {
	
	private EditText keyword_edt;
	private ImageView search_img;
	private RefreshableListView listview;
	private AdapterSearch adapter_search;
	
	private SharedPreferenceUtil sp;
	private int current_page;
	private String last_keyword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_keyword);
		findViews();
		setupViews();
	}

	private void findViews() {
		keyword_edt = (EditText) findViewById(R.id.keyword_edt);
		search_img = (ImageView) findViewById(R.id.search_img);
		listview = (RefreshableListView) findViewById(R.id.listView);
	}
	
	private void setupViews() {
		keyword_edt.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive())
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					search(true);
				}
				return false;
			}
		});
		
		search_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				search(true);
			}
		});
		
		findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		adapter_search = new AdapterSearch(this);
		listview.setAdapter(adapter_search);
		listview.setOnItemClickListener(adapter_search);
		listview.setRefreshListener(this);
		listview.setPageSize(20);
		sp = new SharedPreferenceUtil(this);
	}
	
	private void search(final boolean new_search) {
		String key_word = keyword_edt.getText().toString();
		if (key_word.trim().length() == 0) {
			toastShort("请输入搜索关键词");
			afterLoad(new_search, false, 0);
		} else {
			if (new_search)
				last_keyword = key_word;
			else
				current_page++;
			float latitude = sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT);
			float longitude = sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG);
			if (latitude != 0 && longitude!= 0) {
				EventUtil.searchEventsByKeywords(last_keyword, longitude + "", latitude + "", current_page, new EntityListCallback<EventEntity>() {
					@Override
					public void succeed(List<EventEntity> list) {
						if (new_search)
							adapter_search.addAllData(list);
						else
							adapter_search.appendAllData(list);
						afterLoad(new_search, true, Validator.isEmptyList(list) ? 0 : list.size());
					}
					
					@Override
					public void fail(String error) {
						toastShort(error);
						if (!new_search)
							current_page--;
						afterLoad(new_search, false, 0);
					}
				});
			} else {
				toastShort("无法定位当前位置");
				if (!new_search)
					current_page--;
				afterLoad(new_search, false, 0);
			}
		}
	}
	
	private void afterLoad(boolean new_search, boolean succeed, int size) {
		listview.setResultSize(new_search, succeed, size);
		if (new_search)
			listview.refreshComplete();
		else
			listview.loadComplete();
	}
	
	@Override
	protected void setPageCount() {
		setPageName(PAGE.SEARCH_EVENT_BY_KEYWORD);
	}

	@Override
	public void onRefresh() {
		search(true);
	}

	@Override
	public void onLoadMore() {
		search(false);
	}
}
