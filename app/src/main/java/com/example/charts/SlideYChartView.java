package com.example.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SlideYChartView extends View {

    private static String TAG = "SlideYChartView";

    private Context mContext;

    private ArrayList<SlideYLine> data = new ArrayList<>();

    private float hMargin = -1;// 横线间距
    private float vMargin = -1;// 竖线间距

    // 刻度取值范围
    private float minX = Float.MAX_VALUE;
    private float maxX = Float.MIN_VALUE;
    private float minY = Float.MAX_VALUE;
    private float maxY = Float.MIN_VALUE;

    // 原点坐标
    private float tableX;
    private float tableY;
    private float hLength;// 横线长度
    private float vLength;// 竖线长度
    private float coordTextSize;// 字体大小。。


    private int hLineCount = 5;// 横线数量
    private int vLineCount = 7;// 竖线数量

    private SlideYChartPoint movePoint;
    private float lastY;// 滑动 Y 距离
    private PopupWindow mPopWin;

    public SlideYChartView(Context context, AttributeSet attributeSet) {

        super(context, attributeSet);
        mContext = context;
        init();
    }

    private void init() {

        if (data.size() == 0) {
            SlideYLine line = new SlideYLine();
            line.setColor("#7fffd4");
            for (int i = -5; i < 6; i ++) {
                SlideYChartPoint point = new SlideYChartPoint(i, 0);
                line.addPoint(point);
            }
            data.add(line);

            SlideYLine line1 = new SlideYLine();
            line1.setColor("#0000ff");
            for (int i = -5; i < 12; i ++) {
                SlideYChartPoint point = new SlideYChartPoint(i * 2, i * 3);
                line1.addPoint(point);
            }
            data.add(line1);
            searchMinAndMax();
        }


        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        hLength = width * 0.75f;          // x轴长度
        vLength = height * 0.75f;         // y轴长度
        tableX = (width - hLength) / 2;       // 开始绘图的x坐标
        tableY = (height - vLength) / 2;       // 开始UI图的y坐标
        coordTextSize = (float) (width / 7.5 / 5);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:


                lastY = event.getRawY();

                int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
                float tempXScale = width / 7.5f;
                int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
                float tempYScale = height / 7.5f;

                ArrayList<SlideYChartPoint> tempPoints = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    SlideYLine line = data.get(i);
                    ArrayList<SlideYChartPoint> points = line.getPoints();
                    for (int j = 0; j < points.size(); j++) {
                        SlideYChartPoint point = coordinateConversion(points.get(j));
                        if (Math.abs(touchX - point.getX()) < tempXScale / 5 && Math.abs(touchY - point.getY()) < tempYScale / 5) {
                            tempPoints.add(points.get(j));
                        }
                    }
                }

                if (tempPoints.size() > 1) {
                    float tempMargin = 0;
                    for (int i = 0; i < tempPoints.size(); i++) {
                        SlideYChartPoint point = tempPoints.get(i);
                        if (tempMargin < Math.abs(touchX - point.getX()) + Math.abs(touchY - point.getY())) {
                            movePoint = point;
                        }
                    }
                } else if (tempPoints.size() == 1) {
                    movePoint = tempPoints.get(0);
                } else {
                    return false;
                }

                return true;
            case MotionEvent.ACTION_MOVE:

                if (movePoint == null) {
//                    Toast.makeText(mContext, "滑动时出错", Toast.LENGTH_SHORT).show();
                    return false;
                }

                float dy = event.getRawY() - lastY;
                movePoint.setY(movePoint.getY() - coordinateConversionY(dy));

                setData((ArrayList<SlideYLine>) data.clone());
                fresh();

                lastY = (int) event.getRawY();

                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent: up");
                showDetails();
                movePoint = null;
                break;
        }


        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        //绘制背景色
        canvas.drawColor(Color.WHITE);
        init();
        drawTable(canvas);
        drawLabel(canvas);
        drawLine(canvas);
        drawPoint(canvas);
    }

    // 画表格
    private void drawTable(Canvas canvas) {

        hMargin = vLength / (hLineCount - 1);

        Paint tablePaint = new Paint();
//        tablePaint.setColor(Color.BLACK);
        tablePaint.setStrokeWidth((float) 1.0);              // 设置线宽
        // 横线
        for (int i = 0; i < hLineCount; i++) {
            canvas.drawLine(tableX, tableY + i * hMargin, tableX + hLength, tableY + i * hMargin, tablePaint);        //绘制直线
//            tablePaint.setStrokeWidth((float) 5.0);              // 设置线宽
        }

        vMargin = hLength / (vLineCount - 1);
        for (int i = 0; i < vLineCount; i++) {
            canvas.drawLine(tableX + i * vMargin, tableY, tableX + i * vMargin, tableY + vLength, tablePaint);        //绘制直线
//            tablePaint.setStrokeWidth((float) 5.0);
        }
    }

    // 画标签
    private void drawLabel(Canvas canvas) {

        if (hMargin == -1 || vMargin == -1) {
            Log.d(TAG, "drawLabel: 出错了, 没有算出间距？");
            return;
        }

        Paint labelPaint = new Paint();
        labelPaint.setColor(Color.BLUE);
        labelPaint.setTextSize(coordTextSize);

        Rect bounds = new Rect();

        float xScale = (maxX - minX) / (vLineCount - 1);
        float yScale = (maxY - minY) / (hLineCount - 1);
        if (xScale == 0) {
            maxX = 5;
            minX = -5;
            xScale = (maxX - minX) / (vLineCount - 1);
        }
        if (yScale == 0) {
            maxY = 5;
            minY = -5;
            yScale = (maxY - minY) / (hLineCount - 1);
        }


        // 画左 y label
        for (int i = 0; i < hLineCount; i++) {
            String label = format2Bit(maxY - i * yScale);
            labelPaint.getTextBounds(label, 0, label.length(), bounds);
            canvas.drawText(label, tableX - bounds.width() - 8, tableY + bounds.height() / 2 + hMargin * i, labelPaint);
        }

        // 画右 y label
        for (int i = 0; i < hLineCount; i++) {
            String label = format2Bit(maxY - i * yScale);
            labelPaint.getTextBounds(label, 0, label.length(), bounds);
            canvas.drawText(label, tableX + hLength + 8, tableY + bounds.height() / 2 + hMargin * i, labelPaint);
        }

        // 画上 x label
        for (int i = 0; i < vLineCount; i++) {
            String label = format2Bit(minX + i * xScale);
            labelPaint.getTextBounds(label, 0, label.length(), bounds);
            canvas.drawText(label, tableX - bounds.width() / 2 + vMargin * i, tableY - 8, labelPaint);
        }

        // 画下 x label
        for (int i = 0; i < vLineCount; i++) {
            String label = format2Bit(minX + i * xScale);
            labelPaint.getTextBounds(label, 0, label.length(), bounds);
            canvas.drawText(label, tableX - bounds.width() / 2 + vMargin * i, tableY + vLength + bounds.height() + 8, labelPaint);
        }
    }


    // 画线
    private void drawLine(Canvas canvas) {

        Paint linePaint = new Paint();
        float width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        linePaint.setStrokeWidth(width / 7.5f / 25f);

        for (int i = 0; i < data.size(); i++) {

            linePaint.setColor(Color.parseColor(data.get(i).getColor()));// 设置颜色
            ArrayList<SlideYChartPoint> points = data.get(i).getPoints();

            for (int j = 0; j < points.size() - 1; j++) {
                SlideYChartPoint point = coordinateConversion(points.get(j));
                SlideYChartPoint nextPoint = coordinateConversion(points.get(j + 1));
                canvas.drawLine(point.getX(), point.getY(), nextPoint.getX(), nextPoint.getY(), linePaint);
            }
        }
    }

    // 画点
    private void drawPoint(Canvas canvas) {

        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        float tempScale = width / 7.5f;

        Paint pointPaint = new Paint();
        pointPaint.setStrokeCap(Paint.Cap.ROUND);

        for (int i = 0; i < data.size(); i++) {

            ArrayList<SlideYChartPoint> points = data.get(i).getPoints();
            for (int j = 0; j < points.size(); j++) {
                pointPaint.setColor(Color.parseColor(data.get(i).getColor()));// 设置颜色
                SlideYChartPoint point = coordinateConversion(points.get(j));
                canvas.drawCircle(point.getX(), point.getY(), tempScale / 15, pointPaint);
                pointPaint.setColor(Color.WHITE);
                canvas.drawCircle(point.getX(), point.getY(), tempScale / 30, pointPaint);
            }
        }
    }


    public void setData(ArrayList<SlideYLine> data) {
        this.data = data;

        searchMinAndMax();
    }


    /**
     * 点击数据点后，展示详细的数据值
     */
    private void showDetails() {
        if (mPopWin != null) mPopWin.dismiss();
        TextView tv = new TextView(getContext());
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundResource(R.drawable.shape_pop_bg);
        GradientDrawable myGrad = (GradientDrawable) tv.getBackground();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getPoints().contains(movePoint)) {
                myGrad.setColor(Color.parseColor(data.get(i).getColor()));
                break;
            }
        }

        tv.setPadding(20, 0, 20, 0);
        tv.setGravity(Gravity.CENTER);
        tv.setText("x:" + movePoint.getX() + ",y:" + format2Bit(movePoint.getY()));
        mPopWin = new PopupWindow(tv, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWin.setBackgroundDrawable(new ColorDrawable(0));
        mPopWin.setFocusable(false);
        // 根据坐标点的位置计算弹窗的展示位置
        SlideYChartPoint tempPoint = coordinateConversion(movePoint);
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        float tempXScale = width / 7.5f;
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        float tempYScale = height / 7.5f;
        int xoff = (int) (tempPoint.getX() - 0.5 * tempXScale);
        int yoff = -(int) (getHeight() - tempPoint.getY() + 0.75f * tempYScale);
        mPopWin.showAsDropDown(this, xoff, yoff);
        mPopWin.update();
    }


    private void searchMinAndMax() {

        for (int i = 0; i < data.size(); i++) {
            SlideYLine line = data.get(i);
            for (int j = 0; j < line.getPoints().size(); j++) {
                SlideYChartPoint point = line.getPoints().get(j);

                if (point.getX() > maxX) {
                    maxX = point.getX();
                } else if (point.getX() < minX) {
                    minX = point.getX();
                }

                if (point.getY() > maxY) {
                    maxY = point.getY();
                } else if (point.getY() < minY) {
                    minY = point.getY();
                }
            }
        }
    }

    public ArrayList<SlideYLine> getData() {
        return data;
    }

    // 把坐标值换成图表上的比例
    private SlideYChartPoint coordinateConversion(SlideYChartPoint point) {

        float x = 0;
        float y = 0;
        if (maxX - minX == 0) {
            x = hLength * 0.5f;
        } else {
            x = tableX + (point.getX() - minX) / (maxX - minX) * hLength;
        }
        if (maxY - minY == 0) {
            y = vLength * 0.5f;
        } else {
            y = tableY + vLength - (point.getY() - minY) / (maxY - minY) * vLength;
        }

        return new SlideYChartPoint(x, y);
    }

    // 把坐标从 y 轴的位置转换成实际的值
    private float coordinateConversionY(float y) {
        if (maxY - minY == 0) {

        }

        return (maxY - minY) / vLength * y;
    }

    // 保留两位小数
    private String format2Bit(float number) {

        DecimalFormat decimalFormat = new DecimalFormat("###.00");
        String target = decimalFormat.format(number);
        if (target.startsWith(".")) {
            target = "0" + target;
        }
        return target;
    }

    /**
     * 重新设置x轴刻度、数据、标题后必须刷新重绘
     */
    public void fresh() {
        init();
        requestLayout();
        postInvalidate();
    }
}
