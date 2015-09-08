package com.fingertip.tuding.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.R;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;
import com.fingertip.tuding.util.http.common.ServerConstants.URL;

/**
 * 消息
 * @author Administrator
 *
 */
public class EventEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
	private static SimpleDateFormat SERVER_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	
	public static String STATUS_IN = "live", STATUS_OVER = "over", STATUS_IN_S = "进行中", STATUS_OVER_S = "已结束", STATUS_UNKNOWN_S = "未知";
	
//	"publictime":"2015-07-07 11:03:04"
//	"poslat":"22.553186","titleof":"饭否风格","userid":"13641411876","picof":"","replycount":"0","likedcount":"0","content":"红河谷",
//	"statusof":"over","actionid":"577b032-603E420-6h","poslong":"113.952568","address":"","timeto":"0000-00-00 00:00:00","kindof":"优惠\/特价"}

	public String id, title, content, userid, statusof, address, kindof, send_time_str;
	public long send_time, timeto;
	public int likedcount, replycount, viewcount;
	public double poslat, poslong;
	public List<String> pics_small, pics_big;
	public List<CommentEntity> comments = new ArrayList<CommentEntity>();
	public EventType event_type;
	
	public UserEntity sender;
	
	public String getSendTimeStr() {
		return SDF.format(new Date(send_time)); 
	}

	public String getTimeToStr() {
		return SDF.format(new Date(timeto)); 
	}

	public String getStatusStr() {
		if (STATUS_IN.equals(statusof))
			return STATUS_IN_S;
		else if (STATUS_OVER.equals(statusof))
			return STATUS_OVER_S;
		return STATUS_UNKNOWN_S; 
	}
	
	public int getKindImgInt() {
		switch (event_type) {
		case STUDY:
			return R.drawable.icon_event_type_study;
		case SPECIAL:
			return R.drawable.icon_event_type_special_selling;
		case SOCIALITY:
			return R.drawable.icon_event_type_party;
		case PERFORM:
			return R.drawable.icon_event_type_show;
		case SPORTS:
			return R.drawable.icon_event_type_sports;
		default:
			return R.drawable.icon_event_type_others;
		}
	}

//	"publictime":"2015-07-07 11:03:04","userinfor":
//	{"aboutme":"vvv不","userid":"13641411876","sex":"f","head":"http:\/\.jpg","nick":"嘎嘎嘎","address":"安徽省安庆市"},
//	"poslat":"22.553186","titleof":"饭否风格","userid":"13641411876","picof":"","replycount":"0","likedcount":"0","content":"红河谷",
//	"statusof":"over","actionid":"577b032-603E420-6h","poslong":"113.952568","address":"","timeto":"0000-00-00 00:00:00","kindof":"优惠\/特价"}
	public static List<EventEntity> parseList(JSONObject json) throws JSONException {
		List<EventEntity> list = new ArrayList<EventEntity>();
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
	
	public static EventEntity parseJson(JSONObject json) throws JSONException, ParseException {
		EventEntity event = new EventEntity();
		event.sender = UserEntity.parseJson(json.getJSONObject(PARAM_KEYS.USERINFOR));
//		public String id, title, content, userid, statusof, address, kindof;
//		public long send_time, timeto;
//		public int likedcount, replycount;
//		public double poslat, poslong;
		event.id = json.getString(PARAM_KEYS.ACTIONID);
		event.title = json.getString(PARAM_KEYS.TITLEOF);
		event.content = json.getString(PARAM_KEYS.CONTENT);
		event.userid = json.getString(PARAM_KEYS.USERID); 
		event.statusof = json.getString(PARAM_KEYS.STATUSOF);
		event.address = json.getString(PARAM_KEYS.ADDRESS);
		event.kindof = json.getString(PARAM_KEYS.KINDOF);
		event.event_type = EventType.getEventType(event.kindof);
		
		event.send_time_str = json.getString(PARAM_KEYS.PUBLICTIME);
		event.send_time = SERVER_SDF.parse(json.getString(PARAM_KEYS.PUBLICTIME)).getTime();
		event.timeto = SERVER_SDF.parse(json.getString(PARAM_KEYS.TIMETO)).getTime();
		
		event.likedcount = json.getInt(PARAM_KEYS.LIKEDCOUNT);
		event.replycount = json.getInt(PARAM_KEYS.REPLYCOUNT);
		event.viewcount = json.getInt(PARAM_KEYS.VIEWCOUNT);

		event.poslat = json.getDouble(PARAM_KEYS.POSLAT);
		event.poslong = json.getDouble(PARAM_KEYS.POSLONG);
		
		List<String> l_s = new ArrayList<String>();
		List<String> l_b = new ArrayList<String>();
		JSONArray pics = json.has(PARAM_KEYS.PICOF) && json.get(PARAM_KEYS.PICOF) instanceof JSONArray 
				? json.getJSONArray(PARAM_KEYS.PICOF) : null;
		if (pics != null && pics.length() > 0) {
			for (int i = 0; i < pics.length(); i++) {
				JSONObject pic = pics.getJSONObject(i);
				l_s.add(pic.getString(PARAM_KEYS.S));
				l_b.add(pic.getString(PARAM_KEYS.B));
			}
		}
		event.pics_small = l_s;
		event.pics_big = l_b;
		return event;
	}

	public String getShareContent() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("图丁用户（").append(sender.nick_name).append("）邀请您参加活动。").append(address);
		return buffer.toString();
	}

	public String getShareUrl() {
		return URL.SHARE_EVENT_URL + id;
	}
	
	public enum EventType {

		ALL("全部"), 
		SPECIAL("优惠/特价"), 
		PERFORM("娱乐/表演"), 
		SOCIALITY("社交/聚会"), 
		SPORTS("运动/户外"), 
		STUDY("学习/沙龙"), 
		OTHER("其它");

		private String type;

		private EventType(String type) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}

		public static EventType getEventType(String type) {
			for (EventType eventType : values()) {
				if (eventType.getType().equals(type))
					return eventType;
			}
			return null;
		}
	}
}
