package com.example.customvolumncontrolbar;

import com.example.customvolumncontrolbar.R.styleable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class CustomVolumControlBar extends View {
	// 第一圈的颜色
	private int mFirstColor;
	// 第二圈的颜色
	private int mSecondColor;
	// 圈的宽度
	private int mCircleWidth;
	// 画笔
	private Paint mPaint;
	// 当前进度
	private int mCurrentCount = 3;
	// 中间图片
	private Bitmap mImage;
	// 每快快间的间隙
	private int mSpliteSize;
	// 个数
	private int mCount;

	private Rect mRect;

	public CustomVolumControlBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomVolumControlBar(Context context) {
		this(context, null);
	}

	public CustomVolumControlBar(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// 必要的初始化，获得一些自定义的值
		TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
				styleable.CustomVolumControlBar, defStyle, 0);
		mFirstColor = ta.getColor(R.styleable.CustomVolumControlBar_firstColor,
				Color.GREEN);
		mSecondColor = ta.getColor(
				R.styleable.CustomVolumControlBar_secondColor, Color.CYAN);
		mImage = BitmapFactory.decodeResource(getResources(),
				ta.getResourceId(R.styleable.CustomVolumControlBar_bg, 0));
		mCircleWidth = ta.getDimensionPixelSize(
				R.styleable.CustomVolumControlBar_circleWidth, (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20,
								getResources().getDisplayMetrics()));
		mCount = ta.getInt(R.styleable.CustomVolumControlBar_dotCount, 20);// 默认20
		mSpliteSize = ta.getInt(R.styleable.CustomVolumControlBar_spliteSize,
				20);
		ta.recycle();
		mPaint = new Paint();
		mRect = new Rect();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mPaint.setAntiAlias(true);// 清除锯齿
		mPaint.setStrokeWidth(mCircleWidth);// 设置圆环的宽度
		mPaint.setStrokeCap(Paint.Cap.ROUND);// 设置线段电形状为圆头
		mPaint.setAntiAlias(true);// 清除锯齿
		mPaint.setStyle(Paint.Style.STROKE);// 设置空心
		int center = getWidth() / 2;
		int radius = center - mCircleWidth / 2;// 半径
		// 画块块去
		drawOval(canvas, center, radius);

		// 计算内切正方形的位置
		int relRadius = radius - mCircleWidth / 2;// 获取内圆的半径

		// 内切正方形的顶部
		mRect.left = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius)
				+ mCircleWidth;
		// 内切正方形的距离顶部

		// 内切正方形的距离左边
		mRect.top = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius)
				+ mCircleWidth;
		mRect.bottom = (int) (mRect.left + Math.sqrt(2) * relRadius);
		mRect.right = (int) (mRect.left + Math.sqrt(2) * relRadius);

		// 如果图片比较小，那么根据图片的尺寸放置到正中心
		if (mImage.getWidth() < Math.sqrt(2) * relRadius) {
			mRect.left = (int) (mRect.left + Math.sqrt(2) * relRadius * 1.0f
					/ 2 - mImage.getWidth() * 1.0f / 2);
			mRect.top = (int) (mRect.top + Math.sqrt(2) * relRadius * 1.0f / 2 - mImage
					.getHeight() * 1.0f / 2);
			mRect.right = (int) (mRect.left + mImage.getWidth());
			mRect.bottom = (int) (mRect.top + mImage.getHeight());
		}
		//绘图
		canvas.drawBitmap(mImage, null, mRect, mPaint);  

	}

	/**
	 * 根据参数画出每个小块
	 * 
	 * @param canvas
	 * @param center 外部圆的半径
	 * @param radius 内部圆的半径
	 */
	private void drawOval(Canvas canvas, int center, int radius) {
		// 根据需要画的个数以及间隙计算每个块块所占的比例 * 360
		float itemSize = (360 * 1.0f - mCount * mSpliteSize) / mCount;//每块所占的角度

		// 用于定义的圆弧的形状和大小的界限 *!!!
		RectF oval = new RectF(center - radius, center - radius, center
				+ radius, center + radius);

		mPaint.setColor(mFirstColor);// 设置圆环的颜色
		for (int i = 0; i < mCount; i++) {
			// 根据进度画圆弧
			canvas.drawArc(oval, i * (itemSize + mSpliteSize), itemSize, false,
					mPaint);
		}
		mPaint.setColor(mSecondColor);// 设置圆环的颜色
		// 根据进度画圆弧
		for (int i = 0; i < mCurrentCount; i++) {
			canvas.drawArc(oval, i * (itemSize + mSpliteSize), itemSize, false,
					mPaint);
		}
	}
	
	private int xDown,xUp;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDown = (int) event.getY();
			break;
		case MotionEvent.ACTION_UP:
			xUp = (int) event.getY();
			if(xUp > xDown){//下滑
				down();
			} else {
				up();
			}
			break;
		}
		return true;
	}

	private void up() {
		mCurrentCount++;
		postInvalidate();
	}

	private void down() {
		mCurrentCount--;
		postInvalidate();
	}

}
