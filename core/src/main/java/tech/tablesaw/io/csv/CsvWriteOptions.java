package tech.tablesaw.io.csv;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriteOptions;

public class CsvWriteOptions extends WriteOptions {

  private final boolean header;
  private final boolean ignoreLeadingWhitespaces;
  private final boolean ignoreTrailingWhitespaces;
  private final Character separator;
  private final Character quoteChar;
  private final Character escapeChar;
  private final String lineEnd;
  private final boolean quoteAllFields;
  private final DateTimeFormatter dateFormatter;
  private final DateTimeFormatter dateTimeFormatter;
  private final Map<String, String> columnNameMap;
  private final NumberFormat defaultDecimalNumberFormat;
  private final NumberFormat defaultWholeNumberFormat;
  private final Map<String, NumberFormat> columnSpecificNumberFormatMap;

  private CsvWriteOptions(Builder builder) {
    super(builder);
    this.header = builder.header;
    this.separator = builder.separator;
    this.quoteChar = builder.quoteChar;
    this.escapeChar = builder.escapeChar;
    this.lineEnd = builder.lineEnd;
    this.ignoreLeadingWhitespaces = builder.ignoreLeadingWhitespaces;
    this.ignoreTrailingWhitespaces = builder.ignoreTrailingWhitespaces;
    this.quoteAllFields = builder.quoteAllFields;
    this.dateFormatter = builder.dateFormatter;
    this.dateTimeFormatter = builder.dateTimeFormatter;
    this.columnNameMap = builder.columnNameMap;
    this.defaultDecimalNumberFormat=builder.defaultDecimalNumberFormat;
    this.defaultWholeNumberFormat=builder.defaultWholeNumberFormat;
    this.columnSpecificNumberFormatMap=builder.columnSpecificNumberFormatMap;
    
  }

  public boolean header() {
    return header;
  }

  public boolean ignoreLeadingWhitespaces() {
    return ignoreLeadingWhitespaces;
  }

  public boolean ignoreTrailingWhitespaces() {
    return ignoreTrailingWhitespaces;
  }

  public Character separator() {
    return separator;
  }

  public Character escapeChar() {
    return escapeChar;
  }

  public boolean quoteAllFields() {
    return quoteAllFields;
  }

  public Map<String, String> columnNameMap() {
    return columnNameMap;
  }

  public Character quoteChar() {
    return quoteChar;
  }

  public String lineEnd() {
    return lineEnd;
  }

    public DateTimeFormatter dateTimeFormatter() {
        return dateTimeFormatter;
    }

    public DateTimeFormatter dateFormatter() {
        return dateFormatter;
    }

    public NumberFormat defaultDecimalNumberFormat() {
        return defaultDecimalNumberFormat;
    }

    public NumberFormat defaultWholeNumberFormat() {
        return defaultWholeNumberFormat;
    }

    public Map<String, NumberFormat> columnSpecificNumberFormatMap() {
        return columnSpecificNumberFormatMap;
    }

    public static Builder builder(Destination dest) {
        return new Builder(dest);
    }

    public static Builder builder(OutputStream dest) {
        return new Builder(dest);
    }

    public static Builder builder(Writer dest) {
        return new Builder(dest);
    }

  public static Builder builder(File dest) throws IOException {
    return new Builder(dest);
  }

  public static Builder builder(String fileName) throws IOException {
    return builder(new File(fileName));
  }

  public static class Builder extends WriteOptions.Builder {

    private boolean header = true;
    private boolean ignoreLeadingWhitespaces = true;
    private boolean ignoreTrailingWhitespaces = true;
    private boolean quoteAllFields = false;
    private Character separator;
    private String lineEnd = System.lineSeparator();
    private Character escapeChar;
    private Character quoteChar;
    private DateTimeFormatter dateTimeFormatter;
    private DateTimeFormatter dateFormatter;
    private Map<String, String> columnNameMap = new HashMap<>();
    private NumberFormat defaultWholeNumberFormat;
    private NumberFormat defaultDecimalNumberFormat;
    private Map<String,NumberFormat> columnSpecificNumberFormatMap;

    protected Builder(String fileName) throws IOException {
      super(Paths.get(fileName).toFile());
    }

    protected Builder(Destination dest) {
      super(dest);
    }

    protected Builder(File file) throws IOException {
      super(file);
    }

    protected Builder(Writer writer) {
      super(writer);
    }

    protected Builder(OutputStream stream) {
      super(stream);
    }

    public CsvWriteOptions.Builder separator(char separator) {
      this.separator = separator;
      return this;
    }

    /**
     * Transform one or more column names as they are written to a file. The original column name is
     * unchanged.
     *
     * @param nameMap A map from existing column names to the desired output name
     */
    public CsvWriteOptions.Builder transformColumnNames(Map<String, String> nameMap) {
      this.columnNameMap = nameMap;
      return this;
    }

    public CsvWriteOptions.Builder quoteChar(char quoteChar) {
      this.quoteChar = quoteChar;
      return this;
    }

    public CsvWriteOptions.Builder dateFormatter(DateTimeFormatter dateFormatter) {
      this.dateFormatter = dateFormatter;
      return this;
    }

    public CsvWriteOptions.Builder dateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
      this.dateTimeFormatter = dateTimeFormatter;
      return this;
    }
    
  
    /**Controls the way NumberColumns are formatted when exporting to CSV-file. By default no 
     * formatter at all is applied. By providing NumberFormatters you can:
     * 
     * <li>Set a default decimal number formatter which is then applied to all double and float columns</li>
     * <li>Set a default whole number formatter which is then applied to all short, integer and long columns</li>
     * <li>provide a Map with NumberFormats to be used for specific columns. NumberFormats in the Map will be 
     * applied when the map key matches the name of the column. Matching records in the Map is overriding
     * any default formatter.</li>
     * 
     * <br>
     * 
     * Example. Use comma as decimal separator on decimal numbers and set a dollar format for a specific 
     * column:
     * 
     * <pre>
    *       {@code NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    *       NumberFormat decimalFormat = NumberFormat.getNumberInstance(Locale.GERMAN);
    *       decimalFormat.setGroupingUsed(false);
    *       Map<String, NumberFormat> formatMap =Map.of("dollar_column", currencyFormat);
    *       CsvWriteOptions.builder("/my/file/path/out.csv")
    *               .numberFormatters(decimalFormat, null, formatMap)
    *               .separator(';')
    *               .build());
    *       }
     * </pre>
     * @param defaultDecimalNumberFormatter null or a preferred NumberFormat for decimal numbers
     * @param defaultWholeNumberFormatter null or a preferred NumberFormat for whole numbers
     * @param columnSpecificFormatters null or a Map with column specific formatters
     * @return this CsvWriteOptionsBuilder
     */
    
    public CsvWriteOptions.Builder numberFormatters(NumberFormat defaultDecimalNumberFormatter, NumberFormat defaultWholeNumberFormatter, Map<String,NumberFormat> columnSpecificFormatters){
        this.defaultDecimalNumberFormat=defaultDecimalNumberFormatter;
        this.defaultWholeNumberFormat=defaultWholeNumberFormatter;
        this.columnSpecificNumberFormatMap=columnSpecificFormatters;
        return this;
    }

    /**
     * Causes all data exported as a CSV file to be enclosed in quotes. Note that this includes the
     * headers, and all columns regardless of type
     *
     * @param quoteAll {@code} true, to cause all data and column headers to be quoted.
     * @return this CsvWriteOptionsBuilder
     */
    public CsvWriteOptions.Builder quoteAllFields(boolean quoteAll) {
      this.quoteAllFields = quoteAll;
      return this;
    }

    public CsvWriteOptions.Builder escapeChar(char escapeChar) {
      this.escapeChar = escapeChar;
      return this;
    }

    public CsvWriteOptions.Builder lineEnd(String lineEnd) {
      this.lineEnd = lineEnd;
      return this;
    }

    public CsvWriteOptions.Builder header(boolean header) {
      this.header = header;
      return this;
    }

    public CsvWriteOptions.Builder ignoreLeadingWhitespaces(boolean ignoreLeadingWhitespaces) {
      this.ignoreLeadingWhitespaces = ignoreLeadingWhitespaces;
      return this;
    }

    public CsvWriteOptions.Builder ignoreTrailingWhitespaces(boolean ignoreTrailingWhitespaces) {
      this.ignoreTrailingWhitespaces = ignoreTrailingWhitespaces;
      return this;
    }

    public CsvWriteOptions build() {
      return new CsvWriteOptions(this);
    }
  }
}
