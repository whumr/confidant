package com.fingertip.tuding.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.common.ScrollTouchView;
import com.fingertip.tuding.common.ScrollTouchView.UpdateNotify;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.entity.EventEntity.EventType;
import com.fingertip.tuding.entity.UserEntity.UserType;
import com.fingertip.tuding.main.overlay.OverlayBigActivity;
import com.fingertip.tuding.main.overlay.ViewMapOverlay;
import com.fingertip.tuding.my.MyIndexActivity;
import com.fingertip.tuding.search.KeyWordSearchActivity;
import com.fingertip.tuding.search.SearchMainActivity;
import com.fingertip.tuding.services.MessageService;
import com.fingertip.tuding.util.ImageCache;
import com.fingertip.tuding.util.ImageCache.UserHeadCallback;
import com.fingertip.tuding.util.UmengConfig.EVENT;
import com.fingertip.tuding.util.UmengConfig.PAGE;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.EventUtil;
import com.fingertip.tuding.util.http.callback.EntityCallback;
import com.fingertip.tuding.util.http.callback.EntityListCallback;
import com.fingertip.tuding.util.http.common.ServerConstants;
import com.lidroid.xutils.BitmapUtils;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseActivity implements UpdateNotify{
	private static final String TAG = "MainActivity";
	
	private MapView mMapView;
	private BaiduMap baiduMap;
	private LocationClient mLocationClient;
	private MyLocationListenner myLocationListenner;
	private ScrollTouchView  view_tab;
	
	/** 地图数据 **/
	private HashSet<String> event_id_set = new HashSet<String>();
	private HashSet<String> multi_event_id_set = new HashSet<String>();
	private HashMap<Marker, EventEntity> marker_map = new HashMap<Marker, EventEntity>();
	
	
	private ImageView user_info_img;
	private Intent service;
	private Timer timer;
	
	private long exitTime;
	private int quit_idle = 2000;
	
	private SDKReceiver mSDKReceiver;
	private Marker last_click_marker = null;
	
	private BitmapUtils bitmapUtils;
	private SharedPreferenceUtil sp;
	
	private float lastZoom = 16;
	private String current_type = EventType.ALL.getType();
	
	private boolean load_data = false;
	private LatLng last_lat = null;
	
	private static int MULTI_COUNT = 3;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			user_info_img.setImageDrawable(getResources().getDrawable(R.drawable.icon_main_user_red));
		};
	};
	
	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.d(TAG, "action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
//				text.setText("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(context, "网络出错", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.activity_main);
        
        setupViews();
        
        initSDKCheck();
        
        initMyData();
        if (UserSession.getInstance().isLogin())
        	startMsgService();
	}
	
	private void startMsgService() {
		service = new Intent();
		service.setClass(this, MessageService.class);
		startService(service);
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				//有新消息
				if (sp.getBooleanValue(SharedPreferenceUtil.HAS_NEW_MESSAGE, false)) {
					Message msg = Message.obtain(handler, 0);
					msg.sendToTarget();
				}
			}
		}, 5000, ServerConstants.GET_MESSAGE_GAP * 1000);
	}

	private void setupViews() {
		bitmapUtils = new BitmapUtils(this);
		sp = new SharedPreferenceUtil(this);
		
		user_info_img = (ImageView)findViewById(R.id.user_info_img);
		
		mMapView = (MapView)findViewById(R.id.bmapView);
		baiduMap = mMapView.getMap();
		
		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		
		baiduMap.setMyLocationEnabled(true);
		mLocationClient = new LocationClient(this);
		myLocationListenner = new MyLocationListenner();
		mLocationClient.registerLocationListener(myLocationListenner);
		baiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setTimeOut(10000);
		option.setScanSpan(30000);
		mLocationClient.setLocOption(option);
		
		mMapView.showZoomControls(false);
		
		view_tab = (ScrollTouchView)findViewById(R.id.view_tab);
		initTabeData();
		
		findViewById(R.id.user_info_img).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MyIndexActivity.class);
				startActivity(intent);
			}
		});
		
		//关键词搜索
		findViewById(R.id.key_search_img).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, KeyWordSearchActivity.class);
				startActivity(intent);
			}
		});
		
		//一般搜索
		findViewById(R.id.common_search_img).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SearchMainActivity.class);
				startActivity(intent);
			}
		});
		
		findViewById(R.id.iv_position).setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT) > 0){
					isFirstLoc = false;
					LatLng ll = new LatLng(sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT), sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG));
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll ,16);
					baiduMap.animateMapStatus(u);
				}
				isFirstLoc = true;
				mLocationClient.start();
			}
		});
		
		findViewById(R.id.map_plus_img).setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				MapStatus mapStatus = baiduMap.getMapStatus();
				baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(mapStatus.zoom + 1));
			}
		});
		findViewById(R.id.map_minus_img).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				MapStatus mapStatus = baiduMap.getMapStatus();
				baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(mapStatus.zoom - 1));
			}
		});
	}
	
	private void initSDKCheck(){
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mSDKReceiver = new SDKReceiver();
		registerReceiver(mSDKReceiver, iFilter);
		
		if(sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT) >= 0){
			LatLng ll = new LatLng(sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT), sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG));
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll ,16);
			baiduMap.animateMapStatus(u);
		}
	}
	
	/** 初始化底部数据 **/
	private void initTabeData(){
		view_tab.setUpdateNotify(MainActivity.this);
		
		Resources resources = getResources();
		Drawable drawableUnSelection = resources.getDrawable(R.drawable.icon_tab_activity_dot);
		
		view_tab.setTextColor(resources.getColor(R.color.maintab_selection), resources.getColor(R.color.gray_58));
		
		view_tab.addItem(EventType.ALL.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_event_type_all));
		view_tab.addItem(EventType.SPORTS.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_event_type_sports));
		view_tab.addItem(EventType.SOCIALITY.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_event_type_party));
		view_tab.addItem(EventType.PERFORM.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_event_type_show));
		view_tab.addItem(EventType.STUDY.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_event_type_study));
		view_tab.addItem(EventType.SPECIAL.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_event_type_special_selling));
		view_tab.addItem(EventType.OTHER.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_event_type_others));
		
		view_tab.setSelectionPosition(0);
	}
	
	private void initMyData(){
		MapStatus mapStatus = baiduMap.getMapStatus();
		lastZoom = mapStatus.zoom;
//		clearMarkerList();
		baiduMap.setOnMapClickListener(new OnMapClickListener() {			
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}
			
			@Override
			public void onMapClick(LatLng arg0) {
				baiduMap.hideInfoWindow();
			}
		});
		
		baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {			
			@Override
			public boolean onMarkerClick(Marker marker) {
				return clickMarker(marker);
			}
		});
		
		getPosData();
	}
	
//	private void clearMarkerList(){
//		Marker marker = null;
//		for (int i = 0; i < marker_list.size(); i++) {
//			marker = marker_list.get(i);
//			marker.remove();
//		}
//		marker_list.clear();
//		marker_map.clear();
//	}
	
	@SuppressLint("InflateParams")
	private Marker setOverlayData(final EventEntity event){
		Context context = MainActivity.this;
		final View view_markerImage = LayoutInflater.from(context).inflate(R.layout.view_marker_img, null);
		ImageView iv_markerImg = (ImageView)view_markerImage.findViewById(R.id.image);
		
		if (event.event_type == EventType.SPORTS) {
			iv_markerImg.setBackgroundResource(R.drawable.bg_icon_5);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_event_type_sports));
		} else if (event.event_type == EventType.SOCIALITY) {
			iv_markerImg.setBackgroundResource(R.drawable.bg_icon_3);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_event_type_party));
		} else if (event.event_type == EventType.PERFORM) {
			iv_markerImg.setBackgroundResource(R.drawable.bg_icon_4);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_event_type_show));
		} else if (event.event_type == EventType.STUDY) {
			iv_markerImg.setBackgroundResource(R.drawable.bg_icon_1);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_event_type_study));
		} else if (event.event_type == EventType.SPECIAL) {
			iv_markerImg.setBackgroundResource(R.drawable.bg_icon_2);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_event_type_special_selling));
		} else if (event.event_type == EventType.OTHER) {
			iv_markerImg.setBackgroundResource(R.drawable.bg_icon_4);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_event_type_others));
		} else {
			iv_markerImg.setBackgroundResource(R.drawable.bg_icon_6);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_event_type_all));
		}
		if (event.sender.type == UserType.BUSINESS && !Validator.isEmptyString(event.sender.head_img_url)) {
			ImageCache.loadUserHeadImg(event.sender.head_img_url, event.sender.id, sp, bitmapUtils, iv_markerImg, new UserHeadCallback() {
				
				@Override
				public void loadSucceed() {
					addOverlay(event, view_markerImage);				
				}
				
				@Override
				public void loadFail() {
					addOverlay(event, view_markerImage);
				}
			});
		} else
			return addOverlay(event, view_markerImage);
		return null;
	}
	
	private Marker addOverlay(EventEntity event, View view_markerImage) {
		if (event.poslist.size() > 1 && last_lat != null) {
			view_markerImage = null;
			List<EventEntity> events = EventEntity.getNearestEvents(event, last_lat.longitude, last_lat.latitude, MULTI_COUNT);
			if (event.poslist.size() > MULTI_COUNT)
				multi_event_id_set.add(event.id);
			return addMultiOverlay(events);
		} else {
			if (view_markerImage != null) {
				LatLng point = new LatLng(event.poslat, event.poslong);
				BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view_markerImage);
				OverlayOptions options = new MarkerOptions().position(point).icon(bitmapDescriptor).draggable(false);
				Marker marker = (Marker)(baiduMap.addOverlay(options));
				marker_map.put(marker, event);
				return marker;
			}
			return null;
		}
	}
	
	//返回最近的一个
	@SuppressLint("InflateParams")
	private Marker addMultiOverlay(List<EventEntity> events) {
		Marker result = null;
		for (int i = 0; i < events.size(); i++) {
			View view_markerImage = LayoutInflater.from(this).inflate(R.layout.view_marker_img, null);
			ImageView iv_markerImg = (ImageView)view_markerImage.findViewById(R.id.image);
			EventEntity event = events.get(i);
			EventType event_type = event.event_type;
			if (event_type == EventType.SPORTS) {
				iv_markerImg.setBackgroundResource(R.drawable.bg_icon_5);
				iv_markerImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_event_type_sports));
			} else if (event_type == EventType.SOCIALITY) {
				iv_markerImg.setBackgroundResource(R.drawable.bg_icon_3);
				iv_markerImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_event_type_party));
			} else if (event_type == EventType.PERFORM) {
				iv_markerImg.setBackgroundResource(R.drawable.bg_icon_4);
				iv_markerImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_event_type_show));
			} else if (event_type == EventType.STUDY) {
				iv_markerImg.setBackgroundResource(R.drawable.bg_icon_1);
				iv_markerImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_event_type_study));
			} else if (event_type == EventType.SPECIAL) {
				iv_markerImg.setBackgroundResource(R.drawable.bg_icon_2);
				iv_markerImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_event_type_special_selling));
			} else if (event_type == EventType.OTHER) {
				iv_markerImg.setBackgroundResource(R.drawable.bg_icon_4);
				iv_markerImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_event_type_others));
			} else {
				iv_markerImg.setBackgroundResource(R.drawable.bg_icon_6);
				iv_markerImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_event_type_all));
			}
			if (event.sender.type == UserType.BUSINESS && !Validator.isEmptyString(event.sender.head_img_url))
				ImageCache.setUserHeadImg(event.sender.id, iv_markerImg);
			LatLng point = new LatLng(event.poslat, event.poslong);
			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view_markerImage);
			OverlayOptions options = new MarkerOptions().position(point).icon(bitmapDescriptor).draggable(false);
			Marker marker = (Marker)(baiduMap.addOverlay(options));
//			if(!marker_list.contains(marker))
//				marker_list.add(marker);
			marker_map.put(marker, event);
			if (i == events.size() - 1)
				result = marker;
		}
		return result;
	}
	
	private void refreshMultiEvents() {
		if (!multi_event_id_set.isEmpty()) {
			HashSet<String> id_set = new HashSet<String>();
			List<EventEntity> multi_events = new ArrayList<EventEntity>();
			for (Iterator<Marker> it = marker_map.keySet().iterator(); it.hasNext();) {
				Marker marker = it.next();
				EventEntity event = marker_map.get(marker);
				if (multi_event_id_set.contains(event.id)) {
					marker.remove();
					it.remove();
					marker = null;
					if (id_set.add(event.id))
						multi_events.add(event);
					else
						event = null;
				}
			}
			if (!multi_events.isEmpty()) {
				for (EventEntity event : multi_events) {
					addOverlay(event, null);
				}
			}
		}
		
	}
	
//	private void resetOverlay() {
//		clearMarkerList();
//		for (int i = 0; i < event_list.size(); i++)
//			setOverlayData(event_list.get(i));
//	}

	private void appendOverlay(List<EventEntity> append_list) {
//		if (clear_type)
//			clearUnmatchType();
		for (int i = 0; i < append_list.size(); i++)
			setOverlayData(append_list.get(i));
		append_list.clear();
	}
	
	private void clearUnmatchType() {
		if (!EventType.ALL.getType().equals(current_type)) {
			for (Iterator<Marker> it = marker_map.keySet().iterator(); it.hasNext();) {
				Marker marker = it.next();
				EventEntity event = marker_map.get(marker);
				if (!current_type.equals(event.kindof)) {
					marker.remove();
//					marker_list.remove(marker);
					it.remove();
					marker = null;
					event = null;
				}
			}
			event_id_set.clear();
			for (Marker marker : marker_map.keySet()) {
				EventEntity event = marker_map.get(marker);
				event_id_set.add(event.id);
			}
		}
	}
	
	private OnMapStatusChangeListener onMapStatusChangeListener = new OnMapStatusChangeListener() {		
		@Override
		public void onMapStatusChangeStart(MapStatus mapStatus) {
		}
		
		@Override
		public void onMapStatusChangeFinish(MapStatus mapStatus) {
			getPosData();
		}
		
		@Override
		public void onMapStatusChange(MapStatus mapStatus) {
			if(lastZoom != mapStatus.zoom){
				lastZoom = mapStatus.zoom;
//				resetOverlay();
			}
		}
	};
	
	private boolean isFirstLoc = true;	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null){
				return;
			}
			
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				if(BDLocation.TypeCacheLocation == location.getLocType() || BDLocation.TypeGpsLocation == location.getLocType() 
						|| BDLocation.TypeNetWorkLocation == location.getLocType() || BDLocation.TypeOffLineLocation == location.getLocType()){
					isFirstLoc = false;
					LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll ,16);
					baiduMap.animateMapStatus(u);
					
					sp.setFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT, (float)location.getLatitude());
					sp.setFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG, (float)location.getLongitude());
				} else {
					if(sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT) > 0){
						isFirstLoc = false;
						LatLng ll = new LatLng(sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT), sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG));
						MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll ,16);
						baiduMap.animateMapStatus(u);
					}
				}
			}
			
			if (!load_data)
				getPosData();
		}
		public void onReceivePoi(BDLocation poiLocation) { }
	}
	
	@Override
	protected void onDestroy(){
		// 取消监听 SDK 广播
		unregisterReceiver(mSDKReceiver);
		
		mLocationClient.stop();
		baiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		
		view_tab.clearItem();
		
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (service != null)
			stopService(service);
		super.onDestroy();
	}
	
	@Override
	protected void onResume(){
		mMapView.onResume();
		mLocationClient.start();
		super.onResume();
		if (sp.getBooleanValue(SharedPreferenceUtil.HAS_NEW_MESSAGE, false))
			user_info_img.setImageDrawable(getResources().getDrawable(R.drawable.icon_main_user_red));
		else
			user_info_img.setImageDrawable(getResources().getDrawable(R.drawable.icon_main_user));
	}
	
	@Override
	protected void onPause(){
		mMapView.onPause();
		mLocationClient.stop();
		super.onPause();
	}

	@Override
	public void notifyUpdata(int index) {
		current_type = view_tab.getSelectedText();
		EventUtil.KINDOF = current_type;
		showProgressDialog(false);
		clearUnmatchType();
		getPosData();
	}
	
	/** 获取地图数据 **/
	private void getPosData(){
		double latitude = sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT);
		double longitude = sp.getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG);
		LatLng ll = mMapView.getMap().getMapStatus().bound.getCenter();
		if (ll != null && ll.latitude != 0 && ll.longitude != 0) {
			latitude = ll.latitude;
			longitude = ll.longitude;
			refreshMultiEvents();
		}
		if (latitude > 0 && longitude > 0) {
			last_lat = new LatLng(latitude, longitude);
			EventUtil.searchEvents(EventUtil.Type.nearest, longitude + "", latitude + "", 1, new EntityListCallback<EventEntity>(){
				@Override
				public void succeed(List<EventEntity> list) {
					dismissProgressDialog();
					if (!Validator.isEmptyList(list)) {
						List<EventEntity> append_list = new ArrayList<EventEntity>();
						for (EventEntity event :list) {
							if (event_id_set.add(event.id)) {
								append_list.add(event);
//								event_list.add(event);
							}
						}
						if (!append_list.isEmpty())
							appendOverlay(append_list);
//						event_list.clear();
//						event_list.addAll(list);
//		            	resetOverlay();
					}
				}
				
				@Override
				public void fail(String error) {
					dismissProgressDialog();
					toastShort(error);
				}
			});
			load_data = true;
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.hasExtra(EXTRA_PARAM)) {
			String event_id = intent.getStringExtra(EXTRA_PARAM);
			locateEvent(event_id);
		}
	}
	
	private void locateEvent(String event_id) {
		showProgressDialog(false);
		EventUtil.getEventInfo(event_id, new EntityCallback<EventEntity>() {
			
			@Override
			public void succeed(EventEntity entity) {
				dismissProgressDialog();
				Marker marker = getMarker(entity.id);
				if (marker == null)
					marker = setOverlayData(entity);
				clickMarker(marker);
			}
			
			@Override
			public void fail(String error) {
				toastShort(error);
				dismissProgressDialog();
			}
		});
	}
	
	private Marker getMarker(String event_id) {
		for (Marker marker : marker_map.keySet()) {
			EventEntity event = marker_map.get(marker);
			if (event.id.equals(event_id))
				return marker;
		}
		return null;
	}
	
	private boolean clickMarker(Marker marker) {
		EventEntity event = marker_map.get(marker);
		if (event == null){
			Log.e(TAG, "overlayt is null");
			return false;
		}
		if (last_click_marker != null && last_click_marker == marker) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, OverlayBigActivity.class);
			intent.putExtra(BaseActivity.EXTRA_PARAM, event);
			startActivity(intent);
			return true;
		}
		last_click_marker = marker;
		ViewMapOverlay viewMapOverlay = new ViewMapOverlay(getApplicationContext());
		viewMapOverlay.setEventEntity(event);
		final EventEntity final_event = event;
		viewMapOverlay.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View v) {
				baiduMap.hideInfoWindow();
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, OverlayBigActivity.class);
				intent.putExtra(BaseActivity.EXTRA_PARAM, final_event);
				startActivity(intent);
			}
		});
		LatLng ll = marker.getPosition();
		InfoWindow mInfoWindow = new InfoWindow(viewMapOverlay, ll, 0);
		baiduMap.showInfoWindow(mInfoWindow);
		MapStatus mMapStatus = new MapStatus.Builder().target(new LatLng(ll.latitude, ll.longitude))
				.zoom(baiduMap.getMapStatus().zoom).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);
        MobclickAgent.onEvent(this, EVENT.CLICK_EVENT, event.id);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		boolean quit = true;
		if (System.currentTimeMillis() - exitTime > quit_idle) {
			toastShort("再按一次退出图丁");
			exitTime = System.currentTimeMillis();
			quit = false;
		}
		if (quit)
			finish();
	}
	
	@Override
	protected void setPageCount() {
		setPageName(PAGE.MAIN);
	}
}
