package tech.tablesaw.columns.numbers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tech.tablesaw.api.FloatColumn;

public class FloatDataWrapperTest {

    @Test
    public void getInt() {
        float[] data = {1.2f, 1.8f};
        FloatColumn column = FloatColumn.create("test", data);
        assertEquals(1.0, column.getInt(0), 0.00001);
        assertEquals(2.0, column.getInt(1), 0.00001);
    }
}