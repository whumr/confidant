package com.fingertip.tuding.base;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;

public class BaseEntity {

	public static <E extends BaseEntity> E parseJson(JSONObject json) throws Exception {
		return null;
	}

	
	@SuppressWarnings("unchecked")
	public static <E extends BaseEntity> List<E> parseJsonArray(JSONObject json) throws Exception {
		List<E> list = new ArrayList<E>();
		try {
			JSONArray array = json.getJSONArray(PARAM_KEYS.LIST);
			for (int i = 0; i < array.length(); i++) {
				try {
					list.add((E) parseJson(array.getJSONObject(i)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
}
