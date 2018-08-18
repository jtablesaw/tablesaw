package tech.tablesaw.columns.numbers;

import org.junit.Test;
import tech.tablesaw.api.NumberColumn;

import static org.junit.Assert.*;

public class FloatDataWrapperTest {

    @Test
    public void getInt() {
        float[] data = {1.2f, 1.8f};
        NumberColumn column = NumberColumn.createWithFloats("test", data);
        assertEquals(1.0, column.getInt(0), 0.00001);
        assertEquals(2.0, column.getInt(1), 0.00001);
    }
}