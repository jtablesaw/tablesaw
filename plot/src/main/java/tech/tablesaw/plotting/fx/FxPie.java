package tech.tablesaw.plotting.fx;

import javafx.scene.chart.PieChart;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FxPie extends FxBuilder {

    public static PieChart chart(String title, Table table, String categoryColumnName, String numericColumnName) {

        CategoryColumn categoryColumn = table.categoryColumn(categoryColumnName);
        NumericColumn numericColumn = table.nCol(numericColumnName);

        return chart(title, categoryColumn, numericColumn);
    }

    public static PieChart chart(String title, CategoryColumn categoryColumn, NumericColumn numericColumn) {

        List<PieChart.Data> data = new ArrayList<>(categoryColumn.size());

        for (int i = 0; i < categoryColumn.size(); i++) {
            data.add(new PieChart.Data(categoryColumn.getString(i), numericColumn.getFloat(i)));
        }

        return createChart(title, data);
    }

    public static PieChart chart(String title, ShortColumn categoryColumn, NumericColumn numericColumn) {

        List<PieChart.Data> data = new ArrayList<>(categoryColumn.size());

        for (int i = 0; i < categoryColumn.size(); i++) {
            String name = Short.toString(categoryColumn.get(i));
            data.add(new PieChart.Data(name, numericColumn.getFloat(i)));
        }

        return createChart(title, data);
    }

    public static PieChart chart(String title, IntColumn categoryColumn, NumericColumn numericColumn) {

        List<PieChart.Data> data = new ArrayList<>(categoryColumn.size());

        for (int i = 0; i < categoryColumn.size(); i++) {
            String name = Integer.toString(categoryColumn.get(i));
            data.add(new PieChart.Data(name, numericColumn.getFloat(i)));
        }

        return createChart(title, data);
    }

    /**
     * Independent of the type of data
     *
     */
    private static PieChart createChart(String title, List<PieChart.Data> data) {
        final PieChart pieChart = getPieChart(title);

        pieChart.getData().setAll(data);
        return pieChart;
    }
}
