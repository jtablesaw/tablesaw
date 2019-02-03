package tech.tablesaw.io.html;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.ColumnTypeDetector;
import tech.tablesaw.io.ReadOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class HtmlTableReader {

    public Table read(String url) throws IOException { // TODO: take ReaderOptions. Add a test using a File
        Document doc = Jsoup.connect(url).get();
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

        Table table = Table.create(url);

        if (rows.size() == 0) {
            return table;
        }

        ColumnTypeDetector detector = new ColumnTypeDetector();
        Iterator<String[]> iterator = rows.iterator();
        iterator.next(); // Discard header row. TODO: support tables without headers
        ReadOptions options = ReadOptions.builder(new StringReader(""), url).build(); // TODO: this should be passed in
        ColumnType[] types = detector.detectColumnTypes(iterator, options);
        String[] headerRow = rows.get(0);
        for (int i = 0; i < headerRow.length; i++) {
            table.addColumns(types[i].create(headerRow[i])); // TODO: cleansing and fallback name
        }

        for (int i = 1; i < rows.size(); i++) {
            for (int j = 0; j < table.columnCount(); j++) {
                table.column(j).appendCell(rows.get(i)[j]);        	
            }
        }

        return table;
    }

}
