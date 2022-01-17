package com.example.openlog.ui;

import com.example.openlog.util.DateTimeFormatter;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.Date;
import java.util.List;

import kotlin.Pair;

public class LineGraph {

    List<Pair<Float, Date>> dataSet;
    org.eazegraph.lib.charts.ValueLineChart cubicValueLineChart;
    ValueLineSeries series = new ValueLineSeries();

    public LineGraph(List<Pair<Float, Date>> dataSet, ValueLineChart cubicValueLineChart) {
        this.dataSet = dataSet;
        this.cubicValueLineChart = cubicValueLineChart;
        setValues(dataSet);
    }

    public void setValues(List<Pair<Float, Date>> newDataSet) {
        cubicValueLineChart.clearChart();
        cubicValueLineChart.setShowIndicator(false);
        series.getSeries().clear();

        if (newDataSet == null || newDataSet.isEmpty()) {
            return;
        }

        dataSet = newDataSet;
        if (dataSet.size() > 1) {
            series.setColor(0xFF56B7F1);

            for (Pair<Float, Date> pair : dataSet) {
                series.addPoint(new ValueLinePoint(DateTimeFormatter.Companion.formatAsDate(pair.component2()), pair.component1()));
            }

            cubicValueLineChart.addSeries(series);
            cubicValueLineChart.setShowIndicator(true);
            cubicValueLineChart.startAnimation();
        }
    }
}
