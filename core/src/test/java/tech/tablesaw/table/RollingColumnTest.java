package tech.tablesaw.table;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.tablesaw.aggregate.AggregateFunctions.countTrue;
import static tech.tablesaw.aggregate.AggregateFunctions.latestDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;

public class RollingColumnTest {

  @Test
  public void testRollingMaxDate() {
    LocalDateTime[] data =
        new LocalDateTime[] {
          LocalDate.of(2011, 1, 1).atStartOfDay(),
          LocalDate.of(2011, 1, 3).atStartOfDay(),
          LocalDate.of(2011, 1, 5).atStartOfDay(),
          LocalDate.of(2011, 1, 7).atStartOfDay(),
          LocalDate.of(2011, 1, 9).atStartOfDay()
        };

    LocalDateTime[] sma5 =
        new LocalDateTime[] {
          null,
          LocalDate.of(2011, 1, 3).atStartOfDay(),
          LocalDate.of(2011, 1, 5).atStartOfDay(),
          LocalDate.of(2011, 1, 7).atStartOfDay(),
          LocalDate.of(2011, 1, 9).atStartOfDay()
        };

    DateTimeColumn result =
        (DateTimeColumn) DateTimeColumn.create("data", data).rolling(2).calc(latestDateTime);
    assertArrayEquals(sma5, result.asObjectArray());
  }

  @Test
  public void testRollingCountTrue() {
    Boolean[] data = new Boolean[] {true, false, false, true, true};

    BooleanColumn booleanColumn = BooleanColumn.create("data", data);
    DoubleColumn result = (DoubleColumn) booleanColumn.rolling(2).calc(countTrue);

    assertEquals(Double.NaN, result.getDouble(0), 0.0);
    assertEquals(1, result.getDouble(1), 0.0);
    assertEquals(0, result.getDouble(2), 0.0);
    assertEquals(1, result.getDouble(3), 0.0);
    assertEquals(2, result.getDouble(4), 0.0);
  }

  @Test
  public void testOversizedWindowPreservesDateTimeRowCount() {
    DateTimeColumn data =
        DateTimeColumn.create(
            "dates",
            LocalDate.of(2020, 1, 1).atStartOfDay(),
            LocalDate.of(2020, 1, 2).atStartOfDay());

    DateTimeColumn result = (DateTimeColumn) data.rolling(Integer.MAX_VALUE).calc(latestDateTime);

    assertEquals(data.size(), result.size());
    assertArrayEquals(new LocalDateTime[] {null, null}, result.asObjectArray());
  }

  @Test
  public void testEmptyDateTimeColumnRemainsEmpty() {
    DateTimeColumn result =
        (DateTimeColumn) DateTimeColumn.create("dates").rolling(10).calc(latestDateTime);

    assertEquals(0, result.size());
  }

  @Test
  public void testOversizedBooleanWindowPreservesRowCount() {
    BooleanColumn data = BooleanColumn.create("flags", true, false);

    DoubleColumn result = (DoubleColumn) data.rolling(3).calc(countTrue);

    assertEquals(data.size(), result.size());
    assertArrayEquals(new double[] {Double.NaN, Double.NaN}, result.asDoubleArray());
  }
}
