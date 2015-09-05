package com.example.customcirclewaiting;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class CustomProgressBar extends View {
	// 第一圈的颜色
	private int mFirstColor;
	// 第二圈的颜色
	private int mSecondColor;
	// 圈的宽度
	private int mCircleWidth;
	// 画笔
	private Paint mPaint;
	// 当前进度
	private int mProgress;
	// 速度
	private int mSpeed;

	// 是否应该开始下一个
	private boolean isNext = false;

	public CustomProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomProgressBar(Context context) {
		this(context, null);
	}

	/**
	 * 必要的初始化，获获得一些自定义的值
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.CustomProgressBar, defStyle, 0);
		mFirstColor = ta.getColor(R.styleable.CustomProgressBar_firstColor,
				Color.GREEN);
		mSecondColor = ta.getColor(R.styleable.CustomProgressBar_secondColor,
				Color.RED);
		mCircleWidth = ta.getDimensionPixelSize(
				R.styleable.CustomProgressBar_circleWidth, (int)TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20,
								getResources().getDisplayMetrics()));
		mSpeed = ta.getInt(R.styleable.CustomProgressBar_speed, 20);//默认
		ta.recycle();
		mPaint = new Paint();
		
		//绘制线程
		new Thread(){
			public void run() {
				while(true){
					mProgress++;
					if(mProgress == 360){
						mProgress = 0;
						if(!isNext){
							isNext = true;
						} else {
							isNext = false;
						}
					}
					postInvalidate();
					try {
						Thread.sleep(mSpeed);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			};
		}.start();
	}
	@Override
	protected void onDraw(Canvas canvas) {
		int center = getWidth() / 2; //获取圆心的x坐标
		int radius = center - mCircleWidth / 2;//半径
		mPaint.setStrokeWidth(mCircleWidth);//设置圆环的宽度
		mPaint.setAntiAlias(true);//消除锯齿
		mPaint.setStyle(Paint.Style.STROKE);//设置空心
		RectF oval = new RectF(center - radius, center -radius, center + radius, center + radius);
		if(!isNext){
			//第一颜色的圈完整，第二颜色跑
			mPaint.setColor(mFirstColor);//设置圆环的颜色
			canvas.drawCircle(center, center, radius, mPaint);//滑出圆环
			mPaint.setColor(mSecondColor);//设置圆环的颜色
			canvas.drawArc(oval, -90, mProgress, false, mPaint);
		} else {
			mPaint.setColor(mSecondColor);//设置圆环的颜色
			canvas.drawCircle(center, center, radius, mPaint);//画出圆环
			mPaint.setColor(mFirstColor);//设置圆环的颜色
			canvas.drawArc(oval, -90, mProgress, false, mPaint);
		}
		
	}

}
