package com.fingertip.tuding.entity;

import java.io.Serializable;

import com.fingertip.tuding.base.BaseEntity;

/** 分享实体 **/
public class ShareEntity extends BaseEntity implements Serializable{
	/**  **/
	private static final long serialVersionUID = 1L;
	
	/** 分享的信息id **/
	public String aid;
	
	/** 分享的标题 **/
	public String shareTitle;
	/** 分享的内容 **/
	public String shareContent;
	/**  **/
	public String targetUrl;
	/** 分享图片的地址 **/
	public String imgUrl;
	
	/** 若为分享应用时的下载地址 **/
	public String downloadUrl;
	
	public String sender_id;
	
}
