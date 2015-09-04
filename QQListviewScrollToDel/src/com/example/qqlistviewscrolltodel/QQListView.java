package com.example.qqlistviewscrolltodel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class QQListView extends ListView {

	private static final String TAG = "QQListView";
	/**
	 * 用户滑动的最小距离
	 */
	private int touchSlop;
	/**
	 * 是否响应滑动
	 */
	private boolean isSliding;

	/**
	 * 手指按下的x坐标
	 */
	private int xDown;
	/**
	 * 手指按下的y坐标
	 */
	private int yDown;

	/**
	 * 手指移动时的x坐标
	 */
	private int xMove;
	/**
	 * 手指移动时的y坐标
	 */
	private int yMove;

	private LayoutInflater mInflater;

	private PopupWindow mPopupWindow;
	private int mPopupWindowHeight;
	private int mPopupWindowWidth;

	private TextView tv_del;
	/**
	 * 为删除按钮提供一个回调接口
	 */
	private DelButtonClickListener mListener;
	/**
	 * 当前周知触摸的view
	 */
	private View mCurrentView;
	/**
	 * 当前手指触摸的位置
	 */
	private int mCurrentViewPos;

	public QQListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public QQListView(Context context) {
		this(context, null);
	}

	public QQListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mInflater = LayoutInflater.from(context);
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		View view = mInflater.inflate(R.layout.delete_btn, null);
		tv_del = (TextView) view.findViewById(R.id.tv_del);
		mPopupWindow = new PopupWindow(view,//
				LinearLayout.LayoutParams.WRAP_CONTENT,//
				LinearLayout.LayoutParams.WRAP_CONTENT);
		/**
		 * 先调用下measure,否则拿不到宽和高
		 */
		mPopupWindow.getContentView().measure(0, 0);
		mPopupWindowHeight = mPopupWindow.getContentView().getMeasuredHeight();
		mPopupWindowWidth = mPopupWindow.getContentView().getMeasuredWidth();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			xDown = x;
			yDown = y;
			if (mPopupWindow.isShowing()) {
				dismissPopWindow();
				return false;
			}
			// 获取当期那手指按下的item的位置
			mCurrentViewPos = pointToPosition(xDown, yDown);
			// 获取当前手指按下时的item
			View view = getChildAt(mCurrentViewPos - getFirstVisiblePosition());
			mCurrentView = view;
			break;
		case MotionEvent.ACTION_MOVE:
			xMove = x;
			yMove = y;
			int dx = xMove - xDown;
			int dy = yMove - yDown;
			if (xMove < xDown && Math.abs(dx) > touchSlop
					&& Math.abs(dy) < touchSlop) {
				isSliding = true;
			}
			break;
		}

		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		/**
		 * 如果是从右到左的滑动才响应
		 */
		if(isSliding){
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				int[] location = new int[2];
				//获取当前item的位置x 与y
				mCurrentView.getLocationOnScreen(location);
				//设置popupWindow的动画
				mPopupWindow.setAnimationStyle(R.style.popupwindow_delete_btn_anim_style);
				mPopupWindow.update();
				mPopupWindow.showAtLocation(mCurrentView, Gravity.LEFT | Gravity.TOP,//
						location[0] + mCurrentView.getWidth(),//
						location[1] + mCurrentView.getHeight() / 2 - mPopupWindowHeight / 2);
				tv_del.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(mListener != null){
							mListener.clickHappend(mCurrentViewPos);
							mPopupWindow.dismiss();
						}
					}
				});
				
				break;
			case MotionEvent.ACTION_UP:
				isSliding = false;
			}
			//响应滑动期间itemClick事件，避免发生冲突
			return true;
		}
		
		return super.onTouchEvent(ev);
	}

	/**
	 * 隐藏popupWindow
	 */
	private void dismissPopWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}

	public void setDelButtonClickListener(DelButtonClickListener listener) {
		mListener = listener;
	}

	public interface DelButtonClickListener {
		void clickHappend(int position);
	}

}
