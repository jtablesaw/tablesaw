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

import java.util.LinkedList;
import java.util.List;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

/**
 * Interface and default static methods to filter a {@link Table}. 
 * 
 * @author (c) 2024 Roeland Maes <roeland.maes@vito.be>
 * 
 * @param <T> Type to check
 */
public interface Filter<T> {

	public boolean check(T value);
	
	public static <T> int[] columnFilter(Column<T> column, Filter<T> filter ) {
		List<Integer> returnList = new LinkedList<>();
		for (int i=0; i<column.size(); i++) {
			if (filter.check(column.get(i))) {
				returnList.add(i);
			}
		}
		return returnList.stream().mapToInt(v -> v).toArray();
	}
}
