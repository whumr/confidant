package com.fingertip.tuding.util.http.callback;

import org.json.JSONObject;

public interface JsonCallback {
	public void succeed(JSONObject json);

	public void fail(String error);
}
