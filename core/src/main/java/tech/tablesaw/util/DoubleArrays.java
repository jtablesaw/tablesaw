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

package tech.tablesaw.util;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.table.TableSlice;
import tech.tablesaw.table.TableSliceGroup;

/**
 * Utility functions for creating 2D double arrays from columns and other arrays
 */
public class DoubleArrays {

    /**
     * Returns a double[] initialized with the values from 0 to n-1, inclusive;
     */
    public static double[] toN(int n) {
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            result[i] = i;
        }
        return result;
    }
    
    public static double[][] to2dArray(TableSliceGroup views, int columnNumber) {      
       
        int viewCount = views.size();  
       
        double[][] allVals = new double[viewCount][];  
        for (int viewNumber = 0; viewNumber < viewCount; viewNumber++) {       
            TableSlice view = views.get(viewNumber);   
            allVals[viewNumber] = new double[view.rowCount()]; 
            NumberColumn numberColumn = view.numberColumn(columnNumber);       
            for (int r = 0; r < view.rowCount(); r++) {        
                allVals[viewNumber][r] = numberColumn.get(r);  
            }  
        }      
        return allVals;        
    }    

}
