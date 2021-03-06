package com.fingertip.tuding.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fingertip.tuding.entity.EventTemplate;
import com.fingertip.tuding.entity.UserEntity;
import com.fingertip.tuding.entity.WatchEntity;
import com.fingertip.tuding.util.Validator;
import com.fingertip.tuding.util.http.UserUtil;

public class UserSession {
	
	private String id, login_id, nick_name, head_url, sex, place, mark;
	private boolean login, load_info, load_watcher, load_favor, load_event_template, load_sign_event;
	private Set<String> watcher_list = new HashSet<String>(), 
			favor_event_list = new HashSet<String>(),
			sign_list = new HashSet<String>();
	private List<EventTemplate> event_templates = new ArrayList<EventTemplate>();

	private static UserSession session;
	
	private UserSession() {
	}
	
	public static UserSession getInstance() {
		if (session == null)
			session = new UserSession();
		return session;
	}

	public static void logout() {
		if (session != null) {
			session.getFavor_event_list().clear();
			session.getWatcher_list().clear();
			session.getSign_list().clear();
			session = null;
		}
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getLogin_id() {
		return login_id;
	}

	public void setLogin_id(String login_id) {
		this.login_id = login_id;
	}

	public String getHead_url() {
		return head_url;
	}

	public void setHead_url(String head_url) {
		this.head_url = head_url;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
		if (login)
			loadInitData();
	}

	public boolean isLoad_info() {
		return load_info;
	}

	public void setLoad_info(boolean load_info) {
		this.load_info = load_info;
	}

	public Set<String> getWatcher_list() {
		return watcher_list;
	}

	public void setWatcher_list(List<WatchEntity> list) {
		getWatcher_list();
		watcher_list.clear();
		if (!Validator.isEmptyList(list)) {
			for (WatchEntity watchEntity : list) {
				watcher_list.add(watchEntity.user.id);
			}
		}
	}

	public Set<String> getFavor_event_list() {
		return favor_event_list;
	}

	public void setFavor_event_list(Set<String> favor_event_list) {
		this.favor_event_list = favor_event_list;
	}

	public boolean isLoad_watcher() {
		return load_watcher;
	}

	public void setLoad_watcher(boolean load_watcher) {
		this.load_watcher = load_watcher;
	}

	public boolean isLoad_favor() {
		return load_favor;
	}

	public void setLoad_favor(boolean load_favor) {
		this.load_favor = load_favor;
	}

	public boolean isLoad_event_template() {
		return load_event_template;
	}

	public void setLoad_event_template(boolean load_event_template) {
		this.load_event_template = load_event_template;
	}

	public List<EventTemplate> getEvent_templates() {
		return event_templates;
	}
	
	public boolean isLoad_sign_event() {
		return load_sign_event;
	}

	public void setLoad_sign_event(boolean load_sign_event) {
		this.load_sign_event = load_sign_event;
	}

	public Set<String> getSign_list() {
		return sign_list;
	}

	public UserEntity getUser() {
		UserEntity user = new UserEntity();
		user.id = id;
		user.nick_name = nick_name;
		user.head_img_url = head_url;
		user.sex = sex;
		user.place = place;
		user.mark = mark;
		return user;
	}
	
	private void loadInitData() {
		if (!load_favor)
			UserUtil.loadFavorList(null);
		if (!load_watcher)
			UserUtil.loadWatchList(null);
		if (!load_sign_event)
			UserUtil.loadSignedList(null);
	}
}
