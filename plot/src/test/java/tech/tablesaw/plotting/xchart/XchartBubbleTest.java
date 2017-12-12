/*
 * Copyright 2017 mario.schroeder.
 *
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

import javax.swing.JFrame;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.operators.JFrameOperator;
import tech.tablesaw.api.plot.Bubble;

/**
 *
 * @author mario.schroeder
 */
public class XchartBubbleTest {
    
    private JFrameOperator operator;
    private JFrame frame;
    
    private double [] x = new double[]{10,15,20};
    private double [] y = new double[]{10,15,20};
    private double [] data = new double[]{9,14,19};
    
    
    @Before
    public void setUp() throws InterruptedException {
        XchartBubble bubble = new XchartBubble();
        frame = bubble.show("foo", x, "a", y, "b", data);
        operator = new JFrameOperator("Tablesaw");
    }
    
    @After
    public void tearDown() {
        frame.setVisible(false);
        frame.dispose();
    }

     @Test
     public void testShow_Title_shoulBe_Tablesaw() {
         String title = operator.getTitle();
         assertThat(title, equalTo("Tablesaw"));
     }
}
