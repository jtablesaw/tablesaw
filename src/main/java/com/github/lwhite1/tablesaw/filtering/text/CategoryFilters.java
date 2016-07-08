package com.github.lwhite1.tablesaw.filtering.text;

import com.github.lwhite1.tablesaw.columns.CategoryColumnUtils;
import com.github.lwhite1.tablesaw.util.BitmapBackedSelection;
import com.github.lwhite1.tablesaw.util.Selection;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public interface CategoryFilters extends CategoryColumnUtils {

  default Selection equalToIgnoringCase(String string) {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (next.endsWith(string)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection startsWith(String string) {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (next.startsWith(string)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection endsWith(String string) {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (next.endsWith(string)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection stringContains(String string) {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (next.contains(string)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection matchesRegex(String string) {
    Pattern p = Pattern.compile(string);
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      Matcher m = p.matcher(next);
      if (m.matches()) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection empty() {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (next.isEmpty()) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection isAlpha() {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (StringUtils.isAlpha(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection isNumeric() {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (StringUtils.isNumeric(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection isAlphaNumeric() {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (StringUtils.isAlphanumeric(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection isUpperCase() {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (StringUtils.isAllUpperCase(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection isLowerCase() {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (StringUtils.isAllLowerCase(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection hasLengthEqualTo(int lengthChars) {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (next.length() == lengthChars) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection isShorterThan(int lengthChars) {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (next.length() < lengthChars) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default Selection isLongerThan(int lengthChars) {
    Selection results = new BitmapBackedSelection();
    int i = 0;
    for (String next : this) {
      if (next.length() > lengthChars) {
        results.add(i);
      }
      i++;
    }
    return results;
  }
}
