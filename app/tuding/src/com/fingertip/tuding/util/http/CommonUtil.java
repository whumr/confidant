package com.fingertip.tuding.util.http;

import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.util.http.callback.DefaultCallback;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_VALUES;
import com.fingertip.tuding.util.http.common.ServerConstants.URL;

public class CommonUtil extends BaseHttpUtil {

	public static void report(String content, String event_id, String type, final DefaultCallback callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_REPORT);
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id() == null ? "" : session.getLogin_id());
			data.put(PARAM_KEYS.USERID, session.getId() == null ? "" : session.getId());
			data.put(PARAM_KEYS.CONTENT, content);
			data.put(PARAM_KEYS.KINDOF, type);
			data.put(PARAM_KEYS.PARAMOF, event_id);
		} catch (JSONException e) {
		}
		postDefalt(URL.REPORT, data, "²Ù×÷Ê§°Ü:", callback);
	}
	
	public static void commitSuggestion(String phone, String content, final DefaultCallback callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_FEEDBACK);
			data.put(PARAM_KEYS.LOGINID, session.isLogin() ? session.getLogin_id() : "");
			data.put(PARAM_KEYS.USERID, phone);
			data.put(PARAM_KEYS.FEEDBACK, content);
		} catch (JSONException e) {
		}
		postDefalt(URL.FEEDBACK, data, "²Ù×÷Ê§°Ü:", callback);
	}
}
