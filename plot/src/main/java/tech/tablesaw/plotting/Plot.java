package tech.tablesaw.plotting;

import java.util.List;

/**
 *
 */
public interface Plot {

    String title();

    String xTitle();

    List<Series> seriesList();

}
