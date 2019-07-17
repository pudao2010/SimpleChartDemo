package com.example.charts;

import java.util.ArrayList;


public class SlideYLine {

    private ArrayList<SlideYChartPoint> points = new ArrayList<>();
    private String color;

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void addPoint(SlideYChartPoint point) {
        points.add(point);
    }

    public ArrayList<SlideYChartPoint> getPoints() {
        return points;
    }
}
