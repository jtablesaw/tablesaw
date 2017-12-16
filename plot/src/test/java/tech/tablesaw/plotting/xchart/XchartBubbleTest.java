/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.tablesaw.plotting.xchart;

import java.awt.Component;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.JFrame;
import static org.hamcrest.CoreMatchers.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import org.knowm.xchart.BubbleChart;
import org.knowm.xchart.XChartPanel;
import org.netbeans.jemmy.operators.JFrameOperator;


public class XchartBubbleTest {

    private static JFrameOperator operator;
    private static JFrame frame;

    @BeforeClass
    public static void setUp() throws InterruptedException {
        double[] x = new double[]{10, 15, 20};
        double[] y = new double[]{10, 15, 20};
        double[] data = new double[]{9, 14, 19};

        frame = XchartBubble.show("foo", x, "a", y, "b", data);
        operator = new JFrameOperator("Tablesaw");
    }

    @AfterClass
    public static void tearDown() {
        frame.setVisible(false);
        frame.dispose();
    }

    private Optional<XChartPanel> findXChartPanel() {
        Component[] comps = operator.getContentPane().getComponents();
        Optional<Component> comp = Arrays.asList(comps).stream().filter(c -> c instanceof XChartPanel).findFirst();
        return comp.map(c -> (XChartPanel) c);
    }

    @Test
    public void titleShouldBeTablesaw() {
        String title = operator.getTitle();
        assertThat(title, equalTo("Tablesaw"));
    }

    @Test
    public void shouldHaveXChartpanel() {
        Optional<XChartPanel> pan = findXChartPanel();
        assertThat(pan.isPresent(), is(true));
    }

    @Test
    public void chartPanelShouldHaveBubbleChart() {
        Optional<XChartPanel> pan = findXChartPanel();
        assumeTrue(pan.isPresent());

        XChartPanel xpan = pan.get();
        assertThat(xpan.getChart(), instanceOf(BubbleChart.class));
    }
}
