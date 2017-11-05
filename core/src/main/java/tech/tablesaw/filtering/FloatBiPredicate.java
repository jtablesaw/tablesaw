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

package tech.tablesaw.filtering;

public interface FloatBiPredicate {

    /**
     * Returns true if valueToTest meets the criteria of this predicate when valueToCompareAgainst is considered
     * <p>
     * Example (to compare all the values v in a column such that v is greater than 4.0, v is the value to test and 4.0
     * is the value to compare against
     *
     * @param valueToTest           the value you're checking. Often this is the value of a cell in a float column
     * @param valueToCompareAgainst the value to compare against. Often this is a single value for all comparisons
     */
    boolean test(float valueToTest, float valueToCompareAgainst);
}
