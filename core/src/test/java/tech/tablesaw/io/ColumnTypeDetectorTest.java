package tech.tablesaw.io;

import static org.junit.jupiter.api.Assertions.*;
import static tech.tablesaw.api.ColumnType.BOOLEAN;
import static tech.tablesaw.api.ColumnType.DOUBLE;
import static tech.tablesaw.api.ColumnType.FLOAT;
import static tech.tablesaw.api.ColumnType.INTEGER;
import static tech.tablesaw.api.ColumnType.LOCAL_DATE;
import static tech.tablesaw.api.ColumnType.LOCAL_DATE_TIME;
import static tech.tablesaw.api.ColumnType.LOCAL_TIME;
import static tech.tablesaw.api.ColumnType.LONG;
import static tech.tablesaw.api.ColumnType.SHORT;
import static tech.tablesaw.api.ColumnType.STRING;

import com.google.common.collect.Lists;
import java.io.StringReader;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.dates.DateColumnType;
import tech.tablesaw.columns.strings.StringColumnType;

class ColumnTypeDetectorTest {

  private static final String LINE_END = System.lineSeparator();

  @Test
  void detectColumnTypes() {

    String[][] val = {{"", "2010-05-03", "x"}, {"", "", ""}};

    ArrayList<String[]> dates = Lists.newArrayList(val);

    ColumnTypeDetector detector =
        new ColumnTypeDetector(
            Lists.newArrayList(
                LOCAL_DATE_TIME,
                LOCAL_TIME,
                LOCAL_DATE,
                BOOLEAN,
                SHORT,
                INTEGER,
                LONG,
                FLOAT,
                DOUBLE,
                STRING));

    ColumnType[] types =
        detector.detectColumnTypes(dates.iterator(), new ReadOptions.Builder().build());
    assertEquals(DateColumnType.instance(), types[1]);
    assertEquals(StringColumnType.instance(), types[2]);
  }

  /**
   * Test to ensure a useful error message is thrown when type detection runs into a row with an
   * extra comma at the end
   */
  @Test
  void errorMsg() {
    String df =
        "subject, time, age, weight, height"
            + LINE_END
            + "John Smith,    1,  33,     90,   1.87"
            + LINE_END
            + "Mary Smith,    1,  NA,     NA,   1.54, ";
    StringReader reader = new StringReader(df);
    assertThrows(ColumnIndexOutOfBoundsException.class, () -> Table.read().csv(reader));
  }
}
