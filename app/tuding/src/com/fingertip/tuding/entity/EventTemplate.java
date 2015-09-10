package com.fingertip.tuding.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;

public class EventTemplate implements Serializable {

	private static final long serialVersionUID = 1L;

	public String titleof, content, picof;
	
	public static List<EventTemplate> parseList(JSONObject json) {
		List<EventTemplate> list = new ArrayList<EventTemplate>();
		try {
			JSONArray array = json.getJSONArray(PARAM_KEYS.LIST);
			for (int i = 0; i < array.length(); i++) {
				try {
					list.add(parseJson(array.getJSONObject(i)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static EventTemplate parseJson(JSONObject json) throws JSONException {
		EventTemplate template = new EventTemplate();
		template.titleof = new String(Base64.decode(json.getString(PARAM_KEYS.TITLEOF), Base64.DEFAULT));
		template.content = new String(Base64.decode(json.getString(PARAM_KEYS.CONTENT), Base64.DEFAULT));
		template.picof = json.getString(PARAM_KEYS.PICOF);
		return template;
	}
}