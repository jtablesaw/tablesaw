package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.plotting.glimpse.Display;
import com.metsci.glimpse.axis.Axis1D;
import com.metsci.glimpse.axis.listener.AxisListener1D;
import com.metsci.glimpse.canvas.GlimpseCanvas;
import com.metsci.glimpse.layout.GlimpseLayoutProvider;
import com.metsci.glimpse.painter.track.TrackPainter;
import com.metsci.glimpse.plot.timeline.StackedTimePlot2D;
import com.metsci.glimpse.plot.timeline.data.Epoch;
import com.metsci.glimpse.plot.timeline.layout.TimePlotInfo;
import com.metsci.glimpse.support.color.GlimpseColor;
import com.metsci.glimpse.util.units.time.TimeStamp;

import javax.swing.*;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

/**
 * Demonstrates use of StackedTimePlot2D to create a horizontal timeline axis
 * with lineplots stacked vertically on top, each with an independent y axis.<p>
 */
public class TimeSeries implements GlimpseLayoutProvider {

  private String plotTitle;
  private String xAxisName;
  private String yAxisName;
  private DateColumn x;
  private NumericColumn[] seriesYvalues;


  public static void show(String plotTitle, DateColumn x, NumericColumn y) {
    Display display;
    try {
       display = Display.showWithSwing(new TimeSeries(plotTitle, x, y));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    attachRepaintListener(display);
  }

  public TimeSeries(String plotTitle, DateColumn x, NumericColumn ... seriesYvalues) throws IOException {
    this.plotTitle = plotTitle;
    this.x = x;
    this.xAxisName = x.name();
    this.yAxisName = seriesYvalues[0].name();
    this.seriesYvalues = seriesYvalues;
  }

  @Override
  public StackedTimePlot2D getLayout() {

    // create a timeline with plot areas arranged in a vertical line
    StackedTimePlot2D plot = createPlot();

    // shut off display of the timezone
    plot.getDefaultTimeline().getTimeZonePainter().setVisible(false);

    // calculate some TimeStamps representing the selected time range and initial extents of the timeline
    Epoch epoch = plot.getEpoch();

    TimeStamp axisMaxTime = TimeStamp.fromDate(Date.from(x.max().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    TimeStamp axisMinTime = TimeStamp.fromDate(Date.from(x.min().atStartOfDay(ZoneId.systemDefault()).toInstant()));

    // set the overall bounds of the timeline
    plot.setTimeAxisBounds(axisMinTime, axisMaxTime);

    // create a plot (which by default will appear to the right of the timeline)
    // the returned ChartLayoutInfo reference can be used to add GlimpsePainters to
    // the plot area or customize its coloring and appearance
    TimePlotInfo plot1 = plot.createTimePlot("time series 1");

    // give the plot custom text labels indicating value being plotted and units
    plot1.setLabelText(yAxisName);

    // turn on timeline labels
    plot.setLabelSize(30);
    plot.setShowLabels(true);

    // display vertical labels
    plot1.getLabelPainter().setHorizontalLabels(false);

    plot1.getGridPainter().setShowHorizontalLines(false);

    setChartData(plot1, epoch);

    return plot;
  }

  private void setChartData(TimePlotInfo chart, Epoch epoch) {
    // create a painter to display data on the plot
    TrackPainter painter = new TrackPainter( );

    // set colors and sizes for the painter
    painter.setPointColor(1, GlimpseColor.getRed());
    painter.setLineColor(1, GlimpseColor.getRed());
    painter.setPointSize(1, 5.0f);
    painter.setShowLines(1, true);

    // generate some random data
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;

    for (int i = 0; i < x.size(); i++) {
      float valueX = seriesYvalues[0].getFloat(i);
      min = Math.min(min, valueX);
      max = Math.max(max, valueX);
      Date time =  Date.from(x.get(i).atStartOfDay(ZoneId.systemDefault()).toInstant());
      addData(painter, epoch, valueX, TimeStamp.fromDate(time));
    }

    // add the painter to the layout
    chart.addPainter( painter );

    // adjust the axis bounds to fit the data
    setBounds(chart, min, max);
  }

  private static StackedTimePlot2D createPlot( ) {
    // set the epoch and orientation for the timeline
    // time values will be stored relative to the epoch
    return new StackedTimePlot2D(new Epoch(TimeStamp.currentTime()));
  }

  private static void addData(TrackPainter painter, Epoch epoch, double data, TimeStamp time) {
    painter.addPoint(1, 0, epoch.fromTimeStamp(time), data, time.toPosixMillis());
  }

  private static void setBounds(TimePlotInfo chart, double lowerBound, double upperBound) {
    Axis1D axis = chart.getBaseLayout().getAxis().getAxisY();
    axis.setMin(lowerBound);
    axis.setMax(upperBound);
  }
  public static void attachRepaintListener(Display example)
  {
    final GlimpseCanvas canvas = example.getCanvas( );
    StackedTimePlot2D layout = (StackedTimePlot2D) example.getLayout( );

    // add an AxisListener1D which repaints the GlimpseCanvas
    // whenever one of the plot axes is modified

    AxisListener1D repaint = new AxisListener1D( )
    {
      @Override
      public void axisUpdated( Axis1D axis )
      {
        SwingUtilities.invokeLater(new Runnable( )
        {

          @Override
          public void run( )
          {
            canvas.getGLDrawable( ).display( );
          }

        } );
      }
    };

    layout.getTimeAxis().addAxisListener( repaint );
    layout.getCommonAxis( ).addAxisListener( repaint );
  }

}