package tech.tablesaw.components;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.components.Symbol;

public class MarkerTest {

  @Test
  public void asJavascript() {
    Marker x = Marker.builder().size(12.0).symbol(Symbol.DIAMOND_TALL).color("#c68486").build();

    assertTrue(x.asJavascript().contains("color"));
    assertTrue(x.asJavascript().contains("symbol"));
    assertTrue(x.asJavascript().contains("size"));
  }

  @Test
  public void testCustomPalette() {
    Marker palette = Marker.builder().colorScale(Marker.Palette.JET).build();
    assertTrue(palette.asJavascript().contains("colorscale: 'Jet'"), palette.asJavascript());
    Marker x = Marker.builder().addColorScale(0, 255, 0, 0).addColorScale(1, 0, 255, 0).build();
    assertTrue(
        x.asJavascript()
            .contains(
                "colorscale: [\n"
                    + "['0.0', 'rgb(255,0,0)'],\n"
                    + "['1.0', 'rgb(0,255,0)']\n"
                    + "]"));
  }
}
