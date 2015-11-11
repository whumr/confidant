package com.fingertip.tuding.util.http;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.entity.CommentEntity;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.entity.EventEntity.EventType;
import com.fingertip.tuding.entity.EventTemplate;
import com.fingertip.tuding.entity.SignerEntity;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.callback.DefaultCallback;
import com.fingertip.tuding.util.http.callback.EntityCallback;
import com.fingertip.tuding.util.http.callback.EntityListCallback;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_VALUES;
import com.fingertip.tuding.util.http.common.ServerConstants.URL;
import com.fingertip.tuding.util.http.common.UploadImgEntity;

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
		JSONObject data = getSessionJson();
//		{"fc":"get_action_bypos", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", 
//		 "poslong":113.3124, "poslat":23.1428, "kindof":"优惠/特价"}
		try {
			data.put(PARAM_KEYS.FC, fc);
			data.put(PARAM_KEYS.POSLONG, poslong);
			data.put(PARAM_KEYS.POSLAT, poslat);
			data.put(PARAM_KEYS.KINDOF, kind);
			data.put(PARAM_KEYS.PAGENO, page);
			data.put(PARAM_KEYS.DECODE_CONTENT, PARAM_VALUES.N);
		} catch (JSONException e) {
		}
		postForEntityList(url, data, "查询失败:", callback, EventEntity.class);
	}
	
	public static void searchEventsByKeywords(String keyword, String poslong, String poslat, int page, final EntityListCallback<EventEntity> callback) {
		JSONObject data = getSessionJson();
//		{"fc":"get_action_bysearch", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", 
//			 "poslong":113.3124, "poslat":23.1428, "keyword":"深圳", "pageno":1 , "decode_content":"y"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_SEARCH_EVENT);
			data.put(PARAM_KEYS.POSLONG, poslong);
			data.put(PARAM_KEYS.POSLAT, poslat);
			data.put(PARAM_KEYS.KEYWORD, keyword);
			data.put(PARAM_KEYS.PAGENO, page);
			data.put(PARAM_KEYS.DECODE_CONTENT, PARAM_VALUES.N);
		} catch (JSONException e) {
		}
		postForEntityList(URL.SEARCH_EVENT, data, "查询失败:", callback, EventEntity.class);
	}
	
	/**
	 * 获取活动详情
	 * @param id
	 * @param callback
	 */
	public static void getEventInfo(final String id, final EntityCallback<EventEntity> callback) {
		JSONObject data = getSessionJson();
//		{"fc":"get_action_byactionid", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs",  "actionid":"571f34g-AD1820C-4g"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_EVENT_INFO);
			data.put(PARAM_KEYS.ACTIONID, id);
			data.put(PARAM_KEYS.DECODE_CONTENT, PARAM_VALUES.N);
		} catch (JSONException e) {
		}
		postForEntity(URL.GET_EVENT_INFO, data, "查询失败:", callback, EventEntity.class);
	}

	public static void getEventComments(final String id, int page, final EntityListCallback<CommentEntity> callback) {
		JSONObject data = getSessionJson();
//		{"fc":"get_action_reply_list", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "actionid":"56gi31s-36BC016-4g", "pageno":1}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_EVENT_COMMENTS);
			data.put(PARAM_KEYS.ACTIONID, id);
			data.put(PARAM_KEYS.PAGENO, page);
		} catch (JSONException e) {
		}
		postForEntityList(URL.GET_EVENT_COMMENTS, data, "查询失败:", callback, CommentEntity.class);
	}
	
	public static void publishEvent(String title, String content, String type, String address, String start_time, String end_time, String latitude, String longitude, 
			String show_mode, List<UploadImgEntity> entitys, final EntityCallback<String> callback) {
		JSONObject data = getSessionJson();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_PUBLISH_EVENT);
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
		postForString(URL.PUBLISH_EVENT, data, "发布失败:", callback, PARAM_KEYS.ACTIONID);
	}

	public static void publishEvent_v2(String title, String content, String type, String address, String start_time, String end_time, String latitude, String longitude, 
			String show_mode, List<UploadImgEntity> entitys, final EntityCallback<String> callback) {
		JSONObject data = getSessionJson();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_PUBLISH_EVENT_V2);
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
		postForString(URL.PUBLISH_EVENT_V2, data, "发布失败:", callback, PARAM_KEYS.ACTIONID);
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
		JSONObject data = getSessionJson();
//		{"fc":"get_action_template", "userid":"18979528420", "loginid":"t4etskerghskdryhgsdfklhs", "kindof":""}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_EVENT_TEMPLATE);
			data.put(PARAM_KEYS.KINDOF, "");
		} catch (JSONException e) {
		}
		postForEntityList(URL.GET_EVENT_TEMPLATE, data, "加载模板失败:", callback, EventTemplate.class);
	}
	
	public static void signEvent(String event_id, final DefaultCallback callback) {
		signEvent0(event_id, PARAM_VALUES.SIGN_EVENT_SIGN, callback);
	}
	
	public static void checkEvent(String event_id, final DefaultCallback callback) {
		signEvent0(event_id, PARAM_VALUES.SIGN_EVENT_CHECK, callback);
	}
	
	public static void cancelEvent(String event_id, final DefaultCallback callback) {
		signEvent0(event_id, PARAM_VALUES.SIGN_EVENT_CANCEL, callback);
	}
	
	private static void signEvent0(String event_id, String type, final DefaultCallback callback) {
		JSONObject data = getSessionJson();
//		{"fc":"set_action_sign", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "actionid":"58dh48i-8327549-4g", "does":"sign"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_SIGN_EVENT);
			data.put(PARAM_KEYS.ACTIONID, event_id);
			data.put(PARAM_KEYS.DOES, type);
		} catch (JSONException e) {
		}
		postDefalt(URL.SIGN_EVENT, data, "", callback);
	}
	
	public static void getAllSignedEvents(final EntityListCallback<EventEntity> callback) {
		List<EventEntity> list = new ArrayList<EventEntity>();
		getSignedEvents(1, list, callback);
	}
	
	private static void getSignedEvents(final int page, final List<EventEntity> all_list, final EntityListCallback<EventEntity> callback) {
		JSONObject data = getSessionJson();
//		{"fc":"action_sign_ofmy", "userid":18000343400, "loginid":"t4etskerghskdryhgsdfklhs", "pageno":1}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_SIGN_EVENT);
			data.put(PARAM_KEYS.PAGENO, page);
		} catch (JSONException e) {
		}
		postForEntityList(URL.GET_SIGN_EVENT, data, "获取参与的活动列表失败:", new EntityListCallback<EventEntity>() {
			@Override
			public void succeed(List<EventEntity> list) {
				if (!Validator.isEmptyList(list)) {
					all_list.addAll(list);
					getSignedEvents(page + 1, all_list, callback);
				} else
					callback.succeed(all_list);
			}
			
			@Override
			public void fail(String error) {
				callback.fail(error);
			}
		}, EventEntity.class);
	}
	
	public static void getSignedEvents(final int page, final EntityListCallback<EventEntity> callback) {
		JSONObject data = getSessionJson();
//		{"fc":"action_sign_ofmy", "userid":18000343400, "loginid":"t4etskerghskdryhgsdfklhs", "pageno":1}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_SIGN_EVENT);
			data.put(PARAM_KEYS.PAGENO, page);
		} catch (JSONException e) {
		}
		postForEntityList(URL.GET_SIGN_EVENT, data, "获取参与的活动列表失败:", callback, EventEntity.class);
	}
	
	public static void getEventSigners(String event_id, final EntityListCallback<SignerEntity> callback) {
		JSONObject data = getSessionJson();
//		{"fc":"get_action_sign_list", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "actionid":"58dh48i-8327549-4g"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_SIGN_USERS);
			data.put(PARAM_KEYS.ACTIONID, event_id);
		} catch (JSONException e) {
		}
		postForEntityList(URL.GET_SIGN_USERS, data, "获取报名列表失败:", callback, SignerEntity.class);
	}
}
