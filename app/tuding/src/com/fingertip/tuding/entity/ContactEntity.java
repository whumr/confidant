package com.fingertip.tuding.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fingertip.tuding.base.BaseEntity;

/**
 * ÏûÏ¢
 * @author Administrator
 *
 */
public class ContactEntity extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static int REGISTERED = 1, NOT_REGISTERED = 2, WATCHED = 3;
	
	public String id;
	public String name;
	public List<String> phone_numbers = new ArrayList<String>();
	public int status = NOT_REGISTERED;
}
