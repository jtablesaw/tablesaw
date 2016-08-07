package com.github.lwhite1.tablesaw.plotting.xchart;

import com.github.lwhite1.tablesaw.plotting.StandardColors;
import org.knowm.xchart.style.GGPlot2Theme;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Theme;
import org.knowm.xchart.style.markers.Marker;

import java.awt.*;

/**
 *
 */
public class TablesawTheme implements Theme {

  GGPlot2Theme ggPlot2Theme = new GGPlot2Theme();

  public TablesawTheme() {

  }

  @Override
  public Color getChartBackgroundColor() {
    return Color.WHITE;
  }

  @Override
  public Color getChartFontColor() {
    return ggPlot2Theme.getChartFontColor();
  }

  @Override
  public int getChartPadding() {
    return ggPlot2Theme.getChartPadding();
  }

  @Override
  public Font getChartTitleFont() {
    return ggPlot2Theme.getChartTitleFont();
  }

  @Override
  public boolean isChartTitleVisible() {
    return true;
  }

  @Override
  public boolean isChartTitleBoxVisible() {
    return false;
  }

  @Override
  public Color getChartTitleBoxBackgroundColor() {
    return ggPlot2Theme.getChartTitleBoxBackgroundColor();
  }

  @Override
  public Color getChartTitleBoxBorderColor() {
    return ggPlot2Theme.getChartTitleBoxBorderColor();
  }

  @Override
  public int getChartTitlePadding() {
    return ggPlot2Theme.getAxisTitlePadding();
  }

  @Override
  public Font getLegendFont() {
    return ggPlot2Theme.getLegendFont();
  }

  @Override
  public boolean isLegendVisible() {
    return false;
  }

  @Override
  public Color getLegendBackgroundColor() {
    return ggPlot2Theme.getLegendBackgroundColor();
  }

  @Override
  public Color getLegendBorderColor() {
    return ggPlot2Theme.getLegendBorderColor();
  }

  @Override
  public int getLegendPadding() {
    return ggPlot2Theme.getLegendPadding();
  }

  @Override
  public int getLegendSeriesLineLength() {
    return ggPlot2Theme.getLegendSeriesLineLength();
  }

  @Override
  public Styler.LegendPosition getLegendPosition() {
    return ggPlot2Theme.getLegendPosition();
  }

  @Override
  public boolean isXAxisTitleVisible() {
    return true;
  }

  @Override
  public boolean isYAxisTitleVisible() {
    return true;
  }

  @Override
  public Font getAxisTitleFont() {
    return ggPlot2Theme.getAxisTitleFont();
  }

  @Override
  public boolean isXAxisTicksVisible() {
    return true;
  }

  @Override
  public boolean isYAxisTicksVisible() {
    return true;
  }

  @Override
  public Font getAxisTickLabelsFont() {
    return ggPlot2Theme.getAxisTickLabelsFont();
  }

  @Override
  public int getAxisTickMarkLength() {
    return ggPlot2Theme.getAxisTickMarkLength();
  }

  @Override
  public int getAxisTickPadding() {
    return ggPlot2Theme.getAxisTickPadding();
  }

  @Override
  public Color getAxisTickMarksColor() {
    return Color.LIGHT_GRAY;
  }

  @Override
  public Stroke getAxisTickMarksStroke() {
    return ggPlot2Theme.getAxisTickMarksStroke();
  }

  @Override
  public Color getAxisTickLabelsColor() {
    return ggPlot2Theme.getAxisTickLabelsColor();
  }

  @Override
  public boolean isAxisTicksLineVisible() {
    return true;
  }

  @Override
  public boolean isAxisTicksMarksVisible() {
    return ggPlot2Theme.isAxisTicksMarksVisible();
  }

  @Override
  public int getAxisTitlePadding() {
    return ggPlot2Theme.getAxisTitlePadding();
  }

  @Override
  public int getXAxisTickMarkSpacingHint() {
    return ggPlot2Theme.getXAxisTickMarkSpacingHint();
  }

  @Override
  public int getYAxisTickMarkSpacingHint() {
    return ggPlot2Theme.getYAxisTickMarkSpacingHint();
  }

  @Override
  public boolean isPlotGridLinesVisible() {
    return true;
  }

  @Override
  public boolean isPlotGridVerticalLinesVisible() {
    return true;
  }

  @Override
  public boolean isPlotGridHorizontalLinesVisible() {
    return true;
  }

  @Override
  public Color getPlotBackgroundColor() {
    return Color.WHITE;
  }

  @Override
  public Color getPlotBorderColor() {
    return ggPlot2Theme.getPlotBorderColor();
  }

  @Override
  public boolean isPlotBorderVisible() {
    return ggPlot2Theme.isPlotBorderVisible();
  }

  @Override
  public Color getPlotGridLinesColor() {
    return Color.decode("#e9e8e7");
  }

  @Override
  public Stroke getPlotGridLinesStroke() {
    return ggPlot2Theme.getPlotGridLinesStroke();
  }

  @Override
  public boolean isPlotTicksMarksVisible() {
    return ggPlot2Theme.isPlotTicksMarksVisible();
  }

  @Override
  public double getPlotContentSize() {
    return ggPlot2Theme.getPlotContentSize();
  }

  @Override
  public int getPlotMargin() {
    return ggPlot2Theme.getPlotMargin();
  }

  @Override
  public double getAvailableSpaceFill() {
    return ggPlot2Theme.getAvailableSpaceFill();
  }

  @Override
  public boolean isOverlapped() {
    return ggPlot2Theme.isOverlapped();
  }

  @Override
  public boolean isCircular() {
    return ggPlot2Theme.isCircular();
  }

  @Override
  public double getStartAngleInDegrees() {
    return ggPlot2Theme.getStartAngleInDegrees();
  }

  @Override
  public Font getPieFont() {
    return ggPlot2Theme.getPieFont();
  }

  @Override
  public double getAnnotationDistance() {
    return ggPlot2Theme.getAnnotationDistance();
  }

  @Override
  public PieStyler.AnnotationType getAnnotationType() {
    return ggPlot2Theme.getAnnotationType();
  }

  @Override
  public boolean isDrawAllAnnotations() {
    return ggPlot2Theme.isDrawAllAnnotations();
  }

  @Override
  public double getDonutThickness() {
    return ggPlot2Theme.getDonutThickness();
  }

  @Override
  public int getMarkerSize() {
    return ggPlot2Theme.getMarkerSize();
  }

  @Override
  public boolean showMarkers() {
    return ggPlot2Theme.showMarkers();
  }

  @Override
  public Color getErrorBarsColor() {
    return ggPlot2Theme.getErrorBarsColor();
  }

  @Override
  public boolean isErrorBarsColorSeriesColor() {
    return ggPlot2Theme.isErrorBarsColorSeriesColor();
  }

  @Override
  public Font getAnnotationFont() {
    return ggPlot2Theme.getAnnotationFont();
  }

  @Override
  public Color[] getSeriesColors() {
    return StandardColors.standardColorArray();
  }

  @Override
  public BasicStroke[] getSeriesLines() {
    return ggPlot2Theme.getSeriesLines();
  }

  @Override
  public Marker[] getSeriesMarkers() {
    return ggPlot2Theme.getSeriesMarkers();
  }
}
