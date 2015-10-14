package com.fingertip.tuding.util.http;

import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.http.callback.DefaultCallback;
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
					error = error_msg + e.getMessage();
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
}
