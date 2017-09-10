package tech.tablesaw.io;

import java.time.LocalDate;
import java.util.Locale;
import org.junit.Test;
import tech.tablesaw.api.DoubleColumn;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tech.tablesaw.api.ColumnType.DOUBLE;

public class TypeUtilsTest {

    /**
     * Test would throw ClassCastException if method does not work properly
     */
    @Test
    public void testNewColumn() {
        DoubleColumn column = (DoubleColumn) TypeUtils.newColumn("test", DOUBLE);
        assertThat(column, notNullValue());
    }
    
    @Test
    public void testDateFormaterWithLocaleEN() {
        String anotherDate = "12-May-2015";
        LocalDate result = LocalDate.parse(anotherDate, TypeUtils.DATE_FORMATTER.withLocale(Locale.ENGLISH));
        assertThat(result, notNullValue());
    }
}