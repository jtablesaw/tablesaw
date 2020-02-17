package tech.tablesaw.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Grid;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Margin;

public class LayoutTest {

  // @Test
  public void asJavascript() {

    Axis x = Axis.builder().title("x axis").build();
    Axis y = Axis.builder().title("y axis").build();

    Layout layout =
        Layout.builder()
            .title("foobar")
            .xAxis(x)
            .yAxis(y)
            .showLegend(true)
            .margin(Margin.builder().top(100).bottom(100).left(200).right(200).build())
            .build();
    System.out.println(layout.asJavascript());
  }

  // @Test
  public void asJavascriptForGrid() {

    Axis x = Axis.builder().title("x axis").build();
    Axis y = Axis.builder().title("y axis").build();
    Grid grid = Grid.builder().rows(2).columns(2).build();
    Layout layout =
        Layout.builder()
            .title("foobar")
            .xAxis(x)
            .yAxis(y)
            .grid(grid)
            .showLegend(true)
            .margin(Margin.builder().top(100).bottom(100).left(200).right(200).build())
            .build();
    String asJavascript = layout.asJavascript();
    assertTrue(asJavascript.contains("rows"));
    assertTrue(asJavascript.contains("columns"));
    assertTrue(asJavascript.contains("rows"));
    assertTrue(asJavascript.contains("xAxis"));
  }

  @Test
  public void testAutosize() {
    {
      Layout layout = Layout.builder().autosize(true).build();
      assertEquals(
          "var layout = {\n"
              + //
              "    autosize: true,\n"
              + //
              "\n\n"
              + //
              "};\n",
          layout.asJavascript());
    }
    {
      Layout layout = Layout.builder().autosize(true).width(800).build();
      assertEquals(
          "var layout = {\n"
              + //
              "    width: 800,\n"
              + //
              "    autosize: true,\n"
              + //
              "\n\n"
              + //
              "};\n",
          layout.asJavascript());
    }
    {
      Layout layout = Layout.builder().autosize(true).height(600).width(800).build();
      assertEquals(
          "var layout = {\n"
              + //
              "    height: 600,\n"
              + //
              "    width: 800,\n"
              + //
              "    autosize: true,\n"
              + //
              "\n\n"
              + //
              "};\n",
          layout.asJavascript());
    }
    {
      // see if 700x450
      Layout layout = Layout.builder().autosize(false).height(600).build();
      assertEquals(
          "var layout = {\n"
              + //
              "    height: 600,\n"
              + //
              "    width: 700,\n"
              + //
              "\n\n"
              + //
              "};\n",
          layout.asJavascript());
    }
  }
}
