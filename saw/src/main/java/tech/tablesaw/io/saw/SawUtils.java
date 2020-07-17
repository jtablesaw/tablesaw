/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.io.saw;

import com.google.common.annotations.Beta;
import java.nio.file.FileSystems;
import java.util.regex.Pattern;

/**
 * Utilities and constants for reading and writing data in Tablesaw's own compressed,
 * column-oriented file format aka "saw"
 */
@Beta
class SawUtils {

  private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");
  private static final String FILE_EXTENSION = "saw";
  private static final Pattern SEPARATOR_PATTERN =
      Pattern.compile(Pattern.quote(FileSystems.getDefault().getSeparator()));

  private SawUtils() {}

  static final String FLOAT = "FLOAT";
  static final String DOUBLE = "DOUBLE";
  static final String INTEGER = "INTEGER";
  static final String LONG = "LONG";
  static final String SHORT = "SHORT";
  static final String STRING = "STRING";
  static final String TEXT = "TEXT";
  static final String INSTANT = "INSTANT";
  static final String LOCAL_DATE = "LOCAL_DATE";
  static final String LOCAL_TIME = "LOCAL_TIME";
  static final String LOCAL_DATE_TIME = "LOCAL_DATE_TIME";
  static final String BOOLEAN = "BOOLEAN";

  static String makeName(String name) {
    // remove whitespace from table name
    String nm = WHITE_SPACE_PATTERN.matcher(name).replaceAll("");
    nm = SEPARATOR_PATTERN.matcher(nm).replaceAll("_"); // remove path separators from name
    return nm + '.' + FILE_EXTENSION;
  }
}
