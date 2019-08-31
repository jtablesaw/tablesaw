package tech.tablesaw.docs;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Saves string snippets to an output file so they can be used in the docs.
 *
 * <p>TODO clean this up. It is a bit of a hack.
 */
public class OutputWriter {
  private final Class<?> clazz;

  public OutputWriter(Class<?> clazz) {
    this.clazz = clazz;
    try {
      emptyFile();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * Write the output of arbitrary java code to a file so it can be used in the documentation.
   *
   * @param object the object to write to the text file. Will call toString();
   * @param tag the tag to mark the snippet with.
   * @throws IOException
   */
  public void write(Object object, String tag) throws IOException {
    List<String> lines = new ArrayList<>();
    lines.add("// @@ " + tag);
    lines.addAll(Arrays.asList(object.toString().split(java.lang.System.lineSeparator())));
    lines.add("// @@ " + tag);
    lines.add(java.lang.System.lineSeparator());
    Files.write(getPath(), lines, UTF_8, APPEND);
  }

  private Path getPath() {
    URL fullPath = clazz.getResource(clazz.getSimpleName() + ".class");
    String path = fullPath.toString().split("classes")[1];
    path = path.replace(".class", ".txt");
    path = "./output" + path;
    return Paths.get(path);
  }

  private void emptyFile() throws IOException {
    if (Files.exists(getPath())) {
      Files.delete(getPath());
    }
    if (!Files.exists(getPath().getParent())) {
      Files.createDirectories(getPath().getParent());
    }
    Files.createFile(getPath());
  }

  /**
   * Class to mock System.out.println()
   *
   * <p>This class is non-blocking and returns a String.
   */
  public static class System {
    public static final Printer out = new Printer();

    public static class Printer {
      private Printer() {};

      public String println(Object obj) {
        return obj.toString();
      }
    }
  }
}
