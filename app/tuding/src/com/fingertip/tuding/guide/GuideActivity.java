package com.fingertip.tuding.guide;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.main.SplashActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class GuideActivity extends BaseActivity implements OnPageChangeListener, OnTouchListener {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;

	// ����ͼƬ��Դ
	private static final int[] pics = { R.drawable.guide_1, R.drawable.guide_2,
			R.drawable.guide_3, R.drawable.guide_4 };

	// �ײ�С��ͼƬ
	private ImageView[] dots;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	// ��¼��ǰѡ��λ��
	private int currentIndex;
	private float last_x;
	private boolean jumping = false;
	
	private SharedPreferenceUtil sp;

	/** Called when the activity is first created. */
	@SuppressLint("InflateParams")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		MobclickAgent.openActivityDurationTrack(false);
		sp = new SharedPreferenceUtil(this);
		boolean guide = sp.getBooleanValue(SharedPreferenceUtil.NEED_GUIDE, true);
		if (!guide)
			gotoMain(false);
		else {
			this.options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_pic_empty)
				.cacheInMemory(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
			views = new ArrayList<View>();
			// ��ʼ������ͼƬ�б�
			for (int i = 0; i < pics.length; i++) {
				View view = LayoutInflater.from(this).inflate(R.layout.view_guide_page, null);
				ImageView img = (ImageView)view.findViewById(R.id.guide_img);
				img.setTag(pics[i]);
				ImageView btn = (ImageView)view.findViewById(R.id.guide_btn);
				if (i == pics.length -1) {
					btn.setVisibility(View.VISIBLE);
					btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							gotoMain(true);
						}
					});
				}
				views.add(view);
			}
			vp = (ViewPager) findViewById(R.id.viewpager);
			// ��ʼ��Adapter
			vpAdapter = new ViewPagerAdapter(views) {
				@Override
				public Object instantiateItem(ViewGroup container, int position) {
					View view = views.get(position);
					container.addView(view, 0);
					ImageView img = (ImageView)view.findViewById(R.id.guide_img);
					imageLoader.displayImage("drawable://" + Integer.parseInt(img.getTag().toString()), img, options);
					return view;
				}
			};
			vp.setAdapter(vpAdapter);
			// �󶨻ص�
			vp.setOnPageChangeListener(this);
			vp.setOnTouchListener(this);
			// ��ʼ���ײ�С��
			initDots();
		}
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.dot_line);
		dots = new ImageView[pics.length];
		// ѭ��ȡ��С��ͼƬ
		for (int i = 0; i < pics.length; i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setTag(i);// ����λ��tag������ȡ���뵱ǰλ�ö�Ӧ
		}
		currentIndex = 0;
		dots[currentIndex].setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));// ����ѡ��״̬
	}
	
	private void gotoMain(boolean set_sp) {
		if (!jumping) {
			jumping = true;
			sp.setBooleanValue(SharedPreferenceUtil.NEED_GUIDE, false);
			Intent intent = new Intent();
			intent.setClass(this, SplashActivity.class);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * ��ֻ��ǰ����С���ѡ��
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon >= pics.length || currentIndex == positon)
			return;
		dots[positon].setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
		dots[currentIndex].setImageDrawable(getResources().getDrawable(R.drawable.dot_white));
		currentIndex = positon;
	}
	
	// ������״̬�ı�ʱ����,arg0 ==1��ʾ���ڻ�����arg0==2��ʾ��������ˣ�arg0==0��ʾʲô��û������ҳ�濪ʼ������ʱ������״̬�ı仯˳��Ϊ��1��2��0����
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	//��ҳ���ڻ�����ʱ�����ô˷������ڻ�����ֹ֮ͣǰ���˷�����һֱ�õ����á��������������ĺ���ֱ�Ϊ��arg0 :��ǰҳ�棬������������ҳ�档arg1:��ǰҳ��ƫ�Ƶİٷֱȡ�arg2:��ǰҳ��ƫ�Ƶ�����λ�á�
	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {
	}

	//�˷�����ҳ����ת���õ����ã�arg0���㵱ǰѡ�е�ҳ���position
	@Override
	public void onPageSelected(int position) {
		// ���õײ�С��ѡ��״̬
		setCurDot(position);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			last_x = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if ((last_x - event.getX()) > 100 && (currentIndex == views.size() - 1))
				gotoMain(true);
			break;
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.clearMemoryCache();
	}
}
