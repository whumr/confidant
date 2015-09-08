package com.fingertip.tuding.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;

public class CommentEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/** ��������Ϣ **/  
	public UserEntity userEntity;
	/** ����id **/
	public String id;
	/** �������� **/
	public String comment;
	/** ����ʱ�� **/
	public String time;
	/** ������ **/
	public int appraise;
	//�ظ�
	public String reply;
	
	public static List<CommentEntity> parseList(JSONObject json) {
		List<CommentEntity> list = new ArrayList<CommentEntity>();
		try {
			JSONArray array = json.getJSONArray(PARAM_KEYS.REPLYLIST);
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

	public static CommentEntity parseJson(JSONObject json) throws JSONException {
		CommentEntity comment = new CommentEntity();
		comment.id = json.getString(PARAM_KEYS.REPLYID);
		String content = json.getString(PARAM_KEYS.CONTENT);
		comment.comment = content;
		if (content.startsWith("{{") && content.indexOf("}}") > 0) {
			comment.reply = content.substring(2, content.indexOf("}}"));
			comment.comment = content.substring(content.indexOf("}}") + 2);
		}
		comment.time = json.getString(PARAM_KEYS.STIME);
		/** ��������Ϣ **/
		comment.userEntity = UserEntity.parseJson(json.getJSONObject(PARAM_KEYS.USERINFOR));
		return comment;
	}
}