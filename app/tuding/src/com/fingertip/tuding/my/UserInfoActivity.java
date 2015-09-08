package com.fingertip.tuding.my;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fingertip.tuding.Globals;
import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseNavActivity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.common.gif.GifView;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.entity.UserEntity;
import com.fingertip.tuding.my.adapter.AdapterUserEvent;
import com.fingertip.tuding.my.widget.SendMsgActivity;
import com.fingertip.tuding.util.ImageCache;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.UmengConfig.EVENT;
import com.fingertip.tuding.util.UmengConfig.PAGE;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.callback.DefaultCallback;
import com.fingertip.tuding.util.http.callback.EntityCallback;
import com.fingertip.tuding.util.http.callback.EntityListCallback;
import com.fingertip.tuding.util.http.common.ServerConstants;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_VALUES;
import com.fingertip.tuding.util.http.UserUtil;
import com.lidroid.xutils.BitmapUtils;
import com.umeng.analytics.MobclickAgent;

public class UserInfoActivity extends BaseNavActivity implements View.OnClickListener {
	
	public static String KEY_USER = "user";
	public static String KEY_USER_ID = "user_id";
	
	private ImageView user_info_head_img, user_info_sex_img, hidden_img;
	private TextView user_info_name_txt, user_info_up_count_txt, user_info_place_txt, user_info_mark_txt, event_empty_txt;
	private Button user_info_watch_btn, user_info_chat_btn, user_info_mask_btn;
	private GifView gifView;
	private LinearLayout empty_area;
	private ListView listView;
	private AdapterUserEvent adapter;
	
	private UserEntity user;
	
	private BitmapUtils bitmapUtils;
	private UserSession session;
	private SharedPreferenceUtil sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_user_info);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		user_info_head_img = (ImageView) findViewById(R.id.user_info_head_img);
		user_info_sex_img = (ImageView) findViewById(R.id.user_info_sex_img);
		hidden_img = (ImageView) findViewById(R.id.hidden_img);
		gifView = (GifView)findViewById(R.id.gifView);
		
		user_info_name_txt = (TextView) findViewById(R.id.user_info_name_txt);
		user_info_up_count_txt = (TextView) findViewById(R.id.user_info_up_count_txt);
		user_info_place_txt = (TextView) findViewById(R.id.user_info_place_txt);
		user_info_mark_txt = (TextView) findViewById(R.id.user_info_mark_txt);
		event_empty_txt = (TextView) findViewById(R.id.user_info_event_empty_txt);
		
		user_info_watch_btn = (Button) findViewById(R.id.user_info_watch_btn);
		user_info_chat_btn = (Button) findViewById(R.id.user_info_chat_btn);
		user_info_mask_btn = (Button) findViewById(R.id.user_info_mask_btn);
		
		empty_area = (LinearLayout) findViewById(R.id.user_info_event_empty);
		listView = (ListView) findViewById(R.id.user_info_event_listview);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.my_user_info);
		gifView.setGifImage(R.drawable.loading2);
		
		user_info_watch_btn.setOnClickListener(this);
		user_info_chat_btn.setOnClickListener(this);
		user_info_mask_btn.setVisibility(View.GONE);
		user_info_mask_btn.setOnClickListener(this);
		
		bitmapUtils = new BitmapUtils(this);
		session = UserSession.getInstance();
		sp = new SharedPreferenceUtil(this);
		
		adapter = new AdapterUserEvent(this, empty_area);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapter);
		loadUser();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//��ע
		case R.id.user_info_watch_btn:
			watch();
			break;
		//˽��
		case R.id.user_info_chat_btn:
			showMsgDialog();
			break;
		//����
		case R.id.user_info_mask_btn:
			mask();
			break;
		}
	}
	
	private void loadUser() {
		Intent intent = this.getIntent();
		if (intent != null && (intent.hasExtra(KEY_USER) || intent.hasExtra(KEY_USER_ID))) {
			if (intent.hasExtra(KEY_USER)) {
				user = (UserEntity) intent.getSerializableExtra(KEY_USER);
				if (user != null)
					setUserInfo();
			} else if (intent.hasExtra(KEY_USER_ID)) {
				String user_id = intent.getStringExtra(KEY_USER_ID);
				if (user_id == null)
					toastLong("δѡ���û����뷵������");
				else {
					UserUtil.getUserInfo(user_id, new EntityCallback<UserEntity>() {
						@Override
						public void succeed(UserEntity user) {
							UserInfoActivity.this.user = user;
							setUserInfo();
						}
						
						@Override
						public void fail(String error) {
							toastShort(error + "\n�뷵������");
						}
					});
				}
			}
		}
	}
	
	private void setUserInfo() {
//		 user_info_head_img, user_info_sex_img;
//		TextView user_info_name_txt, user_info_up_count_txt, user_info_province_txt, user_info_city_txt, user_info_mark_txt, user_info_event_empty_txt
		ImageCache.loadUserHeadImg(user.head_img_url, user.id, sp, bitmapUtils, user_info_head_img, hidden_img);
		if (ServerConstants.SEX_FEMALE.equals(user.sex))
			user_info_sex_img.setImageDrawable(getResources().getDrawable(R.drawable.icon_female));
		user_info_name_txt.setText(user.nick_name);
		user_info_up_count_txt.setText(user.up_count + "");
		user_info_place_txt.setText(user.place);
		user_info_mark_txt.setText(user.mark);
		
		//�ѹ�ע
		if (session.isLogin() && session.getWatcher_list().contains(user.id)) {
			user_info_watch_btn.setBackgroundColor(getResources().getColor(R.color.gray_ad));
			user_info_watch_btn.setText("�ѹ�ע");
			user_info_watch_btn.setOnClickListener(null);
		}
		
		loadUserEvents();
	}
	
	private void loadUserEvents() {
		UserUtil.getUserEvents(user.id, new EntityListCallback<EventEntity>() {
			
			@Override
			public void succeed(List<EventEntity> list) {
				adapter.addAllList(list);
				if (Validator.isEmptyList(list))
					event_empty_txt.setVisibility(View.VISIBLE);
				else
					empty_area.setVisibility(View.GONE);
				gifView.setVisibility(View.GONE);
			}
			
			@Override
			public void fail(String error) {
				gifView.setVisibility(View.GONE);
				event_empty_txt.setVisibility(View.VISIBLE);
				event_empty_txt.setText("��ȡ�û��ʧ��");
			}
		});
	}
	
	private void watch() {
		if (Tools.checkLogin(this)) {
			showProgressDialog(false);
			UserUtil.editWatch(user.id, PARAM_VALUES.LINK_WATCH, new DefaultCallback() {
				@Override
				public void succeed() {
					toastShort("��ע�ɹ�");
					session.getWatcher_list().add(user.id);
					user_info_watch_btn.setBackgroundColor(getResources().getColor(R.color.gray_ad));
					user_info_watch_btn.setText("�ѹ�ע");
					user_info_watch_btn.setOnClickListener(null);
					dismissProgressDialog();
				}
				
				@Override
				public void fail(String error) {
					toastShort("��עʧ��\n" + error);
					dismissProgressDialog();
				}
			});
			MobclickAgent.onEvent(this, EVENT.ADD_WATCHER, user.id);
		}
	}
	
	private void showMsgDialog() {
		if (Tools.checkLogin(this)) {
			Intent intent = new Intent();
			intent.setClass(this, SendMsgActivity.class);
			startActivityForResult(intent, R.id.send_msg);
		}
	}

	private void sendMsg(String msg) {
		showProgressDialog(false);
		UserUtil.sendMsg(user.id, msg, new DefaultCallback() {
			@Override
			public void succeed() {
				toastShort("���ͳɹ�");
				dismissProgressDialog();
			}
			
			@Override
			public void fail(String error) {
				toastShort("����ʧ��\n" + error);
				dismissProgressDialog();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case R.id.send_msg:
				sendMsg(data.getExtras().getString(Globals.COMMON_RESULT));
				break;
			}
		}
	}
	
	private void mask() {
		if (Tools.checkLogin(this)) {
			showProgressDialog(false);
			UserUtil.editWatch(user.id, PARAM_VALUES.LINK_HEI, new DefaultCallback() {
				@Override
				public void succeed() {
					toastShort("���γɹ�");
					dismissProgressDialog();
				}
				
				@Override
				public void fail(String error) {
					toastShort("����ʧ��\n" + error);
					dismissProgressDialog();
				}
			});
		}
	}
	
	@Override
	protected void setPageCount() {
		setPageName(PAGE.USER_DETAIL);
	}
}
