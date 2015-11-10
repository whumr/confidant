package com.fingertip.tuding.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.base.BaseEntity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * ��Ϣ
 * @author Administrator
 *
 */
public class MessageEntity extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static SimpleDateFormat SERVER_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	
//	{"chatid":"56u9223-599CF648CC56B4EA","userid":"18979528420",
//	"says":"eyJraW5kIjoidGV4dCIsImNvbnRlbnQiOiI1b2lSNVkrcTZJTzk1WkcxNVpHMTVMcUciLCAianVtcHBhcmFtIjoiNTZnaTMxcy0zNkJDMDE2LTRnIn0=",
//	"msgtype":"����","sendtime":"2015-06-30 09:22:06"}
//	msgtypeΪ��Ϣ���ͣ���"�Ի�", "����", "�ظ�", "����", "��ע"
//	sys����
//	{"kind":"text","content":"5oiR5Y+q6IO95ZG15ZG15LqG", "quote":"5ZCJ5bGL5Ye656ef5LiA5a6k5LiA5Y6F", "jumpparam":"actionid:56gi31s-36BC016-4g"}
	
	public String id, receiver_id;
	public UserEntity sender;
	public  String type;
	public long send_time;
	public SaysEntity says;
	public MessageDbEntity msg_db;
	
	

	public static Map<String, String> type_map = new HashMap<String, String>();
	static {
//		���ۣ��µĻ����
//		�ظ����ظ�����
//		��ע���µĹ�ע��Ϣ
//		���룺�µĻ����
		
			type_map.put("��ע", "�µĹ�ע��Ϣ");
	 		type_map.put("�Ի�", "�µĹ�ע��Ϣ");
		type_map.put("�ظ�", "�ظ�����");
		type_map.put("����", "�µĻ����");
	
		type_map.put("����", "�µĻ����");
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<MessageEntity> parseJsonArray(JSONObject json) throws JSONException {
		List<MessageEntity> list = new ArrayList<MessageEntity>();
		try {
			JSONArray array = json.getJSONArray(PARAM_KEYS.LIST);
			for (int i = 0; i < array.length(); i++) {
				try {
					list.addAll(parseUserList(array.getJSONObject(i)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private static List<MessageEntity> parseUserList(JSONObject json) throws JSONException {
		UserSession session = UserSession.getInstance();
		List<MessageEntity> list = new ArrayList<MessageEntity>();
		UserEntity user = UserEntity.parseJson(json);
		JSONArray msgs = json.getJSONArray(PARAM_KEYS.MSGLIST);
//		{"aboutme":"vvv��vv:x_*�ǺǹŹŹָֹոպû��û��øոպ�","userid":"13641411876","sex":"f","head":"http:\/\/x.jpg","nick":"�¸¸�",
//		"msglist":[{"userid":"13641411876","says":"==", "chatid":"57ai47n-67133922063A2A2B","sendtime":"2015-07-10 18:47:46","msgtype":"����"}],"address":"����ʡ������"}
		
//		{"chatid":"56u9223-599CF648CC56B4EA","userid":"18979528420",
//		"says":"eyJraW5kIjoidGV4dCIsImNvbnRlbnQiOiI1b2lSNVkrcTZJTzk1WkcxNVpHMTVMcUciLCAianVtcHBhcmFtIjoiNTZnaTMxcy0zNkJDMDE2LTRnIn0=",
//		"msgtype":"����","sendtime":"2015-06-30 09:22:06"}
		for (int i = 0; i < msgs.length(); i++) {
			JSONObject j = msgs.getJSONObject(i);
			MessageEntity msg = new MessageEntity();
			msg.id = j.getString(PARAM_KEYS.CHATID);
			msg.type = j.getString(PARAM_KEYS.MSGTYPE);
			try {
				msg.send_time = SERVER_SDF.parse(j.getString(PARAM_KEYS.SENDTIME)).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			msg.sender = user;
			String says = Tools.decodeString(j.getString(PARAM_KEYS.SAYS));
			msg.says = SaysEntity.parseJson(new JSONObject(says));
			
			MessageDbEntity msg_db = new MessageDbEntity();
			msg_db.receiver_id = session.getId();
			msg_db.id = msg.id;
			msg_db.type = msg.type;
			msg_db.send_time = msg.send_time;
			msg_db.sender_id = user.id;
			msg_db.sender_name = user.nick_name;
			msg_db.sender_head = user.head_img_url;
			msg_db.says = j.getString(PARAM_KEYS.SAYS);
			msg.msg_db = msg_db;
			list.add(msg);
		}
		return list;
	}
	
	public static List<MessageEntity> parseDbEntity(List<MessageDbEntity> list) {
		if (list != null && !list.isEmpty()) {
			List<MessageEntity> result = new ArrayList<MessageEntity>();
			for (MessageDbEntity mde : list) {
				MessageEntity msg = new MessageEntity();
				msg.id = mde.id;
				msg.type = mde.type;
				msg.send_time = mde.send_time;
				UserEntity sender = new UserEntity();
				sender.id = mde.sender_id;
				sender.nick_name = mde.sender_name;
				sender.head_img_url = mde.sender_head;
				msg.sender = sender;
				String says = Tools.decodeString(mde.says);
				try {
					msg.says = SaysEntity.parseJson(new JSONObject(says));
				} catch (JSONException e) {
					continue;
				}
				result.add(msg);
			}
			return result;
		}
		return null;
	}
	
	public String getTypeStr() {
		return type_map.get(type) == null ? type : type_map.get(type);
	}

	public static class SaysEntity implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
//	kindֵΪtext��ʾ����ϢΪ���ı�
//	contentΪ��������Ϣ���ݣ�������Ϊ�ı����ݣ�
//	quoteΪ��ǰ�ظ��Ļ�ı��⣨��ѡ��
//	jumpparamΪ��ת������ð��ǰ��ʾ��ת���ͣ�ð�ź��ʾ����ID����ѡ��
//	{"kind":"text","content":"5oiR5Y+q6IO95ZG15ZG15LqG", "quote":"5ZCJ5bGL5Ye656ef5LiA5a6k5LiA5Y6F", "jumpparam":"actionid:56gi31s-36BC016-4g"}
		public String kind;
		public String content;
		public String quote;
		public String jumpparam;
		public String event_id;
		
		public static SaysEntity parseJson(JSONObject json) throws JSONException {
			SaysEntity says = new SaysEntity();
			says.kind = json.getString(PARAM_KEYS.KIND);
			says.content = Tools.decodeString(json.getString(PARAM_KEYS.CONTENT));
			String quote = json.has(PARAM_KEYS.QUOTE) ? json.getString(PARAM_KEYS.QUOTE) : null;
			if (!Validator.isEmptyString(quote))
				says.quote = Tools.decodeString(quote);
			String jumpparam = json.has(PARAM_KEYS.JUMPPARAM) ? json.getString(PARAM_KEYS.JUMPPARAM) : null;
			if (!Validator.isEmptyString(jumpparam)) {
				says.jumpparam = json.getString(PARAM_KEYS.JUMPPARAM);
				if (says.jumpparam.startsWith(PARAM_KEYS.ACTIONID) && says.jumpparam.contains(":"))
					says.event_id = says.jumpparam.split(":")[1];
			}
			return says;
		}
		
	}
	
	@Table(name = "message")
	public static class MessageDbEntity implements Serializable {
		
		private static final long serialVersionUID = 1L;

		@Id(column = "id")
	    public String id;
		
		@Column(column = "receiver_id")
		public String receiver_id;

		@Column(column = "says")
		public String says;
		
		@Column(column = "type")
		public String type;
		
		@Column(column = "send_time")
		public long send_time;
		
		@Column(column = "sender_id")
		public String sender_id;
		
		@Column(column = "sender_name")
		public String sender_name;
		
		@Column(column = "sender_head")
		public String sender_head;

	}
}
