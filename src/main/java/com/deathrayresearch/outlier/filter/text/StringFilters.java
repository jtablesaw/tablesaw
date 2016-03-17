package com.deathrayresearch.outlier.filter.text;

import com.deathrayresearch.outlier.columns.StringColumnUtils;
import org.apache.commons.lang3.StringUtils;
import org.roaringbitmap.RoaringBitmap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public interface StringFilters extends StringColumnUtils {

  default RoaringBitmap equalToIgnoringCase(String string) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (next.endsWith(string)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap startsWith(String string) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (next.startsWith(string)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap endsWith(String string) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (next.endsWith(string)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap contains(String string) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (next.contains(string)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap matchesRegex(String string) {
    Pattern p = Pattern.compile(string);
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      Matcher m = p.matcher(next);
      if (m.matches()) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap empty() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (next.isEmpty()) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap isAlpha() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (StringUtils.isAlpha(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap isNumeric() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (StringUtils.isNumeric(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap isAlphaNumeric() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (StringUtils.isAlphanumeric(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap isUpperCase() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (StringUtils.isAllUpperCase(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap isLowerCase() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (StringUtils.isAllLowerCase(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap hasLengthEqualTo(int lengthChars) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (next.length() == lengthChars) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap isShorterThan(int lengthChars) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (next.length() < lengthChars) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  default RoaringBitmap isLongerThan(int lengthChars) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data()) {
      if (next.length() > lengthChars) {
        results.add(i);
      }
      i++;
    }
    return results;
  }
}
