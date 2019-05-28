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

import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriterRegistry;
import tech.tablesaw.io.html.HtmlWriteOptions.ElementCreator;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class HtmlWriter implements DataWriter<HtmlWriteOptions> {

    private static final HtmlWriter INSTANCE = new HtmlWriter();

    static {
        register(Table.defaultWriterRegistry);
    }

    public static void register(WriterRegistry registry) {
        registry.registerExtension("html", INSTANCE);
        registry.registerOptions(HtmlWriteOptions.class, INSTANCE);
    }

    public void write(Table table, HtmlWriteOptions options) throws IOException {
        ElementCreator elements = options.elementCreator();
        Element html = elements.create("table");
        html.appendChild(header(table.columns(), elements));

        Element tbody = elements.create("tbody");
        html.appendChild(tbody);
        for (int row = 0; row < table.rowCount(); row++) {
            tbody.appendChild(row(row, table, elements, options));
        }

        Writer writer = options.destination().createWriter();
        writer.write(html.toString());
        writer.flush();
    }

    /**
     * Returns a string containing the html output of one table row
     */
    private static Element row(int row, Table table, ElementCreator elements, HtmlWriteOptions options) {
        Element tr = elements.create("tr", null, row);
        for (Column<?> col : table.columns()) {
            if (options.escapeText()) {
                tr.appendChild(elements.create("td", col, row)
                        .appendText(String.valueOf(col.getString(row))));
            } else {
                tr.appendChild(elements.create("td", col, row)
                        .appendChild(new DataNode(String.valueOf(col.getString(row)))));
            }
        }
        return tr;
    }

    private static Element header(List<Column<?>> cols, ElementCreator elements) {
        Element thead = elements.create("thead");
        Element tr = elements.create("tr");
        thead.appendChild(tr);
        for (Column<?> col : cols) {
            tr.appendChild(elements.create("th", col, null)
                    .appendText(col.name()));
        }
        return thead;
    }

    @Override
    public void write(Table table, Destination dest) throws IOException {
        write(table, HtmlWriteOptions.builder(dest).build());
    }
}