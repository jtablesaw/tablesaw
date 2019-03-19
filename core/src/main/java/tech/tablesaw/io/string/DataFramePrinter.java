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

package tech.tablesaw.io.string;

import tech.tablesaw.table.Relation;
import tech.tablesaw.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.IntStream;

/**
 * A class that can pretty print a DataFrame to text for visualization in a console
 * <p>
 * Based off of https://github.com/zavtech/morpheus-core/blob/master/src/main/java/com/zavtech/morpheus/reference/XDataFramePrinter.java
 * under Apache 2 license
 */
public class DataFramePrinter {

    private final int maxRows;
    private final OutputStream stream;

    /**
     * Constructor
     *
     * @param maxRows the max rows to print
     * @param stream  the print stream to write to
     */
    public DataFramePrinter(int maxRows, OutputStream stream) {
        this.maxRows = maxRows;
        this.stream = stream;
    }

    /**
     * Returns the column widths required to print the header and data
     *
     * @param headers the headers to print
     * @param data    the data items to print
     * @return the required column widths
     */
    private static int[] getWidths(String[] headers, String[][] data) {
        final int[] widths = new int[headers.length];
        for (int j = 0; j < headers.length; j++) {
            final String header = headers[j];
            widths[j] = Math.max(widths[j], header != null ? header.length() : 0);
        }
        for (String[] rowValues : data) {
            for (int j = 0; j < rowValues.length; j++) {
                final String value = rowValues[j];
                widths[j] = Math.max(widths[j], value != null ? value.length() : 0);
            }
        }
        return widths;
    }

    /**
     * Returns the header template given the widths specified
     *
     * @param widths the token widths
     * @return the line format template
     */
    private static String getHeaderTemplate(int[] widths, String[] headers) {
        return IntStream.range(0, widths.length).mapToObj(i -> {
            final int width = widths[i];
            final int length = headers[i].length();
            final int leading = (width - length) / 2;
            final int trailing = width - (length + leading);
            final StringBuilder text = new StringBuilder();
            whitespace(text, leading + 1);
            text.append("%").append(i + 1).append("$s");
            whitespace(text, trailing);
            text.append("  |");
            return text.toString();
        }).reduce((left, right) -> left + " " + right).orElse("");
    }

    /**
     * Returns the data template given the widths specified
     *
     * @param widths the token widths
     * @return the line format template
     */
    private static String getDataTemplate(int[] widths) {
        return IntStream.range(0, widths.length)
                .mapToObj(i -> " %" + (i + 1) + "$" + widths[i] + "s  |")
                .reduce((left, right) -> left + " " + right)
                .orElse("");
    }

    /**
     * Returns a whitespace string of the length specified
     *
     * @param length the length for whitespace
     */
    private static void whitespace(StringBuilder text, int length) {
        IntStream.range(0, length).forEach(i -> text.append(" "));
    }

    /**
     * Prints the specified DataFrame to the stream bound to this printer
     *
     * @param frame the DataFrame to print
     */
    public void print(Relation frame) {
        try {
            final String[] headers = getHeaderTokens(frame);
            final String[][] data = getDataTokens(frame);
            final int[] widths = getWidths(headers, data);
            final String dataTemplate = getDataTemplate(widths);
            final String headerTemplate = getHeaderTemplate(widths, headers);
            final int totalWidth = IntStream.of(widths).map(w -> w + 5).sum() - 1;
            final int totalHeight = data.length + 1;
            int capacity = totalWidth * totalHeight;
            if (capacity < 0) {
                capacity = 0;
            }
            final StringBuilder text = new StringBuilder(capacity);
            if (frame.name() != null) {
                text.append(tableName(frame, totalWidth)).append(System.lineSeparator());
            }
            final String headerLine = String.format(headerTemplate, (Object[]) headers);
            text.append(headerLine).append(System.lineSeparator());
            for (int j = 0; j < totalWidth; j++) {
                text.append("-");
            }
            for (String[] row : data) {
                final String dataLine = String.format(dataTemplate, (Object[]) row);
                text.append(System.lineSeparator());
                text.append(dataLine);
            }
            final byte[] bytes = text.toString().getBytes();
            this.stream.write(bytes);
            this.stream.flush();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to print DataFrame", ex);
        }
    }

    private String tableName(Relation frame, int width) {
        if (frame.name().length() > width) {
            return frame.name();
        }
        int diff = width - frame.name().length();
        String result = StringUtils.repeat(" ", diff / 2) + frame.name();
        return result + StringUtils.repeat(" ", width - result.length());
    }

    /**
     * Returns the header string tokens for the frame
     *
     * @param frame the frame to create header tokens
     * @return the header tokens
     */
    private String[] getHeaderTokens(Relation frame) {
        final int colCount = frame.columnCount();
        final String[] header = new String[colCount];
        IntStream.range(0, colCount).forEach(colIndex -> {
            header[colIndex] = frame.column(colIndex).name();
        });
        return header;
    }

    /**
     * Returns the 2-D array of data tokens from the frame specified
     *
     * @param frame the DataFrame from which to create 2D array of formatted tokens
     * @return the array of data tokens
     */
    private String[][] getDataTokens(Relation frame) {
        if (frame.rowCount() == 0) return new String[0][0];
        final int rowCount = Math.min(maxRows, frame.rowCount());
        final boolean truncated = frame.rowCount() > maxRows;
        final int colCount = frame.columnCount();
        final String[][] data;
        if (truncated) {
            data = new String[rowCount + 1][colCount];
            int i;
            for (i = 0; i < Math.ceil((double) rowCount / 2); i++) {
                for (int j = 0; j < colCount; j++) {
                    data[i][j] = frame.getString(i, j);
                }
            }
            for (int j = 0; j < colCount; j++) {
                data[i][j] = "...";
            }
            for (++i; i <= rowCount; i++) {
                for (int j = 0; j < colCount; j++) {
                    data[i][j] = frame.getString(frame.rowCount() - maxRows + i - 1, j);
                }
            }
        } else {
            data = new String[rowCount][colCount];
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < colCount; j++) {
                    String value = frame.getString(i, j);
                    data[i][j] = value == null ? "" : value;
                }
            }
        }
        return data;
    }

}

