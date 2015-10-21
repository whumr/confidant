package com.fingertip.tuding.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreference���湫����
 * @author Administrator
 *
 */
public class SharedPreferenceUtil {
	@SuppressWarnings("unused")
	private Context context;
	/**SharePreferences���� */
	private SharedPreferences sp ;
	/**�޸�Preferences����*/
	private SharedPreferences.Editor editor;
	
	/** �û�id **/
	public static final String LAST_UID = "last_uid";
	/** ��¼id(session) **/
	public static final String LAST_LOGIN_ID = "last_login_id";
	/** ����ҳ **/
	public static final String NEED_GUIDE = "need_guide";
	
	/** ��һʱ���γ�� **/
	public static final String LASTLOCATIONLAT = "lastlocationlat";
	/** ��һʱ��ξ��� **/
	public static final String LASTLOCATIONLONG = "lastlocationlong";
	
	public static final String HEADIMAGE = "headImage";
	public static final String HEADIMAGE_FULL = "headImage_full";

	public static final String HAS_NEW_MESSAGE = "has_new_message";
	public static final String HAS_NEW_WATCH = "has_new_watch";
	public static final String LASTREAD = "lastread";
	
	public SharedPreferenceUtil(Context context){
		this.context = context;
		sp = context.getSharedPreferences("tuding", 0);
		editor = sp.edit();
	}
	
	
	public void setLongValue(String key, long value){
		editor.putLong(key, value).commit();
	}
	
	public long getLongValue(String key){
		return sp.getLong(key, 0);
	}
	
	public void setStringValue(String key, String value){
		editor.putString(key, value).commit();
	}
	public String getStringValue(String key){
		return sp.getString(key, "");
	}
	
	public void setBooleanValue(String key, boolean value){
		editor.putBoolean(key, value).commit();
	}
	public boolean getBooleanValue(String key, boolean flag_default){
		return sp.getBoolean(key, flag_default);
	}

	public void setIntValue(String key, int value){
		editor.putInt(key, value).commit();
	}
	public int getIntValue(String key){
		return sp.getInt(key, -1);
	}
	
	public void setFloatValue(String key, float value){
		editor.putFloat(key, value).commit();
	}
	public float getFloatValue(String key){
		return sp.getFloat(key, -1);
	}
	
	public void setStringValue(String user_id, String key, String value){
		editor.putString(user_id + "." + key, value).commit();
	}
	
	public String getStringValue(String user_id, String key){
		return sp.getString(user_id + "." + key, "");
	}
	
	public void setBooleanValue(String user_id, String key, boolean value){
		editor.putBoolean(user_id + "." + key, value).commit();
	}
	
	public boolean getBooleanValue(String user_id, String key, boolean flag_default){
		return sp.getBoolean(user_id + "." + key, flag_default);
	}
	
	
}
