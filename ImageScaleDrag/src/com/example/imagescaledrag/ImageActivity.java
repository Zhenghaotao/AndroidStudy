package com.example.imagescaledrag;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ImageActivity extends Activity implements OnTouchListener {
	private static final String TAG = "ImageActivity";
	public static final int RESULT_CODE_NOFOUND = 200;

	private Matrix matrix = new Matrix();
	private Matrix saveMatrix = new Matrix();
	private DisplayMetrics dm;
	private Bitmap bitmap;

	// 最小缩放比例
	private float minScale = 1.0f;
	// 最大缩放比例
	private static final float MAX_SCALE = 10f;

	// 初始状态

	// 拖动
	private static final int NONE = 0;
	// 拖动
	private static final int DRAG = 1;
	// 缩放
	private static final int ZOOM = 2;
	// 当前模式
	private int mode = NONE;
	private PointF prev = new PointF();
	private PointF mid = new PointF();
	private float dist = 1f;

	private ImageView iv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取图片资源
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tu);
		setContentView(R.layout.activity_image);
		iv = (ImageView) findViewById(R.id.iv);
		iv.setImageBitmap(bitmap);// 获取控件
		iv.setOnTouchListener(this);// 设置触屏监听
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);// 获取分辨率
		center();
		iv.setImageMatrix(matrix);

	}
	
	/**
	 * 限制最大最小缩放比例,自动居中
	 */
	private void checkView(){
		float p[] = new float[9];
		matrix.getValues(p);
		if (mode == ZOOM) {  
			if (p[0] < minScale) {  
				Log.d("", "当前缩放级别:"+p[0]+",最小缩放级别:"+minScale);
				matrix.setScale(minScale, minScale);  
			}
			if(p[0] > MAX_SCALE){
				Log.d("", "当前缩放级别:"+p[0]+",最大缩放级别:"+MAX_SCALE);
				matrix.set(saveMatrix);  
			}
			center();
		}
		
	}

	/**
	 * 最小缩放比例，最大为100%
	 */
	private void minZoom() {
		minScale = Math.min(//
				(float) dm.widthPixels / bitmap.getWidth(),//
				(float) dm.heightPixels / (float) bitmap.getHeight());
		if(minScale < 1.0){
			matrix.postScale(minScale, minScale);
		}
	}

	/**
	 * 两点的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getY(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 两点的中点
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private void center() {
		center(true, true);
	}

	/**
	 * 纵向，横向居中
	 */
	protected void center(boolean horizontal, boolean vertical) {
		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		m.mapRect(rect);
		float height = rect.height();
		float width = rect.width();
		float deltaX = 0;
		float deltaY = 0;
		if (vertical) {
			// 图片小于屏幕大小，则居中显示,大于屏幕，上方留空则往上移，下方留空则往下移
			int screenHeight = dm.heightPixels;
			if (height < screenHeight) {
				deltaY = (screenHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = iv.getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int screenWidth = dm.widthPixels;
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right > screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		matrix.postTranslate(deltaX, deltaY);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 主点按下
		case MotionEvent.ACTION_DOWN:
			saveMatrix.set(matrix);
			prev.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		// 副点按下
		case MotionEvent.ACTION_POINTER_DOWN:
			dist = spacing(event);
			// 如果连续两点距离大于10, 则判定为多点模式
			if (spacing(event) > 10f) {
				saveMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if(mode == DRAG){
				matrix.set(saveMatrix);
				matrix.postTranslate(event.getX() -prev.x,event.getY() - prev.y );
			} else if(mode == ZOOM){
				float newDist = spacing(event);
				if(newDist == spacing(event)){
					matrix.set(saveMatrix);
					float tScale = newDist / dist;
					matrix.postScale(tScale, tScale,mid.x,mid.y);
				}
			}
			break;
		}
		iv.setImageMatrix(matrix);
		checkView();
		return true;
	}

}
