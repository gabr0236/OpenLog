package com.example.openlog.ui;

import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.List;

public class LineGraph {

    List<Integer> dataSet;
    org.eazegraph.lib.charts.ValueLineChart cubicValueLineChart;
    ValueLineSeries series = new ValueLineSeries();

    public LineGraph(List<Integer> dataSet, org.eazegraph.lib.charts.ValueLineChart cubicValueLineChart) {
        this.dataSet = dataSet;
        this.cubicValueLineChart = cubicValueLineChart;
        setValues(dataSet);
    }

    public void setValues(List<Integer> newDataSet) {
        if (newDataSet==null) return;

        if (dataSet!=null) series.getSeries().clear();

        dataSet = newDataSet;
        if (dataSet.size() > 1) {
            series.setColor(0xFF56B7F1);

            for (Integer value : dataSet) {
                series.addPoint(new ValueLinePoint("Dato", value.floatValue()));
            }

            cubicValueLineChart.addSeries(series);
            cubicValueLineChart.startAnimation();
        }
    }
}
