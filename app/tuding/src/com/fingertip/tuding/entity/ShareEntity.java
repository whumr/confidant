package com.fingertip.tuding.entity;

import java.io.Serializable;

/** ����ʵ�� **/
public class ShareEntity implements Serializable{
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
	
}
