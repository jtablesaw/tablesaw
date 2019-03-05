package tech.tablesaw.components;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Margin;

@Disabled
public class LayoutTest {

    @Test
    public void asJavascript() {

        Axis x = Axis.builder().title("x axis").build();
        Axis y = Axis.builder().title("y axis").build();

        Layout layout = Layout.builder()
                .title("foobar")
                .xAxis(x)
                .yAxis(y)
                .showLegend(true)
                .margin(
                        Margin.builder()
                                .top(100)
                                .bottom(100)
                                .left(200)
                                .right(200)
                                .build())

                .build();
        System.out.println(layout.asJavascript());
    }
}