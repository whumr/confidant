package com.fingertip.tuding.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.base.BaseEntity;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;

/**
 * ��Ϣ
 * @author Administrator
 *
 */
public class WatchEntity extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String id;
	public int up_count;
	
	public UserEntity user;
	
//	{"list":[{"aboutme":"�Ҿ�����","userid":"1644980","sex":"m","head":"","nick":"jim","address":"������������·1��"}],"ok":"y","fc":"get_friend_list"}
	@SuppressWarnings("unchecked")
	public static List<WatchEntity> parseJsonArray(JSONObject json) throws JSONException {
		List<WatchEntity> list = new ArrayList<WatchEntity>();
		try {
			JSONArray array = json.getJSONArray(PARAM_KEYS.LIST);
			for (int i = 0; i < array.length(); i++) {
				WatchEntity watch = new WatchEntity();
				try {
					watch.user = UserEntity.parseJson(array.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				list.add(watch);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
}
