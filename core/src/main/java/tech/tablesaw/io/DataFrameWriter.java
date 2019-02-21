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

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvWriteOptions;
import tech.tablesaw.io.csv.CsvWriter;
import tech.tablesaw.io.fixed.FixedWidthWriteOptions;
import tech.tablesaw.io.fixed.FixedWidthWriter;
import tech.tablesaw.io.html.HtmlTableWriter;
import tech.tablesaw.io.json.JsonWriteOptions;
import tech.tablesaw.io.json.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class DataFrameWriter {

    private final Table table;

    public DataFrameWriter(Table table) {
        this.table = table;
    }

    public void csv(String file) throws IOException {
        CsvWriteOptions options = new CsvWriteOptions.Builder(file).build();
        new CsvWriter(table, options).write();
    }

    public void csv(File file) throws IOException {
        CsvWriteOptions options = new CsvWriteOptions.Builder(file).build();
        new CsvWriter(table, options).write();
    }

    public void csv(CsvWriteOptions options) {
        new CsvWriter(table, options).write();
    }

    public void csv(OutputStream stream) {
        CsvWriteOptions options = new CsvWriteOptions.Builder(stream).build();
        new CsvWriter(table, options).write();
    }

    public void csv(Writer writer) {
        CsvWriteOptions options = new CsvWriteOptions.Builder(writer).build();
        new CsvWriter(table, options).write();
    }

    public void fixedWidth(String file) throws IOException {
        FixedWidthWriteOptions options = new FixedWidthWriteOptions.Builder(file).build();
        new FixedWidthWriter(table, options).write();
    }

    public void fixedWidth(File file) throws IOException {
        FixedWidthWriteOptions options = new FixedWidthWriteOptions.Builder(file).build();
        new FixedWidthWriter(table, options).write();
    }

    public void fixedWidth(FixedWidthWriteOptions options) {
        new FixedWidthWriter(table, options).write();
    }

    public void fixedWidth(OutputStream stream) {
        FixedWidthWriteOptions options = new FixedWidthWriteOptions.Builder(stream).build();
        new FixedWidthWriter(table, options).write();
    }

    public void fixedWidth(Writer writer) {
        FixedWidthWriteOptions options = new FixedWidthWriteOptions.Builder(writer).build();
        new FixedWidthWriter(table, options).write();
    }


    public void html(OutputStream stream) {
        HtmlTableWriter.write(table, stream);
    }

    public String json() throws IOException {
        return json(JsonWriteOptions.builder().build());
    }

    public String json(JsonWriteOptions options) throws IOException {
        return new JsonWriter(options).write(table);
    }
}
