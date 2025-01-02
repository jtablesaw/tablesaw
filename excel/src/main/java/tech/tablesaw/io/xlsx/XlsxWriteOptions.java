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

package tech.tablesaw.io.xlsx;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriteOptions;

/**
 *
 * @author (c) 2024 Roeland Maes <roeland.maes@vito.be>
 */
public class XlsxWriteOptions extends WriteOptions {

	private String defaultSheetName;
	private String dateFormat;
	private String dateTimeFormat;

	private boolean autoFilter; // create an auto filter for all the the columns.

	private int defaultColumnWidth;
	private Map<Integer, Integer> columnWidths; // Key is the column index, value the width. If not set, the 'defaultColumnWidth' is used

	private int freezeFirstRows;
	private int freezeFirstColumns;

	/**
	 * Constructor
	 *
	 * @param builder
	 */
	private XlsxWriteOptions(Builder builder) {
		super(builder);
	}

	public boolean autoClose() {
		return autoClose;
	}

	public String getDefaultSheetName() {
		return defaultSheetName;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public boolean isAutoFilter() {
		return autoFilter;
	}

	public int getColumnWidth(int columnIndex) {
		Integer columnWidth = this.columnWidths.get(columnIndex);
		return null == columnWidth ? defaultColumnWidth : columnWidth;
	}

	public int getFreezeFirstRows() {
		return freezeFirstRows;
	}

	public int getFreezeFirstColumns() {
		return freezeFirstColumns;
	}

	public static Builder builder(Destination dest) {
		return new Builder(dest);
	}

	public static Builder builder(OutputStream dest) {
		return new Builder(dest);
	}

	public static Builder builder(Writer dest) {
		return new Builder(dest);
	}

	public static Builder builder(File dest)  {
		return new Builder(dest);
	}

	public static Builder builder(String fileName) {
		return builder(new File(fileName));
	}

	public static class Builder extends WriteOptions.Builder {

		private String defaultSheetName = "Sheet";
		private String dateFormat = "yyyy-mm-dd";
		private String dateTimeFormat = "yyyy-mm-ddThh:mm:ss";

		private boolean autoFilter = true;

		private int defaultColumnWidth = 3000;
		private Map<Integer, Integer> columnWidths = new HashMap<>();

		private int freezeFirstRows = 1;
		private int freezeFirstColumns = 0;


		protected Builder(String fileName) {
			super(Paths.get(fileName).toFile());
		}

		protected Builder(Destination dest) {
			super(dest);
		}

		protected Builder(File file) {
			super(file);
		}

		protected Builder(Writer writer) {
			super(writer);
		}

		protected Builder(OutputStream stream) {
			super(stream);
		}

		public Builder setDateFormat(String dateFormat) {
			this.dateFormat = dateFormat;
			return this;
		}

		public Builder setDateTimeFormat(String dateTimeFormat) {
			this.dateTimeFormat = dateTimeFormat;
			return this;
		}

		public Builder setDefaultSheetName(String defaultSheetName) {
			this.defaultSheetName = defaultSheetName;
			return this;
		}

		public Builder setAutoFilter(boolean autoFilter) {
			this.autoFilter = autoFilter;
			return this;
		}

		public Builder setDefaultColumnWidth(int defaultColumnWidth) {
			this.defaultColumnWidth = defaultColumnWidth;
			return this;
		}

		public Builder setColumnWidth(int columnIndex, Integer columnWidth) {
			if (null == columnWidth) {
				this.columnWidths.remove(columnWidth);
			} else {
				this.columnWidths.put(columnIndex, columnWidth);
			}
			return this;
		}

		public Builder setFreezeFirstRows(int freezeFirstRows) {
			this.freezeFirstRows = freezeFirstRows;
			return this;
		}

		public Builder setFreezeFirstColumns(int freezeFirstColumns) {
			this.freezeFirstColumns = freezeFirstColumns;
			return this;
		}

		public Builder setAll(XlsxWriteOptions other) {
			this.defaultSheetName = other.defaultSheetName;
			this.dateFormat = other.dateFormat;
			this.dateTimeFormat = other.dateTimeFormat;

			this.autoFilter = other.autoFilter;
			this.defaultColumnWidth = other.defaultColumnWidth;
			this.freezeFirstRows = other.freezeFirstRows;
			this.freezeFirstColumns = other.freezeFirstColumns;
			this.columnWidths = Collections.unmodifiableMap(new HashMap<>(other.columnWidths));
			return this;
		}

		public XlsxWriteOptions build() {
			XlsxWriteOptions xlsxWriteOptions = new XlsxWriteOptions(this);
			xlsxWriteOptions.defaultSheetName = this.defaultSheetName;
			xlsxWriteOptions.dateFormat = this.dateFormat;
			xlsxWriteOptions.dateTimeFormat = this.dateTimeFormat;

			xlsxWriteOptions.autoFilter = this.autoFilter;
			xlsxWriteOptions.defaultColumnWidth = this.defaultColumnWidth;
			xlsxWriteOptions.freezeFirstRows = this.freezeFirstRows;
			xlsxWriteOptions.freezeFirstColumns = this.freezeFirstColumns;
			xlsxWriteOptions.columnWidths = Collections.unmodifiableMap(new HashMap<>(this.columnWidths));
			return xlsxWriteOptions;
		}
	}
}
