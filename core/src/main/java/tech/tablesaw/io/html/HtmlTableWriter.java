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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.annotations.VisibleForTesting;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

/**
 * Static utility that writes Tables in HTML format for display
 */
public final class HtmlTableWriter {

    /**
     * Private constructor to prevent instantiation
     */
    private HtmlTableWriter() {
    }

    public static String write(Table table) {
        StringBuilder builder = new StringBuilder();
        builder.append(header(table.columnNames()));
        builder.append("<tbody>")
                .append('\n');
        for (int row : table.rows()) {
            builder.append(row(row, table));
        }
        builder.append("</tbody>");
        return builder.toString();
    }

    public static void write(Table table, OutputStream outputStream) {
        try (PrintWriter p = new PrintWriter(outputStream)) {
            p.println(write(table));
        }
    }

    /**
     * Returns a string containing the html output of one table row
     */
    @VisibleForTesting
    static String row(int row, Table table) {
        StringBuilder builder = new StringBuilder()
                .append("<tr>");

        for (Column col : table.columns()) {
            builder
                    .append("<td>")
                    .append(String.valueOf(col.getString(row)))
                    .append("</td>");
        }
        builder
                .append("</tr>")
                .append('\n');
        return builder.toString();
    }

    @VisibleForTesting
    static String header(List<String> columnNames) {
        StringBuilder builder = new StringBuilder()
                .append("<thead>")
                .append('\n')
                .append("<tr>");
        for (String name : columnNames) {
            builder
                    .append("<th>")
                    .append(splitCamelCase(splitOnUnderscore(name)))
                    .append("</th>");
        }
        builder
                .append("</tr>")
                .append('\n')
                .append("</thead>")
                .append('\n');

        return builder.toString();
    }

    // todo move to utils
    private static String splitCamelCase(String s) {
        return StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(s),
                ' '
        );
    }

    // todo move to utils
    static String splitOnUnderscore(String s) {
        return StringUtils.join(
                StringUtils.split(s, '_'),
                ' '
        );
    }
}