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

package tech.tablesaw.columns.strings;

public interface StringReduceUtils extends Iterable<String> {

  /**
   * Returns a single string made by appending all the strings in this column, separated by the
   * given delimiter
   */
  default String appendAll(String delimiter) {
    StringBuilder builder = new StringBuilder();
    int count = 0;
    for (String next : this) {
      builder.append(next);
      if (count < size() - 1) {
        builder.append(delimiter);
        count++;
      } else {
        break;
      }
    }
    return builder.toString();
  }

  /**
   * Returns a single string made by appending all the strings in this column, separated by a single
   * space character
   */
  default String appendAll() {
    return appendAll(" ");
  }

  int size();
}
