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

package tech.tablesaw.mapping;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.columns.Column;

/**
 * An interface for mapping operations unique to Boolean columns
 */
public interface BooleanMapUtils extends Column {

    /*
     * TODO(lwhite): Replace this implementation with a roaring bitmap version
     */
    default BooleanColumn and(BooleanColumn... columns) {
        BooleanColumn newColumn = new BooleanColumn("");
        BooleanColumn thisColumn = (BooleanColumn) this;
        for (int i = 0; i < this.size(); i++) {
            boolean booleanValue = thisColumn.get(i);
            if (!booleanValue) {
                newColumn.append(false);
            } else {
                boolean result = true;
                for (BooleanColumn booleanColumn : columns) {
                    result = booleanColumn.get(i);
                    if (!result) {
                        break;
                    }
                }
                newColumn.append(result);
            }
        }
        return newColumn;
    }

    default BooleanColumn or(BooleanColumn... columns) {
        BooleanColumn newColumn = new BooleanColumn("");
        BooleanColumn thisColumn = (BooleanColumn) this;

        for (int i = 0; i < this.size(); i++) {
            boolean booleanValue = thisColumn.get(i);
            if (booleanValue) {
                newColumn.append(true);
            } else {
                boolean result = false;
                for (BooleanColumn booleanColumn : columns) {
                    result = booleanColumn.get(i);
                    if (result) {
                        break;
                    }
                }
                newColumn.append(result);
            }
        }
        return newColumn;
    }
}
