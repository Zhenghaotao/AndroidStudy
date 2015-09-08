package com.example.customviewgroupcontainer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CustomViewGroupContainer extends ViewGroup {

	public CustomViewGroupContainer(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomViewGroupContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomViewGroupContainer(Context context) {
		super(context);
	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/**
		 * 获得此viewGroup的上级容器为其推荐的宽度和高，以及计算模式
		 */
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heighSize = MeasureSpec.getSize(heightMeasureSpec);

		// 计算出所有的childView的宽和高
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		MarginLayoutParams cParams = null;

		/**
		 * 记录如果时wrap_content是设置的宽度和高
		 */
		int width = 0;
		int height = 0;

		int count = getChildCount();
		// 用于计算左边两个childView的高度
		int lHeight = 0;
		// 用于计算右边两个childView的高度
		int rHeight = 0;
		// 用于计算上边两个childView的宽度
		int tWidth = 0;
		// 用于计算下边两个childView的宽度
		int bWidth = 0;

		/**
		 * 根据childView计算的宽和高，以及设置的margin就算出容器的宽和高，主要是用于容器是wrap_content时
		 */
		for (int i = 0; i < count; i++) {
			View childView = getChildAt(i);
			int cWidth = childView.getMeasuredWidth();
			int cHeight = childView.getMeasuredHeight();
			cParams = (MarginLayoutParams) childView.getLayoutParams();
			// 上面两个childView的宽度
			if (i == 0 || i == 1) {
				tWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
			}
			// 右边两个childView的高度
			if (i == 1 || i == 3) {
				rHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
			}
			// 左边两个childView的高度
			if (i == 0 || i == 2) {
				lHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
			}
			// 下边两个childView的宽度
			if (i == 2 || i == 3) {
				bWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
			}

			width = Math.max(tWidth, bWidth);
			height = Math.max(lHeight, rHeight);
			/**
			 * 如果时wrap_content 设置为我们计算的值 否则，直接设置为父容器计算的值
			 */
			setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize
					: width, heightMode == MeasureSpec.EXACTLY ? heighSize
					: height);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();
		int cWidth = 0;
		int cHeight = 0;
		MarginLayoutParams cParams = null;
		for (int i = 0; i < count; i++) {
			View childView = getChildAt(i);
			cWidth = childView.getMeasuredWidth();
			cHeight = childView.getMeasuredHeight();
			cParams = (MarginLayoutParams) childView.getLayoutParams();
			int cl = 0;
			int ct = 0;
			int cr = 0;
			int cb = 0;

			switch (i) {
			case 0:
				cl = cParams.leftMargin;
				ct = cParams.topMargin;
				break;
			case 1:
				cl = getWidth() - cWidth  - cParams.rightMargin;//有问题，要是子view的margin各不相同呢
				ct = cParams.topMargin;
				break;
			case 2:
				cl = cParams.leftMargin;
				ct = getHeight() - cHeight - cParams.bottomMargin;
				
				break;
			case 3:
				cl = getWidth() - cWidth  - cParams.rightMargin;//有问题，要是子view的margin各不相同呢
				ct = getHeight() - cHeight - cParams.bottomMargin;

				break;
			}
			cr = cl + cWidth;
			cb = ct + cHeight;
			childView.layout(cl, ct, cr, cb);
		}
	}

}
