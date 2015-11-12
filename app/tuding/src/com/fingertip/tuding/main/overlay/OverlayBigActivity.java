package com.fingertip.tuding.main.overlay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.fingertip.tuding.Globals;
import com.fingertip.tuding.R;
import com.fingertip.tuding.barcode.ScanBarcodeActivity;
import com.fingertip.tuding.barcode.ScanBarcodeActivity.BarcodeValidator;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.common.ShareDialog;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.CommentEntity;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.entity.SignerEntity;
import com.fingertip.tuding.entity.EventEntity.EventType;
import com.fingertip.tuding.entity.ShareEntity;
import com.fingertip.tuding.main.widget.AdapterComment;
import com.fingertip.tuding.main.widget.AdapterSigner;
import com.fingertip.tuding.main.widget.PublicRecommendActivity;
import com.fingertip.tuding.main.widget.SignCertDialog;
import com.fingertip.tuding.main.widget.SignedDialog;
import com.fingertip.tuding.main.widget.SignedMenuDialog;
import com.fingertip.tuding.my.UserInfoActivity;
import com.fingertip.tuding.setting.ReportActivity;
import com.fingertip.tuding.util.ImageCache;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.UmengConfig.EVENT;
import com.fingertip.tuding.util.UmengConfig.PAGE;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.EventUtil;
import com.fingertip.tuding.util.http.UserUtil;
import com.fingertip.tuding.util.http.callback.DefaultCallback;
import com.fingertip.tuding.util.http.callback.EntityListCallback;
import com.google.zxing.Result;
import com.lidroid.xutils.BitmapUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 地图大气泡
 * 
 * @author Administrator
 *
 */
public class OverlayBigActivity extends BaseActivity implements OnClickListener {

	private static int REQUEST_CODE_COMMENT = 1000, REQUEST_CODE_CHECK = 1001;
	
	private LinearLayout layout_main, layout_img, layout_collection, layout_share, layout_signup, sign_list_layout;
	private ImageView iv_head, iv_topic, iv_img_signup;
	private TextView tv_title, tv_name, tv_time, tv_detail, tv_collection, 
		tv_btnCollection, tv_recommendTopic, tv_recommend, tv_accusation, no_comment_txt, load_comment_txt,
		tv_sign_topic, tv_check, no_sign_txt, load_sign_txt;
	private WebView wv_detail;
	
	private EventEntity event;
	private boolean rich_event = false;
	private BitmapUtils bitmapUtils;
	private SharedPreferenceUtil sp;
	private SignedDialog signedDialog;
	private SignCertDialog signCertDialog;
	private SignedMenuDialog signedMenuDialog;

	private ListView comment_listview, sign_listview;
	private AdapterComment adapter_comment;
	private AdapterSigner adapter_signer;
	
	// 屏幕左右间隔
	private int screenMargin = 80;
	/** 当前评论页数 **/
	private int pageIndex_recommend = 1;
	private boolean isFirst = true;
	private int screen_width, screen_height;
	private UserSession session;
	private BarcodeValidater barcodeValidater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overlaybig);
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.width = getResources().getDisplayMetrics().widthPixels - screenMargin;
		findViews();
		setupViews();
		initEntity();
		initFavor();
	}

	private void findViews() {
		layout_main = (LinearLayout) findViewById(R.id.layout_main);
		layout_img = (LinearLayout) findViewById(R.id.layout_img);
		layout_collection = (LinearLayout) findViewById(R.id.layout_collection);
		sign_list_layout = (LinearLayout) findViewById(R.id.sign_list_layout);
		layout_share = (LinearLayout) findViewById(R.id.layout_share);
		layout_signup = (LinearLayout) findViewById(R.id.layout_signup);
		
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_detail = (TextView) findViewById(R.id.tv_detail);
		tv_collection = (TextView) findViewById(R.id.tv_collection);
		tv_btnCollection = (TextView) findViewById(R.id.btn_collection);
		tv_recommendTopic = (TextView) findViewById(R.id.tv_recommend_topic);
		tv_recommend = (TextView) findViewById(R.id.tv_recommend);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_accusation = (TextView) findViewById(R.id.tv_accusation);
		no_comment_txt = (TextView) findViewById(R.id.no_comment_txt);
		load_comment_txt = (TextView) findViewById(R.id.load_comment_txt);
		tv_sign_topic = (TextView) findViewById(R.id.tv_sign_topic);
		tv_check = (TextView) findViewById(R.id.tv_check);
		no_sign_txt = (TextView) findViewById(R.id.no_sign_txt);
		load_sign_txt = (TextView) findViewById(R.id.load_sign_txt);
		
		iv_head = (ImageView) findViewById(R.id.iv_head);
		iv_topic = (ImageView) findViewById(R.id.iv_topic);
		iv_img_signup = (ImageView) findViewById(R.id.iv_img_signup);

		wv_detail = (WebView) findViewById(R.id.wv_detail);

		comment_listview = (ListView) findViewById(R.id.comment_listview);
		sign_listview = (ListView) findViewById(R.id.sign_listview);
	}

	private void setupViews() {
		ViewTreeObserver vto = layout_main.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				WindowManager.LayoutParams layoutParams = getWindow()
						.getAttributes();
				int heightPixels = getResources().getDisplayMetrics().heightPixels;
				if (isFirst && layout_main.getHeight() >= (heightPixels - 150)) {
					layoutParams.height = heightPixels - 150;
					getWindow().setAttributes(layoutParams);
					isFirst = false;
				}
			}
		});

		tv_accusation.setOnClickListener(this);
		tv_recommend.setOnClickListener(this);
		tv_btnCollection.setOnClickListener(this);
		findViewById(R.id.btn_share).setOnClickListener(this);
		iv_head.setOnClickListener(this);

		layout_collection.setOnClickListener(this);
		layout_share.setOnClickListener(this);
		layout_signup.setOnClickListener(this);

		session = UserSession.getInstance();
		
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		screen_width = outMetrics.widthPixels;
		screen_height = outMetrics.heightPixels;
		
		adapter_comment = new AdapterComment(this);
		comment_listview.setAdapter(adapter_comment);
		comment_listview.setOnItemClickListener(reply_listener);
		load_comment_txt.setOnClickListener(this);
		
		tv_check.setOnClickListener(this);
		load_sign_txt.setOnClickListener(this);
		
		barcodeValidater = new BarcodeValidater();
	}

	private void initEntity() {
		event = (EventEntity) getIntent().getSerializableExtra(EXTRA_PARAM);
		if (event == null) {
			toastShort("数据错误");
			finish();
			return;
		}
		rich_event = EventEntity.SHOWMODE_RICH.equals(event.showmode);
		tv_title.setText(event.title);
		tv_name.setText(event.sender.nick_name);
		if (EventEntity.SHOWMODE_DEFAULT.equals(event.showmode)) {
			wv_detail.setVisibility(View.GONE);
			tv_detail.setVisibility(View.VISIBLE);
			tv_detail.setText(event.content);
		} else if (EventEntity.SHOWMODE_RICH.equals(event.showmode)) {
			wv_detail.setVisibility(View.VISIBLE);
			wv_detail.getSettings().setDefaultTextEncodingName("UTF-8");
			tv_detail.setVisibility(View.GONE);
			wv_detail.loadData(event.content, "text/html; charset=UTF-8", "UTF-8");
		}
		tv_collection.setText("" + event.viewcount);// 调用浏览次数
		tv_recommendTopic.setText("评论（" + event.replycount + "）");
		tv_time.setText(event.send_time_str);

		bitmapUtils = new BitmapUtils(this);
		sp = new SharedPreferenceUtil(this);
		
		ImageCache.loadUserHeadImg(event.sender.head_img_url, event.sender.id, sp, bitmapUtils, iv_head);

		if (rich_event)
			iv_topic.setVisibility(View.GONE);
		else {
			String topImag = Validator.isEmptyList(event.pics_big) ? null : event.pics_big.get(0);
			if (!Validator.isEmptyString(topImag))
				ImageCache.loadUrlImg(topImag, iv_topic, bitmapUtils);
		}
		
		if (rich_event) {
			layout_img.setVisibility(View.GONE);
			findViewById(R.id.tv_img_tip).setVisibility(View.GONE);
		} else {
			ImageView imageView = null;
			LinearLayout layout_img_horizontal = null;
			LayoutParams layoutParams_horizontal = null;
			LayoutParams layoutParams_vertical = null;
			
			// 图片
			int paddingRight = 17;
			int maxWidth = (getResources().getDisplayMetrics().widthPixels - screenMargin - paddingRight * 4) / 3;
			
			layoutParams_vertical = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutParams_vertical.topMargin = paddingRight / 2;
			layoutParams_vertical.bottomMargin = paddingRight / 2;
			
			for (int i = 0; i < event.pics_small.size(); i++) {
				layoutParams_horizontal = new LayoutParams(maxWidth, maxWidth);
				if (i % 3 == 0) {
					layout_img_horizontal = new LinearLayout(this);
					layout_img.addView(layout_img_horizontal, layoutParams_vertical);
					layoutParams_horizontal.leftMargin = paddingRight / 2;
				}
				if (i % 3 == 2)
					layoutParams_horizontal.rightMargin = 0;
				else
					layoutParams_horizontal.rightMargin = paddingRight;
				imageView = new ImageView(this);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setAdjustViewBounds(true);
				// imageView.setScaleType(ScaleType.CENTER);
				ImageCache.loadUrlImg(event.pics_small.get(i), imageView, bitmapUtils);
				imageView.setTag(i);
				imageView.setOnClickListener(imgOnClickListener);
				layout_img_horizontal.addView(imageView, layoutParams_horizontal);
			}
			imageView = null;
		}
		setOverlayType();
		
		boolean is_owner = session.isLogin() && session.getId().equals(event.sender.id);
		if (is_owner)
			tv_check.setVisibility(View.VISIBLE);
		adapter_signer = new AdapterSigner(this, is_owner);
		sign_listview.setAdapter(adapter_signer);
		//评论
		requestRecommendList();
		//报名
		requestSigner();
	}

	private void initFavor() {
		if (session.isLogin()) {
			if (session.isLoad_favor()) {
				if (session.getFavor_event_list().contains(event.id))
					setCollected();
			} else
				UserUtil.loadFavorList(null);
			if (session.isLoad_sign_event()) {
				if (session.getSign_list().contains(event.id))
					setSigned(true);
			} else
				UserUtil.loadSignedList(null);
		}
	}
	
	private void setCollected() {
		findViewById(R.id.iv_collection_starts).setBackgroundResource(R.drawable.collection_starts_p);
		tv_btnCollection.setOnClickListener(null);
		tv_btnCollection.setText("已收藏");
	}
	
	private void setSigned(boolean signed) {
		iv_img_signup.setImageDrawable(getResources().getDrawable(signed ? R.drawable.icon_signuped : R.drawable.icon_signup));
	}

	private OnClickListener imgOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Tools.previewPics(OverlayBigActivity.this, (ArrayList<String>)event.pics_big, (Integer) v.getTag());
		}
	};

	private void setOverlayType() {
		if (event.event_type == EventType.SPORTS) {
			layout_main.setBackgroundResource(R.drawable.bg_recommend);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_recommend);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_recommend);
		} else if (event.event_type == EventType.SOCIALITY) {
			layout_main.setBackgroundResource(R.drawable.bg_friend);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_friend);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_friend);
		} else if (event.event_type == EventType.PERFORM) {
			layout_main.setBackgroundResource(R.drawable.bg_activity);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_activity);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_activity);
		} else if (event.event_type == EventType.STUDY) {
			layout_main.setBackgroundResource(R.drawable.bg_event);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_event);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_event);
		} else if (event.event_type == EventType.SPECIAL) {
			layout_main.setBackgroundResource(R.drawable.bg_buy);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_buy);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_buy);
		} else if (event.event_type == EventType.OTHER) {
			layout_main.setBackgroundResource(R.drawable.bg_activity);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_activity);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_activity);
		} else {
			layout_main.setBackgroundResource(R.drawable.bg_activity);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_activity);
		}
	}

	@Override
	public void onClick(View view) {
		Intent intent = null;
		switch (view.getId()) {
		case R.id.iv_head:
			intent = new Intent();
			intent.setClass(this, UserInfoActivity.class);
			intent.putExtra(UserInfoActivity.KEY_USER_ID, event.sender.id);
			startActivity(intent);
			break;
		case R.id.tv_accusation:// 举报
			intent = new Intent();
			intent.setClass(this, ReportActivity.class);
			intent.putExtra(EXTRA_PARAM, event);
			startActivity(intent);
			break;
		case R.id.tv_recommend:// 评论
			if (Tools.checkLogin(this)) {
				intent = new Intent();
				intent.setClass(this, PublicRecommendActivity.class);
				intent.putExtra(EXTRA_PARAM, event);
				startActivity(intent);
			}
			break;
		case R.id.layout_collection://
			if (Tools.checkLogin(this)) {
				showProgressDialog(false);
				requestCollecion();
			}
			break;
		case R.id.layout_share://
			ShareEntity shareEntity = new ShareEntity();
			shareEntity.shareTitle = event.title;
			shareEntity.shareContent = event.getShareContent();
			shareEntity.targetUrl = event.getShareUrl();
			shareEntity.aid = event.id;
			shareEntity.sender_id = event.sender.id;
			intent = new Intent();
			intent.setClass(this, ShareDialog.class);
			intent.putExtra(EXTRA_PARAM, shareEntity);
			startActivity(intent);
			break;
		case R.id.layout_signup:
			if (Tools.checkLogin(this))
				signup();
			break;
		case R.id.load_comment_txt:
			requestRecommendList();
			break;
		case R.id.tv_check:
			if (Tools.checkLogin(this)) {
				Intent in = new Intent();
				in.setClass(this, ScanBarcodeActivity.class);
				in.putExtra(ScanBarcodeActivity.KEY_VALIDATOR, barcodeValidater);
				startActivityForResult(in, REQUEST_CODE_CHECK);
			}
			break;
		}
	}

	/** 评论回复 **/
	private OnItemClickListener reply_listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (Tools.checkLogin(OverlayBigActivity.this)) {
				Intent intent = new Intent();
				intent.setClass(OverlayBigActivity.this,PublicRecommendActivity.class);
				intent.putExtra(BaseActivity.EXTRA_PARAM, event);
				intent.putExtra(PublicRecommendActivity.EXTRA_COMMENT, adapter_comment.getItem(position));
				startActivityForResult(intent, REQUEST_CODE_COMMENT);
			}
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && data != null) {
			if (requestCode == REQUEST_CODE_COMMENT) {
				try {
					CommentEntity comment = (CommentEntity)data.getSerializableExtra(EXTRA_PARAM);
					adapter_comment.insertComment(comment);
					setComment(null);
					event.replycount = event.replycount + 1;
					tv_recommendTopic.setText("评论（" + event.replycount + "）");
				} catch (Exception e) {
				}
			//验票
			} else if (requestCode == REQUEST_CODE_CHECK) {
				String result = data.getStringExtra(Globals.COMMON_RESULT);
				String user_id = UserUtil.parseBarcode(result);
				if (user_id != null)
					Tools.openUser(this, user_id);
				else
					Tools.viewWeb(this, result);
			}
		}
	};
	
	/** 收藏 **/
	private void requestCollecion() {
		EventUtil.favorEvent(event.id, new DefaultCallback() {
			@Override
			public void succeed() {
				dismissProgressDialog();
				setCollected();
				toastShort("收藏成功");
				session.getFavor_event_list().add(event.id);
			}

			@Override
			public void fail(String error) {
				dismissProgressDialog();
				toastShort(error);
			}
		});
		MobclickAgent.onEvent(this, EVENT.ADD_FAV, event.id);
	}

	/** 评论列表 **/
	private void requestRecommendList() {
		EventUtil.getEventComments(event.id, pageIndex_recommend, new EntityListCallback<CommentEntity>() {
			@Override
			public void succeed(List<CommentEntity> list) {
				if (pageIndex_recommend == 1)
					adapter_comment.addAllList(list);
				else
					adapter_comment.appendList(list);
				pageIndex_recommend++;
				setComment(!Validator.isEmptyList(list) && list.size() == 10);
			}

			@Override
			public void fail(String error) {
				toastShort(error);
			}
		});
	}

	/** 报名列表 **/
	private void requestSigner() {
		EventUtil.getEventSigners(event.id, new EntityListCallback<SignerEntity>() {
			
			@Override
			public void succeed(List<SignerEntity> list) {
				adapter_signer.addAllList(list);
				no_sign_txt.setVisibility(Validator.isEmptyList(list) ? View.VISIBLE : View.GONE);
			}
			
			@Override
			public void fail(String error) {
				Log.e("requestSigner", error);
			}
		});
	}
	
	private void setComment(Boolean has_more) {
		if (adapter_comment.isEmpty()) {
			no_comment_txt.setVisibility(View.VISIBLE);
			load_comment_txt.setVisibility(View.GONE);
		} else {
			no_comment_txt.setVisibility(View.GONE);
			if (has_more != null)
				load_comment_txt.setVisibility(has_more ? View.VISIBLE : View.GONE);
		}
	}
	
	/**
	 * 报名
	 */
	private void signup() {
		//已报名
		if (session.getSign_list().contains(event.id)) {
			if (signedMenuDialog == null) {
				signedMenuDialog = new SignedMenuDialog(this, screen_width, screen_height);
				signedMenuDialog.getWindow().setGravity(Gravity.BOTTOM);
			}
			signedMenuDialog.show();
		//未报名
		} else {
			showProgressDialog(false);
			EventUtil.signEvent(event.id, new DefaultCallback() {
				@Override
				public void succeed() {
					setSigned(true);
					session.getSign_list().add(event.id);
					requestSigner();
					dismissProgressDialog();
					if (signedDialog == null)
						signedDialog = new SignedDialog(OverlayBigActivity.this, screen_width, screen_height);
					signedDialog.show();
				}
				
				@Override
				public void fail(String error) {
					toastShort("报名失败");
					dismissProgressDialog();
				}
			});
		}
	}
	
	public void showSignCert() {
		if (signCertDialog == null)
			signCertDialog = new SignCertDialog(this, screen_width, screen_height, event);
		if (signedDialog != null && signedDialog.isShowing())
			signedDialog.dismiss();
		if (signedMenuDialog != null && signedMenuDialog.isShowing())
			signedMenuDialog.dismiss();
		signCertDialog.show();
	}
	
	public void cancelSign() {
		if (signedMenuDialog != null && signedMenuDialog.isShowing())
			signedMenuDialog.dismiss();
		showProgressDialog(false);
		EventUtil.cancelEvent(event.id, new DefaultCallback() {
			@Override
			public void succeed() {
				setSigned(false);
				session.getSign_list().remove(event.id);
				requestSigner();
				toastShort("已取消报名");
				dismissProgressDialog();
			}
			
			@Override
			public void fail(String error) {
				toastShort("报名失败");
				dismissProgressDialog();
			}
		});
	}
	
	@Override
	protected void setPageCount() {
		setPageName(PAGE.EVENT_DETAIL);
	}
	
}

class BarcodeValidater implements BarcodeValidator, Serializable {
	
	private static final long serialVersionUID = 4881968100236933358L;

	@Override
	public boolean canProcess(Result result) {
		String str = result.getText();
		if (UserUtil.parseBarcode(str) != null || str.startsWith("http://"))
			return true;
		return false;
	}
	
}
