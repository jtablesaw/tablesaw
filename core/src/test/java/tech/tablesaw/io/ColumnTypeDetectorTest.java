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
import static tech.tablesaw.api.ColumnType.TEXT;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.dates.DateColumnType;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.columns.strings.TextColumnType;

class ColumnTypeDetectorTest {

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
                STRING,
                TEXT));

    ColumnType[] types =
        detector.detectColumnTypes(dates.iterator(), new ReadOptions.Builder().build());
    assertEquals(TextColumnType.instance(), types[0]);
    assertEquals(DateColumnType.instance(), types[1]);
    assertEquals(StringColumnType.instance(), types[2]);
  }
}
