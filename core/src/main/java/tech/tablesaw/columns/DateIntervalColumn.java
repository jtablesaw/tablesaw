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

package tech.tablesaw.columns;

import com.google.common.annotations.Beta;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.columns.packeddata.DateInterval;
import tech.tablesaw.columns.packeddata.PackedLocalDate;
import tech.tablesaw.util.Selection;

import java.util.List;


/**
 * EXPERIMENTAL
 */
@Beta
public abstract class DateIntervalColumn {

  /*-------------------------------------------------------*/
    // Column-wise boolean operations against individual values
  /*-------------------------------------------------------*/

    // boolean comparisons to other intervals
    abstract Selection equals(DateInterval interval);

    abstract Selection before(DateInterval interval);

    abstract Selection after(DateInterval interval);

    abstract Selection overlaps(DateInterval interval);

    /**
     * Returns a selection containing all cells whose interval is during (containedBy) the given interval
     */
    abstract Selection containedIn(DateInterval interval);

    abstract Selection contains(DateInterval interval);

    /**
     * Returns true if interval a end + 1 = interval b start; or vice versa
     */
    abstract Selection meets(DateInterval interval);

    // boolean comparisons to individual dates
    abstract Selection before(PackedLocalDate date);

    abstract Selection after(PackedLocalDate date);

    abstract Selection contains(PackedLocalDate date);

    abstract Selection meets(PackedLocalDate date);


  /*-------------------------------------------------------*/
    // Column-wise boolean operations against other columns
  /*-------------------------------------------------------*/

    abstract Selection equals(DateIntervalColumn interval);

    abstract Selection before(DateIntervalColumn interval);

    abstract Selection after(DateIntervalColumn interval);

    abstract Selection overlaps(DateIntervalColumn interval);

    /**
     * Returns a selection containing all cells whose interval is during (containedBy) the given interval
     */
    abstract Selection containedIn(DateIntervalColumn interval);

    abstract Selection contains(DateIntervalColumn interval);

    /**
     * Returns true if interval a end + 1 = interval b start; or vice versa
     */
    abstract Selection meets(DateIntervalColumn interval);

    // boolean comparisons to individual dates
    abstract Selection before(DateColumn column);

    abstract Selection after(DateColumn column);

    abstract Selection contains(DateColumn column);

    abstract Selection meets(DateColumn column);

    /**
     * /*-------------------------------------------------------
     */
    // reduction methods
  /*-------------------------------------------------------*/
    abstract int sumDuration();

    abstract int maxDuration();

    abstract int minDuration();

    abstract float meanDuration();

    abstract float medianDuration();

    abstract float durationVariance();

    abstract float durationStdDev();

    abstract PackedLocalDate earliestStart();

    abstract PackedLocalDate lastestEnd();

    abstract DateInterval span();

    /*-------------------------------------------------------*/
    // misc methods
  /*-------------------------------------------------------*/
    abstract List<PackedLocalDate> toDays();

}
