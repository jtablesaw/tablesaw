[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

# Importing & exporting data

## Supported Formats

Tablesaw supports importing and exporting data to and from a variety of data types and sources. 

| Format                         | Import | Export |
| :----------------------------- | :----- | :----- |
| CSV (and other delimited text) | Yes    | Yes    |
| JSON                           | Yes    | Yes    |
| RDBMS (via JDBC)               | Yes    |        |
| Fixed Width Text               | Yes    | Yes    |
| Excel                          | Yes    |        |
| HTML                           | Yes    | Yes    |


Importing/Exporting data from CSV and RDBMS are included in the standard tablesaw library. To import/export JSON and HTML data, and import Excel data, requires the [tablesaw-json](https://mvnrepository.com/artifact/tech.tablesaw/tablesaw-json), [tablesaw-html](https://mvnrepository.com/artifact/tech.tablesaw/tablesaw-html), and [tablesaw-excel](https://mvnrepository.com/artifact/tech.tablesaw/tablesaw-excel) dependencies, respectively. 

## Importing data

See the Javadoc for [DataFrameReader](http://static.javadoc.io/tech.tablesaw/tablesaw-core/0.31.0/tech/tablesaw/io/DataFrameReader.html) for a listing of all the `Table.read()` methods that are available.

### Text files (CSV, Tab-delimited, Fixed-width field, etc.)

Most text formats are treated similarly. This section covers rectangular text formats like CSV, but much of the information is also relevant for JSON, Excel, and HTML tables. 

The easiest way to load data from a CSV file on disk is to use:

```Java
Table t = Table.read().file("myFile.csv");
```

This method supplies defaults for everything but the filename. We assume that columns are separated by commas, and that the file has a header row, which we use to create column names. If one or more defaults are incorrect, you can customize the loading process using the class CsvReadOptions. 

You can create an options object with a builder:

```Java
CsvReadOptions.Builder builder = 
	CsvReadOptions.builder("myFile.csv")
		.separator('\t')										// table is tab-delimited
		.header(false)											// no header
		.dateFormat("yyyy.MM.dd");  				// the date format to use. 

CsvReadOptions options = builder.build();

Table t1 = Table.read().usingOptions(options);
```

The _header_ option indicates whether or not there’s a one-line header row at the top of the file. If *header* is false, we treat all the rows as data.

The _separator_ option allows you to specify a delimiter other than a comma, in case you’re loading a Tab-delimited file, for example.

The *dateFormat* lets you provide a format for reading dates. All dates in the file should use the same format, and the format is as defined in java.time.format.DateTimeFormatter.

When the table is created, it is given a default name based on the name of the file it was loaded from. You can change the name at any time using ```table.setName(aString);```. The table name is used in printing the table and information about it.

#### Column types

With all these methods, Tablesaw looks at the data in each column in the file and takes a wild guess at the type. Actually, it looks at a *sample* of the data and applies some heuristics. Of course, it’s possible that the data set includes rare values that are missed in the type inference sample. If that happens, you can set the option ```sample(false)``` to consider all the data when performing type inference. 

If nothing else seems to fit, the column is read as a StringColumn. Usually, Tablesaw gets it right, but sometimes it needs a little help. 

##### Specifying the datatypes for each column

By a little help, we mean you could specify the types explicitly, by passing an array of ColumnType objects to the read().csv() method. For example:

```Java
ColumnType[] types = {LOCAL_DATE, INTEGER, FLOAT, FLOAT, CATEGORY};
Table t = Table.read().usingOptions(CsvReadOptions
    .builder("myFile.csv")
    .columnTypes(types));
```

If that seems like a lot of work, it does have some advantages.

First, it reduces the loading time as the system does not need to infer the column types. The time saved can be significant if the file is large.

Second, it gives you complete control over the types for your columns. 

In some cases, you *must* specify the column type, because Tablesaw can’t guess correctly. For example, if a file has times encoded as HHmm so that noon appears as ‘1200’, it’s impossible to infer that this means 12:00 noon, and not the integer 1,200.  

##### A shortcut: Getting the guessed column types

If the table has many columns, it can be tedious to build the column type array by hand. To help, CsvReader has methods that return the inferred ColumnTypes in the form of an array, or as a String. The String is formatted so that it resembles a Java array literal. This method can be used even if reading the file fails.

```java
String types = CsvReader.printColumnTypes("data/bush.csv", true, ','));
System.out.println(types);
> ColumnType[] columnTypes = {
  LOCAL_DATE, // 0 date 
  SHORT_INT,  // 1 approval 
  CATEGORY,   // 2 who 
}
```

Note that the returned String is a legal array literal you can paste into Java code: the types are comma separated, and the index position and the column name are provided such that they would be interpreted as comments. You can paste it into your code and then edit it to fix whatever column types are incorrect.

##### Skipping columns during import

Another advantage to specifying the column types is that you can skip some if you don't need them. You can prevent those columns from being imported by using the special “SKIP” column type as shown below:

```Java
ColumnType[] types = {SKIP, INTEGER, FLOAT, FLOAT, SKIP};
Table t = Table.read().usingOptions(CsvReadOptions
    .builder("myFile.csv")
    .columnTypes(types));
```

In this example, the first and last columns are not loaded.

#### Handling Missing data

Tablesaw has a predefined set of strings that it interprets as missing data when reading from a CSV file. These are: “NaN”,  “*”, “NA”, “null” and, of course, the empty string “”.

When one of these strings is encountered, it is replaced by a type-specific missing indicator inside Tablesaw. For Strings, it's an empty string. For doubles it's Double.NaN. See the JavaDoc for ColumnType for more information.

If your file has an unsupported missing value indicator (e.g. "-"), you can provide it in the options builder.

```Java
Table t = Table.read().usingOptions(CsvReadOptions
    .builder("myFile.csv")
    .missingValueIndicator("-"));
```

#### Dealing with Dates and Times

Importing dates and times can be tricky because of Locales and the wide variety of possible formats. As with other Column types, Tablesaw does its best to determine what type is represented and import it correctly. When this fails, two things can help. The first is to specify a locale.  A locale can also help with number formats. 

The second is to specify the precise format for each temporal column.

```Java
Table t = Table.read().usingOptions(CsvReadOptions
    .builder("myFile.csv")
    .locale(Locale.FRENCH)
    .dateFormat("yyyy.MM.dd")
    .timeFormat("HH:mm:ss)
    .dateTimeFormat("yyyy.MM.dd::HH:mm:ss");
```

### Using the Stream API

All the examples above attempt to streamline the loading process when you have a CSV file stored on your file system. A more flexible way to load a CSV is using the Stream interface, which takes a java.io.InputStream as a parameter.

```java
Table.read().csv(InputStream stream, String tableName);
```

It can be used to read local files, but also files read across the net, in S3, etc. Here’s some examples of how it can be used.

#### Loading a CSV from a Website:

```java
ColumnType[] types = {SHORT_INT, FLOAT, SHORT_INT};
String location = 
    "https://raw.githubusercontent.com/jtablesaw/tablesaw/master/data/bush.csv";
Table table = Table.read().usingOptions(CsvReadOptions.builder(new URL(location))
    .tableName("bush")
  	.columnTypes(types)));
```

#### Loading a CSV from S3:

```Java
ColumnTypes[] types = {SHORT_INT, FLOAT, SHORT_INT};
S3Object object = 
    s3Client.getObject(new GetObjectRequest(bucketName, key));

InputStream stream = object.getObjectContent();
Table t = Table.csv(CsvReadOptions.builder(stream)
    .tableName("bush")
    .columnTypes(types)));
```

### Handling alternate encodings

By default, we assume a UTF-8 encoding for your files. If your files use another encoding, the loading process is slightly different. You need to open a reader on a FileInputStream that was constructed with the correct encoding. Here's an example. 

```Java
// file has a latin-1 encoding so, special sauce
InputStreamReader reader = new InputStreamReader(
			new FileInputStream("somefile.csv"), Charset.forName("ISO-8859-1"));

Table restaurants = Table.read()
		.usingOptions(CsvReadOptions.builder(reader, "restaurants"));
```

### Importing from a Database

It's equally easy to create a table from the results of a database query. In this case, you never need to specify the column types, because they are inferred from the database column types. 

    Table t = Table.read().db(ResultSet resultSet, String tableName);

Here’s a more complete example that  includes the JDBC setup:

```Java
String DB_URL = "jdbc:derby:CoffeeDB;create=true";
Connection conn = DriverManager.getConnection(DB_URL);

Table customer = null; 
try (Statement stmt = conn.createStatement()) {
  String sql = "SELECT * FROM Customer";
  try (ResultSet results = stmt.executeQuery(sql)) {
    customer = Table.read().db(results, "Customer");
  }
}
```

### Importing from HTML, JSON, Excel

Tablesaw supports importing data from HTML, JSON, and Excel. See the Javadoc for the [Table.read()](http://static.javadoc.io/tech.tablesaw/tablesaw-core/0.31.0/tech/tablesaw/io/DataFrameReader.html) methods for more info. 

## Exporting data

Tablesaw supports exporting data to CSV, HTML, and JSON. See the Javadoc for full details. 

### Exporting to CSV

Any `Table` object can be exported to a local csv file using the following command. 
```
Table myData; //name of table object

myData.write().csv("file-path-here.csv");
```

### Exporting to JSON

```
Table myData; //name of table object

JsonWriter jsonWriter = new JsonWriter();
jsonWriter.write(myData, new Destination(new File("myData.json")));
```


