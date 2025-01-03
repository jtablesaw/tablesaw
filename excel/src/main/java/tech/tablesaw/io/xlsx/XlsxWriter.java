package tech.tablesaw.io.xlsx;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.RuntimeIOException;
import tech.tablesaw.io.WriterRegistry;

public class XlsxWriter implements DataWriter<XlsxWriteOptions> {

  private static final XlsxWriter INSTANCE = new XlsxWriter();

  static {
    register(Table.defaultWriterRegistry);
  }

  public static void register(WriterRegistry registry) {
    registry.registerExtension("xlsx", INSTANCE);
    registry.registerOptions(XlsxWriteOptions.class, INSTANCE);
  }

  @Override
  public void write(Table table, Destination dest) {
    write(table, XlsxWriteOptions.builder(dest).build());
  }

  @Override
  public void write(Table table, XlsxWriteOptions options) {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      CellStyle localDateStyle = workbook.createCellStyle();
      localDateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd"));
      CellStyle localDateTimeStyle = workbook.createCellStyle();
      localDateTimeStyle.setDataFormat(
          workbook.createDataFormat().getFormat("yyyy-MM-dd hh:mm:ss"));
      CellStyle localTimeStyle = workbook.createCellStyle();
      localTimeStyle.setDataFormat(workbook.createDataFormat().getFormat("[h]:mm:ss"));
      XSSFSheet sheet = workbook.createSheet(table.name());
      int rowNum = 0;
      List<String> columnNames = table.columnNames();

      var headerRow = sheet.createRow(rowNum++);
      int colNum = 0;
      for (String colName : columnNames) {
        var cell = headerRow.createCell(colNum++);
        cell.setCellValue(colName);
      }

      for (var row : table) {
        var excelRow = sheet.createRow(rowNum++);
        colNum = 0;
        for (String colName : columnNames) {
          var cell = excelRow.createCell(colNum++);
          var type = row.getColumnType(colName);

          if (ColumnType.STRING.equals(type)) {
            cell.setCellValue(row.getString(colName));
          } else if (ColumnType.LOCAL_DATE.equals(type)) {
            cell.setCellValue(row.getDate(colName));
            cell.setCellStyle(localDateStyle);
          } else if (ColumnType.LOCAL_DATE_TIME.equals(type)) {
            cell.setCellValue(row.getDate(colName));
            cell.setCellStyle(localDateTimeStyle);
          } else if (ColumnType.LOCAL_TIME.equals(type)) {
            double time = DateUtil.convertTime(row.getTime(colName).toString());
            cell.setCellValue(time);
            cell.setCellStyle(localTimeStyle);
          } else if (ColumnType.INSTANT.equals(type)) {
            ZonedDateTime zdt =
                ZonedDateTime.ofInstant(row.getInstant(colName), ZoneId.systemDefault());
            cell.setCellValue(GregorianCalendar.from(zdt));
            cell.setCellStyle(localDateTimeStyle);
          } else if (ColumnType.FLOAT.equals(type)) {
            cell.setCellValue(row.getFloat(colName));
          } else if (ColumnType.INTEGER.equals(type)) {
            cell.setCellValue(row.getInt(colName));
          } else if (ColumnType.DOUBLE.equals(type)) {
            cell.setCellValue(row.getDouble(colName));
          } else if (ColumnType.BOOLEAN.equals(type)) {
            cell.setCellValue(row.getBoolean(colName));
          } else {
            cell.setCellValue(String.valueOf(row.getObject(colName)));
          }
        }
      }

      try (OutputStream os = options.destination().stream()) {
        workbook.write(os);
      }

    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
  }
}
