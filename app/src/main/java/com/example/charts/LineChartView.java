package com.example.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by pucheng.
 * Date: 2019/7/17 0017.
 */

public class LineChartView extends View {
    private static final String TAG = "LineChartView";
    //控件的实际宽度和高度
    private int mWidth;
    private int mHeight;
    //坐标轴线的长度取控件的0.75系数
    private int xAxisLength;
    private int yAxixLength;
    private Paint axisPaint;
    //原点坐标
    private int xOrigin;
    private int yOrigin;

    //坐标标签
    private Paint labelPaint;
    private float labelTextSize = 26;

    //折线
    private Paint linePaint;

    //空点圆点
    private Paint pointPaint;
    private int pointRadius = 5;

    //阴影区域
    private Paint areaPaint;
    private Path path;

    //纵轴虚线
    private Paint dashPaint;
    private static final float lineWidth = 20;//实线宽度
    private static final float dashWidth = 20;//虚线间距
    private DashPathEffect mDashPathEffect;
    public static final float[] INTERVALS = new float[]{lineWidth, dashWidth};

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LineChartView(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
        invalidate();
    }

    private void init() {
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        xAxisLength = (int) (mWidth * 0.75f);
        yAxixLength = (int) (mHeight * 0.75f);

        xOrigin = (mWidth - xAxisLength) / 2;
        yOrigin = (mHeight - yAxixLength) / 2 + yAxixLength;
        if (axisPaint == null) {
            axisPaint = new Paint();
        }
        axisPaint.setColor(Color.RED);

        if (labelPaint == null) {
            labelPaint = new Paint();
        }
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(labelTextSize);

        if (linePaint == null) {
            linePaint = new Paint();
        }
        linePaint.setColor(Color.GREEN);

        if (pointPaint == null) {
            pointPaint = new Paint();
        }
        pointPaint.setColor(Color.YELLOW);

        if (path == null) {
            path = new Path();
        }
        if (areaPaint == null) {
            areaPaint = new Paint();
        }

        if (dashPaint == null) {
            dashPaint = new Paint();
        }

        Log.e(TAG, "init: mWidth=" + mWidth + ", mHeight=" + mHeight);
        Log.e(TAG, "init: xAxisLength=" + xAxisLength + ", yAxixLength=" + yAxixLength);
        Log.e(TAG, "init: xOrigin=" + xOrigin + ", yOrigin=" + yOrigin);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景色
        canvas.drawColor(Color.WHITE);
        //绘制坐标系
        drawCoordinateSystem(canvas);
        //绘制标签
        drawLabel(canvas);
        //绘制水平虚线
        drawVerticalDash(canvas);
        //绘制折线
        drawLine(canvas);
        //绘制空心原点
        drawPoint(canvas);
        //绘制区域填充
//        drawArea(canvas);
    }

    private void drawVerticalDash(Canvas canvas) {
        //不使用硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, dashPaint);
        dashPaint.setColor(Color.LTGRAY);
        /**
         * intervals： 控制实线和实线之后空白线的宽度（数组长度必须为偶数）
            phase： 将View向”左“偏移phase
         */
        if (mDashPathEffect == null) {
            mDashPathEffect = new DashPathEffect(INTERVALS, 8);
        }
        dashPaint.setPathEffect(mDashPathEffect);
        canvas.drawLine(xOrigin, yOrigin - yAxixLength, xOrigin + xAxisLength, yOrigin - yAxixLength, dashPaint);
    }

    private void drawArea(Canvas canvas) {

    }

    private void drawPoint(Canvas canvas) {
        float yAverage = yAxixLength * 1.0f / 2000;
        float xAverage = xAxisLength * 1.0f / 8;
        pointPaint.setStrokeWidth(2);
        pointPaint.setStyle(Paint.Style.STROKE);//空心效果
        canvas.drawCircle(xOrigin + xAverage, yOrigin - yAverage * 1800, pointRadius, pointPaint);
        canvas.drawCircle(xOrigin + xAverage * 2, yOrigin - yAverage * 1350, pointRadius, pointPaint);
        canvas.drawCircle(xOrigin + xAverage * 3, yOrigin - yAverage * 700, pointRadius, pointPaint);
        canvas.drawCircle(xOrigin + xAverage * 4, yOrigin - yAverage * 1500, pointRadius, pointPaint);
        canvas.drawCircle(xOrigin + xAverage * 5, yOrigin - yAverage * 1000, pointRadius, pointPaint);
        canvas.drawCircle(xOrigin + xAverage * 6, yOrigin - yAverage * 2000, pointRadius, pointPaint);
        canvas.drawCircle(xOrigin + xAverage * 7, yOrigin - yAverage * 1600, pointRadius, pointPaint);
    }

    private void drawLine(Canvas canvas) {
        //模拟数据(1, 1800),(2, 1350),(3, 700), (4, 1500),(5, 1000),(6, 2000), (7, 1600)
        //坐标需要换算
        float yAverage = yAxixLength * 1.0f / 2000;
        float xAverage = xAxisLength * 1.0f / 8;
        canvas.drawLine(xOrigin, yOrigin, xOrigin + xAverage, yOrigin - yAverage * 1800, linePaint);
        canvas.drawLine(xOrigin + xAverage, yOrigin - yAverage * 1800, xOrigin + xAverage * 2, yOrigin - yAverage * 1350, linePaint);
        canvas.drawLine(xOrigin + xAverage * 2, yOrigin - yAverage * 1350, xOrigin + xAverage * 3, yOrigin - yAverage * 700, linePaint);
        canvas.drawLine(xOrigin + xAverage * 3, yOrigin - yAverage * 700, xOrigin + xAverage * 4, yOrigin - yAverage * 1500, linePaint);
        canvas.drawLine(xOrigin + xAverage * 4, yOrigin - yAverage * 1500, xOrigin + xAverage * 5, yOrigin - yAverage * 1000, linePaint);
        canvas.drawLine(xOrigin + xAverage * 5, yOrigin - yAverage * 1000, xOrigin + xAverage * 6, yOrigin - yAverage * 2000, linePaint);
        canvas.drawLine(xOrigin + xAverage * 6, yOrigin - yAverage * 2000, xOrigin + xAverage * 7, yOrigin - yAverage * 1600, linePaint);
    }

    private void drawLabel(Canvas canvas) {
        //绘制横轴标签
        String text = "01234567";
        int averageX = xAxisLength / 8;
        Rect bounds = new Rect();
        for (int i = 0; i < 8; i++) {
            String xStr = String.valueOf(text.charAt(i));
            labelPaint.getTextBounds(xStr, 0, 1, bounds);
            canvas.drawText(xStr, xOrigin - bounds.width() / 2 + i * averageX, yOrigin + bounds.width()+8, labelPaint);
//            canvas.drawText(String.valueOf(text.charAt(i)), xOrigin - labelTextSize / 2 + i * averageX, yOrigin + labelTextSize, labelPaint);
        }
        //绘制纵轴标签
        int[] yValue = {500, 1000, 1500, 2000};
        int averageY = yAxixLength / 4;
        for (int i = 0; i < 4; i++) {
            String yStr = String.valueOf(yValue[i]);
            labelPaint.getTextBounds(yStr, 0, yStr.length(), bounds);
            canvas.drawText(yStr, xOrigin - bounds.width(), yOrigin - averageY * (i + 1), labelPaint);
//            canvas.drawText(String.valueOf(yValue[i]), xOrigin - labelTextSize * 2.5f, yOrigin - averageY * (i + 1), labelPaint);
        }
    }

    private void drawCoordinateSystem(Canvas canvas) {
        canvas.drawLine(xOrigin, yOrigin, xOrigin + xAxisLength, yOrigin, axisPaint);
        canvas.drawLine(xOrigin, yOrigin, xOrigin, (mHeight - yAxixLength) / 2, axisPaint);
    }
}
