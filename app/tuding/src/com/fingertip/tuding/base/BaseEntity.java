package com.fingertip.tuding.base;

import java.util.List;

import org.json.JSONObject;

public class BaseEntity {

	public static <E extends BaseEntity> E parseJson(JSONObject json) throws Exception {
		return null;
	}

	public static <E extends BaseEntity> List<E> parseJsonArray(JSONObject json) throws Exception {
		return null;
	}
}
