package com.example.viewdraglayout;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class VDHLayout extends LinearLayout {
	
	private ViewDragHelper mDragHelper;
	
	private View mDragView;
	private View mAutoBackView;
	private View mEdgeTrackerView;
	
	private Point mAutoBackPos =new Point();
	
	public VDHLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDragHelper = ViewDragHelper.create(this, 1.0f, new Callback() {
			
			@Override
			public boolean tryCaptureView(View child, int pointerId) {
				return child == mDragView || child == mAutoBackView; 
			}
			@Override
			public int clampViewPositionHorizontal(View child, int left, int dx) {
				return left;
			}
			@Override
			public int clampViewPositionVertical(View child, int top, int dy) {
				return top;
			}
			
			@Override
			public void onViewReleased(View releasedChild, float xvel,
					float yvel) {
				if(releasedChild == mAutoBackView){
					mDragHelper.settleCapturedViewAt(mAutoBackPos.x, mAutoBackPos.y);
					invalidate();
				}
			}
			@Override
			public void onEdgeDragStarted(int edgeFlags, int pointerId) {
				mDragHelper.captureChildView(mEdgeTrackerView, pointerId);
			}
		});
		mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDragHelper.processTouchEvent(event);
		return true;
	}
	
	@Override
	public void computeScroll() {
		if(mDragHelper.continueSettling(true)){
			invalidate();
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		mAutoBackPos.x = mAutoBackView.getLeft();
		mAutoBackPos.y = mAutoBackView.getTop();
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mDragView = getChildAt(0);
		mAutoBackView = getChildAt(1);
		mEdgeTrackerView = getChildAt(2);
		
	}
	
}
