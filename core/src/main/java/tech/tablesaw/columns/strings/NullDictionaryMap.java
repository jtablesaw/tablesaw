package tech.tablesaw.columns.strings;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

/**
 * A null dictionary map has no actual dictionary as the underlying data is not dictionary encoded.
 * It works with textual data that is non-categorical, or where the number of categories approaches
 * 1/2 of the total number of values, making dictionary encoding inefficient.
 */
public class NullDictionaryMap implements DictionaryMap {

  private final TextualStringData data;

  public NullDictionaryMap(DictionaryMap dictionaryMap) {
    data = TextualStringData.create();
    for (String s : dictionaryMap) {
      data.append(s);
    }
  }

  @Override
  public void sortDescending() {
    data.sortDescending();
  }

  @Override
  public void sortAscending() {
    data.sortAscending();
  }

  @Override
  public int getKeyAtIndex(int rowNumber) {
    throw new UnsupportedOperationException(
        "NullDictionaryMap does not support getKeyAtIndex because there is no dictionary encoding.");
  }

  @Override
  public String getValueForKey(int key) {
    throw new UnsupportedOperationException(
        "NullDictionaryMap does not support getValueForKey because there is no dictionary encoding.");
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public String getValueForIndex(int rowIndex) {
    return data.get(rowIndex);
  }

  @Override
  public int countOccurrences(String value) {
    return data.countOccurrences(value);
  }

  @Override
  public Set<String> asSet() {
    return data.asSet();
  }

  @Override
  public int getKeyForIndex(int i) {
    throw new UnsupportedOperationException(
        "NullDictionaryMap does not support getKeyForIndex because there is no dictionary encoding.");
  }

  @Override
  public int firstIndexOf(String string) {
    return data.firstIndexOf(string);
  }

  @Override
  public String[] asObjectArray() {
    return data.asObjectArray();
  }

  @Override
  public Selection selectIsIn(String... strings) {
    return data.isIn(strings);
  }

  @Override
  public Selection selectIsIn(Collection<String> strings) {
    return data.isIn(strings);
  }

  @Override
  public void append(String value) throws NoKeysAvailableException {
    data.append(value);
  }

  @Override
  public void set(int rowIndex, String stringValue) throws NoKeysAvailableException {
    data.set(rowIndex, stringValue);
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public int countUnique() {
    return data.countUnique();
  }

  @Override
  public Table countByCategory(String columnName) {
    return data.countByCategory(columnName);
  }

  @Override
  public Selection isEqualTo(String string) {
    return data.isEqualTo(string);
  }

  @Override
  public String get(int index) {
    return data.get(index);
  }

  @Override
  public Selection isIn(String... strings) {
    return data.isIn(strings);
  }

  @Override
  public Selection isIn(Collection<String> strings) {
    return data.isIn(strings);
  }

  @Override
  public Selection isNotIn(String... strings) {
    return data.isNotIn(strings);
  }

  @Override
  public Selection isNotIn(Collection<String> strings) {
    return data.isNotIn(strings);
  }

  @Override
  public List<BooleanColumn> getDummies() {
    return data.getDummies();
  }

  @Override
  public byte[] asBytes(int rowNumber) {
    return data.asBytes(rowNumber);
  }

  @Override
  public int countMissing() {
    return data.countMissing();
  }

  @Override
  public Iterator<String> iterator() {
    return data.iterator();
  }

  @Override
  public void appendMissing() {
    data.appendMissing();
  }

  @Override
  public boolean isMissing(int rowNumber) {
    return data.isMissing(rowNumber);
  }

  @Override
  public DictionaryMap promoteYourself() {
    return this;
  }

  @Override
  public int nextKeyWithoutIncrementing() {
    return size();
  }

  @Override
  public boolean canPromoteToText() {
    return false;
  }
}
