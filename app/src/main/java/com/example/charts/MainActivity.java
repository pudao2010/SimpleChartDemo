package com.example.charts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SlideYChartView mChartView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChartView1 = (SlideYChartView) findViewById(R.id.chart_view);
    }

    private void setData() {

        ArrayList<SlideYLine> data = new ArrayList();

        SlideYLine line1 = new SlideYLine();
        SlideYLine line2 = new SlideYLine();
        SlideYLine line3 = new SlideYLine();

        line1.setColor("#0000ff");
        line2.setColor("#5f9ea0");
        line3.setColor("#ff8c00");


        for (int i = 0; i < 10; i++) {
            line1.addPoint(new SlideYChartPoint(i, i * 10));
        }

        for (int i = 10; i < 20; i++) {
            line2.addPoint(new SlideYChartPoint(i, i * 10));
        }

        for (int i = 20; i < 30; i++) {
            line3.addPoint(new SlideYChartPoint(i, i * 10));
        }


        data.add(line1);
        data.add(line2);
        data.add(line3);
        mChartView1.setData(data);
        mChartView1.fresh();
    }

    public void load(View view) {
        Toast.makeText(this, "正在加载数据...", Toast.LENGTH_SHORT).show();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "数据加载成功", Toast.LENGTH_SHORT).show();
//                setData();
            }
        }, 100);
    }
}
