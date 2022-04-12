package tech.tablesaw.plotly.components;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class HoverLabelTest {

  @Test
  public void asJavascript() {
    HoverLabel x =
        HoverLabel.builder()
            .nameLength(10)
            .bgColor("blue")
            .borderColor("green")
            .font(Font.builder().family(Font.Family.ARIAL).size(8).color("red").build())
            .build();

    System.out.println(x);
  }
}
