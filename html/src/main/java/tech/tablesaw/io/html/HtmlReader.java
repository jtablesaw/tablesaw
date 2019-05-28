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
import tech.tablesaw.io.DataReader;
import tech.tablesaw.io.ReaderRegistry;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.TableBuildingUtils;

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
    public Table read(HtmlReadOptions options) throws IOException {
        Document doc;
        InputStream inputStream = options.source().inputStream();
        if (inputStream != null) {
            // Reader must support mark, so can't use InputStreamReader
            // Parse the InputStream directly
            doc = Jsoup.parse(inputStream, null, "");
        } else {
            doc = Parser.htmlParser().parseInput(options.source().createReader(null), "");
        }
        Elements tables = doc.select("table");
        if (tables.size() != 1) {
            throw new IllegalStateException(
                    "Reading html to table currently works if there is exactly 1 html table on the page. "
                            + " The URL you passed has " + tables.size()
                            + ". You may file a feature request with the URL if you'd like your pagae to be supported");
        }
        Element htmlTable = tables.get(0);

        List<String[]> rows = new ArrayList<>();
        for (Element row : htmlTable.select("tr")) {
            Elements headerCells = row.getElementsByTag("th");
            Elements cells = row.getElementsByTag("td");
            String[] nextLine = Stream.concat(headerCells.stream(), cells.stream())
                    .map(Element::text).toArray(String[]::new);
            rows.add(nextLine);
        }

        Table table = Table.create(options.tableName());

        if (rows.size() == 0) {
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
    public Table read(Source source) throws IOException {
      return read(HtmlReadOptions.builder(source).build());
    }

}
