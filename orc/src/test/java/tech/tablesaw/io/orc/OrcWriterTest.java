package tech.tablesaw.io.orc;

import org.apache.hadoop.conf.Configuration;
import org.apache.orc.OrcFile;
import org.apache.orc.TypeDescription;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

public class OrcWriterTest {

    private final <T> void assertColumnValues(Column<T> column, Column<T> columnRight) {
        for (int i = 0; i < column.size(); i++) {
            if (columnRight.get(i) == null) {
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
                        column.get(i), columnRight.get(i), "Wrong value in row " + i + " of column " + column.name());
            }
        }
    }

    @Test
    public void testWritePositive(){
        Table table = Table.create("orc-write-p.orc");
        StringColumn stringColumn = StringColumn.create("name", Arrays.asList("john", "marry", "cassey", "batman"));
        TextColumn textColumn = TextColumn.create("address", Arrays.asList("12th downing street","342th Sideway road"
                ,"90th Riverfornt apartment","Wane Manor, Gotham"));
        BooleanColumn booleanColumn = BooleanColumn.create("married",Arrays.asList(false,true,false,true));
        IntColumn intColumn = IntColumn.create("age", new int []{21,34,12,35});
        LongColumn longColumn = LongColumn.create("phone",new long[]{9420132578L,9420132978L,9454132578L,9465132578L});
        ShortColumn shortColumn = ShortColumn.create("id",new short[]{102,108,765,245});
        DoubleColumn doubleColumn = DoubleColumn.create("balance",new double[]{89523600.987,95222111000.9008,78855522200.97,9600000000000.9000});
        FloatColumn floatColumn = FloatColumn.create("weight", new float[]{98.23f,40.7f,25f,75f});
        InstantColumn instantColumn = InstantColumn.create("instant", Arrays.asList(Instant.now(),Instant.now(),Instant.now(),Instant.now()));
        DateColumn dateColumn = DateColumn.create("DOB",Arrays.asList(LocalDate.of(1982,2,21),
                LocalDate.of(1982,2,22),
                LocalDate.of(1982,2,23),
                LocalDate.of(1981,2,21)));
        DateTimeColumn timeColumn = DateTimeColumn.create("time",Arrays.asList(LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now()));
        table.addColumns(stringColumn,textColumn,booleanColumn,intColumn,longColumn,shortColumn,doubleColumn,floatColumn,instantColumn,dateColumn,timeColumn);

        String pathToFile = "../data/orc-write-p.orc";
        try {
            File outFile = new File(pathToFile);
            table.write().usingOptions(OrcWriteOptions.builder(outFile).build());
            Table readTable = new OrcReader().read(OrcReadOptions.builder(pathToFile).build());
            assertEquals(table.columnCount(),readTable.columnCount());
            assertEquals(table.rowCount(),readTable.rowCount());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testWritePositiveWithProvideTypeDescription(){
        Configuration configuration = new Configuration();
        TypeDescription schema = TypeDescription.fromString("struct<" +
                "name1:varchar(50)," +
                "address:char(25)," +
                "isMarried:boolean," +
                "age:int," +
                "phone:bigint," +
                "id:smallint," +
                "balanceNew:double," +
                "weight:float," +
                "instantNoodle:timestamp with local time zone," +
                "dates:date," +
                "time:timestamp>");
        OrcFile.WriterOptions writeOptions = OrcFile.writerOptions(configuration);
        Table table = Table.create("orc-write-p-2.orc");
        StringColumn stringColumn = StringColumn.create("name", Arrays.asList("john", "marry", "cassey", "batman"));
        TextColumn textColumn = TextColumn.create("address", Arrays.asList("12th downing street","342th Sideway road"
                ,"90th Riverfornt apartment","Wane Manor, Gotham"));
        BooleanColumn booleanColumn = BooleanColumn.create("married",Arrays.asList(false,true,false,true));
        IntColumn intColumn = IntColumn.create("age", new int []{21,34,12,35});
        LongColumn longColumn = LongColumn.create("phone",new long[]{9420132578L,9420132978L,9454132578L,9465132578L});
        ShortColumn shortColumn = ShortColumn.create("id",new short[]{102,108,765,245});
        DoubleColumn doubleColumn = DoubleColumn.create("balance",new double[]{89523600.987,95222111000.9008,78855522200.97,9600000000000.9000});
        FloatColumn floatColumn = FloatColumn.create("weight", new float[]{98.23f,40.7f,25f,75f});
        InstantColumn instantColumn = InstantColumn.create("instant", Arrays.asList(Instant.now(),Instant.now(),Instant.now(),Instant.now()));
        DateColumn dateColumn = DateColumn.create("DOB",Arrays.asList(LocalDate.of(1982,2,21),
                LocalDate.of(1982,2,22),
                LocalDate.of(1982,2,23),
                LocalDate.of(1981,2,21)));
        DateTimeColumn timeColumn = DateTimeColumn.create("time",Arrays.asList(LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now()));
        table.addColumns(stringColumn,textColumn,booleanColumn,intColumn,longColumn,shortColumn,doubleColumn,floatColumn,instantColumn,dateColumn,timeColumn);

        String pathToFile = "../data/orc-write-p-2.orc";
        try {
            File outFile = new File(pathToFile);
            table.write().usingOptions(OrcWriteOptions.builder(outFile).ocrWriteOptions(writeOptions.setSchema(schema)).build());
            Table readTable = new OrcReader().read(OrcReadOptions.builder(pathToFile).build());
            System.out.println(readTable.print());
            assertEquals(table.columnCount(),readTable.columnCount());
            assertEquals(table.rowCount(),readTable.rowCount());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
    @Test
    public void testWriteWithSomeNulls(){
        Table table = Table.create("orc-write-p-null.orc");
        StringColumn stringColumn = StringColumn.create("name", Arrays.asList("john", null, "cassey", "batman"));
        TextColumn textColumn = TextColumn.create("address", Arrays.asList("12th downing street","342th Sideway road"
                ,"90th Riverfornt apartment",null));
        BooleanColumn booleanColumn = BooleanColumn.create("married",Arrays.asList(false,true,false,null));
        IntColumn intColumn = IntColumn.create("age",  new Integer[] {21,34,null,35});
        LongColumn longColumn = LongColumn.create("phone",LongStream.of(9420132578L,9420132978L,9454132578L,9465132578L));
        ShortColumn shortColumn = ShortColumn.create("id",new Short[]{102,108,765,78});
        DoubleColumn doubleColumn = DoubleColumn.create("balance",new Double[]{89523600.987,95222111000.9008,78855522200.898,null});
        FloatColumn floatColumn = FloatColumn.create("weight", new Float[]{98.23f,null,25f,null});
        InstantColumn instantColumn = InstantColumn.create("instant", Arrays.asList(Instant.now(),Instant.now(),null,null));//Appa
        DateColumn dateColumn = DateColumn.create("DOB",Arrays.asList(LocalDate.of(1982,2,21),
                LocalDate.of(1982,2,22),
                null,
                LocalDate.of(1981,2,21)));
        DateTimeColumn timeColumn = DateTimeColumn.create("time",Arrays.asList(null,LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now()));
        table.addColumns(stringColumn,textColumn,booleanColumn,intColumn,longColumn,shortColumn,doubleColumn,floatColumn,instantColumn,dateColumn,timeColumn);

        String pathToFile = "../data/orc-write-p-null.orc";
        try {
            File outFile = new File(pathToFile);
            table.write().usingOptions(OrcWriteOptions.builder(outFile).build());
            Table readTable = new OrcReader().read(OrcReadOptions.builder(pathToFile).build());
            assertEquals(table.columnCount(),readTable.columnCount());
            assertEquals(table.rowCount(),readTable.rowCount());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
