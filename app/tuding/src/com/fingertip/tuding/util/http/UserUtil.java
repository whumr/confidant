package com.fingertip.tuding.util.http;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.EventEntity;
import com.fingertip.tuding.entity.MessageEntity;
import com.fingertip.tuding.entity.UserEntity;
import com.fingertip.tuding.entity.WatchEntity;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.callback.DefaultCallback;
import com.fingertip.tuding.util.http.callback.EntityCallback;
import com.fingertip.tuding.util.http.callback.EntityListCallback;
import com.fingertip.tuding.util.http.callback.JsonCallback;
import com.fingertip.tuding.util.http.common.ServerConstants;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_VALUES;
import com.fingertip.tuding.util.http.common.ServerConstants.URL;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class UserUtil extends BaseHttpUtil {

	/**
	 * 获取用户资料
	 * @param user_id
	 * @param callback
	 */
	public static void getUserInfo(String user_id, EntityCallback<UserEntity> callback) {
		getUserInfo(user_id, callback, true);
	}
	
	/**
	 * 获取用户资料
	 * @param user_id
	 * @param callback
	 * @param full
	 */
	public static void getUserInfo(String user_id, final EntityCallback<UserEntity> callback, boolean full) {
		JSONObject data = getSessionJson();
//		{"fc":"get_user_infor", "userid":1257053, "loginid":"t4etskerghskdryhgsdfklhs", "inforuid":1257053, "infor":"mini"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_USER_INFO);
			data.put(PARAM_KEYS.INFORUID, user_id);
			if (full)
				data.put(PARAM_KEYS.INFOR, PARAM_VALUES.INFOR_PLUS);
			else
				data.put(PARAM_KEYS.INFOR, PARAM_VALUES.INFOR_MINI);
		} catch (JSONException e) {
		}
		postForEntity(URL.GET_USER_INFO, data, "获取用户资料失败:", callback, UserEntity.class);
	}

	/**
	 * 昵称查询用户
	 * @param nick
	 * @param callback
	 */
	public static void searchUserByNick(String nick, final EntityListCallback<UserEntity> callback) {
		JSONObject data = getSessionJson();
//		{"fc":"get_user_bysearch", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "nick":"Jim"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_SEARCH_USER);
			data.put(PARAM_KEYS.USER_NICK_NAME, nick);
		} catch (JSONException e) {
		}
		postForEntityList(URL.SEARCH_USER, data, "查询用户失败:", callback, UserEntity.class);
	}
	
	/**
	 * 添加、删除关注，屏蔽
	 * 
	 * @param user_id	用户id
	 * @param action	PARAM_VALUES.LINK_WATCH, LINK_HAOYOU, LINK_HEI, LINK_SHANCHU
	 * @param callback
	 */
	public static void editWatch(String user_id, String action, final DefaultCallback callback) {
		JSONObject data = getSessionJson();
//		{"fc":"friend_link", "userid":1257053, "loginid":"t4etskerghskdryhgsdfklhs", "frienduid":"1644980", "link":"shanchu"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_EDIT_WATCH);
			data.put(PARAM_KEYS.FRIENDUID, user_id);
			data.put(PARAM_KEYS.LINK, action);
		} catch (JSONException e) {
		}
		postDefalt(URL.EDIT_WATCH, data, "操作失败:", callback);
	}
	
	/**
	 * 发送私聊消息
	 * @param to_user_id
	 * @param msg
	 * @param callback
	 */
	public static void sendMsg(String to_user_id, String content, final DefaultCallback callback) {
		sendTextMsg(to_user_id, content, null, null, callback);
	}
	
	/**
	 * 发送消息
	 * 
	 * @param to_user_id
	 * @param content
	 * @param title
	 * @param event_id
	 * @param callback
	 */
	public static void sendTextMsg(String to_user_id, String content, String title, String event_id, final DefaultCallback callback) {
	//		{"fc":"send_msg", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", 
	//		 "touserid":"18979528420", "says":"eyJraW5kIjoidGV4dCIsImNvbnRlbnQiOiI2SXVONkl5cjU1cUU1YVNwNXJhdjVwaXY1b2lSNTVxRTU0aXgifQ=="}
		JSONObject data = getSessionJson();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_SEND_MSG);
			data.put(PARAM_KEYS.TOUSERID, to_user_id);
			JSONObject says = new JSONObject();
			says.put(PARAM_KEYS.KIND, PARAM_VALUES.SAYS_TEXT);
			says.put(PARAM_KEYS.CONTENT, Tools.encodeString(content));
			if (!Validator.isEmptyString(title))
				says.put(PARAM_KEYS.QUOTE, title);
			if (!Validator.isEmptyString(event_id))
				says.put(PARAM_KEYS.JUMPPARAM, PARAM_KEYS.ACTIONID + ":" + event_id);
			data.put(PARAM_KEYS.SAYS, Tools.encodeString(says.toString()));
		} catch (JSONException e) {
		}
		postDefalt(URL.SEND_MSG, data, "发送失败:", callback);
	}
	
	/**
	 * 获取用户关注列表
	 * @param callback
	 */
	public static void getUserWatchList(final EntityListCallback<WatchEntity> callback) {
		JSONObject data = getSessionJson();
//		{"fc":"get_friend_list", "userid":1257053, "loginid":"t4etskerghskdryhgsdfklhs", "link":"guanzhu", "infor":"mini"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_MY_WATCH);
			data.put(PARAM_KEYS.LINK, PARAM_VALUES.LINK_WATCH);
			data.put(PARAM_KEYS.INFOR, PARAM_VALUES.INFOR_STAND);
		} catch (JSONException e) {
		}
		postForEntityList(URL.GET_MY_WATCH, data, "获取关注列表失败:", callback, WatchEntity.class);
	}
	
	/**
	 * 加载用户关注列表到内存
	 */
	public static void loadWatchList(final DefaultCallback callback) {
		final UserSession session = UserSession.getInstance();
		if (session.isLogin()) {
			final Set<String> watch_set = session.getWatcher_list();
			getUserWatchList(new EntityListCallback<WatchEntity>() {
				@Override
				public void succeed(List<WatchEntity> list) {
					watch_set.clear();
					if (!Validator.isEmptyList(list)) {
						for (WatchEntity watchEntity : list)
							watch_set.add(watchEntity.user.id);
					}
					session.setWatcher_list(list);
					session.setLoad_watcher(true);
					if (callback != null)
						callback.succeed();
				}
				
				@Override
				public void fail(String error) {
					if (callback != null)
						callback.fail(error);
				}
			});
		}
	}
	
	/**
	 * 获取用户关注的活动列表
	 * @param page
	 * @param callback
	 */
	public static void getUserFavorEvents(final EntityListCallback<EventEntity> callback) {
		JSONObject data = getSessionJson();
//		{"fc":"action_fav_ofmy", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_MY_FAVOR_EVENT);
			data.put(PARAM_KEYS.DECODE_CONTENT, PARAM_VALUES.N);
		} catch (JSONException e) {
		}
		postForEntityList(URL.GET_MY_FAVOR_EVENT, data, "获取活动列表失败:", callback, EventEntity.class);
	}
	
	public static void loadFavorList(final DefaultCallback callback) {
		final UserSession session = UserSession.getInstance();
		if (session.isLogin()) {
			final Set<String> favor_set = session.getFavor_event_list();
			getUserFavorEvents(new EntityListCallback<EventEntity>() {
				@Override
				public void succeed(List<EventEntity> list) {
					favor_set.clear();
					if (!Validator.isEmptyList(list)) {
						for (EventEntity eventEntity : list)
							favor_set.add(eventEntity.id);
					}
					session.setLoad_favor(true);
					if (callback != null)
						callback.succeed();
				}
				
				@Override
				public void fail(String error) {
					if (callback != null)
						callback.fail(error);
				}
			});
		}
	}
	
	public static void loadSignedList(final DefaultCallback callback) {
		final UserSession session = UserSession.getInstance();
		if (session.isLogin()) {
			final Set<String> signed_set = session.getSign_list();
			EventUtil.getAllSignedEvents(new EntityListCallback<EventEntity>() {
				@Override
				public void succeed(List<EventEntity> list) {
					signed_set.clear();
					signed_set.clear();
					if (!Validator.isEmptyList(list)) {
						for (EventEntity eventEntity : list)
							signed_set.add(eventEntity.id);
					}
					session.setLoad_sign_event(true);
					if (callback != null)
						callback.succeed();
				}
				
				@Override
				public void fail(String error) {
					if (callback != null)
						callback.fail(error);
				}
			});
		}
	}
	
	/**
	 * 获取用户的消息
	 * @param callback
	 */
	public static void loadUserMsg(String lastread, final SharedPreferenceUtil sp, final EntityListCallback<MessageEntity> callback) {
		JSONObject data = getSessionJson();
//		{"fc":"get_msg_ofmy", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "lastread":"-"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_MY_MSG);
			data.put(PARAM_KEYS.LASTREAD, lastread);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_MY_MSG, params, new RequestCallBack<String>() {
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = Tools.decodeString(responseInfo.result);
				String error = null;
				JSONObject json = null;
				List<MessageEntity> list = new ArrayList<MessageEntity>();
				try {
					json = new JSONObject(result);
//					{"fc":"get_msg_ofmy","ok":"y","lastread":"5ale25t","list":[],"have_new":["15088138465","18588859895","13641411876","18682198397"]}
					Log.e("getMsg", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else {
						list = MessageEntity.parseJsonArray(json);
						String user_id = UserSession.getInstance().getId();
						if (!Validator.isEmptyList(list))
							sp.setBooleanValue(user_id, SharedPreferenceUtil.HAS_NEW_MESSAGE, true);
						String have_new = json.getString(PARAM_KEYS.HAVE_NEW);
						if (have_new.trim().length() > 2)
							sp.setBooleanValue(user_id, SharedPreferenceUtil.HAS_NEW_WATCH, true);
						sp.setStringValue(user_id, PARAM_KEYS.LASTREAD, json.getString(PARAM_KEYS.LASTREAD));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					error = "获取消息列表失败:" + e.getMessage();
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
	 * 获取自己发布的活动
	 * @param callback
	 */
	public static void getMyEvents(int page, final EntityListCallback<EventEntity> callback) {
		JSONObject data = getSessionJson();
//		{"fc":"action_ofmy", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_MY_EVENT);
			data.put(PARAM_KEYS.PAGENO, page);
			data.put(PARAM_KEYS.DECODE_CONTENT, PARAM_VALUES.N);
		} catch (JSONException e) {
		}
		postForEntityList(URL.GET_MY_EVENT, data, "获取活动列表失败:", callback, EventEntity.class);
	}
	
	/**
	 * 获取用户发布的活动
	 * @param user_id
	 * @param callback
	 */
	public static void getUserEvents(String user_id, final EntityListCallback<EventEntity> callback) {
		JSONObject data = getSessionJson();
//		{"fc":"action_list_byuser", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "byuser":13641411876}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_USER_EVENT);
			data.put(PARAM_KEYS.BYUSER, user_id);
			data.put(PARAM_KEYS.DECODE_CONTENT, PARAM_VALUES.N);
		} catch (JSONException e) {
		}
		postForEntityList(URL.GET_USER_EVENT, data, "获取活动列表失败:", callback, EventEntity.class);
	}
	
	public static void checkReg(String phone_list, final JsonCallback callback) {
		JSONObject data = getSessionJson();
//		{"fc":"friend_link", "userid":1257053, "loginid":"t4etskerghskdryhgsdfklhs", "frienduid":"1644980", "link":"shanchu"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_CHECK_PHONE_REG);
			data.put(PARAM_KEYS.PHONELIST, phone_list);
		} catch (JSONException e) {
		}
		postForJson(URL.CHECK_PHONE_REG, data, "获取数据失败:", callback);
	}
	
	public static void modifyUserInfo(String[] keys, String[] values, final DefaultCallback callback) {
		JSONObject data = getSessionJson();
//		{"fc":"user_infor_edit", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", 
//			 "head":"http://xxxxxxxx/xxx.jpg", "headbig":"http://xxxxxx/xxx.jpg",
//			 "nick":"Jim", "address":"广州市体育东路1号", "aboutme":"我就是我"}
		try {
			//必须参数
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_EDIT_USER_INFO);
			//修改值
			for (int i = 0; i < keys.length; i++) 
				data.put(keys[i], values[i]);
		} catch (JSONException e) {
		}
		postDefalt(URL.EDIT_USER_INFO, data, "编辑个人资料失败:", callback);
	}
	
	public static void deleteMyEvent(String event_id, final DefaultCallback callback) {
		JSONObject data = getSessionJson();
//		{"fc":"action_delete", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "actionid":"56gg55e-D5CB6E8-4g"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_DELETE_MY_EVENT);
			data.put(PARAM_KEYS.ACTIONID, event_id);
		} catch (JSONException e) {
		}
		postDefalt(URL.DELETE_MY_EVENT, data, "删除失败:", callback);
	}
	
	public static void deleteMyFavorEvent(String event_id, final DefaultCallback callback) {
		JSONObject data = getSessionJson();
//		{"fc":"action_fav_del", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "actionid":"56gg55e-D5CB6E8-4g"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_DELETE_MY_FAVOR_EVENT);
			data.put(PARAM_KEYS.ACTIONID, event_id);
		} catch (JSONException e) {
		}
		postDefalt(URL.DELETE_MY_FAVOR_EVENT, data, "删除失败:", callback);
	}
	
	public static void deleteMyWatcher(final String user_id, final DefaultCallback callback) {
		JSONObject data = getSessionJson();
//		{"fc":"friend_link", "userid":1257053, "loginid":"t4etskerghskdryhgsdfklhs", "frienduid":"1644980", "link":"shanchu"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_EDIT_WATCH);
			data.put(PARAM_KEYS.FRIENDUID, user_id);
			data.put(PARAM_KEYS.LINK, PARAM_VALUES.LINK_SHANCHU);
		} catch (JSONException e) {
		}
		postDefalt(URL.EDIT_WATCH, data, "删除失败:", callback);
	}
	
	public static void resetPassword(String old_pwd, String new_pwd, final DefaultCallback callback) {
		JSONObject data = getSessionJson();
		try {
			//http://tutuapp.aliapp.com/_user/user_pass_edit.php
			//{"fc":"user_pass_edit", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "oldpass":"123456", "newpass":"abcdefg"}
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_EDIT_USER_PASS);
			data.put(PARAM_KEYS.USER_OLD_PASS, old_pwd);
			data.put(PARAM_KEYS.USER_NEW_PASS, new_pwd);
		} catch (JSONException e) {
		}
		postDefalt(URL.EDIT_USER_PASS, data, "修改密码失败:", callback);
	}
	
	public static void uploadHeadImg(String big_head_path, String small_head_path, final JsonCallback callback) {
//	            		"fc":"upload_file", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs",
//	            		 "filefor":"头像"
//	            		sfile 缩略图, sfull 原图
		JSONObject data = getSessionJson();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_UPLOAD_FILE);
			data.put(PARAM_KEYS.UPLOAD_FILEFOR, PARAM_VALUES.UPLOAD_HEAD);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addQueryStringParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		params.addBodyParameter(PARAM_KEYS.UPLOAD_SFULL, new File(big_head_path));
		params.addBodyParameter(PARAM_KEYS.UPLOAD_SFILE, new File(small_head_path));

		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, ServerConstants.URL.UPLOAD_IMG, params,
		    new RequestCallBack<String>() {
		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	String result = Tools.decodeString(responseInfo.result);
					String error = null;
					String file_url = null;
					String full_url = null;
					JSONObject json = null;
					try {
						json = new JSONObject(result);
						file_url = json.getString(PARAM_KEYS.UPLOAD_RESULT_URLFILE);
						full_url = json.getString(PARAM_KEYS.UPLOAD_RESULT_URLFULL);
//	            		上传图片返回结果无状态码
					} catch (JSONException e) {
						e.printStackTrace();
						error = e.getMessage();
					}
					if (file_url == null || full_url == null)
						error = "上传图片失败";
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
	
	public static void getMyWatchGroup(String poslat, String poslong, int page, final EntityListCallback<EventEntity> callback) {
//		{"fc":"get_myfriend_action", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", 
//			 "poslong":113.8124, "poslat":22.5428, "pageno":1 }
		JSONObject data = getSessionJson();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_WATCH_GROUP);
			data.put(PARAM_KEYS.POSLAT, poslat);
			data.put(PARAM_KEYS.POSLONG, poslong);
			data.put(PARAM_KEYS.PAGENO, page);
			data.put(PARAM_KEYS.DECODE_CONTENT, PARAM_VALUES.N);
		} catch (JSONException e) {
		}
		postForEntityList(URL.GET_WATCH_GROUP, data, "查询失败:", callback, EventEntity.class);
	}

	public static Bitmap createQRCode(String user_id, int widthAndHeight) throws WriterException {  
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
        BitMatrix matrix = new MultiFormatWriter().encode(URL.USER_BARCODE_BASE + user_id, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);  
        int width = matrix.getWidth();  
        int height = matrix.getHeight();  
        int[] pixels = new int[width * height];  
  
        for (int y = 0; y < height; y++) {  
            for (int x = 0; x < width; x++) {  
                if (matrix.get(x, y))
                    pixels[y * width + x] = Color.BLACK;  
                else
                	pixels[y * width + x] = Color.WHITE;  
            }  
        }  
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
        return bitmap;  
    }
	
	public static String parseBarcode(String result) {
		if (result.startsWith(URL.USER_BARCODE_BASE)) {
			result = result.substring(URL.USER_BARCODE_BASE.length()).trim();
			if (Validator.isMobilePhone(result))
				return result;
		}
		return null;
	}
	
	public static void Login(final String account, final String password, final Location location, final JsonCallback callback) {
		JSONObject data = new JSONObject();
//			{"fc":"user_login","userid":1257053, "pass":"123456", "poslong":"113.3261", "poslat":"23.1330"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_LOGIN);
			data.put(PARAM_KEYS.USERID, account);
			data.put(PARAM_KEYS.PASSWORD, password);
			data.put(PARAM_KEYS.POSLAT, location == null ? "" : location.getLatitude());
			data.put(PARAM_KEYS.POSLONG, location == null ? "" : location.getLongitude());
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Tools.encodeString(data.toString()));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.LOGIN, params, new RequestCallBack<String>() {
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = Tools.decodeString(responseInfo.result);
				String error = null;
				String login_id = null;
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						login_id = json.getString(PARAM_KEYS.LOGINID);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (login_id == null)
					error = "登录失败";
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
}
