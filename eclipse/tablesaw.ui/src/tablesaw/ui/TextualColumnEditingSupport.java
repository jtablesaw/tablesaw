package tablesaw.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class TextualColumnEditingSupport extends AbstractTablesawColumnEditingSupport {

	private CellEditor editor;

	public TextualColumnEditingSupport(final TableViewer viewer, final AbstractTablesawColumnLabelProvider labelProvider, final Object... objects) {
		super(viewer, labelProvider);
		setChoices(objects);
	}

	private Object[] objects;
	private String[] labels;

	public void setChoices(final Object... objects) {
		this.objects = objects;
		if (objects != null && objects.length > 0) {
			this.labels = new String[objects.length];
			for (int i = 0; i < objects.length; i++) {
				labels[i] = labelProvider.getColumnText(objects[i]);
			}
			this.editor = new ComboBoxCellEditor(getTableViewer().getTable(), labels, SWT.NONE);
		} else {
			this.labels = null;
			this.editor = new TextCellEditor(getTableViewer().getTable(), SWT.NONE);
		}
	}

	private final Map<ColumnType, CellEditor> cellEditors = new HashMap<ColumnType, CellEditor>();

	public void setColumnTypeEditor(final ColumnType columnType, final CellEditor cellEditor) {
		cellEditors.put(columnType, cellEditor);
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		if (element instanceof TablesawContentRow) {
			final TablesawContentRow row = (TablesawContentRow) element;
			final Column<?> column = labelProvider.getColumn(row);
			if (column != null) {
				final CellEditor cellEditor = cellEditors.get(column.type());
				if (cellEditor != null) {
					return cellEditor;
				}
			}
		}
		return editor;
	}

	@Override
	protected Object getValue(final Object element) {
		final Object value = super.getValue(element);
		return (value != null ? String.valueOf(value) : "");
	}

	@Override
	protected void setColumnValue(final Column<?> column, final int rowNum, final Object inputValue) {
		if (inputValue == null || labelProvider.getMissingValueLabel().equals(inputValue)) {
			setColumnObject(column, rowNum, (String) null);
		} else if (inputValue instanceof String) {
			if (this.labels != null) {
				if (inputValue != null) {
					for (int i = 0; i < labels.length; i++) {
						if (inputValue.equals(labels[i])) {
							setColumnObject(column, rowNum, objects[i]);
							break;
						}
					}
				}
			}
			setColumnObject(column, rowNum, inputValue);
		} else {
			setColumnObject(column, rowNum, String.valueOf(inputValue));
		}
	}

	protected CsvReadOptions csvReadOptions;

	public void setCsvReadOptions(final CsvReadOptions csvReadOptions) {
		this.csvReadOptions = csvReadOptions;
	}

	public void setCsvReadOptions(final CsvReadOptions.Builder csvReadOptions) {
		setCsvReadOptions(csvReadOptions.build());
	}

	protected void setColumnObject(final Column<?> column, final int rowNum, final Object input) {
		if (column instanceof BooleanColumn) {
			if (input == null) {
				((BooleanColumn) column).setMissing(rowNum);
			} else {
				((BooleanColumn) column).set(rowNum, Boolean.TRUE.equals(Boolean.valueOf(String.valueOf(input))));
			}
		} else if (column instanceof DoubleColumn) {
			final double value = (input == null ? (Double) DoubleColumnType.missingValueIndicator() : Double.valueOf(String.valueOf(input)));
			((DoubleColumn) column).set(rowNum, value);
		} else if (column instanceof StringColumn) {
			((StringColumn) column).set(rowNum, (input == null ? null : String.valueOf(input)));
		} else if (column instanceof DateColumn) {
			((DateColumn) column).set(rowNum, (input == null ? null : LocalDate.parse(String.valueOf(input))));
		} else if (column instanceof DateTimeColumn) {
			((DateTimeColumn) column).set(rowNum, (input == null ? null : LocalDateTime.parse(String.valueOf(input))));
		} else if (column instanceof TimeColumn) {
			((TimeColumn) column).set(rowNum, (input == null ? null : LocalTime.parse(String.valueOf(input))));
		}
	}
}
