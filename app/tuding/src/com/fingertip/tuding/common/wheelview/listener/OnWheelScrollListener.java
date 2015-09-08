package com.fingertip.tuding.common.wheelview.listener;


import com.fingertip.tuding.common.wheelview.WheelView;

/**
 * Wheel scrolled listener interface.
 */
public interface OnWheelScrollListener {
	/**
	 * Callback method to be invoked when scrolling started.
	 * @param wheel the wheel view whose state 
	 */
	void onScrollingStarted(WheelView wheel);
	
	/**
	 * Callback method to be invoked when scrolling ended.
	 * @param wheel the wheel view whose state 
	 */
	void onScrollingFinished(WheelView wheel);
}
