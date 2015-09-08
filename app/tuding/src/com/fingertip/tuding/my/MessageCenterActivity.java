package com.fingertip.tuding.my;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseNavActivity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.common.gif.GifView;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.MessageEntity;
import com.fingertip.tuding.my.adapter.AdapterMessage;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.UmengConfig.EVENT;
import com.fingertip.tuding.util.UmengConfig.PAGE;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.UserUtil;
import com.fingertip.tuding.util.http.callback.EntityListCallback;
import com.umeng.analytics.MobclickAgent;

public class MessageCenterActivity extends BaseNavActivity implements View.OnClickListener {
	
	private ImageView nav_delete_btn, nav_right_btn;
	private TextView tv_cancel;
	private Button btn_delete;
	private LinearLayout msg_empty;
	private ListView listView;
	private AdapterMessage adapter;
	
	private UserSession session;
	private SharedPreferenceUtil sp;
	
	private View view_loading;
	private boolean delete = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_center);
		findViews();
		setupViews();
		
		showLoadingView(true);
		loadData();
		MobclickAgent.onEvent(this, EVENT.MSG_CENTER);
	}
	
	protected void findViews() {
		super.findViews();
		
		view_loading = findViewById(R.id.layout_loading);
		GifView gifView = (GifView)findViewById(R.id.gifView);
		gifView.setGifImage(R.drawable.loading2);
		
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		nav_delete_btn = (ImageView) findViewById(R.id.nav_right_btn2);
		nav_right_btn = (ImageView) findViewById(R.id.nav_right_btn);
		btn_delete = (Button) findViewById(R.id.btn_delete);
		
		msg_empty = (LinearLayout) findViewById(R.id.msg_empty);
		listView = (ListView) findViewById(R.id.msg_listView);
		adapter = new AdapterMessage(this, msg_empty);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapter);
	}
	
	private void showLoadingView(boolean flag){
		if(flag){
			view_loading.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} else {
			view_loading.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.msg_center);
		tv_cancel.setOnClickListener(this);
		nav_right_btn.setVisibility(View.GONE);
		nav_delete_btn.setImageDrawable(getResources().getDrawable(R.drawable.icon_my_event_delete));
		nav_delete_btn.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
		btn_delete.setBackgroundColor(getResources().getColor(R.color.gray_ad));
		session = UserSession.getInstance();
		sp = new SharedPreferenceUtil(this);
	}

	private void loadData() {
		//������Ϣ����״̬
		sp.setBooleanValue(SharedPreferenceUtil.HAS_NEW_MESSAGE, false);
		//�ȼ��ر�����Ϣ
		new AsyncTask<String, Integer, List<MessageEntity>>() {

			@Override
			protected List<MessageEntity> doInBackground(String... params) {
				return Tools.getMessages(MessageCenterActivity.this, session.getId(), 10, 1);
			}
			
			protected void onPostExecute(List<MessageEntity> result) {
				if (!Validator.isEmptyList(result))
					adapter.appendList(result);
			};

		}.execute();
		//�ӷ�������ȡ��Ϣ
		UserUtil.loadUserMsg(new EntityListCallback<MessageEntity>() {
			
			@Override
			public void succeed(List<MessageEntity> list) {
				showLoadingView(false);
				adapter.insertList(list);
				if (!Validator.isEmptyList(list)) {
					for (MessageEntity msg : list)
						msg.receiver_id = session.getId();
					Tools.saveMessages(MessageCenterActivity.this, list);
				}
			}
			
			@Override
			public void fail(String error) {
				showLoadingView(false);
				toastShort(error);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (delete)
					cancelDelete();
				else
					finish();
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nav_right_btn2:
			beginDelete();
			break;
		case R.id.tv_cancel:
			cancelDelete();
			break;
		case R.id.btn_delete:
			doDelete();
			break;
		}
	}

	private void beginDelete() {
		if (adapter.size() > 0) {
			nav_delete_btn.setVisibility(View.GONE);
			tv_cancel.setVisibility(View.VISIBLE);
			btn_delete.setVisibility(View.VISIBLE);
			adapter.begainDelete();
			delete = true;
		} else
			toastShort("�����ݿ�ɾ��");
	}
	
	private void cancelDelete() {
		nav_delete_btn.setVisibility(View.VISIBLE);
		tv_cancel.setVisibility(View.GONE);
		btn_delete.setVisibility(View.GONE);
		adapter.endDelete();
		delete = false;
	}
	
	private void doDelete() {
		if (adapter.selectedSize() > 0)
			adapter.doDelete();
		else
			toastShort("������ѡ��һ��");
	}
	
	public void finishDelete() {
		nav_delete_btn.setVisibility(View.VISIBLE);
		tv_cancel.setVisibility(View.GONE);
		btn_delete.setVisibility(View.GONE);
		adapter.endDelete();
		delete = false;
	}
	
	public void setSelectedCount(int count) {
		if (count > 0) {
			btn_delete.setText("(" + count + ") ɾ��");
			btn_delete.setBackgroundColor(getResources().getColor(R.color.blue_msg));
		} else {
			btn_delete.setText("ɾ��");
			btn_delete.setBackgroundColor(getResources().getColor(R.color.gray_ad));
		}
	}
	
	@Override
	protected void setPageCount() {
		setPageName(PAGE.MSG_CENTER);
	}
}
