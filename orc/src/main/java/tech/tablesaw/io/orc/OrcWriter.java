package tech.tablesaw.io.orc;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.*;
import org.apache.orc.OrcFile;
import org.apache.orc.TypeDescription;
import org.apache.orc.Writer;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriterRegistry;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrcWriter implements DataWriter<OrcWriteOptions>
{
    private static final OrcWriter INSTANCE = new OrcWriter();
    private static final Map<ColumnType,TypeDescription> sawOrcTypeMapping;
    private static final Map<ColumnType,ITypeConverter> sawOrcConverterMapping;

    static {
        register(Table.defaultWriterRegistry);

        sawOrcTypeMapping = new HashMap<>();
        sawOrcTypeMapping.put(ColumnType.BOOLEAN,TypeDescription.createBoolean());
        sawOrcTypeMapping.put(ColumnType.INTEGER,TypeDescription.createInt());
        sawOrcTypeMapping.put(ColumnType.LONG,TypeDescription.createLong());
        sawOrcTypeMapping.put(ColumnType.SHORT,TypeDescription.createShort());
        sawOrcTypeMapping.put(ColumnType.DOUBLE,TypeDescription.createDouble());
        sawOrcTypeMapping.put(ColumnType.FLOAT,TypeDescription.createFloat());
        sawOrcTypeMapping.put(ColumnType.STRING,TypeDescription.createString());
        sawOrcTypeMapping.put(ColumnType.TEXT,TypeDescription.createString());
        sawOrcTypeMapping.put(ColumnType.INSTANT,TypeDescription.createTimestampInstant());
        sawOrcTypeMapping.put(ColumnType.LOCAL_DATE,TypeDescription.createDate());
        sawOrcTypeMapping.put(ColumnType.LOCAL_DATE_TIME,TypeDescription.createTimestamp());

        ITypeConverter booleanTypeConverter = new BooleanTypeConverter();
        ITypeConverter longTypeConverter = new LongTypeConverter();
        ITypeConverter doubleTypeConverter = new DoubleTypeConverter();
        ITypeConverter bytesTypeConverter = new BytesTypeConverter();
        ITypeConverter dateTypeConverter = new DateTypeConverter();
        ITypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();
        sawOrcConverterMapping = new HashMap<>();
        sawOrcConverterMapping.put(ColumnType.BOOLEAN,booleanTypeConverter);
        sawOrcConverterMapping.put(ColumnType.INTEGER,longTypeConverter);
        sawOrcConverterMapping.put(ColumnType.LONG,longTypeConverter);
        sawOrcConverterMapping.put(ColumnType.SHORT,longTypeConverter);
        sawOrcConverterMapping.put(ColumnType.DOUBLE,doubleTypeConverter);
        sawOrcConverterMapping.put(ColumnType.FLOAT,doubleTypeConverter);
        sawOrcConverterMapping.put(ColumnType.STRING,bytesTypeConverter);
        sawOrcConverterMapping.put(ColumnType.TEXT,bytesTypeConverter);
        sawOrcConverterMapping.put(ColumnType.LOCAL_DATE,dateTypeConverter);
        sawOrcConverterMapping.put(ColumnType.INSTANT,dateTimeTypeConverter);
        sawOrcConverterMapping.put(ColumnType.LOCAL_DATE_TIME,dateTimeTypeConverter);
    }

    public static void register(WriterRegistry registry) {
        registry.registerExtension("orc", INSTANCE);
        registry.registerOptions(OrcWriteOptions.class, INSTANCE);
    }

    @Override
    public void write(Table table, OrcWriteOptions options) throws IOException {
        Configuration conf = new Configuration();
        OrcFile.WriterOptions writeOptions = OrcFile.writerOptions(conf);
        TypeDescription typeDescription = TypeDescription.createStruct();
        if(options.getWriterOptions() != null){//Executed if TypeDescription and writer option provide by user
            writeOptions = options.getWriterOptions();
            typeDescription = writeOptions.getSchema();
        }else{
            List<Column<?>> columns = table.columns();
            for(Column<?> column : columns){
                if(!sawOrcTypeMapping.containsKey(column.type()))
                    throw new IllegalArgumentException("Unable to find relevant ORC data type to store: " + column.type());
                typeDescription.addField(column.name(),
                        sawOrcTypeMapping.get(column.type()));
            }
            writeOptions.setSchema(typeDescription);
        }
        try(Writer writer = OrcFile.createWriter(new Path(options.getOutputPath().getAbsolutePath()),
                writeOptions.overwrite(true))){
            VectorizedRowBatch batch = typeDescription.createRowBatch();
            int row = 0;
            for(int i=0; i<table.rowCount();i++){
                row = batch.size++;
                for (int j=0;j<table.columnCount();j++){
                    Column<?> column = table.column(j);
                    ColumnVector columnVector =  batch.cols[j];
                    sawOrcConverterMapping.get(column.type()).convertToOrcType(column,i,columnVector,row);
                }
                // If the batch is full, write it out and start over.
                if (batch.size == batch.getMaxSize()) {
                    writer.addRowBatch(batch);
                    batch.reset();
                }
            }
            if (batch.size != 0) {
                writer.addRowBatch(batch);
                batch.reset();
            }
        }
    }

    interface ITypeConverter{

        void convertToOrcType(Column<?> column, int rowIndexInJTable, ColumnVector columnVector, int row);

        default boolean checkAndHandleNull(Column<?> column, int rowIndexInJTable, ColumnVector columnVector, int row){
            Object cellValue = column.get(rowIndexInJTable);
            if(cellValue == null || StringUtils.isBlank(cellValue.toString())){
                columnVector.noNulls = false;
                columnVector.isNull[row] = true;
                return false;
            }
            return true;
        }
    }

    static class BooleanTypeConverter implements ITypeConverter{

        @Override
        public void convertToOrcType(Column<?> column, int rowIndexInJTable, ColumnVector columnVector, int row) {
            if(checkAndHandleNull(column, rowIndexInJTable, columnVector, row)){
                BooleanColumn booleanColumn = (BooleanColumn)column;
                ((LongColumnVector)columnVector).vector[row] = booleanColumn.get(rowIndexInJTable)?1:0;
            }
        }
    }

    static class LongTypeConverter implements ITypeConverter{

        @Override
        public void convertToOrcType(Column<?> column, int rowIndexInJTable, ColumnVector columnVector, int row) {
            if(checkAndHandleNull(column, rowIndexInJTable, columnVector, row)){
                Long value = ((Number)column.get(rowIndexInJTable)).longValue();
                ((LongColumnVector)columnVector).vector[row] = value;
            }
        }
    }

    static class DoubleTypeConverter implements ITypeConverter{

        @Override
        public void convertToOrcType(Column<?> column, int rowIndexInJTable, ColumnVector columnVector, int row) {
            if(checkAndHandleNull(column, rowIndexInJTable, columnVector, row)){
                Double value = ((Number)column.get(rowIndexInJTable)).doubleValue();
                ((DoubleColumnVector)columnVector).vector[row] = value;
            }
        }
    }

    static class BytesTypeConverter implements ITypeConverter{

        @Override
        public void convertToOrcType(Column<?> column, int rowIndexInJTable, ColumnVector columnVector, int row) {
            if(checkAndHandleNull(column, rowIndexInJTable, columnVector, row)){
                String value = column.getString(rowIndexInJTable);
                ((BytesColumnVector)columnVector).setVal(row,value.getBytes());
            }
        }
    }

    static class DateTypeConverter implements ITypeConverter{

        @Override
        public void convertToOrcType(Column<?> column, int rowIndexInJTable, ColumnVector columnVector, int row) {
            if(checkAndHandleNull(column, rowIndexInJTable, columnVector, row)){
                LocalDate date = ((DateColumn)column).get(rowIndexInJTable);
                ((LongColumnVector)columnVector).vector[row] = date.toEpochDay();
            }
        }
    }

    static class DateTimeTypeConverter implements ITypeConverter{

        @Override
        public void convertToOrcType(Column<?> column, int rowIndexInJTable, ColumnVector columnVector, int row) {
            if(checkAndHandleNull(column, rowIndexInJTable, columnVector, row)){
                ColumnType type = column.type();
                if(type == ColumnType.LOCAL_DATE_TIME) {
                    LocalDateTime dateTime = ((DateTimeColumn) column).get(rowIndexInJTable);
                    ((TimestampColumnVector)columnVector).set(row, Timestamp.valueOf(dateTime));
                }else {
                    Instant instant = ((InstantColumn) column).get(rowIndexInJTable);
                    ((TimestampColumnVector)columnVector).set(row, Timestamp.from(instant));
                }
            }
        }
    }

    @Override
    public void write(Table table, Destination dest) throws IOException {
        write(table,OrcWriteOptions.builder(dest).build());
    }

}
