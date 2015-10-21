package com.fingertip.tuding.services;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.MessageEntity;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.UserUtil;
import com.fingertip.tuding.util.http.callback.EntityListCallback;
import com.fingertip.tuding.util.http.common.ServerConstants;

public class MessageService extends Service {
	
	private Timer timer = new Timer();
	private SharedPreferenceUtil sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (sp == null)
			sp = new SharedPreferenceUtil(this);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (Tools.isNetworkConnected(MessageService.this)) {
					final UserSession session = UserSession.getInstance();
					String lastread = sp.getStringValue(session.getId(), SharedPreferenceUtil.LASTREAD);
					if ("".equals(lastread))
						lastread = "-1";
					UserUtil.loadUserMsg(lastread, sp, new EntityListCallback<MessageEntity>() {
						@Override
						public void succeed(List<MessageEntity> list) {
							if (!Validator.isEmptyList(list)) {
								for (MessageEntity msg : list)
									msg.receiver_id = session.getId();
								Tools.saveMessages(MessageService.this, list);
							}
						}
						
						@Override
						public void fail(String error) {
						}
					});
				}
			}
		}, 0, ServerConstants.GET_MESSAGE_GAP * 1000);
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		timer.cancel();
		timer = null;
	}
}
