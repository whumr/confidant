package com.fingertip.tuding.entity;

import java.io.Serializable;

import com.fingertip.tuding.base.BaseEntity;

/** ����ʵ�� **/
public class ShareEntity extends BaseEntity implements Serializable{
	/**  **/
	private static final long serialVersionUID = 1L;
	
	/** �������Ϣid **/
	public String aid;
	
	/** ����ı��� **/
	public String shareTitle;
	/** ��������� **/
	public String shareContent;
	/**  **/
	public String targetUrl;
	/** ����ͼƬ�ĵ�ַ **/
	public String imgUrl;
	
	/** ��Ϊ����Ӧ��ʱ�����ص�ַ **/
	public String downloadUrl;
	
	public String sender_id;
	
}
