package tablesaw.ui.util;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

// https://github.com/lawhcd/SWTMultiCheckSelectionCombo
public class MultiCheckSelectionCombo extends Composite {

	private List<Option> options = new ArrayList<Option>();
	private Button[] buttons;
	private final List<ModifyListener> modifyListeners = new ArrayList<ModifyListener>();
	private final List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
	private final List<VerifyListener> verifyListeners = new ArrayList<VerifyListener>();

	private String defaultText = "options";
	private Text display;

	private class Option {
		String text;
		boolean selection = false;
		Option(final String text) {
			if (text == null) throw new IllegalArgumentException();
			this.text = text;
		}
		Option(final String text, final boolean selection) {
			if (text == null) throw new IllegalArgumentException();
			this.text = text;
			this.selection = selection;
		}
	}

	/**
	 *
	 * Constructs a new instance of this class given its parent and a style value describing its behavior and appearance.
	 *
	 * The style value is either one of the style constants defined in class SWT which is applicable to instances of this class, or must be built by bitwise OR'ing together (that is, using the int "|" operator) two or more of those SWT style constants. The class description lists the style constants that are applicable to the class. Style bits are also inherited from superclasses.
	 *
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
	 * @param style the style of control to construct
	 * @throws IllegalArgumentException if the parent is null
	 * @throws SWTException if not called from the thread that created the parent
	 * @since version 1.0.0.0
	 */
	public MultiCheckSelectionCombo(final Composite parent, final int style) {
		super(parent, style);
		init();
	}

	/**
	 *
	 * Constructs a new instance of this class given its parent, a style value describing its behavior and appearance, and default text
	 *
	 * The style value is either one of the style constants defined in class SWT which is applicable to instances of this class, or must be built by bitwise OR'ing together (that is, using the int "|" operator) two or more of those SWT style constants. The class description lists the style constants that are applicable to the class. Style bits are also inherited from superclasses.
	 *
	 * The default text is displayed when no options are selected.
	 *
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
	 * @param style the style of control to construct
	 * @param defaultText the default text to display when no options are selected
	 * @throws IllegalArgumentException if the parent is null
	 * @throws IllegalArgumentException if the defaultText is null
	 * @throws SWTException if not called from the thread that created the parent
	 * @since version 1.0.0.0
	 */
	public MultiCheckSelectionCombo(final Composite parent, final int style, final String defaultText) {
		super(parent, style);
		if (defaultText == null) throw new IllegalArgumentException("Default Text cannot be null");
		this.defaultText = defaultText;
		init();
	}

	private void init() {
		final GridLayout layout = new GridLayout();
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		display = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		display.setLayoutData(new GridData(GridData.FILL_BOTH));
		display.setText(defaultText);
		display.addListener(SWT.MouseDown, e -> {
			showFloatShell(display);
		});
	}


	private void showFloatShell(final Text display) {
		final Point p = display.getParent().toDisplay(display.getLocation());
		final Point size = display.getSize();
		final Rectangle shellRect = new Rectangle(p.x, p.y + size.y, size.x, 0);
		final Shell shell = new Shell(MultiCheckSelectionCombo.this.getShell(), SWT.BORDER);
		shell.setLayout(new GridLayout());

		final Button toggle = new Button(shell, SWT.BUTTON1);
		toggle.setText("Toggle");
		toggle.addListener(SWT.MouseDown, e -> {
			toggleAll();
			for (final SelectionListener l : selectionListeners) {
				l.widgetSelected(new SelectionEvent(e));
			}
		});

		buttons = new Button[options.size()];
		for (int i =0; i < options.size(); i++) {
			final Button b = new Button(shell, SWT.CHECK);
			final Option o = options.get(i);
			b.setText(o.text);
			b.setSelection(o.selection);
			b.addListener(SWT.Selection, e -> {
				o.selection = b.getSelection();
				for (final SelectionListener l : selectionListeners) {
					l.widgetSelected(new SelectionEvent(e));
				}
			});
			b.pack();
			buttons[i] = b;
		}

		shell.pack();
		shell.setLocation(shellRect.x, shellRect.y);

		shell.addListener(SWT.Deactivate, e-> {
			if (shell != null && !shell.isDisposed()) {
				shell.setVisible(false);
				for (final SelectionListener l : selectionListeners) {
					l.widgetDefaultSelected(new SelectionEvent(e));
				}
				for (final VerifyListener l : verifyListeners) {
					final VerifyEvent v = new VerifyEvent(e);
					v.doit =false;
					l.verifyText(v);
				}

				displayText(display);

				for (final ModifyListener l : modifyListeners) {
					l.modifyText(new ModifyEvent(e));
				}
				for (final Button b : buttons) {
					b.dispose();
				}
				buttons = null;
				shell.dispose();
			}
		});

		shell.open();
	}

	private void displayText(final Text display) {
		final StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (final Option o : options) {
			if (o.selection) {
				sb.append(first? o.text : ", " + o.text);
				first = false;
			}
		}
		display.setText((sb.length() > 0)? sb.toString() : defaultText);
		display.pack();
		this.pack();
	}

	/**
	 *
	 * Sets the default display text to the argument.
	 *
	 * The default text is displayed when no options are selected.
	 *
	 * @param defaultText the default text to be set
	 * @throws IllegalArgumentException if the string is null
	 * @since version 1.0.0.0
	 */
	public void setDefaultText(final String defaultText) {
		if (defaultText == null) throw new IllegalArgumentException("Default Text cannot be null");
		this.defaultText = defaultText;
	}

	/**
	 *
	 * Adds the argument to the end of the receiver's list.
	 *
	 * @param string the option to be added
	 * @throws IllegalArgumentException if the string is null
	 * @since version 1.0.0.0
	 */
	public void add(final String string) {
		options.add(new Option(string));
	}

	/**
	 *
	 * Adds the argument with selection to the end of the receiver's list.
	 *
	 * @param string the new item
	 * @param selection default selection of the new item
	 * @throws IllegalArgumentException if the string is null
	 * @since version 1.0.0.0
	 */
	public void add(final String string, final boolean selection) {
		options.add(new Option(string, selection));
	}

	/**
	 *
	 * Adds the argument to the receiver's list at the given zero-relative index.
	 *
	 * @param string the new item
	 * @param index the index for the item
	 * @throws IllegalArgumentException if the string is null
	 * @throws IllegalArgumentException if the index is not between 0 and the number of elements in the list (inclusive)
	 * @since version 1.0.0.0
	 */
	public void add(final String string, final int index) {
		if (index < 0 || index > options.size()) throw new IllegalArgumentException("ERROR_INVALID_RANGE");
		options.add(index, new Option(string));
	}

	/**
	 *
	 * Adds the argument with selection to the receiver's list at the given zero-relative index.
	 *
	 * @param string the new item
	 * @param selection default selection of the new item
	 * @param index the index for the item
	 * @throws IllegalArgumentException if the string is null
	 * @throws IllegalArgumentException if the index is not between 0 and the number of elements in the list (inclusive)
	 * @since version 1.0.0.0
	 */
	public void add(final String string, final boolean selection, final int index) {
		if (index < 0 || index > options.size()) throw new IllegalArgumentException("ERROR_INVALID_RANGE");
		options.add(index, new Option(string, selection));
	}

	/**
	 *
	 * Adds the listener to the collection of listeners who will be notified when the receiver's text is modified, by sending it one of the messages defined in the ModifyListener interface..
	 *
	 * @param listener the listener which should be notified
	 * @throws IllegalArgumentException if the listener is null
	 * @since version 1.0.0.0
	 */
	public void addModifyListener(final ModifyListener listener) {
		if (listener == null) throw new IllegalArgumentException();
		modifyListeners.add(listener);
	}

	/**
	 *
	 * Adds the listener to the collection of listeners who will be notified when the user changes the receiver's selection, by sending it one of the messages defined in the SelectionListener interface.
	 *
	 * widgetSelected is called when the user changes the multi-check-selection-combo's list selection. widgetDefaultSelected is called when the floating shell is deactivated.
	 *
	 * @param listener the listener which should be notified
	 * @throws IllegalArgumentException if the listener is null
	 * @since version 1.0.0.0
	 */
	public void addSelectionListener(final SelectionListener listener) {
		if (listener == null) throw new IllegalArgumentException();
		selectionListeners.add(listener);
	}

	/**
	 *
	 * Adds the listener to the collection of listeners who will be notified when the receiver's text is verified, by sending it one of the messages defined in the VerifyListener interface.
	 *
	 * @param listener the listener which should be notified
	 * @throws IllegalArgumentException if the listener is null
	 * @since version 1.0.0.0
	 */
	public void addVerifyListener(final VerifyListener listener) {
		if (listener == null) throw new IllegalArgumentException();
		verifyListeners.add(listener);
	}

	/**
	 *
	 * Returns the preferred size of the receiver.
	 * The preferred size of a control is the size that it would best be displayed at. The width hint and height hint arguments allow the caller to ask a control questions such as "Given a particular width, how high does the control need to be to show all of the contents?" To indicate that the caller does not wish to constrain a particular dimension, the constant SWT.DEFAULT is passed for the hint.
	 *
	 * If the changed flag is true, it indicates that the receiver's contents have changed, therefore any caches that a layout manager containing the control may have been keeping need to be flushed. When the control is resized, the changed flag will be false, so layout manager caches can be retained.
	 *
	 * @param wHint the width hint (can be SWT.DEFAULT)
	 * @param hHint the height hint (can be SWT.DEFAULT)
	 * @param changed true if the control's contents have changed, and false otherwise
	 * @return the preferred size of the control
	 * @since version 1.0.0.0
	 */
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		return display.computeSize(wHint, hHint);
	}

	/**
	 *
	 * Deselects the item at the given zero-relative index in the receiver's list. If the item at the index was already deselected, it remains deselected. Indices that are out of range are ignored.
	 *
	 * @param index the index of the item to deselect
	 * @since version 1.0.0.0
	 */
	public void deselect(final int index) {
		if (index >= 0 && index < options.size()) {
			options.get(index).selection = false;
			if (buttons != null) {
				buttons[index].setSelection(false);
			}
		}
	}

	/**
	 *
	 * Deselects all selected items in the receiver's list.
	 *
	 * @since version 1.0.0.0
	 */
	public void deselectAll() {
		for (final Option o : options) {
			o.selection = false;
		}
		if (buttons != null) {
			for (int i=0; i < options.size(); i++){
				buttons[i].setSelection(false);
			}
		}
	}

	/**
	 *
	 * Returns the item at the given, zero-relative index in the receiver's list. Throws an exception if the index is out of range.
	 *
	 * @param index the index of the item to return
	 * @return the item at the given index
	 * @throws IllegalArgumentException if the index is not between 0 and the number of elements in the list minus 1 (inclusive)
	 * @since version 1.0.0.0
	 */
	public String getItem(final int index) {
		checkrange(index);
		return options.get(index).text;
	}



	/**
	 *
	 * Returns the number of items contained in the receiver's list.
	 *
	 * @return the number of items
	 * @since version 1.0.0.0
	 */
	public int getItemCount() {
		return options.size();
	}

	/**
	 *
	 * Returns the zero-relative indices of the items which are currently selected in the receiver's list, or empty array if no item is selected.
	 *
	 * @return the indices of the selected items
	 * @since version 1.0.0.0
	 */
	public int[] getSelectionIndices() {
		final ArrayDeque<Integer> selections = new ArrayDeque<Integer>();
		for (int i = 0; i<options.size(); i++) {
			if (options.get(i).selection) {
				selections.add(i);
			}
		}
		return selections.stream().mapToInt(i->i).toArray();
	}

	/**
	 *
	 * Returns the items which are currently selected in the receiver's list, or empty array if no item is selected.
	 *
	 * @return array of the selected items
	 * @since version 1.0.0.0
	 */
	public String[] getSelections() {
		final ArrayDeque<String> selections = new ArrayDeque<String>();
		for (int i = 0; i<options.size(); i++) {
			final Option o = options.get(i);
			if (o.selection) {
				selections.add(o.text);
			}
		}
		return selections.toArray(new String[selections.size()]);
	}

	/**
	 *
	 * Returns a string containing a copy of the contents of the receiver's text field
	 *
	 * @return the receiver's text
	 * @since version 1.0.0.0
	 */
	public String getText() {
		return display.getText();
	}

	/**
	 *
	 * Returns the height of the receivers's text field
	 *
	 * @return the text height
	 * @since version 1.0.0.0
	 */
	public int getTextHeight() {
		return display.getLineHeight();
	}

	/**
	 *
	 * Returns the maximum number of characters that the receiver's text field is capable of holding.
	 *
	 * @return the text limit
	 * @since version 1.0.0.0
	 */
	public int getTextLimit() {
		return display.getTextLimit();
	}

	/**
	 *
	 * Searches the receiver's list starting at the first item (index 0) until an item is found that is equal to the argument, and returns the index of that item. If no item is found, returns -1.
	 *
	 * @param string the search item
	 * @return index of the item
	 * @throws IllegalArgumentException if the string is null
	 * @since version 1.0.0.0
	 */
	public int indexOf(final String string) {
		if (string == null) throw new IllegalArgumentException();
		for (int i=0; i <options.size(); i++) {
			if (options.get(i).text.equals(string)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 *
	 * Searches the receiver's list starting at the given, zero-relative index until an item is found that is equal to the argument, and returns the index of that item. If no item is found or the starting index is out of range, returns -1.
	 *
	 * @param string the search item
	 * @param start the zero-relative index at which to begin the search
	 * @return index of the item
	 * @throws IllegalArgumentException if the string is null
	 * @since version 1.0.0.0
	 */
	public int indexOf(final String string, final int start) {
		if (string == null) throw new IllegalArgumentException();
		if (start < 0 || start >= options.size()) return -1;
		for (int i=start; i <options.size(); i++) {
			if (options.get(i).text.equals(string)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 *
	 * Removes the item from the receiver's list at the given zero-relative index.
	 *
	 * Disables but does not remove buttons from the floating shell if it is open. The floating shell will be updated when it is deactivated and re-populated
	 *
	 * @param index index for the item
	 * @throws IllegalArgumentException if the index is not between 0 and the number of elements in the list minus 1 (inclusive)
	 * @since version 1.0.0.0
	 */
	public void remove(final int index) {
		checkrange(index);
		options.remove(index);
		if (buttons != null) {
			buttons[index].setEnabled(false);
		}
	}

	/**
	 *
	 * Removes the items from the receiver's list which are between the given zero-relative start and end indices (inclusive).
	 *
	 * Disables but does not remove buttons from the floating shell if it is open. The floating shell will be updated when it is deactivated and re-populated
	 *
	 * @param start start of range
	 * @param end end of range
	 * @throws IllegalArgumentException  if either the start or end are not between 0 and the number of elements in the list minus 1 (inclusive)
	 * @since version 1.0.0.0
	 */
	public void remove(final int start, final int end) {
		checkrange(start);
		checkrange(end);
		assert start <= end;
		for (int i = start; i <= end; i++) {
			options.remove(i);
			if (buttons != null) {
				buttons[i].setEnabled(false);
			}
		}
	}

	/**
	 *
	 * Searches the receiver's list starting at the first item until an item is found that is equal to the argument, and removes that item from the list.
	 *
	 * Disables but does not remove buttons from the floating shell if it is open. The floating shell will be updated when it is deactivated and re-populated
	 *
	 * @param string the item to remove
	 * @throws IllegalArgumentException if the string is null
	 * @throws IllegalArgumentException if the string is not found in the list.
	 * @since version 1.0.0.0
	 */
	public void remove(final String string) {
		if (string != null) {
			for (int i=0; i < options.size(); i++) {
				if (options.get(i).text.equals(string)) {
					options.remove(i);
					if (buttons != null) {
						buttons[i].setEnabled(false);
					}
					return;
				}
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 *
	 * Removes all of the items from the receiver's list and restores receiver's text field to default.
	 *
	 * Disables but does not remove buttons from the floating shell if it is open. The floating shell will be updated when it is deactivated and re-populated
	 *
	 * @since version 1.0.0.0
	 */
	public void removeAll() {
		options.clear();
		if (buttons != null) {
			for (final Button b : buttons) {
				b.setEnabled(false);
			}
		}
		display.setText(defaultText);
		display.pack();
	}

	/**
	 *
	 * Removes the listener from the collection of listeners who will be notified when the user changes the receiver's selection.
	 *
	 * @param listener the listener which should no longer be notified
	 * @throws IllegalArgumentException if the listener is null
	 * @since version 1.0.0.0
	 */
	public void removeSelectionListener(final SelectionListener listener) {
		if (listener == null) throw new IllegalArgumentException();
		selectionListeners.remove(listener);
	}

	/**
	 *
	 * Removes the listener from the collection of listeners who will be notified when the control is verified.
	 *
	 * @param listener the listener which should no longer be notified
	 * @throws IllegalArgumentException if the listener is null
	 * @since version 1.0.0.0
	 */
	public void removeVerifyListener(final VerifyListener listener) {
		if (listener == null) throw new IllegalArgumentException();
		verifyListeners.remove(listener);
	}

	/**
	 *
	 * Removes the listener from the collection of listeners who will be notified when the receiver's text is modified.
	 *
	 * @param listener the listener which should no longer be notified
	 * @throws IllegalArgumentException if the listener is null
	 * @since version 1.0.0.0
	 */
	public void removeModifyListener(final ModifyListener listener) {
		if (listener == null) throw new IllegalArgumentException();
		modifyListeners.remove(listener);
	}

	/**
	 *
	 * Selects the item at the given zero-relative index in the receiver's list. If the item at the index was already selected, it remains selected. Indices that are out of range are ignored.
	 *
	 * @param index the index of item to select
	 * @since version 1.0.0.0
	 */
	public void select(final int index) {
		if (index >= 0 && index < options.size()) {
			options.get(index).selection = true;
			if (buttons != null) {
				buttons[index].setSelection(true);
			}
		}
	}

	/**
	 *
	 * Selects the items at the given zero-relative indices in the receiver's list. If the item at the index was already selected, it remains selected. Indices that are out of range are ignored.
	 *
	 * @param indices the indices of items to select
	 * @since version 1.0.0.0
	 */
	public void select(final int[] indices) {
		for (final int i : indices) {
			select(i);
		}
	}

	/**
	 *
	 * Sets the font that the receiver will use to paint textual information to the font specified by the argument, or to the default font for that kind of control if the argument is null.
	 *
	 * @param font the new font (or null)
	 * @since version 1.0.0.0
	 */
	@Override
	public void setFont(final Font font) {
		display.setFont(font);
	}

	/**
	 *
	 * Sets the text of the item in the receiver's list at the given zero-relative index to the string argument.
	 *
	 * @param index the index for the item
	 * @param string new text for the item
	 * @throws IllegalArgumentException if the index is not between 0 and the number of elements in the list minus 1 (inclusive)
	 * @throws IllegalArgumentException if the string is null
	 * @since version 1.0.0.0
	 */
	public void setItem(final int index, final String string) {
		checkrange(index);
		if (string == null) throw new IllegalArgumentException();
		options.get(index).text = string;
		if (buttons != null) {
			buttons[index].setText(string);
			buttons[index].pack();
		}
	}

	/**
	 *
	 * Sets the receiver's list to be the given array of items.
	 *
	 * @param items the array of items
	 * @throws IllegalArgumentException if the items array is null
	 * @throws IllegalArgumentException if an item in the items array is null

	 * @since version 1.0.0.0
	 */
	public void setItems(final String[] items) {
		options = new ArrayList<Option>(items.length);
		for (final String s : items) {
			add(s);
		}
	}

	/**
	 *
	 * Toggles the selection of each item in the receiver's list.
	 *
	 * @since version 1.0.0.0
	 */
	public void toggleAll() {
		for (final Option o : options) {
			o.selection = !o.selection;
		}
		if (buttons != null) {
			for (final Button b : buttons) {
				b.setSelection(!b.getSelection());
			}
		}
	}

	private void checkrange(final int index) {
		if (index < 0 || index >= options.size()) throw new IllegalArgumentException("ERROR_INVALID_RANGE");
	}

}