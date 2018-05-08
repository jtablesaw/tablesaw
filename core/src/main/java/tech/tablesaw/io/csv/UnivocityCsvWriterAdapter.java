package tech.tablesaw.io.csv;

import java.io.Writer;

import com.univocity.parsers.csv.CsvWriterSettings;

public class UnivocityCsvWriterAdapter implements CsvWriterAdapter {

    

    private com.univocity.parsers.csv.CsvWriter csvWriter;

    public UnivocityCsvWriterAdapter(Writer writer) {

        CsvWriterSettings settings = new CsvWriterSettings();
        csvWriter = new com.univocity.parsers.csv.CsvWriter(writer, settings);
        
    }

    public void writeNext(String[] record) {
        csvWriter.writeRow(record);
        // TODO Auto-generated method stub
        
    }
    
}
