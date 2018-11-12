package tablesaw.ui.examples;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

@SuppressWarnings("all")
public class Test1 implements Runnable {
  public void run() {
    final String var1 = "Aud";
    final int var2 = 1;
    StringColumn nameColumn = StringColumn.create("name");
    DoubleColumn ageColumn = DoubleColumn.create("age");
    nameColumn.append("Hallvard");
    ageColumn.append((50 + var2));
    nameColumn.append(var1);
    ageColumn.append(80);
    final Table tab1 = Table.create("A table", nameColumn, ageColumn);
    Column<?> _column = tab1.column("name");
    ShortColumn ageColumn_1 = ShortColumn.create("age");
    final Table tab2 = Table.create("tab2", ((StringColumn) _column), ageColumn_1);
    Column<?> _column_1 = tab2.column("age");
    ((ShortColumn) _column_1).append(((short) 42));
    Column<?> _column_2 = tab2.column("age");
    ((ShortColumn) _column_2).append(((short) 48));
    this.helper(tab1);
    this.helper(tab2);
  }
  
  public static void main(final String[] args) {
    new Test1().run();
  }
  
  private void helper(final Table table) {
    System.out.println(table);
  }
}
