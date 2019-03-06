package tech.tablesaw.columns;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.TextColumn;
import tech.tablesaw.api.TimeColumn;

public class ColumnAppendTest {

    private static class Scenario<T extends Column<?>> {
        public final T col1;
        public final T col2;
        public final List<?> col1col2Appended;

        public Scenario(T col) {
            this(col, col);
        }

        public Scenario(T col1, T col2) {
            this.col1 = col1;
            this.col2 = col2;
            this.col1col2Appended = Arrays.asList(ArrayUtils.addAll(this.col1.asObjectArray(), this.col2.asObjectArray()));
        }
    }

    public static Stream<Scenario<?>> scenarios() {
        return Stream.of(
                new Scenario<>(
                        FloatColumn.create("floatCol1", new float[]{1f, 2f, 3f}),
                        FloatColumn.create("floatCol2", new float[]{4f})
                ),
                new Scenario<>(
                        FloatColumn.create("floatCol1", new float[]{5f})
                ),
                new Scenario<>(
                        DoubleColumn.create("doubleCol1", new double[]{5d})
                ),
                new Scenario<>(
                        DoubleColumn.create("doubleCol1", new double[]{1d, 2d, 3d}),
                        DoubleColumn.create("doubleCol2", new double[]{4d})
                ),
                new Scenario<>(
                        DoubleColumn.create("doubleCol1", new double[]{5d})
                ),
                new Scenario<>(
                        ShortColumn.create("shortCol1", new short[]{1, 2, 3}),
                        ShortColumn.create("shortCol2", new short[]{4})
                ),
                new Scenario<>(
                        ShortColumn.create("shortCol1", new short[]{5})
                ),
                new Scenario<>(
                        IntColumn.create("intCol1", new int[]{1, 2, 3}),
                        IntColumn.create("intCol2", new int[]{4})
                ),
                new Scenario<>(
                        IntColumn.create("intCol1", new int[]{5})
                ),
                new Scenario<>(
                        LongColumn.create("longCol1", new long[]{1l, 2l, 3l}),
                        LongColumn.create("longCol2", new long[]{4l})
                ),
                new Scenario<>(
                        LongColumn.create("longCol1", new long[]{5l})
                ),
                new Scenario<>(
                        BooleanColumn.create("boolCol1", new boolean[]{true}),
                        BooleanColumn.create("boolCol2", new boolean[]{false})
                ),
                new Scenario<>(
                        BooleanColumn.create("boolCol1", new boolean[]{true})
                ),
                new Scenario<>(
                        DateColumn.create("dateCol1", new LocalDate[]{LocalDate.now()}),
                        DateColumn.create("dateCol2", new LocalDate[]{LocalDate.now()})
                ),
                new Scenario<>(
                        DateColumn.create("dateCol1", new LocalDate[]{LocalDate.now()})
                ),
                new Scenario<>(
                        DateTimeColumn.create("dateTimeCol1", new LocalDateTime[]{LocalDateTime.now()}),
                        DateTimeColumn.create("dateTimeCol2", new LocalDateTime[]{LocalDateTime.now()})
                ),
                new Scenario<>(
                        DateTimeColumn.create("dateTimeCol1", new LocalDateTime[]{LocalDateTime.now()})
                ),
                new Scenario<>(
                        StringColumn.create("stringCol1", new String[]{"foo"}),
                        StringColumn.create("stringCol2", new String[]{"bar"})
                ),
                new Scenario<>(
                        StringColumn.create("stringCol1", new String[]{"baz"})
                ),
                new Scenario<>(
                        TextColumn.create("textCol1", new String[]{"foo"}),
                        TextColumn.create("textCol2", new String[]{"bar"})
                ),
                new Scenario<>(
                        TextColumn.create("textCol1", new String[]{"baz"})
                ),
                new Scenario<>(
                        TimeColumn.create("timeCol1", new LocalTime[]{LocalTime.now()}),
                        TimeColumn.create("timeCol2", new LocalTime[]{LocalTime.now()})
                ),
                new Scenario<>(
                        TimeColumn.create("timeCol1", new LocalTime[]{LocalTime.now()})
                )
        );
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testColumnAppend(Scenario scenario) {
        assertEquals(scenario.col1col2Appended, scenario.col1.append(scenario.col2).asList());
    }

}


