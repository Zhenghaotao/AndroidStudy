package com.example.arcmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class ArcMenu extends ViewGroup implements OnClickListener {

	// 菜单显示的位置
	private Position mPosition = Position.LEFT_TOP;

	// 菜单显示的半径,默认 100dp
	private int mRadius = 100;

	// 用户点击的按钮
	private View mButton;

	// 当前arcMenu的状态
	private Status mCurrentStatus = Status.CLOSE;

	// 回调接口
	private OnMenuItemClickListener onMenuItemClickListener;

	public ArcMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ArcMenu(Context context) {
		this(context, null);
	}

	public ArcMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.ArcMenu, defStyle, 0);
		mRadius = ta.getDimensionPixelSize(R.styleable.ArcMenu_radius,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
						100f, getResources().getDisplayMetrics()));
		int val = ta.getInt(R.styleable.ArcMenu_position, 0);
		switch (val) {
		case 0:
			mPosition = Position.LEFT_TOP;
			break;
		case 1:
			mPosition = Position.RIGHT_TOP;
			break;
		case 2:
			mPosition = Position.RIGHT_BOTTOM;
			break;
		case 3:
			mPosition = Position.LEFT_BOTTOM;
			break;
		}

		ta.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(MeasureSpec.UNSPECIFIED,
					MeasureSpec.UNSPECIFIED);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			layoutButton();
			int count = getChildCount();
			// 设置所有孩子的位置 例如(第一个为按钮)： 左上时，从左到右 1 第二个:
			for (int i = 0; i < count - 1; i++) {
				View child = getChildAt(i + 1);
				// child.setVisibility(View.GONE); //TODO
				int tempCl = (int) (mRadius * (Math.sin(Math.PI / 2
						/ (count - 2) * i)));
				int tempCt = (int) (mRadius * (Math.cos(Math.PI / 2
						/ (count - 2) * i)));
				int cl = 0;
				int ct = 0;
				int cWidth = child.getMeasuredWidth();
				int cHeight = child.getMeasuredHeight();
				// 左上 cl,ct 不变
				switch (mPosition) {
				case LEFT_TOP:
					cl = tempCl;
					ct = tempCt;
					break;
				case RIGHT_TOP:
					cl = getMeasuredWidth() - cWidth - tempCt;
					ct = tempCl;
					break;
				case RIGHT_BOTTOM:
					cl = getMeasuredWidth() - cWidth - tempCt;
					ct = getMeasuredHeight() - cHeight - tempCl;
					break;
				case LEFT_BOTTOM:
					cl = tempCl;
					ct = getMeasuredHeight() - cHeight - tempCt;
					break;
				}
				child.layout(cl, ct, cl + cWidth, ct + cHeight);
			}
		}
	}

	/**
	 * 第一个子元素为按钮，为按钮布局并且初始化事件
	 */
	private void layoutButton() {
		View cButton = getChildAt(0);
		cButton.setOnClickListener(this);

		int l = 0;
		int t = 0;
		int width = cButton.getMeasuredWidth();
		int height = cButton.getMeasuredHeight();
		switch (mPosition) {
		case LEFT_TOP:
			l = 0;
			t = 0;
			break;
		case RIGHT_TOP:
			l = getMeasuredWidth() - width;
			t = 0;
			break;
		case RIGHT_BOTTOM:
			l = getMeasuredWidth() - width;
			t = getMeasuredHeight() - height;
			break;
		case LEFT_BOTTOM:
			l = 0;
			t = getMeasuredHeight() - height;
			break;
		}
		cButton.layout(l, t, l + width, t + height);

	}

	public void setOnMenuItemClickListener(
			OnMenuItemClickListener onMenuItemClickListener) {
		this.onMenuItemClickListener = onMenuItemClickListener;
	}

	@Override
	public void onClick(View v) {

	}

	/**
	 * 状态的枚举类
	 */
	public enum Status {
		OPEN, CLOSE;
	}

	/**
	 * 设置位置菜单实现的位置，4选 1，默认右下
	 * 
	 */
	public enum Position {
		LEFT_TOP, RIGHT_TOP, RIGHT_BOTTOM, LEFT_BOTTOM;
	}

	public interface OnMenuItemClickListener {
		void onClick(View view, int pos);
	}

}
