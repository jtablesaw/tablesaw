package tech.tablesaw.io.html;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.TableBuildingUtils;

public class HtmlTableReader {

    public Table read(HtmlReadOptions options) throws IOException {
        Reader reader = TableBuildingUtils.createReader(options, null);
        Document doc = Parser.htmlParser().parseInput(reader, "");
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

        String[] headerRow = rows.get(0);
        List<String> columnNames = new ArrayList<>();
        for (int i = 0; i < headerRow.length; i++) {
            columnNames.add(headerRow[i]); // TODO: cleansing and fallback name
        }

        return TableBuildingUtils.build(columnNames, rows, options);
    }

}
