package pathstudy.taotao.com.pathtest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

/**
 */
public class PathTextTest extends View {
    private static final String DRAW_STR = "泡代码的涛";
    private Path[] paths = new Path[3];
    private Paint paint;
    public PathTextTest(Context context) {
        super(context);
        paths[0] = new Path();
        paths[0].moveTo(0,0);
        for(int i = 1; i <= 7; i++) {
            paths[0].lineTo(i * 30,(float)Math.random() * 30);

        }
        paths[1] = new Path();
        RectF rectF = new RectF(0, 0, 200, 120);
        paths[1].addOval(rectF, Path.Direction.CCW);
        paths[2] = new Path();
        paths[2].addArc(rectF, 60, 180);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.CYAN);
        paint.setStrokeWidth(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        //设置从右边开始绘制(右对齐)
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(20);
        //绘制路径
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(paths[0], paint);
        //　沿着路径绘制一段文本
        paint.setStyle(Paint.Style.FILL);
        canvas.drawTextOnPath(DRAW_STR, paths[0], -8, 20, paint);
        // 对Canvas 进行坐标变换：画布下移120
        canvas.translate(0, 60);
        // 绘制路径
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(paths[1], paint);
        //沿着路径绘制一段文本
        paint.setStyle(Paint.Style.FILL);
        canvas.drawTextOnPath(DRAW_STR, paths[1], -20, 20, paint);

        // 对　Canvas 进行坐标变换: 画布下移１２０
        canvas.translate(0, 120);
        //绘制路径
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(paths[2], paint);

        //沿着路径绘制一段文本
        paint.setStyle(Paint.Style.FILL);
        canvas.drawTextOnPath(DRAW_STR, paths[2],-10,20,paint);
    }
}
