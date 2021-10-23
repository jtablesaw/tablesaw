package tech.tablesaw.io.html;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.*;

public class HtmlReader implements DataReader<HtmlReadOptions> {

  private static final HtmlReader INSTANCE = new HtmlReader();

  static {
    register(Table.defaultReaderRegistry);
  }

  public static void register(ReaderRegistry registry) {
    registry.registerExtension("html", INSTANCE);
    registry.registerMimeType("text/html", INSTANCE);
    registry.registerOptions(HtmlReadOptions.class, INSTANCE);
  }

  @Override
  public Table read(HtmlReadOptions options) {
    Document doc;
    InputStream inputStream = options.source().inputStream();
    try {
      if (inputStream != null) {
        // Reader must support mark, so can't use InputStreamReader
        // Parse the InputStream directly
        doc = Jsoup.parse(inputStream, null, "");
      } else {
        doc = Parser.htmlParser().parseInput(options.source().createReader(null), "");
      }
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
    Elements tables = doc.select("table");
    int tableIndex = 0;
    if (tables.size() != 1) {
      if (options.tableIndex() != null) {
        if (options.tableIndex() >= 0 && options.tableIndex() < tables.size()) {
          tableIndex = options.tableIndex();
        } else {
          throw new IndexOutOfBoundsException(
              "Table index outside bounds. The URL has " + tables.size() + " tables");
        }
      } else {
        throw new IllegalStateException(
            tables.size()
                + " tables found. When more than one html table is present on the page you must specify the index of the table to read from.");
      }
    }
    Element htmlTable = tables.get(tableIndex);

    List<String[]> rows = new ArrayList<>();
    for (Element row : htmlTable.select("tr")) {
      Elements headerCells = row.getElementsByTag("th");
      Elements cells = row.getElementsByTag("td");
      String[] nextLine =
          Stream.concat(headerCells.stream(), cells.stream())
              .map(Element::text)
              .toArray(String[]::new);
      rows.add(nextLine);
    }

    Table table = Table.create(options.tableName());

    if (rows.isEmpty()) {
      return table;
    }

    List<String> columnNames = new ArrayList<>();
    if (options.header()) {
      String[] headerRow = rows.remove(0);
      for (int i = 0; i < headerRow.length; i++) {
        columnNames.add(headerRow[i]);
      }
    } else {
      for (int i = 0; i < rows.get(0).length; i++) {
        columnNames.add("C" + i);
      }
    }

    return TableBuildingUtils.build(columnNames, rows, options);
  }

  @Override
  public Table read(Source source) {
    return read(HtmlReadOptions.builder(source).build());
  }
}
