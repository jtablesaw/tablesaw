package tech.tablesaw.plotting;

import org.junit.Test;

import tech.tablesaw.plotting.StandardColors;

import java.awt.*;
import java.util.List;

import static org.junit.Assert.assertFalse;


/**
 *
 */
public class StandardColorsTest {
    @Test
    public void testStandardColors() {
        List<Color> colors = StandardColors.standardColors();
        assertFalse(colors.isEmpty());
        //System.out.println(colors);
    }

}