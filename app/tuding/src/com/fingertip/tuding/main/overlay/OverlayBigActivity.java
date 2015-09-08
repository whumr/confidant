package com.fingertip.tuding.main.overlay;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.common.ShareDialog;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.entity.CommentEntity;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.entity.EventEntity.EventType;
import com.fingertip.tuding.entity.ShareEntity;
import com.fingertip.tuding.entity.UserEntity;
import com.fingertip.tuding.main.widget.PublicRecommendActivity;
import com.fingertip.tuding.my.UserInfoActivity;
import com.fingertip.tuding.setting.ReportActivity;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.UmengConfig.EVENT;
import com.fingertip.tuding.util.UmengConfig.PAGE;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.EventUtil;
import com.fingertip.tuding.util.http.callback.DefaultCallback;
import com.fingertip.tuding.util.http.callback.EntityListCallback;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 地图大气泡
 * 
 * @author Administrator
 *
 */
public class OverlayBigActivity extends BaseActivity implements View.OnClickListener {

	private LinearLayout layout_main;
	private ImageView iv_head;
	private TextView tv_title;
	private TextView tv_name;
	/* * */
	private TextView tv_time;
	private TextView tv_detail;
	private TextView tv_collection;
	/** 收藏按钮 **/
	private TextView tv_btnCollection;
	/* 评论标题 * */
	private TextView tv_recommendTopic;
	/* 评论 * */
	private TextView tv_recommend;
	private LinearLayout layout_img;
	/* * */
	private LinearLayout layout_commend;
	// 主题图片
	private ImageView iv_topic;

	private EventEntity event;

	private BitmapUtils bitmapUtils;

	// 屏幕左右间隔
	private int screenMargin = 80;

	/** 当前评论页数 **/
	private int pageIndex_recommend = 1;

	// 马上举报
	private TextView tv_accusation;

	// 收藏和邀请的布局
	private LinearLayout layout_collection, layout_share;

	private boolean isFirst = true;

	private UserSession session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overlaybig);

		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.width = getResources().getDisplayMetrics().widthPixels
				- screenMargin;

		findViews();
		setupViews();
		initEntity();

		initFavor();
	}

	private void findViews() {
		layout_main = (LinearLayout) findViewById(R.id.layout_main);
		layout_commend = (LinearLayout) findViewById(R.id.layout_commend);
		layout_img = (LinearLayout) findViewById(R.id.layout_img);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_detail = (TextView) findViewById(R.id.tv_detail);
		tv_collection = (TextView) findViewById(R.id.tv_collection);
		tv_btnCollection = (TextView) findViewById(R.id.btn_collection);
		tv_recommendTopic = (TextView) findViewById(R.id.tv_recommend_topic);
		tv_recommend = (TextView) findViewById(R.id.tv_recommend);
		tv_time = (TextView) findViewById(R.id.tv_time);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		iv_topic = (ImageView) findViewById(R.id.iv_topic);
		tv_accusation = (TextView) findViewById(R.id.tv_accusation);
		layout_collection = (LinearLayout) findViewById(R.id.layout_collection);
		layout_share = (LinearLayout) findViewById(R.id.layout_share);
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

		session = UserSession.getInstance();
	}

	private void initEntity() {
		event = (EventEntity) getIntent().getSerializableExtra(EXTRA_PARAM);
		if (event == null) {
			toastShort("数据错误");
			finish();
			return;
		}
		tv_title.setText(event.title);
		tv_name.setText(event.sender.nick_name);
		tv_detail.setText(event.content);
		tv_collection.setText("" + event.viewcount);// 调用浏览次数
		// tv_collection.setText("" + overlayEntity.appraiseCount);
		tv_recommendTopic.setText("评论（" + event.replycount + "）");
		tv_time.setText(event.send_time_str);

		bitmapUtils = new BitmapUtils(this);
		bitmapUtils.display(iv_head, event.sender.head_img_url,
			new BitmapLoadCallBack<ImageView>() {
				@Override
				public void onLoadCompleted(ImageView container,
						String uri, Bitmap bitmap,
						BitmapDisplayConfig config, BitmapLoadFrom from) {
					try {
						container.setImageBitmap(Tools.toRoundCorner(bitmap));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
					LogUtils.i("head img load fail");
				}
			});

		String topImag = Validator.isEmptyList(event.pics_big) ? null : event.pics_big.get(0);
		if (topImag != null) {
			bitmapUtils.display(iv_topic, topImag, new BitmapLoadCallBack<ImageView>() {
					@Override
					public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap,
							BitmapDisplayConfig config, BitmapLoadFrom from) {
						try {
							container.setImageBitmap(bitmap);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
						LogUtils.i("head img load fail");
					}
				});
		}
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
			bitmapUtils.display(imageView, event.pics_small.get(i));
			imageView.setTag(i);
			imageView.setOnClickListener(imgOnClickListener);
			layout_img_horizontal.addView(imageView, layoutParams_horizontal);
		}
		imageView = null;
		setOverlayType();
	}

	private void setCollected() {
		findViewById(R.id.iv_collection_starts).setBackgroundResource(R.drawable.collection_starts_p);
		tv_btnCollection.setOnClickListener(null);
		tv_btnCollection.setText("已收藏");
	}

	private void initFavor() {
		if (session.isLogin()) {
			if (session.isLoad_favor()) {
				if (session.getFavor_event_list().contains(event.id))
					setCollected();
			} else
				session.isLoad_favor();
		}
	}

	private View.OnClickListener imgOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Tools.previewPics(OverlayBigActivity.this, (ArrayList<String>)event.pics_big, (Integer) v.getTag());
//			Intent intent = new Intent();
//			intent.setClass(OverlayBigActivity.this, ImageViewPagerActivity.class);
//			intent.putExtra(EXTRA_PARAM, event.getImgList());
//			intent.putExtra(ImageViewPagerActivity.INDEX_SHOW, (Integer) v.getTag());
//			startActivity(intent);
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

	/** 设置评论 **/
	@SuppressLint("InflateParams")
	private void setComment() {
		Context context = OverlayBigActivity.this;
		// 评论
		List<CommentEntity> commentList = event.comments;
		tv_recommendTopic.setText("评论（" + event.replycount + "）");
		
		CommentEntity comment = null;
		View view_line = null;
		View view_commend = null;
		ImageView iv_commendHead;
		TextView tv_commendName;
		TextView tv_commendReply;
		TextView tv_commendContent;

		for (int i = 0; i < commentList.size(); i++) {
			LogUtils.i("i:" + i + ",i*2:" + (i * 2) + ",count:" + layout_commend.getChildCount());
			if ((i * 2) < layout_commend.getChildCount()) {
				view_commend = layout_commend.getChildAt(i * 2);
			} else {
				view_commend = LayoutInflater.from(context).inflate(R.layout.view_commend_listitem, null);
				layout_commend.addView(view_commend);

				view_line = new View(OverlayBigActivity.this);
				view_line.setBackgroundColor(getResources().getColor(R.color.gray_d7));
				LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
				layoutParams.topMargin = 8;
				layout_commend.addView(view_line, layoutParams);
			}

			comment = commentList.get(i);
			iv_commendHead = (ImageView) view_commend.findViewById(R.id.iv_head);
			tv_commendName = (TextView) view_commend.findViewById(R.id.tv_name);
			tv_commendReply = (TextView) view_commend.findViewById(R.id.tv_reply);
			tv_commendContent = (TextView) view_commend.findViewById(R.id.tv_content);

			try {
				iv_commendHead.setImageDrawable(getResources().getDrawable(R.drawable.bg_head_default_little));
				bitmapUtils.display(iv_commendHead, comment.userEntity.head_img_url,
						new BitmapLoadCallBack<ImageView>() {
							@Override
							public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap,
									BitmapDisplayConfig config,BitmapLoadFrom from) {
								try {
									container.setImageBitmap(Tools.toRoundCorner(bitmap));
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
								LogUtils.i("head img load fail");
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
			}
			tv_commendName.setText(comment.userEntity.nick_name);
			if (comment.reply != null) {
				tv_commendReply.setText(comment.reply);
				tv_commendReply.setVisibility(View.VISIBLE);
			}
			tv_commendContent.setText(comment.comment);

			iv_commendHead.setTag(comment.userEntity);
			iv_commendHead.setOnClickListener(onClickListener);

			view_commend.setTag(comment);
			view_commend.setBackgroundResource(R.drawable.selector_bg_gray);
			view_commend.setOnClickListener(onClickListener_reply);
		}

		commentList = null;
		comment = null;

		view_commend = null;
		iv_commendHead = null;
		tv_commendName = null;
		tv_commendContent = null;
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
			intent = new Intent();
			intent.setClass(this, ShareDialog.class);
			intent.putExtra(EXTRA_PARAM, shareEntity);
			startActivity(intent);
			break;
		}
	}

	/** 头像 **/
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Tools.openUser(OverlayBigActivity.this, ((UserEntity) view.getTag()).id);
		}
	};

	/** 评论回复 **/
	private View.OnClickListener onClickListener_reply = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (Tools.checkLogin(OverlayBigActivity.this)) {
				Intent intent = new Intent();
				intent.setClass(OverlayBigActivity.this,PublicRecommendActivity.class);
				intent.putExtra(BaseActivity.EXTRA_PARAM, event);
				intent.putExtra(PublicRecommendActivity.EXTRA_COMMENT, (CommentEntity) view.getTag());
				startActivity(intent);
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		pageIndex_recommend = 1;
		requestRecommendList();
	}

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
		EventUtil.getEventComments(event.id, pageIndex_recommend,
				new EntityListCallback<CommentEntity>() {
					@Override
					public void succeed(List<CommentEntity> list) {
						if (pageIndex_recommend == 1)
							event.comments.clear();
						event.comments.addAll(list);
						event.replycount = event.comments.size();
						setComment();
						pageIndex_recommend++;
					}

					@Override
					public void fail(String error) {
						toastShort(error);
					}
				});
	}

	@Override
	protected void setPageCount() {
		setPageName(PAGE.EVENT_DETAIL);
	}
}
