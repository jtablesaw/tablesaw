Importing data
==============

The most common way to get data into Tablesaw is from a CSV file, and the simplest way to do that is:

    Table t = Table.read().csv("myFile.csv");

You can also load data from a relational database using a JDBC ResultSet. This option is described below, along with variations on the read().csv() syntax, some helpful utilities, and more advanced options like loading multiple files at once.

## CSV files

As shown above, the easiest way to load data from a CSV file on disk is:

This method supplies defaults for everything but the filename. We assume that columns are separated by commas, and that the file has a single header row, which will be used to create column names. The method with all the option you can specify is:

    Table t1 = Table.read().csv(CsvReadOptionsBuilder options);

The _header_ option indicates whether or not there’s a one-line header row at the top of the file. If *header* is false, we treat all the rows as data.

The _separator_ option allows you to specify a delimiter other than a comma, in case you’re loading a Tab-delimited file, for example.

When the table is created, it is given a default name based on the name of the file it was loaded from. You can change the name at any time using table.setName(aString);

With all these methods, we rely on Tablesaw to guess the data types stored in each column in the file, which it does by evaluating a sample of the data.

### Specifying the datatypes for each column

You can also specify the types explicitly, by passing an array of ColumnType objects to the read().csv() method. For example:

    ColumnType[] types = {LOCAL_DATE, INTEGER, FLOAT, FLOAT, CATEGORY};
    Table t = Table.read().csv(CsvReadOptions
        .builder("myFile.csv")
        .columnTypes(types));
    
This has some advantages. First, it reduces the loading time. Second, it gives you complete control over the types for your columns. In some cases, you must specify the column type, because Tablesaw can’t always guess correctly. For example, if a file has times encoded as HHmm so that noon appears as ‘1200’, it’s impossible to know that we want the time 12:00 and not the short integer 1,200. It’s also possible that the data set includes rare values that are missed in the guessing process.

### Getting the guessed column types

If the table has many columns, it can be tedious to build the column type array by hand. To help, CsvReader has a method that will guess the ColumnTypes without loading the file and return them in the form of a String that resembles a Java array literal.

    CsvReader.printColumnTypes("data/BushApproval.csv", true, ','));

which returns a string that includes the types for each column, their position in the  table, and the name derived from the header:

    ColumnType[] columnTypes = {
      LOCAL_DATE, // 0 date 
      SHORT_INT, // 1 approval 
      CATEGORY, // 2 who 
    }

Note that this String is a legal array literal: the types are comma separated, and the index position and the column name would be interpreted as comments if you paste the output into java code. You can edit it, paste it into your code, and use it directly.

Skipping columns during import

Sometimes you have a file with columns that you’re not interested in. You can ignore those columns during the import process by using the special “SKIP” column type as shown below:

### Missing data

Tablesaw has a predefined set of strings that it interprets as missing data when reading from a CSV file. These are: “NaN”,  “*”, “NA”, “null” and, of course, the empty string “”.

When one of these strings is encountered, it is replaced by a type-specific missing indicator inside tablesaw.  See the documentation on Missing Data for more information.

Note that currently, there is no way to specify different missing value strings. This is a recognized deficiency captured in this issue.

### Using the Stream API

All the examples above attempt to streamline the loading process when you have a CSV file stored on your file system. A more flexible way to load a CSV is using the Stream interface, which takes a java.io.InputStream as a parameter.

    Table.read().csv(InputStream stream, String tableName

It can be used to read local files, but also files read across the net, in S3, etc. Here are examples using HTTP and S3. Here’s some examples.

### Loading a CSV from a Website:

    ColumnType[] types = {SHORT_INT, FLOAT, SHORT_INT};
    String location = "https://raw.githubusercontent.com/lwhite1/tablesaw/master/data/BushApproval.csv";
    Table table;
    try (InputStream input = new URL(location).openStream()) {
      table = Table.csv(CsvReadOptions.bulider(input, "bush").columnTypes(types)));
    }

### Loading a CSV from S3:

    ColumnTypes[] types = {SHORT_INT, FLOAT, SHORT_INT};
    S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key));
    InputStream stream = object.getObjectContent();
    Table t = Table.csv(CsvReadOptions.bulider(stream, "bush").columnTypes(types)));

### Loading from Database ResultSets
It's equally easy to create a table from the results of a database query. In this case, you never need to specify the column types, because they are inferred from the database column types. 

    Table t = Table.read().db(ResultSet resultSet, String tableName);
    
Here’s a more complete example that  includes the JDBC setup:

    String DB_URL = "jdbc:derby:CoffeeDB;create=true";
    Connection conn = DriverManager.getConnection(DB_URL);

    Table customer = null; 
    try (Statement stmt = conn.createStatement()) {
      String sql = "SELECT * FROM Customer";
      try (ResultSet results = stmt.executeQuery(sql)) {
        customer = Table.read().db(results, "Customer");
      }
    }

(Todo: Add section on multi-file loading).
