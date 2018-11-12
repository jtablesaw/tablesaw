package tablesaw.ui;

import tech.tablesaw.api.Table;

public class TablesawContentRow {

	private final Table table;
	private final int rowNum, tableRowNum;

	public TablesawContentRow(final int rowNum, final Table table, final int tableRowNum) {
		this.rowNum = rowNum;
		this.table = table;
		this.tableRowNum = tableRowNum;
	}

	public int getRowNum() {
		return rowNum;
	}

	public Table getTable() {
		return table;
	}

	public int getTableRowNum() {
		return tableRowNum;
	}
}
