package tech.tablesaw.plotly.components;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.mitchellbosecke.pebble.loader.Loader;
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
