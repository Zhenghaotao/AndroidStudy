package com.example.customviewstudy;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class CustomTitleView extends View {

	// 文本
	private String mTitleText;

	// 文本的颜色
	private int mTitleTextColor;

	// 文本的大小
	private int mTitleTextSize;

	// 绘制时控制文本绘制的范围
	private Rect mBound;
	private Paint mPaint;

	public CustomTitleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomTitleView(Context context) {
		this(context, null);
	}

	public CustomTitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 获取自定义的样式属性

		TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.CustomTitleView, defStyle, 0);
		mTitleText = ta.getString(R.styleable.CustomTitleView_titleText);
		mTitleTextColor = ta.getColor(
				R.styleable.CustomTitleView_titleTextColor, Color.BLACK);
		mTitleTextSize = ta.getDimensionPixelSize(
				R.styleable.CustomTitleView_titleTextSize,(int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
								getResources().getDisplayMetrics()));

		ta.recycle();
		/**
		 * 获取绘制文本的宽和高
		 */
		mPaint = new Paint();
		mPaint.setTextSize(mTitleTextSize);
//		mPaint.setColor(mTitleTextColor);
		mBound = new Rect();
		mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);
		
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTitleText = randomText();
				postInvalidate();
			}
		});
	}
	protected String randomText() {
		Random random = new Random();
		Set<Integer> set = new HashSet<Integer>();
		while(set.size() < 4){
			int randomInt = random.nextInt();
			set.add(randomInt);
		}
		StringBuffer sb = new StringBuffer();
		for(Integer i : set){
			sb.append("" + i);
		}
		return sb.toString();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width;
		int height;
		if(widthMode == MeasureSpec.EXACTLY){
			width = widthSize;
		} else {
			mPaint.setTextSize(mTitleTextSize);
			mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);
			float  textWidth = mBound.width();
			int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
			width = desired;
			
		}
		if(heightMode == MeasureSpec.EXACTLY){
			height = heightSize;
		} else {
			mPaint.setTextSize(mTitleTextSize);
			 mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);
			 float textHeight = mBound.height();
			 int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
			 height = desired;
		}
		
		setMeasuredDimension(width, height);  
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.setColor(Color.YELLOW);
		canvas.drawRect(0, 0,getMeasuredWidth(),getMeasuredHeight(),mPaint);
		mPaint.setColor(mTitleTextColor);
		canvas.drawText(mTitleText, getWidth() / 2 - mBound.width() / 2, getHeight()/ 2  + mBound.height() / 2, mPaint);
	}

}
