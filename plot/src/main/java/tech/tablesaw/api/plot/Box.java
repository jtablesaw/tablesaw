package tech.tablesaw.api.plot;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotting.smile.SmileBox;
import tech.tablesaw.table.ViewGroup;

/**
 *
 */
public class Box {

    public static void show(String title, ViewGroup groups, int columnIndex) {
        SmileBox.show(title, groups, columnIndex);
    }

    public static void show(String title, Table table, String summaryColumnName, String groupingColumnName) {
        SmileBox.show(title, table, summaryColumnName, groupingColumnName);
    }
}
