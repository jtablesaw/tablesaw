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

package tech.tablesaw.io;

import com.google.common.collect.ImmutableList;
import javax.annotation.concurrent.Immutable;
import tech.tablesaw.api.ColumnType;

/** Utilities for working with {@link ColumnType}s */
@Immutable
public final class TypeUtils {

  /** Strings representing missing values in, for example, a CSV file that is being imported */
  private static final String missingInd1 = "NaN";

  private static final String missingInd2 = "*";
  private static final String missingInd3 = "NA";
  private static final String missingInd4 = "null";
  private static final String missingInd5 = "N/A";
  public static final ImmutableList<String> MISSING_INDICATORS =
      ImmutableList.of(missingInd1, missingInd2, missingInd3, missingInd4, missingInd5);

  /** Private constructor to prevent instantiation */
  private TypeUtils() {}
}
