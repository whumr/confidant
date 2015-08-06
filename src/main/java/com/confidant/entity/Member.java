package com.confidant.entity;

import com.confidant.common.BaseEntity;

import java.util.Date;

/**
 * Created by Administrator on 2015/1/29.
 */
public class Member extends BaseEntity {

    public static String SEX_MAlE = "M", SEX_FEMALE = "F", SEX_DEFAULT = SEX_MAlE;

    private Long id;
    private String account, password, nick_name, image_url, sex;// = SEX_DEFAULT;
    private Date create_time, last_updated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Date last_updated) {
        this.last_updated = last_updated;
    }
}
