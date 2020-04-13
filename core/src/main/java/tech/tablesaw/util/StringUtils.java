/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.tablesaw.util;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Operations on {@link java.lang.String} that are {@code null} safe.
 *
 * <p>{@code StringUtils} handles {@code null} input Strings quietly. That is to say that a {@code
 * null} input will return {@code null}. Where a {@code boolean} or {@code int} is being returned
 * details vary by method.
 *
 * <p>A side effect of the {@code null} handling is that a {@code NullPointerException} should be
 * considered a bug in {@code StringUtils}.
 *
 * <p>Methods in this class give sample code to explain their operation. The symbol {@code *} is
 * used to indicate any input including {@code null}.
 *
 * <p>#ThreadSafe#
 *
 * @see java.lang.String
 * @since 1.0
 */
// @Immutable
public class StringUtils {
  // Performance testing notes (JDK 1.4, Jul03, scolebourne)
  // Whitespace:
  // Character.isWhitespace() is faster than WHITESPACE.indexOf()
  // where WHITESPACE is a string of all whitespace characters
  //
  // Character access:
  // String.charAt(n) versus toCharArray(), then array[n]
  // String.charAt(n) is about 15% worse for a 10K string
  // They are about equal for a length 50 string
  // String.charAt(n) is about 4 times better for a length 3 string
  // String.charAt(n) is best bet overall
  //
  // Append:
  // String.concat about twice as fast as StringBuffer.append
  // (not sure who tested this)

  /**
   * The empty String {@code ""}.
   *
   * @since 2.0
   */
  private static final String EMPTY = "";

  /** The maximum size to which the padding constant(s) can expand. */
  private static final int PAD_LIMIT = 8192;

  private static final Pattern ZERO_DECIMAL_PATTERN = Pattern.compile("\\.0+$");

  private StringUtils() {}

  // Empty checks
  // -----------------------------------------------------------------------

  /**
   * Splits a String by Character type as returned by {@code java.lang.Character.getType(char)}.
   * Groups of contiguous characters of the same type are returned as complete tokens, with the
   * following exception: if {@code camelCase} is {@code true}, the character of type {@code
   * Character.UPPERCASE_LETTER}, if any, immediately preceding a token of type {@code
   * Character.LOWERCASE_LETTER} will belong to the following token rather than to the preceding, if
   * any, {@code Character.UPPERCASE_LETTER} token.
   *
   * @param str the String to split, may be {@code null}
   * @return an array of parsed Strings, {@code null} if null String input
   * @since 2.4
   */
  public static String[] splitByCharacterTypeCamelCase(final String str) {
    if (str == null) {
      return null;
    }
    if (str.isEmpty()) {
      return new String[0];
    }
    final char[] c = str.toCharArray();
    final List<String> list = new ArrayList<>();
    int tokenStart = 0;
    int currentType = Character.getType(c[tokenStart]);
    for (int pos = tokenStart + 1; pos < c.length; pos++) {
      final int type = Character.getType(c[pos]);
      if (type == currentType) {
        continue;
      }
      if (type == Character.LOWERCASE_LETTER && currentType == Character.UPPERCASE_LETTER) {
        final int newTokenStart = pos - 1;
        if (newTokenStart != tokenStart) {
          list.add(new String(c, tokenStart, newTokenStart - tokenStart));
          tokenStart = newTokenStart;
        }
      } else {
        list.add(new String(c, tokenStart, pos - tokenStart));
        tokenStart = pos;
      }
      currentType = type;
    }
    list.add(new String(c, tokenStart, c.length - tokenStart));
    return list.toArray(new String[0]);
  }

  // Joining
  // -----------------------------------------------------------------------

  /**
   * Joins the elements of the provided array into a single String containing the provided list of
   * elements.
   *
   * <p>No delimiter is added before or after the list. Null objects or empty strings within the
   * array are represented by empty strings.
   *
   * <pre>
   * StringUtils.join(null, *)               = null
   * StringUtils.join([], *)                 = ""
   * StringUtils.join([null], *)             = ""
   * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
   * StringUtils.join(["a", "b", "c"], null) = "abc"
   * StringUtils.join([null, "", "a"], ';')  = ";;a"
   * </pre>
   *
   * @param array the array of values to join together, may be null
   * @param separator the separator character to use
   * @return the joined String, {@code null} if null array input
   * @since 2.0
   */
  public static String join(final Object[] array, final char separator) {
    if (array == null) {
      return null;
    }
    return join(array, separator, 0, array.length);
  }

  /**
   * Joins the elements of the provided array into a single String containing the provided list of
   * elements.
   *
   * <p>No delimiter is added before or after the list. Null objects or empty strings within the
   * array are represented by empty strings.
   *
   * <pre>
   * StringUtils.join(null, *)               = null
   * StringUtils.join([], *)                 = ""
   * StringUtils.join([null], *)             = ""
   * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
   * StringUtils.join(["a", "b", "c"], null) = "abc"
   * StringUtils.join([null, "", "a"], ';')  = ";;a"
   * </pre>
   *
   * @param array the array of values to join together, may be null
   * @param separator the separator character to use
   * @param startIndex the first index to start joining from. It is an error to pass in an end index
   *     past the end of the array
   * @param endIndex the index to stop joining from (exclusive). It is an error to pass in an end
   *     index past the end of the array
   * @return the joined String, {@code null} if null array input
   * @since 2.0
   */
  private static String join(
      final Object[] array, final char separator, final int startIndex, final int endIndex) {
    if (array == null) {
      return null;
    }
    final int noOfItems = endIndex - startIndex;
    if (noOfItems <= 0) {
      return EMPTY;
    }
    final StringBuilder buf = new StringBuilder(noOfItems * 16);
    for (int i = startIndex; i < endIndex; i++) {
      if (i > startIndex) {
        buf.append(separator);
      }
      if (array[i] != null) {
        buf.append(array[i]);
      }
    }
    return buf.toString();
  }

  // Padding
  // -----------------------------------------------------------------------
  /**
   * Repeat a String {@code repeat} times to form a new String.
   *
   * <pre>
   * StringUtils.repeat(null, 2) = null
   * StringUtils.repeat("", 0)   = ""
   * StringUtils.repeat("", 2)   = ""
   * StringUtils.repeat("a", 3)  = "aaa"
   * StringUtils.repeat("ab", 2) = "abab"
   * StringUtils.repeat("a", -2) = ""
   * </pre>
   *
   * @param str the String to repeat, may be null
   * @param repeat number of times to repeat str, negative treated as zero
   * @return a new String consisting of the original String repeated, {@code null} if null String
   *     input
   */
  public static String repeat(final String str, final int repeat) {
    // Performance tuned for 2.0 (JDK1.4)

    if (str == null) {
      return null;
    }
    if (repeat <= 0) {
      return EMPTY;
    }
    final int inputLength = str.length();
    if (repeat == 1 || inputLength == 0) {
      return str;
    }
    if (inputLength == 1 && repeat <= PAD_LIMIT) {
      return repeat(str.charAt(0), repeat);
    }

    final int outputLength = inputLength * repeat;
    switch (inputLength) {
      case 1:
        return repeat(str.charAt(0), repeat);
      case 2:
        final char ch0 = str.charAt(0);
        final char ch1 = str.charAt(1);
        final char[] output2 = new char[outputLength];
        for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
          output2[i] = ch0;
          output2[i + 1] = ch1;
        }
        return new String(output2);
      default:
        final StringBuilder buf = new StringBuilder(outputLength);
        for (int i = 0; i < repeat; i++) {
          buf.append(str);
        }
        return buf.toString();
    }
  }

  /**
   * Returns padding using the specified delimiter repeated to a given length.
   *
   * <pre>
   * StringUtils.repeat('e', 0)  = ""
   * StringUtils.repeat('e', 3)  = "eee"
   * StringUtils.repeat('e', -2) = ""
   * </pre>
   *
   * <p>Note: this method does not support padding with <a
   * href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary
   * Characters</a> as they require a pair of {@code char}s to be represented. If you are needing to
   * support full I18N of your applications consider using {@link #repeat(String, int)} instead.
   *
   * @param ch character to repeat
   * @param repeat number of times to repeat char, negative treated as zero
   * @return String with repeated character
   * @see #repeat(String, int)
   */
  private static String repeat(final char ch, final int repeat) {
    if (repeat <= 0) {
      return EMPTY;
    }
    final char[] buf = new char[repeat];
    for (int i = repeat - 1; i >= 0; i--) {
      buf[i] = ch;
    }
    return new String(buf);
  }

  /**
   * Gets a CharSequence length or {@code 0} if the CharSequence is {@code null}.
   *
   * @param cs a CharSequence or {@code null}
   * @return CharSequence length or {@code 0} if the CharSequence is {@code null}.
   * @since 2.4
   * @since 3.0 Changed signature from length(String) to length(CharSequence)
   */
  public static int length(final CharSequence cs) {
    return cs == null ? 0 : cs.length();
  }

  // Case conversion
  // -----------------------------------------------------------------------

  /**
   * Capitalizes a String changing the first character to title case as per {@link
   * Character#toTitleCase(int)}. No other characters are changed.
   *
   * <p>A {@code null} input String returns {@code null}.
   *
   * <pre>
   * StringUtils.capitalize(null)  = null
   * StringUtils.capitalize("")    = ""
   * StringUtils.capitalize("cat") = "Cat"
   * StringUtils.capitalize("cAt") = "CAt"
   * StringUtils.capitalize("'cat'") = "'cat'"
   * </pre>
   *
   * @param str the String to capitalize, may be null
   * @return the capitalized String, {@code null} if null String input
   * @since 2.0
   */
  public static String capitalize(final String str) {
    int strLen;
    if (str == null || (strLen = str.length()) == 0) {
      return str;
    }

    final int firstCodepoint = str.codePointAt(0);
    final int newCodePoint = Character.toTitleCase(firstCodepoint);
    if (firstCodepoint == newCodePoint) {
      // already capitalized
      return str;
    }

    final int newCodePoints[] = new int[strLen]; // cannot be longer than the char array
    int outOffset = 0;
    newCodePoints[outOffset++] = newCodePoint; // copy the first codepoint
    for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; ) {
      final int codepoint = str.codePointAt(inOffset);
      newCodePoints[outOffset++] = codepoint; // copy the remaining ones
      inOffset += Character.charCount(codepoint);
    }
    return new String(newCodePoints, 0, outOffset);
  }

  // Character Tests
  // -----------------------------------------------------------------------
  /**
   * Checks if the CharSequence contains only Unicode letters.
   *
   * <p>{@code null} will return {@code false}. An empty CharSequence (length()=0) will return
   * {@code false}.
   *
   * <pre>
   * StringUtils.isAlpha(null)   = false
   * StringUtils.isAlpha("")     = false
   * StringUtils.isAlpha("  ")   = false
   * StringUtils.isAlpha("abc")  = true
   * StringUtils.isAlpha("ab2c") = false
   * StringUtils.isAlpha("ab-c") = false
   * </pre>
   *
   * @param cs the CharSequence to check, may be null
   * @return {@code true} if only contains letters, and is non-null
   * @since 3.0 Changed signature from isAlpha(String) to isAlpha(CharSequence)
   * @since 3.0 Changed "" to return false and not true
   */
  public static boolean isAlpha(final String cs) {
    if (Strings.isNullOrEmpty(cs)) {
      return false;
    }
    final int sz = cs.length();
    for (int i = 0; i < sz; i++) {
      if (!Character.isLetter(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the CharSequence contains only Unicode letters or digits.
   *
   * <p>{@code null} will return {@code false}. An empty CharSequence (length()=0) will return
   * {@code false}.
   *
   * <pre>
   * StringUtils.isAlphanumeric(null)   = false
   * StringUtils.isAlphanumeric("")     = false
   * StringUtils.isAlphanumeric("  ")   = false
   * StringUtils.isAlphanumeric("abc")  = true
   * StringUtils.isAlphanumeric("ab c") = false
   * StringUtils.isAlphanumeric("ab2c") = true
   * StringUtils.isAlphanumeric("ab-c") = false
   * </pre>
   *
   * @param cs the CharSequence to check, may be null
   * @return {@code true} if only contains letters or digits, and is non-null
   * @since 3.0 Changed signature from isAlphanumeric(String) to isAlphanumeric(CharSequence)
   * @since 3.0 Changed "" to return false and not true
   */
  public static boolean isAlphanumeric(final String cs) {
    if (Strings.isNullOrEmpty(cs)) {
      return false;
    }
    final int sz = cs.length();
    for (int i = 0; i < sz; i++) {
      if (!Character.isLetterOrDigit(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the CharSequence contains only Unicode digits. A decimal point is not a Unicode digit
   * and returns false.
   *
   * <p>{@code null} will return {@code false}. An empty CharSequence (length()=0) will return
   * {@code false}.
   *
   * <p>Note that the method does not allow for a leading sign, either positive or negative. Also,
   * if a String passes the numeric test, it may still generate a NumberFormatException when parsed
   * by Integer.parseInt or Long.parseLong, e.g. if the value is outside the range for int or long
   * respectively.
   *
   * <pre>
   * StringUtils.isNumeric(null)   = false
   * StringUtils.isNumeric("")     = false
   * StringUtils.isNumeric("  ")   = false
   * StringUtils.isNumeric("123")  = true
   * StringUtils.isNumeric("\u0967\u0968\u0969")  = true
   * StringUtils.isNumeric("12 3") = false
   * StringUtils.isNumeric("ab2c") = false
   * StringUtils.isNumeric("12-3") = false
   * StringUtils.isNumeric("12.3") = false
   * StringUtils.isNumeric("-123") = false
   * StringUtils.isNumeric("+123") = false
   * </pre>
   *
   * @param cs the CharSequence to check, may be null
   * @return {@code true} if only contains digits, and is non-null
   * @since 3.0 Changed signature from isNumeric(String) to isNumeric(CharSequence)
   * @since 3.0 Changed "" to return false and not true
   */
  public static boolean isNumeric(final String cs) {
    if (Strings.isNullOrEmpty(cs)) {
      return false;
    }
    final int sz = cs.length();
    for (int i = 0; i < sz; i++) {
      if (!Character.isDigit(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the CharSequence contains only lowercase characters.
   *
   * <p>{@code null} will return {@code false}. An empty CharSequence (length()=0) will return
   * {@code false}.
   *
   * <pre>
   * StringUtils.isAllLowerCase(null)   = false
   * StringUtils.isAllLowerCase("")     = false
   * StringUtils.isAllLowerCase("  ")   = false
   * StringUtils.isAllLowerCase("abc")  = true
   * StringUtils.isAllLowerCase("abC")  = false
   * StringUtils.isAllLowerCase("ab c") = false
   * StringUtils.isAllLowerCase("ab1c") = false
   * StringUtils.isAllLowerCase("ab/c") = false
   * </pre>
   *
   * @param cs the CharSequence to check, may be null
   * @return {@code true} if only contains lowercase characters, and is non-null
   * @since 2.5
   * @since 3.0 Changed signature from isAllLowerCase(String) to isAllLowerCase(CharSequence)
   */
  public static boolean isAllLowerCase(final String cs) {
    if (Strings.isNullOrEmpty(cs)) {
      return false;
    }
    final int sz = cs.length();
    for (int i = 0; i < sz; i++) {
      if (!Character.isLowerCase(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the CharSequence contains only uppercase characters.
   *
   * <p>{@code null} will return {@code false}. An empty String (length()=0) will return {@code
   * false}.
   *
   * <pre>
   * StringUtils.isAllUpperCase(null)   = false
   * StringUtils.isAllUpperCase("")     = false
   * StringUtils.isAllUpperCase("  ")   = false
   * StringUtils.isAllUpperCase("ABC")  = true
   * StringUtils.isAllUpperCase("aBC")  = false
   * StringUtils.isAllUpperCase("A C")  = false
   * StringUtils.isAllUpperCase("A1C")  = false
   * StringUtils.isAllUpperCase("A/C")  = false
   * </pre>
   *
   * @param cs the CharSequence to check, may be null
   * @return {@code true} if only contains uppercase characters, and is non-null
   * @since 2.5
   * @since 3.0 Changed signature from isAllUpperCase(String) to isAllUpperCase(CharSequence)
   */
  public static boolean isAllUpperCase(final String cs) {
    if (Strings.isNullOrEmpty(cs)) {
      return false;
    }
    final int sz = cs.length();
    for (int i = 0; i < sz; i++) {
      if (!Character.isUpperCase(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Removes all trailing zero decimals from the given String, assuming all decimals are zero and
   * any zero decimals actually exist.
   *
   * <p>A {@code null} input String returns {@code null}.
   *
   * @param str the String to handle, may be null
   * @return string without trailing zero decimals
   */
  public static String removeZeroDecimal(final String str) {
    if (Strings.isNullOrEmpty(str)) {
      return str;
    }
    return ZERO_DECIMAL_PATTERN.matcher(str).replaceFirst(EMPTY);
  }

  // Abbreviating
  // -----------------------------------------------------------------------
  /**
   * Abbreviates a String using ellipses. This will turn "Now is the time for all good men" into
   * "Now is the time for..."
   *
   * <p>Specifically:
   *
   * <ul>
   *   <li>If the number of characters in {@code str} is less than or equal to {@code maxWidth},
   *       return {@code str}.
   *   <li>Else abbreviate it to {@code (substring(str, 0, max-3) + "...")}.
   *   <li>If {@code maxWidth} is less than {@code 4}, throw an {@code IllegalArgumentException}.
   *   <li>In no case will it return a String of length greater than {@code maxWidth}.
   * </ul>
   *
   * <pre>
   * StringUtils.abbreviate(null, *)      = null
   * StringUtils.abbreviate("", 4)        = ""
   * StringUtils.abbreviate("abcdefg", 6) = "abc..."
   * StringUtils.abbreviate("abcdefg", 7) = "abcdefg"
   * StringUtils.abbreviate("abcdefg", 8) = "abcdefg"
   * StringUtils.abbreviate("abcdefg", 4) = "a..."
   * StringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
   * </pre>
   *
   * @param str the String to check, may be null
   * @param abbrevMarker the String indicate abbreviation
   * @param maxWidth maximum length of result String, must be at least 4
   * @return abbreviated String, {@code null} if null String input
   * @throws IllegalArgumentException if the width is too small
   * @since 2.0
   */
  public static String abbreviate(final String str, final String abbrevMarker, final int maxWidth) {
    return abbreviate(str, abbrevMarker, 0, maxWidth);
  }

  /**
   * Abbreviates a String using a given replacement marker. This will turn "Now is the time for all
   * good men" into "...is the time for..." if "..." was defined as the replacement marker.
   *
   * <p>Works like {@code abbreviate(String, String, int)}, but allows you to specify a "left edge"
   * offset. Note that this left edge is not necessarily going to be the leftmost character in the
   * result, or the first character following the replacement marker, but it will appear somewhere
   * in the result.
   *
   * <p>In no case will it return a String of length greater than {@code maxWidth}.
   *
   * <pre>
   * StringUtils.abbreviate(null, null, *, *)                 = null
   * StringUtils.abbreviate("abcdefghijklmno", null, *, *)    = "abcdefghijklmno"
   * StringUtils.abbreviate("", "...", 0, 4)                  = ""
   * StringUtils.abbreviate("abcdefghijklmno", "---", -1, 10) = "abcdefg---"
   * StringUtils.abbreviate("abcdefghijklmno", ",", 0, 10)    = "abcdefghi,"
   * StringUtils.abbreviate("abcdefghijklmno", ",", 1, 10)    = "abcdefghi,"
   * StringUtils.abbreviate("abcdefghijklmno", ",", 2, 10)    = "abcdefghi,"
   * StringUtils.abbreviate("abcdefghijklmno", "::", 4, 10)   = "::efghij::"
   * StringUtils.abbreviate("abcdefghijklmno", "...", 6, 10)  = "...ghij..."
   * StringUtils.abbreviate("abcdefghijklmno", "*", 9, 10)    = "*ghijklmno"
   * StringUtils.abbreviate("abcdefghijklmno", "'", 10, 10)   = "'ghijklmno"
   * StringUtils.abbreviate("abcdefghijklmno", "!", 12, 10)   = "!ghijklmno"
   * StringUtils.abbreviate("abcdefghij", "abra", 0, 4)       = IllegalArgumentException
   * StringUtils.abbreviate("abcdefghij", "...", 5, 6)        = IllegalArgumentException
   * </pre>
   *
   * @param str the String to check, may be null
   * @param abbrevMarker the String used as replacement marker
   * @param offset left edge of source String
   * @param maxWidth maximum length of result String, must be at least 4
   * @return abbreviated String, {@code null} if null String input
   * @throws IllegalArgumentException if the width is too small
   * @since 3.6
   */
  private static String abbreviate(
      final String str, final String abbrevMarker, int offset, final int maxWidth) {
    if (Strings.isNullOrEmpty(str) || Strings.isNullOrEmpty(abbrevMarker)) {
      return str;
    }

    final int abbrevMarkerLength = abbrevMarker.length();
    final int minAbbrevWidth = abbrevMarkerLength + 1;
    final int minAbbrevWidthOffset = abbrevMarkerLength + abbrevMarkerLength + 1;

    if (maxWidth < minAbbrevWidth) {
      throw new IllegalArgumentException(
          String.format("Minimum abbreviation width is %d", minAbbrevWidth));
    }
    if (str.length() <= maxWidth) {
      return str;
    }
    if (offset > str.length()) {
      offset = str.length();
    }
    if (str.length() - offset < maxWidth - abbrevMarkerLength) {
      offset = str.length() - (maxWidth - abbrevMarkerLength);
    }
    if (offset <= abbrevMarkerLength + 1) {
      return str.substring(0, maxWidth - abbrevMarkerLength) + abbrevMarker;
    }
    if (maxWidth < minAbbrevWidthOffset) {
      throw new IllegalArgumentException(
          String.format("Minimum abbreviation width with offset is %d", minAbbrevWidthOffset));
    }
    if (offset + maxWidth - abbrevMarkerLength < str.length()) {
      return abbrevMarker
          + abbreviate(str.substring(offset), abbrevMarker, maxWidth - abbrevMarkerLength);
    }
    return abbrevMarker + str.substring(str.length() - (maxWidth - abbrevMarkerLength));
  }
}
