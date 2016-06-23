package com.github.lwhite1.tablesaw.plotting;

import com.github.lwhite1.tablesaw.columns.FloatColumn;
import com.github.lwhite1.tablesaw.columns.IntColumn;

import java.io.File;
import java.net.MalformedURLException;

/**
 *
 */
public class PlotUtils {

  public static void plot(FloatColumn x, FloatColumn y) {

    String[] args = new String[2];
    PlotController.launch(args);
  }

  public static void plot(IntColumn x, IntColumn y) {


    StringTemplateEngine templateEngine = StringTemplateEngine.INSTANCE;


    String[] args = new String[2];
    String name = x.name() + " x " + y.name();
    String page = null;
    try {
      page = new File(System.getProperty("user.dir") + "/src/main/web/dimple.html")
          .toURI().toURL().toExternalForm();
    } catch (MalformedURLException e) {
      throw new RuntimeException("Couldn't find the html file template", e);
    }
    args[0] = name;
    args[1] = page;
    PlotController.doIt(args);
  }
}
