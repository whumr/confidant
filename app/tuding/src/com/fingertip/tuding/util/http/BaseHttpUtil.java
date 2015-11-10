package com.fingertip.tuding.util.http;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.base.BaseEntity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.http.callback.DefaultCallback;
import com.fingertip.tuding.util.http.callback.EntityCallback;
import com.fingertip.tuding.util.http.callback.EntityListCallback;
import com.fingertip.tuding.util.http.callback.JsonCallback;
import com.fingertip.tuding.util.http.common.ServerConstants;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_VALUES;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class BaseHttpUtil {
	
	protected static void postDefalt(String url, JSONObject data, final String error_msg, final DefaultCallback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
	        @Override
	        public void onSuccess(ResponseInfo<String> responseInfo) {
	        	String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
				} catch (JSONException e) {
					e.printStackTrace();
					error = error_msg + ServerConstants.PARSE_ERROR_TIP;
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed();
	        }

	        @Override
	        public void onFailure(HttpException error, String msg) {
	        	callback.fail(ServerConstants.NET_ERROR_TIP);
	        }
		});
	}
	
	protected static void postForJson(String url, JSONObject data, final String error_msg, final JsonCallback callback) {
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
	        @Override
	        public void onSuccess(ResponseInfo<String> responseInfo) {
	        	String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
				} catch (Exception e) {
					e.printStackTrace();
					error = error_msg + ServerConstants.PARSE_ERROR_TIP;
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(json);
	        }

	        @Override
	        public void onFailure(HttpException error, String msg) {
	        	callback.fail(ServerConstants.NET_ERROR_TIP);
	        }
		});
	}

	protected static void postForString(String url, JSONObject data, final String error_msg, final EntityCallback<String> callback, final String key) {
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				String string = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						string = json.getString(key);
				} catch (Exception e) {
					e.printStackTrace();
					error = error_msg + ServerConstants.PARSE_ERROR_TIP;
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(string);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	protected static <E extends BaseEntity> void postForEntity(String url, JSONObject data, final String error_msg, 
			final EntityCallback<E> callback, final Class<? extends BaseEntity> clazz) {
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
	        @Override
	        public void onSuccess(ResponseInfo<String> responseInfo) {
	        	String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				E entity = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						entity = (E)clazz.getMethod("parseJson", JSONObject.class).invoke(null, json);
				} catch (Exception e) {
					e.printStackTrace();
					error = error_msg + ServerConstants.PARSE_ERROR_TIP;
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(entity);
	        }

	        @Override
	        public void onFailure(HttpException error, String msg) {
	        	callback.fail(ServerConstants.NET_ERROR_TIP);
	        }
		});
	}

	@SuppressWarnings("unchecked")
	protected static <E extends BaseEntity> void postForEntityList(String url, JSONObject data, final String error_msg, 
			final EntityListCallback<E> callback, final Class<? extends BaseEntity> clazz) {
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				List<E> list = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						list = (List<E>)clazz.getMethod("parseJsonArray", JSONObject.class).invoke(null, json);
				} catch (Exception e) {
					e.printStackTrace();
					error = error_msg + ServerConstants.PARSE_ERROR_TIP;
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(list);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}
	
	protected static JSONObject getSessionJson() {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
		try {
			data.put(PARAM_KEYS.USERID, session.isLogin() ? session.getId() : "");
			data.put(PARAM_KEYS.LOGINID, session.isLogin() ? session.getLogin_id() : "");
		} catch (JSONException e) {
		}
		return data;
	}
}
