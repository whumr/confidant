package com.fingertip.tuding.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;

/**
 * �û�ʵ��
 * @author Administrator
 *
 */
public class UserEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String id;
	public String nick_name;
	public String head_img_url;
	public String place;
	public String sex;
	public String mark;
	public String userkind;
	public UserType type;
	public int up_count;
	
//	{"userid": "1257053","nick": "Jim","sex": "m","head": "","address": "������������·1��","aboutme": "�Ҿ�����","headbig": "http://x/v922l-6313D63.jpg",
//    "fans": {"f": "3","h": "0","g": "3","x": "0"}}
	public static UserEntity parseJson(JSONObject json) throws JSONException {
		UserEntity user = new UserEntity();
		user.id = json.getString(PARAM_KEYS.USERID);
		user.nick_name = json.getString(PARAM_KEYS.USER_NICK_NAME);
//		user.head_img_url = new String(Base64.decode(json.getString(PARAM_KEYS.USER_HEAD), Base64.DEFAULT));
		user.head_img_url = json.getString(PARAM_KEYS.USER_HEAD);
		try {user.place = json.getString(PARAM_KEYS.USER_PLACE);} catch (Exception e) { }	
		user.sex = json.getString(PARAM_KEYS.USER_SEX);
		user.userkind = json.getString(PARAM_KEYS.USER_KIND);
		user.type = UserType.getUserType(user.userkind);
		try { user.mark = json.getString(PARAM_KEYS.USER_MARK);} catch (Exception e) { }	
		try { user.up_count = json.getJSONObject(PARAM_KEYS.USER_FANS).getInt(PARAM_KEYS.USER_FANS_F);} catch (Exception e) { }	
		return user;
	}
	
	public enum UserType {
		
		PERSON("p"),
		BUSINESS("b");
		
		private String type;

		private UserType(String type) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}

		public static UserType getUserType(String type) {
			for (UserType user_type : values()) {
				if (user_type.getType().equals(type))
					return user_type;
			}
			return null;
		}
	}
}
