package tech.tablesaw.plotly.components;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class AxisTest {

  @Test
  public void asJavascript() {
    Axis x =
        Axis.builder()
            .title("x Axis 1")
            .visible(true)
            .type(Axis.Type.DEFAULT)
            .titleFont(Font.builder().family(Font.Family.ARIAL).size(8).color("red").build())
            .build();

    System.out.println(x);
  }
}
