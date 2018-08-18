package tech.tablesaw.columns;

import org.junit.Assert;
import org.junit.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.DoubleColumnType;

import static org.junit.Assert.assertEquals;

public class AbstractColumnTest {

    @Test
    public void fillMissing_defaultValue() {
        NumberColumn col1 = NumberColumn.create("col1",
                new double[]{0.0, 1.0, DoubleColumnType.missingValueIndicator(), 2.0, DoubleColumnType.missingValueIndicator()});
        NumberColumn expected = NumberColumn.create("expected", new double[]{0.0, 1.0, 7.0, 2.0, 7.0});
        Assert.assertArrayEquals(expected.asDoubleArray(), col1.fillMissing(7.0).asDoubleArray(), 0.0001);
    }

    @Test
    public void fillMissing_columnArg() {
        NumberColumn col1 = NumberColumn.create("col1",
                new double[]{0.0, 1.0, DoubleColumnType.missingValueIndicator(), 2.0, DoubleColumnType.missingValueIndicator()});
        NumberColumn col2 = NumberColumn.create("col1", new double[]{7.0, 7.0, 3.0, 7.0, 4.0});
        NumberColumn expected = NumberColumn.create("expected", new double[]{0.0, 1.0, 3.0, 2.0, 4.0});
        Assert.assertArrayEquals(expected.asDoubleArray(), col1.fillMissing(col2).asDoubleArray(), 0.0001);
    }

    @Test
    public void doWithEach() throws Exception {

        Table table = Table.read().csv("../data/bush.csv").first(5);

        DateColumn dc1 = table.dateColumn("date");

        DateColumn dc2 = DateColumn.create("100 days later");

        dc1.forEach(localDate -> dc2.append(localDate.plusDays(100)));

        assertEquals(dc1.get(0).plusDays(100), dc2.get(0));
    }
}
