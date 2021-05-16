package tech.tablesaw.io;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

class DataFrameReaderTest {

  private FileSystem fs;

  @BeforeEach
  void setUp() {
    fs = Jimfs.newFileSystem(Configuration.forCurrentPlatform());
  }

  private URL mockUrlHelper(String url, List<String> content) throws Exception {
    // Remove http:// part to be able to save to a local filesystem file
    Path path = mockFileHelper(url.replace("http://", ""), content);
    return path.toUri().toURL();
  }

  private Path mockFileHelper(String path, List<String> content) throws IOException {
    Path mockPath = fs.getPath(path);
    Files.createDirectories(mockPath.getParent());
    Files.createFile(mockPath);
    Files.write(mockPath, content);
    return mockPath;
  }

  @Test
  public void csv() throws IOException {
    Path path = mockFileHelper("data/file.csv", ImmutableList.of("region", "canada", "us"));
    Table expected = Table.create(StringColumn.create("region", new String[] {"canada", "us"}));
    Table actual = Table.read().csv(Files.newInputStream(path));
    assertEquals(expected.columnNames(), actual.columnNames());
    assertEquals(expected.stringColumn(0).asList(), actual.stringColumn(0).asList());
  }

  @Test
  public void readUrlWithExtension() throws Exception {
    URL url =
        mockUrlHelper(
            "http://something.other.com/file.csv", ImmutableList.of("region", "canada", "us"));
    Table expected = Table.create(StringColumn.create("region", new String[] {"canada", "us"}));
    Table actual = Table.read().url(url);
    assertEquals(expected.columnNames(), actual.columnNames());
    assertEquals(expected.stringColumn(0).asList(), actual.stringColumn(0).asList());
  }

  @Test
  public void readCsvUrl() throws Exception {
    URL url =
        mockUrlHelper(
            "http://something.other.com/file", ImmutableList.of("region", "canada", "us"));
    Table expected = Table.create(StringColumn.create("region", new String[] {"canada", "us"}));
    Table actual = Table.read().csv(url);
    assertEquals(expected.columnNames(), actual.columnNames());
    assertEquals(expected.stringColumn(0).asList(), actual.stringColumn(0).asList());
  }

  @Test
  public void readUrlUnknownMimeTypeNoExtension() throws Exception {
    // Mimetype should be text/plain, it depends on the installed FileTypeDetectors
    URL url = mockUrlHelper("http://something.other.com/file", ImmutableList.of());
    Throwable thrown = assertThrows(IllegalArgumentException.class, () -> Table.read().url(url));

    assertTrue(
        thrown
            .getMessage()
            .contains("No reader registered for mime-type application/octet-stream"));
  }
}
