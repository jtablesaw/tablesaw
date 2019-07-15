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

import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.html.HtmlWriteOptions.ElementCreator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlWriterTest {

    private static final String LINE_END = System.lineSeparator();

    private double[] v1 = {1, 2, NaN};
    private double[] v2 = {1, 2, NaN};
    private Table table = Table.create("t",
            DoubleColumn.create("v", v1),
            DoubleColumn.create("v2", v2)
    );

    @Test
    public void basic() {
        String output = table.write().toString("html");
        assertEquals("<table>" + LINE_END +
                " <thead>" + LINE_END +
                "  <tr>" + LINE_END +
                "   <th>v</th>" + LINE_END +
                "   <th>v2</th>" + LINE_END +
                "  </tr>" + LINE_END +
                " </thead>" + LINE_END +
                " <tbody>" + LINE_END +
                "  <tr>" + LINE_END +
                "   <td>1.0</td>" + LINE_END +
                "   <td>1.0</td>" + LINE_END +
                "  </tr>" + LINE_END +
                "  <tr>" + LINE_END +
                "   <td>2.0</td>" + LINE_END +
                "   <td>2.0</td>" + LINE_END +
                "  </tr>" + LINE_END +
                "  <tr>" + LINE_END +
                "   <td></td>" + LINE_END +
                "   <td></td>" + LINE_END +
                "  </tr>" + LINE_END +
                " </tbody>" + LINE_END +
                "</table>", output);
    }

    @Test
    public void alternatingRows() throws IOException {
        OutputStream baos = new ByteArrayOutputStream();
        ElementCreator elementCreator = (elementName, column, row) -> {
            Element element = new Element(elementName);
            if (elementName.equals("tr") && row != null) {
                return element.addClass(row % 2 == 0 ? "even" : "odd");
            }
            return element;
        };
        table.write().usingOptions(HtmlWriteOptions.builder(baos).elementCreator(elementCreator).build());
        String output = baos.toString();
        assertEquals("<table>" + LINE_END +
                " <thead>" + LINE_END +
                "  <tr>" + LINE_END +
                "   <th>v</th>" + LINE_END +
                "   <th>v2</th>" + LINE_END +
                "  </tr>" + LINE_END +
                " </thead>" + LINE_END +
                " <tbody>" + LINE_END +
                "  <tr class=\"even\">" + LINE_END +
                "   <td>1.0</td>" + LINE_END +
                "   <td>1.0</td>" + LINE_END +
                "  </tr>" + LINE_END +
                "  <tr class=\"odd\">" + LINE_END +
                "   <td>2.0</td>" + LINE_END +
                "   <td>2.0</td>" + LINE_END +
                "  </tr>" + LINE_END +
                "  <tr class=\"even\">" + LINE_END +
                "   <td></td>" + LINE_END +
                "   <td></td>" + LINE_END +
                "  </tr>" + LINE_END +
                " </tbody>" + LINE_END +
                "</table>", output);
    }

    @Test
    public void noEscape() throws IOException {
        String[] data = {"<p>foo</p>"};
        Table table = Table.create("t", StringColumn.create("data", data));

        OutputStream baos = new ByteArrayOutputStream();

        table.write().usingOptions(HtmlWriteOptions.builder(baos).escapeText(false).build());
        String output = baos.toString();
        assertEquals("<table>" + LINE_END +
                " <thead>" + LINE_END +
                "  <tr>" + LINE_END +
                "   <th>data</th>" + LINE_END +
                "  </tr>" + LINE_END +
                " </thead>" + LINE_END +
                " <tbody>" + LINE_END +
                "  <tr>" + LINE_END +
                "   <td><p>foo</p></td>" + LINE_END +
                "  </tr>" + LINE_END +
                " </tbody>" + LINE_END +
                "</table>", output);
    }
    @Test
    public void escape() throws IOException {
        String[] data = {"<p>foo</p>"};
        Table table = Table.create("t", StringColumn.create("data", data));

        OutputStream baos = new ByteArrayOutputStream();

        table.write().usingOptions(HtmlWriteOptions.builder(baos).build());
        String output = baos.toString();
        assertEquals("<table>" + LINE_END +
                " <thead>" + LINE_END +
                "  <tr>" + LINE_END +
                "   <th>data</th>" + LINE_END +
                "  </tr>" + LINE_END +
                " </thead>" + LINE_END +
                " <tbody>" + LINE_END +
                "  <tr>" + LINE_END +
                "   <td>&lt;p&gt;foo&lt;/p&gt;</td>" + LINE_END +
                "  </tr>" + LINE_END +
                " </tbody>" + LINE_END +
                "</table>", output);
    }

}
