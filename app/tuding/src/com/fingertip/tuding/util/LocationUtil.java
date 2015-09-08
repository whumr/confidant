package com.fingertip.tuding.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationUtil {

	public static Location getLocation(Context context) {
		Location location;
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		else {
//			LocationListener locationListener = new LocationListener() {
//				// Provider��״̬�ڿ��á���ʱ�����ú��޷�������״ֱ̬���л�ʱ�����˺���
//				@Override
//				public void onStatusChanged(String provider, int status, Bundle extras) {
//				}
//
//				// Provider��enableʱ�����˺���������GPS����
//				@Override
//				public void onProviderEnabled(String provider) {
//				}
//
//				// Provider��disableʱ�����˺���������GPS���ر�
//				@Override
//				public void onProviderDisabled(String provider) {
//				}
//
//				// ������ı�ʱ�����˺��������Provider������ͬ�����꣬���Ͳ��ᱻ����
//				@Override
//				public void onLocationChanged(Location location) {
//					if (location != null) {
//						Log.e("Map", "Location changed : Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
//					}
//				}
//			};
//			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
			location = locationManager .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}
}
