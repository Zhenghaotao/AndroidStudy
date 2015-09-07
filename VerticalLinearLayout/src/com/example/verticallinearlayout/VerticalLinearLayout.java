package com.example.verticallinearlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

public class VerticalLinearLayout extends ViewGroup {
	
	//屏幕的高度
	private int mScreenHeight;
	
	//手指按下时的 getScrollY
	private int mScrollStart;
	
	//手指抬起时的 getScrollY
	private int mScrollEnd;
	
	//记录移动的 y
	private int mLastY;
	
	//记录移动的y
	private Scroller mScroller;
	
	//是否正在滚动
	private boolean isScrolling;
	
	
	//加速度检测器
	private VelocityTracker mVelocityTracker;
	
	//记录当前页
	private int currentPage = 0;
	
	private OnPageChangeListener mOnPageChangeListener;
	
	
	public VerticalLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		/**
		 * 获的屏幕的高度
		 */
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenHeight = dm.heightPixels;
		//初始化
		mScroller = new Scroller(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int count = getChildCount();
		for(int i = 0; i < count; i++){
			View childView = getChildAt(i);
			measureChild(childView, widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(changed){
			int childCount = getChildCount();
			//设置主布局的高度
			MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
			lp.height = mScreenHeight * childCount;
			setLayoutParams(lp);
			for(int i = 0; i < childCount ; i++){
				View child = getChildAt(i);
				if(child.getVisibility() != View.GONE){
					child.layout(l, i * mScreenHeight, r, (i + 1) * mScreenHeight);//调用每个子布局的layout
				}
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//如果当前正在滚动
		if(isScrolling){
			return super.onTouchEvent(event);
		}
		
		int action = event.getAction();
		int y = (int) event.getY();
		obtainVelocity(event);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mScrollStart = getScrollY();
			mLastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			if(!mScroller.isFinished()){
				mScroller.abortAnimation();
			}
			int dy = mLastY - y;
			//边界值检查
			int scrollY = getScrollY();
			//已经到达顶端了，下拉多少，往上滚动多少
			if(dy < 0 && scrollY + dy < 0){
				dy = -scrollY;
			}
			//已经到达顶部了，上拉多少，就往下滚动多少
			if(dy > 0 && scrollY > getHeight() - mScreenHeight){
				dy = getHeight() - mScreenHeight - scrollY;
			}
			scrollBy(0, dy);
			mLastY = y;
			break;
		case MotionEvent.ACTION_UP:
			mScrollEnd = getScrollY();
			
			int dScrollY = mScrollEnd - mScrollStart;
			
			if(wantScrollToNext()){//往上滑动
				if(shouldScrollToNext()){
					mScroller.startScroll(0, getScrollY(), 0, mScreenHeight - dScrollY);
				} else {
					mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
				}
			}
			if(wantScrollToPre()){
				if(shouldScrollToPre()){
					mScroller.startScroll(0, getScrollY(), 0, -mScreenHeight - dScrollY);
				} else {
					mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
				}
			}
			isScrolling = true;
			postInvalidate();
			recycyleVelocity();
			break;
		}
		
		return true;
	}
	/**
	 * 根据用户滑动，判断用户的意图是否滚动到上一页
	 * @return
	 */
	private boolean wantScrollToPre() {
		return mScrollEnd < mScrollStart;
	}

	/**
	 * 根据用户滑动，判断用户的意图是否能够滚动到上一页
	 * @return
	 */
	private boolean shouldScrollToPre() {
		return - mScrollEnd + mScrollStart  > mScreenHeight / 2 || Math.abs(getVelocity()) > 600;
	}

	private boolean shouldScrollToNext() {
		return mScrollEnd - mScrollStart  > mScreenHeight / 2 || Math.abs(getVelocity()) > 600;
	}

	private boolean wantScrollToNext() {
		return mScrollEnd > mScrollStart;
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if(mScroller.computeScrollOffset()){
			scrollTo(0, mScroller.getCurrY());
			postInvalidate();
		} else {
			int position = getScrollY() / mScreenHeight;
			if(position != currentPage){
				if(mOnPageChangeListener != null){
					currentPage = position;
					mOnPageChangeListener.onPageChange(currentPage);
				}
			}
			isScrolling = false;
		}
	}
	
	/**
	 * 获取y方向上的速度
	 * @return
	 */
	private int getVelocity(){
		mVelocityTracker.computeCurrentVelocity(1000);
		return (int) mVelocityTracker.getYVelocity();
	}
	/**
	 * 释放加速度器的资源
	 */
	private void recycyleVelocity(){
		if(mVelocityTracker != null){
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}
	
	
	/**
	 * 初始化加速度检测器
	 * @param event
	 */
	private void obtainVelocity(MotionEvent event) {
		if(mVelocityTracker == null){
			mVelocityTracker  = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}
	/**
	 * 设置回调接口
	 * @param onPageChangeListener
	 */
	 public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener){
		 this.mOnPageChangeListener = onPageChangeListener;
	 }
	
	public interface OnPageChangeListener{
		void onPageChange(int currentPage);
	}
	

}
