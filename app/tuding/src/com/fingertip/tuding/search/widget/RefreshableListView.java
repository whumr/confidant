package com.fingertip.tuding.search.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.widget.view.ListViewForScroll;

/**
 * @author SunnyCoffee
 * @create 2013-10-24
 * @version 1.0
 * @desc �Զ���Listview������ˢ��,�������ظ���
 */

public class RefreshableListView extends ListViewForScroll implements OnScrollListener {

	// ���ֵ�ǰ������ˢ�»��Ǽ���
	public static final int REFRESH = 0;
	public static final int LOAD = 1;

	// ����PULL��RELEASE�ľ���Ĵ�С
	private static final int SPACE = 20;

	// ����header������״̬�͵�ǰ״̬
	private static final int NONE = 0;
	private static final int PULL = 1;
	private static final int RELEASE = 2;
	private static final int REFRESHING = 3;
	private int state;

	private LayoutInflater inflater;
	private View header;
	private View footer;
	private TextView tip;
	private TextView lastUpdate;
	private ImageView arrow;
	private ProgressBar refreshing;

	private TextView noData;
	private TextView loadFull;
	private TextView more;
	private ProgressBar loading;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;

	private int firstVisibleItem;
	private int scrollState;
	private int headerContentInitialHeight;
	private int headerContentHeight;

	// ֻ����listview��һ��item��ʾ��ʱ��listview�����˶������Ž�������ˢ�£� �����ʱ������ֻ�ǻ���listview
	private boolean isRecorded;
	private boolean isLoading;// �ж��Ƿ����ڼ���
	private boolean loadEnable = true;// �������߹رռ��ظ��๦��
	private boolean isLoadFull;
	private boolean init;
	private int pageSize = 10;

	private RefreshListener refreshListener;

	public RefreshableListView(Context context) {
		super(context);
		initView(context);
	}

	public RefreshableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public RefreshableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public void setRefreshListener(RefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	public boolean isLoadEnable() {
		return loadEnable;
	}

	// ����Ŀ������߹رռ��ظ��࣬����֧�ֶ�̬����
	public void setLoadEnable(boolean loadEnable) {
		this.loadEnable = loadEnable;
//		this.removeFooterView(footer);
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	// ��ʼ�����
	@SuppressLint("InflateParams")
	private void initView(Context context) {
		// ���ü�ͷ��Ч
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(100);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(100);
		reverseAnimation.setFillAfter(true);

		inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.view_listview_footer, null);
		noData = (TextView) footer.findViewById(R.id.noData);
		loadFull = (TextView) footer.findViewById(R.id.loadFull);
		more = (TextView) footer.findViewById(R.id.more);
		loading = (ProgressBar) footer.findViewById(R.id.loading);

		header = inflater.inflate(R.layout.view_listview_header, null);
		arrow = (ImageView) header.findViewById(R.id.arrow);
		tip = (TextView) header.findViewById(R.id.tip);
		lastUpdate = (TextView) header.findViewById(R.id.lastUpdate);
		refreshing = (ProgressBar) header.findViewById(R.id.refreshing);

		// Ϊlistview���ͷ����β���������г�ʼ��
		headerContentInitialHeight = header.getPaddingTop();
		measureView(header);
		headerContentHeight = header.getMeasuredHeight();
		topPadding(-headerContentHeight);
		addHeaderView(header);
		setOnScrollListener(this);
		setBackgroundResource(R.color.white);
	}

	public void refreshComplete() {
		lastUpdate.setText(this.getContext().getString(R.string.lastUpdateTime, Tools.getTimeStr(System.currentTimeMillis())));
		state = NONE;
		if (!init) {
			init = true;
			addFooterView(footer);
		}
		refreshHeaderViewByState();
	}

	// ���ڼ��ظ��������Ļص�
	public void loadComplete() {
		isLoading = false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		ifNeedLoad(view, scrollState);
	}

	// ����listview������״̬�ж��Ƿ���Ҫ���ظ���
	private void ifNeedLoad(AbsListView view, int scrollState) {
		if (!loadEnable)
			return;
		try {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& view.getLastVisiblePosition() == view.getPositionForView(footer) 
					&& !isLoadFull && !isLoading) {
				refreshListener.onLoadMore();
				isLoading = true;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * ���������¼����������
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (firstVisibleItem == 0) {
				isRecorded = true;
				startY = (int) ev.getY();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (state == PULL) {
				state = NONE;
				refreshHeaderViewByState();
			} else if (state == RELEASE) {
				state = REFRESHING;
				refreshHeaderViewByState();
				refreshListener.onRefresh();
			}
			isRecorded = false;
			break;
		case MotionEvent.ACTION_MOVE:
			whenMove(ev);
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	public void initData() {
		init = false;
		state = REFRESHING;
		refreshHeaderViewByState();
		refreshListener.onRefresh();
	}

	// ������ƣ�ˢ��header״̬
	private void whenMove(MotionEvent ev) {
		if (!isRecorded) {
			return;
		}
		int tmpY = (int) ev.getY();
		int space = tmpY - startY;
		int topPadding = space - headerContentHeight;
		switch (state) {
		case NONE:
			if (space > 0) {
				state = PULL;
				refreshHeaderViewByState();
			}
			break;
		case PULL:
			topPadding(topPadding);
			if (scrollState == SCROLL_STATE_TOUCH_SCROLL
					&& space > headerContentHeight + SPACE) {
				state = RELEASE;
				refreshHeaderViewByState();
			}
			break;
		case RELEASE:
			topPadding(topPadding);
			if (space > 0 && space < headerContentHeight + SPACE) {
				state = PULL;
				refreshHeaderViewByState();
			} else if (space <= 0) {
				state = NONE;
				refreshHeaderViewByState();
			}
			break;
		}

	}

	// ����header�Ĵ�С����ʵ������ֻ�Ǿ��붥���ĸ߶ȡ�
	private void topPadding(int topPadding) {
		header.setPadding(header.getPaddingLeft(), topPadding,
				header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}

	/**
	 * ��������Ǹ��ݽ���Ĵ�С������footer��ʾ�ġ�
	 * <p>
	 * ����ٶ�ÿ�����������Ϊ10�����������10��������Ϊ�������ݡ�����������10��������Ϊ�����Ѿ�ȫ�����أ���ʱfooter��ʾ�Ѿ�ȫ������
	 * </p>
	 * 
	 * @param refresh
	 * @param succeed
	 * @param resultSize
	 */
	public void setResultSize(boolean refresh, boolean succeed, int resultSize) {
		if (!succeed) {
			isLoadFull = false;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			noData.setVisibility(View.GONE);
		} else {
			if (resultSize == 0) {
				isLoadFull = true;
				loading.setVisibility(View.GONE);
				more.setVisibility(View.GONE);
				loadFull.setVisibility(refresh ? View.GONE : View.VISIBLE);
				noData.setVisibility(refresh ? View.VISIBLE : View.GONE);
			} else if (resultSize < pageSize) {
				isLoadFull = true;
				loadFull.setVisibility(View.VISIBLE);
				loading.setVisibility(View.GONE);
				more.setVisibility(View.GONE);
				noData.setVisibility(View.GONE);
			} else if (resultSize == pageSize) {
				isLoadFull = false;
				loadFull.setVisibility(View.GONE);
				loading.setVisibility(View.VISIBLE);
				more.setVisibility(View.VISIBLE);
				noData.setVisibility(View.GONE);
			}
		}
		setLoadEnable(getAdapter().getCount() >= pageSize);
	}

	// ���ݵ�ǰ״̬������header
	private void refreshHeaderViewByState() {
		switch (state) {
		case NONE:
			topPadding(-headerContentHeight);
			tip.setText(R.string.pull_to_refresh);
			refreshing.setVisibility(View.GONE);
			arrow.clearAnimation();
			arrow.setImageResource(R.drawable.pull_to_refresh_arrow);
			break;
		case PULL:
			arrow.setVisibility(View.VISIBLE);
			tip.setVisibility(View.VISIBLE);
			lastUpdate.setVisibility(View.VISIBLE);
			refreshing.setVisibility(View.GONE);
			tip.setText(R.string.pull_to_refresh);
			arrow.clearAnimation();
			arrow.setAnimation(reverseAnimation);
			break;
		case RELEASE:
			arrow.setVisibility(View.VISIBLE);
			tip.setVisibility(View.VISIBLE);
			lastUpdate.setVisibility(View.VISIBLE);
			refreshing.setVisibility(View.GONE);
			tip.setText(R.string.pull_to_refresh);
			tip.setText(R.string.release_to_refresh);
			arrow.clearAnimation();
			arrow.setAnimation(animation);
			break;
		case REFRESHING:
			topPadding(headerContentInitialHeight);
			refreshing.setVisibility(View.VISIBLE);
			arrow.clearAnimation();
			arrow.setVisibility(View.GONE);
			tip.setVisibility(View.GONE);
			lastUpdate.setVisibility(View.GONE);
			break;
		}
	}

	// ��������header��С�ġ��Ƚ����ޡ���Ϊheader�ĳ�ʼ�߶Ⱦ���0,ò�ƿ��Բ��á�
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) 
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec = lpHeight > 0 ? MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY) :
			MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		child.measure(childWidthSpec, childHeightSpec);
	}

	public interface RefreshListener {
		public void onRefresh();
		public void onLoadMore();
	}
	
	
	public void setNoDataString(String no_data) {
		noData.setText(no_data);
	}
	
	public void setLoadFullString(String load_full) {
		loadFull.setText(load_full);
	}
	
	public void setMoreString(String more_string) {
		more.setText(more_string);
	}

}
