package tech.tablesaw.table;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableTransposeTest {

    @Test
    void transpose()
    {
        Table testTable = defineSchema();
        Table result = testTable.transpose();

        assertEquals("                Data                 \n" +
                " label   |  row1  |  row2  |  row3  |\n" +
                "-------------------------------------\n" +
                " value1  |     1  |   1.1  |   1.2  |\n" +
                " value2  |     2  |   2.1  |   2.2  |", result.print());
        assertEquals(testTable.print(), result.transpose().print());
    }

    private static Table defineSchema() {
        Table t = Table.create("Data");
        StringColumn label = StringColumn.create("label");
        DoubleColumn value = DoubleColumn.create("value1");
        DoubleColumn value2 = DoubleColumn.create("value2");

        t.addColumns(label);
        t.addColumns(value);
        t.addColumns(value2);

        label.append("row1").append("row2").append("row3");
        value.append(1.0).append(1.1).append(1.2);
        value2.append(2.0).append(2.1).append(2.2);
        return t;
    }
}
