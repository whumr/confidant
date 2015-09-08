package com.fingertip.tuding.common.wheelview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class GestureScrollView extends ScrollView {

	GestureDetector myGesture;

	public GestureScrollView(Context context) {
		super(context);
	}

	public GestureScrollView(Context context, GestureDetector gest) {
		super(context);
		myGesture = gest;
	}

	public GestureScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(myGesture.onTouchEvent(ev))return true;
		else return super.onInterceptTouchEvent(ev);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(myGesture.onTouchEvent(ev))return true;
		else return super.onTouchEvent(ev);
	}
	
	public void setGesture(GestureDetector gest){
		myGesture=gest;
	}
}
