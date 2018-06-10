/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.plotting;

import java.awt.*;

/**
 * A color scheme based on Munsell's color charts
 */
public class StandardColor {

    private final Hue hue;
    private final int chroma;
    private final int value;
    private final String hexColor;

    public StandardColor(String hue, int chroma, int value, String hexColor) {
        this.hue = Hue.from(hue);
        this.chroma = chroma;
        this.value = value;
        this.hexColor = hexColor;
    }

    public String hexColor() {
        return hexColor;
    }

    public int value() {
        return value;
    }

    public int chroma() {
        return chroma;
    }

    public Hue hue() {
        return hue;
    }

    public Color asColor() {
        return Color.decode(hexColor);
    }
}
