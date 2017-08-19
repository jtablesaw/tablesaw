package tech.tablesaw.io;

import org.junit.Test;
import tech.tablesaw.api.DoubleColumn;

import static tech.tablesaw.api.ColumnType.DOUBLE;

public class TypeUtilsTest {

    /**
     * Test would throw ClassCastException if method does not work properly
     */
    @Test
    public void newColumn() throws Exception {
        DoubleColumn column = (DoubleColumn) TypeUtils.newColumn("test", DOUBLE);
    }

}