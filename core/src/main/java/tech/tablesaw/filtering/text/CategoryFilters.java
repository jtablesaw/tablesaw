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

package tech.tablesaw.filtering.text;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import tech.tablesaw.columns.CategoryColumnUtils;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.DictionaryMap;
import tech.tablesaw.util.Selection;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface CategoryFilters extends CategoryColumnUtils {

    default Selection equalToIgnoringCase(String string) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            if (next.endsWith(string)) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection startsWith(String string) {
        Selection results = new BitmapBackedSelection();

        IntRBTreeSet sorted = new IntRBTreeSet();
        DictionaryMap dictionaryMap = this.dictionaryMap();

        for (Object2IntMap.Entry<String> entry : dictionaryMap.valueToKeyMap().object2IntEntrySet()) {
            String key = entry.getKey();
            if (key.startsWith(string)) {
                sorted.add(entry.getIntValue());
            }
        }

        int i = 0;
        for (int next : values()) {
            if (sorted.contains(next)) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection endsWith(String string) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            if (next.endsWith(string)) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection stringContains(String string) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            if (next.contains(string)) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection matchesRegex(String string) {
        Pattern p = Pattern.compile(string);
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            Matcher m = p.matcher(next);
            if (m.matches()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection empty() {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            if (next.isEmpty()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isAlpha() {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            if (StringUtils.isAlpha(next)) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isNumeric() {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            if (StringUtils.isNumeric(next)) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isAlphaNumeric() {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            if (StringUtils.isAlphanumeric(next)) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isUpperCase() {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            if (StringUtils.isAllUpperCase(next)) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isLowerCase() {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            if (StringUtils.isAllLowerCase(next)) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection hasLengthEqualTo(int lengthChars) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            if (next.length() == lengthChars) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isShorterThan(int lengthChars) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            if (next.length() < lengthChars) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isLongerThan(int lengthChars) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (String next : this) {
            if (next.length() > lengthChars) {
                results.add(i);
            }
            i++;
        }
        return results;
    }
}
