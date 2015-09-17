package com.example.flowlayout;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {

	// 存储所有的view,按行记录
	private List<List<View>> mAllViews = new ArrayList<List<View>>();
	// 记录每一行的最大高度
	private List<Integer> mLineHeight = new ArrayList<Integer>();

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public FlowLayout(Context context) {
		super(context);
	}
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获的它父类的宽高及测量模式
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		// 如果是wrap_content情况下，记录宽和高
		int width = 0;
		int height = 0;
		int lineWidth = 0;
		int lineHeight = 0;
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View childView = getChildAt(i);
			// 测量每个子View的宽高
			measureChild(childView, widthMeasureSpec, heightMeasureSpec);
			// 得到child的lp
			MarginLayoutParams lp = (MarginLayoutParams) childView
					.getLayoutParams();
			// 当前子view的宽度
			int childWidth = childView.getMeasuredWidth() + lp.leftMargin
					+ lp.rightMargin;
			int childHeight = childView.getHeight() + lp.topMargin
					+ lp.bottomMargin;
			/**
			 * 如果加入后lineWidth的大于最大宽度值即widthSize
			 */
			if (lineWidth + childWidth > widthSize) {
				width = Math.max(lineWidth, childWidth);
				lineWidth = childWidth;// 重新开启新行，开始记录
				// 叠加高度
				height += lineHeight;
				// 开始记录下一行的高度
				lineHeight = childHeight;
			} else {
				lineWidth += childWidth;
				lineHeight = Math.max(lineHeight, childHeight);
			}
			if (i == count - 1) {
				width = Math.max(width, lineWidth);
				height += lineHeight;
			}
		}
		setMeasuredDimension((widthMode == MeasureSpec.EXACTLY ? widthSize
				: width), (heightMode == MeasureSpec.EXACTLY ? heightSize
				: height));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mAllViews.clear();
		mLineHeight.clear();
		int width = getWidth();
		int lineWidth = 0;
		int lineHeight = 0;
		// 存储每一行所有的childView
		List<View> lineViews = new ArrayList<View>();
		int count = getChildCount();
		// 遍历所有的子view
		for (int i = 0; i < count; i++) {
			View childView = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) childView
					.getLayoutParams();
			int childWidth = childView.getMeasuredWidth();
			int childHeight = childView.getMeasuredHeight();
			// 如果已经需要换行
			if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {
				// 记录下一行所有的view以及最大高度
				mLineHeight.add(lineHeight);
				// 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
				mAllViews.add(lineViews);
				lineWidth = 0;// 重置行宽
				lineViews = new ArrayList<View>();
			}
			// 不需要换行，累加
			lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
			lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
					+ lp.bottomMargin);
			lineViews.add(childView);
		}
		//记录最后一行
		mLineHeight.add(lineHeight);
		mAllViews.add(lineViews);
		
		int left = 0;
		int top = 0;
		//得到总行数
		int lineNums = mAllViews.size();
		for(int i = 0; i < lineNums; i++){
			lineViews = mAllViews.get(i);
			//当前行的最大高度
			lineHeight = mLineHeight.get(i);
			for(int j = 0;j < lineViews.size();j++){
				View child = lineViews.get(j);
				if(child.getVisibility() == View.GONE){
					continue;
				}
				MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
				//计算childView的left,top,right,bottom
				int lc = left + lp.leftMargin;
				int tc = top + lp.topMargin;
				int rc = lc + child.getMeasuredWidth();
				int bc = tc + child.getMeasuredHeight();
				child.layout(lc, tc, rc, bc);
				left += child.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;
			}
			left = 0;
			top += lineHeight;
		}
	}
}
