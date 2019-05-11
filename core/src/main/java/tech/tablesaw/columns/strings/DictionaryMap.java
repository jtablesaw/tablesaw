package tech.tablesaw.columns.strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

public interface DictionaryMap {

    void sortDescending();

    void sortAscending();

    String getValueForKey(int key);

    int size();

    String getValueForIndex(int rowIndex);

    int countOccurrences(String value);

    Set<String> asSet();

    /**
     * Returns the number of unique values at or before the given index
     */
    default int uniqueValuesAt(int index) {
        int result = 0;
        List<String> uniqueValues = new ArrayList<>();
        for (int i = 0; i <= index; i++) {
            String value = getValueForIndex(i);
            int uniqueIndex = uniqueValues.indexOf(value);
            if (uniqueIndex < 0) {
                uniqueValues.add(value);
                uniqueIndex = uniqueValues.size() - 1;
                result++;
            }
        }
        return result;
    }
    
    default int[] asIntArray() {
        int[] result = new int[size()];
        List<String> uniqueValues = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            String value = getValueForIndex(i);
              int uniqueIndex = uniqueValues.indexOf(value);
              if (uniqueIndex < 0) {
                    uniqueValues.add(value);
                    uniqueIndex = uniqueValues.size() - 1;
              }
              result[i] = uniqueIndex;
        }
        return result;
    }

    int getKeyForIndex(int i);

    int firstIndexOf(String string);

    String[] asObjectArray();

    Selection selectIsIn(String... strings);

    Selection selectIsIn(Collection<String> strings);

    void append(String value) throws NoKeysAvailableException;

    void set(int rowIndex, String stringValue) throws NoKeysAvailableException;

    void clear();

    int countUnique();

    Table countByCategory(String columnName);

    Selection isEqualTo(String string);

    default Selection isNotEqualTo(String string) {
        Selection selection = new BitmapBackedSelection();
        selection.addRange(0, size());
        selection.andNot(isEqualTo(string));
        return selection;
    }

    List<BooleanColumn> getDummies();

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     */
    public byte[] asBytes(int rowNumber);

    /**
     * Returns the count of missing values in this column
     */
    int countMissing();

    Iterator<String> iterator() ;

    void appendMissing();

    boolean isMissing(int rowNumber);

    DictionaryMap promoteYourself();
}
