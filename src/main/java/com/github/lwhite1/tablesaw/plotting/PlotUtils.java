package com.github.lwhite1.tablesaw.plotting;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.plotting.plotly.XyPlot;
import com.github.lwhite1.tablesaw.plotting.plotly.XySeries;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class PlotUtils {

  public static void xyPlot(FloatColumn x, FloatColumn y) {

    String[] args = new String[2];
    PlotController.launch(args);
  }

  public static void xyPlot(DateColumn x, ShortColumn y) {

    StringTemplateEngine templateEngine = StringTemplateEngine.INSTANCE;

    Map<String, Object> attributeMap = new HashMap<>();

    XySeries<DateColumn, ShortColumn> series = new XySeries<>(x, y);

    XyPlot plot = new XyPlot.Builder().plotTitle("Hola!!").addSeries(series).build();

    String seriesString = series.asString(1);
    attributeMap.put("series", seriesString);

    String[] args = new String[2];
    String name = x.name() + " x " + y.name();
    String page = null;
    try {
      page = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir"), "src/main/web/plotly.html")));

      page = templateEngine.render(page, attributeMap);

    } catch (MalformedURLException e) {
      throw new RuntimeException("Couldn't find the html file template", e);
    } catch (IOException e) {
      e.printStackTrace();
    }
    args[0] = name;
    args[1] = page;
    PlotController.doIt(args);
  }
}
