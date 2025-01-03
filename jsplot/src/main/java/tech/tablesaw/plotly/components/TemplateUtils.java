package tech.tablesaw.plotly.components;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.loader.DelegatingLoader;
import io.pebbletemplates.pebble.loader.FileLoader;
import io.pebbletemplates.pebble.loader.Loader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TemplateUtils {

  private TemplateUtils() {}

  private static Collection<String> templateLocations = new ArrayList<>();

  public static void setTemplateLocations(String... locations) {
    templateLocations = Arrays.asList(locations);
  }

  public static PebbleEngine getNewEngine() {
    PebbleEngine engine;
    try {
      Loader<?> loader = new ClasspathLoader();
      if (templateLocations != null && !templateLocations.isEmpty()) {
        List<Loader<?>> loaders = new ArrayList<>();
        for (String templateLocation : templateLocations) {
          FileLoader fileLoader = new FileLoader();
          fileLoader.setPrefix(templateLocation);
          loaders.add(fileLoader);
        }
        // add this one last, so it is shadowed
        loaders.add(loader);
        loader = new DelegatingLoader(loaders);
      }
      engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
    } catch (PebbleException e) {
      throw new IllegalStateException(e);
    }
    return engine;
  }
}
