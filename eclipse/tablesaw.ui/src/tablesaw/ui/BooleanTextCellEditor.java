package tablesaw.ui;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

public class BooleanTextCellEditor extends TextCellEditor {

	public BooleanTextCellEditor(final Composite parent) {
		super(parent);
	}

	@Override
	public void activate() {
		setValue(Boolean.toString(! Boolean.valueOf(String.valueOf(getValue()))));
		fireApplyEditorValue();
		deactivate();
	}
}