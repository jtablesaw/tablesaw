package tech.tablesaw.components;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Grid;

@Disabled
public class GridTest {

    @Test
    public void asJavascript() {
        Grid x = Grid.builder()
                .rows(10)
                .columns(5)
                .rowOrder(Grid.RowOrder.BOTTOM_TO_TOP)
                .pattern(Grid.Pattern.INDEPENDENT)
                .build();

        System.out.println(x);
    }
}