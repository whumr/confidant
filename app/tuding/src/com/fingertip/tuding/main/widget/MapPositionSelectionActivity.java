package com.fingertip.tuding.main.widget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.db.SharedPreferenceUtil;

public class MapPositionSelectionActivity extends BaseActivity implements OnGetGeoCoderResultListener{
	
	public static final String KEY_ADDRESS = "address", KEY_LAT = "lat", KEY_LONG = "long";
	
	private MapView mMapView;
	private BaiduMap baiduMap;
	private TextView tv_marker;
	
	private LocationClient mLocationClient;
	private MyLocationListenner myLocationListenner = new MyLocationListenner();
	
	private Marker marker = null;
	private GeoCoder mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��

	private LatLng position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mappoistionselection);
		findViews();
		setupViews();
		initData();
	}

	private void findViews() {
		
	}

	private void setupViews() {
		findViewById(R.id.tv_more).setVisibility(View.INVISIBLE);
		TextView tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("���λ��");
		tv_marker = (TextView)findViewById(R.id.tv_marker);
		
		findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		findViewById(R.id.tv_config).setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				if(marker == null){
					Toast.makeText(MapPositionSelectionActivity.this, "λ�ô���", Toast.LENGTH_SHORT).show();
					return;
				}
				if (position == null) {
					toastShort("����ѡ��λ��");
				} else {
					Intent intent = new Intent();
					intent.putExtra(KEY_ADDRESS, tv_marker.getText().toString().trim());
					intent.putExtra(KEY_LAT, position.latitude);
					intent.putExtra(KEY_LONG, position.longitude);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
		
	}
	
	private void initData(){
		mMapView = (MapView)findViewById(R.id.bmapView);
		baiduMap = mMapView.getMap();
		
		// ��ʼ������ģ�飬ע���¼�����
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		
		baiduMap.setMyLocationEnabled(true);
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(myLocationListenner);
//		baiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(5000);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		
		mMapView.showZoomControls(false);
		
		
		baiduMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}
			
			@Override
			public void onMapClick(LatLng latLng) {
				if(latLng != null){
					// ��Geo����
					mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
					tv_marker.setText("��������λ��...");
					baiduMap.clear();
					setMarker(latLng);
				}
			}
		});
	}
	
	@Override
	protected void onResume(){
		mMapView.onResume();
		super.onResume();
	}
	
	@Override
	protected void onDestroy(){
		mLocationClient.stop();
		baiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
	
	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {
		private boolean isFirstLoc = true;
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null){
				return;
			}
			@SuppressWarnings("unused")
			float zoom = baiduMap.getMapStatus().zoom;
			
			if (isFirstLoc) {
				if(BDLocation.TypeCacheLocation == location.getLocType() || BDLocation.TypeGpsLocation == location.getLocType()|| BDLocation.TypeNetWorkLocation == location.getLocType() || BDLocation.TypeOffLineLocation == location.getLocType()){
					isFirstLoc = false;
					LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
					setMarker(ll);
				}else {
					if(getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT) > 0){
						isFirstLoc = false;
						LatLng ll = new LatLng(getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT), getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG));
						setMarker(ll);
					}else {
//						Toast.makeText(MapPositionSelectionActivity.this, "��λʧ��", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
		public void onReceivePoi(BDLocation poiLocation) { }
	}
	
	private void setMarker(LatLng point){
		OverlayOptions options = new MarkerOptions()
				.position(point)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding)).draggable(false);
		marker = (Marker)(baiduMap.addOverlay(options));
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(point ,16);
		baiduMap.animateMapStatus(u);
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			tv_marker.setText("δ��ȷ��λ��");
			return;
		}
		tv_marker.setText("" +  result.getAddress());
		position = result.getLocation();
	}
}
