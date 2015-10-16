package com.fingertip.tuding.util.http;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.entity.CommentEntity;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.entity.EventEntity.EventType;
import com.fingertip.tuding.entity.EventTemplate;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.http.callback.DefaultCallback;
import com.fingertip.tuding.util.http.callback.EntityCallback;
import com.fingertip.tuding.util.http.callback.EntityListCallback;
import com.fingertip.tuding.util.http.common.ServerConstants;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_VALUES;
import com.fingertip.tuding.util.http.common.ServerConstants.URL;
import com.fingertip.tuding.util.http.common.UploadImgEntity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class EventUtil extends BaseHttpUtil {

	public static String KINDOF = EventType.ALL.getType();
	
	public enum Type {
		nearest, newest, hotest
	}
	
	public static void searchEvents(Type type, String poslong, String poslat, int page, final EntityListCallback<EventEntity> callback) {
		searchEvents(type, KINDOF, poslong, poslat, page, callback);
	}
	
	/**
	 * 查询活动
	 * 
	 * @param type
	 * @param location
	 * @param page
	 * @param callback
	 */
	public static void searchEvents(Type type, String kind, String poslong, String poslat, int page, final EntityListCallback<EventEntity> callback) {
		UserSession session = UserSession.getInstance();
		String url = null, fc = null;
		switch (type) {
		case nearest :
			url = URL.GET_NEAREST_EVENT;
			fc = PARAM_VALUES.FC_GET_NEAREST_EVENT;
			break;
		case newest :
			url = URL.GET_NEWEST_EVENT;
			fc = PARAM_VALUES.FC_GET_NEWEST_EVENT;
			break;
		case hotest :
			url = URL.GET_HOTEST_EVENT;
			fc = PARAM_VALUES.FC_GET_HOTEST_EVENT;
			break;
		}
		JSONObject data = new JSONObject();
//		{"fc":"get_action_bypos", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", 
//		 "poslong":113.3124, "poslat":23.1428, "kindof":"优惠/特价"}
		try {
			data.put(PARAM_KEYS.FC, fc);
			data.put(PARAM_KEYS.USERID, session.getId() == null ? "" : session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id() == null ? "" : session.getLogin_id());
//			test "poslat":"22.553019","poslong":"113.952339"
//			data.put(PARAM_KEYS.POSLONG, "113.952339");
//			data.put(PARAM_KEYS.POSLAT, "22.553019");
//			data.put(PARAM_KEYS.KINDOF, "社交/聚会");
			data.put(PARAM_KEYS.POSLONG, poslong);
			data.put(PARAM_KEYS.POSLAT, poslat);
			data.put(PARAM_KEYS.KINDOF, kind);
			data.put(PARAM_KEYS.PAGENO, page);
			data.put(PARAM_KEYS.DECODE_CONTENT, PARAM_VALUES.N);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				List<EventEntity> list = null;
				try {
					json = new JSONObject(result);
//					Log.e("searchEvents", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						list = EventEntity.parseList(json);
				} catch (JSONException e) {
					error = "查询失败:" + e.getMessage();
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
	
	public static void searchEventsByKeywords(String keyword, String poslong, String poslat, int page, final EntityListCallback<EventEntity> callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"get_action_bysearch", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", 
//			 "poslong":113.3124, "poslat":23.1428, "keyword":"深圳", "pageno":1 , "decode_content":"y"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_SEARCH_EVENT);
			data.put(PARAM_KEYS.USERID, session.getId() == null ? "" : session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id() == null ? "" : session.getLogin_id());
			data.put(PARAM_KEYS.POSLONG, poslong);
			data.put(PARAM_KEYS.POSLAT, poslat);
			data.put(PARAM_KEYS.KEYWORD, keyword);
			data.put(PARAM_KEYS.PAGENO, page);
			data.put(PARAM_KEYS.DECODE_CONTENT, PARAM_VALUES.N);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.SEARCH_EVENT, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				List<EventEntity> list = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						list = EventEntity.parseList(json);
				} catch (JSONException e) {
					error = "查询失败:" + e.getMessage();
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
	
	/**
	 * 获取活动详情
	 * @param id
	 * @param callback
	 */
	public static void getEventInfo(final String id, final EntityCallback<EventEntity> callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"get_action_byactionid", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs",  "actionid":"571f34g-AD1820C-4g"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_EVENT_INFO);
			data.put(PARAM_KEYS.USERID, session.getId() == null ? "" : session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id() == null ? "" : session.getLogin_id());
			data.put(PARAM_KEYS.ACTIONID, id);
			data.put(PARAM_KEYS.DECODE_CONTENT, PARAM_VALUES.N);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_EVENT_INFO, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				EventEntity event = null;
				try {
					json = new JSONObject(result);
//					Log.e("getEventInfo", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						event = EventEntity.parseJson(json.getJSONObject(PARAM_KEYS.ACTIONINFOR));
				} catch (Exception e) {
					error = "查询失败:" + e.getMessage();
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(event);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}

	public static void getEventComments(final String id, int page, final EntityListCallback<CommentEntity> callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"get_action_reply_list", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "actionid":"56gi31s-36BC016-4g", "pageno":1}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_EVENT_COMMENTS);
			data.put(PARAM_KEYS.USERID, session.getId() == null ? "" : session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id() == null ? "" : session.getLogin_id());
			data.put(PARAM_KEYS.ACTIONID, id);
			data.put(PARAM_KEYS.PAGENO, page);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_EVENT_COMMENTS, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				List<CommentEntity> list = null;
				try {
					json = new JSONObject(result);
//					Log.e("getEventComments", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						list = CommentEntity.parseList(json);
				} catch (Exception e) {
					error = "查询失败:" + e.getMessage();
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
	
	public static void publishEvent(String title, String content, String type, String address, String start_time, String end_time, String latitude, String longitude, 
			String show_mode, List<UploadImgEntity> entitys, final EntityCallback<String> callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_PUBLISH_EVENT);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.TITLEOF, title);
			data.put(PARAM_KEYS.CONTENT, content);
			data.put(PARAM_KEYS.KINDOF, type);
			data.put(PARAM_KEYS.TIMEFROM, start_time);
			data.put(PARAM_KEYS.TIMETO, end_time);
			data.put(PARAM_KEYS.ADDRESS, address);
			data.put(PARAM_KEYS.POSLAT, latitude);
			data.put(PARAM_KEYS.POSLONG, longitude);
			data.put(PARAM_KEYS.SHOWMODE, show_mode);
			JSONArray pics = new JSONArray();
			for (UploadImgEntity entity : entitys) {
				JSONObject json = new JSONObject();
				json.put(PARAM_KEYS.S, entity.small_url);
				json.put(PARAM_KEYS.B, entity.big_url);
				pics.put(json);
			}
			data.put(PARAM_KEYS.PICOF, pics);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.PUBLISH_EVENT, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				String event_id = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						event_id = json.getString(PARAM_KEYS.ACTIONID);
				} catch (Exception e) {
					error = "发布失败:" + e.getMessage();
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(event_id);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}

	public static void publishEvent_v2(String title, String content, String type, String address, String start_time, String end_time, String latitude, String longitude, 
			String show_mode, List<UploadImgEntity> entitys, final EntityCallback<String> callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_PUBLISH_EVENT_V2);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.TITLEOF, title);
			data.put(PARAM_KEYS.CONTENT, content);
			data.put(PARAM_KEYS.KINDOF, type);
			data.put(PARAM_KEYS.TIMEFROM, start_time);
			data.put(PARAM_KEYS.TIMETO, end_time);
			data.put(PARAM_KEYS.ADDRESS, address);
			data.put(PARAM_KEYS.POSLAT, latitude);
			data.put(PARAM_KEYS.POSLONG, longitude);
			data.put(PARAM_KEYS.SHOWMODE, show_mode);
			JSONArray pics = new JSONArray();
			for (UploadImgEntity entity : entitys) {
				JSONObject json = new JSONObject();
				json.put(PARAM_KEYS.S, entity.small_url);
				json.put(PARAM_KEYS.B, entity.big_url);
				pics.put(json);
			}
			data.put(PARAM_KEYS.PICOF, pics);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.PUBLISH_EVENT_V2, params, new RequestCallBack<String>() {
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				String event_id = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						event_id = json.getString(PARAM_KEYS.ACTIONID);
				} catch (Exception e) {
					error = "发布失败:" + e.getMessage();
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(event_id);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}
	
	public static void favorEvent(String event_id, final DefaultCallback callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_FAV_EVENT);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.ACTIONID, event_id);
		} catch (JSONException e) {
		}
		postDefalt(URL.FAV_EVENT, data, "收藏失败:", callback);
	}
	
	public static void pubEventComment(String event_id, String content, final DefaultCallback callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_COMMENT_EVENT);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.ACTIONID, event_id);
			data.put(PARAM_KEYS.CONTENT, content);
		} catch (JSONException e) {
		}
		postDefalt(URL.COMMENT_EVENT, data, "评论失败:", callback);
	}
	
	public static void replyEventComment(String event_id, String replyid, String content, final DefaultCallback callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_COMMENT_REPLY_EVENT);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.ACTIONID, event_id);
			data.put(PARAM_KEYS.REPLYID, replyid);
			data.put(PARAM_KEYS.CONTENT, content);
		} catch (JSONException e) {
		}
		postDefalt(URL.COMMENT_REPLY_EVENT, data, "评论回复失败:", callback);
	}
	
	public static void loadEventTemplates(final EntityListCallback<EventTemplate> callback) {
//		{"fc":"get_action_template", "userid":"18979528420", "loginid":"t4etskerghskdryhgsdfklhs", "kindof":""}
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_EVENT_TEMPLATE);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.KINDOF, "");
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_EVENT_TEMPLATE, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				List<EventTemplate> list = null;
				try {
					json = new JSONObject(result);
//					Log.e("getEventComments", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						list = EventTemplate.parseList(json);
				} catch (Exception e) {
					error = "查询失败:" + e.getMessage();
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
}
