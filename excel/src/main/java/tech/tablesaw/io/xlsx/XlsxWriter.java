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

package tech.tablesaw.io.xlsx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;

/**
 * A {@link DataWriter} implementation to write {@link Table}s to XLSX files.
 *
 * @author (c) 2024 Roeland Maes <roeland.maes@vito.be>
 */
public final class XlsxWriter implements DataWriter<XlsxWriteOptions> {

	private static final XlsxWriter INSTANCE = new XlsxWriter();

	public static XlsxWriter getInstance() {
		return INSTANCE;
	}

	@Override
	public void write(Table table, Destination dest) {
		write(XlsxWriteOptions.builder(dest).build(), table);
	}

	@Override
	public void write(Table table, XlsxWriteOptions options) {
		write(options, table);
	}

	/**
	 * @param dest
	 * @param tables
	 */
	public void write(File dest, Table ... tables) {
		write(XlsxWriteOptions.builder(dest).build(), tables);
	}

	/**
	 * Will write multiple tables to one Excel file.
	 * Every table will be written to another Excel sheet. The name of the sheets will be equal to ({@link Table#name()}.
	 *
	 * @param tables List of tables. Each table will be written to with to an Excel sheet.  The name of the each sheet is  {@link Table#name()}
	 * @param options
	 */
	public void write(XlsxWriteOptions options, Table ... tables) {
		OutputStream outputStream = options.destination().stream();
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			Map<Class<?>, XSSFCellStyle> styles = createStyles(options, workbook);

			for (Table table : tables) {
				Sheet sheet = workbook.createSheet(table.name());
				writeTableToSheet(sheet, table, styles);
				updateColumns(options, sheet, table);
			}
			workbook.write(outputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (options.autoClose()) {
				try {
					outputStream.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void appendTable(File excelFile, Table table, XlsxWriteOptions options) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excelFile));
		if (null == options) {
			options = XlsxWriteOptions.builder(excelFile).build();
		}
		Map<Class<?>, XSSFCellStyle> styles = createStyles(options, workbook);
		Sheet sheet = workbook.createSheet(table.name());
		writeTableToSheet(sheet, table, styles);
		updateColumns(options, sheet, table);
		try (FileOutputStream out = new FileOutputStream(excelFile)){
		     workbook.write(out);
		}
	}

	private Map<Class<?>, XSSFCellStyle> createStyles(XlsxWriteOptions options, XSSFWorkbook workbook) {
		Map<Class<?>, XSSFCellStyle> returnMap = new HashMap<>();
		XSSFCellStyle styleDateTime = workbook.createCellStyle();
		styleDateTime.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(options.getDateTimeFormat()));
		returnMap.put(LocalDateTime.class, styleDateTime);

		XSSFCellStyle styleDate = workbook.createCellStyle();
		styleDate.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(options.getDateFormat()));
		returnMap.put(LocalDate.class, styleDate);
		return returnMap;
	}

	private void updateColumns(XlsxWriteOptions options, Sheet sheet, Table table) {
		for (int i=0; i<table.columnCount(); i++) {
			sheet.setColumnWidth(i, options.getColumnWidth(i));
		}

		if (table.rowCount() > 0 && options.isAutoFilter()) {
			sheet.setAutoFilter(new CellRangeAddress(0, table.rowCount() - 1, 0, table.columnCount() -1 ));
		}
		sheet.createFreezePane(options.getFreezeFirstColumns(), options.getFreezeFirstRows());
	}

	private void writeTableToSheet(Sheet sheet, Table table, Map<Class<?>, XSSFCellStyle> styles) {
		Row header = sheet.createRow(0);
		ArrayList<String> columns = new ArrayList<>(table.columnNames());
		writeHeader(header, columns);
		for (tech.tablesaw.api.Row tableRow : table) {
			Row xlsxRow = sheet.createRow(tableRow.getRowNumber() + 1);
			writeRow(xlsxRow, tableRow, columns, styles);
		}
	}

	private void writeHeader(Row header, ArrayList<String> columns) {
		for (int i=0; i<columns.size(); i++) {
			Cell cell = header.createCell(i);
			cell.setCellValue(columns.get(i));
		}
	}

	private void writeRow(Row xlsxRow, tech.tablesaw.api.Row tableRow, ArrayList<String> columns, Map<Class<?>, XSSFCellStyle> styles) {
		for (int i=0; i<columns.size(); i++) {
			Cell cell = xlsxRow.createCell(i);
			Object value = tableRow.getObject(i);
			if (null != value) {
				XSSFCellStyle style = null;
				if (value instanceof Date) {
					cell.setCellValue((Date) value);
				} else if (value instanceof LocalDate) {
					style = styles.get(LocalDate.class);
					cell.setCellValue((LocalDate) value);
				} else if (value instanceof LocalDateTime) {
					style = styles.get(LocalDateTime.class);
					cell.setCellValue((LocalDateTime) value);
				} else if (value instanceof Calendar) {
					cell.setCellValue((Calendar) value);
				} else if (value instanceof Boolean) {
					cell.setCellValue((Boolean) value);
				} else if (value instanceof Number) {
					cell.setCellValue(((Number) value).doubleValue());
				} else {
					String strValue = value.toString();
					String maybeFormula = maybeFormula(strValue);
					if (null != maybeFormula) {
						setFormulaIfValid(cell, maybeFormula, strValue);
					} else {
						cell.setCellValue(strValue);
					}
				}
				if (null != style) {
					cell.setCellStyle(style);
				}
			}
		}
	}

	private String maybeFormula(String strValue) {
		if (null != strValue && strValue.startsWith("=")) {
			String formula = strValue.substring(1);
			return formula.isBlank() || formula.startsWith("=") ? null : formula;
		} else {
			return null;
		}
	}

	private void setFormulaIfValid(Cell cell, String maybeFormula, String strValue) {
		try {
			cell.setCellFormula(maybeFormula);
		} catch (FormulaParseException fpe) {
			cell.setCellValue(strValue);
		}
	}

}
