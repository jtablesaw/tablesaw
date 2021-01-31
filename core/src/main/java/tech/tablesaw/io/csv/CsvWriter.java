/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.io.csv;

import com.univocity.parsers.csv.CsvWriterSettings;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.concurrent.Immutable;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriterRegistry;

/** Class that writes tables and individual columns to CSV files */
@Immutable
public final class CsvWriter implements DataWriter<CsvWriteOptions> {

  private static final CsvWriter INSTANCE = new CsvWriter();
  private static final String nullValue = "";

  static {
    register(Table.defaultWriterRegistry);
  }

  public static void register(WriterRegistry registry) {
    registry.registerExtension("csv", INSTANCE);
    registry.registerOptions(CsvWriteOptions.class, INSTANCE);
  }

  public void write(Table table, CsvWriteOptions options) {
    CsvWriterSettings settings = createSettings(options);
    
    Map<String, NumberFormat> columnSpecificFormats=setupNumberFormatters(table, options);
    
    com.univocity.parsers.csv.CsvWriter csvWriter = null;
    // Creates a writer with the above settings;
    try {
      csvWriter =
          new com.univocity.parsers.csv.CsvWriter(options.destination().createWriter(), settings);

      if (options.header()) {
        String[] header = new String[table.columnCount()];
        for (int c = 0; c < table.columnCount(); c++) {
          String name = table.column(c).name();
          header[c] = options.columnNameMap().getOrDefault(name, name);
        }
        csvWriter.writeHeaders(header);
      }
        for (int r = 0; r < table.rowCount(); r++) {
            String[] entries = new String[table.columnCount()];
            for (int c = 0; c < table.columnCount(); c++) {
                table.get(r, c);//Why this statement?
                DateTimeFormatter dateFormatter = options.dateFormatter();
                DateTimeFormatter dateTimeFormatter = options.dateTimeFormatter();
                ColumnType columnType = table.column(c).type();
                if (dateFormatter != null && columnType.equals(ColumnType.LOCAL_DATE)) {
                    DateColumn dc = (DateColumn) table.column(c);
                    entries[c] = options.dateFormatter().format(dc.get(r));
                } else if (dateTimeFormatter != null && columnType.equals(ColumnType.LOCAL_DATE_TIME)) {
                    DateTimeColumn dc = (DateTimeColumn) table.column(c);
                    entries[c] = options.dateTimeFormatter().format(dc.get(r));
                } else if (columnSpecificFormats != null && (table.column(c) instanceof NumberColumn)) {
                    NumberFormat numberFormat = columnSpecificFormats.get(table.column(c).name());
                    if (numberFormat != null) {
                        NumberColumn nc = (NumberColumn) table.column(c);
                        try {
                            entries[c] = numberFormat.format(nc.get(r));
                        } catch (Exception e) {
                            //NumberFormat.format throws illegalArgumentException for missing values. Fall back on unformatted value
                            entries[c] = table.getUnformatted(r, c);
                        }
                    } else {
                        entries[c] = table.getUnformatted(r, c);
                    }
                } else {
                    entries[c] = table.getUnformatted(r, c);
                }
            }
        csvWriter.writeRow(entries);
      }
    } finally {
      if (csvWriter != null) {
        csvWriter.flush();
        csvWriter.close();
      }
    }
  }

  protected static CsvWriterSettings createSettings(CsvWriteOptions options) {
    CsvWriterSettings settings = new CsvWriterSettings();
    // Sets the character sequence to write for the values that are null.
    settings.setNullValue(nullValue);
    if (options.separator() != null) {
      settings.getFormat().setDelimiter(options.separator());
    }
    if (options.quoteChar() != null) {
      settings.getFormat().setQuote(options.quoteChar());
    }
    if (options.escapeChar() != null) {
      settings.getFormat().setQuoteEscape(options.escapeChar());
    }
    if (options.lineEnd() != null) {
      settings.getFormat().setLineSeparator(options.lineEnd());
    }
    settings.setIgnoreLeadingWhitespaces(options.ignoreLeadingWhitespaces());
    settings.setIgnoreTrailingWhitespaces(options.ignoreTrailingWhitespaces());
    // writes empty lines as well.
    settings.setSkipEmptyLines(false);
    settings.setQuoteAllFields(options.quoteAllFields());
    return settings;
  }

  @Override
  public void write(Table table, Destination dest) {
    write(table, CsvWriteOptions.builder(dest).build());
  }
  
  
  /**Setup a complete Map where both Default NumberFormatters and column specific NumberFormatters
   * are applied to relevant columns
   * 
   * @param table
   * @param options
   * @return null if no Formatting options has been set, a complete Map with keys for all 
   * NumberColumns otherwise. The NumberFormat for a particular column may still be 
   * null if no option has been set that covers the column.
   */
  private Map<String, NumberFormat> setupNumberFormatters(Table  table, CsvWriteOptions options){
     
      if(options.defaultDecimalNumberFormat()==null && options.defaultWholeNumberFormat()==null && options.columnSpecificNumberFormatMap()==null){
          return null;
      }else{
          Map<String, NumberFormat> numberFormatMap=new HashMap<>();
                  
          if(options.columnSpecificNumberFormatMap()!=null){
              numberFormatMap.putAll(options.columnSpecificNumberFormatMap());
          }
          List<String> columnNames=table.columnNames();
          
          for (String columnName : columnNames) {
              Column column=table.column(columnName);
              ColumnType numberType=column.type();
              
              if(numberFormatMap.get(columnName)!=null && !(column instanceof NumberColumn)){
                  throw new IllegalArgumentException("Column specific NumberFormat applied to non Numeric column");
              }else if(numberFormatMap.get(columnName)!=null && column instanceof NumberColumn){
                  //leave the current NumberFormat unchanged
              }else if(numberFormatMap.get(columnName)==null 
                      && options.defaultDecimalNumberFormat()!=null 
                      && (numberType.equals(ColumnType.DOUBLE)||numberType.equals(ColumnType.FLOAT))){          
                  numberFormatMap.put(columnName, options.defaultDecimalNumberFormat());
              }else if(numberFormatMap.get(columnName)==null 
                      && options.defaultWholeNumberFormat()!=null 
                      && (numberType.equals(ColumnType.INTEGER)||numberType.equals(ColumnType.LONG)||numberType.equals(ColumnType.SHORT))){
                  numberFormatMap.put(columnName,options.defaultWholeNumberFormat());
              }
              else{
                  numberFormatMap.put(columnName,null);
              }
          }
          return numberFormatMap;
      }
  }
      
}
