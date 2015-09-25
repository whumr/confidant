package com.fingertip.tuding.my.widget;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.entity.UserEntity;

public class UserSearchDialog extends Dialog implements OnClickListener {
	
	private TextView close_txt, cancel_txt;
	private ListView listView;
	private Context context;
	private OnUserSelected onUserSelected;
	private UserSearchAdapter adapter;
	private int screen_width, screen_height;
	private List<UserEntity> list;

	public UserSearchDialog(Context context, int screen_width, int screen_height, OnUserSelected onUserSelected) {
		super(context, R.style.commonDialogStyle);
		this.screen_width = screen_width;
		this.screen_height = screen_height;
		this.context = context;
		this.onUserSelected = onUserSelected;
		setupView();
	}

	@SuppressLint("InflateParams")
	private void setupView() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_user_search, null);
		setContentView(view, new LayoutParams((int)(screen_width * 0.9), (int)(screen_height * 0.8)));
		close_txt = (TextView)findViewById(R.id.close_txt);
		cancel_txt = (TextView)findViewById(R.id.cancel_txt);
		close_txt.setOnClickListener(this);
		cancel_txt.setOnClickListener(this);
		listView = (ListView)findViewById(R.id.listView);
		adapter = new UserSearchAdapter(context);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (onUserSelected != null)
					onUserSelected.onSelected(list.get(position));
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		adapter.addUsers(list);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.close_txt:
		case R.id.cancel_txt:
			dismiss();
			break;
		}
	}
	
	public void setList(List<UserEntity> list) {
		this.list = list;
	}

	public interface OnUserSelected {
		public void onSelected(UserEntity user);
	}
}
