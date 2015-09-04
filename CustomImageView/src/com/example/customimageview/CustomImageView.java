package com.example.customimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class CustomImageView extends View {
	// 控件的宽
	private int mWidth;
	// 控件的高
	private int mHeight;
	// 控件中的图片
	private Bitmap mImage;
	/**
	 * 图片缩放模式
	 */
	private int mImageScale;
	private static final int IMAGE_SCALE_FITXY = 0;
	private static final int IMAGE_SCALE_CENTER = 1;
	// 图片介绍
	private String mTitle;
	// 字体的颜色
	private int mTextColor;
	// 字体的大小
	private int mTextSize;

	private Paint mPaint;

	// 对文本的约束
	private Rect mTextBound;
	
	//控制整体布局
	private Rect rect;

	public CustomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomImageView(Context context) {
		this(context, null);
	}

	public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.CustomImageView, defStyle, 0);

		mTitle = ta.getString(R.styleable.CustomImageView_titleText);
		mTextColor = ta.getColor(R.styleable.CustomImageView_titleTextColor,
				Color.BLACK);
		mImageScale = ta.getInt(R.styleable.CustomImageView_imageScaleType, 0);
		//相当与 ImageView 的 src 属性
		mImage = BitmapFactory.decodeResource(getResources(),
				ta.getResourceId(R.styleable.CustomImageView_image, 0));
		
		mTextSize = ta.getDimensionPixelSize(
				R.styleable.CustomImageView_titleTextSize, (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
								getResources().getDisplayMetrics()));
		ta.recycle();
		ta.recycle();
		rect = new Rect();
		mPaint = new Paint();
		mTextBound = new Rect();
		//计算了描绘字体需要的范围
		mPaint.getTextBounds(mTitle, 0, mTitle.length(), mTextBound);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/**
		 * 设置宽度
		 */
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		if(specMode == MeasureSpec.EXACTLY){//match_parent, accurate
			mWidth = specSize;
		}  else {
			//由图片决定的宽
			int desireByImg = getPaddingLeft() + getPaddingRight() + mImage.getWidth();
			//由字体决定的宽
			int desireByTitle = getPaddingLeft() + getPaddingRight() +  mTextBound.width();
			if(specMode == MeasureSpec.AT_MOST){
				int desire = Math.max(desireByImg, desireByTitle);
				mWidth = Math.min(desire, specSize);
			}
		}
		
		/**
		 * 设置高度
		 */
		specMode = MeasureSpec.getMode(heightMeasureSpec);
		specSize = MeasureSpec.getSize(heightMeasureSpec);
		if(specMode == MeasureSpec.EXACTLY){//match_parent, accurate
			mHeight = specSize;
		} else{
			int desire = getPaddingTop() + getPaddingBottom() + mImage.getHeight() + mTextBound.height();
			if(specMode == MeasureSpec.AT_MOST){
				mHeight = Math.min(desire, specSize);
			}
		}
		setMeasuredDimension(mWidth, mHeight);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		/**
		 * 边框
		 */
		mPaint.setStrokeWidth(4);;
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.CYAN);
		canvas.drawRect(0, 0,getMeasuredWidth(),getMeasuredHeight(),mPaint);
		
		rect.left = getPaddingLeft();
		rect.right = mWidth - getPaddingRight();
		rect.top = getPaddingTop();
		rect.bottom = mHeight - getPaddingBottom();
		
		mPaint.setColor(mTextColor);
		mPaint.setStyle(Style.FILL);
		
		/**
		 * 当前设置的宽度小雨字体需要的宽度,将字体改为xxx...
		 */
		if(mTextBound.width() > mWidth){
			TextPaint paint = new TextPaint(mPaint);
			String msg = TextUtils.ellipsize(mTitle, paint, (float)mWidth - getPaddingLeft()- getPaddingRight(),TextUtils.TruncateAt.END).toString();
			canvas.drawText(msg, getPaddingLeft(), mHeight - getPaddingRight(), mPaint);
		} else {
			//正常情况，将字体居中
			canvas.drawText(mTitle, mWidth / 2- mTextBound.width() * 1.0f / 2, mHeight - getPaddingBottom() , mPaint);
		}
		//取消使用掉的块
		rect.bottom -= mTextBound.height();
		if(mImageScale == IMAGE_SCALE_FITXY){
			canvas.drawBitmap(mImage, null, rect,mPaint);
		} else {
			//计算居中的矩形范围
			rect.left = mWidth / 2 - mImage.getWidth() / 2;
			rect.right = mWidth / 2 + mImage.getWidth() /2;
			rect.top = (mHeight - mTextBound.height())/ 2 - mImage.getHeight() / 2;
			rect.bottom = (mHeight - mTextBound.height())/ 2 + mImage.getHeight() / 2;
			canvas.drawBitmap(mImage, null, rect,mPaint);
			
		}
		
		
		
		
		
	}
	
	
	

}
