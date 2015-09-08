package com.fingertip.tuding.util.http.callback;

import java.util.List;

public interface EntityListCallback<E> {

	public void succeed(List<E> list);
	
	public void fail(String error);
}
