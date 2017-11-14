package com.beakerx;

import tech.tablesaw.api.Table;
import com.twosigma.beakerx.jvm.object.OutputCell;
import com.twosigma.beakerx.table.TableDisplay;
import jupyter.Displayer;
import jupyter.Displayers;

import java.util.Map;

public class TablesawDisplayer {

  public static void register() {
    Displayers.register(Table.class, new Displayer<Table>() {
      @Override
      public Map<String, String> display(Table table) {
        new TableDisplay(
                table.rowCount(),
                table.columnCount(),
                table.columnNames(),
                new TableDisplay.Element() {
                  @Override
                  public String get(int columnIndex, int rowIndex) {
                    return table.get(rowIndex,columnIndex);
                  }
                }
        ).display();
        return OutputCell.DISPLAYER_HIDDEN;
      }
    });

  }
}
