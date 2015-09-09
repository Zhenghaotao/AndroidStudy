package com.example.arcmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
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
		if (changed)
		{

			layoutButton();
			int count = getChildCount();
			/**
			 * 设置所有孩子的位置 例如(第一个为按钮)： 左上时，从左到右 ] 第2个：mRadius(sin0 , cos0)
			 * 第3个：mRadius(sina ,cosa) 注：[a = Math.PI / 2 * (cCount - 1)]
			 * 第4个：mRadius(sin2a ,cos2a) 第5个：mRadius(sin3a , cos3a) ...
			 */
			for (int i = 0; i < count - 1; i++)
			{
				View child = getChildAt(i + 1);
				child.setVisibility(View.GONE);

				int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2)
						* i));
				int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2)
						* i));
				// childview width
				int cWidth = child.getMeasuredWidth();
				// childview height
				int cHeight = child.getMeasuredHeight();

				// 下面两个childView的顶部位置
				if (mPosition == Position.LEFT_BOTTOM
						|| mPosition == Position.RIGHT_BOTTOM)
				{
					ct = getMeasuredHeight() - cHeight - ct;
				}
				// 右边两个的左侧位置
				if (mPosition == Position.RIGHT_TOP
						|| mPosition == Position.RIGHT_BOTTOM)
				{
					cl = getMeasuredWidth() - cWidth - cl;
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
		mButton = findViewById(R.id.btn);
		if (mButton == null) {
			mButton = getChildAt(0);
		}
		rotateView(mButton, 0f, 270f, 300);
		toggleMenu(300);
	}

	private void toggleMenu(int durationMillis) {
		int count = getChildCount();
		for (int i = 0; i < count - 1; i++) {
			final View childView = getChildAt(i + 1);
			childView.setVisibility(View.VISIBLE);

			int xflag = 1;
			int yflag = 1;

			if (mPosition == Position.LEFT_TOP
					|| mPosition == Position.LEFT_BOTTOM) {
				xflag = -1;
			}
			if (mPosition == Position.LEFT_TOP
					|| mPosition == Position.RIGHT_TOP) {
				yflag = -1;
			}
			// child left
			int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
			// child top
			int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
			
			AnimationSet animSet = new AnimationSet(true);
			Animation animation = null;
			if(mCurrentStatus == Status.CLOSE){
				animSet.setInterpolator(new OvershootInterpolator(2F));
				animation = new TranslateAnimation(xflag * cl, 0, yflag * ct, 0);
				childView.setClickable(true);
				childView.setFocusable(true);
			} else {
				animation = new TranslateAnimation(0f, xflag * cl, 0f, yflag
						* ct);
				childView.setClickable(false);
				childView.setFocusable(false);
			}
			animation.setAnimationListener(new AnimationListener()
			{
				public void onAnimationStart(Animation animation)
				{
				}

				public void onAnimationRepeat(Animation animation)
				{
				}

				public void onAnimationEnd(Animation animation)
				{
					if (mCurrentStatus == Status.CLOSE)
						childView.setVisibility(View.GONE);

				}
			});
			animation.setFillAfter(true);
			animation.setDuration(durationMillis);
			// 为动画设置一个开始延迟时间，纯属好看，可以不设
			animation.setStartOffset((i * 100) / (count - 1));
			RotateAnimation rotate = new RotateAnimation(0, 720,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotate.setDuration(durationMillis);
			rotate.setFillAfter(true);
			animSet.addAnimation(rotate);
			animSet.addAnimation(animation);
			childView.startAnimation(animSet);
			final int index = i + 1;
			childView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (onMenuItemClickListener != null)
						onMenuItemClickListener.onClick(childView, index - 1);
					menuItemAnin(index - 1);
					changeStatus();
					
				}
			});
		}
		changeStatus();

	}

	private void changeStatus() {
		mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN
				: Status.CLOSE);
	}
	
	/**
	 * 开始菜单动画，点击的MenuItem放大消失，其他的缩小消失
	 * @param item
	 */
	private void menuItemAnin(int item)
	{
		for (int i = 0; i < getChildCount() - 1; i++)
		{
			View childView = getChildAt(i + 1);
			if (i == item)
			{
				childView.startAnimation(scaleBigAnim(300));
			} else
			{
				childView.startAnimation(scaleSmallAnim(300));
			}
			childView.setClickable(false);
			childView.setFocusable(false);
		}
	}
	/**
	 * 缩小消失
	 * @param durationMillis
	 * @return
	 */
	private Animation scaleSmallAnim(int durationMillis)
	{
		Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(durationMillis);
		anim.setFillAfter(true);
		return anim;
	}
	/**
	 * 放大，透明度降低
	 * @param durationMillis
	 * @return
	 */
	private Animation scaleBigAnim(int durationMillis)
	{
		AnimationSet animationset = new AnimationSet(true);

		Animation anim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		Animation alphaAnimation = new AlphaAnimation(1, 0);
		animationset.addAnimation(anim);
		animationset.addAnimation(alphaAnimation);
		animationset.setDuration(durationMillis);
		animationset.setFillAfter(true);
		return animationset;
	}

	private void rotateView(View view, float fromDegrees, float toDegrees,
			int durationMillis) {
		RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotate.setDuration(durationMillis);
		rotate.setFillAfter(true);
		view.startAnimation(rotate);
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
	/////
}
