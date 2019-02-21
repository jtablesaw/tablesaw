package tech.tablesaw.io.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.io.TableBuildingUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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

        ReadOptions options = ReadOptions.builder(new StringReader(""), url).build(); // TODO: this should be passed in
        String[] headerRow = rows.get(0);
        List<String> columnNames = new ArrayList<>();
        for (int i = 0; i < headerRow.length; i++) {
            columnNames.add(headerRow[i]); // TODO: cleansing and fallback name
        }

        return TableBuildingUtils.build(columnNames, rows, options);
    }

}
