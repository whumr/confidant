package com.fingertip.tuding.widget;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.common.DotPageDirector;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.widget.pic.PicPreviewAdapter;
import com.lidroid.xutils.BitmapUtils;

public class PicPreviewActivity extends BaseActivity {
	
	public static String KEY_PICS = "pics", KEY_INDEX = "index";
	
	private ViewPager pager;
	private DotPageDirector dot_page_director;
	private PicPreviewAdapter adapter;
	private TextView title_txt;

	private ArrayList<String> pic_list;
	private ArrayList<View> pic_views = new ArrayList<View>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageviewpager);
		
		findViews();
		setupViews();
		initData();
	}

	private void findViews() {
		pager = (ViewPager)findViewById(R.id.viewPager);
		dot_page_director = (DotPageDirector)findViewById(R.id.dot_page_director);
		dot_page_director.setDotRes(R.drawable.dot_white, R.drawable.dot_gray);
		dot_page_director.setDotPadding(2, 12);
	}

	private void setupViews() {
		adapter = new PicPreviewAdapter(pic_views);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(new OnPageChangeListener() {			
			@Override
			public void onPageSelected(int current) {
				dot_page_director.setCurrent(current);
				setPreviewTitle(current);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }
			@Override
			public void onPageScrollStateChanged(int arg0) { }
		});
		
		findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		title_txt = (TextView)findViewById(R.id.tv_title);
		title_txt.setText("ͼƬԤ��");
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent.hasExtra(KEY_PICS))
			pic_list = intent.getStringArrayListExtra(KEY_PICS);
		int index = intent.getIntExtra(KEY_INDEX, 0);
		if (Validator.isEmptyList(pic_list)) {
			toastShort("��ͼƬ");
			finish();
		} else {
			BitmapUtils bitmapUtils = new BitmapUtils(this);
			for (int i = 0; i < pic_list.size(); i++) {
				ImageView img_view = new ImageView(this);
				pic_views.add(img_view);
				img_view.setAdjustViewBounds(true);
				bitmapUtils.display(img_view, pic_list.get(i));
			}
			adapter.notifyDataSetChanged();
			dot_page_director.setCount(adapter.getCount(), 0);
			pager.setCurrentItem(index);
			dot_page_director.setCurrent(index);
			setPreviewTitle(index);
		}
	}
	
	public void setPreviewTitle(int index) {
		title_txt.setText("ͼƬԤ��(" + (index + 1) + "/" + pic_views.size() + ")");
	}
}
