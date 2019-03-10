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

import com.google.common.io.Files;

public class DataFrameWriter {

    private final WriterRegistry registry;
    private final Table table;

    public DataFrameWriter(WriterRegistry registry, Table table) {
	this.registry = registry;
        this.table = table;
    }

    public void toFile(String file) throws IOException {
	toFile(new File(file));
    }

    public void toFile(File file) throws IOException {
	String extension = Files.getFileExtension(file.getCanonicalPath());
	DataWriter<?> dataWriter = registry.getWriterForExtension(extension);
	dataWriter.write(table, new Destination(file));
    }

    public void toStream(OutputStream stream, String extension) {
	DataWriter<?> dataWriter = registry.getWriterForExtension(extension);
	dataWriter.write(table, new Destination(stream));
    }

    public void toWriter(Writer writer, String extension) {
	DataWriter<?> dataWriter = registry.getWriterForExtension(extension);
	dataWriter.write(table, new Destination(writer));
    }
 
    public void usingOptions(WriteOptions options) {
	DataWriter<?> dataWriter = registry.getWriterForOptions(options);
	dataWriter.write(table, options.destination());
    }

    public void csv(String file) throws IOException {
        CsvWriteOptions options = CsvWriteOptions.builder(file).build();
        new CsvWriter().write(table, options);
    }

    public void csv(File file) throws IOException {
        CsvWriteOptions options = CsvWriteOptions.builder(file).build();
        new CsvWriter().write(table, options);
    }

    public void csv(CsvWriteOptions options) {
        new CsvWriter().write(table, options);
    }

    public void csv(OutputStream stream) {
        CsvWriteOptions options = CsvWriteOptions.builder(stream).build();
        new CsvWriter().write(table, options);
    }

    public void csv(Writer writer) {
        CsvWriteOptions options = CsvWriteOptions.builder(writer).build();
        new CsvWriter().write(table, options);
    }

    public void fixedWidth(String file) throws IOException {
        FixedWidthWriteOptions options = FixedWidthWriteOptions.builder(file).build();
        new FixedWidthWriter(options).write(table);
    }

    public void fixedWidth(File file) throws IOException {
        FixedWidthWriteOptions options = FixedWidthWriteOptions.builder(file).build();
        new FixedWidthWriter(options).write(table);
    }

    public void fixedWidth(FixedWidthWriteOptions options) {
        new FixedWidthWriter(options).write(table);
    }

    public void fixedWidth(OutputStream stream) {
        FixedWidthWriteOptions options = FixedWidthWriteOptions.builder(stream).build();
        new FixedWidthWriter(options).write(table);
    }

    public void fixedWidth(Writer writer) {
        FixedWidthWriteOptions options = FixedWidthWriteOptions.builder(writer).build();
        new FixedWidthWriter(options).write(table);
    }

    public void html(OutputStream stream) {
        new HtmlTableWriter().write(table, stream);
    }

    public String json() throws IOException {
        return json(JsonWriteOptions.builder().build());
    }

    public String json(JsonWriteOptions options) throws IOException {
        return new JsonWriter(options).write(table);
    }
}
