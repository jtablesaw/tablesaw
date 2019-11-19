package tech.tablesaw.io.orc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrcReaderTest {

    @SafeVarargs
    private final <T> void assertColumnValues(Column<T> column, T... ts) {
        for (int i = 0; i < column.size(); i++) {
            if (ts[i] == null) {
                assertTrue(
                        column.isMissing(i),
                        "Should be missing value in row "
                                + i
                                + " of column "
                                + column.name()
                                + ", but it was "
                                + column.get(i));
            } else {
                assertEquals(
                        ts[i], column.get(i), "Wrong value in row " + i + " of column " + column.name());
            }
        }
    }

    @Test
    void testReadForSupportedTypes(){
        LocalDateTime localDateTime = LocalDateTime.of(2019,11,19,22,45,54);
        LocalDate localDate = LocalDate.of(2019,11,19);
        try {
            String pathToFile = "../data/my-file.orc";
            Table table = new OrcReader().read(OrcReadOptions.builder(pathToFile).build());
            assertColumnValues(table.intColumn("age"),0,1,2);
            assertColumnValues(table.floatColumn("weight"),0F,30.899999618530273F,61.79999923706055F);
            assertColumnValues(table.stringColumn("name"),"John Doe 0","John Doe 1","John Doe 2");
            assertColumnValues(table.dateTimeColumn("times"),localDateTime,localDateTime,localDateTime);
            assertColumnValues(table.dateColumn("dates"),localDate,localDate,localDate);
            assertColumnValues(table.stringColumn("gender"),"MAL","FEM","MAL");
            assertColumnValues(table.stringColumn("fullname"),"John Doe 0","John Doe 1","John Doe 2");
            assertColumnValues(table.shortColumn("saving"),(short)32750,(short)32751,(short)32752);
            assertColumnValues(table.longColumn("somelong"),922337203685477L,922337203685478L,922337203685479L);
            assertColumnValues(table.doubleColumn("somedouble"),0D,7990979.8789,15981959.7578);
            assertColumnValues(table.shortColumn("sometiny"),(short)124,(short)125,(short)126);
            assertColumnValues(table.booleanColumn("isPresent"),false,true,false);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testReadForUnSupportedTypes(){
        String pathToBinaryStringFile = "../data/TestOrcFile.testStringAndBinaryStatistics.orc";
        assertThrows(IllegalArgumentException.class, ()->{
            new OrcReader().read(OrcReadOptions.builder(pathToBinaryStringFile).build());
        });
    }

    @Test
    void testReadForColumnWithNullValues(){
        LocalDateTime localDateTime = LocalDateTime.of(2019,11,19,22,45,54);
        LocalDate localDate = LocalDate.of(2019,11,19);
        try {
            String pathToFileWithNulls = "../data/my-file-null.orc";
            Table table = new OrcReader().read(OrcReadOptions.builder(pathToFileWithNulls).build());
            assertColumnValues(table.intColumn("age"),0,null,2);
            assertColumnValues(table.floatColumn("weight"),0F,null,61.79999923706055F);
            assertColumnValues(table.stringColumn("name"),"John Doe 0",null,"John Doe 2");
            assertColumnValues(table.dateTimeColumn("times"),localDateTime,null,localDateTime);
            assertColumnValues(table.dateColumn("dates"),localDate,null,localDate);
            assertColumnValues(table.stringColumn("gender"),"FEM",null,"FEM");
            assertColumnValues(table.stringColumn("fullname"),"John Doe 0",null,"John Doe 2");
            assertColumnValues(table.shortColumn("saving"),(short)32750,null,(short)32751);
            assertColumnValues(table.longColumn("somelong"),922337203685477L,null,922337203685478L);
            assertColumnValues(table.doubleColumn("somedouble"),0D,null,15981959.7578);
            assertColumnValues(table.shortColumn("sometiny"),(short)124,null,(short)125);
            assertColumnValues(table.booleanColumn("isPresent"),false,null,false);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testReadForZeroSizeTable(){
        try {
            String zeroTest = "../data/zero.orc";
            Table table = new OrcReader().read(OrcReadOptions.builder(zeroTest).build());
            assertEquals(table.columnCount(),0);
            assertEquals(table.name(),"zero.orc");
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testReadForRepeatingValues(){
        LocalDateTime localDateTime = LocalDateTime.of(2019,11,19,22,45,54);
        LocalDate localDate = LocalDate.of(2019,11,19);
        try {
            String pathToFile = "../data/my-file-rep.orc";
            Table table = new OrcReader().read(OrcReadOptions.builder(pathToFile).build());
            assertColumnValues(table.intColumn("age"),0,1,2);
            assertColumnValues(table.floatColumn("weight"),0F,30.899999618530273F,61.79999923706055F);
            assertColumnValues(table.stringColumn("name"),"John Doe","John Doe","John Doe");
            assertColumnValues(table.dateTimeColumn("times"),localDateTime,localDateTime,localDateTime);
            assertColumnValues(table.dateColumn("dates"),localDate,localDate,localDate);
            assertColumnValues(table.stringColumn("gender"),"MAL","FEM","MAL");
            assertColumnValues(table.stringColumn("fullname"),"John Doe","John Doe","John Doe");
            assertColumnValues(table.shortColumn("saving"),(short)32750,(short)32750,(short)32750);
            assertColumnValues(table.longColumn("somelong"),922337203685477L,922337203685477L,922337203685477L);
            assertColumnValues(table.doubleColumn("somedouble"),0D,7990979.8789,15981959.7578);
            assertColumnValues(table.shortColumn("sometiny"),(short)124,(short)124,(short)124);
            assertColumnValues(table.booleanColumn("isPresent"),false,true,false);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

}