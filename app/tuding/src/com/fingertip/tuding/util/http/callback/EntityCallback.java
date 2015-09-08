package com.fingertip.tuding.util.http.callback;

public interface EntityCallback<E> {

	public void succeed(E entity);
	
	public void fail(String error);
}
