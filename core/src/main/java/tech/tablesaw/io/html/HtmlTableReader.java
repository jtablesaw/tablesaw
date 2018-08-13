package tech.tablesaw.io.html;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringWriter;
import java.util.stream.Stream;

public class HtmlTableReader {

    public String tableToCsv(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements tables = doc.select("table");
        if (tables.size() != 1) {
            throw new IllegalStateException(
                    "Reading html to table currently works if there is exactly 1 html table on the page. "
                            + " The URL you passed has " + tables.size()
                            + ". You may file a feature request with the URL if you'd like your pagae to be supported");
        }
        Element table = tables.get(0);
        CsvWriterSettings settings = new CsvWriterSettings();
        StringWriter stringWriter = new StringWriter();
        CsvWriter csvWriter = new CsvWriter(stringWriter, settings);

        for (Element row : table.select("tr")) {
            Elements headerCells = row.getElementsByTag("th");
            Elements cells = row.getElementsByTag("td");
            String[] nextLine = Stream.concat(headerCells.stream(), cells.stream())
                    .map(Element::text).toArray(String[]::new);
            csvWriter.writeRow(nextLine);
        }
        return stringWriter.toString();
    }
}
