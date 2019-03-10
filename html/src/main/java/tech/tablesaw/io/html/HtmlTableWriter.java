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

package tech.tablesaw.io.html;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import static tech.tablesaw.io.ParsingUtils.splitCamelCase;
import static tech.tablesaw.io.ParsingUtils.splitOnUnderscore;

public class HtmlTableWriter {

    public String write(Table table) {
        StringBuilder builder = new StringBuilder();
        builder.append(header(table.columnNames()));
        builder.append("<tbody>")
                .append(System.lineSeparator());
        for (int row = 0; row < table.rowCount(); row++) {
            builder.append(row(row, table));
        }
        builder.append("</tbody>");
        return builder.toString();
    }

    public void write(Table table, OutputStream outputStream) {
        try (PrintWriter p = new PrintWriter(outputStream)) {
            p.println(write(table));
        }
    }

    /**
     * Returns a string containing the html output of one table row
     */
    private static String row(int row, Table table) {
        StringBuilder builder = new StringBuilder()
                .append("<tr>");

        for (Column<?> col : table.columns()) {
            builder
                    .append("<td>")
                    .append(String.valueOf(col.getString(row)))
                    .append("</td>");
        }
        builder
                .append("</tr>")
                .append(System.lineSeparator());
        return builder.toString();
    }

    private static String header(List<String> columnNames) {
        StringBuilder builder = new StringBuilder()
                .append("<thead>")
                .append(System.lineSeparator())
                .append("<tr>");
        for (String name : columnNames) {
            builder
                    .append("<th>")
                    .append(splitCamelCase(splitOnUnderscore(name)))
                    .append("</th>");
        }
        builder
                .append("</tr>")
                .append(System.lineSeparator())
                .append("</thead>")
                .append(System.lineSeparator());

        return builder.toString();
    }
}