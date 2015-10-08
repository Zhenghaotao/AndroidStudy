package com.example.canvastest.view;

import com.example.canvastest.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

public class MyView extends View {

	public MyView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 把整张画布绘制成白色
		canvas.drawColor(Color.WHITE);

		Paint paint = new Paint();
		// 抗锯齿
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(3);
		// 绘制圆形
		canvas.drawCircle(40, 40, 30, paint);
		// 绘制正方形
		canvas.drawRect(10, 80, 70, 140, paint);
		// 绘制矩形
		canvas.drawRect(10, 150, 70, 190, paint);
		// 绘制圆角矩形
		RectF re1 = new RectF(10, 200, 70, 230);
		canvas.drawRoundRect(re1, 10, 10, paint);
		// 绘制椭圆
		RectF re11 = new RectF(10, 240, 70, 270);
		canvas.drawOval(re11, paint);
		// 定义一个Path对象，封闭成一个三角形
		Path path1 = new Path();
		path1.moveTo(10, 340);
		path1.lineTo(70, 340);
		path1.lineTo(40, 290);
		path1.close();
		// 根据Path绘制成三角形
		canvas.drawPath(path1, paint);

		// 定义一个Path 对象，绘制三角形
		Path path2 = new Path();
		path2.moveTo(26, 360);
		path2.lineTo(54, 360);
		path2.lineTo(70, 392);
		path2.lineTo(42, 420);
		path2.lineTo(10, 392);
		path2.close();
		// 根据Path进行绘制五角行
		canvas.drawPath(path2, paint);

		// ------------------------设置填充风格后绘制---------------------------------
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.RED);
		// 绘制圆形
		canvas.drawCircle(120, 40, 30, paint);
		// 绘制正方形
		canvas.drawRect(90, 80, 150, 140, paint);
		// 绘制矩形
		canvas.drawRect(90, 150, 150, 190, paint);
		RectF re2 = new RectF(90, 200, 150, 230);
		// 绘制圆角矩形
		canvas.drawRoundRect(re2, 15, 15, paint);
		// 绘制椭圆形
		RectF re21 = new RectF(90, 240, 150, 270);
		canvas.drawOval(re21, paint);
		// 绘制三角形
		Path path3 = new Path();
		path3.moveTo(90, 340);
		path3.lineTo(150, 340);
		path3.lineTo(120, 290);
		path3.close();
		canvas.drawPath(path3, paint);
		// 绘制五角行
		Path path4 = new Path();
		path4.moveTo(106, 360);
		path4.lineTo(134, 360);
		path4.lineTo(150, 392);
		path4.lineTo(120, 420);
		path4.lineTo(90, 392);
		path4.close();
		canvas.drawPath(path4, paint);
		// ---------------------设置渐变器后绘制------------------------
		Shader mShader = new LinearGradient(0, 0, 40, 60, new int[] {
				Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW }, null,
				Shader.TileMode.REPEAT);
		paint.setShader(mShader);
		//设置阴影
		paint.setShadowLayer(45, 10, 10, Color.GRAY);
		//绘制圆形
		canvas.drawCircle(200, 40, 30, paint);
		//绘制正方形
		canvas.drawRect(170, 80, 230, 140, paint);
		//绘制矩形
		canvas.drawRect(170, 150, 230, 190, paint);
		//绘制圆角矩形
		RectF re3 = new RectF(170,200,230,230);
		canvas.drawRoundRect(re3, 15, 15, paint);
		//绘制椭圆
		RectF re31 = new RectF(170,240,230,270);
		canvas.drawOval(re31, paint);
		//绘制三角形
		Path path5 = new Path();
		path5.moveTo(170, 340);
		path5.lineTo(230, 340);
		path5.lineTo(200, 290);
		path5.close();
		canvas.drawPath(path5, paint);
		//绘制五角星
		Path path6 = new Path();
		path6.moveTo(180, 360);
		path6.lineTo(214, 360);
		path6.lineTo(230, 392);
		path6.lineTo(200, 420);
		path6.lineTo(170, 392);
		path6.close();
		canvas.drawPath(path6, paint);
		// --------------------设置字符大小后绘制----------------------
		//绘制七个字符串
		paint.setTextSize(24);
		paint.setShader(null);
		canvas.drawText(getResources().getString(R.string.circle), 240, 50, paint);
		canvas.drawText(getResources().getString(R.string.square), 240, 120, paint);
		canvas.drawText(getResources().getString(R.string.rect), 240, 175, paint);
		canvas.drawText(getResources().getString(R.string.round_rect), 240, 220, paint);
		canvas.drawText(getResources().getString(R.string.oval), 240, 260, paint);
		canvas.drawText(getResources().getString(R.string.triangle), 240, 325, paint);
		canvas.drawText(getResources().getString(R.string.pentagon), 240, 390, paint);
	}

}
