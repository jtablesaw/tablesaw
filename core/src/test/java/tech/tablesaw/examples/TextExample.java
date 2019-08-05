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

package tech.tablesaw.examples;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;

/** Usage example using a Tornado data set */
public class TextExample extends AbstractExample {

  private static final String[] words1 = {"one", "two words"};
  private static final StringColumn stringColumn1 = StringColumn.create("words", words1);

  public static void main(String[] args) {
    countWords(stringColumn1);
  }

  private static void countWords(StringColumn sc) {
    DoubleColumn nc = sc.countTokens(" ");
    out("Word count: " + nc.sum());
  }
}
