package com.github.lwhite1.tablesaw.plotting.plotly;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Plotly XY Plot and it's varieties, including scatter and line plots, timeseries, etc.
 */
public class XyPlot {

  List<XySeries> seriesList = new ArrayList<>();
  Layout layout;

  private XyPlot(Builder builder) {
    Layout layout = new Layout();
    layout.height = builder.plotHeight;
    layout.width = builder.plotWidth;
    layout.margin = builder.margin;
    layout.title = builder.plotTitle;
    seriesList = builder.seriesList;
  }

  public List<XySeries> seriesList() {
    return seriesList;
  }

  public Layout layout() {
    return layout;
  }

  public static class Builder {

    String plotTitle;
    int plotHeight = 450;
    int plotWidth = 700;
    Margin margin;
    Axis xAxis;
    Axis yAxis;
    List<XySeries> seriesList = new ArrayList<>();

    public Builder() {
    }

    public Builder addSeries(XySeries series) {
      this.seriesList.add(series);
      return this;
    }

    public Builder plotTitle(String plotTitle) {
      this.plotTitle = plotTitle;
      return this;
    }

    public Builder plotWidth(int plotWidth) {
      Preconditions.checkArgument(plotWidth >= 10);
      this.plotWidth = plotWidth;
      return this;
    }

    public Builder plotHeight(int plotHeight) {
      Preconditions.checkArgument(plotHeight >= 10);
      this.plotHeight = plotHeight;
      return this;
    }

    public XyPlot build() {
      return new XyPlot(this);
    }
  }
}