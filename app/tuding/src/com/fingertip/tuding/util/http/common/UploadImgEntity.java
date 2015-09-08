package com.fingertip.tuding.util.http.common;

import java.io.File;
import java.io.Serializable;

public class UploadImgEntity implements Serializable {

	private static final long serialVersionUID = 1913842944650922162L;

	public String small_url, big_url;
	
	public File small_file, big_file;
}
