package com.example.luckypan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class LuckyPan extends SurfaceView implements Callback, Runnable {
	private SurfaceHolder mHolder;
	private Canvas mCanvas;

	/**
	 * 用于绘制的线程
	 */
	private Thread t;

	/**
	 * 线程的控制开关
	 */
	private boolean isRunning;
	private String[] mStrs = new String[] { "单反相机", "IPAD", "恭喜发财", "IPHONE",
			"服装一套", "恭喜发财" };
	private int[] mImgs = new int[] { R.drawable.danfan, R.drawable.ipad,
			R.drawable.f040, R.drawable.iphone, R.drawable.meizi,
			R.drawable.f040 };
	private int[] mColors = new int[] { 0xFFFFC300, 0XFFF17E01, 0xFFFFC300,
			0xFFF17E01, 0xFFFFC300, 0xFFF17E01 };
	private int mItemCount = 6;

	/**
	 * 与图片对应的bitmap数组
	 */
	private Bitmap[] mImageBitmap;
	// 整个盘块的范围
	private RectF mRange = new RectF();

	private int mRadius;

	/**
	 * 绘制盘块的画壁
	 */
	private Paint mArcPaint;

	/**
	 * 绘制文本的画笔
	 */
	private Paint mTextPaint;

	// 滚动的速度
	private double mSpeed;

	private volatile float mStartAngle = 0;

	/**
	 * 判断是否点击了停止按钮
	 */
	private boolean isShouldEnd;

	/**
	 * 转盘的中心位置
	 */
	private int mCenter;

	/**
	 * 直接取paddingLeft为准
	 */
	private int mPadding;

	private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.bg2);

	private float mTextSize = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());

	public LuckyPan(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
		mPadding = getPaddingLeft();
		// 半径
		mRadius = width - mPadding * 2;
		// 中心点
		mCenter = width / 2;
		setMeasuredDimension(width, width);
	}

	public LuckyPan(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		// 可获得焦点
		setFocusable(true);
		setFocusableInTouchMode(true);
		// 设置常亮
		setKeepScreenOn(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 初始化绘制盘块的画笔
		mArcPaint = new Paint();
		mArcPaint.setAntiAlias(true);
		mArcPaint.setDither(true);

		// 初始化绘制文本的画笔
		mTextPaint = new Paint();
		mTextPaint.setColor(0xffffffff);
		mTextPaint.setTextSize(mTextSize);

		// 初始化盘块的绘制范围
		mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding
				+ mRadius);

		// 初始化图片
		mImageBitmap = new Bitmap[mItemCount];

		for (int i = 0; i < mItemCount; i++) {
			mImageBitmap[i] = BitmapFactory.decodeResource(getResources(),
					mImgs[i]);
		}

		isRunning = true;
		t = new Thread(this);
		t.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
	}

	@Override
	public void run() {

		// 不断进行绘制
		while (isRunning) {
			long start = System.currentTimeMillis();
			draw();

			long end = System.currentTimeMillis();
			if (end - start < 50) {
				try {
					Thread.sleep(50 - (end - start));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void draw() {
		try {
			mCanvas = mHolder.lockCanvas();
			if (mCanvas != null) {
				// 绘制背景
				drawBg();
				// 绘制盘块
				float tmpAngle = mStartAngle;

				float sweepAngle = 360 / mItemCount;
				for (int i = 0; i < mItemCount; i++) {
					mArcPaint.setColor(mColors[i]);
					mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true,
							mArcPaint);
					// 绘制文本
					drawText(tmpAngle, sweepAngle, mStrs[i]);
					// 绘制图片
					drawIcon(tmpAngle, mImageBitmap[i]);

					tmpAngle = tmpAngle + sweepAngle;
				}
				mStartAngle += mSpeed;
				// 如果点击了停止按钮
				if (isShouldEnd) {
					mSpeed -= 1;
					if (mSpeed < 0) {
						mSpeed = 0;
						isShouldEnd = false;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCanvas != null) {
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
	}

	/**
	 * 点击启动旋转
	 */
	public void luckyStart(int index) {
		// 计算每一项的角度
		int angle = 360 / mItemCount;

		// 中奖范围(当前 index )

		float from = 270 - (index +  1) * angle ;
		float end = from + angle;

		// 设置停下来需要旋转的距离
		float targetFrom = 4 * 360 + from;
		float targetEnd = 4 * 360 + end;

		float v1 = (float) ((-1 + Math.sqrt(1 + 8 * targetFrom)) / 2);

		float v2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);
		
		mSpeed = v1 + Math.random() * (v2 - v1);
		isShouldEnd = false;
	}

	public void luckyEnd() {
		mStartAngle = 0;
		isShouldEnd = true;
	}

	/**
	 * 转盘是否在旋转
	 */
	public boolean isStart() {
		return mSpeed != 0;
	}

	public boolean isShouldEnd() {
		
		return isShouldEnd;
	}

	/**
	 * 绘制icon
	 * 
	 * @param tmpAngle
	 * @param bitmap
	 */
	private void drawIcon(float tmpAngle, Bitmap bitmap) {

		// 设置图片的宽度,为直径的 1/ 8
		int imgWidth = mRadius / 8;

		float angle = (float) ((tmpAngle + 360 / mItemCount / 2) * Math.PI / 180);
		int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
		int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));

		// 确定图片位置
		Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth
				/ 2, y + imgWidth / 2);
		mCanvas.drawBitmap(bitmap, null, rect, null);
	}

	/**
	 * 绘制每个盘块的文本
	 * 
	 * @param tmpAngle
	 * @param sweepAngle
	 * @param string
	 */
	private void drawText(float tmpAngle, float sweepAngle, String text) {
		Path path = new Path();
		path.addArc(mRange, tmpAngle, sweepAngle);
		// 利用水平偏移量让文字居中
		int textWidth = (int) mTextPaint.measureText(text);
		int hOffset = (int) (mRadius * Math.PI / mItemCount / 2 - textWidth / 2);

		int vOffset = mRadius / 2 / 6;

		mCanvas.drawTextOnPath(text, path, hOffset, 30, mTextPaint);
	}

	private void drawBg() {
		mCanvas.drawColor(0xFFFFFFFF);
		mCanvas.drawBitmap(mBgBitmap, null, new RectF(mPadding / 2,
				mPadding / 2, getMeasuredWidth() - mPadding / 2,
				getMeasuredHeight() - mPadding / 2), null);
	}

}
