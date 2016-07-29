package com.github.lwhite1.tablesaw.plotting;

import java.util.List;

/**
 *
 */
public interface Plot {

  String title();
  String xTitle();
  List<Series> seriesList();

}
