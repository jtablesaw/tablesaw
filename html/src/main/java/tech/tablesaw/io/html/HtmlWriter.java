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

import static tech.tablesaw.io.ParsingUtils.splitCamelCase;
import static tech.tablesaw.io.ParsingUtils.splitOnUnderscore;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriterRegistry;

public class HtmlWriter implements DataWriter<HtmlWriteOptions> {

    private static final HtmlWriter INSTANCE = new HtmlWriter();

    static {
        register(Table.defaultWriterRegistry);
    }

    public static void register(WriterRegistry registry) {
        registry.registerExtension("html", INSTANCE);
        registry.registerOptions(HtmlWriteOptions.class, INSTANCE);
    }

    public void write(Table table, HtmlWriteOptions options) {
        StringBuilder builder = new StringBuilder();
        builder.append("<table>").append(System.lineSeparator());
        builder.append(header(table.columnNames()));
        builder.append("<tbody>").append(System.lineSeparator());
        for (int row = 0; row < table.rowCount(); row++) {
            builder.append(row(row, table));
        }
        builder.append("</tbody>").append(System.lineSeparator());
        builder.append("</table>");
        String str = builder.toString();
        try {
            Writer writer = options.destination().createWriter();
            writer.write(str);
            writer.flush();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
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

    @Override
    public void write(Table table, Destination dest) {
        write(table, HtmlWriteOptions.build(dest).build());
    }
}