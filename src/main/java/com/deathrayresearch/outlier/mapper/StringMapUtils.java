package com.deathrayresearch.outlier.mapper;

import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.TextColumn;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

/**
 * String utility functions. Each function takes one or more String columns as input and produces
 * another Column as output. The resulting column need not be a string column.
 */
public interface StringMapUtils extends Column {

  default TextColumn upperCase() {
    TextColumn newColumn = TextColumn.create(this.name() + "[ucase]");
    TextColumn thisColumn = (TextColumn) this;
    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      if (value == null) {
        newColumn.set(r, null);
      } else {
        newColumn.set(r, value.toUpperCase());
      }
    }
    return newColumn;
  }

  default TextColumn lowerCase() {

    TextColumn newColumn = TextColumn.create(name() + "[lcase]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, value.toLowerCase());
    }
    return newColumn;
  }

  default TextColumn trim() {

    TextColumn newColumn = TextColumn.create(name() + "[trim]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, value.trim());
    }
    return newColumn;
  }

  default TextColumn replaceAll(String regex, String replacement) {

    TextColumn newColumn = TextColumn.create(name() + "[repl]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, value.replaceAll(regex, replacement));
    }
    return newColumn;
  }

  default TextColumn replaceFirst(String regex, String replacement) {

    TextColumn newColumn = TextColumn.create(name() + "[repl]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, value.replaceFirst(regex, replacement));
    }
    return newColumn;
  }

  default TextColumn substring(int start, int end) {

    TextColumn newColumn = TextColumn.create(name() + "[sub]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, value.substring(start, end));
    }
    return newColumn;
  }


  default TextColumn substring(int start) {

    TextColumn newColumn = TextColumn.create(name() + "[sub]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, value.substring(start));
    }
    return newColumn;
  }

  default TextColumn abbreviate(int maxWidth) {

    TextColumn newColumn = TextColumn.create(name() + "[abbr]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, StringUtils.abbreviate(value, maxWidth));
    }
    return newColumn;
  }

  default TextColumn padEnd(int minLength, char padChar) {

    TextColumn newColumn = TextColumn.create(name() + "[pad]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, Strings.padEnd(value, minLength, padChar));
    }
    return newColumn;
  }

  default TextColumn padStart(int minLength, char padChar) {

    TextColumn newColumn = TextColumn.create(name() + "[pad]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value = thisColumn.get(r);
      newColumn.set(r, Strings.padStart(value, minLength, padChar));
    }
    return newColumn;
  }

  default TextColumn commonPrefix(TextColumn column2) {

    TextColumn newColumn = TextColumn.create(name() + column2.name() + "[prefix]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value1 = thisColumn.get(r);
      String value2 = column2.get(r);
      newColumn.set(r, Strings.commonPrefix(value1, value2));
    }
    return newColumn;
  }

  default TextColumn commonSuffix(TextColumn column2) {

    TextColumn newColumn = TextColumn.create(name() + column2.name() + "[suffix]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value1 = thisColumn.get(r);
      String value2 = column2.get(r);
      newColumn.set(r, Strings.commonSuffix(value1, value2));
    }
    return newColumn;
  }

  /**
   * Returns a column containing the levenshtein distance between the two given string columns
   */
  default Column distance(TextColumn column2) {

    FloatColumn newColumn = FloatColumn.create(name() + column2.name() + "[distance]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String value1 = thisColumn.get(r);
      String value2 = column2.get(r);
      newColumn.set(r, StringUtils.getLevenshteinDistance(value1, value2));
    }
    return newColumn;
  }

  default TextColumn join(TextColumn column2, String delimiter) {

    TextColumn newColumn = TextColumn.create(name() + column2.name() + "[join]");
    TextColumn thisColumn = (TextColumn) this;

    for (int r = 0; r < size(); r++) {
      String[] values = new String[2];
      values[0] = thisColumn.get(r);
      values[1] = column2.get(r);
      newColumn.set(r, StringUtils.join(values, delimiter));
    }
    return newColumn;
  }
}
