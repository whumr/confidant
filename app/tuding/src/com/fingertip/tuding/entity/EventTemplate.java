package com.fingertip.tuding.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.base.BaseEntity;
import com.fingertip.tuding.entity.EventEntity.EventType;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;

public class EventTemplate extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public String titleof, content, picof;
	public EventType kindof;
	
	@SuppressWarnings("unchecked")
	public static List<EventTemplate> parseJsonArray(JSONObject json) {
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
	
	@SuppressWarnings("unchecked")
	public static EventTemplate parseJson(JSONObject json) throws JSONException {
		EventTemplate template = new EventTemplate();
		template.titleof = Tools.decodeString(json.getString(PARAM_KEYS.TITLEOF));
		template.content = Tools.decodeString(json.getString(PARAM_KEYS.CONTENT));
		template.picof = json.getString(PARAM_KEYS.PICOF);
		template.kindof = EventType.getEventType(json.getString(PARAM_KEYS.KINDOF));
		return template;
	}
}