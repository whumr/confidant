package com.fingertip.tuding.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.base.BaseEntity;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_KEYS;
import com.fingertip.tuding.util.http.common.ServerConstants.PARAM_VALUES;

public class SignerEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public String autoid, userid, checkout, timeof;
	public boolean checked;
	public Date time;
	public UserEntity user;
	
	@SuppressWarnings("unchecked")
	public static SignerEntity parseJson(JSONObject json) throws Exception {
		SignerEntity signer = new SignerEntity();
		signer.autoid = json.getString(PARAM_KEYS.AUTOID);
		signer.userid = json.getString(PARAM_KEYS.USERID);
		signer.checkout = json.getString(PARAM_KEYS.CHECKOUT);
		signer.timeof = json.getString(PARAM_KEYS.TIMEOF);
		signer.checked = PARAM_VALUES.Y.equals(signer.checkout);
		signer.time = Tools.strToDate(signer.timeof);
		signer.user = UserEntity.parseJson(json.getJSONObject(PARAM_KEYS.USERINFOR));
		return signer;
	}
	
	@SuppressWarnings("unchecked")
	public static List<SignerEntity> parseJsonArray(JSONObject json) throws Exception {
		List<SignerEntity> list = new ArrayList<SignerEntity>();
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
}
