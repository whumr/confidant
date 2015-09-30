package com.fingertip.tuding.my;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.tuding.Globals;
import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseNavActivity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.my.widget.SetMarkActivity;
import com.fingertip.tuding.my.widget.SetNickActivity;
import com.fingertip.tuding.my.widget.SetPasswordActivity;
import com.fingertip.tuding.my.widget.SetSexActivity;
import com.fingertip.tuding.my.widget.zoom.SetZoomActivity;
import com.fingertip.tuding.util.ImageCache;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.http.UserUtil;
import com.fingertip.tuding.util.http.callback.DefaultCallback;
import com.fingertip.tuding.util.http.callback.JsonCallback;
import com.fingertip.tuding.util.http.common.ServerConstants;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;
import com.lidroid.xutils.BitmapUtils;

public class MyInfoActivity extends BaseNavActivity implements View.OnClickListener {
	
	private ImageView my_head_img, my_sex_img;
	private LinearLayout my_head, my_nick, my_sex, my_mark, my_place, my_reset_password;
	private TextView my_nick_txt, my_sex_txt, my_mark_txt, my_place_txt;
	
	private UserSession session;
	private BitmapUtils bitmapUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_info);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		my_head_img = (ImageView) findViewById(R.id.my_head_img);
		my_sex_img = (ImageView) findViewById(R.id.my_sex_img);
		
		my_head = (LinearLayout) findViewById(R.id.my_head);
		my_nick = (LinearLayout) findViewById(R.id.my_nick);
		my_sex = (LinearLayout) findViewById(R.id.my_sex);
		my_mark = (LinearLayout) findViewById(R.id.my_mark);
		my_place = (LinearLayout) findViewById(R.id.my_place);
		my_reset_password = (LinearLayout) findViewById(R.id.my_reset_password);
		
		my_nick_txt = (TextView) findViewById(R.id.my_nick_txt);
		my_sex_txt = (TextView) findViewById(R.id.my_sex_txt);
		my_mark_txt = (TextView) findViewById(R.id.my_mark_txt);
		my_place_txt = (TextView) findViewById(R.id.my_place_txt);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(getString(R.string.user_info));
		my_head.setOnClickListener(this);
		my_nick.setOnClickListener(this);
		my_sex.setOnClickListener(this);
		my_mark.setOnClickListener(this);
		my_place.setOnClickListener(this);
		my_reset_password.setOnClickListener(this);
		
		session = UserSession.getInstance();
		bitmapUtils = new BitmapUtils(this);
		initData();
	}
	
	private void initData() {
		my_place_txt.setText(session.getPlace());
		my_mark_txt.setText(session.getMark());
		my_nick_txt.setText(session.getNick_name());
		
		String sex = session.getSex();
		if (ServerConstants.SEX_MALE.equals(sex)) {
			my_sex_txt.setText(ServerConstants.SEX_MALE_S);
			my_sex_img.setImageResource(R.drawable.icon_male);
		} else if (ServerConstants.SEX_FEMALE.equals(sex)) {
			my_sex_txt.setText(ServerConstants.SEX_FEMALE_S);
			my_sex_img.setImageResource(R.drawable.icon_female);
		} else if (ServerConstants.SEX_UNKNOW.equals(sex)) {
			my_sex_txt.setText(ServerConstants.SEX_UNKNOW_S);
			my_sex_img.setImageResource(R.drawable.icon_male);
		}
		
		if (!ImageCache.setUserHeadImg(session.getId(), my_head_img))
			ImageCache.loadUserHeadImg(session.getHead_url(), session.getId(), getSP(), bitmapUtils, my_head_img);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_head:
			Tools.selectSinglePic(this, R.id.my_info_set_head, true);
			break;
		case R.id.my_nick:
			setNick();
			break;
		case R.id.my_sex:
			setSex();
			break;
		case R.id.my_mark:
			setMark();
			break;
		case R.id.my_place:
			setPlace();
			break;
		case R.id.my_reset_password:
			setPassword();
			break;
		}
	}
	
	private void setSex() {
		Intent intent = new Intent();
		intent.setClass(this, SetSexActivity.class);
		startActivityForResult(intent, R.id.my_info_set_sex);
	}

	private void setNick() {
		Intent intent = new Intent();
		intent.setClass(this, SetNickActivity.class);
		intent.putExtra(PARAM_KEYS.USER_NICK_NAME, my_nick_txt.getText().toString());
		startActivityForResult(intent, R.id.my_info_set_nick);
	}

	private void setMark() {
		Intent intent = new Intent();
		intent.setClass(this, SetMarkActivity.class);
		intent.putExtra(PARAM_KEYS.USER_MARK, my_mark_txt.getText().toString());
		startActivityForResult(intent, R.id.my_info_set_mark);
	}
	
	private void setPlace() {
		Intent intent = new Intent();
		intent.setClass(this, SetZoomActivity.class);
		startActivityForResult(intent, R.id.my_info_set_place);
	}
	
	private void setPassword() {
		Intent intent = new Intent();
		intent.setClass(this, SetPasswordActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case R.id.my_info_set_sex:
				modifyUserInfo(PARAM_KEYS.USER_SEX, data.getExtras().getString(Globals.COMMON_RESULT));
				break;
			case R.id.my_info_set_nick:
				modifyUserInfo(PARAM_KEYS.USER_NICK_NAME, data.getExtras().getString(Globals.COMMON_RESULT));
				break;
			case R.id.my_info_set_mark:
				modifyUserInfo(PARAM_KEYS.USER_MARK, data.getExtras().getString(Globals.COMMON_RESULT));
				break;
			case R.id.my_info_set_head:
				uploadHeadImg(data);
				break;
			case R.id.my_info_set_place:
				modifyUserInfo(PARAM_KEYS.USER_PLACE, data.getExtras().getString(Globals.COMMON_RESULT));
				break;
			}
		}
	}
	
	private void uploadHeadImg(Intent intent) {
		File img_file = new File(ImageCache.getCutImgPath());
		if (!img_file.exists())
			toastShort("�ü�ͼƬʧ��");
		else {
			Bitmap bitmap = BitmapFactory.decodeFile(ImageCache.getCutImgPath());
	        if (bitmap != null) {
	        	showProgressDialog(false);
	        	String user_id = session.getId();
	        	final Bitmap head_img = bitmap;
	    		//��ͼ
	    		final String big_head = ImageCache.getUserImgPath(user_id, false, true);
	    		ImageCache.saveUserImg(head_img, user_id, false, true);
	    		//Сͼ
	    		final String small_head = ImageCache.getUserImgPath(user_id, true, true);
	    		//ѹ��
	    		boolean saved = ImageCache.saveUserImg(compressImage(head_img), user_id, true, true);
	    		if (!saved)
	    			toastShort("����ͷ��ʧ��");
	    		else
	    			UserUtil.uploadHeadImg(big_head, small_head, new JsonCallback() {
						@Override
						public void succeed(JSONObject json) {
							try {
								String file_url = json.getString(PARAM_KEYS.UPLOAD_RESULT_URLFILE);
								String full_url = json.getString(PARAM_KEYS.UPLOAD_RESULT_URLFULL);
								modifyUserInfo(new String[]{PARAM_KEYS.USER_HEAD, PARAM_KEYS.USER_HEAD_BIG}, 
										new String[]{file_url, full_url});
							} catch (JSONException e) {
							}
						}
						
						@Override
						public void fail(String error) {
							toastShort(error);
							dismissProgressDialog();
						}
					});
	        } else
	    		toastShort("�ü�ͼƬʧ��");
		}
	}

	private void modifyUserInfo(final String key, final String value) {
		showProgressDialog(false);
		modifyUserInfo(new String[]{key}, new String[]{value});
	}
	
	private void modifyUserInfo(final String[] keys, final String[] values) {
		UserUtil.modifyUserInfo(keys, values, new DefaultCallback() {
			@Override
			public void succeed() {
				editCallBack(keys, values);
				dismissProgressDialog();
			}
			
			@Override
			public void fail(String error) {
				dismissProgressDialog();
				toastShort(error);
			}
		});
	}
	
	private void editCallBack(String[] keys, String[] values) {
		String user_id = session.getId();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String value = values[i];
			//ͷ��
			if (PARAM_KEYS.USER_HEAD.equals(key)) {
				ImageCache.saveTmpImg(user_id, true);
				getSP().setStringValue(user_id, SharedPreferenceUtil.HEADIMAGE, value);
				ImageCache.setUserHeadImg(session.getId(), my_head_img);
			} else if (PARAM_KEYS.USER_HEAD_BIG.equals(key)) {
				ImageCache.saveTmpImg(user_id, false);
				getSP().setStringValue(user_id, SharedPreferenceUtil.HEADIMAGE_FULL, value);
			//�ǳ�
			} else if (PARAM_KEYS.USER_NICK_NAME.equals(key)) {
				my_nick_txt.setText(value);
				session.setNick_name(value);
			} else if (PARAM_KEYS.USER_PLACE.equals(key)) {
				my_place_txt.setText(value);
				session.setPlace(value);
			} else if (PARAM_KEYS.USER_SEX.equals(key)) {
				if (ServerConstants.SEX_MALE.equals(value)) {
					my_sex_txt.setText(ServerConstants.SEX_MALE_S);
					my_sex_img.setImageResource(R.drawable.icon_male);
				} else if (ServerConstants.SEX_FEMALE.equals(value)) {
					my_sex_txt.setText(ServerConstants.SEX_FEMALE_S);
					my_sex_img.setImageResource(R.drawable.icon_female);
				} else if (ServerConstants.SEX_UNKNOW.equals(value)) {
					my_sex_txt.setText(ServerConstants.SEX_UNKNOW_S);
					my_sex_img.setImageResource(R.drawable.icon_male);
				}
				session.setSex(value);
			} else if (PARAM_KEYS.USER_MARK.equals(key)) {
				my_mark_txt.setText(value);
				session.setMark(value);
			}
		}
	}
	
	private Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
		int options = 100;
		while ( baos.toByteArray().length / 1024 > 50) {	//ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��		
			baos.reset();//����baos�����baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��
			options /= 2;//ÿ�ζ�����
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ
		return bitmap;
	}
}
